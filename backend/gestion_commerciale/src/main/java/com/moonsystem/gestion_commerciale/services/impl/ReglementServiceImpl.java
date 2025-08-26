package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
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
import com.moonsystem.gestion_commerciale.services.ReglementService;
import com.moonsystem.gestion_commerciale.utils.BeanCopyUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReglementServiceImpl implements ReglementService {

    private final ReglementRepository reglementRepository;
    private final BonsortiRepository bonsortiRepository;
    private final UserRepository userRepository;
    private final TierRepository tierRepository;


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

        if (dto.getCheque() != null && dto.getCheque().compareTo(BigDecimal.ZERO) > 0 && dto.getDetailsCheque() == null) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }
        if ((dto.getDetailsCheque() != null && !dto.getDetailsCheque().isEmpty())
                && dto.getCheque() == null) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );        }
        // 4) Sécuriser les BigDecimal
        BigDecimal totalCredit = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal espece = dto.getEspece() == null ? BigDecimal.ZERO : dto.getEspece();
        BigDecimal cheque = dto.getCheque()==null  ? BigDecimal.ZERO: dto.getCheque();
        BigDecimal reglement = espece.add(cheque);

        // 5) Vérification du solde
        if (totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidOperationException(
                    "Le solde est déjà 0",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Solde à 0")
            );
        }

        // 6) Ajuster le signe du règlement en fonction de la nature du solde
        if (totalCredit.compareTo(BigDecimal.ZERO) > 0) {
            // Le client doit de l'argent → règlement diminue la dette
            reglement = reglement.multiply(BigDecimal.valueOf(-1));
        }
        // Si le solde est négatif (fournisseur doit de l'argent), règlement augmente le solde

        // 7) Nouveau solde
        BigDecimal newSolde = totalCredit.add(reglement);

        // 8) Création de l'entité Reglement avec etat = true
        Reglement reg = Reglement.builder()
                .espece(espece)
                .cheque(cheque)
                .total(reglement)
                .det_cheque(dto.getDetailsCheque())
                .datRegl(dto.getDatRegl() == null ? LocalDateTime.now() : dto.getDatRegl())
                .tier(tier)
                .user(user)
                .build();

        // 9) Mise à jour du tier
        tier.setSolde(newSolde);

        // 10) Persistance
        tierRepository.saveAndFlush(tier);
        Reglement savedReg = reglementRepository.save(reg);

        return ReglementDto.toDto(savedReg);
    }


    @Transactional
    public void deleteReglement(Integer regId) {

        Reglement reg= reglementRepository.findReglementByIdRegl(regId).orElseThrow(

                ()-> new EntityNotFoundException(
                        "Reglement non trouvé",
                        List.of("Reglement not found"),
                        ErrorCodes.REGL_NOT_FOUND
                )
        );
        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(reg.getTier().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));




        // 4) Sécuriser les BigDecimal
        BigDecimal totalCredit = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal espece = reg.getEspece() == null ? BigDecimal.ZERO : reg.getEspece();
        BigDecimal cheque = reg.getCheque() == null ? BigDecimal.ZERO : reg.getCheque();
        BigDecimal reglement = espece.add(cheque);



        // 7) Nouveau solde
        if (totalCredit.compareTo(BigDecimal.ZERO) > 0) {
            // Si lors de l'ajout on avait mis le règlement en négatif
            reglement = reglement.multiply(BigDecimal.valueOf(-1));
        }
        BigDecimal newSolde = totalCredit.subtract(reglement);
        reglementRepository.delete(reg);

        // 9) Mise à jour du tier
        tier.setSolde(newSolde);

        // 10) Persistance
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

        // 2) Vérifier le tier
        Tier tier = tierRepository.findById(existingRegl.getTier().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),
                        ErrorCodes.TIER_NOT_FOUND
                ));

        if (reglementDto.getCheque() != null && reglementDto.getCheque().compareTo(BigDecimal.ZERO) > 0 && reglementDto.getDetailsCheque() == null) {
            throw new InvalidOperationException(
                    "Détails de chèque sont obligatoires",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Détails Chèque")
            );
        }
        if ((reglementDto.getDetailsCheque() != null && !reglementDto.getDetailsCheque().isEmpty())
                && reglementDto.getCheque() == null) {
            throw new InvalidOperationException(
                    "Montant Chèque n'est pas défini",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant Chèque n'est pas défini")
            );        }
        // 5) ÉTAPE 1 : Annuler l'impact de l'ancien règlement
        BigDecimal currentSolde = tier.getSolde() == null ? BigDecimal.ZERO : tier.getSolde();
        BigDecimal oldEspece = existingRegl.getEspece() == null ? BigDecimal.ZERO : existingRegl.getEspece();
        BigDecimal oldCheque = existingRegl.getCheque() == null ? BigDecimal.ZERO : existingRegl.getCheque();
        BigDecimal oldReglement = oldEspece.add(oldCheque);

        // Annuler l'ancien règlement (logique inverse de ajouterReglement)
        BigDecimal soldeApresAnnulation;
        if (currentSolde.subtract(oldReglement).compareTo(BigDecimal.ZERO) >= 0) {
            // Le solde original était positif, on avait mis le règlement en négatif
            soldeApresAnnulation = currentSolde.add(oldReglement);
        } else {
            // Le solde original était négatif, le règlement était positif
            soldeApresAnnulation = currentSolde.subtract(oldReglement);
        }

        // 6) ÉTAPE 2 : Appliquer le nouveau règlement
        BigDecimal newEspece = reglementDto.getEspece() == null ? BigDecimal.ZERO : reglementDto.getEspece();
        BigDecimal newCheque = reglementDto.getCheque() == null ? BigDecimal.ZERO : reglementDto.getCheque();
        BigDecimal newReglement = newEspece.add(newCheque);

        // Vérifier que le nouveau règlement n'est pas nul
        if (newReglement.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidOperationException(
                    "Le montant du règlement ne peut pas être nul",
                    ErrorCodes.BAD_CREDENTIALS,
                    List.of("Montant règlement")
            );
        }

        // Appliquer la logique de ajouterReglement pour le nouveau montant
        BigDecimal finalSolde;
        if (soldeApresAnnulation.compareTo(BigDecimal.ZERO) > 0) {
            // Le client doit de l'argent → règlement diminue la dette
            finalSolde = soldeApresAnnulation.add(newReglement.multiply(BigDecimal.valueOf(-1)));
        } else {
            // Fournisseur doit de l'argent → règlement augmente le solde
            finalSolde = soldeApresAnnulation.add(newReglement);
        }

        // 7) Mettre à jour l'entité Reglement
        existingRegl.setEspece(newEspece);
        existingRegl.setCheque(newCheque);
        existingRegl.setTotal(newReglement);
        existingRegl.setDet_cheque(reglementDto.getDetailsCheque());

        // Mettre à jour la date si fournie
        if (reglementDto.getDatRegl() != null) {
            existingRegl.setDatRegl(reglementDto.getDatRegl());
        }

        // 8) Mise à jour du tier avec le nouveau solde
        tier.setSolde(finalSolde);

        // 9) Persistance
        tierRepository.saveAndFlush(tier);
        Reglement savedRegl = reglementRepository.save(existingRegl);

        return ReglementDto.toDto(savedRegl);
    }

    public ReglementResponseDto listerReglements(Integer userId, Integer tierId){

      List<ReglementDto> reglements=  this.reglementRepository.findByTierAndUser(tierId,userId).stream()
                .map(ReglementDto::toDto).toList();
        BigDecimal totalEspece=BigDecimal.ZERO;
        BigDecimal totalCheque=BigDecimal.ZERO;
      reglements.forEach(
              reg->{
                  totalEspece.add(reg.getEspece());
                  totalCheque.add(reg.getCheque());
              }
      );

      return ReglementResponseDto.builder()
              .reglements(reglements)
              .totalEspece(totalEspece)
              .totalCheque(totalCheque)
              .build();
    }






}
