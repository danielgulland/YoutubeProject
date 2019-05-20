package app.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableMap;

public class ValidatorTest {

   private static final ValidationError ERROR = ValidationError.BAD_VALUE;
   private static final ValidationError ERROR2 = ValidationError.DUPLICATE_VALUE;
   private static final String FIELD = "test";
   private static final String FIELD2 = "test2";

   private Validator validator;

   @Before
   public void setup() {
      this.validator = new Validator(new HashMap<>());
   }

   @Test
   public void testCheck_WithNoErrors() {
      // Act
      final boolean response = validator.check(true, ERROR, FIELD);

      // Assert
      Assert.assertTrue(response);
      Assert.assertTrue(validator.getErrors().isEmpty());
      Assert.assertEquals(HttpStatus.OK, validator.getStatus());
   }

   @Test
   public void testCheck_WithErrors() {
      // Act
      final boolean response = validator.check(false, ERROR, FIELD);
      final Map<String, List<String>> errors = validator.getErrors();

      // Assert
      Assert.assertFalse(response);
      Assert.assertEquals(1, validator.getErrors().size());
      Assert.assertTrue(errors.containsKey(ERROR.getTag()));
      Assert.assertTrue(errors.get(ERROR.getTag()).contains(FIELD));
      Assert.assertEquals(ERROR.getStatus(), validator.getStatus());
   }

   @Test
   public void testChain_WithNoErrors() {
      // Act
      final Validator response = validator.chain(true, ERROR, FIELD)
            .chain(true, ERROR, FIELD);

      // Assert
      Assert.assertEquals(validator, response);
      Assert.assertTrue(response.getErrors().isEmpty());
   }

   @Test
   public void testChain_WithDuplicateErrorsAndNullField() {
      // Act
      final Validator response = validator.chain(false, ERROR, FIELD)
            .chain(false, ERROR, null);
      final Map<String, List<String>> errors = validator.getErrors();

      // Assert
      Assert.assertEquals(validator, response);
      Assert.assertEquals(1, validator.getErrors().size());
      Assert.assertTrue(errors.containsKey(ERROR.getTag()));
      Assert.assertEquals(1, errors.get(ERROR.getTag()).size());
      Assert.assertTrue(errors.get(ERROR.getTag()).contains(FIELD));
      Assert.assertEquals(ERROR.getStatus(), validator.getStatus());
   }

   @Test
   public void testChainAndCheck_withMultipleErrors() {
      // Act
      final boolean response = validator.chain(false, ERROR, FIELD)
            .chain(false, ERROR, FIELD2)
            .chain(false, ERROR2, null)
            .check(true, ERROR, null);

      final Map<String, List<String>> errors = validator.getErrors();

      // Assert
      Assert.assertFalse(response);
      Assert.assertEquals(2, validator.getErrors().size());
      Assert.assertTrue(errors.containsKey(ERROR.getTag()));
      Assert.assertTrue(errors.containsKey(ERROR2.getTag()));
      Assert.assertEquals(2, errors.get(ERROR.getTag()).size());
      Assert.assertTrue(errors.get(ERROR.getTag()).contains(FIELD));
      Assert.assertTrue(errors.get(ERROR.getTag()).contains(FIELD2));
      Assert.assertTrue(errors.get(ERROR2.getTag()).isEmpty());
      Assert.assertEquals(ERROR.getStatus(), validator.getStatus());
   }

   @Test
   public void testGetResponseEntity_WithNoErrors() {
      // Act
      final ResponseEntity response = validator.getResponseEntity();

      // Assert
      Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assert.assertNull(response.getBody());
   }

   @Test
   public void testGetResponseEntity_WithErrors() {
      // Act
      validator.check(false, ERROR, FIELD);
      final ResponseEntity response = validator.getResponseEntity();

      // Assert
      Assert.assertEquals(ERROR.getStatus(), response.getStatusCode());
      Assert.assertEquals(ImmutableMap.of("errors", validator.getErrors()), response.getBody());
   }

   @Test(expected = NullPointerException.class)
   public void testCheck_NonNull() {
      validator.check(false, null, null);
   }

   @Test(expected = NullPointerException.class)
   public void testChain_NonNull() {
      validator.chain(false, null, null);
   }
}
