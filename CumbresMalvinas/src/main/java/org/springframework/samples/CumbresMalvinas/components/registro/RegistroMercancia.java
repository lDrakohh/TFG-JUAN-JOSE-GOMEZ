package org.springframework.samples.CumbresMalvinas.components.registro;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.LocalDate;

import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "registros_mercancia")
public class RegistroMercancia {

    @ManyToOne(optional = false)
    @JoinColumn(name = "prevision_id", nullable = false)
    private Prevision prevision;

    @Column(nullable = false)
    private Integer cantidadTraida;

    @Column(nullable = false)
    private LocalDate fecha;  // Fecha del movimiento

}
