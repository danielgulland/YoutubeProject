package app.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailUtilsTest {

   private static final String SENDER = "sender@test.com";
   private static final String PERSONAL = "sender";
   private static final String RECIPIENT = "recipient@test.com";
   private static final String SUBJECT = "subject";
   private static final String CONTENT = "content";

   @Mock
   private Session session;

   @Mock
   private Transport transport;

   @Captor
   private ArgumentCaptor<Message> messageArgumentCaptor;

   private EmailUtils emailUtils;

   @Before
   public void setup() throws UnsupportedEncodingException, AddressException {
      emailUtils = new EmailUtils(new InternetAddress(SENDER, PERSONAL), session);
   }

   @Test
   public void testSendEmail_successful() throws MessagingException, IOException {
      // Arrange
      when(session.getTransport("smtp")).thenReturn(transport);
      when(session.getProperties()).thenReturn(new Properties());

      // Act
      emailUtils.sendEmail(RECIPIENT, SUBJECT, CONTENT);

      // Assert
      verify(transport).connect();
      verify(transport).sendMessage(messageArgumentCaptor.capture(), any(Address[].class));
      verify(transport).close();
      verifyNoMoreInteractions(transport);

      final Message message = messageArgumentCaptor.getValue();
      Assert.assertEquals(SENDER, ((InternetAddress) message.getFrom()[0]).getAddress());
      Assert.assertEquals(RECIPIENT, ((InternetAddress) message.getAllRecipients()[0]).getAddress());
      Assert.assertEquals(SUBJECT, message.getSubject());
      Assert.assertEquals(CONTENT, message.getContent());
   }

   @Test
   public void testIsEmailValid_validEmail() {
      Assert.assertTrue(EmailUtils.isEmailValid(SENDER));
   }

   @Test
   public void testIsEmailValid_invalidEmail() {
      Assert.assertFalse(EmailUtils.isEmailValid("bademail"));
   }
}
