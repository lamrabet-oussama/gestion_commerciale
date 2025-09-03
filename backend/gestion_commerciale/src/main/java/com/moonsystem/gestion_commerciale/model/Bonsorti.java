package com.moonsystem.gestion_commerciale.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bonsorti"


        ,indexes = {
        @Index(name="idx_datbon",columnList = "datBon"),
        @Index(name="idx_bs_tier",columnList = "tier"),
        @Index(name="idx_bs_credit",columnList = "credit")
})
public class Bonsorti {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_bon")
    private Integer idBon;


    @Column(name = "Serie" , unique = true)
    private String serie;
    @Enumerated(EnumType.STRING)
    @Column(name = "Mvt")
    private MvtType mvt;




    @ManyToOne
    @JoinColumn(name = "tier", nullable = false)
    @JsonIgnoreProperties("bonsortis")
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

    @Column(name = "Reglements", length = 40)
    private String reglements;

    @ManyToOne
    @JoinColumn(name = "user_id") // correspond à la colonne varchar(30) dans la table
    private User user;

    @Column(name = "Date_Opp", length = 15)
    private String dateOpp;

    @Column(name = "Marche", length = 100)
    private String marche;

    @Column(name = "Transport", length = 100)
    private String transport;

    @Column(name = "Note01", length = 100)
    private String note01;

    @Column(name = "valid")
    private Boolean valid;

    // Relation inverse
    @OneToMany(mappedBy = "bonSorti",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flux> fluxes;

}
