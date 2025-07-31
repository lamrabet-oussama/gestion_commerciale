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
@Table(name="Bonsorti")
public class Bonsorti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bon")
    private Integer idBon;

    @Column(name = "Serie", length = 20, unique = true)
    private String serie;

    @Column(name = "Mvt", length = 12)
    private String mvt;

    @ManyToOne
    @JoinColumn(name = "tier", nullable = false)
    private Tier tier;

    @Column(name = "Dat_Bon")
    private LocalDateTime datBon;

    @Column(name = "Montant", precision = 12, scale = 2)
    private BigDecimal montant;

    // ... autres champs ...

    @OneToMany(mappedBy = "bonSorti")
    private List<Flux> fluxes;

    // getters and setters
}
