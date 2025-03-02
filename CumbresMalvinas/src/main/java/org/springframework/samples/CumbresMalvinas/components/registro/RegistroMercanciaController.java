package org.springframework.samples.CumbresMalvinas.components.registro;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/empresa/{empresaId}/hoy")
    public ResponseEntity<List<RegistroMercancia>> getRegistrosDeHoyPorEmpresa(@PathVariable Integer empresaId) {
        LocalDate hoy = LocalDate.now();
        List<RegistroMercancia> registros = registroMercanciaService.findByEmpresaAndFecha(empresaId, hoy);
        return ResponseEntity.ok(registros);
    }

    @PostMapping("/{previsionId}")
    public ResponseEntity<RegistroMercancia> registrarRegistro(
            @PathVariable Integer previsionId,
            @RequestBody RegistroRequest request) {

        Optional<Prevision> previsionOptional = previsionService.findById(previsionId);

        if (previsionOptional.isPresent()) {
            Prevision prevision = previsionOptional.get();
            RegistroMercancia registro = registroMercanciaService.registrarRegistro(prevision,
                    request.getCantidadTraida());
            return ResponseEntity.ok(registro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/prevision/{previsionId}")
    public ResponseEntity<List<RegistroMercancia>> getRegistrosPorPrevision(@PathVariable Integer previsionId) {
        List<RegistroMercancia> registros = registroMercanciaService.findByPrevisionId(previsionId);

        if (registros.isEmpty()) {
            return ResponseEntity.noContent().build(); //204 
        }

        return ResponseEntity.ok(registros); //200 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable Integer id) {
        Optional<RegistroMercancia> registroOptional = registroMercanciaService.findById(id);

        if (registroOptional.isPresent()) {
            RegistroMercancia registro = registroOptional.get();
            Prevision prevision = registro.getPrevision();

            prevision.setPrevTraidas(prevision.getPrevTraidas() - registro.getCantidadTraida());
            prevision.setPrevFaltantes(prevision.getPrevisto() - prevision.getPrevTraidas());

            previsionService.save(prevision);
            registroMercanciaService.deleteById(id);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO para el JSON
    public static class RegistroRequest {
        private Integer cantidadTraida;

        public Integer getCantidadTraida() {
            return cantidadTraida;
        }

        public void setCantidadTraida(Integer cantidadTraida) {
            this.cantidadTraida = cantidadTraida;
        }
    }
}
