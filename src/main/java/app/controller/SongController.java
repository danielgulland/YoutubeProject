package app.controller;

import app.model.Song;
import app.request.CreateSongData;
import app.service.SongService;
import app.validation.ValidationError;
import app.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.REFERENCE;
import static app.constant.FieldConstants.TITLE;

@RestController
@RequestMapping(path = "/songs")
public class SongController {

   @Autowired
   private SongService songService;

   @Autowired
   private Validator validator;

   /**
    * Get a Song by the song id.
    *
    * @param id song id
    * @return Response with status 200 and Song in the body for successful call, otherwise validation response
    */
   @GetMapping("/{id}")
   public ResponseEntity getSongById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         final Song song = songService.getSongById(id);

         return ResponseEntity.status(HttpStatus.OK).body(song);
      }

      return validator.getResponseEntity();
   }

   /**
    * Creates a new song given the song data.
    *
    * @param createSongData information required to create a new song
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping()
   public ResponseEntity createNewSong(@RequestBody final CreateSongData createSongData) {
      if (validator.chain(StringUtils.isNotBlank(createSongData.getTitle()), ValidationError.MISSING_FIELD, TITLE)
            .check(StringUtils.isNotBlank(createSongData.getReference()), ValidationError.MISSING_FIELD, REFERENCE)) {
         songService.createNewSong(buildSongFromCreateSongData(createSongData));

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   private Song buildSongFromCreateSongData(final CreateSongData createSongData) {
      return Song.builder()
            .title(createSongData.getTitle())
            .reference(createSongData.getReference())
            .build();
   }
}
