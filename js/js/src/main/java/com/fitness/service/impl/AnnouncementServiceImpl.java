package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.dto.AnnouncementDTO;
import com.fitness.dto.CreateAnnouncementRequest;
import com.fitness.dto.UpdateAnnouncementRequest;
import com.fitness.entity.Announcement;
import com.fitness.entity.User;
import com.fitness.mapper.AnnouncementMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.AnnouncementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告服务实现类
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public AnnouncementDTO createAnnouncement(CreateAnnouncementRequest request, Long userId) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .targetUserId(request.getTargetUserId())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .expireAt(request.getExpireAt())
                .viewCount(0)
                .createdBy(userId)
                .build();

        // 处理目标角色
        if (request.getTargetRoles() != null && request.getTargetRoles().length > 0) {
            try {
                announcement.setTargetRoles(objectMapper.writeValueAsString(request.getTargetRoles()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize target roles", e);
            }
        }

        save(announcement);
        return convertToDTO(announcement);
    }

    @Override
    public AnnouncementDTO updateAnnouncement(Long id, UpdateAnnouncementRequest request) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new RuntimeException("Announcement not found");
        }

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setType(request.getType());
        announcement.setTargetUserId(request.getTargetUserId());
        announcement.setStatus(request.getStatus() != null ? request.getStatus() : announcement.getStatus());
        announcement.setPriority(request.getPriority() != null ? request.getPriority() : announcement.getPriority());
        announcement.setExpireAt(request.getExpireAt());

        // 处理目标角色
        if (request.getTargetRoles() != null && request.getTargetRoles().length > 0) {
            try {
                announcement.setTargetRoles(objectMapper.writeValueAsString(request.getTargetRoles()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize target roles", e);
            }
        } else {
            announcement.setTargetRoles(null);
        }

        updateById(announcement);
        return convertToDTO(announcement);
    }

    @Override
    public AnnouncementDTO getAnnouncementById(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new RuntimeException("Announcement not found");
        }
        return convertToDTO(announcement);
    }

    @Override
    public List<AnnouncementDTO> getAllAnnouncements() {
        List<Announcement> announcements = list();
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementDTO> getAnnouncementsByUserRole(String role, Long userId) {
        List<Announcement> announcements = announcementMapper.selectByUserRole(role, userId);
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        return removeById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        announcementMapper.incrementViewCount(id);
    }

    /**
     * 将实体转换为DTO
     */
    private AnnouncementDTO convertToDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setType(announcement.getType());
        dto.setTargetUserId(announcement.getTargetUserId());
        dto.setStatus(announcement.getStatus());
        dto.setPriority(announcement.getPriority());
        dto.setExpireAt(announcement.getExpireAt());
        dto.setViewCount(announcement.getViewCount());
        dto.setCreatedBy(announcement.getCreatedBy());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());

        // 获取创建人姓名
        User user = userMapper.selectById(announcement.getCreatedBy());
        if (user != null) {
            dto.setCreatedByName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        // 处理目标角色
        if (announcement.getTargetRoles() != null) {
            try {
                dto.setTargetRoles(objectMapper.readValue(announcement.getTargetRoles(), String[].class));
            } catch (JsonProcessingException e) {
                dto.setTargetRoles(new String[0]);
            }
        }

        return dto;
    }
}
