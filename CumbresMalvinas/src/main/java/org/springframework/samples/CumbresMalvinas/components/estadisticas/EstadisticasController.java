package org.springframework.samples.CumbresMalvinas.components.estadisticas;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionRepository;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercancia;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estadisticas")
public class EstadisticasController {

    @Autowired
    private PrevisionRepository previsionRepository;

    @Autowired
    private RegistroMercanciaRepository registroMercanciaRepository;

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenEstadisticas() {
        List<Prevision> previsiones = previsionRepository.findAll();
        List<RegistroMercancia> registros = registroMercanciaRepository.findAll();
    
        System.out.println("Previsiones: " + previsiones);
        System.out.println("Registros: " + registros);
    
        Map<String, Object> resumen = new HashMap<>();
    
        // Agregar estadísticas por semana y empresa
        Map<String, Map<Integer, Integer>> previsionesPorEmpresaYSemana = new HashMap<>();
        Map<String, Map<Integer, Integer>> registrosPorEmpresaYSemana = new HashMap<>();
        Map<Integer, String> fechasDeSemana = new HashMap<>();
    
        for (Prevision prevision : previsiones) {
            int semana = prevision.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            LocalDate startOfWeek = prevision.getFecha().with(WeekFields.ISO.getFirstDayOfWeek());
            LocalDate endOfWeek = startOfWeek.plusDays(6);
    
            String semanaLabel = startOfWeek + " al " + endOfWeek;
            fechasDeSemana.put(semana, semanaLabel);
    
            previsionesPorEmpresaYSemana
                .computeIfAbsent(prevision.getEmpresa().getNombreEmpresa(), k -> new HashMap<>())
                .put(semana, previsionesPorEmpresaYSemana.getOrDefault(prevision.getEmpresa().getNombreEmpresa(), new HashMap<>()).getOrDefault(semana, 0) + prevision.getPrevisto());
        }
    
        for (RegistroMercancia registro : registros) {
            int semana = registro.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            LocalDate startOfWeek = registro.getFecha().with(WeekFields.ISO.getFirstDayOfWeek());
            LocalDate endOfWeek = startOfWeek.plusDays(6);
    
            String semanaLabel = startOfWeek + " al " + endOfWeek;
            fechasDeSemana.put(semana, semanaLabel);
    
            registrosPorEmpresaYSemana
                .computeIfAbsent(registro.getPrevision().getEmpresa().getNombreEmpresa(), k -> new HashMap<>())
                .put(semana, registrosPorEmpresaYSemana.getOrDefault(registro.getPrevision().getEmpresa().getNombreEmpresa(), new HashMap<>()).getOrDefault(semana, 0) + registro.getCantidadTraida());
        }
    
        resumen.put("previsiones", previsionesPorEmpresaYSemana);
        resumen.put("registros", registrosPorEmpresaYSemana);
        resumen.put("fechasDeSemana", fechasDeSemana);
    
        return ResponseEntity.ok(resumen);
    }
    
}
