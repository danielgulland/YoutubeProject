package app.exception;

import app.validation.ValidationError;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ApiExceptionTest {

   private static final String MESSAGE = "test api exception";
   private static final ValidationError ERROR = ValidationError.NOT_FOUND;
   private static final String FIELD = "test field";
   private static final List<String> FIELDS = ImmutableList.of(FIELD);

   @Test
   public void testApiException_MessageAndError() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertTrue(apiException.getFields().isEmpty());
      Assert.assertNull(apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorAndCause() {
      // Act
      final NullPointerException npe = new NullPointerException();
      final ApiException apiException = new ApiException(MESSAGE, ERROR, npe);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertTrue(apiException.getFields().isEmpty());
      Assert.assertEquals(npe, apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorAndField() {

      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR, FIELD);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertEquals(FIELDS, apiException.getFields());
      Assert.assertNull(apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorAndFields() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR, FIELDS);

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertEquals(FIELDS, apiException.getFields());
      Assert.assertNull(apiException.getCause());
   }

   @Test
   public void testApiException_MessageErrorFieldAndCause() {
      // Act
      final ApiException apiException = new ApiException(MESSAGE, ERROR, FIELDS, new Throwable(MESSAGE));

      // Assert
      Assert.assertEquals(MESSAGE, apiException.getMessage());
      Assert.assertEquals(ERROR, apiException.getError());
      Assert.assertEquals(FIELDS, apiException.getFields());
      Assert.assertNotNull(apiException.getCause());
      Assert.assertEquals(MESSAGE, apiException.getCause().getMessage());
   }

   @Test(expected = NullPointerException.class)
   public void testApiException_fieldNonNull() {
      new ApiException(null, null, (String) null);
   }

   @Test(expected = NullPointerException.class)
   public void testApiException_fieldsNonNull() {
      new ApiException(null, null, (List) null);
   }

   @Test(expected = NullPointerException.class)
   public void testApiException_fieldsNonNullWithThrowable() {
      new ApiException(null, null, null, null);
   }
}
