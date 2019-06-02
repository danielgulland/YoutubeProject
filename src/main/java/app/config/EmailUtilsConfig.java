package app.config;

import app.util.EmailUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailUtilsConfig {

   private static final String PERSONAL = "YoutubeProject";

   @Bean("EmailUtils")
   public EmailUtils emailUtils(@Value("${email.sender}") final String sender,
                                @Value("${email.password}") final String password)
         throws UnsupportedEncodingException, AddressException {

      return new EmailUtils(EmailUtils.getInternetAddress(sender, PERSONAL), getSession(sender, password));
   }

   private Session getSession(final String sender, final String password) {
      final Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");

      return Session.getInstance(properties, new javax.mail.Authenticator() {
         @Override
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(sender, password);
         }
      });
   }
}
