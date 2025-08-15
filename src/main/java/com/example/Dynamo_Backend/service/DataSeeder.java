package com.example.Dynamo_Backend.service;

import org.springframework.stereotype.Component;

import com.example.Dynamo_Backend.entities.Admin;
import com.example.Dynamo_Backend.entities.DrawingCode;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.GroupKpi;
import com.example.Dynamo_Backend.entities.Machine;
import com.example.Dynamo_Backend.entities.MachineKpi;
import com.example.Dynamo_Backend.entities.Order;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.entities.Plan;
import com.example.Dynamo_Backend.entities.ProcessTime;
import com.example.Dynamo_Backend.entities.Staff;
import com.example.Dynamo_Backend.entities.StaffKpi;
import com.example.Dynamo_Backend.repository.AdminRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeProcessRepository;
import com.example.Dynamo_Backend.repository.DrawingCodeRepository;
import com.example.Dynamo_Backend.repository.GroupKpiRepository;
import com.example.Dynamo_Backend.repository.GroupRepository;
import com.example.Dynamo_Backend.repository.MachineKpiRepository;
import com.example.Dynamo_Backend.repository.MachineRepository;
import com.example.Dynamo_Backend.repository.OrderDetailRepository;
import com.example.Dynamo_Backend.repository.OrderRepository;
import com.example.Dynamo_Backend.repository.PlanRepository;
import com.example.Dynamo_Backend.repository.ProcessTimeRepository;
import com.example.Dynamo_Backend.repository.ReportRepository;
import com.example.Dynamo_Backend.repository.StaffKpiRepository;
import com.example.Dynamo_Backend.repository.StaffRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder {

        private final GroupRepository groupRepository;
        private final GroupKpiRepository groupKpiRepository;
        private final StaffRepository staffRepository;
        private final StaffKpiRepository staffKpiRepository;
        private final MachineRepository machineRepository;
        private final MachineKpiRepository machineKpiRepository;
        private final DrawingCodeRepository drawingCodeRepository;
        private final OrderRepository orderRepository;
        private final OrderDetailRepository orderDetailRepository;
        private final DrawingCodeProcessRepository processRepository;
        private final PlanRepository planRepository;
        private final AdminRepository adminRepository;
        private final ReportRepository reportRepository;
        private final ProcessTimeRepository processTimeRepository;

        long createdTimestamp = System.currentTimeMillis();

        @PostConstruct
        public void loadData() {
                if (groupRepository.count() > 0 || groupKpiRepository.count() > 0 || machineKpiRepository.count() > 0
                                || staffKpiRepository.count() > 0 || staffRepository.count() > 0
                                || machineRepository.count() > 0 || drawingCodeRepository.count() > 0
                                || orderRepository.count() > 0 || orderDetailRepository.count() > 0
                                || processRepository.count() > 0 || planRepository.count() > 0
                                || reportRepository.count() > 0 || adminRepository.count() > 0) {
                        System.out.println("🟡 Skipping DataLoader: data already exists.");
                        return;
                } else {
                        Admin admin = new Admin();
                        admin.setEmail("phuonganh@gmail.com");
                        admin.setPassword("123456");
                        admin.setCreatedDate(createdTimestamp);
                        admin.setUpdatedDate(createdTimestamp);
                        adminRepository.save(admin);

                        Admin admin1 = new Admin();
                        admin1.setEmail("phuongem@gmail.com");
                        admin1.setPassword("123456");
                        admin1.setCreatedDate(createdTimestamp);
                        admin1.setUpdatedDate(createdTimestamp);
                        adminRepository.save(admin1);

                        Group group1 = new Group();
                        group1.setGroupName("Group 1");
                        group1.setGroupType("staff");
                        group1.setCreatedDate(createdTimestamp);
                        group1.setUpdatedDate(createdTimestamp);
                        groupRepository.save(group1);

                        Group group2 = new Group();
                        group2.setGroupName("Group 2");
                        group2.setGroupType("staff");
                        group2.setCreatedDate(createdTimestamp);
                        group2.setUpdatedDate(createdTimestamp);
                        groupRepository.save(group2);

                        Group group3 = new Group();
                        group3.setGroupName("Group 3");
                        group3.setGroupType("staff");
                        group3.setCreatedDate(createdTimestamp);
                        group3.setUpdatedDate(createdTimestamp);
                        groupRepository.save(group3);

                        Group group4 = new Group();
                        group4.setGroupName("Group 4");
                        group4.setGroupType("staff");
                        group4.setCreatedDate(createdTimestamp);
                        group4.setUpdatedDate(createdTimestamp);
                        groupRepository.save(group4);

                        Staff staff1 = new Staff();
                        staff1.setStaffId(1001);
                        staff1.setStaffName("Văn A");
                        staff1.setShortName("A");
                        staff1.setStaffOffice("Sản xuất");
                        staff1.setStaffSection("Giai công tinh");
                        staff1.setStatus(1);
                        staff1.setCreatedDate(createdTimestamp);
                        staff1.setUpdatedDate(createdTimestamp);
                        staffRepository.save(staff1);

                        Staff staff2 = new Staff();
                        staff2.setStaffId(1002);
                        staff2.setStaffName("Văn B");
                        staff2.setShortName("B");
                        staff2.setStaffOffice("Sản xuất");
                        staff2.setStaffSection("Giai công tinh");
                        staff2.setStatus(1);
                        staff2.setCreatedDate(createdTimestamp);
                        staff2.setUpdatedDate(createdTimestamp);
                        staffRepository.save(staff2);

                        Staff staff3 = new Staff();
                        staff3.setStaffId(1003);
                        staff3.setStaffName("Văn C");
                        staff3.setShortName("C");
                        staff3.setStaffOffice("Sản xuất");
                        staff3.setStaffSection("Giai công tinh");
                        staff3.setStatus(1);
                        staff3.setCreatedDate(createdTimestamp);
                        staff3.setUpdatedDate(createdTimestamp);
                        staffRepository.save(staff3);

                        Staff staff4 = new Staff();
                        staff4.setStaffId(1004);
                        staff4.setStaffName("Văn D");
                        staff4.setShortName("D");
                        staff4.setStaffOffice("Sản xuất");
                        staff4.setStaffSection("Giai công tinh");
                        staff4.setStatus(1);
                        staff4.setCreatedDate(createdTimestamp);
                        staff4.setUpdatedDate(createdTimestamp);
                        staffRepository.save(staff4);

                        StaffKpi staffKpi1 = new StaffKpi();
                        staffKpi1.setGroup(group1);
                        staffKpi1.setMonth(8);
                        staffKpi1.setYear(2025);
                        staffKpi1.setStaff(staff1);
                        staffKpi1.setKpi((float) 100);
                        staffKpi1.setWorkGoal((float) 9);
                        staffKpi1.setPgTimeGoal((float) 9);
                        staffKpi1.setMachineTimeGoal((float) 10);
                        staffKpi1.setManufacturingPoint((float) 100);
                        staffKpi1.setOleGoal((float) 10);
                        staffKpi1.setCreatedDate(createdTimestamp);
                        staffKpi1.setUpdatedDate(createdTimestamp);
                        staffKpiRepository.save(staffKpi1);

                        StaffKpi staffKpi2 = new StaffKpi();
                        staffKpi2.setGroup(group2);
                        staffKpi2.setMonth(8);
                        staffKpi2.setYear(2025);
                        staffKpi2.setStaff(staff2);
                        staffKpi2.setKpi((float) 95);
                        staffKpi2.setWorkGoal((float) 8);
                        staffKpi2.setPgTimeGoal((float) 8.5);
                        staffKpi2.setMachineTimeGoal((float) 9);
                        staffKpi2.setManufacturingPoint((float) 98);
                        staffKpi2.setOleGoal((float) 9.5);
                        staffKpi2.setCreatedDate(createdTimestamp);
                        staffKpi2.setUpdatedDate(createdTimestamp);
                        staffKpiRepository.save(staffKpi2);

                        StaffKpi staffKpi3 = new StaffKpi();
                        staffKpi3.setGroup(group3);
                        staffKpi3.setMonth(8);
                        staffKpi3.setYear(2025);
                        staffKpi3.setStaff(staff3);
                        staffKpi3.setKpi((float) 88);
                        staffKpi3.setWorkGoal((float) 7.5);
                        staffKpi3.setPgTimeGoal((float) 7);
                        staffKpi3.setMachineTimeGoal((float) 8);
                        staffKpi3.setManufacturingPoint((float) 90);
                        staffKpi3.setOleGoal((float) 8);
                        staffKpi3.setCreatedDate(createdTimestamp);
                        staffKpi3.setUpdatedDate(createdTimestamp);
                        staffKpiRepository.save(staffKpi3);

                        StaffKpi staffKpi4 = new StaffKpi();
                        staffKpi4.setGroup(group4);
                        staffKpi4.setMonth(7);
                        staffKpi4.setYear(2025);
                        staffKpi4.setStaff(staff4);
                        staffKpi4.setKpi((float) 92);
                        staffKpi4.setWorkGoal((float) 8.8);
                        staffKpi4.setPgTimeGoal((float) 8.2);
                        staffKpi4.setMachineTimeGoal((float) 9.1);
                        staffKpi4.setManufacturingPoint((float) 95);
                        staffKpi4.setOleGoal((float) 9);
                        staffKpi4.setCreatedDate(createdTimestamp);
                        staffKpi4.setUpdatedDate(createdTimestamp);
                        staffKpiRepository.save(staffKpi4);

                        Machine machine1 = new Machine();
                        machine1.setMachineName("I-20");
                        machine1.setMachineOffice("Insert");
                        machine1.setMachineType("Phay CNC");
                        machine1.setMachineGroup("Insert");
                        machine1.setCreatedDate(createdTimestamp);
                        machine1.setUpdatedDate(createdTimestamp);
                        machineRepository.save(machine1);

                        Machine machine2 = new Machine();
                        machine2.setMachineName("L-30");
                        machine2.setMachineOffice("Lathe");
                        machine2.setMachineType("Tiện CNC");
                        machine2.setMachineGroup("Lathe");
                        machine2.setCreatedDate(createdTimestamp);
                        machine2.setUpdatedDate(createdTimestamp);
                        machineRepository.save(machine2);

                        Machine machine3 = new Machine();
                        machine3.setMachineName("M-50");
                        machine3.setMachineOffice("Milling");
                        machine3.setMachineType("Phay CNC");
                        machine3.setMachineGroup("Milling");
                        machine3.setCreatedDate(createdTimestamp);
                        machine3.setUpdatedDate(createdTimestamp);
                        machineRepository.save(machine3);

                        Machine machine4 = new Machine();
                        machine4.setMachineName("D-10");
                        machine4.setMachineOffice("Drilling");
                        machine4.setMachineType("Khoan CNC");
                        machine4.setMachineGroup("Drilling");
                        machine4.setCreatedDate(createdTimestamp);
                        machine4.setUpdatedDate(createdTimestamp);
                        machineRepository.save(machine4);

                        Machine machine5 = new Machine();
                        machine5.setMachineName("C-25");
                        machine5.setMachineOffice("Cutting");
                        machine5.setMachineType("Cắt CNC");
                        machine5.setMachineGroup("Cutting");
                        machine5.setCreatedDate(createdTimestamp);
                        machine5.setUpdatedDate(createdTimestamp);
                        machineRepository.save(machine5);

                        MachineKpi machineKpi1 = new MachineKpi();
                        machineKpi1.setGroup(group4);
                        machineKpi1.setMachine(machine5);
                        machineKpi1.setMonth(8);
                        machineKpi1.setYear(2025);
                        machineKpi1.setMachineMiningTarget((float) 3);
                        machineKpi1.setOee((float) 5);
                        machineKpi1.setCreatedDate(createdTimestamp);
                        machineKpi1.setUpdatedDate(createdTimestamp);
                        machineKpiRepository.save(machineKpi1);

                        MachineKpi machineKpi2 = new MachineKpi();
                        machineKpi2.setGroup(group4);
                        machineKpi2.setMachine(machine1);
                        machineKpi2.setMonth(8);
                        machineKpi2.setYear(2025);
                        machineKpi2.setMachineMiningTarget((float) 4);
                        machineKpi2.setOee((float) 6);
                        machineKpi2.setCreatedDate(createdTimestamp);
                        machineKpi2.setUpdatedDate(createdTimestamp);
                        machineKpiRepository.save(machineKpi2);

                        MachineKpi machineKpi3 = new MachineKpi();
                        machineKpi3.setGroup(group4);
                        machineKpi3.setMachine(machine2);
                        machineKpi3.setMonth(8);
                        machineKpi3.setYear(2024); // previous year to test fallback
                        machineKpi3.setMachineMiningTarget((float) 5);
                        machineKpi3.setOee((float) 7);
                        machineKpi3.setCreatedDate(createdTimestamp);
                        machineKpi3.setUpdatedDate(createdTimestamp);
                        machineKpiRepository.save(machineKpi3);

                        MachineKpi machineKpi4 = new MachineKpi();
                        machineKpi4.setGroup(group4);
                        machineKpi4.setMachine(machine3);
                        machineKpi4.setMonth(7); // previous month
                        machineKpi4.setYear(2025);
                        machineKpi4.setMachineMiningTarget((float) 6);
                        machineKpi4.setOee((float) 8);
                        machineKpi4.setCreatedDate(createdTimestamp);
                        machineKpi4.setUpdatedDate(createdTimestamp);
                        machineKpiRepository.save(machineKpi4);

                        MachineKpi machineKpi5 = new MachineKpi();
                        machineKpi5.setGroup(group4);
                        machineKpi5.setMachine(machine4);
                        machineKpi5.setMonth(8);
                        machineKpi5.setYear(2025);
                        machineKpi5.setMachineMiningTarget((float) 7);
                        machineKpi5.setOee((float) 9);
                        machineKpi5.setCreatedDate(createdTimestamp);
                        machineKpi5.setUpdatedDate(createdTimestamp);
                        machineKpiRepository.save(machineKpi5);

                        DrawingCode drawingCode1 = new DrawingCode();
                        drawingCode1.setDrawingCodeName("9927-V2");
                        drawingCode1.setStatus(1);
                        drawingCode1.setCreatedDate(createdTimestamp);
                        drawingCode1.setUpdatedDate(createdTimestamp);
                        drawingCodeRepository.save(drawingCode1);

                        DrawingCode drawingCode2 = new DrawingCode();
                        drawingCode2.setDrawingCodeName("9927-V3");
                        drawingCode2.setStatus(1);
                        drawingCode2.setCreatedDate(createdTimestamp);
                        drawingCode2.setUpdatedDate(createdTimestamp);
                        drawingCodeRepository.save(drawingCode2);

                        DrawingCode drawingCode3 = new DrawingCode();
                        drawingCode3.setDrawingCodeName("9927-V4");
                        drawingCode3.setStatus(1);
                        drawingCode3.setCreatedDate(createdTimestamp);
                        drawingCode3.setUpdatedDate(createdTimestamp);
                        drawingCodeRepository.save(drawingCode3);

                        DrawingCode drawingCode4 = new DrawingCode();
                        drawingCode4.setDrawingCodeName("9927-V5");
                        drawingCode4.setStatus(1);
                        drawingCode4.setCreatedDate(createdTimestamp);
                        drawingCode4.setUpdatedDate(createdTimestamp);
                        drawingCodeRepository.save(drawingCode4);

                        Order order = new Order();
                        order.setPoNumber("20250101");
                        order.setStatus(1);
                        order.setCreatedDate(createdTimestamp);
                        order.setUpdatedDate(createdTimestamp);
                        orderRepository.save(order);

                        Order order1 = new Order();
                        order1.setPoNumber("20250102");
                        order1.setStatus(1);
                        order1.setCreatedDate(createdTimestamp);
                        order1.setUpdatedDate(createdTimestamp);
                        orderRepository.save(order1);

                        Order order2 = new Order();
                        order2.setPoNumber("20250103");
                        order2.setStatus(1);
                        order2.setCreatedDate(createdTimestamp);
                        order2.setUpdatedDate(createdTimestamp);
                        orderRepository.save(order2);

                        Order order3 = new Order();
                        order3.setPoNumber("20250104");
                        order3.setStatus(1);
                        order3.setCreatedDate(createdTimestamp);
                        order3.setUpdatedDate(createdTimestamp);
                        orderRepository.save(order3);

                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order3);
                        orderDetail.setDrawingCode(drawingCode4);
                        orderDetail.setManagerGroup(group4);
                        orderDetail.setQuantity(10);
                        orderDetail.setOrderType("SP_Chính");
                        orderDetail.setOrderCode(order3.getPoNumber() + "_" + drawingCode1.getDrawingCodeName());
                        orderDetail.setPgTimeGoal((float) 100);
                        orderDetail.setCreatedDate(createdTimestamp);
                        orderDetail.setUpdatedDate(createdTimestamp);
                        orderDetailRepository.save(orderDetail);

                        OrderDetail orderDetail2 = new OrderDetail();
                        orderDetail2.setOrder(order3);
                        orderDetail2.setDrawingCode(drawingCode2);
                        orderDetail2.setManagerGroup(group4);
                        orderDetail2.setQuantity(15);
                        orderDetail2.setOrderType("SP_Chính");
                        orderDetail2.setOrderCode(order3.getPoNumber() + "_" + drawingCode2.getDrawingCodeName());
                        orderDetail2.setPgTimeGoal((float) 120);
                        orderDetail2.setCreatedDate(createdTimestamp);
                        orderDetail2.setUpdatedDate(createdTimestamp);
                        orderDetailRepository.save(orderDetail2);

                        OrderDetail orderDetail3 = new OrderDetail();
                        orderDetail3.setOrder(order3);
                        orderDetail3.setDrawingCode(drawingCode3);
                        orderDetail3.setManagerGroup(group4);
                        orderDetail3.setQuantity(8);
                        orderDetail3.setOrderType("SP_Phụ");
                        orderDetail3.setOrderCode(order3.getPoNumber() + "_" + drawingCode3.getDrawingCodeName());
                        orderDetail3.setPgTimeGoal((float) 80);
                        orderDetail3.setCreatedDate(createdTimestamp);
                        orderDetail3.setUpdatedDate(createdTimestamp);
                        orderDetailRepository.save(orderDetail3);

                        OrderDetail orderDetail4 = new OrderDetail();
                        orderDetail4.setOrder(order3);
                        orderDetail4.setDrawingCode(drawingCode1);
                        orderDetail4.setManagerGroup(group4);
                        orderDetail4.setQuantity(20);
                        orderDetail4.setOrderType("SP_Chính");
                        orderDetail4.setOrderCode(
                                        order3.getPoNumber() + "_" + drawingCode1.getDrawingCodeName() + "_BATCH2");
                        orderDetail4.setPgTimeGoal((float) 150);
                        orderDetail4.setCreatedDate(createdTimestamp);
                        orderDetail4.setUpdatedDate(createdTimestamp);
                        orderDetailRepository.save(orderDetail4);

                        DrawingCodeProcess drawingCodeProcess = new DrawingCodeProcess();
                        drawingCodeProcess.setProcessType("Du bi");
                        drawingCodeProcess.setOrderDetail(orderDetail4);
                        drawingCodeProcess.setManufacturingPoint(10);
                        drawingCodeProcess.setPartNumber(1);
                        drawingCodeProcess.setStepNumber(1);
                        drawingCodeProcess.setPgTime((long) 180);
                        drawingCodeProcess.setMachine(machine5);
                        drawingCodeProcess.setIsPlan(1);
                        drawingCodeProcess.setStatus(1);
                        drawingCodeProcess.setProcessStatus(1);
                        drawingCodeProcess.setCreatedDate(createdTimestamp);
                        drawingCodeProcess.setUpdatedDate(createdTimestamp);
                        processRepository.save(drawingCodeProcess);

                        DrawingCodeProcess drawingCodeProcess2 = new DrawingCodeProcess();
                        drawingCodeProcess2.setProcessType("SP_Chinh");
                        drawingCodeProcess2.setOrderDetail(orderDetail2);
                        drawingCodeProcess2.setManufacturingPoint(15);
                        drawingCodeProcess2.setPartNumber(2);
                        drawingCodeProcess2.setStepNumber(1);
                        drawingCodeProcess2.setPgTime(200L);
                        drawingCodeProcess2.setIsPlan(1);
                        drawingCodeProcess2.setStatus(1);
                        drawingCodeProcess2.setProcessStatus(1);
                        drawingCodeProcess2.setCreatedDate(createdTimestamp);
                        drawingCodeProcess2.setUpdatedDate(createdTimestamp);
                        processRepository.save(drawingCodeProcess2);

                        DrawingCodeProcess drawingCodeProcess3 = new DrawingCodeProcess();
                        drawingCodeProcess3.setProcessType("NG_Chay lai");
                        drawingCodeProcess3.setOrderDetail(orderDetail3);
                        drawingCodeProcess3.setManufacturingPoint(12);
                        drawingCodeProcess3.setPartNumber(3);
                        drawingCodeProcess3.setStepNumber(2);
                        drawingCodeProcess3.setPgTime(150L);
                        drawingCodeProcess3.setIsPlan(0); // Not planned yet
                        drawingCodeProcess3.setMachine(machine2);
                        drawingCodeProcess3.setStatus(1); // Not started
                        drawingCodeProcess3.setProcessStatus(1);
                        drawingCodeProcess3.setCreatedDate(createdTimestamp);
                        drawingCodeProcess3.setUpdatedDate(createdTimestamp);
                        processRepository.save(drawingCodeProcess3);

                        DrawingCodeProcess drawingCodeProcess4 = new DrawingCodeProcess();
                        drawingCodeProcess4.setProcessType("LK-Do ga");
                        drawingCodeProcess4.setOrderDetail(orderDetail);
                        drawingCodeProcess4.setManufacturingPoint(8);
                        drawingCodeProcess4.setPartNumber(4);
                        drawingCodeProcess4.setStepNumber(3);
                        drawingCodeProcess4.setPgTime(100L);
                        drawingCodeProcess4.setIsPlan(1);
                        drawingCodeProcess4.setStatus(1);
                        drawingCodeProcess4.setProcessStatus(1);
                        drawingCodeProcess4.setCreatedDate(createdTimestamp);
                        drawingCodeProcess4.setUpdatedDate(createdTimestamp);
                        processRepository.save(drawingCodeProcess4);

                        Plan plan = new Plan();
                        plan.setInProgress(1);
                        plan.setStatus(1);
                        plan.setDrawingCodeProcess(drawingCodeProcess4);
                        plan.setMachine(machine4);
                        plan.setPlanner(admin1);
                        plan.setStaff(staff4);
                        plan.setStartTime(createdTimestamp);
                        plan.setEndTime(createdTimestamp);
                        plan.setCreatedDate(createdTimestamp);
                        plan.setUpdatedDate(createdTimestamp);
                        planRepository.save(plan);

                        Plan plan2 = new Plan();
                        plan2.setInProgress(1);
                        plan2.setStatus(1);
                        plan2.setDrawingCodeProcess(drawingCodeProcess2);
                        plan2.setMachine(machine2);
                        plan2.setPlanner(admin1);
                        plan2.setStaff(staff1);
                        plan2.setStartTime(createdTimestamp);
                        plan2.setEndTime(createdTimestamp);
                        plan2.setCreatedDate(createdTimestamp);
                        plan2.setUpdatedDate(createdTimestamp);
                        planRepository.save(plan2);

                        Plan plan3 = new Plan();
                        plan3.setInProgress(1);
                        plan3.setStatus(1);
                        plan3.setMachine(machine4);
                        plan3.setDrawingCodeProcess(drawingCodeProcess3);
                        plan3.setStaff(staff3);
                        plan3.setStartTime(createdTimestamp);
                        plan3.setEndTime(createdTimestamp);
                        plan3.setCreatedDate(createdTimestamp);
                        plan3.setUpdatedDate(createdTimestamp);
                        planRepository.save(plan3);

                        Plan plan4 = new Plan();
                        plan4.setInProgress(1);
                        plan4.setStatus(1);
                        plan4.setDrawingCodeProcess(drawingCodeProcess);
                        plan4.setMachine(machine4);
                        plan4.setPlanner(admin);
                        plan4.setStaff(staff2);
                        plan4.setStartTime(createdTimestamp);
                        plan4.setEndTime(createdTimestamp);
                        plan4.setCreatedDate(createdTimestamp);
                        plan4.setUpdatedDate(createdTimestamp);
                        planRepository.save(plan4);

                        // For Group Efficiency
                        GroupKpi groupKpi = new GroupKpi();
                        groupKpi.setGroup(group4); // reference to your Group entity
                        groupKpi.setIsMonth(0); // 0 for week, 1 for month
                        groupKpi.setWeek(4); // week number
                        groupKpi.setMonth(7); // July
                        groupKpi.setYear(2025);
                        groupKpi.setWorkingHourGoal(40);
                        groupKpi.setWorkingHourDifference(0);
                        groupKpi.setCreatedDate(createdTimestamp);
                        groupKpi.setUpdatedDate(createdTimestamp);
                        groupKpi.setOffice("D-11");
                        groupKpi.setWorkingHour(40); // or any float value
                        groupKpiRepository.save(groupKpi);

                        DrawingCodeProcess process = new DrawingCodeProcess();
                        process.setMachine(machine3); // reference to your Machine entity
                        process.setStartTime(1753056000000L); // 2025-07-21
                        process.setEndTime(1753315200000L); // 2025-07-24
                        process.setIsPlan(0);
                        process.setStatus(1);
                        process.setProcessStatus(3);
                        process.setPartNumber(1);
                        process.setStepNumber(1);
                        process.setManufacturingPoint(40);
                        process.setPgTime(30L);
                        process.setCreatedDate(createdTimestamp);
                        process.setUpdatedDate(createdTimestamp);
                        process.setProcessType("chính");
                        process.setOrderDetail(orderDetail);
                        processRepository.save(process);

                        ProcessTime processTime = new ProcessTime();
                        processTime.setDrawingCodeProcess(process); // reference to DrawingCodeProcess
                        processTime.setRunTime(10f);
                        processTime.setPgTime(5f);
                        processTime.setOffsetTime(1f);
                        processTime.setSpanTime(10f);
                        processTime.setStopTime(0f);

                        processTimeRepository.save(processTime);

                }
        }

}
