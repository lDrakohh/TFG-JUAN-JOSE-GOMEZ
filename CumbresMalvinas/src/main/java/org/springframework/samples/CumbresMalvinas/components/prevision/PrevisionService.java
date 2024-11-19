package org.springframework.samples.CumbresMalvinas.components.prevision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrevisionService {

    private final PrevisionRepository previsionRepository;

    @Autowired
    public PrevisionService(PrevisionRepository previsionRepository) {
        this.previsionRepository = previsionRepository;
    }

    public List<Prevision> findAll() {
        return previsionRepository.findAll();
    }

    public Optional<Prevision> findById(Integer id) {
        return previsionRepository.findById(id);
    }

    public Prevision save(Prevision prevision) {
        return previsionRepository.save(prevision);
    }

    public void deleteById(Integer id) {
        previsionRepository.deleteById(id);
    }

    public List<Prevision> findByFecha(LocalDate fecha) {
        return previsionRepository.findByFecha(fecha);
    }

    public List<Prevision> findByEmpresaAndFecha(Empresa empresa, LocalDate fecha) {
        return previsionRepository.findByEmpresaAndFecha(empresa, fecha);
    }
}
