package com.moonsystem.gestion_commerciale.dto;

import com.moonsystem.gestion_commerciale.model.MesInfox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur complet
public class MesInfoxDto {

    private Integer num;

    private String nomSociete;

    private String activite;

    private String adresse;

    private String piedPage;

    private String serial;

    private String fLogo;

    private String fCod;

    private String bNom;

    private String bSerial;

    private String bActivite;

    private String bAdresse;

    private String bLogo;

    private String bCod;

    private String note01;

    private String note02;

    public static MesInfoxDto fromEntity(MesInfox info) {
        return new MesInfoxDto(
                info.getNum(),
                info.getNomSociete(),
                info.getActivite(),
                info.getAdresse(),
                info.getPiedPage(),
                info.getSerial(),
                info.getFLogo(),
                info.getFCod(),
                info.getBNom(),
                info.getBSerial(),
                info.getBActivite(),
                info.getBAdresse(),
                info.getBLogo(),
                info.getBCod(),
                info.getNote01(),
                info.getNote02()
        );
    }

    public static MesInfox toEntity(MesInfoxDto dto) {
        if (dto == null) {
            return null;
        }
        MesInfox info = new MesInfox();
        info.setNum(dto.getNum());
        info.setNomSociete(dto.getNomSociete());
        info.setActivite(dto.getActivite());
        info.setAdresse(dto.getAdresse());
        info.setPiedPage(dto.getPiedPage());
        info.setSerial(dto.getSerial());
        info.setFLogo(dto.getFLogo());
        info.setFCod(dto.getFCod());
        info.setBNom(dto.getBNom());
        info.setBSerial(dto.getBSerial());
        info.setBActivite(dto.getBActivite());
        info.setBAdresse(dto.getBAdresse());
        info.setBCod(dto.getBCod());
        info.setBLogo(dto.getBLogo());
        info.setNote01(dto.getNote01());
        info.setNote02(dto.getNote02());
        return info;
    }
}
