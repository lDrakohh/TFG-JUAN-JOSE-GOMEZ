package org.springframework.samples.CumbresMalvinas.components.fruta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FrutaService {

    private final FrutaRepository frutaRepository;

    @Autowired
    public FrutaService(FrutaRepository frutaRepository) {
        this.frutaRepository = frutaRepository;
    }

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
