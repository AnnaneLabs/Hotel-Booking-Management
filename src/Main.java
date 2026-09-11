import exception.*;
import model.*;
import repository.*;
import repository.impl.*;
import service.*;
import util.InputUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {

    private static AuthService authService;
    private static RoomService roomService;
    private static ReservationService reservationService;

    public static void main(String[] args) {

        // ---------- Wiring ----------
        UserRepository userRepository = new InMemoryUserRepository();
        RoomRepository roomRepository = new InMemoryRoomRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();

        authService = new AuthService(userRepository);
        roomService = new RoomService(roomRepository);
        reservationService = new ReservationService(reservationRepository, roomRepository);

        seedData(userRepository, roomRepository);

        // ---------- Main loop ----------
        boolean running = true;
        while (running) {
            if (!authService.isLoggedIn()) {
                running = showLoggedOutMenu();
            } else {
                running = showLoggedInMenu();
            }
        }

        System.out.println("Goodbye.");
    }

    // ---------- Seeding ----------

    private static void seedData(UserRepository userRepository, RoomRepository roomRepository) {
        userRepository.save(new User("Alice Dupont", "alice@example.com", "0600000001", "alice123"));
        userRepository.save(new User("Bob Martin", "bob@example.com", "0600000002", "bob123"));

        roomRepository.save(new Room("101", RoomType.SINGLE, 1, new BigDecimal("300.00"), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("102", RoomType.DOUBLE, 2, new BigDecimal("500.00"), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("201", RoomType.DOUBLE, 2, new BigDecimal("550.00"), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("205", RoomType.SUITE, 4, new BigDecimal("900.00"), RoomStatus.AVAILABLE));
        roomRepository.save(new Room("301", RoomType.SUITE, 4, new BigDecimal("1200.00"), RoomStatus.MAINTENANCE));
    }

    // ---------- Logged-out menu ----------

    private static boolean showLoggedOutMenu() {
        System.out.println("\n========================");
        System.out.println("HOTEL BOOKING");
        System.out.println("========================");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");

        int choice = InputUtils.readInt("Choice: ");

        switch (choice) {
            case 1 -> handleRegister();
            case 2 -> handleLogin();
            case 0 -> {
                return false;
            }
            default -> System.out.println("Invalid choice, try again.");
        }
        return true;
    }

    private static void handleRegister() {
        String fullName = InputUtils.readNonBlankString("Full name: ");
        String email = InputUtils.readNonBlankString("Email: ");
        String phone = InputUtils.readNonBlankString("Phone: ");
        String password = InputUtils.readNonBlankString("Password: ");

        try {
            authService.register(fullName, email, phone, password);
            System.out.println("Registration successful. You can now log in.");
        } catch (EmailAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        String email = InputUtils.readNonBlankString("Email: ");
        String password = InputUtils.readNonBlankString("Password: ");

        try {
            authService.login(email, password);
            System.out.println("Login successful.");
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------- Logged-in menu ----------

    private static boolean showLoggedInMenu() {
        User current = authService.getCurrentUser();
        System.out.println("\n================================");
        System.out.println("Logged in as: " + current.getFullName());
        System.out.println("================================");
        System.out.println("1. Search available rooms");
        System.out.println("2. View all rooms");
        System.out.println("3. Create reservation");
        System.out.println("4. My reservations");
        System.out.println("5. Update reservation");
        System.out.println("6. Cancel reservation");
        System.out.println("7. Update profile");
        System.out.println("8. Change password");
        System.out.println("9. Logout");
        System.out.println("0. Exit");

        int choice = InputUtils.readInt("Choice: ");

        switch (choice) {
            case 1 -> handleSearchAvailableRooms();
            case 2 -> handleViewAllRooms();
            case 3 -> handleCreateReservation();
            case 4 -> handleMyReservations();
            case 5 -> handleUpdateReservation();
            case 6 -> handleCancelReservation();
            case 7 -> handleUpdateProfile();
            case 8 -> handleChangePassword();
            case 9 -> {
                authService.logout();
                System.out.println("Logged out.");
            }
            case 0 -> {
                return false;
            }
            default -> System.out.println("Invalid choice, try again.");
        }
        return true;
    }

    private static void handleSearchAvailableRooms() {
        LocalDate checkIn = InputUtils.readDate("Check-in date (dd/MM/yyyy): ");
        LocalDate checkOut = InputUtils.readDate("Check-out date (dd/MM/yyyy): ");
        int guests = InputUtils.readInt("Number of guests: ");

        List<Room> allRooms = roomService.getAllRooms();

        System.out.println("\nAvailable rooms:");
        for (Room room : allRooms) {
            if (room.getStatus() != RoomStatus.AVAILABLE) continue;
            if (room.getCapacity() < guests) continue;

            boolean isFree = reservationService.isRoomAvailable(room.getRoomNumber(), checkIn, checkOut);
            if (isFree) {
                System.out.println(room);
            }
        }
    }

    private static void handleViewAllRooms() {
        List<Room> allRooms = roomService.getAllRooms();
        System.out.println("\nAll rooms:");
        allRooms.forEach(System.out::println);
    }

    private static void handleCreateReservation() {
        String roomNumber = InputUtils.readNonBlankString("Room number: ");
        LocalDate checkIn = InputUtils.readDate("Check-in date (dd/MM/yyyy): ");
        LocalDate checkOut = InputUtils.readDate("Check-out date (dd/MM/yyyy): ");
        int guests = InputUtils.readInt("Number of guests: ");

        try {
            Reservation reservation = reservationService.createReservation(
                    authService.getCurrentUser(), roomNumber, checkIn, checkOut, guests);
            System.out.println("Reservation created: " + reservation);
        } catch (RoomNotFoundException | RoomUnavailableException
                 | InvalidReservationDateException | RoomCapacityExceededException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleMyReservations() {
        List<Reservation> reservations = reservationService.getReservationsForUser(authService.getCurrentUser());
        System.out.println("\nYour reservations:");
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    private static void handleUpdateReservation() {
        String code = InputUtils.readNonBlankString("Reservation code: ");
        LocalDate checkIn = InputUtils.readDate("New check-in date (dd/MM/yyyy): ");
        LocalDate checkOut = InputUtils.readDate("New check-out date (dd/MM/yyyy): ");
        int guests = InputUtils.readInt("New number of guests: ");

        try {
            Reservation updated = reservationService.updateReservation(
                    code, authService.getCurrentUser(), checkIn, checkOut, guests);
            System.out.println("Reservation updated: " + updated);
        } catch (ReservationNotFoundException | UnauthorizedReservationAccessException
                 | ReservationAlreadyCancelledException | RoomNotFoundException
                 | RoomUnavailableException | InvalidReservationDateException
                 | RoomCapacityExceededException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleCancelReservation() {
        String code = InputUtils.readNonBlankString("Reservation code: ");

        try {
            reservationService.cancelReservation(code, authService.getCurrentUser());
            System.out.println("Reservation cancelled.");
        } catch (ReservationNotFoundException | UnauthorizedReservationAccessException
                 | ReservationAlreadyCancelledException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleUpdateProfile() {
        String fullName = InputUtils.readNonBlankString("New full name: ");
        String email = InputUtils.readNonBlankString("New email: ");
        String phone = InputUtils.readNonBlankString("New phone: ");

        try {
            authService.updateProfile(fullName, email, phone);
            System.out.println("Profile updated.");
        } catch (EmailAlreadyExistsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleChangePassword() {
        String oldPassword = InputUtils.readNonBlankString("Old password: ");
        String newPassword = InputUtils.readNonBlankString("New password: ");

        try {
            authService.changePassword(oldPassword, newPassword);
            System.out.println("Password changed.");
        } catch (InvalidCredentialsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}