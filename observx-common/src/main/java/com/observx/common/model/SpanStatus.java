package com.observx.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Status of a span or trace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpanStatus {

    /**
     * Status code
     */
    private StatusCode code;

    /**
     * Optional status description
     */
    private String description;

    /**
     * Status code enumeration (OpenTelemetry compatible)
     */
    public enum StatusCode {
        /** The operation completed successfully */
        OK,

        /** The operation contains an error */
        ERROR,

        /** The default status */
        UNSET
    }
}
