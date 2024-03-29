package app.controller;

import app.model.Playlist;
import app.request.CreatePlaylistData;
import app.request.UpdatePlaylistData;
import app.service.PlaylistService;
import app.validation.ValidationError;
import app.validation.Validator;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.GENRE;
import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.NAME;
import static app.constant.FieldConstants.PLAYLIST_ID;
import static app.constant.FieldConstants.PLAYLIST_SONG_ID;
import static app.constant.FieldConstants.SONG_ID;

@RestController
@RequestMapping(path = "/playlists")
public class PlaylistController {

   @Autowired
   private PlaylistService playlistService;

   @Autowired
   private Validator validator;

   /**
    * Create a new Playlist given the playlist data.
    *
    * @param createPlaylistData information required to create a new playlist
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping()
   public ResponseEntity createNewPlaylist(@RequestBody final CreatePlaylistData createPlaylistData) {
      if (validator.chain(createPlaylistData.getUserId() > 0, ValidationError.BAD_VALUE, ID)
            .chain(StringUtils.isNotBlank(createPlaylistData.getName()), ValidationError.MISSING_FIELD, NAME)
            .check(StringUtils.isNotBlank(createPlaylistData.getGenre()), ValidationError.MISSING_FIELD, GENRE)) {
         playlistService.createNewPlaylist(buildFromCreatePlaylistData(createPlaylistData));

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }


   /**
    * Get a list of playlists by the name or genre, if name and genre are empty return all songs.
    * We're defaulting to "%" for the case of getting all playlists. So, it default returns a list of all playlists
    *    until a specific filter is set for name or genre.
    *
    * @param name name used to search for playlists
    * @param genre genre used to search for playlists
    * @return Response with status 200 and Playlist in the body for successful call
    */
   @GetMapping()
   public ResponseEntity getPlaylists(@RequestParam(required = false, defaultValue = "%") final String name,
                                      @RequestParam(required = false, defaultValue = "%") final String genre) {
      return ResponseEntity.status(HttpStatus.OK).body(playlistService.getPlaylistsByFilter(name, genre));
   }

   /**
    * Add an existing song to an existing playlist.
    *
    * @param songId song's id
    * @param playlistId playlist's id
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping("/{playlistId}/songs/{songId}")
   public ResponseEntity addSongToPlaylist(@PathVariable final int songId, @PathVariable final int playlistId) {
      if (validator.chain(songId > 0, ValidationError.BAD_VALUE, SONG_ID)
            .check(playlistId > 0, ValidationError.BAD_VALUE, PLAYLIST_ID)) {
         playlistService.addSongToPlaylist(songId, playlistId);

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   /**
    * Get a playlist by the playlist's id.
    *
    * @param id playlist's id
    * @return Response with status 200 and playlist in body for successful call, otherwise validation response
    */
   @GetMapping("/{id}")
   public ResponseEntity getPlaylistById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         return ResponseEntity.status(HttpStatus.OK).body(playlistService.getPlaylistById(id));
      }

      return validator.getResponseEntity();
   }

   /**
    * Gets all of the songs in a playlist.
    *
    * @param id playlist's id
    * @return Response with status 200 and list of songs in body for successful call, otherwise validation response
    */
   @GetMapping("/{id}/songs")
   public ResponseEntity getSongsInPlaylist(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, PLAYLIST_ID)) {
         return ResponseEntity.status(HttpStatus.OK).body(playlistService.getSongsInPlaylist(id));
      }

      return validator.getResponseEntity();
   }

   /**
    * Updates an existing playlist given the information.
    *
    * @param id playlist id
    * @param updatePlaylistData contains information to update a playlist
    * @return Response with status 200 and null in the body for successful call, otherwise validation response
    */
   @PutMapping("/{id}")
   public ResponseEntity updatePlaylistById(@PathVariable final int id,
                                            @RequestBody final UpdatePlaylistData updatePlaylistData) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         playlistService.updatePlaylistById(id, updatePlaylistData);
         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   /**
    * Deletes a playlist by the playlist's id.
    *
    * @param id playlist's id
    * @return Response with status 200 and null in the body for successful call, otherwise validation response
    */
   @DeleteMapping("/{id}")
   public ResponseEntity deletePlaylist(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         playlistService.deletePlaylist(id);
         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   /**
    *  Deletes a song in a specific playlist.
    *
    * @param playlistSongId playlist_song id
    * @return Response with status 200 and null in the body for successful call, otherwise validation response
    */
   @DeleteMapping("/songs/{playlistSongId}")
   public ResponseEntity deleteSongInPlaylist(@PathVariable final int playlistSongId) {
      if (validator.check(playlistSongId > 0, ValidationError.BAD_VALUE, PLAYLIST_SONG_ID)) {
         playlistService.deleteSongInPlaylist(playlistSongId);

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   private Playlist buildFromCreatePlaylistData(final CreatePlaylistData createPlaylistData) {
      return Playlist.builder()
            .name(createPlaylistData.getName())
            .userId(createPlaylistData.getUserId())
            .isPrivate(createPlaylistData.isPrivate())
            .genre(createPlaylistData.getGenre())
            .dateCreated(ZonedDateTime.now())
            .build();
   }
}
