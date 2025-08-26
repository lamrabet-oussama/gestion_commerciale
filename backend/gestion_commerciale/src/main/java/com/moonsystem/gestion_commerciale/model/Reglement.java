package com.moonsystem.gestion_commerciale.model;

import com.moonsystem.gestion_commerciale.model.enums.MvtType;
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
@Table(name = "Reglement")
public class Reglement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regl")
    private Integer idRegl;

    @Column(name = "dat_regl")
    private LocalDateTime datRegl;

    @Column(name = "mouvement", length = 6)
    private MvtType mouvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier")
    private Tier tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_cod")
    private User user;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;
    @Column(name = "espece", precision = 12, scale = 2)
    private BigDecimal espece;

    @Column(name = "cheque", precision = 12, scale = 2)
    private BigDecimal cheque;
    @Column(name = "det_cheque")
    private String det_cheque;

    // getters and setters
}

