package org.springframework.samples.CumbresMalvinas.components.registro;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;

@Service
public class RegistroMercanciaService {

    private final RegistroMercanciaRepository registroMercanciaRepository;
    private final PrevisionService previsionService;

    @Autowired
    public RegistroMercanciaService(RegistroMercanciaRepository registroMercanciaRepository, PrevisionService previsionService) {
        this.registroMercanciaRepository = registroMercanciaRepository;
        this.previsionService = previsionService;
    }

    @Transactional
    public RegistroMercancia registrarRegistro(Prevision prevision, Integer cantidadTraida) {
        RegistroMercancia registro = new RegistroMercancia();
        registro.setPrevision(prevision);
        registro.setCantidadTraida(cantidadTraida);
        registro.setFecha(LocalDate.now());

        // Actualizar los valores de la previsión
        int nuevasTraidas = prevision.getPrevTraidas() + cantidadTraida;
        int nuevasFaltantes = prevision.getPrevisto() - nuevasTraidas;

        prevision.setPrevTraidas(nuevasTraidas);
        prevision.setPrevFaltantes(nuevasFaltantes);

        previsionService.save(prevision);

        return registroMercanciaRepository.save(registro);
    }

    public List<RegistroMercancia> findByEmpresaAndFecha(Integer empresaId, LocalDate fecha) {
        return registroMercanciaRepository.findByEmpresaAndFecha(empresaId, fecha);
    }

    public Optional<RegistroMercancia> findById(Integer id) {
        return registroMercanciaRepository.findById(id);
    }

    public void deleteById(Integer id) {
        registroMercanciaRepository.deleteById(id);
    }

    public List<RegistroMercancia> findByPrevisionId(Integer previsionId) {
        return registroMercanciaRepository.findByPrevisionId(previsionId);
    }

    public List<RegistroMercancia> findByEmpresaAndFechaHistoric(Empresa empresa, LocalDate inicio, LocalDate fin) {
        return registroMercanciaRepository.findByEmpresaAndFechaHistorico(empresa.getId(), inicio, fin);
    }    
    
}
