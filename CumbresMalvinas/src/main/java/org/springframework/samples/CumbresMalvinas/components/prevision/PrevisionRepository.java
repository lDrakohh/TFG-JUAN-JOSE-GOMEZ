package org.springframework.samples.CumbresMalvinas.components.prevision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrevisionRepository extends JpaRepository<Prevision, Integer> {
    
    List<Prevision> findByFecha(LocalDate fecha);
    
    List<Prevision> findByEmpresaAndFecha(Empresa empresa, LocalDate fecha);
}
