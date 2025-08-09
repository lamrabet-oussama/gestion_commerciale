package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bonsorti")
public class Bonsorti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bon")
    private Integer idBon;

    @Column(name = "Serie", unique = true)
    private String serie;

    @Column(name = "Mvt")
    private String mvt;

    @ManyToOne
    @JoinColumn(name = "tier", nullable = false)
    private Tier tier;

    @Column(name = "Dat_Bon")
    private LocalDateTime datBon;

    @Column(name = "Montant", scale = 2)
    private BigDecimal montant;

    @Column(name = "Espece", scale = 2)
    private BigDecimal espece;

    @Column(name = "Cheque", scale = 2)
    private BigDecimal cheque;

    @Column(name = "Det_cheq")
    private String detCheq;

    @Column(name = "Echeance")
    private LocalDateTime echeance;

    @Column(name = "Credit", precision = 12, scale = 2)
    private BigDecimal credit;

    @Column(name = "etat")
    private Boolean etat;

    @Column(name = "Remis", precision = 12, scale = 2)
    private BigDecimal remis;

    @Column(name = "mRemis", precision = 12, scale = 2)
    private BigDecimal mRemis;

    @Column(name = "Livraison", length = 200)
    private String livraison;

    @Column(name = "Origine", length = 500)
    private String origine;

    @Column(name = "Noom", length = 250)
    private String noom;

    @Column(name = "M_HT", precision = 12, scale = 2)
    private BigDecimal mHt;

    @Column(name = "M_TVA", precision = 12, scale = 2)
    private BigDecimal mTva;

    @Column(name = "Réglements", length = 40)
    private String reglements;

    @ManyToOne
    @JoinColumn(name = "user_id") // correspond à la colonne varchar(30) dans la table
    private User user;

    @Column(name = "Date_Opp", length = 15)
    private String dateOpp;

    @Column(name = "Marché", length = 100)
    private String marche;

    @Column(name = "Transport", length = 100)
    private String transport;

    @Column(name = "Note01", length = 100)
    private String note01;

    @Column(name = "valid")
    private Boolean valid;

    // Relation inverse
    @OneToMany(mappedBy = "bonSorti")
    private List<Flux> fluxes;

}
