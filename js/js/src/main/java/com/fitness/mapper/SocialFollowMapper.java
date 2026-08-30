package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.SocialFollow;
import com.fitness.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社交关注Mapper
 */
@Mapper
public interface SocialFollowMapper extends BaseMapper<SocialFollow> {

    /**
     * 检查用户是否已关注
     */
    Integer checkFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 获取推荐用户列表
     */
    List<UserDTO> getRecommendedUsers(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 获取关注用户列表
     */
    List<UserDTO> getFollowingUsers(@Param("userId") Long userId, @Param("limit") int limit);
}