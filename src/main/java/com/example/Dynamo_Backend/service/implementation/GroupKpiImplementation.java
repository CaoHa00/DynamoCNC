package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.GroupKpiDto;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
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
                .orElseThrow(() -> new RuntimeException("Group is not found:" + groupKpiDto.getGroupId()));

        groupKpi.setGroup(group);
        groupKpi.setCreatedDate(createdTimestamp);
        groupKpi.setUpdatedDate(createdTimestamp);

        GroupKpi saveGroupKpi = groupKpiRepository.save(groupKpi);
        return GroupKpiMapper.mapToGroupKpiDto(saveGroupKpi);
    }

    @Override
    public GroupKpiDto updateGroupKpi(Integer Id, GroupKpiDto groupKpiDto) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Group group = groupRepository.findById(groupKpiDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group is not found:" + groupKpiDto.getGroupId()));

        groupKpi.setGroup(group);

        groupKpi.setUpdatedDate(updatedTimestamp);
        groupKpi.setWeek(groupKpiDto.getWeek());
        groupKpi.setMonth(groupKpiDto.getMonth());
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
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
        return GroupKpiMapper.mapToGroupKpiDto(groupKpi);
    }

    @Override
    public void deleteGroupKpi(Integer Id) {
        GroupKpi groupKpi = groupKpiRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("GroupKpi is not found:" + Id));
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
                if (row.getRowNum() == 0)
                    continue;
                GroupKpi groupKpi = new GroupKpi();

                Cell dateCell = row.getCell(0);
                LocalDate localDate;
                if (dateCell.getCellType() == CellType.NUMERIC) {
                    localDate = dateCell.getLocalDateTimeCellValue().toLocalDate();
                } else if (dateCell.getCellType() == CellType.STRING) {
                    String dateStr = dateCell.getStringCellValue().trim();
                    DateTimeFormatter formatter;
                    if (dateStr.contains("/")) {
                        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    } else {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    }
                    localDate = LocalDate.parse(dateStr, formatter);
                } else {
                    throw new RuntimeException("Unsupported cell type for date: " + dateCell.getCellType());
                }
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int week = localDate.get(java.time.temporal.WeekFields.ISO.weekOfMonth());
                groupKpi.setYear(year);
                groupKpi.setMonth(month);
                groupKpi.setWeek(week);
                groupKpi.setOffice(row.getCell(2).getStringCellValue());
                groupKpi.setWorkingHourGoal((int) row.getCell(3).getNumericCellValue());
                groupKpi.setWorkingHourDifference((int) row.getCell(4).getNumericCellValue());
                groupKpi.setWorkingHour((int) row.getCell(5).getNumericCellValue());
                long createdTimestamp = System.currentTimeMillis();
                groupKpi.setCreatedDate(createdTimestamp);
                groupKpi.setUpdatedDate(createdTimestamp);
                Group group = groupRepository.findById(row.getCell(6).getStringCellValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Group is not found when add group KPI by excel:"
                                        + row.getCell(6).getStringCellValue()));
                groupKpi.setGroup(group);
                groupKpiList.add(groupKpi);
            }
            groupKpiRepository.saveAll(groupKpiList);
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to import group KPI from Excel file: " + e.getMessage());
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
                if (row.getRowNum() == 0)
                    continue;
                GroupKpi groupKpi = new GroupKpi();
                groupKpi.setYear((int) row.getCell(1).getNumericCellValue());
                groupKpi.setMonth((int) row.getCell(2).getNumericCellValue());
                groupKpi.setOffice(row.getCell(2).getStringCellValue());
                groupKpi.setWorkingHourGoal((int) row.getCell(3).getNumericCellValue());
                groupKpi.setWorkingHourDifference((int) row.getCell(4).getNumericCellValue());
                groupKpi.setWorkingHour((int) row.getCell(5).getNumericCellValue());
                long createdTimestamp = System.currentTimeMillis();
                groupKpi.setCreatedDate(createdTimestamp);
                groupKpi.setUpdatedDate(createdTimestamp);
                Group group = groupRepository.findById(row.getCell(6).getStringCellValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Group is not found when add group KPI by excel:"
                                        + row.getCell(6).getStringCellValue()));
                groupKpi.setGroup(group);
                groupKpiList.add(groupKpi);
            }
            groupKpiRepository.saveAll(groupKpiList);
            workbook.close();
            inputStream.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to import group KPI from Excel file: " + e.getMessage());
        }
    }

}
