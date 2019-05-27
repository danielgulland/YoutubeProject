package app.exception;

import app.validation.ValidationError;
import app.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

   private static final ValidationError SERVER_ERROR = ValidationError.INTERNAL_SERVER_ERROR;

   @Autowired
   private Validator validator;

   /**
    * Handles an API exception.
    *
    * @param ex exception containing information pertaining to the error
    * @return Response with a status and body that relates to the error
    */
   @ExceptionHandler(ApiException.class)
   public final ResponseEntity handleApiException(final ApiException ex) {
      if (ex.getFields().isEmpty()) {
         validator.check(false, ex.getError(), null);
      }
      else {
         for (String field : ex.getFields()) {
            validator.check(false, ex.getError(), field);
         }
      }
      return validator.getResponseEntity();
   }

   /**
    * Handles all other exceptions.
    *
    * @param ex exception containing information pertaining to the error
    * @return Response representing internal server error
    */
   @ExceptionHandler(Exception.class)
   public final ResponseEntity handleServerError(final Exception ex) {
      System.err.println(ex.toString());
      return validator.chain(false, SERVER_ERROR, null).getResponseEntity();
   }
}
