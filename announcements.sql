-- ----------------------------
-- 公告表结构
-- ----------------------------

-- 公告表
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` enum('SYSTEM','ROLE','PERSONAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SYSTEM' COMMENT '公告类型：SYSTEM-系统公告，ROLE-角色公告，PERSONAL-个人通知',
  `target_roles` json NULL COMMENT '目标角色（当type为ROLE时使用）',
  `target_user_id` bigint NULL DEFAULT NULL COMMENT '目标用户ID（当type为PERSONAL时使用）',
  `status` enum('ACTIVE','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '公告状态',
  `priority` enum('LOW','MEDIUM','HIGH') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '查看次数',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_expire_at`(`expire_at` ASC) USING BTREE,
  INDEX `idx_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_created_by`(`created_by` ASC) USING BTREE,
  CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `announcements_ibfk_2` FOREIGN KEY (`target_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- 插入测试数据
INSERT INTO `announcements` (`title`, `content`, `type`, `target_roles`, `status`, `priority`, `expire_at`, `created_by`) VALUES
('系统上线通知', '大学生健身管理系统正式上线，欢迎使用！', 'SYSTEM', NULL, 'ACTIVE', 'HIGH', DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('教练培训通知', '本周六将进行新教练培训，请各位教练准时参加。', 'ROLE', '["COACH"]', 'ACTIVE', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
('学生活动通知', '下周将举办健身比赛，欢迎同学们积极参与！', 'ROLE', '["STUDENT"]', 'ACTIVE', 'MEDIUM', DATE_ADD(NOW(), INTERVAL 14 DAY), 1);
