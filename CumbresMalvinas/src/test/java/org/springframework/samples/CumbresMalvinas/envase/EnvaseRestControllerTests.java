package org.springframework.samples.CumbresMalvinas.envase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.CumbresMalvinas.components.envase.Envase;
import org.springframework.samples.CumbresMalvinas.components.envase.EnvaseService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;


import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EnvaseRestControllerTests {

    private static final String BASE_URL = "/api/v1/envases";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvaseService envaseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Envase envase1;
    private Envase envase2;

    @BeforeEach
    void setup() {
        envase1 = new Envase();
        envase1.setId(1);
        envase1.setNombre("Envase A");
        envase1.setPesoGramos(500);

        envase2 = new Envase();
        envase2.setId(2);
        envase2.setNombre("Envase B");
        envase2.setPesoGramos(1000);
    }

    @Test
    @WithMockUser
    void shouldGetAllEnvases() throws Exception {
        when(envaseService.findAll()).thenReturn(List.of(envase1, envase2));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Envase A"))
                .andExpect(jsonPath("$[1].nombre").value("Envase B"));
    }

    @Test
    @WithMockUser
    void shouldGetEnvaseById() throws Exception {
        when(envaseService.findById(1)).thenReturn(Optional.of(envase1));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Envase A"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenEnvaseDoesNotExist() throws Exception {
        when(envaseService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldCreateEnvase() throws Exception {
        when(envaseService.save(any(Envase.class))).thenReturn(envase1);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(envase1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Envase A"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldUpdateEnvase() throws Exception {
        Envase updated = new Envase();
        updated.setId(1);
        updated.setNombre("Envase A Updated");
        updated.setPesoGramos(600);

        when(envaseService.findById(1)).thenReturn(Optional.of(envase1));
        when(envaseService.save(any(Envase.class))).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Envase A Updated"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldReturnNotFoundWhenUpdatingNonExistingEnvase() throws Exception {
        when(envaseService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(envase1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldDeleteEnvase() throws Exception {
        when(envaseService.findById(1)).thenReturn(Optional.of(envase1));
        doNothing().when(envaseService).deleteById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void shouldReturnNotFoundWhenDeletingNonExistingEnvase() throws Exception {
        when(envaseService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 99))
                .andExpect(status().isNotFound());
    }   
}
