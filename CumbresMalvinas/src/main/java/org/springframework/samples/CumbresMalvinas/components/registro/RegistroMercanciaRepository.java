package org.springframework.samples.CumbresMalvinas.components.registro;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroMercanciaRepository extends JpaRepository<RegistroMercancia, Integer> {
    @Query("SELECT r FROM RegistroMercancia r WHERE r.prevision.empresa.id = :empresaId AND r.fecha = :fecha")
    List<RegistroMercancia> findByEmpresaAndFecha(@Param("empresaId") Integer empresaId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT r FROM RegistroMercancia r WHERE r.prevision.empresa.id = :empresaId")
    List<RegistroMercancia> findByEmpresa(@Param("empresaId") Integer empresaId);

    List<RegistroMercancia> findByFecha(LocalDate hoy);
}
