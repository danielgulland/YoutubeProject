package app.service;

import app.dao.RoomDao;
import app.model.Room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
