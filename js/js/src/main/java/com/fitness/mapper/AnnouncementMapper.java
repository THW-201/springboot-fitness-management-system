package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告Mapper
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 根据用户角色获取公告列表
     * @param role 用户角色
     * @param userId 用户ID
     * @return 公告列表
     */
    List<Announcement> selectByUserRole(@Param("role") String role, @Param("userId") Long userId);

    /**
     * 增加公告查看次数
     * @param id 公告ID
     * @return 更新结果
     */
    int incrementViewCount(@Param("id") Long id);
}
