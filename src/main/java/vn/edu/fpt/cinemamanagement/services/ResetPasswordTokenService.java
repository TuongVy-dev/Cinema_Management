package vn.edu.fpt.cinemamanagement.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ResetPasswordTokenService {

    private static final String TOKEN_SECRET = "wait";

    public long currentTimestamp() {
        return System.currentTimeMillis();
    }

    public LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public String createToken(String customerId, long timestamp) {
        String input = TOKEN_SECRET + ":" + customerId + ":" + timestamp;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create reset password token", e);
        }
    }

    public boolean isValid(String customerId, long timestamp, String token) {
        return createToken(customerId, timestamp).equalsIgnoreCase(token);
    }

    public boolean matchesStoredTimestamp(LocalDateTime storedDateTime, long timestamp) {
        return Math.abs(toTimestamp(storedDateTime) - timestamp) <= 10;
    }
}
