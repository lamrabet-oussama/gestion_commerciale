package com.moonsystem.gestion_commerciale.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MesInfox")
public class MesInfox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private Integer num;

    @Column(name = "Nom", length = 200)
    private String nomSociete;

    @Column(name = "Activité", length = 250)
    private String activite;

    @Column(name = "Adres", length = 500)
    private String adresse;

    @Column(length = 500)
    private String piedPage;

    @Column(length = 25)
    private String serial;

    private String fLogo;

    @Column(length = 25)
    private String fCod;

    @Column(length = 500)
    private String bNom;

    @Column(length = 25)
    private String bSerial;

    @Column(length = 250)
    private String bActivite;

    @Column(length = 500)
    private String bAdresse;

    private String bLogo;

    @Column(length = 25)
    private String bCod;

    @Column(length = 500)
    private String note01;

    @Column(length = 500)
    private String note02;

    // getters and setters
}
