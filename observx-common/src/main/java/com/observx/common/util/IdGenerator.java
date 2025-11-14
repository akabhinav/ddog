package com.observx.common.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility for generating unique identifiers for traces, spans, and metrics.
 */
public class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Generate a UUID-based ID
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a trace ID (128-bit hex string)
     */
    public static String generateTraceId() {
        return generateHexId(16); // 16 bytes = 128 bits
    }

    /**
     * Generate a span ID (64-bit hex string)
     */
    public static String generateSpanId() {
        return generateHexId(8); // 8 bytes = 64 bits
    }

    /**
     * Generate a hex ID of specified byte length
     */
    private static String generateHexId(int byteLength) {
        byte[] bytes = new byte[byteLength];
        RANDOM.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    /**
     * Convert bytes to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }
}
