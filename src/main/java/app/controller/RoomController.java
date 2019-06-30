package app.controller;

import app.model.Room;
import app.request.CreateRoomData;
import app.service.RoomService;
import app.validation.ValidationError;
import app.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

   private Room buildFromCreateRoomData(final CreateRoomData createRoomData) {
      return Room.builder()
            .name(createRoomData.getName())
            .userId(createRoomData.getUserId())
            .isPrivate(createRoomData.isPrivate())
            .build();
   }
}
