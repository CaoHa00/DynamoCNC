CREATE TABLE machine_segment (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    machine_id INT NOT NULL,
    staff_id VARCHAR(255) NULL,
    status NVARCHAR(10) NOT NULL,

    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NULL,

    duration_seconds INT NULL,

    log_date DATE NOT NULL,
    process_id VARCHAR(50) NULL,
    process_type NVARCHAR(20) NULL,
    shift_code NVARCHAR(20) NOT NULL,
    aggregated int DEFAULT 0
);

-- Index phục vụ query
CREATE INDEX idx_segment_machine_start
    ON machine_segment (machine_id, start_time);

CREATE INDEX idx_segment_machine_date_shift
    ON machine_segment (machine_id, log_date, shift_code);

CREATE INDEX idx_segment_log_date
    ON machine_segment (log_date);
