package com.moonsystem.gestion_commerciale.model;

import java.math.BigDecimal;
import java.util.List;

import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.model.enums.VilleMaroc;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tiers")
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tier")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Qualite")
    private TypeTier qualite;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "typ")
    private String type;

    @Column(name = "Ref", unique = true)
    private Integer ref;

    @Column(name = "Nom")
    private String nom;

    @Column(length = 12)
    private String nif;
    @Column(length = 12)
    private String patent;
    @Column(length = 12)
    private String nrc;
    @Column(length = 12)
    private String cin;

    @Column(name = "Adres", length = 500)
    private String adresse;

    @Enumerated(EnumType.STRING)
    private VilleMaroc ville;

    @Column(length = 50)
    private String fon;
    @Column(length = 50)
    private String gsm;
    @Column(length = 50)
    private String fax;

    @Column(length = 100)
    private String banq;

    @Column(name = "numcompt", length = 100)
    private String numCompte;

    @Column(name = "Solde", precision = 13, scale = 2)
    private BigDecimal solde;

    @Column(name = "Note", length = 5500)
    private String note;

    @Column(name = "PlaFond", precision = 12, scale = 2)
    private BigDecimal plafond;

    @Column(name = "cRemise")
    private Integer cRemise;

    @Column(name = "Solde_Fact", precision = 12, scale = 2)
    private BigDecimal soldeFact;

    // ... autres champs ...
    @OneToMany(mappedBy = "tier")
    private List<Bonsorti> bonsortis;

    @OneToMany(mappedBy = "tier")
    private List<Reglement> reglements;

    // getters and setters
}
