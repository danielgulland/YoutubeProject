package app;

import app.model.PasswordReset;
import app.model.Playlist;
import app.model.PlaylistSong;
import app.model.Room;
import app.model.Song;
import app.model.User;
import app.request.CreatePlaylistData;
import app.request.CreateRoomData;
import app.request.CreateSongData;
import app.request.PasswordResetData;
import app.request.RegistrationData;
import app.request.UpdatePlaylistData;
import app.request.UpdateRoomData;
import app.request.UpdateUserData;
import app.validation.ValidationError;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableList;

public class BaseTest {
   
   /* Constants */
   protected static final int VALID_ID = 1;
   protected static final int INVALID_ID = 0;
   protected static final String ID_FIELD = "id";

   // User
   protected static final String USERNAME = "test";
   protected static final String INVALID_USERNAME = " ";
   protected static final String EMAIL = "test@test.com";
   protected static final String INVALID_EMAIL = "badEmail.com";
   protected static final String PASSWORD = "password";
   protected static final String OLD_PASSWORD = "oldPassword";
   protected static final String NEW_PASSWORD = "newPassword";
   protected static final String DIFFERENT_EMAIL = "differentEmail@email.com";
   protected static final String DIFFERENT_USERNAME = "differentUsername";

   // Song
   protected static final String SONG = "song";
   protected static final String SONG_ID = "songId";
   protected static final String TITLE = "title";
   protected static final String INVALID_TITLE = " ";
   protected static final String REFERENCE = "reference";

   // Playlist
   protected static final String PLAYLIST = "playlist";
   protected static final String PLAYLIST_ID = "playlistId";
   protected static final String GENRE = "genre";
   protected static final String NAME = "name";
   protected static final String NEW_GENRE = "newGenre";
   protected static final String NEW_NAME = "newName";

   // PlaylistSong
   protected static final String PLAYLIST_SONG = "playlistSong";
   protected static final String PLAYLIST_SONG_ID = "playlistSongId";

   // PasswordReset
   protected static final String TOKEN = "token";
   protected static final String INVALID_TOKEN = "";

   // Room
   protected static final String ROOM = "room";

   // ApiException
   protected static final String MESSAGE = "test api exception";
   protected static final ValidationError ERROR = ValidationError.BAD_VALUE;
   protected static final ValidationError ERROR2 = ValidationError.DUPLICATE_VALUE;
   protected static final String FIELD = "test field";
   protected static final String FIELD2 = "test field 2";
   protected static final List<String> FIELDS = ImmutableList.of(FIELD);
   
   /* Helper functions */
   protected ResponseEntity buildResponseEntity(final HttpStatus status) {
      return ResponseEntity.status(status).build();
   }

   protected ResponseEntity buildResponseEntity(final ValidationError error) {
      return ResponseEntity.status(error.getStatus()).body(error.getTag());
   }

   // User
   protected RegistrationData buildRegistrationData() {
      final RegistrationData data = new RegistrationData();
      data.setUsername(USERNAME);
      data.setEmail(EMAIL);
      data.setPassword(PASSWORD);

      return data;
   }

   protected User buildUser() {
      return User.builder()
            .id(VALID_ID)
            .username(USERNAME)
            .email(EMAIL)
            .passwordHash(PASSWORD)
            .build();
   }

   protected UpdateUserData buildUpdateUserData() {
      final UpdateUserData data = new UpdateUserData();
      data.setEmail(EMAIL);
      data.setPassword(PASSWORD);
      data.setOldPassword(OLD_PASSWORD);

      return data;
   }

   protected UpdateUserData buildUpdateUserModel() {
      final UpdateUserData data = new UpdateUserData();
      data.setEmail(EMAIL);
      data.setPassword(PASSWORD);
      data.setOldPassword(OLD_PASSWORD);

      return data;
   }

   // Song
   protected CreateSongData buildCreateSongModel() {
      final CreateSongData data = new CreateSongData();
      data.setTitle(TITLE);
      data.setReference(REFERENCE);

      return data;
   }

   protected Song buildSong() {
      return Song.builder()
            .reference(REFERENCE)
            .title(TITLE)
            .build();
   }

   // Playlist
   protected CreatePlaylistData buildCreatePlaylistData() {
      final CreatePlaylistData data = new CreatePlaylistData();
      data.setName(NAME);
      data.setGenre(GENRE);
      data.setUserId(VALID_ID);
      data.setPrivate(false);

      return data;
   }

   protected UpdatePlaylistData buildUpdatePlaylistData() {
      final UpdatePlaylistData data = new UpdatePlaylistData();
      data.setName(NAME);
      data.setGenre(GENRE);
      data.setIsPrivate(false);

      return data;
   }

   protected Playlist buildPlaylist() {
      return Playlist.builder()
            .name(NAME)
            .userId(VALID_ID)
            .isPrivate(false)
            .genre(GENRE)
            .dateCreated(ZonedDateTime.now())
            .build();
   }

   // PlaylistSong
   protected PlaylistSong buildPlaylistSong() {
      return PlaylistSong.builder()
            .playlistId(VALID_ID)
            .songId(VALID_ID)
            .build();
   }

   // PasswordReset
   protected PasswordReset buildPasswordReset() {
      return PasswordReset.builder()
            .userId(VALID_ID)
            .token(TOKEN)
            .expires(ZonedDateTime.now().plusMinutes(30))
            .user(buildUser())
            .build();
   }

   // Room
   protected CreateRoomData buildCreateRoomData() {
      final CreateRoomData data = new CreateRoomData();
      data.setName(NAME);
      data.setUserId(VALID_ID);
      data.setPrivate(false);

      return data;
   }

   protected Room buildRoom() {
      return Room.builder()
            .name(NAME)
            .userId(VALID_ID)
            .isPrivate(false)
            .playlistId(VALID_ID)
            .build();
   }

   protected PasswordResetData buildPasswordResetData() {
      final PasswordResetData passwordResetData = new PasswordResetData();
      passwordResetData.setUserId(VALID_ID);
      passwordResetData.setToken(TOKEN);
      passwordResetData.setPassword(NEW_PASSWORD);
      return passwordResetData;
   }

   // Update Room
   protected UpdateRoomData buildUpdateRoomData() {
      final UpdateRoomData data = new UpdateRoomData();
      data.setName(NAME);
      data.setPlaylistId(VALID_ID);
      data.setIsPrivate(false);

      return data;
   }
}
