package org.springframework.samples.CumbresMalvinas.prevision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.envase.Envase;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionRepository;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrevisionServiceTest {

    @Mock
    private PrevisionRepository previsionRepository;

    @InjectMocks
    private PrevisionService previsionService;

    private Empresa empresa;
    private Fruta fruta;
    private Prevision prevision;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        empresa = new Empresa();
        empresa.setId(1);
        
        fruta = new Fruta();
        fruta.setId(1);
        
        prevision = new Prevision();
        prevision.setId(1);
        prevision.setEmpresa(empresa);
        prevision.setFruta(fruta);
        prevision.setPrevisto(100);
        prevision.setPrevTraidas(50);
        prevision.setPrevFaltantes(50);
        prevision.setFecha(LocalDate.now());
    }

    @Test
    void testFindAll() {
        List<Prevision> previsiones = Arrays.asList(prevision);

        when(previsionRepository.findAll()).thenReturn(previsiones);

        List<Prevision> result = previsionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(prevision.getId(), result.get(0).getId());
    }

    @Test
    void testFindById() {
        when(previsionRepository.findById(1)).thenReturn(Optional.of(prevision));

        Optional<Prevision> result = previsionService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(prevision.getId(), result.get().getId());
    }

    @Test
    void testSave() {
        when(previsionRepository.save(prevision)).thenReturn(prevision);

        Prevision result = previsionService.save(prevision);

        assertNotNull(result);
        assertEquals(prevision.getId(), result.getId());
    }

    @Test
    void testDeleteById() {
        Integer id = 1;

        doNothing().when(previsionRepository).deleteById(id);
        previsionService.deleteById(id);

        verify(previsionRepository, times(1)).deleteById(id);
    }

    @Test
    void testFindByFecha() {
        LocalDate fecha = LocalDate.now();
        List<Prevision> previsiones = Arrays.asList(prevision);

        when(previsionRepository.findByFecha(fecha)).thenReturn(previsiones);

        List<Prevision> result = previsionService.findByFecha(fecha);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByEmpresaAndFecha() {
        LocalDate fecha = LocalDate.now();
        List<Prevision> previsiones = Arrays.asList(prevision);

        when(previsionRepository.findByEmpresaAndFecha(empresa, fecha)).thenReturn(previsiones);

        List<Prevision> result = previsionService.findByEmpresaAndFecha(empresa, fecha);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByEmpresaAndFechaBetween() {
        LocalDate inicio = LocalDate.now().minusDays(1);
        LocalDate fin = LocalDate.now().plusDays(1);
        List<Prevision> previsiones = Arrays.asList(prevision);

        when(previsionRepository.findByEmpresaAndFechaBetween(Collections.singletonList(empresa.getId()), inicio, fin)).thenReturn(previsiones);

        List<Prevision> result = previsionService.findByEmpresaAndFechaBetween(Collections.singletonList(empresa.getId()), inicio, fin);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testActualizarPrevision() {
        prevision.actualizarPrevision(30);

        assertEquals(80, prevision.getPrevTraidas());
        assertEquals(20, prevision.getPrevFaltantes());
    }

    @Test
    void testGetTipo() {
        Envase envase = new Envase();
        envase.setNombre("Caja");
        fruta.setCalidad("Primera");
        fruta.setEnvase(envase);

        String tipo = prevision.getTipo();

        assertNotNull(tipo);
        assertEquals("Primera - Caja", tipo);
    }

    @Test
    void testGetTipoSinEnvase() {
        fruta.setEnvase(null);

        String tipo = prevision.getTipo();

        assertNotNull(tipo);
        assertEquals("Tipo no definido", tipo); 
    }
}
