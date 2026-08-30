package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.common.ResultCode;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CreateEquipmentRequest;
import com.fitness.dto.EquipmentDTO;
import com.fitness.dto.EquipmentQueryDTO;
import com.fitness.dto.UpdateEquipmentRequest;
import com.fitness.entity.Equipment;
import com.fitness.entity.Reservation;
import com.fitness.entity.User;
import com.fitness.entity.enums.EquipmentStatus;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.EquipmentMapper;
import com.fitness.mapper.ReservationMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.CacheService;
import com.fitness.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 器材服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;

    private static final String EQUIPMENT_CACHE_PREFIX = "equipment:";
    private static final String EQUIPMENT_LIST_CACHE_PREFIX = "equipment:list:";
    private static final long EQUIPMENT_CACHE_TTL = 5; // 5分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EquipmentDTO addEquipment(CreateEquipmentRequest request) {
        log.info("Adding equipment: {}", request.getName());

        // 创建器材实体
        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .equipmentType(request.getEquipmentType())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(EquipmentStatus.AVAILABLE) // 初始状态为可用
                .purchaseDate(request.getPurchaseDate())
                .lastMaintenanceDate(request.getLastMaintenanceDate())
                .imageUrl(request.getImageUrl())
                .build();

        equipmentMapper.insert(equipment);
        log.info("Equipment added successfully with ID: {}", equipment.getId());

        // 清除器材列表缓存
//        clearEquipmentCache(null);

        return convertToDTO(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EquipmentDTO updateEquipment(Long equipmentId, UpdateEquipmentRequest request) {
        log.info("Updating equipment: {}", equipmentId);

        // 查询器材
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "器材不存在");
        }

        // 记录原状态
        EquipmentStatus oldStatus = equipment.getStatus();

        // 更新器材信息
        if (request.getName() != null) {
            equipment.setName(request.getName());
        }
        if (request.getEquipmentType() != null) {
            equipment.setEquipmentType(request.getEquipmentType());
        }
        if (request.getDescription() != null) {
            equipment.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            equipment.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
            
            // 如果状态变更为维护中，更新最后维护日期
            if (request.getStatus() == EquipmentStatus.MAINTENANCE && oldStatus != EquipmentStatus.MAINTENANCE) {
                equipment.setLastMaintenanceDate(LocalDate.now());
                log.info("Equipment status changed to MAINTENANCE, updated lastMaintenanceDate");
            }
        }
        if (request.getPurchaseDate() != null) {
            equipment.setPurchaseDate(request.getPurchaseDate());
        }
        if (request.getLastMaintenanceDate() != null) {
            equipment.setLastMaintenanceDate(request.getLastMaintenanceDate());
        }
        if (request.getImageUrl() != null) {
            equipment.setImageUrl(request.getImageUrl());
        }

        equipmentMapper.updateById(equipment);
        log.info("Equipment updated successfully: {}", equipmentId);

        // 清除缓存
//        clearEquipmentCache(equipmentId);

        return convertToDTO(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(Long equipmentId) {
        log.info("Deleting equipment: {}", equipmentId);

        // 查询器材
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "器材不存在");
        }
// 2. 关键新增：检查是否有关联的预约记录
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getEquipmentId, equipmentId);
        Long count = reservationMapper.selectCount(queryWrapper);

        // 如果存在预约记录，直接抛出业务异常，阻止删除
        if (count > 0) {
            throw new BusinessException(ResultCode.CONFLICT, "该器材已有预约记录，无法直接删除！");
        }
        equipmentMapper.deleteById(equipmentId);
        log.info("Equipment deleted successfully: {}", equipmentId);
    }

    @Override
    public IPage<EquipmentDTO> getEquipmentList(Page<EquipmentDTO> page, EquipmentQueryDTO query) {

        log.debug("Getting equipment list - page: {}, size: {}, query: {}",
                page.getCurrent(), page.getSize(), query);

        // 当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = userMapper.selectById(loginUser.getUserId());

        String roleCode = user.getRole().getCode();

        // 查询设备
        Page<Equipment> equipmentPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<Equipment> equipmentResult = equipmentMapper.searchEquipment(equipmentPage, query);

        // DTO转换
        IPage<EquipmentDTO> dtoPage = equipmentResult.convert(this::convertToDTO);

        // 如果是学生，需要判断是否已预约
        if ("STUDENT".equals(roleCode)) {

            List<EquipmentDTO> records = dtoPage.getRecords();

            if (!records.isEmpty()) {

                // 设备ID集合
                List<Long> equipmentIds = records.stream()
                        .map(EquipmentDTO::getId)
                        .toList();

                // 查询学生预约
                List<Reservation> reservations = reservationMapper.selectList(
                        Wrappers.<Reservation>lambdaQuery()
                                .eq(Reservation::getStudentId, user.getId())
                                .eq(Reservation::getReservationType, ReservationType.EQUIPMENT)
                                .in(Reservation::getStatus,
                                        ReservationStatus.PENDING,
                                        ReservationStatus.CONFIRMED)
                                .in(Reservation::getEquipmentId, equipmentIds)
                );

                // 已预约设备ID
                Set<Long> reservedEquipmentIds = reservations.stream()
                        .map(Reservation::getEquipmentId)
                        .collect(Collectors.toSet());

                // 标记预约状态
                for (EquipmentDTO equipment : records) {
                    if (reservedEquipmentIds.contains(equipment.getId())) {
                        equipment.setStatus(EquipmentStatus.RESERVATION);
                    }
                }
            }
        }

        return dtoPage;
    }

    @Override
    public EquipmentDTO getEquipmentById(Long equipmentId) {
        log.debug("Getting equipment by ID: {}", equipmentId);

        // 构建缓存键
        String cacheKey = EQUIPMENT_CACHE_PREFIX + equipmentId;

        // 尝试从缓存获取
        try {
            String cachedData = cacheService.get(cacheKey, String.class);
            if (cachedData != null) {
                log.debug("Equipment found in cache: {}", equipmentId);
                return objectMapper.readValue(cachedData, EquipmentDTO.class);
            }
        } catch (Exception e) {
            log.warn("Failed to get equipment from cache", e);
        }

        // 从数据库查询
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "器材不存在");
        }

        EquipmentDTO dto = convertToDTO(equipment);

        // 缓存结果
        try {
            String jsonData = objectMapper.writeValueAsString(dto);
            cacheService.set(cacheKey, jsonData, EQUIPMENT_CACHE_TTL, TimeUnit.MINUTES);
            log.debug("Equipment cached successfully: {}", equipmentId);
        } catch (Exception e) {
            log.warn("Failed to cache equipment", e);
        }

        return dto;
    }

    @Override
    public void clearEquipmentCache(Long equipmentId) {
        if (equipmentId != null) {
            // 清除特定器材缓存
            String cacheKey = EQUIPMENT_CACHE_PREFIX + equipmentId;
            cacheService.delete(cacheKey);
            log.debug("Cleared cache for equipment: {}", equipmentId);
        }
        
        // 清除器材列表缓存
        cacheService.deletePattern(EQUIPMENT_LIST_CACHE_PREFIX + "*");
        log.debug("Cleared equipment list cache");
    }

    /**
     * 转换Equipment实体为EquipmentDTO
     */
    private EquipmentDTO convertToDTO(Equipment equipment) {
        return EquipmentDTO.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .equipmentType(equipment.getEquipmentType())
                .description(equipment.getDescription())
                .location(equipment.getLocation())
                .status(equipment.getStatus())
                .purchaseDate(equipment.getPurchaseDate())
                .lastMaintenanceDate(equipment.getLastMaintenanceDate())
                .imageUrl(equipment.getImageUrl())
                .introImages(equipment.getIntroImages())
                .coachId(equipment.getCoachId())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }

    /**
     * 构建器材列表缓存键
     */
    private String buildEquipmentListCacheKey(Page<EquipmentDTO> page, com.fitness.dto.EquipmentQueryDTO query) {
        StringBuilder sb = new StringBuilder(EQUIPMENT_LIST_CACHE_PREFIX);
        sb.append("page:").append(page.getCurrent());
        sb.append(":size:").append(page.getSize());
        if (query != null) {
            if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
                sb.append(":keyword:").append(query.getKeyword());
            }
            if (query.getEquipmentType() != null && !query.getEquipmentType().isEmpty()) {
                sb.append(":type:").append(query.getEquipmentType());
            }
            if (query.getStatus() != null) {
                sb.append(":status:").append(query.getStatus());
            }
        }
        return sb.toString();
    }
}