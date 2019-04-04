package app.exception;

import app.validation.ValidationError;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

   private String field;
   private ValidationError error;

   /**
    * ApiException Constructor for message and error.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    */
   public ApiException(final String message, final ValidationError error) {
      super(message);
      this.error = error;
   }

   /**
    * ApiException Constructor for message, error and field.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    * @param field the field that caused the problem
    */
   public ApiException(final String message, final ValidationError error, final String field) {
      super(message);
      this.error = error;
      this.field = field;
   }

   /**
    * ApiException Constructor for all fields.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    * @param field the field that caused the problem
    * @param cause related exception that caused the exception
    */
   public ApiException(final String message, final ValidationError error, final String field, final Throwable cause) {
      super(message, cause);
      this.error = error;
      this.field = field;
   }
}
