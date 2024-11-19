package org.springframework.samples.CumbresMalvinas.components.fruta;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrutaService {

    @Autowired
    private FrutaRepository frutaRepository;

    public List<Fruta> findAll() {
        return frutaRepository.findAll();
    }

    public Optional<Fruta> findById(Integer id) {
        return frutaRepository.findById(id);
    }

    public Fruta save(Fruta fruta) {
        return frutaRepository.save(fruta);
    }

    public void deleteById(Integer id) {
        frutaRepository.deleteById(id);
    }
}

