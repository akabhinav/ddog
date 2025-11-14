package com.observx.common.util;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for parsing time window strings like "1m", "5m", "1h", "1d".
 */
public class TimeWindowParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");

    /**
     * Parse time window string to Duration
     *
     * @param window Time window string (e.g., "1m", "5m", "1h")
     * @return Duration object
     */
    public static Duration parse(String window) {
        if (window == null || window.isEmpty()) {
            throw new IllegalArgumentException("Time window cannot be null or empty");
        }

        Matcher matcher = TIME_PATTERN.matcher(window);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time window format: " + window);
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> Duration.ofSeconds(value);
            case "m" -> Duration.ofMinutes(value);
            case "h" -> Duration.ofHours(value);
            case "d" -> Duration.ofDays(value);
            default -> throw new IllegalArgumentException("Unsupported time unit: " + unit);
        };
    }

    /**
     * Convert Duration to seconds
     */
    public static long toSeconds(Duration duration) {
        return duration.getSeconds();
    }
}
