package app.request;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CreatePlaylistData {
   private String name;
   private int userId;
   @JsonProperty
   private boolean isPrivate;
   private String genre;
}
