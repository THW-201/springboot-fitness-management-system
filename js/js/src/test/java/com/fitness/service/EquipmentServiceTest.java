package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.dto.CreateEquipmentRequest;
import com.fitness.dto.EquipmentDTO;
import com.fitness.dto.UpdateEquipmentRequest;
import com.fitness.entity.Equipment;
import com.fitness.entity.enums.EquipmentStatus;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.EquipmentMapper;
import com.fitness.service.impl.EquipmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EquipmentService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("器材服务测试")
class EquipmentServiceTest {

    @Mock
    private EquipmentMapper equipmentMapper;

    @Mock
    private CacheService cacheService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private Equipment equipment;
    private CreateEquipmentRequest createRequest;
    private UpdateEquipmentRequest updateRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        equipment = Equipment.builder()
                .id(1L)
                .name("跑步机")
                .equipmentType("有氧器材")
                .description("专业跑步机")
                .location("健身房B区")
                .status(EquipmentStatus.AVAILABLE)
                .purchaseDate(LocalDate.of(2023, 1, 15))
                .lastMaintenanceDate(LocalDate.of(2024, 1, 10))
                .imageUrl("https://example.com/treadmill.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateEquipmentRequest();
        createRequest.setName("跑步机");
        createRequest.setEquipmentType("有氧器材");
        createRequest.setDescription("专业跑步机");
        createRequest.setLocation("健身房B区");
        createRequest.setPurchaseDate(LocalDate.of(2023, 1, 15));
        createRequest.setLastMaintenanceDate(LocalDate.of(2024, 1, 10));
        createRequest.setImageUrl("https://example.com/treadmill.jpg");

        updateRequest = new UpdateEquipmentRequest();
        updateRequest.setName("跑步机Pro");
        updateRequest.setLocation("健身房A区");
    }

    @Test
    @DisplayName("添加器材 - 成功")
    void testAddEquipment_Success() {
        // Mock
        when(equipmentMapper.insert(any(Equipment.class))).thenAnswer(invocation -> {
            Equipment e = invocation.getArgument(0);
            e.setId(1L);
            return 1;
        });
        doNothing().when(cacheService).deletePattern(anyString());

        // 执行
        EquipmentDTO result = equipmentService.addEquipment(createRequest);

        // 验证
        assertNotNull(result);
        assertEquals("跑步机", result.getName());
        assertEquals("有氧器材", result.getEquipmentType());
        assertEquals(EquipmentStatus.AVAILABLE, result.getStatus());
        assertEquals("健身房B区", result.getLocation());

        verify(equipmentMapper, times(1)).insert(any(Equipment.class));
        verify(cacheService, times(1)).deletePattern(anyString());
    }

    @Test
    @DisplayName("更新器材 - 成功")
    void testUpdateEquipment_Success() {
        // Mock
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        when(equipmentMapper.updateById(any(Equipment.class))).thenReturn(1);
        doNothing().when(cacheService).delete(anyString());
        doNothing().when(cacheService).deletePattern(anyString());

        // 执行
        EquipmentDTO result = equipmentService.updateEquipment(1L, updateRequest);

        // 验证
        assertNotNull(result);
        assertEquals("跑步机Pro", result.getName());
        assertEquals("健身房A区", result.getLocation());

        verify(equipmentMapper, times(1)).updateById(any(Equipment.class));
        verify(cacheService, times(1)).delete(anyString());
        verify(cacheService, times(1)).deletePattern(anyString());
    }

    @Test
    @DisplayName("更新器材 - 器材不存在")
    void testUpdateEquipment_NotFound() {
        // Mock
        when(equipmentMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            equipmentService.updateEquipment(999L, updateRequest);
        });

        assertEquals("器材不存在", exception.getMessage());
        verify(equipmentMapper, never()).updateById(any(Equipment.class));
    }

    @Test
    @DisplayName("更新器材状态为维护中 - 自动更新维护日期")
    void testUpdateEquipment_StatusToMaintenance() {
        // Mock
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        when(equipmentMapper.updateById(any(Equipment.class))).thenAnswer(invocation -> {
            Equipment e = invocation.getArgument(0);
            // 验证维护日期已更新为今天
            assertEquals(LocalDate.now(), e.getLastMaintenanceDate());
            return 1;
        });
        doNothing().when(cacheService).delete(anyString());
        doNothing().when(cacheService).deletePattern(anyString());

        // 设置状态为维护中
        updateRequest.setStatus(EquipmentStatus.MAINTENANCE);

        // 执行
        EquipmentDTO result = equipmentService.updateEquipment(1L, updateRequest);

        // 验证
        assertNotNull(result);
        verify(equipmentMapper, times(1)).updateById(any(Equipment.class));
    }

    @Test
    @DisplayName("删除器材 - 成功")
    void testDeleteEquipment_Success() {
        // Mock
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        when(equipmentMapper.deleteById(1L)).thenReturn(1);
        doNothing().when(cacheService).delete(anyString());
        doNothing().when(cacheService).deletePattern(anyString());

        // 执行
        assertDoesNotThrow(() -> equipmentService.deleteEquipment(1L));

        // 验证
        verify(equipmentMapper, times(1)).deleteById(1L);
        verify(cacheService, times(1)).delete(anyString());
        verify(cacheService, times(1)).deletePattern(anyString());
    }

    @Test
    @DisplayName("删除器材 - 器材不存在")
    void testDeleteEquipment_NotFound() {
        // Mock
        when(equipmentMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            equipmentService.deleteEquipment(999L);
        });

        assertEquals("器材不存在", exception.getMessage());
        verify(equipmentMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("获取器材详情 - 成功")
    void testGetEquipmentById_Success() {
        // Mock
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        doNothing().when(cacheService).set(anyString(), anyString(), anyLong(), any());

        // 执行
        EquipmentDTO result = equipmentService.getEquipmentById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("跑步机", result.getName());
        assertEquals(EquipmentStatus.AVAILABLE, result.getStatus());

        verify(equipmentMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("获取器材详情 - 器材不存在")
    void testGetEquipmentById_NotFound() {
        // Mock
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
        when(equipmentMapper.selectById(999L)).thenReturn(null);

        // 执行并验证
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            equipmentService.getEquipmentById(999L);
        });

        assertEquals("器材不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取器材列表 - 成功")
    void testGetEquipmentList_Success() {
        // Mock
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
        
        Page<Equipment> equipmentPage = new Page<>(1, 10);
        equipmentPage.setRecords(java.util.Collections.singletonList(equipment));
        equipmentPage.setTotal(1);
        
        when(equipmentMapper.searchEquipment(any(Page.class), any()))
                .thenReturn(equipmentPage);
        doNothing().when(cacheService).set(anyString(), anyString(), anyLong(), any());

        // 执行
        Page<EquipmentDTO> pageParam = new Page<>(1, 10);
        com.fitness.dto.EquipmentQueryDTO query = com.fitness.dto.EquipmentQueryDTO.builder()
                .keyword("跑步机")
                .build();
        IPage<EquipmentDTO> result = equipmentService.getEquipmentList(pageParam, query);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("跑步机", result.getRecords().get(0).getName());

        verify(equipmentMapper, times(1)).searchEquipment(any(Page.class), any());
    }

    @Test
    @DisplayName("获取器材列表 - 按状态筛选")
    void testGetEquipmentList_FilterByStatus() {
        // Mock
        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
        
        Page<Equipment> equipmentPage = new Page<>(1, 10);
        equipmentPage.setRecords(java.util.Collections.singletonList(equipment));
        equipmentPage.setTotal(1);
        
        when(equipmentMapper.searchEquipment(any(Page.class), any()))
                .thenReturn(equipmentPage);
        doNothing().when(cacheService).set(anyString(), anyString(), anyLong(), any());

        // 执行
        Page<EquipmentDTO> pageParam = new Page<>(1, 10);
        com.fitness.dto.EquipmentQueryDTO query = com.fitness.dto.EquipmentQueryDTO.builder()
                .status(EquipmentStatus.AVAILABLE)
                .build();
        IPage<EquipmentDTO> result = equipmentService.getEquipmentList(pageParam, query);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(equipmentMapper, times(1)).searchEquipment(any(Page.class), any());
    }

    @Test
    @DisplayName("清除器材缓存 - 清除特定器材")
    void testClearEquipmentCache_SpecificEquipment() {
        // Mock
        doNothing().when(cacheService).delete(anyString());
        doNothing().when(cacheService).deletePattern(anyString());

        // 执行
        equipmentService.clearEquipmentCache(1L);

        // 验证
        verify(cacheService, times(1)).delete(anyString());
        verify(cacheService, times(1)).deletePattern(anyString());
    }

    @Test
    @DisplayName("清除器材缓存 - 清除所有器材")
    void testClearEquipmentCache_AllEquipment() {
        // Mock
        doNothing().when(cacheService).deletePattern(anyString());

        // 执行
        equipmentService.clearEquipmentCache(null);

        // 验证
        verify(cacheService, never()).delete(anyString());
        verify(cacheService, times(1)).deletePattern(anyString());
    }
}
