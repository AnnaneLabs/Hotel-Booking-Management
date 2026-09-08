import model.*;
import repository.*;
import repository.impl.*;

import java.math.BigDecimal;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        // --- Test Room ---
        RoomRepository roomRepo = new InMemoryRoomRepository();

        Room room102 = new Room("102", RoomType.DOUBLE, 2,
                new BigDecimal("500.00"), RoomStatus.AVAILABLE);
        roomRepo.save(room102);

        Optional<Room> found = roomRepo.findByRoomNumber("102");
        System.out.println("Found room: " + found.orElse(null));

        Optional<Room> notFound = roomRepo.findByRoomNumber("999");
        System.out.println("Not found room (should be empty): " + notFound);

        System.out.println("All rooms: " + roomRepo.findAll());

        // --- Test User ---
        UserRepository userRepo = new InMemoryUserRepository();

        User alice = new User("Alice Dupont", "alice@example.com", "0600000000", "alice123");
        userRepo.save(alice);

        System.out.println("Find by email: " + userRepo.findByEmail("alice@example.com"));
        System.out.println("Exists by email: " + userRepo.existsByEmail("alice@example.com"));
        System.out.println("Exists fake email: " + userRepo.existsByEmail("nope@example.com"));

        // --- Test Reservation ---
        ReservationRepository reservationRepo = new InMemoryReservationRepository();

        Reservation res = new Reservation(alice.getId(), "102",
                java.time.LocalDate.of(2026, 9, 10),
                java.time.LocalDate.of(2026, 9, 13),
                2, 3, new BigDecimal("1500.00"));
        res.setReservationCode("RES-2026-0001");
        reservationRepo.save(res);

        System.out.println("Find by code: " + reservationRepo.findByCode("RES-2026-0001"));
        System.out.println("Find by user: " + reservationRepo.findByUserId(alice.getId()));
    }
}