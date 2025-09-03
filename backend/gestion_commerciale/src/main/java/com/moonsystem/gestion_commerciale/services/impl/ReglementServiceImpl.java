package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.Reglement;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.repository.BonsortiRepository;
import com.moonsystem.gestion_commerciale.repository.ReglementRepository;
import com.moonsystem.gestion_commerciale.repository.TierRepository;
import com.moonsystem.gestion_commerciale.repository.UserRepository;
import com.moonsystem.gestion_commerciale.services.ReglementGeneratePdf;
import com.moonsystem.gestion_commerciale.services.ReglementService;
import com.moonsystem.gestion_commerciale.utils.BeanCopyUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReglementServiceImpl implements ReglementService {

    private final ReglementRepository reglementRepository;
    private final BonsortiRepository bonsortiRepository;
    private final UserRepository userRepository;
    private final TierRepository tierRepository;
    private final ReglementGeneratePdf pdfGenerator;

    @Transactional
    @Override
    public ReglementDto ajouterReglement(ReglementDto dto) {
        // 1) Vérifier l'utilisateur
        User user = userRepository.findByCod(dto.getIdUser())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé",
                            List.of("User not found"),
                            ErrorCodes.USER_NOT_FOUND
                    ));



        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(dto.getIdTier())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        // 3) Validations des chèques
        if ((dto.getDetailsCheque() != null && !dto.getDetailsCheque().isEmpty()&& dto.getCheque()==null) ) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );
        }
        if (dto.getCheque() != null && dto.getCheque().signum() > 0
                && !StringUtils.hasText(dto.getDetailsCheque())) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }

        // 4) Sécuriser les BigDecimal
        BigDecimal currentSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal espece = dto.getEspece() == null ? BigDecimal.ZERO : dto.getEspece();
        BigDecimal cheque = dto.getCheque() == null ? BigDecimal.ZERO : dto.getCheque();
        BigDecimal totalReglement = espece.add(cheque);

        // Vérifier que le règlement n'est pas nul
        if (totalReglement.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(
                    "Le montant du règlement doit être supérieur à zéro",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant règlement")
            );
        }

        // 5) Calculer le nouveau solde AVANT d'ajuster le signe du règlement
        BigDecimal newSolde;
        if (currentSolde.compareTo(BigDecimal.ZERO) >= 0) {
            // Solde positif ou nul (client doit de l'argent) → règlement diminue la dette
            newSolde = currentSolde.subtract(totalReglement);
        } else {
            // Solde négatif (fournisseur doit de l'argent) → règlement augmente le solde
            newSolde = currentSolde.add(totalReglement);
        }

        // 6) Ajuster le signe du règlement en fonction du solde ORIGINAL pour l'enregistrement
        BigDecimal reglementToSave, especeToSave, chequeToSave;
        if (currentSolde.compareTo(BigDecimal.ZERO) < 0) {
            // Solde négatif → enregistrer en négatif
            reglementToSave = totalReglement.multiply(BigDecimal.valueOf(-1));
            especeToSave = espece.multiply(BigDecimal.valueOf(-1));
            chequeToSave = cheque.multiply(BigDecimal.valueOf(-1));
        } else {
            // Solde positif ou nul → enregistrer en positif
            reglementToSave = totalReglement;
            especeToSave = espece;
            chequeToSave = cheque;
        }

        // 7) Création de l'entité Reglement
        Reglement reg = Reglement.builder()
                .espece(especeToSave)
                .cheque(chequeToSave)
                .total(reglementToSave)
                .det_cheque(dto.getDetailsCheque())
                .datRegl(dto.getDatRegl() == null ? LocalDateTime.now() : dto.getDatRegl())
                .tier(tier)
                .user(user)
                .build();

        // 8) Mise à jour du tier
        tier.setSolde(newSolde);

        // 9) Persistance
        tierRepository.saveAndFlush(tier);
        Reglement savedReg = reglementRepository.save(reg);

        return ReglementDto.toDto(savedReg);
    }

    @Transactional
    public void deleteReglement(Integer regId) {
        // 1) Récupérer le règlement
        Reglement reg = reglementRepository.findReglementByIdRegl(regId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Reglement non trouvé",
                        List.of("Reglement not found"),
                        ErrorCodes.REGL_NOT_FOUND
                ));

        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(reg.getTier().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        // 3) Récupérer les valeurs du règlement
        BigDecimal currentSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal reglementEspece = reg.getEspece() == null ? BigDecimal.ZERO : reg.getEspece();
        BigDecimal reglementCheque = reg.getCheque() == null ? BigDecimal.ZERO : reg.getCheque();
        BigDecimal totalReglement = reglementEspece.add(reglementCheque);

        // 4) Calculer le nouveau solde en annulant l'effet du règlement
        BigDecimal newSolde;
        if (totalReglement.compareTo(BigDecimal.ZERO) > 0) {
            // Règlement positif → on l'avait soustrait du solde → on le rajoute
            newSolde = currentSolde.add(totalReglement);
        } else {
            // Règlement négatif → on l'avait ajouté au solde → on le soustrait
            newSolde = currentSolde.subtract(totalReglement.abs());
        }

        // 5) Suppression et mise à jour
        reglementRepository.delete(reg);
        tier.setSolde(newSolde);
        tierRepository.saveAndFlush(tier);
    }

    @Transactional
    public ReglementDto updateReglement(ReglementDto reglementDto) {
        // 1) Vérifier que le règlement existe
        Reglement existingRegl = this.reglementRepository.findById(reglementDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Reglement non trouvé",
                        List.of("Reglement non trouvé"),
                        ErrorCodes.REGL_NOT_FOUND
                ));
        User user;
        if (reglementDto.getIdUser() != null) {
            user = userRepository.findByCod(reglementDto.getIdUser())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé",
                            List.of("User not found"),
                            ErrorCodes.USER_NOT_FOUND
                    ));
        } else {
            user = existingRegl.getUser();
        }
        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(existingRegl.getTier().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));
        if ((reglementDto.getDetailsCheque() != null && !reglementDto.getDetailsCheque().isEmpty()&& reglementDto.getCheque()==null) ) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );
        }
        if (reglementDto.getCheque() != null && reglementDto.getCheque().signum() > 0
                && !StringUtils.hasText(reglementDto.getDetailsCheque())) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }




        // 4) ÉTAPE 1 : Annuler l'impact de l'ancien règlement
        BigDecimal currentSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal oldEspece = existingRegl.getEspece() == null ? BigDecimal.ZERO : existingRegl.getEspece();
        BigDecimal oldCheque = existingRegl.getCheque() == null ? BigDecimal.ZERO : existingRegl.getCheque();
        BigDecimal oldTotal = oldEspece.add(oldCheque);

        // Annuler l'ancien règlement
        BigDecimal soldeApresAnnulation;
        if (oldTotal.compareTo(BigDecimal.ZERO) > 0) {
            // Ancien règlement positif → on l'avait soustrait → on le rajoute
            soldeApresAnnulation = currentSolde.add(oldTotal);
        } else {
            // Ancien règlement négatif → on l'avait ajouté → on le soustrait
            soldeApresAnnulation = currentSolde.subtract(oldTotal.abs());
        }

        // 5) ÉTAPE 2 : Valider et calculer le nouveau règlement
        BigDecimal newEspece = reglementDto.getEspece() == null ? BigDecimal.ZERO : reglementDto.getEspece();
        BigDecimal newCheque = reglementDto.getCheque() == null ? BigDecimal.ZERO : reglementDto.getCheque();
        BigDecimal newTotal = newEspece.add(newCheque);

        // Vérifier que le nouveau règlement n'est pas nul
        if (newTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(
                    "Le montant du règlement doit être supérieur à zéro",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant règlement")
            );
        }

        // 6) Calculer le nouveau solde final
        BigDecimal finalSolde;
        if (soldeApresAnnulation.compareTo(BigDecimal.ZERO) >= 0) {
            // Solde positif ou nul → règlement diminue la dette
            finalSolde = soldeApresAnnulation.subtract(newTotal);
        } else {
            // Solde négatif → règlement augmente le solde
            finalSolde = soldeApresAnnulation.add(newTotal);
        }

        // 7) Ajuster le signe du règlement pour l'enregistrement
        BigDecimal especeToSave, chequeToSave, totalToSave;
        if (soldeApresAnnulation.compareTo(BigDecimal.ZERO) < 0) {
            // Solde négatif → enregistrer en négatif
            especeToSave = newEspece.multiply(BigDecimal.valueOf(-1));
            chequeToSave = newCheque.multiply(BigDecimal.valueOf(-1));
            totalToSave = newTotal.multiply(BigDecimal.valueOf(-1));
        } else {
            // Solde positif ou nul → enregistrer en positif
            especeToSave = newEspece;
            chequeToSave = newCheque;
            totalToSave = newTotal;
        }

        // 8) Mettre à jour l'entité Reglement
        existingRegl.setEspece(especeToSave);
        existingRegl.setUser(user);
        existingRegl.setCheque(chequeToSave);
        existingRegl.setTotal(totalToSave);
        existingRegl.setDet_cheque(reglementDto.getDetailsCheque());

        // Mettre à jour la date si fournie
        if (reglementDto.getDatRegl() != null) {
            existingRegl.setDatRegl(reglementDto.getDatRegl());
        }

        // 9) Mise à jour du tier avec le nouveau solde
        tier.setSolde(finalSolde);

        // 10) Persistance
        tierRepository.saveAndFlush(tier);
        Reglement savedRegl = reglementRepository.save(existingRegl);

        return ReglementDto.toDto(savedRegl);
    }

    public ReglementResponseDto listerReglements(Integer userId, Integer tierId,LocalDateTime date,Integer year) {

        // 1) Vérifier l'utilisateur
        User user=null;
        if(userId!=null){
            user = userRepository.findByCod(userId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Utilisateur non trouvé",
                            List.of("User not found"),
                            ErrorCodes.USER_NOT_FOUND
                    ));
        }


        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        List<ReglementDto> reglements;

        if (year != null) {
            // recherche par année
            reglements = this.reglementRepository.findByTierAndUserAndYearReglements(tier, user, year)
                    .stream()
                    .map(ReglementDto::toDto)
                    .toList();
        } else {
            // recherche par date (si non précisée -> today)
            if (date == null) {
                date = LocalDateTime.now();
            }

            LocalDateTime start = date.toLocalDate().atStartOfDay();
            LocalDateTime end = date.toLocalDate().atTime(LocalTime.MAX);

            reglements = this.reglementRepository.findByTierAndUserAndDateReglements(tier, user, start, end)
                    .stream()
                    .map(ReglementDto::toDto)
                    .toList();
        }




        BigDecimal totalEspece = BigDecimal.ZERO;
        BigDecimal totalCheque = BigDecimal.ZERO;

        for (ReglementDto reg : reglements) {
            BigDecimal espece = reg.getEspece() == null ? BigDecimal.ZERO : reg.getEspece();
            BigDecimal cheque = reg.getCheque() == null ? BigDecimal.ZERO : reg.getCheque();
            totalEspece = totalEspece.add(espece);
            totalCheque = totalCheque.add(cheque);
        }

        return ReglementResponseDto.builder()
                .reglements(reglements)
                .totalEspece(totalEspece)
                .totalCheque(totalCheque)
                .build();
    }

    @Override
    public ResponseEntity<byte[]> downloadRegPdf(Integer userCod, Integer tierId,LocalDateTime date,Integer year) {
        try {
            User user = null;
            // 1) Vérifier l'utilisateur
            if(userCod != null) {
                user = userRepository.findByCod(userCod)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Utilisateur non trouvé",
                                List.of("User not found"),
                                ErrorCodes.USER_NOT_FOUND
                        ));
            }

            // 2) Vérifier le tier
            Tier tier = tierRepository.findById(tierId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Tier non trouvé",
                            List.of("Tier not found"),
                            ErrorCodes.TIER_NOT_FOUND
                    ));


            List<Reglement> reglements;

            if (year != null) {
                // recherche par année
                reglements = this.reglementRepository.findByTierAndUserAndYearReglements(tier, user, year);
            } else {
                // recherche par date (si non précisée -> today)
                if (date == null) {
                    date = LocalDateTime.now();
                }

                LocalDateTime start = date.toLocalDate().atStartOfDay();
                LocalDateTime end = date.toLocalDate().atTime(LocalTime.MAX);

                reglements = this.reglementRepository.findByTierAndUserAndDateReglements(tier, user, start, end);
            }



            // 5) Nettoyer les données nulles dans les règlements
            reglements.forEach(reglement -> {
                if (reglement.getEspece() == null) {
                    reglement.setEspece(BigDecimal.ZERO);
                }
                if (reglement.getCheque() == null) {
                    reglement.setCheque(BigDecimal.ZERO);
                }
                if (reglement.getTotal() == null) {
                    // Calculer le total si il est null
                    reglement.setTotal(reglement.getEspece().add(reglement.getCheque()));
                }
            });

            // 6) Générer le PDF
            byte[] pdfBytes = pdfGenerator.generatePdf(reglements);

            // 7) Créer un nom de fichier
            String fileName = "reglements_" + tier.getNom().replaceAll("[^a-zA-Z0-9]", "_") + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

            // 8) Configurer les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            throw e; // Relancer les exceptions métier
        } catch (Exception e) {
            throw new InvalidEntityException(
                    "Erreur lors de la génération du PDF",
                    ErrorCodes.INTERNAL_ERROR,
                    List.of("Une erreur est survenue : " + e.getMessage())
            );
        }
    }

    @Override
    public ReglementDto getReglementById(Integer reglementId){
        Reglement reglement = reglementRepository.findById(reglementId).orElseThrow(
                ()-> new EntityNotFoundException(
                        "Réglement non trouvé",
                        List.of("Reg not found"),
                        ErrorCodes.USER_NOT_FOUND)
        );

        return ReglementDto.toDto(reglement);


    }

}