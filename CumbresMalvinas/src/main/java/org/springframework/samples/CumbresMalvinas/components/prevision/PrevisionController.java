package org.springframework.samples.CumbresMalvinas.components.prevision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.empresa.EmpresaService;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.components.fruta.FrutaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/previsiones")
public class PrevisionController {

    private final PrevisionService previsionService;
    private final FrutaService frutaService;
    private final EmpresaService empresaService;

    @Autowired
    public PrevisionController(PrevisionService previsionService, FrutaService frutaService,
            EmpresaService empresaService) {
        this.previsionService = previsionService;
        this.empresaService = empresaService;
        this.frutaService = frutaService;
    }

    @GetMapping
    public ResponseEntity<List<Prevision>> getAllPrevisiones() {
        List<Prevision> previsiones = previsionService.findAll();
        return ResponseEntity.ok(previsiones);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<Prevision>> getPrevisionesDeHoy() {
        LocalDate hoy = LocalDate.now();
        List<Prevision> previsiones = previsionService.findByFecha(hoy);
        return ResponseEntity.ok(previsiones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prevision> getPrevisionById(@PathVariable Integer id) {
        Optional<Prevision> prevision = previsionService.findById(id);
        return prevision.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Prevision>> getPrevisionesByFecha(@PathVariable String fecha) {
        LocalDate parsedFecha = LocalDate.parse(fecha);
        List<Prevision> previsiones = previsionService.findByFecha(parsedFecha);
        return ResponseEntity.ok(previsiones);
    }

    @GetMapping("/empresa/{empresaId}/fecha/{fecha}")
    public ResponseEntity<List<Prevision>> getPrevisionesByEmpresaAndFecha(@PathVariable Integer empresaId,
            @PathVariable String fecha) {
        LocalDate parsedFecha = LocalDate.parse(fecha);
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        List<Prevision> previsiones = previsionService.findByEmpresaAndFecha(empresa, parsedFecha);
        return ResponseEntity.ok(previsiones);
    }

    @PostMapping
    public ResponseEntity<?> createPrevision(@RequestBody PrevisionDTO previsionDTO) {
        if (previsionDTO.getEmpresaId() == null) {
            return ResponseEntity.badRequest().body("El campo empresaId es obligatorio.");
        }

        Empresa empresa = empresaService.findById(previsionDTO.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Fruta no encontrada"));

        if (empresa == null) {
            return ResponseEntity.badRequest().body("Empresa no encontrada.");
        }

        Fruta fruta = frutaService.findById(previsionDTO.getFrutaId())
                .orElseThrow(() -> new RuntimeException("Fruta no encontrada"));

        if (fruta == null) {
            return ResponseEntity.badRequest().body("Fruta no encontrada.");
        }

        Prevision nuevaPrevision = new Prevision();
        nuevaPrevision.setEmpresa(empresa);
        nuevaPrevision.setFruta(fruta);
        nuevaPrevision.setPrevisto(previsionDTO.getPrevisto());
        nuevaPrevision.setPrevTraidas(0);
        nuevaPrevision.setPrevFaltantes(previsionDTO.getPrevisto());
        nuevaPrevision.setFecha(LocalDate.now());

        previsionService.save(nuevaPrevision);

        return ResponseEntity.ok(nuevaPrevision);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prevision> updatePrevision(@PathVariable Integer id,
            @RequestBody Prevision previsionDetails) {
        Optional<Prevision> previsionOptional = previsionService.findById(id);

        if (previsionOptional.isPresent()) {
            Prevision prevision = previsionOptional.get();

            if (previsionDetails.getPrevisto() != null) {
                prevision.setPrevisto(previsionDetails.getPrevisto());
            }
            if (previsionDetails.getPrevTraidas() != null) {
                prevision.setPrevTraidas(previsionDetails.getPrevTraidas());
            }
            if (previsionDetails.getPrevFaltantes() != null) {
                prevision.setPrevFaltantes(previsionDetails.getPrevFaltantes());
            }

            Prevision previsionActualizada = previsionService.save(prevision);
            return ResponseEntity.ok(previsionActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrevision(@PathVariable Integer id) {
        if (previsionService.findById(id).isPresent()) {
            previsionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
