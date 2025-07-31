package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Flux")
public class Flux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_flux")
    private Integer idFlux;

    @ManyToOne
    @JoinColumn(name = "Piece", referencedColumnName = "id_bon")
    private Bonsorti bonSorti;

    @ManyToOne
    @JoinColumn(name = "articl", referencedColumnName = "Cod")
    private Article article;

    @Column(name = "Entree")
    private Integer entree;

    @Column(name = "sortie")
    private Integer sortie;

    // ... autres champs ...

    // getters and setters
}
