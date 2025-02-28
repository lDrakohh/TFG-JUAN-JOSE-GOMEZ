package org.springframework.samples.CumbresMalvinas.components.registro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/registros")
public class RegistroMercanciaController {

    private final RegistroMercanciaService registroMercanciaService;

    private final PrevisionService previsionService;

    @Autowired
    public RegistroMercanciaController(RegistroMercanciaService registroMercanciaService,
            PrevisionService previsionService) {
        this.registroMercanciaService = registroMercanciaService;
        this.previsionService = previsionService;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<RegistroMercancia>> getRegistrosByEmpresaAndFecha(
            @PathVariable Integer empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<RegistroMercancia> registros;

        if (fecha != null) {
            registros = registroMercanciaService.findByEmpresaAndFecha(empresaId, fecha);
        } else {
            registros = registroMercanciaService.findByEmpresa(empresaId);
        }

        return ResponseEntity.ok(registros);
    }

    // Registrar el registro de mercancía para una previsión específica
    @PostMapping("/{previsionId}")
    public ResponseEntity<RegistroMercancia> registrarRegistro(@PathVariable Integer previsionId,
            @RequestParam Integer cantidadTraida) {
        Optional<Prevision> previsionOptional = previsionService.findById(previsionId);

        if (previsionOptional.isPresent()) {
            Prevision prevision = previsionOptional.get();
            RegistroMercancia registro = registroMercanciaService.registrarRegistro(prevision, cantidadTraida);
            return ResponseEntity.ok(registro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
