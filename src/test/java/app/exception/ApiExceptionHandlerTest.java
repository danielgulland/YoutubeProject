package app.exception;

import app.validation.ValidationError;
import app.validation.Validator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiExceptionHandlerTest {

   private static final String MESSAGE = "test";

   @Mock
   private Validator validator;

   @InjectMocks
   private ApiExceptionHandler handler;

   @Test
   public void testHandleApiException() {
      // Arrange
      final ValidationError error = ValidationError.NOT_FOUND;
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(error));

      // Act
      final ResponseEntity response = handler.handleApiException(new ApiException(MESSAGE, error));

      // Assert
      verify(validator, times(0)).check(false, ValidationError.BAD_VALUE, "email");
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);

      Assert.assertEquals(error.getStatus(), response.getStatusCode());
      Assert.assertEquals(error.getTag(), response.getBody());
   }

   @Test
   public void testHandleApiException_With_Field() {
      final ValidationError error = ValidationError.NOT_FOUND;
      final String field = "Field error";
      final ApiException ex = new ApiException(MESSAGE, error, ImmutableList.of(field));

      when(validator.check(anyBoolean(), any(ValidationError.class), nullable(String.class))).thenReturn(false);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(error));

      // Act
      final ResponseEntity response = handler.handleApiException(ex);

      // Assert
      verify(validator).check(false, error, field);
      verify(validator).getResponseEntity();
      verifyNoMoreInteractions(validator);

      Assert.assertEquals(error.getStatus(), response.getStatusCode());
      Assert.assertEquals(error.getTag(), response.getBody());
   }

   @Test
   public void testHandleServerError() {
      // Arrange
      final ValidationError error = ValidationError.INTERNAL_SERVER_ERROR;
      when(validator.chain(anyBoolean(), any(ValidationError.class), nullable(String.class))).thenReturn(validator);
      when(validator.getResponseEntity()).thenReturn(buildResponseEntity(error));

      // Act
      final ResponseEntity response = handler.handleServerError(new Exception());

      // Assert
      verify(validator).chain(false, error, null);
      verify(validator).getResponseEntity();

      Assert.assertEquals(error.getStatus(), response.getStatusCode());
      Assert.assertEquals(error.getTag(), response.getBody());
   }

   private ResponseEntity buildResponseEntity(final ValidationError error) {
      return ResponseEntity.status(error.getStatus()).body(error.getTag());
   }
}
