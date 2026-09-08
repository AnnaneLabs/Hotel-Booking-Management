package repository.impl;

import model.Reservation;
import repository.ReservationRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<UUID, Reservation> reservations = new HashMap<>();

    @Override
    public void save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public Optional<Reservation> findByCode(String code) {
        return reservations.values().stream()
                .filter(r -> r.getReservationCode() != null
                        && r.getReservationCode().equalsIgnoreCase(code))
                .findFirst();
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        return reservations.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByRoomNumber(String roomNumber) {
        return reservations.values().stream()
                .filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }
}