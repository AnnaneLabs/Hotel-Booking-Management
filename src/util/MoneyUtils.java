package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {

    public static BigDecimal calculateTotal(long nights, BigDecimal pricePerNight) {
        BigDecimal total = pricePerNight.multiply(BigDecimal.valueOf(nights));
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}