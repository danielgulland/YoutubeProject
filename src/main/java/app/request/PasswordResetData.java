package app.request;

import lombok.Data;

@Data
public class PasswordResetData {
   private int userId;
   private String token;
   private String password;
}
