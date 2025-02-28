package org.springframework.samples.CumbresMalvinas.components.envase;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvaseService {

    @Autowired
    private EnvaseRepository envaseRepository;

    public List<Envase> findAll() {
        return envaseRepository.findAll();
    }

    public Optional<Envase> findById(Integer id) {
        return envaseRepository.findById(id);
    }

    public Envase save(Envase envase) {
        return envaseRepository.save(envase);
    }

    public void deleteById(Integer id) {
        envaseRepository.deleteById(id);
    }
}
