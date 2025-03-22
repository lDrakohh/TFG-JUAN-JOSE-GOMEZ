package org.springframework.samples.CumbresMalvinas.components.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private static final String REMITENTE = "sevillatarde34@gmail.com";
    
    @Autowired
    private JavaMailSender mailSender;

    public void enviarHistoricoPorCorreo(String destinatario, String asunto, String contenido) {
        try {
            if (destinatario == null || destinatario.isEmpty()) {
                throw new IllegalArgumentException("El destinatario no puede estar vacío.");
            }
            if (asunto == null || asunto.isEmpty()) {
                throw new IllegalArgumentException("El asunto no puede estar vacío.");
            }
            if (contenido == null || contenido.isEmpty()) {
                throw new IllegalArgumentException("El contenido no puede estar vacío.");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(REMITENTE);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true); // true → para HTML

            mailSender.send(message);
            logger.info("Correo enviado correctamente a {}", destinatario);

        } catch (MessagingException e) {
            logger.error("Error al enviar correo: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos en el email: {}", e.getMessage());
        }
    }
}
