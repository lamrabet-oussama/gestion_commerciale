package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Taches")
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "Num")
    private Integer num;

    @Column(name = "Libelle", length = 40, unique = true)
    private String libelle;

    @OneToMany(mappedBy = "tache")
    private List<Droit> droits;

    // getters and setters
}
