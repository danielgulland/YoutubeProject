package app.dao;

import app.model.Room;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDao extends JpaRepository<Room, Integer> {

}
