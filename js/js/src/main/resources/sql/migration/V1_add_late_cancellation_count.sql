-- 添加迟到取消次数字段到学生信息表
-- Migration script to add late_cancellation_count field to student_profiles table

ALTER TABLE student_profiles 
ADD COLUMN late_cancellation_count INT DEFAULT 0 COMMENT '迟到取消次数' 
AFTER weight;
