package app.controller;

import app.model.Room;
import app.request.CreateRoomData;
import app.service.RoomService;
import app.validation.ValidationError;
import app.validation.Validator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static app.constant.FieldConstants.ID;
import static app.constant.FieldConstants.NAME;

@RestController
@RequestMapping(path = "/rooms")
public class RoomController {

   @Autowired
   private RoomService roomService;

   @Autowired
   private Validator validator;

   /**
    * Create a new Room given the room data.
    *
    * @param createRoomData information required to create a new room
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @PostMapping()
   public ResponseEntity createNewRoom(@RequestBody final CreateRoomData createRoomData) {
      if (validator.chain(createRoomData.getUserId() > 0, ValidationError.BAD_VALUE, ID)
            .check(StringUtils.isNotBlank(createRoomData.getName()), ValidationError.MISSING_FIELD, NAME)) {
         roomService.createNewRoom(buildFromCreateRoomData(createRoomData));

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   /**
    *  Get a Room by a room id.
    *
    * @param id room id
    * @return Response with status 200 and Room in the body for successful call, otherwise validation response
    */
   @GetMapping("/{id}")
   public ResponseEntity getRoomById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         final Room room = roomService.getRoomById(id);

         return ResponseEntity.status(HttpStatus.OK).body(room);
      }

      return validator.getResponseEntity();
   }

   /**
    * Get a list of rooms based on the name, otherwise every room when name is blank.
    *
    * @param name name or name prefix to search for
    * @return Response with status 200 and rooms in the body for successful call, otherwise validation response
    */
   @GetMapping()
   public ResponseEntity getRooms(@RequestParam(required = false) final String name) {
      final List<Room> rooms;
      if (StringUtils.isNotBlank(name)) {
         rooms = roomService.getRoomsWithFilter(name);
      }
      else {
         rooms = roomService.getAllRooms();
      }

      return ResponseEntity.status(HttpStatus.OK).body(rooms);
   }

   /**
    * Delete a Room by a room id.
    *
    * @param id room id
    * @return Response with status 200 and empty body for successful call, otherwise validation response
    */
   @DeleteMapping("/{id}")
   public ResponseEntity deleteRoomById(@PathVariable final int id) {
      if (validator.check(id > 0, ValidationError.BAD_VALUE, ID)) {
         roomService.deleteRoomById(id);

         return ResponseEntity.status(HttpStatus.OK).body(null);
      }

      return validator.getResponseEntity();
   }

   private Room buildFromCreateRoomData(final CreateRoomData createRoomData) {
      return Room.builder()
            .name(createRoomData.getName())
            .userId(createRoomData.getUserId())
            .isPrivate(createRoomData.isPrivate())
            .build();
   }
}
