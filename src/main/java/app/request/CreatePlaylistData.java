package app.request;

import lombok.Data;

@Data
public class CreatePlaylistData {
   private String name;
   private int userId;
   private boolean isPrivate;
   private String genre;
}
