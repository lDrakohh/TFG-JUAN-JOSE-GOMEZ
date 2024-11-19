package org.springframework.samples.CumbresMalvinas.components.empresa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.springframework.samples.CumbresMalvinas.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresas")
public class Empresa extends BaseEntity {

    @Column(nullable = false)
    private String nombreEmpresa;

    @Column(nullable = false)
    private String nombrePropietario;

    @Column(nullable = false)
    private String apellidoPropietario;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, unique = true)
    private String cif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda moneda;

    public enum Moneda {
        EUROS,
        LEU,
        ZLOTY
    }
}
