package com.petsup.api.entities;

import com.petsup.api.entities.enums.Especie;
import com.petsup.api.entities.enums.Raca;
import com.petsup.api.entities.usuario.UsuarioCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String sexo;

    private LocalDate dataNasc;

    private Integer castrado;

    private Especie especie;

    private Raca raca;

    @OneToMany(mappedBy = "fkPet", fetch = FetchType.LAZY)
    private List<Agendamento> agendamentos;

    @ManyToOne
    @JoinColumn(name = "fkCliente")
    private UsuarioCliente fkCliente;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }

    public Integer getCastrado() {
        return castrado;
    }

    public void setCastrado(Integer castrado) {
        this.castrado = castrado;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public UsuarioCliente getFkCliente() {
        return fkCliente;
    }

    public void setFkCliente(UsuarioCliente fkCliente) {
        this.fkCliente = fkCliente;
    }

    public List<Agendamento> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
    }
}
