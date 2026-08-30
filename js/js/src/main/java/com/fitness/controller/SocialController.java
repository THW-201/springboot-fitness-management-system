package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.CreatePostDTO;
import com.fitness.dto.UpdatePostDTO;
import com.fitness.dto.CreateCommentDTO;
import com.fitness.dto.PostListDTO;
import com.fitness.dto.TopicDTO;
import com.fitness.dto.UserDTO;
import com.fitness.service.SocialPostService;
import com.fitness.service.SocialCommentService;
import com.fitness.service.SocialLikeService;
import com.fitness.service.SocialTopicService;
import com.fitness.service.SocialFollowService;
import com.fitness.service.SocialReportService;
import com.fitness.entity.SocialReport;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.Result;
import com.fitness.common.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交广场控制器
 */
@RestController
@RequestMapping("/social")
public class SocialController {

    @Autowired
    private SocialPostService socialPostService;

    @Autowired
    private SocialCommentService socialCommentService;

    @Autowired
    private SocialLikeService socialLikeService;

    @Autowired
    private SocialTopicService socialTopicService;

    @Autowired
    private SocialFollowService socialFollowService;

    @Autowired
    private SocialReportService socialReportService;

    // ==================== 帖子相关 ====================

    /**
     * 创建帖子
     */
    @PostMapping("/posts")
    public Result<PostListDTO> createPost(@RequestBody CreatePostDTO createPostDTO, @CurrentUser Long userId) {
        PostListDTO post = socialPostService.createPost(createPostDTO, userId);
        return Result.success(post);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public Result<PostListDTO> updatePost(@PathVariable Long id, @RequestBody UpdatePostDTO updatePostDTO, @CurrentUser Long userId) {
        PostListDTO post = socialPostService.updatePost(id, updatePostDTO, userId);
        return Result.success(post);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{id}")
    public Result<?> deletePost(@PathVariable Long id, @CurrentUser Long userId) {
        socialPostService.deletePost(id, userId);
        return Result.success();
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result<PostListDTO> getPostDetail(@PathVariable Long id, @CurrentUser Long userId) {
        PostListDTO post = socialPostService.getPostDetail(id, userId);
        return Result.success(post);
    }

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public Result<IPage<PostListDTO>> getPostList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @CurrentUser Long userId) {
        IPage<PostListDTO> page = socialPostService.getPostList(current, size, userId);
        return Result.success(page);
    }

    /**
     * 获取我的帖子列表
     */
    @GetMapping("/posts/my")
    public Result<IPage<PostListDTO>> getMyPostList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @CurrentUser Long userId) {
        IPage<PostListDTO> page = socialPostService.getMyPostList(current, size, userId);
        return Result.success(page);
    }

    /**
     * 获取关注用户帖子列表
     */
    @GetMapping("/posts/following")
    public Result<IPage<PostListDTO>> getFollowingPostList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @CurrentUser Long userId) {
        IPage<PostListDTO> page = socialPostService.getFollowingPostList(current, size, userId);
        return Result.success(page);
    }

    // ==================== 评论相关 ====================

    /**
     * 创建评论
     */
    @PostMapping("/posts/{postId}/comments")
    public Result<?> createComment(@PathVariable Long postId, @RequestBody CreateCommentDTO createCommentDTO, @CurrentUser Long userId) {
        socialCommentService.createComment(postId, createCommentDTO, userId);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<?> deleteComment(@PathVariable Long id, @CurrentUser Long userId) {
        socialCommentService.deleteComment(id, userId);
        return Result.success();
    }

    /**
     * 获取帖子评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public Result<List<?>> getCommentsByPostId(@PathVariable Long postId) {
        return Result.success(socialCommentService.getCommentsByPostId(postId));
    }

    // ==================== 点赞相关 ====================

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/posts/{postId}/like")
    public Result<Boolean> toggleLike(@PathVariable Long postId, @CurrentUser Long userId) {
        boolean result = socialLikeService.toggleLike(postId, userId);
        return Result.success(result);
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/posts/{postId}/like/check")
    public Result<Boolean> checkLike(@PathVariable Long postId, @CurrentUser Long userId) {
        boolean result = socialLikeService.checkLike(postId, userId);
        return Result.success(result);
    }

    // ==================== 话题相关 ====================

    /**
     * 获取热门话题列表
     */
    @GetMapping("/topics/hot")
    public Result<List<TopicDTO>> getHotTopics() {
        return Result.success(socialTopicService.getHotTopics());
    }

    // ==================== 关注相关 ====================

    /**
     * 关注/取消关注
     */
    @PostMapping("/users/{userId}/follow")
    public Result<Boolean> toggleFollow(@PathVariable Long userId, @CurrentUser Long currentUserId) {
        boolean result = socialFollowService.toggleFollow(userId, currentUserId);
        return Result.success(result);
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/users/{userId}/follow/check")
    public Result<Boolean> checkFollow(@PathVariable Long userId, @CurrentUser Long currentUserId) {
        boolean result = socialFollowService.checkFollow(currentUserId, userId);
        return Result.success(result);
    }

    /**
     * 获取推荐用户列表
     */
    @GetMapping("/users/recommended")
    public Result<List<UserDTO>> getRecommendedUsers(@CurrentUser Long userId) {
        return Result.success(socialFollowService.getRecommendedUsers(userId, 5));
    }

    /**
     * 获取关注用户列表
     */
    @GetMapping("/users/following")
    public Result<List<UserDTO>> getFollowingUsers(@CurrentUser Long userId) {
        return Result.success(socialFollowService.getFollowingUsers(userId, 10));
    }

    // ==================== 举报相关 ====================

    /**
     * 举报帖子
     */
    @PostMapping("/posts/{postId}/report")
    public Result<?> reportPost(@PathVariable Long postId, @RequestBody java.util.Map<String, Object> reportData, @CurrentUser Long userId) {
        String reason = (String) reportData.get("reason");
        String description = (String) reportData.get("description");
        socialReportService.createReport(postId, userId, reason, description);
        return Result.success();
    }

    // ==================== 举报管理相关 ====================

    /**
     * 获取举报列表（管理员）
     */
    @GetMapping("/admin/reports")
    public Result<?> getReportList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<SocialReport> page = socialReportService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 处理举报（管理员）
     */
    @PutMapping("/admin/reports/{id}/process")
    public Result<?> processReport(@PathVariable Long id, @RequestBody java.util.Map<String, Object> processData, @CurrentUser Long userId) {
        String status = (String) processData.get("status");
        socialReportService.processReport(id, userId, status);
        return Result.success();
    }

    // ==================== 帖子管理相关 ====================

    /**
     * 获取帖子列表（管理员）
     */
    @GetMapping("/admin/posts")
    public Result<?> getAdminPostList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<PostListDTO> page = socialPostService.getAdminPostList(current, size);
        return Result.success(page);
    }

    /**
     * 删除帖子（管理员）
     */
    @DeleteMapping("/admin/posts/{id}")
    public Result<?> deletePostAdmin(@PathVariable Long id) {
        socialPostService.deletePost(id, null);
        return Result.success();
    }

    // ==================== 评论管理相关 ====================

    /**
     * 获取评论列表（管理员）
     */
    @GetMapping("/admin/comments")
    public Result<?> getAdminCommentList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<?> page = socialCommentService.getAdminCommentList(current, size);
        return Result.success(page);
    }

    /**
     * 删除评论（管理员）
     */
    @DeleteMapping("/admin/comments/{id}")
    public Result<?> deleteCommentAdmin(@PathVariable Long id) {
        socialCommentService.deleteComment(id, null);
        return Result.success();
    }

    // ==================== 话题管理相关 ====================

    /**
     * 获取话题列表（管理员）
     */
    @GetMapping("/admin/topics")
    public Result<?> getAdminTopicList() {
        return Result.success(socialTopicService.getHotTopics());
    }

    /**
     * 创建话题（管理员）
     */
    @PostMapping("/admin/topics")
    public Result<?> createTopic(@RequestBody java.util.Map<String, Object> topicData) {
        String title = (String) topicData.get("title");
        socialTopicService.createTopic(title);
        return Result.success();
    }

    /**
     * 更新话题（管理员）
     */
    @PutMapping("/admin/topics/{id}")
    public Result<?> updateTopic(@PathVariable Long id, @RequestBody java.util.Map<String, Object> topicData) {
        String title = (String) topicData.get("title");
        socialTopicService.updateTopic(id, title);
        return Result.success();
    }

    /**
     * 删除话题（管理员）
     */
    @DeleteMapping("/admin/topics/{id}")
    public Result<?> deleteTopic(@PathVariable Long id) {
        socialTopicService.deleteTopic(id);
        return Result.success();
    }
}