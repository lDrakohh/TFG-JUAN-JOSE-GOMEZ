package org.springframework.samples.CumbresMalvinas.prevision;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionDTO;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PrevisionRestControllerTests {

    private static final String BASE_URL = "/api/v1/previsiones";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Prevision prevision1;
    private Prevision prevision2;

    @MockBean
    private PrevisionService previsionService;

    @BeforeEach
    void setup() {
        prevision1 = new Prevision();
        prevision1.setId(1);
        prevision1.setPrevisto(100);
        prevision1.setPrevTraidas(90);
        prevision1.setPrevFaltantes(10);

        prevision2 = new Prevision();
        prevision2.setId(2);
        prevision2.setPrevisto(200);
        prevision2.setPrevTraidas(180);
        prevision2.setPrevFaltantes(20);

        when(previsionService.findAll()).thenReturn(Arrays.asList(prevision1, prevision2));
        when(previsionService.findById(1)).thenReturn(Optional.of(prevision1));
    }

    @Test
    void shouldGetAllPrevisiones() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void shouldGetPrevisionById() throws Exception {
        Prevision prevision = new Prevision();
        prevision.setId(1);
        prevision.setPrevisto(100);
        prevision.setPrevTraidas(90);
        prevision.setPrevFaltantes(10);

        when(previsionService.findById(1)).thenReturn(Optional.of(prevision));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.previsto").value(100));
    }

    @Test
    void shouldReturnNotFoundWhenPrevisionDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreatePrevision() throws Exception {
        PrevisionDTO previsionDTO = new PrevisionDTO();
        previsionDTO.setEmpresaId(1);
        previsionDTO.setFrutaId(1);
        previsionDTO.setPrevisto(300);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(previsionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previsto").value(300));
    }

    @Test
    void shouldReturnBadRequestWhenMissingFields() throws Exception {
        PrevisionDTO previsionDTO = new PrevisionDTO();
        previsionDTO.setFrutaId(1);
        previsionDTO.setPrevisto(200);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(previsionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El campo empresaId es obligatorio."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingPrevision() throws Exception {
        Prevision updatedPrevision = new Prevision();
        updatedPrevision.setPrevisto(250);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPrevision)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePrevision() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingPrevision() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }
}
