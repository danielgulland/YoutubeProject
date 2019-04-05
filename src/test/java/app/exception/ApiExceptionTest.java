package app.exception;

import app.validation.ValidationError;

import org.junit.Assert;
import org.junit.Test;

public class ApiExceptionTest {

   private static final String MESSAGE = "test api exception";
   private static final ValidationError ERROR = ValidationError.NOT_FOUND;
   private static final String FIELD = "test field";

   @Test
   public void testApiException_MessageAndError() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertNull(apiException.getField());
      Assert.assertNull(apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorAndField() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR, FIELD);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertEquals(FIELD, apiException.getField());
      Assert.assertNull(apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorFieldAndCause() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR, FIELD, new Throwable(MESSAGE));

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertEquals(FIELD, apiException.getField());
      Assert.assertNotNull(apiException.getCause());
      Assert.assertEquals(MESSAGE, apiException.getCause().getMessage());
   }
}
