package com.trabalhointeligencia.uniparking.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vaga")
    private Integer idVaga;

    @Column(name = "numero_vaga", nullable = false, length = 10)
    private String numeroVaga;

    @ManyToOne
    @JoinColumn(name = "id_estacionamento", referencedColumnName = "id_estacionamento", nullable = false)
    private Estacionamento estacionamento;

    public Integer getIdVaga() {
        return idVaga;
    }
    public void setIdVaga(Integer idVaga) {
        this.idVaga = idVaga;
    }
    public String getNumeroVaga() {
        return numeroVaga;
    }
    public void setNumeroVaga(String numeroVaga) {
        this.numeroVaga = numeroVaga;
    }
    public Estacionamento getEstacionamento() {
        return estacionamento;
    }
    public void setEstacionamento(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
    }
}
