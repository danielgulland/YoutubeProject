package app.request;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UpdatePlaylistData {
   private String name;
   @JsonProperty
   private boolean isPrivate;
   private String genre;
}
