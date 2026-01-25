ALTER TABLE operate_history
ADD log_date DATE;

CREATE INDEX idx_operate_staff_date
ON operate_history (staff_id, log_date)
INCLUDE (process_id, manufacturing_point, pg_time, start_time, stop_time);