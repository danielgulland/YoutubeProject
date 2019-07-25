package app.request;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UpdateRoomData {
   private String name;
   private Integer playlistId;
   @JsonProperty
   private Boolean isPrivate;
}
