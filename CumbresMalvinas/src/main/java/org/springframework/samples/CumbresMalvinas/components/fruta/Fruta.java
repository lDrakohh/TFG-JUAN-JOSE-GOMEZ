package org.springframework.samples.CumbresMalvinas.components.fruta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.springframework.samples.CumbresMalvinas.components.envase.Envase;
import org.springframework.samples.CumbresMalvinas.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "frutas")
public class Fruta extends BaseEntity {

    @Column(nullable = false)
    private String variedad;

    @Column(nullable = false)
    private String calidad;

    @Column(nullable = false)
    private String marca;

    @ManyToOne(optional = true)
    @JoinColumn(name = "envase_id", nullable = false)
    private Envase envase;
}    
