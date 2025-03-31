package com.trabalhointeligencia.uniparking.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Valores")
public class Valores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valor")
    private Integer idValor;

    @ManyToOne
    @JoinColumn(name = "id_estacionamento", referencedColumnName = "id_estacionamento", nullable = false)
    private Estacionamento estacionamento;

    @Column(name = "tempo_minimo", nullable = false)
    private Integer tempoMinimo;

    @Column(name = "valor_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "valor_adicional", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAdicional;

    public Integer getIdValor() {
        return idValor;
    }
    public void setIdValor(Integer idValor) {
        this.idValor = idValor;
    }
    public Estacionamento getEstacionamento() {
        return estacionamento;
    }
    public void setEstacionamento(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
    }
    public Integer getTempoMinimo() {
        return tempoMinimo;
    }
    public void setTempoMinimo(Integer tempoMinimo) {
        this.tempoMinimo = tempoMinimo;
    }
    public BigDecimal getValorBase() {
        return valorBase;
    }
    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }
    public BigDecimal getValorAdicional() {
        return valorAdicional;
    }
    public void setValorAdicional(BigDecimal valorAdicional) {
        this.valorAdicional = valorAdicional;
    }
}
