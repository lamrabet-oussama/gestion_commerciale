package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Droits")
public class Droit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Droi")
    private Integer id;

    @Column(name = "Uzer", length = 40)
    private String uzer;

    @ManyToOne
    @JoinColumn(name = "Tache", referencedColumnName = "Libelle")
    private Tache tache;

    // getters and setters
}
