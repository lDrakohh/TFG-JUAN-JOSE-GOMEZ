package org.springframework.samples.CumbresMalvinas.empresa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.empresa.EmpresaRepository;
import org.springframework.samples.CumbresMalvinas.components.empresa.EmpresaService;

@RunWith(MockitoJUnitRunner.class)
public class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;

    @Before
    public void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombreEmpresa("Cumbres");
        empresa.setNombrePropietario("Juan");
        empresa.setApellidoPropietario("Pérez");
        empresa.setDireccion("Calle Falsa 123");
        empresa.setCif("B12345678");
        empresa.setMoneda(Empresa.Moneda.EUROS);
    }

    @Test
    public void testFindAll() {
        List<Empresa> empresas = Arrays.asList(empresa, new Empresa());
        when(empresaRepository.findAll()).thenReturn(empresas);

        List<Empresa> result = empresaService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(empresaRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        when(empresaRepository.findById(1)).thenReturn(Optional.of(empresa));

        Optional<Empresa> result = empresaService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Cumbres", result.get().getNombreEmpresa());
        verify(empresaRepository, times(1)).findById(1);
    }

    @Test
    public void testSave() {
        when(empresaRepository.save(empresa)).thenReturn(empresa);

        Empresa saved = empresaService.save(empresa);

        assertNotNull(saved);
        assertEquals("Cumbres", saved.getNombreEmpresa());
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    public void testDeleteById() {
        empresaService.deleteById(1);

        verify(empresaRepository, times(1)).deleteById(1);
    }
}
