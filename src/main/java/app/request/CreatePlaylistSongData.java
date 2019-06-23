package app.request;

import lombok.Data;

@Data
public class CreatePlaylistSongData {
   private int playlistId;
   private int songId;
}
