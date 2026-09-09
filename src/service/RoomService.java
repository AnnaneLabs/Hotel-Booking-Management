package service;

import exception.RoomNotFoundException;
import model.Room;
import model.RoomStatus;
import repository.RoomRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room findRoomByNumber(String roomNumber) {
        Optional<Room> roomOpt = roomRepository.findByRoomNumber(roomNumber);
        return roomOpt.orElseThrow(() ->
                new RoomNotFoundException("Room not found."));
    }
    public List<Room> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void addRoom(Room room) {
        roomRepository.save(room);
    }

    public void setRoomStatus(String roomNumber, RoomStatus status) {
        Room room = findRoomByNumber(roomNumber);
        room.setStatus(status);
        roomRepository.save(room);
    }
}