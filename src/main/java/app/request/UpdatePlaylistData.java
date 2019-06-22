package app.request;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UpdatePlaylistData {
   private String name;
   @JsonProperty
   /* We're using a wrapper because if isPrivate isn't specified, it defaults to false. We can just have it set
       to null with a wrapper. */
   private Boolean isPrivate;
   private String genre;
}
