import exception.*;
import model.*;
import repository.*;
import repository.impl.*;
import service.*;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        // --- Setup ---
        UserRepository userRepository = new InMemoryUserRepository();
        RoomRepository roomRepository = new InMemoryRoomRepository();

        AuthService authService = new AuthService(userRepository);
        RoomService roomService = new RoomService(roomRepository);

        // Seed a room
        Room room102 = new Room("102", RoomType.DOUBLE, 2,
                new BigDecimal("500.00"), RoomStatus.AVAILABLE);
        roomService.addRoom(room102);

        Room room301 = new Room("301", RoomType.SUITE, 4,
                new BigDecimal("1200.00"), RoomStatus.MAINTENANCE);
        roomService.addRoom(room301);

        // --- Test register (happy path) ---
        User alice = authService.register("Alice Dupont", "alice@example.com",
                "0600000000", "alice123");
        System.out.println("Registered: " + alice);

        // --- Test register with duplicate email (should throw) ---
        try {
            authService.register("Fake Alice", "alice@example.com", "0611111111", "whatever");
            System.out.println("ERROR: should have thrown EmailAlreadyExistsException");
        } catch (EmailAlreadyExistsException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // --- Test login with wrong password (should throw) ---
        try {
            authService.login("alice@example.com", "wrongpassword");
            System.out.println("ERROR: should have thrown InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // --- Test login with nonexistent email (should throw) ---
        try {
            authService.login("nobody@example.com", "whatever");
            System.out.println("ERROR: should have thrown UserNotFoundException");
        } catch (UserNotFoundException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // --- Test login (happy path) ---
        User loggedIn = authService.login("alice@example.com", "alice123");
        System.out.println("Logged in as: " + loggedIn.getFullName());
        System.out.println("isLoggedIn: " + authService.isLoggedIn());

        // --- Test updateProfile ---
        authService.updateProfile("Alice D.", "alice@example.com", "0699999999");
        System.out.println("After update: " + authService.getCurrentUser());

        // --- Test changePassword with wrong old password (should throw) ---
        try {
            authService.changePassword("wrongold", "newpass123");
            System.out.println("ERROR: should have thrown InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // --- Test changePassword (happy path) ---
        authService.changePassword("alice123", "newpass123");
        System.out.println("Password changed successfully.");

        // --- Test logout ---
        authService.logout();
        System.out.println("isLoggedIn after logout: " + authService.isLoggedIn());

        // --- Test RoomService.findRoomByNumber (happy path) ---
        Room found = roomService.findRoomByNumber("102");
        System.out.println("Found room: " + found);

        // --- Test RoomService.findRoomByNumber (not found, should throw) ---
        try {
            roomService.findRoomByNumber("999");
            System.out.println("ERROR: should have thrown RoomNotFoundException");
        } catch (RoomNotFoundException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // --- Test getRoomsByStatus ---
        System.out.println("Available rooms: " + roomService.getRoomsByStatus(RoomStatus.AVAILABLE));
        System.out.println("Maintenance rooms: " + roomService.getRoomsByStatus(RoomStatus.MAINTENANCE));
    }
}