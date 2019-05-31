package app;

import java.time.ZoneOffset;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
   public static void main(final String[] args) {
      TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
      SpringApplication.run(Application.class, args);
   }
}
