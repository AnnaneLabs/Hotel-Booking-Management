package util;

public class ValidationUtils {

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".");
    }

    public static boolean isValidPassword(String password) {
        return isNotBlank(password) && password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        return isNotBlank(phone);
    }
}