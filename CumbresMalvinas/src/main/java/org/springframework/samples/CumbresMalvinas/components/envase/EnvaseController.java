package org.springframework.samples.CumbresMalvinas.components.envase;

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

@RestController
@RequestMapping("/api/v1/envases")
public class EnvaseController {

    @Autowired
    private EnvaseService envaseService;

    @GetMapping("")
    public ResponseEntity<List<Envase>> getAllEnvases() {
        List<Envase> envases = envaseService.findAll();
        return ResponseEntity.ok(envases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envase> getEnvaseById(@PathVariable Integer id) {
        Optional<Envase> envase = envaseService.findById(id);
        return envase.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Envase> createEnvase(@RequestBody Envase envase) {
        Envase nuevoEnvase = envaseService.save(envase);
        System.out.println("HOLAAAA");

        System.out.println(nuevoEnvase);
        return ResponseEntity.ok(nuevoEnvase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Envase> updateEnvase(@PathVariable Integer id, @RequestBody Envase envaseDetails) {
        Optional<Envase> envaseOptional = envaseService.findById(id);

        if (envaseOptional.isPresent()) {
            Envase envase = envaseOptional.get();
            envase.setNombre(envaseDetails.getNombre());
            envase.setPesoGramos(envaseDetails.getPesoGramos());

            Envase envaseActualizado = envaseService.save(envase);
            return ResponseEntity.ok(envaseActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvase(@PathVariable Integer id) {
        if (envaseService.findById(id).isPresent()) {
            envaseService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
