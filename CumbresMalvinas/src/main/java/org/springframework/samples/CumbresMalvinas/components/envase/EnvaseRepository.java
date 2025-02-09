package org.springframework.samples.CumbresMalvinas.components.envase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvaseRepository extends JpaRepository<Envase, Integer> {
}
