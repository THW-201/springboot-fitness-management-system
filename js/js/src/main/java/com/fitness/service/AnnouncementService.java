package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.dto.AnnouncementDTO;
import com.fitness.dto.CreateAnnouncementRequest;
import com.fitness.dto.UpdateAnnouncementRequest;
import com.fitness.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 创建公告
     * @param request 创建公告请求
     * @param userId 创建人ID
     * @return 公告DTO
     */
    AnnouncementDTO createAnnouncement(CreateAnnouncementRequest request, Long userId);

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新公告请求
     * @return 公告DTO
     */
    AnnouncementDTO updateAnnouncement(Long id, UpdateAnnouncementRequest request);

    /**
     * 根据ID获取公告详情
     * @param id 公告ID
     * @return 公告DTO
     */
    AnnouncementDTO getAnnouncementById(Long id);

    /**
     * 获取所有公告（管理员用）
     * @return 公告DTO列表
     */
    List<AnnouncementDTO> getAllAnnouncements();

    /**
     * 根据用户角色获取公告列表
     * @param role 用户角色
     * @param userId 用户ID
     * @return 公告DTO列表
     */
    List<AnnouncementDTO> getAnnouncementsByUserRole(String role, Long userId);

    /**
     * 删除公告
     * @param id 公告ID
     * @return 是否删除成功
     */
    boolean deleteAnnouncement(Long id);

    /**
     * 增加公告查看次数
     * @param id 公告ID
     */
    void incrementViewCount(Long id);
}
