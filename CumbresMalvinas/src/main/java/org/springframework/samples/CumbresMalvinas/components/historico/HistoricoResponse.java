package org.springframework.samples.CumbresMalvinas.components.historico;

import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercancia;
import java.util.List;

public class HistoricoResponse {
    private List<Prevision> previsiones;
    private List<RegistroMercancia> registros;

    public HistoricoResponse(List<Prevision> previsiones, List<RegistroMercancia> registros) {
        this.previsiones = previsiones;
        this.registros = registros;
    }

    public List<Prevision> getPrevisiones() {
        return previsiones;
    }

    public void setPrevisiones(List<Prevision> previsiones) {
        this.previsiones = previsiones;
    }

    public List<RegistroMercancia> getRegistros() {
        return registros;
    }

    public void setRegistros(List<RegistroMercancia> registros) {
        this.registros = registros;
    }
}
