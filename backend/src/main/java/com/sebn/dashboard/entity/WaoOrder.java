package com.sebn.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wao_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "WAAUNR", length = 30)
    private String waaunr;

    @Column(name = "WATENR", length = 60)
    private String watenr;

    @Column(name = "WAENTE", length = 8)
    private String waente;

    @Column(name = "WAENJK", length = 6)
    private String waenjk;

    @Column(name = "WASTTE", length = 8)
    private String wastte;

    @Column(name = "WASTJK", length = 6)
    private String wastjk;

    @Column(name = "WASACH", length = 20)
    private String wasach;

    @Column(name = "WADISP", length = 20)
    private String wadisp;

    @Column(name = "WAKOTR", length = 20)
    private String wakotr;

    @Column(name = "WAKOMM", length = 255)
    private String wakomm;

    @Column(name = "WADK01", length = 30)
    private String wadk01;

    @Column(name = "WADD01", length = 255)
    private String wadd01;

    @Column(name = "WAURMG", precision = 12, scale = 2)
    private BigDecimal waurmg;

    @Column(name = "WAFEMG", precision = 12, scale = 2)
    private BigDecimal wafemg;

    @Column(name = "WAGFMG", precision = 12, scale = 2)
    private BigDecimal wagfmg;

    @Column(name = "WAAUMG", precision = 12, scale = 2)
    private BigDecimal waaumg;

    @Column(name = "WATLKZ", length = 5)
    private String watlkz;

    @Column(name = "WAWEDA", length = 8)
    private String waweda;

    @Column(name = "WASTAT", length = 5)
    private String wastat;

    @Column(name = "WAMAST", length = 20)
    private String wamast;

    @Column(name = "WAAGST", length = 20)
    private String waagst;

    @Column(name = "WAAUAR", length = 20)
    private String waauar;

    @Column(name = "WAPRIO", length = 10)
    private String waprio;

    @Column(name = "WATEAR", length = 30)
    private String watear;

    @Column(name = "WABEZ1", length = 255)
    private String wabez1;

    @Column(name = "WABEZ2", length = 255)
    private String wabez2;

    @Column(name = "WARMAG", length = 30)
    private String warmag;

    @Column(name = "WARMDA", length = 8)
    private String warmda;

    @Column(name = "WARMUZ", length = 6)
    private String warmuz;

    @Column(name = "WARMUS", length = 30)
    private String warmus;

    @Column(name = "WAANDA", length = 8)
    private String waanda;

    @Column(name = "WAANUS", length = 30)
    private String waanus;

    @Column(name = "WAAEDA", length = 8)
    private String waaeda;

    @Column(name = "WAAEUS", length = 30)
    private String waaeus;
}
