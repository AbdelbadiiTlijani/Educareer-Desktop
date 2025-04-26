package tn.esprit.educareer.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OtpStorage {
    private static final Map<String, OtpEntry> otpMap = new HashMap<>();

    public static void storeOtp(String email, String otp, LocalDateTime expiry) {
        otpMap.put(email, new OtpEntry(otp, expiry));
    }

    public static boolean verifyOtp(String email, String inputOtp) {
        OtpEntry entry = otpMap.get(email);
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiry)) return false;
        return entry.otp.equals(inputOtp);
    }

    public static void removeOtp(String email) {
        otpMap.remove(email);
    }

    public static class OtpEntry {
        public String otp;
        public LocalDateTime expiry;

        public OtpEntry(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
