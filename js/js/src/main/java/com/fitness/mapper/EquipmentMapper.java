package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.entity.Equipment;
import com.fitness.entity.enums.EquipmentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 器材 Mapper 接口
 * 提供器材数据访问操作
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    /**
     * 搜索器材（支持按名称、类型模糊搜索）
     *
     * @param page 分页对象
     * @param query 查询条件对象
     * @return 器材分页列表
     */
    @Select("<script>" +
            "SELECT * FROM equipment WHERE 1=1 " +
            "<if test='query.keyword != null and query.keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{query.keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{query.keyword}, '%') " +
            "OR location LIKE CONCAT('%', #{query.keyword}, '%')) " +
            "</if>" +
            "<if test='query.equipmentType != null and query.equipmentType != \"\"'>" +
            "AND equipment_type = #{query.equipmentType} " +
            "</if>" +
            "<if test='query.status != null'>" +
            "AND status = #{query.status} " +
            "</if>" +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<Equipment> searchEquipment(Page<Equipment> page, @Param("query") com.fitness.dto.EquipmentQueryDTO query);

    /**
     * 根据器材类型查询器材列表
     *
     * @param equipmentType 器材类型
     * @return 器材列表
     */
    @Select("SELECT * FROM equipment WHERE equipment_type = #{equipmentType} ORDER BY name")
    List<Equipment> findByEquipmentType(String equipmentType);

    /**
     * 根据状态查询器材列表
     *
     * @param status 器材状态
     * @return 器材列表
     */
    @Select("SELECT * FROM equipment WHERE status = #{status} ORDER BY name")
    List<Equipment> findByStatus(EquipmentStatus status);

    /**
     * 查询可用器材
     *
     * @return 可用器材列表
     */
    @Select("SELECT * FROM equipment WHERE status = 'AVAILABLE' ORDER BY name")
    List<Equipment> findAvailableEquipment();
}
