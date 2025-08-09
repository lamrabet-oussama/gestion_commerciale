package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;

import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.model.enums.VilleMaroc;
import com.moonsystem.gestion_commerciale.validator.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur complet
public class TierDto {

    private Integer id;

    @NotNull(message = "La qualité est requise")
    @ValidEnum(enumClass = TypeTier.class, message = "Type invalide de qualité")
    private String qualite;

    private String type;

    @NotNull(message = "La référence est requise")
    // @UniqueRef(message = "Cette référence existe déjà")
    private Integer ref;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    private String nif;
    private String patent;
    private String nrc;
    private String cin;

    @NotBlank(message = "L'adresse est requise")
    private String adresse;

    @NotNull(message = "La ville est requise")
    @ValidEnum(enumClass = VilleMaroc.class, message = "Type invalide de Ville Marocaine")
    private String ville;

    @NotBlank(message = "Le téléphone est requis")
    @Pattern(regexp = "^[0-9]{10}$", message = "Le téléphone doit contenir 10 chiffres")
    private String fon;

    private String gsm;
    private String fax;
    private String banq;
    private String numCompte;
    private BigDecimal solde;
    private String note;
    private BigDecimal plafond;
    private Integer cRemise;
    private BigDecimal soldeFact;

    public static TierDto fromEntity(Tier tier) {
        return new TierDto(
                tier.getId(),
                tier.getQualite().name(),
                tier.getType(),
                tier.getRef(),
                tier.getNom(),
                tier.getNif(),
                tier.getPatent(),
                tier.getNrc(),
                tier.getCin(),
                tier.getAdresse(),
                tier.getVille().name(),
                tier.getFon(),
                tier.getGsm(),
                tier.getFax(),
                tier.getBanq(),
                tier.getNumCompte(),
                tier.getSolde(),
                tier.getNote(),
                tier.getPlafond(),
                tier.getCRemise(),
                tier.getSoldeFact()
        );
    }

    public static Tier toEntity(TierDto dto) {
        if (dto == null) {
            return null;
        }
        Tier tier = new Tier();
        tier.setId(dto.getId());
        tier.setQualite(TypeTier.valueOf(dto.getQualite()));
        tier.setType(dto.getType());
        tier.setRef(dto.getRef());
        tier.setNom(dto.getNom());
        tier.setNif(dto.getNif());
        tier.setPatent(dto.getPatent());
        tier.setNrc(dto.getNrc());
        tier.setCin(dto.getCin());
        tier.setAdresse(dto.getAdresse());
        tier.setVille(VilleMaroc.valueOf(dto.getVille()));
        tier.setFon(dto.getFon());
        tier.setGsm(dto.getGsm());
        tier.setFax(dto.getFax());
        tier.setBanq(dto.getBanq());
        tier.setNumCompte(dto.getNumCompte());
        tier.setSolde(dto.getSolde());
        tier.setNote(dto.getNote());
        tier.setPlafond(dto.getPlafond());
        tier.setCRemise(dto.getCRemise());
        tier.setSoldeFact(dto.getSoldeFact());
        return tier;
    }
}
