-- 大学生健身管理系统测试数据
-- 注意：密码使用BCrypt加密，这里的密码都是 "password123"

USE fitness_db;

-- 插入管理员用户
INSERT INTO users (username, password, email, phone, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@fitness.edu.cn', '13800138000', '系统管理员', 'ADMIN', 1);

-- 插入教练用户
INSERT INTO users (username, password, email, phone, real_name, role, status) VALUES
('coach1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'coach1@fitness.edu.cn', '13800138001', '张教练', 'COACH', 1),
('coach2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'coach2@fitness.edu.cn', '13800138002', '李教练', 'COACH', 1);

-- 插入教练信息
INSERT INTO coach_profiles (user_id, specialization, certification, experience_years, bio) VALUES
(2, '力量训练', '国家一级健身教练', 5, '专注于力量训练和体能提升，帮助学生科学健身'),
(3, '瑜伽与柔韧性训练', '国际瑜伽认证教练', 3, '擅长瑜伽和柔韧性训练，注重身心平衡');

-- 插入学生用户
INSERT INTO users (username, password, email, phone, real_name, role, status) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'student1@fitness.edu.cn', '13800138003', '王小明', 'STUDENT', 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'student2@fitness.edu.cn', '13800138004', '李小红', 'STUDENT', 1),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'student3@fitness.edu.cn', '13800138005', '张小华', 'STUDENT', 1);

-- 插入学生信息
INSERT INTO student_profiles (user_id, student_number, coach_id, gender, age, height, weight) VALUES
(4, '2021001', 2, 'MALE', 20, 175.00, 70.00),
(5, '2021002', 2, 'FEMALE', 19, 165.00, 55.00),
(6, '2021003', 3, 'MALE', 21, 180.00, 75.00);

-- 插入课程
INSERT INTO courses (name, description, coach_id, course_type, capacity, current_enrollment, start_time, end_time, location, status, created_by) VALUES
('力量训练基础课', '适合初学者的力量训练课程，学习基本动作和训练方法', 2, '力量训练', 20, 0, '2024-01-15 09:00:00', '2024-01-15 10:30:00', '健身房A区', 'AVAILABLE', 2),
('瑜伽入门课', '瑜伽基础动作教学，提升身体柔韧性和平衡能力', 3, '瑜伽', 15, 0, '2024-01-15 14:00:00', '2024-01-15 15:30:00', '瑜伽室', 'AVAILABLE', 3),
('HIIT高强度间歇训练', '高强度间歇训练，快速燃脂塑形', 2, 'HIIT', 25, 0, '2024-01-16 10:00:00', '2024-01-16 11:00:00', '健身房B区', 'AVAILABLE', 2);

-- 插入器材
INSERT INTO equipment (name, equipment_type, description, location, status, purchase_date) VALUES
('跑步机-01', '有氧器械', 'ProForm跑步机，支持多种训练模式', '有氧区A-01', 'AVAILABLE', '2023-01-01'),
('跑步机-02', '有氧器械', 'ProForm跑步机，支持多种训练模式', '有氧区A-02', 'AVAILABLE', '2023-01-01'),
('哑铃组-10kg', '力量器械', '10kg哑铃一对', '力量区B-01', 'AVAILABLE', '2023-02-01'),
('哑铃组-20kg', '力量器械', '20kg哑铃一对', '力量区B-02', 'AVAILABLE', '2023-02-01'),
('瑜伽垫-01', '辅助器材', '专业瑜伽垫，防滑耐用', '瑜伽室C-01', 'AVAILABLE', '2023-03-01'),
('动感单车-01', '有氧器械', '专业动感单车，可调节阻力', '有氧区A-03', 'AVAILABLE', '2023-04-01');

-- 注意：以下数据仅供参考，实际使用时需要根据当前时间调整
-- 预约、签到、健康计划等数据在系统运行后由用户操作生成
