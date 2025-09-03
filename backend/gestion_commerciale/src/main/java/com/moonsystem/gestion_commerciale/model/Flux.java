package com.moonsystem.gestion_commerciale.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Flux",
indexes = {
        @Index(name = "idx_article",columnList = "articl"),
        @Index(name="idx_dateFlux",columnList = "dateFlux"),
        @Index(name="idx_entree",columnList = "Entree"),
        @Index(name="idx_sortie",columnList = "sortie")
}
)
public class Flux {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_flux")
    private Integer idFlux;

    @Column(name = "libelle")
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Piece", referencedColumnName = "id_bon")
    @JsonIgnoreProperties({"fluxes"})
    private Bonsorti bonSorti;

    private LocalDateTime dateFlux;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articl", referencedColumnName = "Cod")
    @JsonIgnoreProperties({"fluxes"})
    private Article article;

    @Column(name = "ref")
    private Integer ref;

    @Column(name = "Entree")
    private Integer entree;

    @Column(name = "sortie")
    private Integer sortie;

    @Column(name = "Prix_uni", precision = 12, scale = 2)
    private BigDecimal prixUni;

    @Column(name = "montant", precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "cout", precision = 12, scale = 2)
    private BigDecimal cout;

    @Column(name = "Mesure", length = 20)
    private String mesure;

    @Column(name = "SurfE", precision = 12, scale = 2)
    private BigDecimal surfE;

    @Column(name = "SurfS", precision = 12, scale = 2)
    private BigDecimal surfS;

    @Column(name = "SurfU", precision = 12, scale = 2)
    private BigDecimal surfU;

    @Column(name = "fRemis", precision = 5, scale = 2)
    private BigDecimal fRemis;

    @Column(name = "Plat")
    private Integer plat;

    @Column(name = "Tx_TVA", precision = 5, scale = 2)
    private BigDecimal txTva;

    @Column(name = "T_TVA", precision = 12, scale = 2)
    private BigDecimal tTva;

    // getters and setters sont automatiquement générés par Lombok @Data
}