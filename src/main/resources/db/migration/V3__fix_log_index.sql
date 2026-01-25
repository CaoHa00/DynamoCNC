CREATE NONCLUSTERED INDEX idx_log_machine_time
ON log (machine_id, time_stamp);