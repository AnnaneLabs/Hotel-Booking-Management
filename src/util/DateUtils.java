package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Safe parsing: returns Optional.empty() instead of crashing on bad input.
    public static Optional<LocalDate> parseDate(String input) {
        try {
            return Optional.of(LocalDate.parse(input, FORMATTER));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // Number of nights between two dates.
    public static long numberOfNights(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    // The overlap check — explained in detail below the code.
    public static boolean datesOverlap(LocalDate existingCheckIn, LocalDate existingCheckOut,
                                       LocalDate newCheckIn, LocalDate newCheckOut) {
        return newCheckIn.isBefore(existingCheckOut) && existingCheckIn.isBefore(newCheckOut);
    }
}