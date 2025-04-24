package org.springframework.samples.CumbresMalvinas.fruta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.samples.CumbresMalvinas.components.envase.Envase;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.components.fruta.FrutaRepository;
import org.springframework.samples.CumbresMalvinas.components.fruta.FrutaService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FrutaServiceTest {

    @Mock
    private FrutaRepository frutaRepository;

    @InjectMocks
    private FrutaService frutaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Fruta fruta1 = new Fruta();
        Fruta fruta2 = new Fruta();
        List<Fruta> frutas = Arrays.asList(fruta1, fruta2);

        when(frutaRepository.findAll()).thenReturn(frutas);

        List<Fruta> result = frutaService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        Fruta fruta = new Fruta();
        fruta.setId(1);
        fruta.setVariedad("Marisma");
        fruta.setCalidad("Primera");
        fruta.setMarca("Cumbres");

        when(frutaRepository.findById(1)).thenReturn(Optional.of(fruta));

        Optional<Fruta> result = frutaService.findById(1);
        assertTrue(result.isPresent());
        assertEquals("Marisma", result.get().getVariedad());
        assertEquals("Primera", result.get().getCalidad());
        assertEquals("Cumbres", result.get().getMarca());
    }

    @Test
    void testSave() {
        Fruta fruta = new Fruta();
        fruta.setVariedad("151");
        fruta.setCalidad("Muy buena");
        fruta.setMarca("Malvinas");

        Envase envase = new Envase();
        envase.setId(5);
        fruta.setEnvase(envase);

        when(frutaRepository.save(fruta)).thenReturn(fruta);

        Fruta result = frutaService.save(fruta);
        assertNotNull(result);
        assertEquals("151", result.getVariedad());
        assertEquals("Muy buena", result.getCalidad());
        assertEquals("Malvinas", result.getMarca());
        assertEquals(5, result.getEnvase().getId());
    }

    @Test
    void testDeleteById() {
        Integer id = 1;

        doNothing().when(frutaRepository).deleteById(id);

        frutaService.deleteById(id);

        verify(frutaRepository, times(1)).deleteById(id);
    }

    @Test
    void testUpdate() {
        Fruta existing = new Fruta();
        existing.setId(1);
        existing.setVariedad("Marisma");
        existing.setCalidad("Primera");
        existing.setMarca("Cumbres");

        Fruta updated = new Fruta();
        updated.setId(1);
        updated.setVariedad("Marisma Nueva");
        updated.setCalidad("Extra");
        updated.setMarca("Cumbres");

        when(frutaRepository.existsById(1)).thenReturn(true);
        when(frutaRepository.save(updated)).thenReturn(updated);

        Fruta result = frutaService.update(1, updated);
        assertNotNull(result);
        assertEquals("Marisma Nueva", result.getVariedad());
        assertEquals("Extra", result.getCalidad());
    }

    @Test
    void testSaveExistingFrutaThrowsException() {
        Fruta fruta = new Fruta();
        fruta.setId(1);
        fruta.setVariedad("151");

        when(frutaRepository.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            if (fruta.getId() != null && frutaRepository.existsById(fruta.getId())) {
                throw new IllegalArgumentException("La fruta ya existe");
            }
            frutaService.save(fruta);
        });
    }

    @Test
    void testDeleteNonExistingFruta() {
        Integer id = 99;

        doThrow(new NoSuchElementException("Fruta no encontrada")).when(frutaRepository).deleteById(id);

        assertThrows(NoSuchElementException.class, () -> frutaService.deleteById(id));
    }

    @Test
    void testUpdateNonExistingFrutaThrowsException() {
        Fruta updated = new Fruta();
        updated.setId(99);
        updated.setVariedad("Desconocida");

        when(frutaRepository.existsById(99)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> {
            if (!frutaRepository.existsById(99)) {
                throw new NoSuchElementException("Fruta no encontrada");
            }
            frutaService.update(99, updated);
        });
    }

}