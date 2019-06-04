package app.util;

import java.io.UnsupportedEncodingException;

import javax.annotation.Nullable;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {

   private final InternetAddress sender;
   private final Session session;

   /**
    * Constructor for all arguments.
    *
    * @param sender address of the email account sending emails
    * @param session provides config and authentication for sending emails
    * @throws AddressException if there was an error validating the sender
    */
   public EmailUtils(final InternetAddress sender, final Session session) throws AddressException {
      sender.validate();
      this.sender = sender;
      this.session = session;
   }

   /**
    * Send an email to the recipient making use of this instance's sender and session.
    *
    * @param recipient email address to receive the email
    * @param subject email subject
    * @param content email content
    * @throws MessagingException if there was an error trying to send the message
    * @throws UnsupportedEncodingException if there was an error setting the address personal value
    */
   public void sendEmail(final String recipient, final String subject, final String content)
         throws MessagingException, UnsupportedEncodingException {
      final Message message = new MimeMessage(session);
      message.setFrom(sender);
      message.setRecipient(Message.RecipientType.TO, getInternetAddress(recipient));
      message.setSubject(subject);
      message.setContent(content, "text/html");

      try (final Transport transport = session.getTransport("smtp")) {
         transport.connect();
         transport.sendMessage(message, message.getAllRecipients());
      }
   }

   /**
    * Check if the email is valid according to the syntax rules of RFC 822.
    *
    * @param email the email to validate
    * @return true if the email is valid, false if not
    */
   public static boolean isEmailValid(final String email) {
      try {
         getInternetAddress(email);
      } catch (AddressException | UnsupportedEncodingException e) {
         return false;
      }

      return true;
   }

   /**
    * Instantiate an InternetAddress representing an email address.
    *
    * @param email String representation of the email address
    * @return InternetAddress for the given email and optional personal name
    * @throws AddressException if there were errors trying to instantiate the InternetAddress or during validation
    * @throws UnsupportedEncodingException if personal cannot be set
    */
   public static InternetAddress getInternetAddress(final String email)
         throws UnsupportedEncodingException, AddressException {
      return getInternetAddress(email, null);
   }

   /**
    * Instantiate an InternetAddress representing an email address.
    *
    * @param email String representation of the email address
    * @param personal Optional string representing the name associated with the email
    * @return InternetAddress for the given email and optional personal name
    * @throws AddressException if there were errors trying to instantiate the InternetAddress or during validation
    * @throws UnsupportedEncodingException if personal cannot be set
    */
   public static InternetAddress getInternetAddress(final String email,
                                                    @Nullable final String personal)
         throws AddressException, UnsupportedEncodingException {
      final InternetAddress address = new InternetAddress(email, true);
      address.setPersonal(personal);
      address.validate();

      return address;
   }
}
