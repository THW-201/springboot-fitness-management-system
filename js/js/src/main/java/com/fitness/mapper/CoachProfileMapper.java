package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.CoachProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 教练信息 Mapper 接口
 * 提供教练信息数据访问操作
 */
@Mapper
public interface CoachProfileMapper extends BaseMapper<CoachProfile> {

    /**
     * 根据用户ID查询教练信息
     *
     * @param userId 用户ID
     * @return 教练信息
     */
    @Select("SELECT * FROM coach_profiles WHERE user_id = #{userId}")
    Optional<CoachProfile> findByUserId(Long userId);
}
