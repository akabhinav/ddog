-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

-- Metrics table with TimescaleDB hypertable
CREATE TABLE metrics (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    tags JSONB,
    source VARCHAR(255),
    unit VARCHAR(50),
    metadata JSONB
);

-- Convert metrics table to hypertable for time-series optimization
SELECT create_hypertable('metrics', 'timestamp', if_not_exists => TRUE);

-- Create indexes for common queries
CREATE INDEX idx_metrics_name ON metrics (name, timestamp DESC);
CREATE INDEX idx_metrics_tags ON metrics USING GIN (tags);
CREATE INDEX idx_metrics_source ON metrics (source, timestamp DESC);

-- Logs table
CREATE TABLE logs (
    id VARCHAR(255) PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    service VARCHAR(255),
    host VARCHAR(255),
    logger VARCHAR(500),
    thread VARCHAR(255),
    trace_id VARCHAR(255),
    span_id VARCHAR(255),
    tags JSONB,
    fields JSONB,
    stack_trace TEXT
);

-- Convert logs table to hypertable
SELECT create_hypertable('logs', 'timestamp', if_not_exists => TRUE);

-- Create indexes for logs
CREATE INDEX idx_logs_timestamp ON logs (timestamp DESC);
CREATE INDEX idx_logs_service ON logs (service, timestamp DESC);
CREATE INDEX idx_logs_level ON logs (level, timestamp DESC);
CREATE INDEX idx_logs_trace_id ON logs (trace_id);
CREATE INDEX idx_logs_tags ON logs USING GIN (tags);
CREATE INDEX idx_logs_message ON logs USING GIN (to_tsvector('english', message));

-- Spans table for distributed tracing
CREATE TABLE spans (
    span_id VARCHAR(255) PRIMARY KEY,
    trace_id VARCHAR(255) NOT NULL,
    parent_span_id VARCHAR(255),
    name VARCHAR(500) NOT NULL,
    kind VARCHAR(50),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    duration_nanos BIGINT,
    service_name VARCHAR(255),
    attributes JSONB,
    events JSONB,
    status JSONB,
    links JSONB
);

-- Convert spans table to hypertable
SELECT create_hypertable('spans', 'start_time', if_not_exists => TRUE);

-- Create indexes for spans
CREATE INDEX idx_spans_trace_id ON spans (trace_id);
CREATE INDEX idx_spans_start_time ON spans (start_time DESC);
CREATE INDEX idx_spans_service ON spans (service_name, start_time DESC);
CREATE INDEX idx_spans_name ON spans (name, start_time DESC);

-- Continuous aggregates for metrics (pre-computed rollups)
CREATE MATERIALIZED VIEW metrics_1min
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 minute', timestamp) AS bucket,
    name,
    tags,
    AVG(value) AS avg_value,
    MAX(value) AS max_value,
    MIN(value) AS min_value,
    COUNT(*) AS count
FROM metrics
GROUP BY bucket, name, tags
WITH NO DATA;

-- Refresh policy for continuous aggregate
SELECT add_continuous_aggregate_policy('metrics_1min',
    start_offset => INTERVAL '1 hour',
    end_offset => INTERVAL '1 minute',
    schedule_interval => INTERVAL '1 minute');

-- Data retention policies (keep data for 30 days by default)
SELECT add_retention_policy('metrics', INTERVAL '30 days');
SELECT add_retention_policy('logs', INTERVAL '30 days');
SELECT add_retention_policy('spans', INTERVAL '30 days');

-- Compression policies for older data (compress data older than 7 days)
ALTER TABLE metrics SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'name, source'
);

ALTER TABLE logs SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'service, level'
);

ALTER TABLE spans SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'trace_id, service_name'
);

SELECT add_compression_policy('metrics', INTERVAL '7 days');
SELECT add_compression_policy('logs', INTERVAL '7 days');
SELECT add_compression_policy('spans', INTERVAL '7 days');
