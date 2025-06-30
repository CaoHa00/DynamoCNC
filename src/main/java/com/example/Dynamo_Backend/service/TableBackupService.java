package com.example.Dynamo_Backend.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TableBackupService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 0 1 * ?") // Runs at 12:00 AM on the 1st of every month
    public void backupAndClearTable() {
        copyTable("stats");
        copyTable("operate_history");
    }

    public void copyTable(String table) {
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        String year = String.valueOf(LocalDate.now().getYear());

        String backupTable = table + "_" + year + "_" + month;

        // 1. Copy table to backup table
        String copySql = "SELECT * INTO " + backupTable + " FROM " + table;
        jdbcTemplate.execute(copySql);

        // 2. Clear original table
        String clearSql = "TRUNCATE TABLE " + table;
        jdbcTemplate.execute(clearSql);
    }
}
