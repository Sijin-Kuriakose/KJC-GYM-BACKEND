package in.edu.kristujayanti.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailVerificationAndLoginCredentialsSMTP {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailVerificationAndLoginCredentials.class);

    private final Properties emailProperties;

    public EmailVerificationAndLoginCredentialsSMTP() {
        this.emailProperties = loadEmailProperties();
    }

    private Properties loadEmailProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email_config.properties")) {

            if (input == null) {
                LOGGER.error("Unable to find email_config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Error loading email properties", ex);
        }
        return properties;
    }

    public boolean sendEmail(String to, String subject, String body, boolean isVerification) {
        String from = emailProperties.getProperty("email.from");
        String password = emailProperties.getProperty("email.password");
        String host = emailProperties.getProperty("mail.smtp.host");
        int port = Integer.parseInt(emailProperties.getProperty("mail.smtp.port"));
        boolean starttlsEnabled = Boolean.parseBoolean(emailProperties.getProperty("mail.smtp.starttls.enable"));

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", starttlsEnabled);


        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            if (isVerification) {
                LOGGER.info("Verification OTP sent successfully.");
            } else {
                LOGGER.info("Email sent successfully.");
            }
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;

        }
    }
}