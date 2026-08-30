package com.fitness.controller;

import com.fitness.annotation.CurrentUser;
import com.fitness.common.Result;
import com.fitness.dto.AnnouncementDTO;
import com.fitness.dto.CreateAnnouncementRequest;
import com.fitness.dto.UpdateAnnouncementRequest;
import com.fitness.entity.User;
import com.fitness.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "公告管理", description = "公告相关接口")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 创建公告
     * @param request 创建公告请求
     * @param currentUser 当前用户
     * @return 公告DTO
     */
    @PostMapping
    @Operation(summary = "创建公告", description = "创建新的公告")
    public Result<AnnouncementDTO> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @CurrentUser User currentUser) {
        AnnouncementDTO dto = announcementService.createAnnouncement(request, currentUser.getId());
        return Result.success(dto);
    }

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新公告请求
     * @return 公告DTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新公告", description = "更新指定公告")
    public Result<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request) {
        AnnouncementDTO dto = announcementService.updateAnnouncement(id, request);
        return Result.success(dto);
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告DTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情", description = "根据ID获取公告详情")
    public Result<AnnouncementDTO> getAnnouncementById(@PathVariable Long id) {
        AnnouncementDTO dto = announcementService.getAnnouncementById(id);
        // 增加查看次数
        announcementService.incrementViewCount(id);
        return Result.success(dto);
    }

    /**
     * 获取所有公告（管理员用）
     * @return 公告DTO列表
     */
    @GetMapping
    @Operation(summary = "获取所有公告", description = "获取所有公告列表，仅管理员可用")
    public Result<List<AnnouncementDTO>> getAllAnnouncements() {
        List<AnnouncementDTO> dtos = announcementService.getAllAnnouncements();
        return Result.success(dtos);
    }

    /**
     * 获取用户可见的公告列表
     * @param currentUser 当前用户
     * @return 公告DTO列表
     */
    @GetMapping("/user")
    @Operation(summary = "获取用户公告", description = "获取当前用户可见的公告列表")
    public Result<List<AnnouncementDTO>> getAnnouncementsByUserRole(@CurrentUser User currentUser) {
        String roleName = currentUser.getRole() != null ? currentUser.getRole().name() : "STUDENT";
        List<AnnouncementDTO> dtos = announcementService.getAnnouncementsByUserRole(
                roleName, currentUser.getId());
        return Result.success(dtos);
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "删除指定公告")
    public Result<Boolean> deleteAnnouncement(@PathVariable Long id) {
        boolean result = announcementService.deleteAnnouncement(id);
        return Result.success(result);
    }
}
