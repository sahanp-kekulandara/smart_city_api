package com.groupkekulandara.util;

import java.security.SecureRandom;
import java.sql.Timestamp;

public class VerificationCode {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateVerificationCode() {
        long millis = System.currentTimeMillis();
        String timePart = String.valueOf(millis).substring(8); // last few digits

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        code.append(timePart);
        return code.toString();
    }

    public static Timestamp getExpiryTimestamp() {
        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + (5 * 60 * 1000); // 5 minutes
        return new Timestamp(expiryTime);
    }
}
