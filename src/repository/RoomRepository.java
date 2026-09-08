package repository;

import model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {

    void save(Room room);

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findAll();
}