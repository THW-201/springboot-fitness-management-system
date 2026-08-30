package com.fitness.service.impl;


import com.fitness.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 本地文件存储实现
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.storage.local.base-dir:./uploads}")
    private String baseDir;

    @Value("${file.storage.local.public-url-prefix:http://localhost:8085/api/v1/files/}")
    private String urlPrefix;

    @Override
    public String save(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        try {
            // 1) 取后缀
            String original = file.getOriginalFilename();
            String ext = "";
            if (StringUtils.hasText(original) && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // 2) 生成文件名
            String safeFolder = (folder == null || folder.isBlank()) ? "common" : folder.trim();
            String name = UUID.randomUUID() + ext;

            // 3) 保存
            Path dir = Path.of(baseDir, safeFolder).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(name);
            file.transferTo(target.toFile());

            // 4) 返回 URL
            return  "files/"+safeFolder + "/" + name;

        } catch (Exception e) {
            log.error("file save error", e);
            throw new RuntimeException("文件保存失败");
        }
    }
}
