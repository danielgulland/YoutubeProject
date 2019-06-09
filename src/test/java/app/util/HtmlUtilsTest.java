package app.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
public class HtmlUtilsTest {

   @Mock
   private SpringTemplateEngine templateEngine;

   @Captor
   private ArgumentCaptor<Context> contextArgumentCaptor;

   @PrepareForTest({HtmlUtils.class})
   @Test
   public void testGenerateHtmlContent() throws Exception {
      // Arrange
      whenNew(SpringTemplateEngine.class).withNoArguments().thenReturn(templateEngine);
      when(templateEngine.process(anyString(), any(Context.class))).thenReturn("htmlcontent");

      // Act
      final String response = HtmlUtils.generateHtmlContent("resetpassword", new HashMap<>());

      // Assert
      verify(templateEngine).process(anyString(), contextArgumentCaptor.capture());
      Assert.assertEquals(Locale.getDefault(), contextArgumentCaptor.getValue().getLocale());
      Assert.assertEquals("htmlcontent", response);
   }

   @Test
   public void testConstructorIsPrivate() throws Exception {
      final Constructor<HtmlUtils> constructor = HtmlUtils.class.getDeclaredConstructor();
      Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      constructor.newInstance();
   }
}
