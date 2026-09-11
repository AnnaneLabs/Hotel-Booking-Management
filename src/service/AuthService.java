package service;

import exception.EmailAlreadyExistsException;
import exception.InvalidCredentialsException;
import exception.UserNotFoundException;
import model.User;
import repository.UserRepository;

import java.util.Optional;
import util.ValidationUtils;


public class AuthService {

    private final UserRepository userRepository;
    private User currentUser;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String fullName, String email, String phone, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        if (!ValidationUtils.isValidPassword(password)) {
            throw new InvalidCredentialsException("Password must be at least 6 characters.");
        }
        User user = new User(fullName, email, phone, password);
        userRepository.save(user);
        return user;
    }

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // orElseThrow: if the Optional is empty, throw the given exception
        // immediately; if it has a value, unwrap it and continue.
        User user = userOpt.orElseThrow(() ->
                new UserNotFoundException("User not found."));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void updateProfile(String fullName, String email, String phone) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }

        if (!currentUser.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        userRepository.save(currentUser);
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }

        if (!currentUser.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException("Old password is incorrect.");
        }

        currentUser.setPassword(newPassword);
        userRepository.save(currentUser);
    }
}