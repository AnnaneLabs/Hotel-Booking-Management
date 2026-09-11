package service;

import exception.*;
import model.*;
import repository.ReservationRepository;
import repository.RoomRepository;
import util.DateUtils;
import util.MoneyUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    // ---------- CREATE ----------

    public Reservation createReservation(User user, String roomNumber,
                                         LocalDate checkIn, LocalDate checkOut,
                                         int numberOfGuests) {

        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found."));

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new RoomUnavailableException("Room is currently under maintenance.");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Check-in date must be today or later.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidReservationDateException("Check-out date must be after check-in date.");
        }

        if (numberOfGuests > room.getCapacity()) {
            throw new RoomCapacityExceededException("Room capacity exceeded.");
        }

        boolean hasOverlap = reservationRepository.findByRoomNumber(roomNumber).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .anyMatch(r -> DateUtils.datesOverlap(
                        r.getCheckIn(), r.getCheckOut(), checkIn, checkOut));

        if (hasOverlap) {
            throw new RoomUnavailableException("Room is unavailable for the selected dates.");
        }

        long nights = DateUtils.numberOfNights(checkIn, checkOut);
        var totalPrice = MoneyUtils.calculateTotal(nights, room.getPricePerNight());

        Reservation reservation = new Reservation(
                user.getId(), roomNumber, checkIn, checkOut,
                numberOfGuests, nights, totalPrice);

        reservation.setReservationCode(generateReservationCode());
        reservationRepository.save(reservation);

        return reservation;
    }

    // ---------- CANCEL ----------

    public void cancelReservation(String reservationCode, User currentUser) {
        Reservation reservation = reservationRepository.findByCode(reservationCode)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found."));

        if (!reservation.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedReservationAccessException(
                    "You are not allowed to modify this reservation.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException("Reservation already cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // ---------- UPDATE ----------

    public Reservation updateReservation(String reservationCode, User currentUser,
                                         LocalDate newCheckIn, LocalDate newCheckOut,
                                         int newNumberOfGuests) {

        Reservation reservation = reservationRepository.findByCode(reservationCode)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found."));

        if (!reservation.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedReservationAccessException(
                    "You are not allowed to modify this reservation.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException(
                    "Cannot modify a cancelled reservation.");
        }

        Room room = roomRepository.findByRoomNumber(reservation.getRoomNumber())
                .orElseThrow(() -> new RoomNotFoundException("Room not found."));

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new RoomUnavailableException("Room is currently under maintenance.");
        }

        if (newCheckIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Check-in date must be today or later.");
        }

        if (!newCheckOut.isAfter(newCheckIn)) {
            throw new InvalidReservationDateException("Check-out date must be after check-in date.");
        }

        if (newNumberOfGuests > room.getCapacity()) {
            throw new RoomCapacityExceededException("Room capacity exceeded.");
        }

        boolean hasOverlap = reservationRepository.findByRoomNumber(reservation.getRoomNumber()).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> !r.getId().equals(reservation.getId()))
                .anyMatch(r -> DateUtils.datesOverlap(
                        r.getCheckIn(), r.getCheckOut(), newCheckIn, newCheckOut));

        if (hasOverlap) {
            throw new RoomUnavailableException("Room is unavailable for the selected dates.");
        }

        long nights = DateUtils.numberOfNights(newCheckIn, newCheckOut);
        var totalPrice = MoneyUtils.calculateTotal(nights, room.getPricePerNight());

        reservation.setCheckIn(newCheckIn);
        reservation.setCheckOut(newCheckOut);
        reservation.setNumberOfGuests(newNumberOfGuests);
        reservation.setNumberOfNights(nights);
        reservation.setTotalPrice(totalPrice);

        reservationRepository.save(reservation);

        return reservation;
    }

    // ---------- READ ----------

    public List<Reservation> getReservationsForUser(User user) {
        return reservationRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(Reservation::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return reservationRepository.findByRoomNumber(roomNumber).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .noneMatch(r -> DateUtils.datesOverlap(
                        r.getCheckIn(), r.getCheckOut(), checkIn, checkOut));
    }

    // ---------- HELPERS ----------

    private String generateReservationCode() {
        int year = LocalDate.now().getYear();
        long count = reservationRepository.findAll().size() + 1;
        return String.format("RES-%d-%04d", year, count);
    }
}