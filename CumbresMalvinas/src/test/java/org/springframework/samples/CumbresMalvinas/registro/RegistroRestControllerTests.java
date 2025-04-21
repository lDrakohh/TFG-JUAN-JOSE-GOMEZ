package org.springframework.samples.CumbresMalvinas.registro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.CumbresMalvinas.components.prevision.Prevision;
import org.springframework.samples.CumbresMalvinas.components.prevision.PrevisionService;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercancia;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaController;
import org.springframework.samples.CumbresMalvinas.components.registro.RegistroMercanciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegistroRestControllerTests {

    private static final String BASE_URL = "/api/v1/registros";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistroMercanciaService registroService;

    @MockBean
    private PrevisionService previsionService;

    private RegistroMercancia r1;
    private RegistroMercancia r2;
    private Prevision prevision;

    @BeforeEach
    void setup() {
        prevision = new Prevision();
        prevision.setId(1);
        prevision.setPrevisto(100);
        prevision.setPrevTraidas(50);
        prevision.setPrevFaltantes(50);

        r1 = new RegistroMercancia();
        r1.setId(1);
        r1.setCantidadTraida(20);
        r1.setPrevision(prevision);

        r2 = new RegistroMercancia();
        r2.setId(2);
        r2.setCantidadTraida(30);
        r2.setPrevision(prevision);
    }

    @Test
    void shouldGetRegistrosDeHoyPorEmpresa() throws Exception {
        List<RegistroMercancia> registros = Arrays.asList(r1, r2);
        when(registroService.findByEmpresaAndFecha(anyInt(), eq(LocalDate.now()))).thenReturn(registros);

        mockMvc.perform(get(BASE_URL + "/empresa/{empresaId}/hoy", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetRegistrosPorPrevision() throws Exception {
        when(registroService.findByPrevisionId(1)).thenReturn(Arrays.asList(r1, r2));

        mockMvc.perform(get(BASE_URL + "/prevision/{previsionId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnNoContentWhenNoRegistrosForPrevision() throws Exception {
        when(registroService.findByPrevisionId(1)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/prevision/{previsionId}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRegisterRegistro() throws Exception {
        RegistroMercancia newRegistro = new RegistroMercancia();
        newRegistro.setId(3);
        newRegistro.setCantidadTraida(10);
        newRegistro.setPrevision(prevision);

        RegistroMercanciaController.RegistroRequest request = new RegistroMercanciaController.RegistroRequest();
        request.setCantidadTraida(10);

        when(previsionService.findById(1)).thenReturn(Optional.of(prevision));
        when(registroService.registrarRegistro(prevision, 10)).thenReturn(newRegistro);

        mockMvc.perform(post(BASE_URL + "/{previsionId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.cantidadTraida").value(10));
    }

    @Test
    void shouldReturnNotFoundWhenRegisteringWithInvalidPrevisionId() throws Exception {
        RegistroMercanciaController.RegistroRequest request = new RegistroMercanciaController.RegistroRequest();
        request.setCantidadTraida(5);

        when(previsionService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post(BASE_URL + "/{previsionId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteRegistro() throws Exception {
        when(registroService.findById(1)).thenReturn(Optional.of(r1));

        mockMvc.perform(delete(BASE_URL + "/{id}", 1))
                .andExpect(status().isNoContent());

        verify(registroService, times(1)).deleteById(1);
        verify(previsionService, times(1)).save(any(Prevision.class));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingRegistro() throws Exception {
        when(registroService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(delete(BASE_URL + "/{id}", 999))
                .andExpect(status().isNotFound());
    }    
}
