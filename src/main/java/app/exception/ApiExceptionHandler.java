package app.exception;

import app.validation.ValidationError;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

   private static final ValidationError SERVER_ERROR = ValidationError.INTERNAL_SERVER_ERROR;

   @ExceptionHandler(ApiException.class)
   public final ResponseEntity handleApiException(final ApiException ex) {
      final ValidationError error = ex.getError();
      return ResponseEntity.status(error.getStatus()).body(buildErrorBody(error.getTag(), ex.getField()));
   }

   @ExceptionHandler(Exception.class)
   public final ResponseEntity handleServerError(final Exception ex) {
      System.out.println(ex.toString());
      return ResponseEntity.status(SERVER_ERROR.getStatus()).body(buildErrorBody(SERVER_ERROR.getTag(), null));
   }

   private Map<String, String> buildErrorBody(final String tag, final String field) {
      Map<String, String> errorBody = new HashMap<>();
      errorBody.put(tag, field);
      return errorBody;
   }
}
