ALTER TABLE drawing_code_process
ADD 
    shift_code VARCHAR(20),
    log_date DATE;

CREATE INDEX ix_process_machine_type
ON drawing_code_process (machine_id, process_type);
