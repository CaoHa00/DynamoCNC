CREATE TABLE machine_segment (
    id BIGINT IDENTITY PRIMARY KEY,
    machine_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,

    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
    duration BIGINT NOT NULL,

    work_date DATE NOT NULL,
    shift VARCHAR(10) NOT NULL,

    created_at DATETIME2 DEFAULT SYSDATETIME()
);

CREATE INDEX idx_machine_segment_machine
ON machine_segment(machine_id);

CREATE INDEX idx_machine_segment_machine_date_shift
ON machine_segment(machine_id, work_date, shift);