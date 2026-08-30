package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialComment;
import com.fitness.entity.SocialPost;          // 导入 SocialPost 实体
import com.fitness.mapper.SocialCommentMapper;
import com.fitness.service.SocialCommentService;
import com.fitness.dto.CreateCommentDTO;
import com.fitness.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialCommentServiceImpl extends ServiceImpl<SocialCommentMapper, SocialComment> implements SocialCommentService {

    @Autowired
    private SocialPostService socialPostService;

    @Override
    @Transactional
    public SocialComment createComment(Long postId, CreateCommentDTO createCommentDTO, Long userId) {
        // 检查帖子是否存在
        SocialPost post = socialPostService.getById(postId);   // 获取帖子对象（优化点）
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 创建评论
        SocialComment comment = new SocialComment()
                .setPostId(postId)
                .setUserId(userId)
                .setContent(createCommentDTO.getContent());

        this.save(comment);

        // 更新帖子评论数
        int commentCount = this.getCommentCountByPostId(postId);
        post.setComments(commentCount);                         // 直接使用已获取的帖子对象
        socialPostService.updateById(post);                     // 更新帖子

        // 返回包含用户信息的完整评论（关键修改点）
        return baseMapper.selectCommentWithUserById(comment.getId());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 检查评论是否存在
        SocialComment comment = this.getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 如果不是管理员操作，检查评论是否属于当前用户
        if (userId != null && !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        Long postId = comment.getPostId();

        // 删除评论
        this.removeById(commentId);

        // 更新帖子评论数
        int commentCount = this.getCommentCountByPostId(postId);
        SocialPost post = socialPostService.getById(postId);   // 获取帖子对象
        post.setComments(commentCount);
        socialPostService.updateById(post);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> getAdminCommentList(long current, long size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SocialComment> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        long offset = (current - 1) * size;
        page.setRecords(baseMapper.getAdminComments(offset, size));
        page.setTotal(baseMapper.getAdminCommentCount());
        return page;
    }

    @Override
    public List<SocialComment> getCommentsByPostId(Long postId) {
        return baseMapper.getCommentsByPostId(postId);
    }

    @Override
    public int getCommentCountByPostId(Long postId) {
        return baseMapper.getCommentCountByPostId(postId);
    }
}