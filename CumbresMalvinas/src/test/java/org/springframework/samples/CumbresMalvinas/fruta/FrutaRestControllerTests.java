package org.springframework.samples.CumbresMalvinas.fruta;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.CumbresMalvinas.components.fruta.Fruta;
import org.springframework.samples.CumbresMalvinas.components.fruta.FrutaService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FrutaRestControllerTests {

    private static final String BASE_URL = "/api/v1/frutas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FrutaService frutaService;

    private Fruta f1;
    private Fruta f2;

    @BeforeEach
    void setup() {
        f1 = new Fruta();
        f1.setVariedad("Fresa");
        f1.setCalidad("Alta");
        f1.setMarca("MarcaX");

        f2 = new Fruta();
        f2.setVariedad("Arándano");
        f2.setCalidad("Media");
        f2.setMarca("MarcaY");

        when(frutaService.findAll()).thenReturn(Arrays.asList(f1, f2));
        when(frutaService.findById(1)).thenReturn(Optional.of(f1));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldGetAllFrutas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].variedad").value("Fresa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].variedad").value("Arándano"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldGetFrutaById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variedad").value("Fresa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.calidad").value("Alta"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnNotFoundWhenFrutaDoesNotExist() throws Exception {
        when(frutaService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", 999))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldCreateFruta() throws Exception {
        Fruta newFruta = new Fruta();
        newFruta.setVariedad("Manzana");
        newFruta.setCalidad("Alta");
        newFruta.setMarca("MarcaZ");

        when(frutaService.save(any(Fruta.class))).thenReturn(newFruta);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFruta)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variedad").value("Manzana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.marca").value("MarcaZ"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldUpdateFruta() throws Exception {
        Fruta updatedFruta = new Fruta();
        updatedFruta.setVariedad("Fresa");
        updatedFruta.setCalidad("Alta");
        updatedFruta.setMarca("MarcaA");

        when(frutaService.findById(1)).thenReturn(Optional.of(f1));
        when(frutaService.save(any(Fruta.class))).thenReturn(updatedFruta);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFruta)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.marca").value("MarcaA"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnNotFoundWhenUpdatingNonExistingFruta() throws Exception {
        Fruta fruta = new Fruta();
        fruta.setVariedad("Fresa");
        fruta.setCalidad("Alta");
        fruta.setMarca("MarcaX");

        when(frutaService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fruta)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldDeleteFruta() throws Exception {
        when(frutaService.findById(1)).thenReturn(Optional.of(f1));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(frutaService, times(1)).deleteById(1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void shouldReturnNotFoundWhenDeletingNonExistingFruta() throws Exception {
        when(frutaService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", 999))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}