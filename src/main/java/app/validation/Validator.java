package app.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class Validator {

   private final Map<String, String> errors;
   private HttpStatus status = HttpStatus.OK;

   /**
    * Evaluate the expression and immediately return the validation results.
    *
    * @param expression the expression to evaluate
    * @param error validation error to use if expression is false
    * @param field the field to append to the error
    * @return true if the Validator has errors, false if there are no errors
    */
   public boolean check(final boolean expression, final ValidationError error, final String field) {
      if (!expression) {
         addError(error, field);
      }

      return errors.isEmpty();
   }

   /**
    * Evaluate the expression and return the Validator to chain into another validation call.
    *
    * @param expression the expression to evaluate
    * @param error validation error to use if expression is false
    * @param field the field to append to the error
    * @return this Validator
    */
   public Validator chain(final boolean expression, final ValidationError error, final String field) {
      if (!expression) {
         addError(error, field);
      }

      return this;
   }

   /**
    * Get the ResponseEntity based on the Validator.
    *
    * @return ResponseEntity with the status and body based on the Validator
    */
   public ResponseEntity getResponseEntity() {
      return ResponseEntity.status(status).body(errors.isEmpty() ? null : errors);
   }

   /**
    * Add the error to the Validator and set the response status.
    *
    * @param error validation error containing tag and status
    * @param field the field to append to the error
    */
   private void addError(final ValidationError error, final String field) {
      errors.put(error.getTag(), field);
      status = error.getStatus();
   }
}
