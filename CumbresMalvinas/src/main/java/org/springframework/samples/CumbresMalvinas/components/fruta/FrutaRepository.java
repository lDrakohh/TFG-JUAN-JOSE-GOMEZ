package org.springframework.samples.CumbresMalvinas.components.fruta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrutaRepository extends JpaRepository<Fruta, Integer> {

    Fruta update(Fruta fruta);
}
