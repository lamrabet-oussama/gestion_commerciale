package com.moonsystem.gestion_commerciale.services.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    private MesInfoxServiceImp mesInfoxServiceImp;

    private BonsortiRepository bonsortiRepository;
    private UserRepository userRepository;
    private CaissePdfGeneratorService pdfGeneratorService;

    public CaisseJourServiceImpl(BonsortiRepository bonsortiRepository, UserRepository userRepository, CaissePdfGeneratorService pdfGeneratorService, MesInfoxServiceImp mesInfoxServiceImp) {
        this.bonsortiRepository = bonsortiRepository;
        this.userRepository = userRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.mesInfoxServiceImp = mesInfoxServiceImp;

    }

    @Override
    public CaisseJourDto getCaisseJourDtos(
            Integer userCod,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
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
        // 2) Dates par défaut
        // Si l’appelant ne fournit pas startDate, on prend maintenant()…
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        // Si l’appelant ne fournit pas endDate, on considère la fin du même jour
        // if (endDate == null) {
        //     endDate = startDate.toLocalDate()
        //             .plusDays(1)
        //             .atStartOfDay()
        //             .minusNanos(1);
        // }

        // 3) On en déduit le LocalDate « jour »
        LocalDate jour = startDate.toLocalDate();
        LocalDateTime debutJour = jour.atStartOfDay();
        LocalDateTime finJour = jour.plusDays(1).atStartOfDay().minusNanos(1);

        // 4) Récupération des totaux d’un seul appel agrégé
        SommeTotauxDto totaux = bonsortiRepository.sumTotauxByDate(jour);

        // 5) Récupérer tous les bons du jour via la signature correcte de findByFilters
        //    Ici j’utilise Pageable.unpaged() pour ne pas paginer :
        List<Bonsorti> bonsEntites = bonsortiRepository
                .findByFilters(user, debutJour, finJour);

        // 6) Transformation en DTOs individuels
        List<BonSortieDto> bonsDtos = bonsEntites.stream()
                .map(BonSortieDto::of)
                .collect(Collectors.toList());

        // 7) Construction du CaisseJourDto final
        return CaisseJourDto.builder()
                .date(jour)
                .nomUser(username)
                .bons(bonsDtos)
                .totalMontant(totaux.getTotalMontant())
                .totalEspece(totaux.getTotalEspece())
                .totalCheque(totaux.getTotalCheque())
                .build();
    }

    @Override
    public ResponseEntity<byte[]> downloadCaissePdf(Integer userCod, LocalDateTime date) {

        try {
            // Récupérer les données de caisse pour la date
            CaisseJourDto caisseDto = getCaisseJourDtos(userCod, date, null);

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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
