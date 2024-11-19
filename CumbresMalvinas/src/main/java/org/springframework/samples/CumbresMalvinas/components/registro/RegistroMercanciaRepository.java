package org.springframework.samples.CumbresMalvinas.components.registro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroMercanciaRepository extends JpaRepository<RegistroMercancia, Integer> {
}
