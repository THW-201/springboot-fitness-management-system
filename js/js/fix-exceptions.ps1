# PowerShell script to fix BusinessException constructor calls

$files = @(
    "src/main/java/com/fitness/service/impl/EquipmentServiceImpl.java",
    "src/main/java/com/fitness/service/impl/CourseServiceImpl.java",
    "src/main/java/com/fitness/service/impl/AuthServiceImpl.java",
    "src/main/java/com/fitness/security/RoleBasedAccessControl.java",
    "src/main/java/com/fitness/aspect/RoleCheckAspect.java"
)

$replacements = @{
    'throw new BusinessException\("器材不存在"\)' = 'throw new BusinessException(ResultCode.NOT_FOUND, "器材不存在")'
    'throw new BusinessException\("创建者不存在"\)' = 'throw new BusinessException(ResultCode.NOT_FOUND, "创建者不存在")'
    'throw new BusinessException\("只有管理员或教练可以创建课程"\)' = 'throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或教练可以创建课程")'
    'throw new BusinessException\("指定的教练不存在"\)' = 'throw new BusinessException(ResultCode.NOT_FOUND, "指定的教练不存在")'
    'throw new BusinessException\("指定的用户不是教练"\)' = 'throw new BusinessException(ResultCode.BAD_REQUEST, "指定的用户不是教练")'
    'throw new BusinessException\("结束时间必须晚于开始时间"\)' = 'throw new BusinessException(ResultCode.BAD_REQUEST, "结束时间必须晚于开始时间")'
    'throw new BusinessException\("课程不存在"\)' = 'throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在")'
    'throw new BusinessException\("用户不存在"\)' = 'throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在")'
    'throw new BusinessException\("只有管理员或课程创建者可以更新课程"\)' = 'throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或课程创建者可以更新课程")'
    'throw new BusinessException\("新容量不能小于当前报名人数"\)' = 'throw new BusinessException(ResultCode.BAD_REQUEST, "新容量不能小于当前报名人数")'
    'throw new BusinessException\("只有管理员或课程创建者可以删除课程"\)' = 'throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或课程创建者可以删除课程")'
    'throw new BusinessException\("该课程存在未完成的预约，无法删除"\)' = 'throw new BusinessException(ResultCode.COURSE_HAS_RESERVATIONS, "该课程存在未完成的预约，无法删除")'
    'throw new BusinessException\("用户名已存在"\)' = 'throw new BusinessException(ResultCode.USERNAME_EXISTS, "用户名已存在")'
    'throw new BusinessException\("邮箱已存在"\)' = 'throw new BusinessException(ResultCode.EMAIL_EXISTS, "邮箱已存在")'
    'throw new BusinessException\("学生角色必须提供学号"\)' = 'throw new BusinessException(ResultCode.BAD_REQUEST, "学生角色必须提供学号")'
    'throw new BusinessException\("学号已存在"\)' = 'throw new BusinessException(ResultCode.BAD_REQUEST, "学号已存在")'
    'throw new BusinessException\("用户名或密码错误"\)' = 'throw new BusinessException(ResultCode.INVALID_CREDENTIALS, "用户名或密码错误")'
    'throw new BusinessException\("登出失败"\)' = 'throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "登出失败")'
    'throw new BusinessException\("Token 无效或已过期"\)' = 'throw new BusinessException(ResultCode.TOKEN_INVALID, "Token 无效或已过期")'
    'throw new BusinessException\("Token 已被撤销"\)' = 'throw new BusinessException(ResultCode.TOKEN_INVALID, "Token 已被撤销")'
    'throw new BusinessException\("无权访问该学生数据"\)' = 'throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该学生数据")'
    'throw new BusinessException\("未登录或登录已过期"\)' = 'throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期")'
}

foreach ($file in $files) {
    if (Test-Path $file) {
        $content = Get-Content $file -Raw -Encoding UTF8
        foreach ($pattern in $replacements.Keys) {
            $replacement = $replacements[$pattern]
            $content = $content -replace $pattern, $replacement
        }
        Set-Content $file $content -Encoding UTF8 -NoNewline
        Write-Host "Fixed: $file"
    }
}

Write-Host "All BusinessException calls have been fixed!"
