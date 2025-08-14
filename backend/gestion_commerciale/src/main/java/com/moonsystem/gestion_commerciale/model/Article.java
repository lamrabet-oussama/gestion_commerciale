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
@Table(
        name = "Article",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"Designation", "Choix"})
        }
)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod")
    private Integer cod;


    @Column(name = "Ref", unique = true)
    private Integer ref;

    @Column(name = "Famille", length = 25)
    private String famille;

    @Column(name = "Designation", length = 75)
    private String designation;

    @Column(name = "Choix", length = 25)
    private String choix;

    @Column(name = "Référence", length = 30)
    private String reference;

    @Column(name = "Stock", precision = 12, scale = 2)
    private BigDecimal stock=BigDecimal.ZERO;

    @Column(name = "SurfUni", precision = 12, scale = 2)
    private BigDecimal surfaceUnitaire;

    @Column(name = "Surface", precision = 12, scale = 2)
    private BigDecimal surface;

    @Column(name = "Stock_Alert", precision = 12, scale = 2)
    private BigDecimal stockAlert;

    @Column(name = "Prix_Achat", precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(name = "Prix_Min", precision = 12, scale = 2)
    private BigDecimal prixMin=BigDecimal.ZERO;

    @Column(name = "Prix", precision = 12, scale = 2)
    private BigDecimal prix;

    @Column(name = "Remise", precision = 12, scale = 2)
    private BigDecimal remise=BigDecimal.ZERO;

    @Column(name = "Controle", length = 15)
    private String controle;

    @Column(name = "Note", length = 50)
    private String note="";

    @Column(name = "Achat_HT", precision = 12, scale = 2)
    private BigDecimal achatHt;

    @Column(name = "Taux_TVA", precision = 4, scale = 2)
    private BigDecimal tauxTva= new BigDecimal(20);

    @Column(name = "Marge_Fact", precision = 6, scale = 2)
    private BigDecimal margeFact;

    @Column(name = "Vente_HT", precision = 12, scale = 2)
    private BigDecimal venteHt;

    @Column(name = "Stock_Fact", precision = 12, scale = 2)
    private BigDecimal stockFact;

    @Column(name = "Classe", length = 5)
    private String classe;

    @Column(name = "Stock_Max", precision = 12, scale = 2)
    private BigDecimal stockMax;

    @Column(name = "Pack", precision = 12, scale = 2)
    private BigDecimal pack;

    @Column(name = "Actif")
    private Boolean actif=true;

    @Column(name = "Marge1", precision = 12, scale = 2)
    private BigDecimal marge1;

    @Column(name = "Marge2", precision = 12, scale = 2)
    private BigDecimal marge2;

    @Column(name = "BarCode", length = 20, unique = true)
    private String barCode;

    @Column(name = "Rayon", length = 50)
    private String rayon;

    @Column(name = "NoStock")
    private Boolean noStock;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "article")
    private List<Flux> fluxes;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    // getters and setters
}

