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

   @ExceptionHandler(ApiException.class)
   public final ResponseEntity handleApiException(final ApiException ex) {
      return validator.chain(false, ex.getError(), ex.getField()).getResponseEntity();
   }

   @ExceptionHandler(Exception.class)
   public final ResponseEntity handleServerError(final Exception ex) {
      System.err.println(ex.toString());
      return validator.chain(false, SERVER_ERROR, null).getResponseEntity();
   }
}
