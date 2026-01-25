UPDATE report
SET shift_code = 'FULL'
WHERE shift_code IS NULL;

ALTER TABLE report
ALTER COLUMN shift_code VARCHAR(20) NOT NULL;