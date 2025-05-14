package org.springframework.samples.CumbresMalvinas.registro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercancia;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaRepository;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaService;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class RegistroServiceTest {

    @Autowired
    private RegistroMercanciaService registroMercanciaService;

    @MockBean
    private RegistroMercanciaRepository registroMercanciaRepository;

    @MockBean
    private PrevisionService previsionService;

    @Test
    public void testRegistrarRegistro() {

        Empresa empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombreEmpresa("Test Empresa");

        Fruta fruta = new Fruta();
        fruta.setId(1);
        fruta.setVariedad("Test Variedad");
        fruta.setCalidad("Alta");

        Prevision prevision = new Prevision();
        prevision.setId(1);
        prevision.setEmpresa(empresa);
        prevision.setFruta(fruta);
        prevision.setPrevTraidas(50);
        prevision.setPrevFaltantes(100);
        prevision.setPrevisto(150);

        Integer cantidadTraida = 50;

        Mockito.when(previsionService.save(Mockito.any(Prevision.class))).thenReturn(prevision);

        RegistroMercancia mockRegistro = new RegistroMercancia();
        mockRegistro.setPrevision(prevision);
        mockRegistro.setCantidadTraida(cantidadTraida);
        mockRegistro.setFecha(LocalDate.now());

        Mockito.when(registroMercanciaRepository.save(Mockito.any(RegistroMercancia.class)))
                .thenReturn(mockRegistro);

        RegistroMercancia result = registroMercanciaService.registrarRegistro(prevision, cantidadTraida);

        assertNotNull(result);
        assertEquals(prevision, result.getPrevision());
        assertEquals(cantidadTraida, result.getCantidadTraida());
        assertEquals(LocalDate.now(), result.getFecha());

        // Verificamos que la previsión se haya actualizado
        assertEquals(100, prevision.getPrevTraidas());
        assertEquals(50, prevision.getPrevFaltantes());

        Mockito.verify(previsionService, Mockito.times(1)).save(Mockito.any(Prevision.class));

        Mockito.verify(registroMercanciaRepository, Mockito.times(1)).save(Mockito.any(RegistroMercancia.class));
    }

    @Test
    public void testFindByPrevisionId() {
        Prevision prevision = new Prevision();
        prevision.setId(1);

        RegistroMercancia registro1 = new RegistroMercancia();
        registro1.setPrevision(prevision);
        registro1.setCantidadTraida(50);
        registro1.setFecha(LocalDate.now());

        RegistroMercancia registro2 = new RegistroMercancia();
        registro2.setPrevision(prevision);
        registro2.setCantidadTraida(100);
        registro2.setFecha(LocalDate.now());

        Mockito.when(registroMercanciaRepository.findByPrevisionId(Mockito.anyInt()))
                .thenReturn(Arrays.asList(registro1, registro2));

        List<RegistroMercancia> registros = registroMercanciaService.findByPrevisionId(1);

        assertNotNull(registros);
        assertEquals(2, registros.size());
        assertEquals(50, registros.get(0).getCantidadTraida());
        assertEquals(100, registros.get(1).getCantidadTraida());
    }

    @Test
    public void testDeleteById() {
        Integer id = 1;

        Mockito.doNothing().when(registroMercanciaRepository).deleteById(id);

        registroMercanciaService.deleteById(id);

        Mockito.verify(registroMercanciaRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testFindByEmpresaAndFecha() {
        Integer empresaId = 1;
        LocalDate fecha = LocalDate.now();

        RegistroMercancia registro1 = new RegistroMercancia();
        registro1.setPrevision(new Prevision());
        registro1.setCantidadTraida(50);
        registro1.setFecha(fecha);

        RegistroMercancia registro2 = new RegistroMercancia();
        registro2.setPrevision(new Prevision());
        registro2.setCantidadTraida(100);
        registro2.setFecha(fecha);

        Mockito.when(registroMercanciaRepository.findByEmpresaAndFecha(empresaId, fecha))
                .thenReturn(Arrays.asList(registro1, registro2));

        List<RegistroMercancia> registros = registroMercanciaService.findByEmpresaAndFecha(empresaId, fecha);

        assertNotNull(registros);
        assertEquals(2, registros.size());
    }

    @Test
    public void testFindById() {
        Integer id = 1;

        RegistroMercancia registro = new RegistroMercancia();
        registro.setId(id);

        Mockito.when(registroMercanciaRepository.findById(id))
                .thenReturn(Optional.of(registro));

        Optional<RegistroMercancia> result = registroMercanciaService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    public void testFindByEmpresaAndFechaHistoric() {
        List<Integer> empresaIds = Arrays.asList(1, 2);
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();

        RegistroMercancia registro1 = new RegistroMercancia();
        registro1.setPrevision(new Prevision());
        registro1.setCantidadTraida(50);
        registro1.setFecha(inicio);

        RegistroMercancia registro2 = new RegistroMercancia();
        registro2.setPrevision(new Prevision());
        registro2.setCantidadTraida(100);
        registro2.setFecha(fin);

        Mockito.when(registroMercanciaRepository.findByEmpresaAndFechaHistorico(empresaIds, inicio, fin))
                .thenReturn(Arrays.asList(registro1, registro2));

        List<RegistroMercancia> registros = registroMercanciaService.findByEmpresaAndFechaHistoric(empresaIds, inicio,
                fin);

        assertNotNull(registros);
        assertEquals(2, registros.size());
    }

    @Test
    public void testActualizarPrevisionAlRegistrarRegistro() {
        Prevision prevision = new Prevision();
        prevision.setId(1);
        prevision.setPrevTraidas(100);
        prevision.setPrevFaltantes(200);
        prevision.setPrevisto(300);

        Integer cantidadTraida = 50;

        Mockito.when(previsionService.save(Mockito.any(Prevision.class))).thenReturn(prevision);

        RegistroMercancia registro = new RegistroMercancia();
        registro.setPrevision(prevision);
        registro.setCantidadTraida(cantidadTraida);
        registro.setFecha(LocalDate.now());

        Mockito.when(registroMercanciaRepository.save(Mockito.any(RegistroMercancia.class)))
                .thenReturn(registro);

        registroMercanciaService.registrarRegistro(prevision, cantidadTraida);

        assertEquals(150, prevision.getPrevTraidas());
        assertEquals(150, prevision.getPrevFaltantes());
    }

    @Test
    public void testFindByIdNotFound() {
        Integer id = 99;

        Mockito.when(registroMercanciaRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<RegistroMercancia> result = registroMercanciaService.findById(id);

        assertFalse(result.isPresent());
    }

}