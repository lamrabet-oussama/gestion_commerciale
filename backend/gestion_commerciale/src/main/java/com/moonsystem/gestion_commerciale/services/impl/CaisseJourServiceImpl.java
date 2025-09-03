package com.moonsystem.gestion_commerciale.services.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import com.moonsystem.gestion_commerciale.model.Reglement;
import com.moonsystem.gestion_commerciale.repository.ReglementRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lowagie.text.DocumentException;
import com.moonsystem.gestion_commerciale.dto.BonSortieDto;
import com.moonsystem.gestion_commerciale.dto.CaisseJourDto;
import com.moonsystem.gestion_commerciale.dto.SommeTotauxDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.repository.BonsortiRepository;
import com.moonsystem.gestion_commerciale.repository.UserRepository;
import com.moonsystem.gestion_commerciale.services.CaisseJourService;
import com.moonsystem.gestion_commerciale.services.CaissePdfGeneratorService;

@Service
public class CaisseJourServiceImpl implements CaisseJourService {

    private final ReglementRepository reglementRepository;
    private MesInfoxServiceImp mesInfoxServiceImp;

    private BonsortiRepository bonsortiRepository;
    private UserRepository userRepository;
    private CaissePdfGeneratorService pdfGeneratorService;

    public CaisseJourServiceImpl(BonsortiRepository bonsortiRepository, UserRepository userRepository, CaissePdfGeneratorService pdfGeneratorService, MesInfoxServiceImp mesInfoxServiceImp, ReglementRepository reglementRepository) {
        this.bonsortiRepository = bonsortiRepository;
        this.userRepository = userRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.mesInfoxServiceImp = mesInfoxServiceImp;
        this.reglementRepository = reglementRepository;
    }

    @Override
    public CaisseJourDto getCaisseJourDtos(
            Integer userCod,
            LocalDateTime startDate ) {
        // 1) Vérification utilisateur (inchangée)
        User user = null;
        if (userCod != null) {
            user = userRepository.findById(userCod)
                    .orElseThrow(()
                            -> new EntityNotFoundException(
                            "Utilisateur non trouvé",
                            List.of("Utilisateur non trouvé"),
                            ErrorCodes.BAD_CREDENTIALS
                    )
                    );
        }

        String username = "Tous";
        if (user != null) {
            username = user.getLogin();
        }

        if (startDate == null) {
            startDate = LocalDateTime.now();
        }


        LocalDate jour = startDate.toLocalDate();
        LocalDateTime debutJour = jour.atStartOfDay();
        LocalDateTime finJour = jour.plusDays(1).atStartOfDay().minusNanos(1);

        SommeTotauxDto totaux = bonsortiRepository.sumTotauxByDate(jour,user);


        List<Bonsorti> bons = bonsortiRepository.findByFilters(user, debutJour, finJour);
        List<BonSortieDto> bonsDtos = bons.stream()
                .map(BonSortieDto::of)
                .toList();
        Map<String, List<BonSortieDto>> bonsGroupes = bonsDtos.stream()
                .collect(Collectors.groupingBy(BonSortieDto::getMvt));

        List<BonSortieDto> bonsAchat = bonsGroupes.getOrDefault("ACHAT", Collections.emptyList());
        List<BonSortieDto> bonsVentes = bonsGroupes.getOrDefault("VENTE", Collections.emptyList());

        List<ReglementDto> reglements=reglementRepository.findByDate(debutJour,finJour,null).stream().map(reg->{
                    ReglementDto dto=ReglementDto.toDto(reg);
            totaux.setTotalEspece(totaux.getTotalEspece().add(reg.getEspece()));
            totaux.setTotalCheque(totaux.getTotalCheque().add(reg.getCheque()));
            totaux.setTotalMontant(totaux.getTotalMontant().add(reg.getTotal()));
            return dto;
                }
        ).toList();
        return CaisseJourDto.builder()
                .date(jour)
                .nomUser(username)
                .bonsAchat(bonsAchat)
                .bonsVente(bonsVentes)
                .reglements(reglements)
                .totalMontant(totaux.getTotalMontant())
                .totalEspece(totaux.getTotalEspece())
                .totalCheque(totaux.getTotalCheque())
                .totalCredit(totaux.getTotalCredit())
                .build();
    }

    @Override
    public ResponseEntity<byte[]> downloadCaissePdf(Integer userCod, LocalDateTime date) throws EntityNotFoundException {

        try {
            if(date == null) {
                date=LocalDateTime.now();
            }
            // Récupérer les données de caisse pour la date
            CaisseJourDto caisseDto = getCaisseJourDtos(userCod, date);

            if (caisseDto == null) {
                return ResponseEntity.notFound().build();
            }

            // Générer le PDF
            byte[] pdfBytes = pdfGeneratorService.generateCaissePdf(caisseDto);

            // Créer le nom du fichier
            String fileName = "caisse_" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

            // Configurer les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

            // headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (DocumentException | IOException e) {
            throw new InvalidEntityException("Invalid data",ErrorCodes.BAD_CREDENTIALS,List.of("Une erreur se produit"));
        }
    }

}
