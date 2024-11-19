package org.springframework.samples.CumbresMalvinas.components.prevision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/previsiones")
public class PrevisionController {

    private final PrevisionService previsionService;

    @Autowired
    public PrevisionController(PrevisionService previsionService) {
        this.previsionService = previsionService;
    }

    @GetMapping
    public ResponseEntity<List<Prevision>> getAllPrevisiones() {
        List<Prevision> previsiones = previsionService.findAll();
        return ResponseEntity.ok(previsiones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prevision> getPrevisionById(@PathVariable Integer id) {
        Optional<Prevision> prevision = previsionService.findById(id);
        return prevision.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Prevision>> getPrevisionesByFecha(@PathVariable String fecha) {
        LocalDate parsedFecha = LocalDate.parse(fecha);  // Convierte la fecha de String a LocalDate
        List<Prevision> previsiones = previsionService.findByFecha(parsedFecha);
        return ResponseEntity.ok(previsiones);
    }

    @GetMapping("/empresa/{empresaId}/fecha/{fecha}")
    public ResponseEntity<List<Prevision>> getPrevisionesByEmpresaAndFecha(@PathVariable Integer empresaId, @PathVariable String fecha) {
        LocalDate parsedFecha = LocalDate.parse(fecha);
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);  // Asegúrate de obtener la empresa por ID correctamente
        List<Prevision> previsiones = previsionService.findByEmpresaAndFecha(empresa, parsedFecha);
        return ResponseEntity.ok(previsiones);
    }

    @PostMapping
    public ResponseEntity<Prevision> createPrevision(@RequestBody Prevision prevision) {
        Prevision nuevaPrevision = previsionService.save(prevision);
        return ResponseEntity.ok(nuevaPrevision);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prevision> updatePrevision(@PathVariable Integer id, @RequestBody Prevision previsionDetails) {
        Optional<Prevision> previsionOptional = previsionService.findById(id);

        if (previsionOptional.isPresent()) {
            Prevision prevision = previsionOptional.get();
            prevision.setPrevisto(previsionDetails.getPrevisto());
            prevision.setPrevTraidas(previsionDetails.getPrevTraidas());
            prevision.setPrevFaltantes(previsionDetails.getPrevFaltantes());
            prevision.setFecha(previsionDetails.getFecha());
            prevision.setEmpresa(previsionDetails.getEmpresa());
            prevision.setFruta(previsionDetails.getFruta());

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
