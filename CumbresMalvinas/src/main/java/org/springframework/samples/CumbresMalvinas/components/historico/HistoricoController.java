package org.springframework.samples.CumbresMalvinas.components.historico;

import org.springframework.samples.CumbresMalvinas.components.email.EmailService;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {

    private final HistoricoService historicoService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(HistoricoController.class);

    public HistoricoController(HistoricoService historicoService, EmailService emailService) {
        this.historicoService = historicoService;
        this.emailService = emailService;
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

    @PostMapping("/enviar-correo")
    public ResponseEntity<String> enviarHistoricoPorCorreo(@RequestBody Map<String, String> datos) {
        try {
            String destinatario = datos.get("email");
            String contenido = datos.get("contenido");
    
            if (destinatario == null || contenido == null) {
                return ResponseEntity.badRequest().body("Faltan datos para enviar el correo.");
            }
    
            emailService.enviarHistoricoPorCorreo(destinatario, "Histórico de Previsiones y Registros", contenido);
            return ResponseEntity.ok("Correo enviado correctamente.");
        } catch (Exception e) {
            logger.error("Error al enviar el correo: ", e);
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }
    
}

