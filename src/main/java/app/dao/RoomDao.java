package app.dao;

import app.model.Room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDao extends JpaRepository<Room, Integer> {
   List<Room> findByNameStartingWith(String room);
}
