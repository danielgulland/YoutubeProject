package app.exception;

import app.validation.ValidationError;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

@Getter
public class ApiException extends RuntimeException {

   private ValidationError error;
   private List<String> fields;

   /**
    * ApiException Constructor for message and error.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    */
   public ApiException(final String message, final ValidationError error) {
      super(message);
      this.error = error;
      this.fields = Collections.emptyList();
   }

   /**
    * ApiException Constructor for message, error and field.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    * @param field the field that caused the problem
    */
   public ApiException(final String message, final ValidationError error, @NonNull final String field) {
      super(message);
      this.error = error;
      this.fields = ImmutableList.of(field);
   }

   /**
    * ApiException Constructor for message, error and fields.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    * @param fields the fields that caused the problem
    */
   public ApiException(final String message, final ValidationError error, @NonNull final List<String> fields) {
      super(message);
      this.error = error;
      this.fields = fields;
   }

   /**
    * ApiException Constructor for all fields.
    *
    * @param message the detail message
    * @param error ValidationError signifying the reason for the exception
    * @param fields the fields that caused the problem
    * @param cause related exception that caused the exception
    */
   public ApiException(final String message, final ValidationError error, @NonNull final List<String> fields,
                       final Throwable cause) {
      super(message, cause);
      this.error = error;
      this.fields = fields;
   }
}
