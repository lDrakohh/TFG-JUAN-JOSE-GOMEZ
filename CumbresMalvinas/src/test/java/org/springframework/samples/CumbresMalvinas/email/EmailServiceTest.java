package org.springframework.samples.CumbresMalvinas.email;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.CumbresMalvinas.components.email.EmailService;

import jakarta.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Before
    public void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    public void testEnviarHistoricoPorCorreo_Success() throws Exception {
        String destinatario = "test@example.com";
        String asunto = "Prueba";
        String contenido = "<p>Correo de prueba</p>";

        emailService.enviarHistoricoPorCorreo(destinatario, asunto, contenido);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    public void testEnviarHistoricoPorCorreo_DestinatarioVacio() {
        emailService.enviarHistoricoPorCorreo("", "Asunto", "Contenido");
    
        // Verificamos que nunca se intente enviar el correo
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testEnviarHistoricoPorCorreo_AsuntoVacio() {
        emailService.enviarHistoricoPorCorreo("test@example.com", "", "Contenido");
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    public void testEnviarHistoricoPorCorreo_ContenidoVacio() {
        emailService.enviarHistoricoPorCorreo("test@example.com", "Asunto", "");
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}

