import util.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        // --- Test DateUtils.parseDate ---
        Optional<LocalDate> goodDate = DateUtils.parseDate("10/09/2026");
        System.out.println("Parsed good date: " + goodDate);

        Optional<LocalDate> badDate = DateUtils.parseDate("not-a-date");
        System.out.println("Parsed bad date (should be empty): " + badDate);

        // --- Test DateUtils.numberOfNights ---
        LocalDate checkIn = LocalDate.of(2026, 9, 10);
        LocalDate checkOut = LocalDate.of(2026, 9, 13);
        long nights = DateUtils.numberOfNights(checkIn, checkOut);
        System.out.println("Nights (expect 3): " + nights);

        // --- Test DateUtils.datesOverlap ---
        LocalDate existingIn = LocalDate.of(2026, 9, 10);
        LocalDate existingOut = LocalDate.of(2026, 9, 15);

        LocalDate newIn1 = LocalDate.of(2026, 9, 12);
        LocalDate newOut1 = LocalDate.of(2026, 9, 17);
        System.out.println("Overlap case (expect true): " +
                DateUtils.datesOverlap(existingIn, existingOut, newIn1, newOut1));

        LocalDate newIn2 = LocalDate.of(2026, 9, 15);
        LocalDate newOut2 = LocalDate.of(2026, 9, 20);
        System.out.println("No-overlap case (expect false): " +
                DateUtils.datesOverlap(existingIn, existingOut, newIn2, newOut2));

        // --- Test MoneyUtils.calculateTotal ---
        BigDecimal total = MoneyUtils.calculateTotal(3, new BigDecimal("500.00"));
        System.out.println("Total (expect 1500.00): " + total);

        // --- Test ValidationUtils ---
        System.out.println("isValidEmail alice@example.com (expect true): " +
                ValidationUtils.isValidEmail("alice@example.com"));
        System.out.println("isValidEmail bademail (expect false): " +
                ValidationUtils.isValidEmail("bademail"));
        System.out.println("isValidPassword 'alice123' (expect true): " +
                ValidationUtils.isValidPassword("alice123"));
        System.out.println("isValidPassword '123' (expect false): " +
                ValidationUtils.isValidPassword("123"));
        System.out.println("isNotBlank '' (expect false): " +
                ValidationUtils.isNotBlank(""));

        // --- Test InputUtils (interactive — comment out if you don't want to type) ---
        // String name = InputUtils.readNonBlankString("Enter your name: ");
        // System.out.println("You entered: " + name);
        //
        // int age = InputUtils.readInt("Enter your age: ");
        // System.out.println("You entered: " + age);
        //
        // LocalDate date = InputUtils.readDate("Enter a date (dd/MM/yyyy): ");
        // System.out.println("You entered: " + date);
    }
}