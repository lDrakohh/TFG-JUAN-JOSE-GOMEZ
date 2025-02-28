package org.springframework.samples.CumbresMalvinas.components.registro;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionRepository;

@Service
public class RegistroMercanciaService {

    private final RegistroMercanciaRepository registroMercanciaRepository;
    private final PrevisionRepository previsionRepository;

    @Autowired
    public RegistroMercanciaService(RegistroMercanciaRepository registroMercanciaRepository, PrevisionRepository previsionRepository) {
        this.registroMercanciaRepository = registroMercanciaRepository;
        this.previsionRepository = previsionRepository;
    }

    public List<RegistroMercancia> findByEmpresaAndFecha(Integer empresaId, LocalDate fecha) {
        return registroMercanciaRepository.findByEmpresaAndFecha(empresaId, fecha);
    }
    
    public List<RegistroMercancia> findByEmpresa(Integer empresaId) {
        return registroMercanciaRepository.findByEmpresa(empresaId);
    }
    

    // Registrar un movimiento de mercancía y actualizar la previsión
    public RegistroMercancia registrarRegistro(Prevision prevision, Integer cantidadTraida) {
        RegistroMercancia registro = new RegistroMercancia();
        registro.setPrevision(prevision);
        registro.setCantidadTraida(cantidadTraida);
        registro.setFecha(LocalDate.now()); 

        actualizarPrevision(prevision, cantidadTraida);

        return registroMercanciaRepository.save(registro);
    }

    // Método para actualizar los campos de la previsión
    private void actualizarPrevision(Prevision prevision, Integer cantidadTraida) {
        Integer prevTraidas = prevision.getPrevTraidas();
        Integer prevFaltantes = prevision.getPrevFaltantes();

        prevision.setPrevTraidas(prevTraidas + cantidadTraida);
        prevision.setPrevFaltantes(Math.max(prevFaltantes - cantidadTraida, 0));

        previsionRepository.save(prevision);
    }
}
