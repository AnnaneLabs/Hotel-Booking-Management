package repository.impl;

import model.Room;
import repository.RoomRepository;

import java.util.*;

public class InMemoryRoomRepository implements RoomRepository {

    private final Map<String, Room> rooms = new HashMap<>();

    @Override
    public void save(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    @Override
    public Optional<Room> findByRoomNumber(String roomNumber) {
        return Optional.ofNullable(rooms.get(roomNumber));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
}