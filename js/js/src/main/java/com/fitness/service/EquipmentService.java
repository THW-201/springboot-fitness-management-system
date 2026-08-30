package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.dto.CreateEquipmentRequest;
import com.fitness.dto.EquipmentDTO;
import com.fitness.dto.UpdateEquipmentRequest;
import com.fitness.entity.enums.EquipmentStatus;

/**
 * 器材服务接口
 * 提供器材管理的业务逻辑
 */
public interface EquipmentService {

    /**
     * 添加器材
     * 需要管理员权限
     *
     * @param request 创建器材请求
     * @return 器材信息
     */
    EquipmentDTO addEquipment(CreateEquipmentRequest request);

    /**
     * 更新器材
     * 需要管理员权限，状态变更时自动记录时间戳
     *
     * @param equipmentId 器材ID
     * @param request 更新器材请求
     * @return 更新后的器材信息
     */
    EquipmentDTO updateEquipment(Long equipmentId, UpdateEquipmentRequest request);

    /**
     * 删除器材
     * 需要管理员权限
     *
     * @param equipmentId 器材ID
     */
    void deleteEquipment(Long equipmentId);

    /**
     * 获取器材列表
     * 支持分页、搜索、筛选
     *
     * @param page 分页对象
     * @param query 查询条件对象
     * @return 器材分页列表
     */
    IPage<EquipmentDTO> getEquipmentList(Page<EquipmentDTO> page, com.fitness.dto.EquipmentQueryDTO query);

    /**
     * 获取器材详情
     * 返回实时可用状态
     *
     * @param equipmentId 器材ID
     * @return 器材详情
     */
    EquipmentDTO getEquipmentById(Long equipmentId);

    /**
     * 清除器材相关缓存
     *
     * @param equipmentId 器材ID（可选，为null时清除所有器材缓存）
     */
    void clearEquipmentCache(Long equipmentId);
}
