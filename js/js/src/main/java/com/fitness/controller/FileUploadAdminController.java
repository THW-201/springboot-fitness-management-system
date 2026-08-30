package com.fitness.controller;


import com.fitness.common.Result;
import com.fitness.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "文件上传", description = "文件上传")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadAdminController {

    private final FileStorageService fileStorageService;



    @Operation(summary = "上传文件", tags = "文件上传")
    @PostMapping("/upload")
    public Result upload(@RequestPart("file") MultipartFile file,
                         @RequestParam(value = "folder", required = false) String folder) {
        return Result.success(fileStorageService.save(file, folder));
    }
}
