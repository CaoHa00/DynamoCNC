package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Report;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.GroupKpiMapper;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.service.GroupKpiService;

@Service
public class GroupKpiImplementation implements GroupKpiService {
    @Autowired
    GroupKpiRepository groupKpiRepository;
    @Autowired
    GroupRepository groupRepository;

    @Override
    public GroupKpiDto addGroupKpi(GroupKpiDto groupKpiDto) {
        long createdTimestamp = System.currentTimeMillis();
        GroupKpi groupKpi = GroupKpiMapper.mapToGroupKpi(groupKpiDto);
        Group group = groupRepository.findById(groupKpiDto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group is not found:" + groupKpiDto.getGroupId()));

        groupKpi.setGroup(group);
        groupKpi.setCreatedDate(createdTimestamp);
        groupKpi.setUpdatedDate(createdTimestamp);

        GroupKpi saveGroupKpi = groupKpiRepository.save(groupKpi);
        return GroupKpiMapper.mapToGroupKpiDto(saveGroupKpi);
    }

    @Override
    public GroupKpiDto updateGroupKpi(Integer Id, GroupKpiDto groupKpiDto) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupKpi is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Group group = groupRepository.findById(groupKpiDto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group is not found:" + groupKpiDto.getGroupId()));

        groupKpi.setGroup(group);

        groupKpi.setUpdatedDate(updatedTimestamp);
        if (groupKpiDto.getIsMonth() == 0) {
            groupKpi.setWeek(groupKpiDto.getWeek());
        } else {
            groupKpi.setMonth(groupKpiDto.getMonth());
        }
        groupKpi.setYear(groupKpiDto.getYear());
        groupKpi.setOffice(groupKpiDto.getOffice());
        groupKpi.setWorkingHourGoal(groupKpiDto.getWorkingHourGoal());
        groupKpi.setWorkingHourDifference(groupKpiDto.getWorkingHourDifference());
        groupKpi.setWorkingHour(groupKpiDto.getWorkingHour());

        GroupKpi saveGroupKpi = groupKpiRepository.save(groupKpi);
        return GroupKpiMapper.mapToGroupKpiDto(saveGroupKpi);
    }

    @Override
    public GroupKpiDto getGroupKpiById(Integer Id) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupKpi is not found:" + Id));
        return GroupKpiMapper.mapToGroupKpiDto(groupKpi);
    }

    @Override
    public void deleteGroupKpi(Integer Id) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupKpi is not found:" + Id));
        groupKpiRepository.delete(groupKpi);
    }

    @Override
    public List<GroupKpiDto> getGroupKpis() {
        List<GroupKpi> groupKpis = groupKpiRepository.findAll();
        return groupKpis.stream().map(GroupKpiMapper::mapToGroupKpiDto).toList();
    }

    @Override
    public void importGroupKpiWeekFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<GroupKpi> groupKpiList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue;

                boolean missing = false;
                for (int i = 2; i <= 7; i++) {
                    if (row.getCell(i) == null) {
                        missing = true;
                        break;
                    }
                }
                if (missing)
                    continue;

                GroupKpi groupKpi = new GroupKpi();

                Cell dateCell = row.getCell(2);
                if (dateCell == null || dateCell.getCellType() == CellType.BLANK) {
                    continue;
                }
                LocalDate localDate;
                if (dateCell.getCellType() == CellType.NUMERIC) {
                    localDate = dateCell.getLocalDateTimeCellValue().toLocalDate();
                } else if (dateCell.getCellType() == CellType.STRING) {
                    String dateStr = dateCell.getStringCellValue().trim();
                    DateTimeFormatter formatter;
                    if (dateStr.contains("/")) {
                        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    } else {
                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    }
                    localDate = LocalDate.parse(dateStr, formatter);
                } else {
                    throw new BusinessException("Unsupported cell type for date: " + dateCell.getCellType());
                }
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int week = localDate.get(java.time.temporal.WeekFields.ISO.weekOfMonth());
                groupKpi.setYear(year);
                groupKpi.setMonth(month);
                groupKpi.setWeek(week);
                groupKpi.setIsMonth(0);
                groupKpi.setOffice(row.getCell(3).getStringCellValue());
                String groupName = row.getCell(4).getStringCellValue();
                Optional<Group> groupOpt = groupRepository.findByGroupName(groupName);
                if (groupOpt.isEmpty()) {
                    // Optionally log: System.out.println("Group not found: " + groupName);
                    continue; // Skip this row if group not found
                }
                groupKpi.setGroup(groupOpt.get());
                groupKpi.setWorkingHourGoal((float) row.getCell(5).getNumericCellValue());
                groupKpi.setWorkingHourDifference((float) row.getCell(6).getNumericCellValue());
                groupKpi.setWorkingHour((float) row.getCell(7).getNumericCellValue());
                long createdTimestamp = System.currentTimeMillis();
                groupKpi.setCreatedDate(createdTimestamp);
                groupKpi.setUpdatedDate(createdTimestamp);
                groupKpiList.add(groupKpi);
            }
            groupKpiRepository.saveAll(groupKpiList);
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            throw new BusinessException("Failed to import group KPI from Excel file: " + e.getMessage());
        }
    }

    @Override
    public void importGroupKpiMonthFromExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<GroupKpi> groupKpiList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() < 6)
                    continue;

                boolean missing = false;
                for (int i = 2; i <= 8; i++) {
                    if (row.getCell(i) == null) {
                        missing = true;
                        break;
                    }
                }
                if (missing)
                    continue;

                GroupKpi groupKpi = new GroupKpi();
                groupKpi.setYear((int) row.getCell(2).getNumericCellValue());
                groupKpi.setMonth((int) row.getCell(3).getNumericCellValue());
                groupKpi.setWeek(0);
                groupKpi.setIsMonth(1);
                groupKpi.setOffice(row.getCell(4).getStringCellValue());
                String groupName = row.getCell(5).getStringCellValue();
                Optional<Group> groupOpt = groupRepository.findByGroupName(groupName);
                if (groupOpt.isEmpty()) {
                    // Optionally log: System.out.println("Group not found: " + groupName);
                    continue; // Skip this row if group not found
                }
                groupKpi.setGroup(groupOpt.get());
                groupKpi.setWorkingHourGoal((float) row.getCell(6).getNumericCellValue());
                groupKpi.setWorkingHourDifference((float) row.getCell(7).getNumericCellValue());
                groupKpi.setWorkingHour((float) row.getCell(8).getNumericCellValue());
                long createdTimestamp = System.currentTimeMillis();
                groupKpi.setCreatedDate(createdTimestamp);
                groupKpi.setUpdatedDate(createdTimestamp);

                groupKpiList.add(groupKpi);
            }
            groupKpiRepository.saveAll(groupKpiList);
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            throw new BusinessException("Failed to import group KPI from Excel file: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 7 1 * ?") // Runs at 7:00 AM on the 1st of every month
    public void createMonthlyGroupKpis() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int week = now.get(WeekFields.ISO.weekOfYear());

        // Previous month
        int prevYear = (month == 1) ? year - 1 : year;
        int prevMonth = (month == 1) ? 12 : month - 1;

        List<Group> groups = groupRepository.findAll();
        int createdCount = 0;

        for (Group group : groups) {
            // Check if KPI already exists for current month
            GroupKpi existing = groupKpiRepository.findByGroup_GroupIdAndMonthAndYearAndIsMonth(group.getGroupId(),
                    month, year, 1).orElse(null);
            if (existing != null) {
                continue; // Skip if already exists
            }

            // Fetch previous month's KPI
            GroupKpi prevKpi = groupKpiRepository.findByGroup_GroupIdAndMonthAndYearAndIsMonth(group.getGroupId(),
                    prevMonth, prevYear, 1).orElse(null);

            GroupKpiDto dto = new GroupKpiDto();
            dto.setGroupId(group.getGroupId());
            dto.setYear(year);
            dto.setMonth(month);
            dto.setIsMonth(1);

            if (prevKpi != null) {
                dto.setOffice(prevKpi.getOffice());
                dto.setWorkingHourGoal(prevKpi.getWorkingHourGoal());
                dto.setWorkingHourDifference(prevKpi.getWorkingHourDifference());
                dto.setWorkingHour(0.0f);
            } else {
                dto.setOffice("Main Office");
                dto.setWorkingHourGoal(0.0f);
                dto.setWorkingHourDifference(0.0f);
                dto.setWorkingHour(0.0f);
            }

            try {
                addGroupKpi(dto);
                createdCount++;
            } catch (BusinessException e) {
                // Already exists or other business logic, skip
            }
        }

        System.out.println("Monthly Group KPI creation completed. Created " + createdCount + " new KPIs for " + year
                + "-" + month);
    }

    @Scheduled(cron = "0 0 7 ? * MON") // Runs at 7:00 AM every Monday
    public void createWeeklyGroupKpis() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int week = now.get(WeekFields.ISO.weekOfYear());

        // Previous week
        WeekFields wf = WeekFields.ISO;
        int prevYear = year;
        int prevWeek = week - 1;
        if (prevWeek == 0) {
            prevYear = year - 1;
            LocalDate lastDayPrevYear = LocalDate.of(prevYear, 12, 31);
            prevWeek = lastDayPrevYear.get(wf.weekOfYear());
        }

        List<Group> groups = groupRepository.findAll();
        int createdCount = 0;

        for (Group group : groups) {
            // Check if KPI already exists for current week
            GroupKpi existing = groupKpiRepository.findByGroup_GroupIdAndYearAndWeekAndIsMonth(group.getGroupId(), year,
                    week, 0).orElse(null);
            if (existing != null) {
                continue; // Skip if already exists
            }

            // Fetch previous week's KPI
            GroupKpi prevKpi = groupKpiRepository.findByGroup_GroupIdAndYearAndWeekAndIsMonth(group.getGroupId(),
                    prevYear, prevWeek, 0).orElse(null);

            GroupKpiDto dto = new GroupKpiDto();
            dto.setGroupId(group.getGroupId());
            dto.setYear(year);
            dto.setWeek(week);
            dto.setIsMonth(0);

            if (prevKpi != null) {
                dto.setOffice(prevKpi.getOffice());
                dto.setWorkingHourGoal(prevKpi.getWorkingHourGoal());
                dto.setWorkingHourDifference(prevKpi.getWorkingHourDifference());
                dto.setWorkingHour(0.0f);
            } else {
                dto.setOffice("Main Office");
                dto.setWorkingHourGoal(0.0f);
                dto.setWorkingHourDifference(0.0f);
                dto.setWorkingHour(0.0f);
            }

            try {
                addGroupKpi(dto);
                createdCount++;
            } catch (BusinessException e) {
                // Already exists or other business logic, skip
            }
        }

        System.out.println("Weekly Group KPI creation completed. Created " + createdCount + " new KPIs for " + year
                + "-Week" + week);
    }

    // @Scheduled(cron = "0 0 7 * * ?") // Runs every day at midnight to update
    // workingHour based on report hourDiff
    // public void updateDailyWorkingHours() {
    // LocalDate now = LocalDate.now();
    // int year = now.getYear();
    // int month = now.getMonthValue();
    // int week = now.get(WeekFields.ISO.weekOfYear());

    // List<Group> groups = groupRepository.findAll();

    // for (Group group : groups) {
    // // Calculate total hourDiff from reports for this group for the current day
    // long startOfDay =
    // now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
    // long endOfDay =
    // now.plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();

    // Integer totalHourDiff = group.getReports().stream()
    // .filter(report -> report.getDateTime() >= startOfDay && report.getDateTime()
    // < endOfDay)
    // .map(Report::getHourDiff)
    // .reduce(0, Integer::sum);

    // // Update monthly GroupKpi
    // GroupKpi monthlyKpi = groupKpiRepository
    // .findByGroup_GroupIdAndMonthAndYearAndIsMonth(group.getGroupId(), month,
    // year, 1)
    // .orElse(null);
    // if (monthlyKpi != null) {
    // monthlyKpi.setWorkingHour(monthlyKpi.getWorkingHour() +
    // totalHourDiff.floatValue());
    // monthlyKpi.setWorkingHourDifference(monthlyKpi.getWorkingHourGoal() -
    // monthlyKpi.getWorkingHour());
    // monthlyKpi.setUpdatedDate(System.currentTimeMillis());
    // groupKpiRepository.save(monthlyKpi);
    // }

    // // Update weekly GroupKpi
    // GroupKpi weeklyKpi = groupKpiRepository
    // .findByGroup_GroupIdAndYearAndWeekAndIsMonth(group.getGroupId(), year, week,
    // 0)
    // .orElse(null);
    // if (weeklyKpi != null) {
    // weeklyKpi.setWorkingHour(weeklyKpi.getWorkingHour() +
    // totalHourDiff.floatValue());
    // weeklyKpi.setWorkingHourDifference(weeklyKpi.getWorkingHourGoal() -
    // weeklyKpi.getWorkingHour());
    // weeklyKpi.setUpdatedDate(System.currentTimeMillis());
    // groupKpiRepository.save(weeklyKpi);
    // }
    // }

    // System.out.println("Daily working hours updated for all groups for date: " +
    // now.toString());
    // }

    @Override
    public List<GroupKpiDto> getGroupKpiByCurrentWeek() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGroupKpiByCurrentWeek'");
    }

}
