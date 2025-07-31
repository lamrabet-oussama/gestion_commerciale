package com.moonsystem.gestion_commerciale.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cod")
    private Integer cod;

    @Column(name = "Login", length = 33, unique = true)
    private String login;

    @Column(name = "Pass", length = 15)
    private String pass;

    @Column(name = "Role", length = 25)
    private String role;

    @Column(name = "Dépot")
    private Integer depot;

    @Column(name = "Gsm", length = 20)
    private String gsm;

    @Column(name = "Note1", length = 20)
    private String note1;

    @Column(name = "Note2", length = 20)
    private String note2;

    @Column(name = "Note3", length = 20)
    private String note3;

    @Column(name = "état", length = 10)
    private String etat;

    // getters and setters
}
