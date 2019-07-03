package app.service;

import app.dao.RoomDao;
import app.exception.ApiException;
import app.model.Room;
import app.validation.ValidationError;

import java.util.Optional;
import app.model.Room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static app.constant.FieldConstants.ROOM;

@Service
public class RoomService {

   @Autowired
   private RoomDao roomDao;

   /**
    * Service call for creating a new room.
    *
    * @param room contains Room information
    */
   public void createNewRoom(final Room room) {
      roomDao.save(room);
   }

   /**
    * Service call for getting a room by id.
    *
    * @param id room id to check for
    * @return Room found for given id
    */
   public Room getRoomById(final int id) {
      final Optional<Room> room = roomDao.findById(id);

      if (!room.isPresent()) {
         throw new ApiException("Room does not exist", ValidationError.NOT_FOUND, ROOM);
      }

      return room.get();
   }

   /**
    * Service call for deleting a room by id.
    *
    * @param id room id to check for
    */
   public void deleteRoomById(final int id) {
      final Optional<Room> room = roomDao.findById(id);

      if (!room.isPresent()) {
         throw new ApiException("Room does not exist", ValidationError.NOT_FOUND, ROOM);
      }

      roomDao.deleteById(id);
   }
}
