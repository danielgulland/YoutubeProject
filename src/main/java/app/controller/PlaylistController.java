package app.controller;

import app.model.Playlist;
import app.request.CreatePlaylistData;
import app.service.PlaylistService;
import app.validation.ValidationError;
import app.validation.Validator;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.GENRE;
import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.NAME;

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
