CREATE TABLE dbo.machine_daily (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    machine_id INT NOT NULL,
    log_date DATE NOT NULL,
    shift_code VARCHAR(10) NOT NULL,

    run_pg_seconds BIGINT NOT NULL DEFAULT 0,
    run_offset_seconds BIGINT NOT NULL DEFAULT 0,
    empty_seconds BIGINT NOT NULL DEFAULT 0,
    stop_seconds BIGINT NOT NULL DEFAULT 0,
    error_seconds BIGINT NOT NULL DEFAULT 0,

    main_product_seconds BIGINT NOT NULL DEFAULT 0,
    re_run_seconds BIGINT NOT NULL DEFAULT 0,
    lk_seconds BIGINT NOT NULL DEFAULT 0,
    electric_seconds BIGINT NOT NULL DEFAULT 0,
    preparation_seconds BIGINT NOT NULL DEFAULT 0, 

    main_product_pg_seconds BIGINT DEFAULT 0,
    electric_pg_seconds BIGINT DEFAULT 0,
    other_seconds BIGINT DEFAULT 0,
    span_seconds BIGINT DEFAULT 0,
    expected_seconds BIGINT DEFAULT 0,
    quantity INT DEFAULT 0,

    CONSTRAINT uq_machine_daily UNIQUE(machine_id, log_date, shift_code)
);

-- Indexes
CREATE NONCLUSTERED INDEX idx_daily_date_shift
ON machine_daily(log_date, shift_code);

CREATE NONCLUSTERED INDEX idx_daily_machine_shift_date
ON machine_daily(machine_id, shift_code, log_date);
