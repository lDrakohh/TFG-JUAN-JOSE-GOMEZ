package org.springframework.samples.CumbresMalvinas.components.envase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.springframework.samples.CumbresMalvinas.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "envases")
public class Envase extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer pesoGramos;
}
