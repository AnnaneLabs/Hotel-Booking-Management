package util;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class InputUtils {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readNonBlankString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();
            if (ValidationUtils.isNotBlank(value)) {
                return value;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();
            Optional<LocalDate> parsed = DateUtils.parseDate(value);
            if (parsed.isPresent()) {
                return parsed.get();
            }
            System.out.println("Invalid date format. Please use dd/MM/yyyy.");
        }
    }
}