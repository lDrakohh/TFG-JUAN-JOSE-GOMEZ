package org.springframework.samples.CumbresMalvinas.components.fruta;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/frutas")
@SecurityRequirement(name = "bearerAuth")
public class FrutaController {

    @Autowired
    private FrutaService frutaService;

    @GetMapping("/frutas")
    public ResponseEntity<List<Fruta>> getAllFrutas() {
        List<Fruta> frutas = frutaService.findAll();
        return ResponseEntity.ok(frutas);
    }

    @GetMapping("/frutas/{id}")
    public ResponseEntity<Fruta> getFrutaById(@PathVariable Integer id) {
        Optional<Fruta> fruta = frutaService.findById(id);
        return fruta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/frutas")
    public ResponseEntity<Fruta> createFruta(@RequestBody Fruta fruta) {
        Fruta nuevaFruta = frutaService.save(fruta);
        return ResponseEntity.ok(nuevaFruta);
    }

    @PutMapping("/frutas/{id}")
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

    @DeleteMapping("/frutas/{id}")
    public ResponseEntity<Void> deleteFruta(@PathVariable Integer id) {
        if (frutaService.findById(id).isPresent()) {
            frutaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
