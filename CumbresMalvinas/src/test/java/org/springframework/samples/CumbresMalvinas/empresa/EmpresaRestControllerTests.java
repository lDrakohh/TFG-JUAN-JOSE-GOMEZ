package org.springframework.samples.CumbresMalvinas.empresa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa;
import org.springframework.samples.CumbresMalvinas.components.empresa.EmpresaService;
import org.springframework.samples.CumbresMalvinas.components.empresa.Empresa.Moneda;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaRestControllerTests {

    private static final String BASE_URL = "/api/v1/empresas";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Empresa empresa1;
    private Empresa empresa2;

    @BeforeEach
    void setup() {
        empresa1 = new Empresa();
        empresa1.setId(1);
        empresa1.setNombreEmpresa("Fresas SA");
        empresa1.setNombrePropietario("Carlos");
        empresa1.setApellidoPropietario("Sánchez");
        empresa1.setDireccion("Calle Mayor 123");
        empresa1.setCif("B12345678");
        empresa1.setMoneda(Moneda.EUROS);

        empresa2 = new Empresa();
        empresa2.setId(2);
        empresa2.setNombreEmpresa("ECorp");
        empresa2.setNombrePropietario("Laura");
        empresa2.setApellidoPropietario("Martínez");
        empresa2.setDireccion("Avenida Sur 45");
        empresa2.setCif("B87654321");
        empresa2.setMoneda(Moneda.LEU);
    }

    @Test
    @WithMockUser
    void shouldGetAllEmpresas() throws Exception {
        when(empresaService.findAll()).thenReturn(List.of(empresa1, empresa2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombreEmpresa").value("Fresas SA"))
                .andExpect(jsonPath("$[1].nombreEmpresa").value("ECorp"));
    }

    @Test
    @WithMockUser
    void shouldGetEmpresaById() throws Exception {
        when(empresaService.findById(1)).thenReturn(Optional.of(empresa1));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreEmpresa").value("Fresas SA"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenEmpresaDoesNotExist() throws Exception {
        when(empresaService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @AutoConfigureMockMvc
    void shouldCreateEmpresa() throws Exception {
        when(empresaService.save(any(Empresa.class))).thenReturn(empresa1);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresa1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("Fresas SA"));
    }

    @Test
    @WithMockUser(username = "admin1", roles = { "ADMIN" })
    void shouldUpdateEmpresa() throws Exception {
        Empresa updated = new Empresa();
        updated.setId(1);
        updated.setNombreEmpresa("UpdatedFresas");
        updated.setNombrePropietario("Pedro");
        updated.setApellidoPropietario("López");
        updated.setDireccion("Nueva dirección");
        updated.setCif("Z99999999");
        updated.setMoneda(Moneda.EUROS);

        when(empresaService.findById(1)).thenReturn(Optional.of(empresa1));
        when(empresaService.save(any(Empresa.class))).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEmpresa").value("UpdatedFresas"));
    }

    @Test
    @WithMockUser(username = "admin1", roles = { "ADMIN" })
    void shouldReturnNotFoundWhenUpdatingNonExistingEmpresa() throws Exception {
        when(empresaService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empresa1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin1", roles = { "ADMIN" })
    void shouldDeleteEmpresa() throws Exception {
        when(empresaService.findById(1)).thenReturn(Optional.of(empresa1));
        doNothing().when(empresaService).deleteById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin1", roles = { "ADMIN" })
    void shouldReturnNotFoundWhenDeletingNonExistingEmpresa() throws Exception {
        when(empresaService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }
}
