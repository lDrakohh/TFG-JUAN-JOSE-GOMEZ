package org.springframework.samples.CumbresMalvinas.components.prevision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "previsiones")
public class Prevision extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false) 
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fruta_id", nullable = false)
    private Fruta fruta;

    @Column(nullable = false)
    private Integer previsto;

    @Column(nullable = false)
    private Integer prevTraidas;

    @Column(nullable = false)
    private Integer prevFaltantes;

    @Column(nullable = false)
    private LocalDate fecha; 

    @Transient
    public String getTipo() {
        if (fruta != null && fruta.getEnvase() != null) {
            return fruta.getCalidad() + " - " + fruta.getEnvase().getNombre();
        }
        return "Tipo no definido";
    }

    // Método para actualizar las previsiones cuando se traen mercancías
    public void actualizarPrevision(Integer cantidadTraida) {
        this.prevTraidas += cantidadTraida;
        this.prevFaltantes = Math.max(this.prevFaltantes - cantidadTraida, 0);  // No cantidades negativas
    }
}
