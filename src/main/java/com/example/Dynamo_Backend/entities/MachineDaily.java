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

@Table(name = "machine_daily", schema = "dbo")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineDaily {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Integer machineId;
        private LocalDate logDate;
        private String shiftCode;

        private Long runPgSeconds;
        private Long runOffsetSeconds;
        private Long emptySeconds;
        private Long stopSeconds;
        private Long errorSeconds;

        // tính cho cột lấy từ machine segment
        private Long mainProductSeconds;
        private Long reRunSeconds;
        private Long lkSeconds;
        private Long electricSeconds;
        private Long preparationSeconds;
        //
        private Long mainProductPgSeconds;
        private Long electricPgSeconds;
        private Long otherSeconds;
        private Long spanSeconds;
        private Long expectedSeconds;

        // sản lượng
        private Integer quantity;
}
