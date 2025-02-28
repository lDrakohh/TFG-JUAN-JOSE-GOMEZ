package org.springframework.samples.CumbresMalvinas.components.fruta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/frutas")
public class FrutaController {

    private final FrutaService frutaService;

    @Autowired
    public FrutaController(FrutaService frutaService) {
        this.frutaService = frutaService;
    }

    @GetMapping
    public ResponseEntity<List<Fruta>> getAllFrutas() {
        List<Fruta> frutas = frutaService.findAll();
        return ResponseEntity.ok(frutas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fruta> getFrutaById(@PathVariable Integer id) {
        Optional<Fruta> fruta = frutaService.findById(id);
        return fruta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Fruta> createFruta(@RequestBody Fruta fruta) {
        Fruta nuevaFruta = frutaService.save(fruta);
        return ResponseEntity.ok(nuevaFruta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fruta> updateFruta(@PathVariable Integer id, @RequestBody Fruta frutaDetails) {
        Optional<Fruta> frutaOptional = frutaService.findById(id);

        if (frutaOptional.isPresent()) {
            Fruta fruta = frutaOptional.get();
            fruta.setVariedad(frutaDetails.getVariedad());
            fruta.setCalidad(frutaDetails.getCalidad());
            fruta.setMarca(frutaDetails.getMarca());
            fruta.setEnvase(frutaDetails.getEnvase());

            Fruta frutaActualizada = frutaService.save(fruta);
            return ResponseEntity.ok(frutaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFruta(@PathVariable Integer id) {
        if (frutaService.findById(id).isPresent()) {
            frutaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
