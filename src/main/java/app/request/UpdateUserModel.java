package app.request;

import lombok.Data;

@Data
public class UpdateUserModel {
   private String email;
   private String oldPassword;
   private String password;
   private String role;
}
