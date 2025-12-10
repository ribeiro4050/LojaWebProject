package br.com.lojaroupa.util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    // PREENCHA COM SEUS DADOS DO MAILTRAP AQUI
    private final String username = "7e7b213844aa5d";
    private final String password = "833dd8e4578e32";

    public void enviarEmail(String destinatario, String assunto, String mensagemTexto) {
        
        // 1. Configurações do servidor SMTP (Mailtrap)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io"); // Host do Mailtrap
        props.put("mail.smtp.port", "2525"); // Porta padrão do Mailtrap

        // 2. Cria a sessão com autenticação
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 3. Monta o e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("sistema@lojaroupa.com")); // Quem envia
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); // Quem recebe
            message.setSubject(assunto); // Assunto
            message.setText(mensagemTexto); // Corpo do e-mail

            // 4. Envia!
            Transport.send(message);
            System.out.println("📧 E-mail enviado com sucesso via Mailtrap!");

        } catch (MessagingException e) {
            System.err.println("❌ Erro ao enviar e-mail: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    // Teste Rápido (Main)
    public static void main(String[] args) {
        EmailService service = new EmailService();
        service.enviarEmail("admin@teste.com", "Teste Java", "Se chegou aqui, o Mailtrap funcionou!");
    }
}