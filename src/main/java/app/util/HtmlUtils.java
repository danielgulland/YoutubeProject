package app.util;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class HtmlUtils {

   private static final SpringTemplateEngine templateEngine;

   /**
    * Prevent instantiating this class.
    */
   private HtmlUtils() {
   }

   /*
    * Initialize the template engine and the resolver it uses.
    */
   static {
      final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
      templateResolver.setPrefix("/templates/");
      templateResolver.setSuffix(".html");
      templateResolver.setTemplateMode(TemplateMode.HTML);
      templateResolver.setCharacterEncoding("UTF-8");
      templateResolver.setCacheable(false);

      templateEngine = new SpringTemplateEngine();
      templateEngine.setTemplateResolver(templateResolver);
   }

   /**
    * Process the template and context and generate an HTML string.
    *
    * @param template HTML file in resouces/templates to process
    * @param variables map containing variables used for setting values in the HTML template
    * @return String representation of the processed HTML template
    */
   public static String generateHtmlContent(final String template, final Map<String, Object> variables) {
      return templateEngine.process(template, new Context(Locale.getDefault(), variables));
   }
}
