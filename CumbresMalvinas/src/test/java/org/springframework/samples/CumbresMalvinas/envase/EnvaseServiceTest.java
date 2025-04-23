package org.springframework.samples.CumbresMalvinas.envase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.samples.CumbresMalvinas.components.envase.Envase;
import org.springframework.samples.CumbresMalvinas.components.envase.EnvaseRepository;
import org.springframework.samples.CumbresMalvinas.components.envase.EnvaseService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnvaseServiceTest {

    @Mock
    private EnvaseRepository envaseRepository;

    @InjectMocks
    private EnvaseService envaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Envase envase1 = new Envase();
        Envase envase2 = new Envase();
        List<Envase> envases = Arrays.asList(envase1, envase2);

        when(envaseRepository.findAll()).thenReturn(envases);

        List<Envase> result = envaseService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        Envase envase = new Envase();
        envase.setId(1);
        envase.setNombre("Tarrina");
        envase.setPesoGramos(500);

        when(envaseRepository.findById(1)).thenReturn(Optional.of(envase));

        Optional<Envase> result = envaseService.findById(1);
        assertTrue(result.isPresent());
        assertEquals("Tarrina", result.get().getNombre());
        assertEquals(500, result.get().getPesoGramos());
    }

    @Test
    void testSave() {
        Envase envase = new Envase();
        envase.setNombre("Caja");
        envase.setPesoGramos(200);

        when(envaseRepository.save(envase)).thenReturn(envase);

        Envase result = envaseService.save(envase);
        assertNotNull(result);
        assertEquals("Caja", result.getNombre());
        assertEquals(200, result.getPesoGramos());
    }

    @Test
    void testDeleteById() {
        Integer id = 1;

        doNothing().when(envaseRepository).deleteById(id);

        envaseService.deleteById(id);

        verify(envaseRepository, times(1)).deleteById(id);
    }
}
