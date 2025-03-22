package org.springframework.samples.CumbresMalvinas.components.historico;

import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionRepository;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercancia;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {

    private final PrevisionRepository previsionRepository;
    private final RegistroMercanciaRepository registroMercanciaRepository;

    public HistoricoService(PrevisionRepository previsionRepository,
            RegistroMercanciaRepository registroMercanciaRepository) {
        this.previsionRepository = previsionRepository;
        this.registroMercanciaRepository = registroMercanciaRepository;
    }

    public Optional<HistoricoResponse> obtenerHistorico(List<Integer> empresaIds, LocalDate inicio, LocalDate fin) {

        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        List<Prevision> previsiones = previsionRepository.findByEmpresaAndFechaBetween(empresaIds, inicio, fin);
        List<RegistroMercancia> registros = registroMercanciaRepository.findByEmpresaAndFechaHistorico(empresaIds,
                inicio, fin);

        System.out.println("Previsiones encontradas: " + previsiones.size());
        System.out.println("Registros encontrados: " + registros.size());

        if (previsiones.isEmpty() && registros.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new HistoricoResponse(previsiones, registros));
    }
}
