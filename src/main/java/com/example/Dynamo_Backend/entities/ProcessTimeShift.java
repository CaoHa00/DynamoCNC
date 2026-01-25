package com.example.Dynamo_Backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "process_shift")
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTimeShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer machineId;
    private LocalDate logDate;
    private String shiftCode;

    // sử dụng cho tùng process nếu cần;
    // private Long mainProductSeconds = 0L;
    // private Long reRunSeconds = 0L;
    // private Long lkSeconds = 0L;
    private Long spanSeconds = 0L;
    private Long expectedSeconds = 0L;

    // tính cho hiệu suất lấy từ processTime pg time và loại process
    private Long mainProductPgSeconds = 0L;
    private Long electricPgSeconds = 0L;
    private Long otherPgSeconds = 0L;
    private Integer quantity = 0;
}
