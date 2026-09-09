import exception.*;
import model.User;
import repository.UserRepository;
import repository.impl.InMemoryUserRepository;
import service.AuthService;
import util.InputUtils;

public class Main {
    public static void main(String[] args) {

        UserRepository userRepository = new InMemoryUserRepository();
        AuthService authService = new AuthService(userRepository);

        boolean running = true;

        while (running) {
            if (!authService.isLoggedIn()) {
                System.out.println("\n========================");
                System.out.println("HOTEL BOOKING");
                System.out.println("========================");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");

                int choice = InputUtils.readInt("Choice: ");

                switch (choice) {
                    case 1 -> handleRegister(authService);
                    case 2 -> handleLogin(authService);
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice, try again.");
                }

            } else {
                User current = authService.getCurrentUser();
                System.out.println("\n================================");
                System.out.println("Logged in as: " + current.getFullName());
                System.out.println("================================");
                System.out.println("1. Update profile");
                System.out.println("2. Change password");
                System.out.println("9. Logout");
                System.out.println("0. Exit");

                int choice = InputUtils.readInt("Choice: ");

                switch (choice) {
                    case 1 -> handleUpdateProfile(authService);
                    case 2 -> handleChangePassword(authService);
                    case 9 -> {
                        authService.logout();
                        System.out.println("Logged out.");
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice, try again.");
                }
            }
        }

        System.out.println("Goodbye.");
    }

    private static void handleRegister(AuthService authService) {
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

    private static void handleLogin(AuthService authService) {
        String email = InputUtils.readNonBlankString("Email: ");
        String password = InputUtils.readNonBlankString("Password: ");

        try {
            authService.login(email, password);
            System.out.println("Login successful.");
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleUpdateProfile(AuthService authService) {
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

    private static void handleChangePassword(AuthService authService) {
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