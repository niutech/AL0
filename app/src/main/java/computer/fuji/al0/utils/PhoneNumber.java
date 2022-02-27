package computer.fuji.al0.utils;

import android.telephony.PhoneNumberUtils;

import java.util.Locale;

public class PhoneNumber {
    public static String cleanPhoneNumber (String number) {
        return number.replaceAll("[()\\s-]+", "");
    }

    public static String wildcardifyPhoneNumber (String number) {
        String cleanPhoneNumber = cleanPhoneNumber(number);
        String wildcardChar = "%";
        String wildcardifyedPhoneNumber = wildcardChar;
        for (int i = 0; i < cleanPhoneNumber.length(); i++) {
            wildcardifyedPhoneNumber = wildcardifyedPhoneNumber.concat(cleanPhoneNumber.charAt(i) + wildcardChar);
        }

        return wildcardifyedPhoneNumber;
    }

    public static String formatPhoneNumber (String phoneNumber) {
        String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
        if (formattedNumber != null) {
            return formattedNumber;
        } else {
            return phoneNumber;
        }
    }
}
