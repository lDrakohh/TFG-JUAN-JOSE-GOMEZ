package org.springframework.samples.CumbresMalvinas.components.historico;

import org.springframework.samples.CumbresMalvinas.components.historico.HistoricoResponse;
import org.springframework.samples.CumbresMalvinas.components.historico.HistoricoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerHistorico(
            @RequestParam List<Integer> empresas,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {

        if (inicio.isAfter(fin)) {
            return ResponseEntity.badRequest().body("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        Optional<HistoricoResponse> historico = historicoService.obtenerHistorico(empresas, inicio, fin);
        return historico.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}

