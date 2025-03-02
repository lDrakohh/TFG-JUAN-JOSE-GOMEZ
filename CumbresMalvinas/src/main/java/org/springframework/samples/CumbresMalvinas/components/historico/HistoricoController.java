package org.springframework.samples.CumbresMalvinas.components.historico;

import org.springframework.samples.CumbresMalvinas.components.historico.HistoricoResponse;
import org.springframework.samples.CumbresMalvinas.components.historico.HistoricoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/{empresaId}")
    public ResponseEntity<?> obtenerHistorico(
            @PathVariable Integer empresaId,
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);

        Optional<HistoricoResponse> historico = historicoService.obtenerHistorico(empresaId, fechaInicio, fechaFin);

        return historico.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
