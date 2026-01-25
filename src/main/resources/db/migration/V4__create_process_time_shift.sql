CREATE TABLE process_shift (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    machine_id INT NOT NULL,
    log_date DATE NOT NULL,
    shift_code NVARCHAR(20) NOT NULL,

    main_product_pg_seconds BIGINT DEFAULT 0,
    electric_pg_seconds BIGINT DEFAULT 0,
    other_pg_seconds BIGINT DEFAULT 0,
    span_seconds BIGINT DEFAULT 0,
    expected_seconds BIGINT DEFAULT 0,

    quantity INT DEFAULT 0

    CONSTRAINT uq_process_shift UNIQUE (machine_id, log_date, shift_code)
);

CREATE UNIQUE INDEX ux_process_shift_machine_date_shift
ON process_shift (machine_id, log_date, shift_code);