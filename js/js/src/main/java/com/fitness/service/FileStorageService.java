package com.fitness.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务
 */
public interface FileStorageService {
    /**
     * 保存文件并返回可访问 URL
     */
    String save(MultipartFile file, String folder);
}
