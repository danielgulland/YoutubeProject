package app.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ValidationError {
   BAD_VALUE("badValue", HttpStatus.BAD_REQUEST),

   NOT_FOUND("notFound", HttpStatus.NOT_FOUND),

   MISSING_FIELD("missingField", HttpStatus.BAD_REQUEST),

   INTERNAL_SERVER_ERROR("internalServerError", HttpStatus.INTERNAL_SERVER_ERROR),

   DUPLICATE_VALUE("duplicateValue", HttpStatus.BAD_REQUEST);

   private final String tag;
   private final HttpStatus status;
}
