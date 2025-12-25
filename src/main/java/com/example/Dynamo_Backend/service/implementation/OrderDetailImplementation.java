package com.example.Dynamo_Backend.service.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Dynamo_Backend.dto.ProcessTimeSummaryDto;
import com.example.Dynamo_Backend.dto.RequestDto.DrawingCodeProcessResquestDto;
import com.example.Dynamo_Backend.dto.ResponseDto.ListOrderDetailStatus;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailResponseDto;
import com.example.Dynamo_Backend.dto.ResponseDto.OrderDetailStatus;
import com.example.Dynamo_Backend.dto.ResponseDto.PartProgressDto;
import com.example.Dynamo_Backend.dto.OrderDetailDto;
import com.example.Dynamo_Backend.entities.DrawingCodeProcess;
import com.example.Dynamo_Backend.entities.Group;
import com.example.Dynamo_Backend.entities.OrderDetail;
import com.example.Dynamo_Backend.exception.BusinessException;
import com.example.Dynamo_Backend.exception.ResourceNotFoundException;
import com.example.Dynamo_Backend.mapper.OrderDetailMapper;
import com.example.Dynamo_Backend.service.DrawingCodeProcessService;
import com.example.Dynamo_Backend.service.DrawingCodeService;
import com.example.Dynamo_Backend.service.OrderDetailService;
import com.example.Dynamo_Backend.service.OrderService;
import com.example.Dynamo_Backend.service.ProcessTimeSummaryService;
import com.example.Dynamo_Backend.repository.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderDetailImplementation implements OrderDetailService {
    public OrderDetailRepository orderDetailRepository;
    public DrawingCodeRepository drawingCodeRepository;
    DrawingCodeProcessRepository drawingCodeProcessRepository;
    public OrderRepository orderRepository;
    public OrderService orderService;
    public DrawingCodeService drawingCodeService;
    public GroupRepository groupRepository;
    public ProcessTimeSummaryService processTimeSummaryService;
    public DrawingCodeProcessService drawingCodeProcessService;

    @Override
    public OrderDetailDto addOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = OrderDetailMapper.mapToOrderDetail(orderDetailDto);
        // OrderDto order = orderService.getOrderById(orderDetailDto.getOrderId());
        // DrawingCodeDto DrawingCode =
        // drawingCodeService.getDrawingCodeById(orderDetailDto.getDrawingCodeId());
        // DrawingCode newDrawingCode = DrawingCodeMapper.mapToDrawingCode(DrawingCode);
        // Order newOrder = OrderMapper.mapToOrder(order);
        long createdTimestamp = System.currentTimeMillis();
        // String orderCode = newOrder.getPoNumber() + "_" +
        // newDrawingCode.getDrawingCodeName();
        Group managerGroup = groupRepository.findById(orderDetailDto.getManagerGroupId())
                .orElseGet(null);
        if (orderDetailRepository.existsByOrderCode(orderDetailDto.getOrderCode())) {
            throw new BusinessException("ID mã hàng: " + orderDetailDto.getOrderCode() + " đã tồn tại");
        }
        orderDetail.setManagerGroup(managerGroup);
        orderDetail.setCreatedDate(createdTimestamp);
        orderDetail.setUpdatedDate(createdTimestamp);
        orderDetail.setStatus(1);
        orderDetail.setProgress(1);

        OrderDetail saveOrderDetail = orderDetailRepository.save(orderDetail);
        int n = orderDetail.getNumberOfStep();
        for (int i = 1; i <= n; i++) {
            Integer partNumber = i;
            for (int j = 1; j <= orderDetail.getQuantity(); j++) {
                Integer stepNumber = j;
                DrawingCodeProcessResquestDto dto = new DrawingCodeProcessResquestDto();
                dto.setPartNumber(partNumber);
                dto.setIsPlan(0);
                dto.setStepNumber(stepNumber);
                dto.setOrderCode(saveOrderDetail.getOrderCode());
                dto.setProcessType(saveOrderDetail.getOrderType());
                drawingCodeProcessService.addDrawingCodeProcess(dto);
            }
        }
        return OrderDetailMapper.mapToOrderDetailDto(saveOrderDetail);
    }

    @Override
    public OrderDetailDto updateOrderDetail(String Id, OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = orderDetailRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderDetail is not found:" + Id));
        long updatedTimestamp = System.currentTimeMillis();
        Group managerGroup = groupRepository.findById(orderDetailDto.getManagerGroupId())
                .orElseGet(null);
        if (managerGroup != null) {
            orderDetail.setManagerGroup(managerGroup);
        }

        orderDetail.setUpdatedDate(updatedTimestamp);
        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setOrderCode(orderDetailDto.getOrderCode());
        orderDetail.setOrderType(orderDetailDto.getOrderType());
        orderDetail.setPgTimeGoal(orderDetailDto.getPgTimeGoal());
        if (orderDetailDto.getNumberOfSteps() >= orderDetail.getNumberOfStep()) {
            orderDetail.setNumberOfStep(orderDetailDto.getNumberOfSteps());
            int n = orderDetailDto.getNumberOfSteps() - orderDetail.getNumberOfStep();
            int i = 0;
            int k = orderDetailDto.getNumberOfSteps();
            while (i < n) {
                Integer partNumber = k;
                for (int j = 1; j <= orderDetail.getQuantity(); j++) {
                    Integer stepNumber = j;
                    DrawingCodeProcessResquestDto dto = new DrawingCodeProcessResquestDto();
                    dto.setPartNumber(partNumber);
                    dto.setIsPlan(0);
                    dto.setStepNumber(stepNumber);
                    dto.setOrderCode(orderDetail.getOrderCode());
                    dto.setProcessType(orderDetail.getOrderType());
                    drawingCodeProcessService.addDrawingCodeProcess(dto);
                }
                i++;
                k++;
            }
        } else {
            throw new ResourceNotFoundException("Không thể giảm số lượng nguyên công");
        }

        orderDetail.setOffice(orderDetailDto.getOffice());
        orderDetail.setProgress(orderDetailDto.getProgress());

        OrderDetail updatedOrderDetail = orderDetailRepository.save(orderDetail);
        if (updatedOrderDetail.getProgress() == 3) {
            drawingCodeProcessService.updateProcessStatus(updatedOrderDetail.getOrderDetailId());
        }
        return OrderDetailMapper.mapToOrderDetailDto(updatedOrderDetail);
    }

    @Override
    public void deleteOrderDetail(String Id) {
        OrderDetail orderDetail = orderDetailRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderDetail is not found:" + Id));
        orderDetail.setStatus(0);
        orderDetailRepository.save(orderDetail);
        drawingCodeProcessService.updateProcessStatus(orderDetail.getOrderDetailId());
    }

    @Override
    public Page<OrderDetailResponseDto> getOrderDetails(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<OrderDetail> pageEntity = orderDetailRepository.findByStatusAndProgressNot(1, 3, pageable);

        return pageEntity.map(od -> {
            ProcessTimeSummaryDto summary = processTimeSummaryService.getByOrderDetailId(od.getOrderDetailId());
            return OrderDetailMapper.mapToOrderDetailResponseDto(od, summary);
        });
    }

    @Override
    public void updateOrderCode(String drawingCodeId, String orderId) {
        // OrderDetail orderDetail = null;
        // String drawingCodeName = "";
        // String poNumber = "";
        // if ((drawingCodeId != null)) {
        // orderDetail =
        // orderDetailRepository.findByDrawingCode(drawingCodeId).orElse(null);
        // drawingCodeName = orderDetail.getDrawingCode().getDrawingCodeName();
        // poNumber = orderDetail.getOrder().getPoNumber();
        // } else {
        // orderDetail = orderDetailRepository.findByOrder(orderId).orElse(null);
        // drawingCodeName = orderDetail.getDrawingCode().getDrawingCodeName();
        // poNumber = orderDetail.getOrder().getPoNumber();
        // }

        // orderDetail.setOrderCode(poNumber + "_" + drawingCodeName);
    }

    @Override
    public OrderDetailDto getOrderDetailById(String Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrderDetailById'");
    }

    @Override
    public void importExcel(MultipartFile file) {
        try {
            InputStream inputStream = ((MultipartFile) file).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<OrderDetail> orderDetails = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            boolean flag = true;
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

                OrderDetail orderDetail = new OrderDetail();

                long createdTimestamp = System.currentTimeMillis();

                String orderCode = row.getCell(2).getStringCellValue();
                if (orderDetailRepository.existsByOrderCode(orderCode)) {
                    flag = false;
                    ids.add(orderCode);
                }
                orderDetail.setOrderCode(orderCode);
                orderDetail.setPgTimeGoal((int) row.getCell(8).getNumericCellValue());
                orderDetail.setNumberOfStep((int) row.getCell(4).getNumericCellValue());
                orderDetail.setOrderType(row.getCell(3).getStringCellValue());
                orderDetail.setQuantity((int) row.getCell(5).getNumericCellValue());
                orderDetail.setOffice(row.getCell(7).getStringCellValue());

                String groupName = row.getCell(6).getStringCellValue();

                Optional<Group> groupOpt = groupRepository.findByGroupName(groupName);
                if (groupOpt.isEmpty()) {
                    // Optionally log: System.out.println("Group not found: " + groupName);
                    continue; // Skip this row if group not found
                }
                orderDetail.setManagerGroup(groupOpt.get());

                orderDetail.setCreatedDate(createdTimestamp);
                orderDetail.setUpdatedDate(createdTimestamp);
                orderDetail.setStatus(1);
                orderDetail.setProgress(1);
                orderDetails.add(orderDetail);
            }
            if (flag == false) {
                throw new BusinessException("ID mã hàng " + ids + " đã có trong hệ thống");
            }
            orderDetailRepository.saveAll(orderDetails);
            workbook.close();
            inputStream.close();
            for (int i = 0; i < orderDetails.size(); i++) {
                for (int j = 1; j <= orderDetails.get(i).getNumberOfStep(); j++) {
                    Integer partNumber = j;
                    for (int k = 1; k <= orderDetails.get(i).getQuantity(); k++) {
                        Integer stepNumber = k;
                        DrawingCodeProcessResquestDto dto = new DrawingCodeProcessResquestDto();
                        dto.setPartNumber(partNumber);
                        dto.setIsPlan(0);
                        dto.setStepNumber(stepNumber);
                        dto.setOrderCode(orderDetails.get(i).getOrderCode());
                        dto.setProcessType(orderDetails.get(i).getOrderType());
                        drawingCodeProcessService.addDrawingCodeProcess(dto);
                    }
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public List<ListOrderDetailStatus> getListOrderStatus() {
        List<PartProgressDto> raw = drawingCodeProcessRepository.getPartProgress();
        HashMap<String, ListOrderDetailStatus> map = new LinkedHashMap<>();

        for (PartProgressDto dto : raw) {
            map.computeIfAbsent(dto.getOrderDetailId(), id -> {
                ListOrderDetailStatus obj = new ListOrderDetailStatus();
                obj.setOrderDetailId(dto.getOrderDetailId());
                obj.setOrderCode(dto.getOrderCode());
                return obj;
            });

            map.get(dto.getOrderDetailId()).getOrderStatus().add(
                    new OrderDetailStatus(
                            dto.getPartNumber(),
                            dto.getTotalStep().intValue(),
                            dto.getDoneStep().intValue(),
                            dto.getDoingStep().intValue()));
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetails() {
        return orderDetailRepository.findByStatusAndProgressNot(1, 3).stream()
                .map(od -> {
                    ProcessTimeSummaryDto summary = processTimeSummaryService.getByOrderDetailId(od.getOrderDetailId());
                    return OrderDetailMapper.mapToOrderDetailResponseDto(od, summary);
                })
                .toList();
    }

}
