/*
 Navicat Premium Data Transfer

 Source Server         : management
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : fitness_db

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 11/06/2026 23:18:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_history
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问答历史ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回答',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '上下文',
  `response_time_ms` int NULL DEFAULT NULL COMMENT '响应时间(毫秒)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `ai_chat_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI问答历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_history
-- ----------------------------
INSERT INTO `ai_chat_history` VALUES (1, 1, '你好', '你好！很高兴能为你提供专业的健身、运动与营养方面的建议。无论你是刚开始接触健身，还是希望优化现有的训练计划，我都可以为你提供帮助。请告诉我你的具体需求，例如：\n\n1. **健身目标**：增肌、减脂、提升耐力、改善体态等。\n2. **运动计划**：如何安排训练频率、动作选择、强度设定等。\n3. **营养建议**：饮食搭配、蛋白质摄入、补剂选择等。\n4. **问题解答**：运动损伤预防、平台期突破、动作细节等。\n\n请随时提出你的问题，我会根据科学原理和实践经验，为你提供安全有效的建议！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 6758, '2026-03-03 10:43:15');
INSERT INTO `ai_chat_history` VALUES (2, 1, '123', '您好！作为您的健身顾问，我很乐意为您提供专业的建议。请告诉我您具体想了解哪方面的问题？例如：\n\n### 1. **健身训练**\n   - 增肌/减脂的训练计划\n   - 动作技巧（如深蹲、卧推的正确姿势）\n   - 家庭健身或健身房器械使用\n   - 运动损伤预防\n\n### 2. **运动营养**\n   - 减脂期/增肌期的饮食搭配\n   - 蛋白质、碳水、脂肪的摄入建议\n   - 运动补剂（蛋白粉、肌酸等）的选择与使用\n\n### 3. **恢复与健康**\n   - 睡眠、拉伸、放松技巧\n   - 如何应对平台期\n   - 体质评估（如代谢类型、体态问题）\n\n### 4. **个性化需求**\n   - 针对特定目标（如马拉松、体能测试）\n   - 不同人群（初学者、孕妇、中老年）的运动建议\n\n请告诉我您的具体目标或困惑，我会为您提供安全、科学且可执行的方案！ 💪\n\n（例如：“我想在3个月内减脂10公斤，该如何安排训练和饮食？”）', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 12010, '2026-03-03 23:26:37');
INSERT INTO `ai_chat_history` VALUES (3, 12, '1', '您好！作为您的专属健身顾问，我很乐意为您提供关于健身、运动、营养等方面的专业建议。无论您是想制定训练计划、改善饮食习惯、解决运动损伤问题，还是了解如何突破平台期，我都会根据科学原理和实际经验为您解答。\n\n**请告诉我您的具体需求，例如：**\n1. **目标**：增肌、减脂、提升体能、改善体态、备战比赛等。\n2. **当前状态**：年龄/性别/健康状况/运动经验/可用时间/可用设备（健身房或居家）。\n3. **具体问题**：动作技巧、饮食搭配、恢复方法、补剂选择等。\n\n**我会为您提供：**\n- **个性化建议**：基于您的目标和条件定制方案。\n- **科学依据**：解释原理（如能量代谢、肌肉生长机制）。\n- **安全提示**：避免常见错误和运动损伤。\n- **实用技巧**：如何坚持计划、高效安排时间等。\n\n**示例问题参考：**\n- “我每周只能去3次健身房，如何高效增肌？”\n- “跑步后膝盖疼，该如何调整训练？”\n- “减脂期如何安排碳水摄入？”\n- “居家训练，哪些动作能有效练核心？”\n\n请随时提出您的问题，我们一步步规划您的健康旅程！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 11048, '2026-03-05 12:47:51');
INSERT INTO `ai_chat_history` VALUES (4, 12, '如何制定减脂计划？', '抱歉，AI服务暂时不可用，请稍后再试。', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 106680, '2026-03-05 12:56:35');
INSERT INTO `ai_chat_history` VALUES (5, 12, '新手应该从哪些训练开始？', '作为健身新手，科学的起步至关重要。以下是为您量身定制的分阶段入门方案，兼顾安全、可持续性和效果：\n\n### 第一阶段：基础适应期（第1-4周）\n**目标：建立运动习惯，激活全身肌群，学习正确动作模式**\n\n1. **心肺功能激活**\n   - 每周3次，每次20-30分钟低强度有氧\n   - 推荐：快走、慢跑、椭圆机、动感单车\n   - 保持心率在（220-年龄）×60%左右\n\n2. **全身性基础训练**（每周2-3次）\n   - **自重训练**：\n     - 徒手深蹲 3组×12次（学习髋关节主导发力）\n     - 跪姿俯卧撑 3组×8次（或墙上俯卧撑）\n     - 平板支撑 3组×30秒\n     - 臀桥 3组×15次\n     - 鸟狗式 每侧3组×10次（提升核心稳定性）\n\n3. **柔韧性训练**\n   - 每次训练后完成10分钟拉伸\n   - 重点：大腿前后侧、胸椎、肩关节活动度\n\n### 第二阶段：系统学习期（第5-12周）\n**目标：掌握基础动作模式，建立神经肌肉连接**\n\n1. **器械入门**（从固定器械开始）\n   - 腿举机 → 学习下肢发力\n   - 高位下拉 → 建立背部发力感\n   - 坐姿推胸 → 感受胸肌收缩\n   - 每动作3组×10-12次，重量选择能标准完成最后2次有挑战的\n\n2. **自由重量准备**\n   - 空杆深蹲学习（注重脊柱中立位）\n   - 哑铃划船（轻重量）\n   - 哑铃卧推（从1-2kg开始）\n\n### 关键原则\n1. **动作质量优先**\n   - 每个动作前观看专业教学视频（推荐Athlean-X、Jeff Nippard等频道）\n   - 可拍摄自己的动作对比纠正\n   - 必要时请专业教练进行3-5节入门指导\n\n2. **渐进超负荷**\n   - 每周尝试增加：次数→重量→组数→缩短组间休息\n   - 示例：深蹲本周3组×10次20kg，下周可尝试3组×12次20kg\n\n3. **恢复管理**\n   - 新手建议练一休一或练二休一\n   - 睡眠保证7-8小时\n   - 泡沫轴放松紧张肌群\n\n### 营养支持\n- 训练后30分钟内补充蛋白质（乳清蛋白/鸡蛋/希腊酸奶）+ 快碳（香蕉/白面包）\n- 每日蛋白质摄入：体重（kg）×1.2-1.5g\n- 保持水分：训练中每20分钟补水150-200ml\n\n### 心理建设\n1. 记录训练日志（动作、重量、感受）\n2. 设置过程性目标（如“本周完成标准平板支撑40秒”）\n3. 加入健身社群获取支持\n\n### 需要立即停止的信号\n- 关节锐痛（区别于肌肉酸痛）\n- 头晕或呼吸困难\n- 动作严重变形仍强行继续\n\n**进阶提示**：完成3个月系统训练后，可根据目标选择分化训练（如推/拉/腿分化）。建议每3个月进行一次体测和动作评估，调整计划。\n\n记住：最好的计划是你能持续执行的计划。从今天开始，完成一次20分钟的全身激活训练就是成功的第一步。', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 64052, '2026-03-05 12:56:40');
INSERT INTO `ai_chat_history` VALUES (6, 12, '你好 你是谁', '你好！我是你的专业健身顾问，专注于提供健身训练、运动科学、营养搭配及健康生活方式的建议。无论你是想增肌、减脂、提升运动表现，还是解决饮食困惑，我都可以根据科学原理为你提供实用方案。请随时告诉我你的具体需求或问题，我会尽力帮助你！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 3850, '2026-03-05 12:57:16');
INSERT INTO `ai_chat_history` VALUES (7, 12, '你是谁', '你好！我是你的专属AI健身顾问，专注于提供科学、个性化的健身、运动与营养建议。无论你是想增肌、减脂、提升运动表现，还是需要饮食规划或动作指导，我都可以为你解答。请随时告诉我你的具体目标或问题，我会尽力帮助你！ 💪\n\n（例如：如何制定家庭训练计划？蛋白粉有必要吗？跑步后如何拉伸？）', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 4143, '2026-03-05 12:59:35');
INSERT INTO `ai_chat_history` VALUES (8, 12, '你是谁', '你好！我是你的专属AI健身顾问，专注于提供科学、个性化的健身、运动与营养建议。无论你是想增肌、减脂、提升运动表现，还是需要饮食规划或动作指导，我都可以为你解答。请随时告诉我你的具体需求或目标，我会尽力帮助你！💪\n\n（例如：想设计家庭训练计划？不知道如何搭配蛋白质摄入？或者有受伤后的恢复疑问？都可以问我！）', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 3898, '2026-03-05 12:59:43');
INSERT INTO `ai_chat_history` VALUES (9, 12, '你是谁', '你好！我是你的专属AI健身顾问，专注于提供科学、个性化的健身、运动和营养建议。无论你是想增肌、减脂、提升运动表现，还是调整饮食计划，我都可以根据你的需求提供专业指导。\n\n**我能帮你解决的问题包括：**\n1. **训练计划**：力量训练、有氧运动、柔韧性训练等。\n2. **动作指导**：动作要领、常见错误纠正、替代动作推荐。\n3. **营养建议**：热量计算、营养素分配、饮食搭配、补剂选择。\n4. **恢复与健康**：睡眠优化、压力管理、运动损伤预防。\n5. **目标定制**：根据你的体能、时间、设备条件设计可行方案。\n\n**需要帮助时，请告诉我：**\n- 你的具体目标（如“减重10斤”）\n- 当前状态（如“新手/有经验”）\n- 可用资源（如“健身房/居家训练”）\n- 其他限制（如“膝盖旧伤”）\n\n我会为你提供安全有效的建议，但请注意：如有健康问题，请先咨询医生。现在，告诉我你想从哪里开始吧！💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 9547, '2026-03-05 13:00:54');
INSERT INTO `ai_chat_history` VALUES (10, 12, '你是谁啊', '你好！我是你的专属AI健身顾问，专注于提供健身训练、运动技巧、营养饮食和健康生活方式的科学建议。无论你是想增肌、减脂、提升体能，还是解决运动损伤、饮食规划等问题，我都可以根据你的需求提供个性化指导。\n\n**我能帮你什么？**\n- 🏋️ **训练计划**：力量训练、有氧运动、柔韧性训练等。\n- 🥗 **营养搭配**：减脂餐单、增肌饮食、补剂选择等。\n- 🔍 **问题解答**：动作细节、恢复技巧、常见误区解析。\n- 📊 **进度调整**：根据你的目标调整训练/饮食方案。\n\n**举个栗子🌰：**\n如果你说：“我想3个月减脂10斤”，我可以为你规划：\n1. 每周训练安排（力量+有氧比例）\n2. 每日热量缺口计算\n3. 食材推荐与食谱示例\n4. 如何监测进度并调整\n\n请随时告诉我你的具体目标或问题，我会用易懂的语言为你拆解！ （例如：健身新手如何开始？蛋白粉有必要喝吗？膝盖疼怎么调整训练？）', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 9961, '2026-03-05 13:04:18');
INSERT INTO `ai_chat_history` VALUES (11, 1, '你好 帮我制定健身计划', '你好！很高兴为你提供健身计划制定的帮助。为了给你一个**安全、有效且个性化**的方案，我需要了解一些基本信息。请告诉我以下情况：\n\n---\n\n### **第一步：请提供你的基本信息**\n1.  **年龄、性别、身高、体重**（评估身体状况）。\n2.  **健身目标**（例如：减脂、增肌、提升力量、改善体能、塑形等）。\n3.  **健身经验**（新手/有一定经验/经常锻炼）。\n4.  **每周可训练天数**（例如：3天、4天、5天）。\n5.  **可用设备和场地**（健身房/家庭/徒手/有哪些器械）。\n6.  **是否有伤病或健康问题**（如膝盖、腰部不适，高血压等）。\n7.  **饮食和作息概况**（例如：是否控制饮食、睡眠时间）。\n\n---\n\n### **第二步：通用健身计划框架（示例）**\n在您提供信息前，我先给出一个**适合新手的通用型3天/周全身训练计划**，您可以根据自身情况调整：\n\n#### **训练频率：每周3天（如周一、三、五），每次60-75分钟**\n**每次训练结构：**\n1.  **热身（5-10分钟）**：动态拉伸（如高抬腿、开合跳）、关节活动。\n2.  **主体训练（45-60分钟）**：\n    - **周一：全身力量（侧重下肢）**\n      - 深蹲 3组×8-12次\n      - 卧推（或俯卧撑）3组×8-12次\n      - 哑铃划船 3组×10-15次\n      - 平板支撑 3组×30-60秒\n      - 弓箭步 3组×每侧10次\n    - **周三：全身力量（侧重上肢）**\n      - 硬拉（或臀桥）3组×8-12次\n      - 杠铃/哑铃推举 3组×8-12次\n      - 引体向上（或高位下拉）3组×尽力做\n      - 哑铃弯举 3组×10-15次\n      - 仰卧举腿 3组×15-20次\n    - **周五：全身综合+有氧**\n      - 重复周一的动作，或选择其他全身动作（如腿举、俯身飞鸟等）\n      - 结束后增加20分钟低强度有氧（快走、慢跑、跳绳）。\n3.  **拉伸放松（5-10分钟）**：静态拉伸主要肌群。\n\n---\n\n### **第三步：关键原则与建议**\n1.  **渐进超负荷**：每周逐渐增加重量、次数或组数，让身体持续适应。\n2.  **动作质量优先**：确保姿势正确，避免受伤。新手建议从轻重量开始。\n3.  **营养与恢复**：\n    - **增肌**：保证蛋白质摄入（每公斤体重1.6-2.2克），热量略高于日常消耗。\n    - **减脂**：适度热量缺口（300-500卡/天），保持高蛋白饮食。\n    - **睡眠**：每天7-9小时，促进肌肉修复。\n4.  **有氧训练**：根据目标安排（减脂者可每周增加2-3次30分钟有氧）。\n5.  **记录与调整**：记录训练内容和身体感受，每4-6周调整计划。\n\n---\n\n### **请补充你的信息，我会为你优化具体方案！**\n例如：\n- 如果你的目标是**减脂**，我会增加有氧和HIIT建议。\n- 如果你是**健身房新手**，我会细化动作指导和替代动作。\n- 如果你只有**家庭哑铃**，我会提供居家训练版本。\n\n期待你的回复，祝你健身顺利！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 28417, '2026-03-07 19:48:11');
INSERT INTO `ai_chat_history` VALUES (12, 1, '晚上好', '晚上好！很高兴能为您提供健身、运动或营养方面的专业建议。请告诉我您具体想了解什么，例如：\n\n1. **训练计划**：增肌、减脂、体能提升等目标如何安排？  \n2. **动作指导**：某个动作的正确姿势或替代方案？  \n3. **营养搭配**：饮食规划、补剂选择、减脂/增肌期饮食要点？  \n4. **恢复与伤痛**：拉伸放松、运动损伤预防或缓解？  \n5. **其他疑问**：健身装备选择、时间安排、心态调整等？\n\n请随时提出您的具体问题，我会为您提供科学、安全的建议！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 6134, '2026-03-07 19:50:55');
INSERT INTO `ai_chat_history` VALUES (13, 1, '你好  你是谁', '你好！我是你的专属AI健身顾问，专注于提供健身、运动、营养和健康生活方式的科学建议。无论你是想制定训练计划、调整饮食、改善体态，还是解决运动中的具体问题，我都可以根据你的需求提供个性化指导。\n\n**我的服务范围包括：**\n1. **健身训练**：增肌、减脂、力量训练、柔韧性提升等。\n2. **运动康复**：动作纠正、伤痛预防、恢复建议。\n3. **营养搭配**：热量计算、膳食规划、补剂选择。\n4. **习惯养成**：动力提升、时间管理、可持续计划制定。\n\n**请告诉我你的具体目标或问题**，例如：\n- “如何安全地开始第一次力量训练？”\n- “减脂期如何安排三餐？”\n- “跑步后膝盖疼怎么办？”\n- “适合办公室人群的拉伸动作有哪些？”\n\n我会为你提供清晰、安全且实用的方案！ 🏋️♂️🥗✨', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 8649, '2026-03-07 22:30:03');
INSERT INTO `ai_chat_history` VALUES (14, 1, '如何制定减脂计划？', '抱歉，AI服务暂时不可用，请稍后再试。', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 71118, '2026-03-07 22:42:43');
INSERT INTO `ai_chat_history` VALUES (15, 1, '新手应该从哪些训练开始？', '作为健身新手，科学的起步至关重要。以下是为您量身定制的系统化入门指南，涵盖训练、营养与安全三大核心：\n\n### 一、训练入门：从基础到进阶\n**1. 第一步：建立运动习惯（第1-2周）**\n   - **目标**：每周3次，每次20-30分钟低强度活动。\n   - **推荐项目**：\n     - 快走/慢跑：提升心肺功能。\n     - 基础动态拉伸：改善关节灵活性。\n     - 自重训练：墙壁俯卧撑、椅子深蹲、跪姿平板支撑（每组8-12次，2-3组）。\n\n**2. 第二步：全身性力量训练（第3-8周）**\n   - **频率**：每周3次，隔天进行，确保肌肉恢复。\n   - **核心动作（学习正确姿势优先于重量）**：\n     - **深蹲**：锻炼下肢与核心。\n     - **俯卧撑**（可从跪姿开始）：强化胸肩手臂。\n     - **划船**（使用弹力带或哑铃）：平衡前后肌群，预防圆肩。\n     - **平板支撑**：提升核心稳定性（从30秒开始）。\n   - **建议方案**：每个动作3组，每组8-12次，组间休息60秒。\n\n**3. 第三步：逐步增加强度（2个月后）**\n   - 加入哑铃/壶铃训练（如哑铃推举、罗马尼亚硬拉）。\n   - 尝试分化训练（如推/拉/腿分化）。\n   - 引入间歇训练：如30秒快跑+60秒慢走，重复6-8组。\n\n### 二、营养支持：为身体充电\n- **运动前**（1-2小时）：少量碳水+蛋白质（如香蕉+酸奶）。\n- **运动后**（30分钟内）：快速吸收营养（乳清蛋白+水果，或鸡蛋+全麦面包）。\n- **每日基础**：\n  - 蛋白质：体重（kg）×1.2-1.5克（鸡胸肉、鱼、豆制品）。\n  - 碳水：以粗粮为主（燕麦、糙米），提供持久能量。\n  - 水分：每天饮水≥2升，运动时每20分钟补水150-200ml。\n\n### 三、安全与恢复：避免受伤的关键\n1. **热身与放松**：每次训练前5-10分钟动态热身（开合跳、高抬腿），结束后静态拉伸。\n2. **循序渐进**：每周训练量增幅不超过10%（如重量、次数、时长）。\n3. **睡眠优先**：保证7-8小时睡眠，肌肉在休息时修复生长。\n4. **警惕信号**：关节刺痛、持续酸痛超过72小时需休息并咨询专业人士。\n\n### 四、心理建设：保持动力的秘诀\n- **设定SMART目标**：例如“8周内完成标准俯卧撑3组×10次”。\n- **记录进展**：用健身APP或笔记本记录训练数据，可视化进步。\n- **寻找社群**：加入线上/线下健身小组，互相激励。\n\n### 快速自查清单：\n- [ ] 训练前是否热身？\n- [ ] 动作姿势是否正确？（可拍摄视频对比教程）\n- [ ] 次日是否感觉轻微酸痛而非剧痛？\n- [ ] 饮食是否包含足量蛋白质和蔬菜？\n\n**最后提醒**：如有高血压、关节病史或长期不运动，建议先进行体检并咨询医生。健身是马拉松而非冲刺——**前3个月的核心是建立神经肌肉连接和身体适应性**，而非追求极限重量。每一次坚持都在重塑你的身体与心态，现在就开始你的第一次训练吧！', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 29777, '2026-03-07 22:43:42');
INSERT INTO `ai_chat_history` VALUES (16, 14, '如何制定减脂计划？', '你好！制定一个科学有效的减脂计划，需要综合考虑**饮食、运动、恢复和心态**四大支柱。以下是一份为你量身打造的系统性指南，你可以根据自身情况进行调整。\n\n### **第一步：明确目标与评估现状（计划基石）**\n1.  **设定SMART目标**：\n    *   **具体**：例如“在3个月内减掉5公斤脂肪”。\n    *   **可衡量**：用体重、体脂率、腰围、照片作为指标。\n    *   **可实现**：每周减重0.5-1公斤是安全可持续的速度。\n    *   **相关**：确保这个目标对你个人有重要意义。\n    *   **有时限**：设定明确的起止日期和阶段性检查点。\n2.  **评估现状**：\n    *   **记录**：记录当前体重、体脂（如有设备）、饮食和运动习惯。\n    *   **计算基础代谢（BMR）和每日总消耗（TDEE）**：使用在线计算器估算你每日维持当前体重所需的热量。\n\n### **第二步：营养规划（减脂的核心，占70%重要性）**\n1.  **创造热量缺口**：\n    *   在TDEE基础上，每日减少 **300-500千卡** 的热量摄入，这是安全且可持续的缺口。\n    *   **切勿过度节食**（低于1200千卡/天），否则会导致肌肉流失、代谢下降和反弹。\n2.  **优化宏量营养素比例**（仅供参考，可调整）：\n    *   **蛋白质**：**1.6-2.2克/公斤体重**。保证饱腹感，维持肌肉。来源：鸡胸肉、鱼虾、鸡蛋、豆制品、瘦肉。\n    *   **脂肪**：**占总热量的20-30%**。选择健康脂肪，如牛油果、坚果、橄榄油、鱼油。\n    *   **碳水化合物**：**用剩余热量计算**。优先选择复合碳水，如燕麦、糙米、薯类、全麦面包、蔬菜。\n3.  **执行关键饮食原则**：\n    *   **多吃天然食物**：保证蔬菜（占餐盘一半）、优质蛋白和复合碳水的摄入。\n    *   **戒除/限制空热量食物**：严格控制添加糖、含糖饮料、油炸食品、精加工零食。\n    *   **多喝水**：每天喝够 **体重（公斤）× 30-40毫升** 的水。\n    *   **烹饪方式**：以蒸、煮、烤、快炒为主，少油少盐。\n    *   **规律进食**：少食多餐或一日三餐，避免长时间饥饿导致暴食。\n\n### **第三步：运动规划（塑造形体、提升代谢）**\n采用 **“力量训练 + 有氧运动”** 相结合的最高效模式。\n1.  **力量训练（增肌、提高基础代谢）**：\n    *   **频率**：每周 **3-4次**，每次45-60分钟。\n    *   **内容**：以复合动作为主（深蹲、硬拉、卧推、划船、推举），针对全身大肌群。\n    *   **强度**：选择8-12次力竭的重量，做3-5组。\n2.  **有氧运动（直接消耗热量）**：\n    *   **频率**：每周 **3-5次**，每次30-45分钟。\n    *   **方式**：可以选择慢跑、骑行、游泳、椭圆机等。\n    *   **高效策略**：尝试 **高强度间歇训练（HIIT）**，每周1-2次，20分钟效果显著，但强度大，适合有基础者。\n3.  **安排建议**：\n    *   **方案A（分日进行）**：周一、三、五力量训练；周二、四有氧；周末休息或低强度活动（如散步）。\n    *   **方案B（同日进行）**：先进行力量训练，紧接着进行有氧训练，效率更高。\n\n### **第四步：恢复与心态（可持续发展的保障）**\n1.  **睡眠**：保证每晚 **7-9小时** 的高质量睡眠。睡眠不足会扰乱激素（如皮质醇升高、瘦素降低），极大阻碍减脂。\n2.  **压力管理**：长期压力同样会导致皮质醇升高，增加腹部脂肪堆积。可通过冥想、散步、爱好来减压。\n3.  **耐心与坚持**：减脂不是线性的，会有平台期。关注长期趋势，而非每日体重波动。\n4.  **记录与调整**：每周固定时间称重、测量围度、拍照对比。如果连续2-3周体重', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 22815, '2026-03-11 14:37:28');
INSERT INTO `ai_chat_history` VALUES (17, 1, '如何制定减脂计划？', '你好！制定一个科学有效的减脂计划，需要从**饮食、训练、休息和心态**四个方面系统规划。以下是一份详细的步骤指南，你可以根据自身情况进行调整。\n\n### **第一步：明确目标与评估现状（基石）**\n1.  **设定SMART目标**：\n    *   **具体**：例如“3个月内减重5公斤”或“腰围减少5厘米”。\n    *   **可衡量**：用体重、体脂率、围度、照片作为指标。\n    *   **可实现**：每周减重0.5-1公斤是健康且可持续的速度。\n    *   **相关**：确保这个目标对你个人有重要意义。\n    *   **有时限**：设定明确的起止日期。\n2.  **评估当前状态**：\n    *   **记录**：记录当前体重、体脂率（如有设备）、主要身体围度（胸、腰、臀、臂、腿）。\n    *   **计算基础代谢（BMR）和每日总消耗（TDEE）**：\n        *   使用在线计算器，根据你的年龄、身高、体重、活动水平计算出**TDEE**（维持当前体重所需的热量）。\n    *   **了解自身**：有无伤病、运动经验、可用时间、饮食偏好等。\n\n### **第二步：核心原则——创造热量缺口**\n*   **原理**：减脂的根本是“热量摄入 < 热量消耗”。\n*   **安全缺口**：在TDEE基础上，每天减少 **300-500千卡** 的热量摄入，或通过运动增加同等消耗。**总缺口不建议超过500-700千卡**，以免导致肌肉流失和代谢下降。\n*   **示例**：如果你的TDEE是2000千卡，那么每日摄入可设定在1500-1700千卡。\n\n### **第三步：营养计划（减脂成功的关键，占70%以上比重）**\n1.  **均衡宏观营养素**：\n    *   **蛋白质**：**至关重要！** 保证每公斤体重摄入 **1.6-2.2克** 蛋白质。它能增加饱腹感、维持肌肉、提高食物热效应。来源：鸡胸肉、鱼虾、鸡蛋、瘦肉、豆制品、乳清蛋白。\n    *   **碳水化合物**：选择**复合碳水**，提供持久能量。控制精制碳水和添加糖。来源：燕麦、糙米、藜麦、红薯、全麦面包、蔬菜。\n    *   **脂肪**：摄入**健康脂肪**，保证激素正常分泌。来源：牛油果、坚果、橄榄油、鱼油。\n2.  **饮食结构建议**：\n    *   **多吃天然食物**：蔬菜（大量）、优质蛋白、全谷物。\n    *   **控制烹饪用油**：减少煎炸，多用蒸、煮、烤、快炒。\n    *   **戒除/严格限制**：含糖饮料、甜品、油炸食品、深度加工零食。\n    *   **多喝水**：每天喝够 **体重（kg）× 30-40毫升** 的水。\n    *   **规律进食**：少食多餐或一日三餐，避免长时间饥饿导致暴食。\n3.  **简单执行法**：\n    *   **餐盘法则**：每餐保证1/2盘蔬菜、1/4盘蛋白质、1/4盘复合碳水。\n\n### **第四步：训练计划（塑造形体、提升代谢）**\n最佳策略是 **力量训练 + 有氧运动** 结合。\n1.  **力量训练（增肌、提高基础代谢）**：\n    *   **频率**：每周 **3-4次**，每次45-60分钟。\n    *   **内容**：以**复合动作为主**（深蹲、硬拉、卧推、划船、推举），能调动更多肌肉，消耗更多热量。\n    *   **原则**：渐进超负荷，逐步增加重量、组数或次数。\n2.  **有氧运动（直接消耗热量）**：\n    *   **频率**：每周 **3-5次**，每次30-45分钟。\n    *   **类型**：\n        *   **稳态有氧**：如慢跑、游泳、骑车，保持中等强度（心率在最大心率的60-70%）。\n        *   **高强度间歇训练（HIIT）**：如冲刺跑、波比跳，耗时短、效率高、有后燃效应，但对心肺要求高，适合有基础者。每周1-2次即可。\n3.  **日常活动消耗（NEAT）**：\n    *   多走路、走楼梯、做家务。这是容易被忽视但非常重要的部分。\n\n### **第五步', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 46389, '2026-03-14 21:27:31');
INSERT INTO `ai_chat_history` VALUES (18, 15, '你好', '你好！很高兴能为你提供专业的健身、运动与营养方面的建议。无论你是刚开始接触健身，还是希望优化现有的训练计划，我都可以为你提供帮助。请告诉我你的具体需求，例如：\n\n1. **健身目标**：增肌、减脂、提升耐力、改善体态等。  \n2. **运动计划**：如何安排训练频率、动作选择、强度设定等。  \n3. **营养建议**：饮食搭配、蛋白质摄入、补剂选择等。  \n4. **问题排查**：遇到平台期、运动损伤恢复、疲劳管理等。  \n\n你可以直接描述你的情况或提出问题，我会根据科学原理和实践经验为你提供详细指导！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 8857, '2026-03-14 21:33:54');
INSERT INTO `ai_chat_history` VALUES (19, 15, '你专业嘛', '当然专业！作为您的专属健身顾问，我在以下领域拥有系统知识，可以为您提供科学、安全且个性化的建议：\n\n**1. 健身训练**\n   - **动作指导**：力量训练（自由重量/器械）、有氧运动、柔韧性训练等动作细节、常见错误纠正。\n   - **计划制定**：增肌、减脂、塑形、提升运动表现等不同目标的分阶段方案。\n   - **运动康复**：常见运动损伤预防、恢复性训练建议（注：严重损伤需优先就医）。\n\n**2. 运动营养**\n   - **饮食规划**：根据目标（如增肌/减脂）计算宏观营养素需求，设计饮食结构。\n   - **补剂指南**：蛋白粉、肌酸、维生素等常见补剂的科学使用场景与注意事项。\n   - **实战策略**：外食选择、运动前后饮食安排、水分补充方案等。\n\n**3. 健康管理**\n   - **体态评估**：久坐人群的肌力平衡调整、日常姿势纠正。\n   - **习惯养成**：可持续的运动习惯培养、疲劳管理与睡眠优化。\n   - **特殊人群**：孕期运动、中老年适应性训练等注意事项（需结合医生建议）。\n\n**我的原则**：\n✅ **安全第一**：不推荐极端饮食或危险动作。\n✅ **证据为本**：建议均基于运动生理学及营养学研究。\n✅ **个性化**：需了解您的年龄、健康状况、运动经验等背景才能给出精准方案。\n\n**请随时提出具体问题**，例如：\n- “我想在3个月内安全减脂10斤，该如何安排训练和饮食？”\n- “深蹲时膝盖疼痛，可能是什么原因？”\n- “健身前后吃什么能最大化效果？”\n\n请补充您的**具体目标、健康状况、运动经验**等信息，我会为您定制方案！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 19129, '2026-03-14 21:53:02');
INSERT INTO `ai_chat_history` VALUES (20, 12, '你好', '你好！很高兴能为你提供专业的健身、运动与营养方面的建议。无论你是刚刚开始接触健身，还是希望优化现有的训练计划，或者有任何关于饮食、恢复、目标设定等方面的问题，都可以随时向我提问。\n\n为了更高效地帮助你，你可以告诉我：\n1. **你的具体目标**（例如：增肌、减脂、提升耐力、改善体态、准备赛事等）。\n2. **当前的运动习惯**（例如：每周训练频率、喜欢的运动类型、有无伤病等）。\n3. **饮食情况**（例如：日常饮食结构、是否有特殊饮食需求或限制）。\n4. **任何具体的困惑**（例如：动作技巧、计划安排、营养补充、恢复方法等）。\n\n我会根据科学原理和实践经验，为你提供安全、个性化且可执行的建议。\n\n**举个常见问题的例子：**\n* “我想减脂，应该先做有氧还是力量训练？”\n* “增肌期间每天需要摄入多少蛋白质？”\n* “跑步后膝盖疼，该如何调整？”\n\n请随时告诉我你的需求，我们一起制定适合你的方案！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 10954, '2026-03-15 18:17:54');
INSERT INTO `ai_chat_history` VALUES (21, 15, '我想把体重从130斤减到100斤，有什么推荐', '你好！很高兴能作为你的健身顾问，为你提供减重建议。从130斤减到100斤（即减重30斤）是一个需要耐心和科学方法的过程，通常建议在**4-6个月**内完成，以保证健康且不易反弹。以下是综合运动、饮食和生活习惯的详细方案：\n\n---\n\n### **一、科学设定目标**\n- **合理速度**：每周减重0.5-1斤（每月2-4斤）是安全可持续的速度，避免快速减肥导致肌肉流失或代谢下降。\n- **关注体脂率**：建议用体脂率或腰围变化衡量进度，而非只看体重。\n\n---\n\n### **二、饮食调整（核心关键）**\n#### **1. 控制热量缺口**\n   - 每日比日常消耗少摄入 **300-500大卡**，可通过APP（如MyFitnessPal）粗略计算基础代谢和摄入。\n   - **示例**：若你每日消耗约1800大卡，则摄入控制在1300-1500大卡。\n\n#### **2. 优化饮食结构**\n   - **蛋白质优先**（增强饱腹感、维持肌肉）：\n     - 每餐包含鸡蛋、鸡胸肉、鱼虾、豆腐、希腊酸奶等。\n   - **复合碳水为主**（稳定血糖）：\n     - 用糙米、燕麦、全麦面包替代白米白面。\n   - **多吃蔬菜和纤维**：\n     - 绿叶蔬菜占餐盘一半，水果选低糖类（如莓果、苹果）。\n   - **健康脂肪适量**：\n     - 坚果、牛油果、橄榄油，每天一小把坚果或一勺油即可。\n   - **戒掉添加糖和加工食品**：\n     - 避免奶茶、甜点、油炸食品，警惕沙拉酱、果汁等“隐形糖”。\n\n#### **3. 饮食习惯**\n   - 喝足水（每天2L），餐前一杯水可减少进食量。\n   - 吃饭时细嚼慢咽，每餐用时不少于20分钟。\n   - 记录饮食，避免无意识摄入。\n\n---\n\n### **三、运动计划（结合有氧与力量）**\n#### **1. 力量训练（每周3-4次）**\n   - **目的**：增加肌肉量，提高基础代谢。\n   - **动作**：深蹲、俯卧撑（可从跪姿开始）、哑铃划船、臀桥等。\n   - **建议**：每次30-45分钟，每个动作3-4组，每组10-15次。\n\n#### **2. 有氧运动（每周3-4次）**\n   - **方式**：快走、慢跑、跳绳、游泳或骑行。\n   - **建议**：每次30-40分钟，可尝试间歇训练（如快慢跑交替）提高燃脂效率。\n\n#### **3. 日常活动增加**\n   - 多走路、爬楼梯，每天步数争取达到8000-10000步。\n\n---\n\n### **四、生活习惯与心态**\n1. **睡眠充足**：每天睡7-8小时，缺乏睡眠会增加饥饿激素分泌。\n2. **压力管理**：通过冥想、散步缓解压力，避免情绪化进食。\n3. **定期监测**：每周固定时间称体重并测量围度，但不必每天称重。\n4. **允许偶尔放松**：每周可安排一顿“自由餐”，避免过度压抑导致暴食。\n\n---\n\n### **五、注意事项**\n- **警惕极端方法**：不要过度节食（如每日低于1200大卡）或完全戒断碳水，可能导致营养不良、脱发或月经失调。\n- **平台期应对**：如果体重停滞2周以上，可调整运动方式（如增加强度）或微调饮食结构。\n- **健康优先**：如有健康问题（如低血糖、胃病），请咨询医生或营养师调整方案。\n\n---\n\n### **示例一日饮食计划**\n- **早餐**：2个水煮蛋 + 全麦面包1片 + 无糖豆浆1杯\n- **午餐**：掌心大小鸡胸肉 + 糙米饭半碗 + 西兰花炒香菇（少油）\n- **加餐**：希腊酸奶100g 或 苹果1个\n- **晚餐**：清蒸鱼1块 + 凉拌菠菜豆腐 + 紫薯半个\n\n---\n\n**最后提醒**：减重是长期健康习惯的培养过程，不要追求完美。遇到挫折时，调整心态比严格坚持更重要。如果需要更个性化的方案，可以告诉我你的身高、年龄、日常活动量以及是否有运动基础，我会帮你进一步细化！加油！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 42228, '2026-03-15 18:21:24');
INSERT INTO `ai_chat_history` VALUES (22, 15, '如何健身', '很高兴能作为你的健身顾问，为你提供专业的指导！健身是一个系统性的过程，需要结合科学的训练、合理的营养和充足的恢复。以下是一份全面的健身入门指南，你可以根据自身情况调整。\n\n### **第一步：明确目标**\n首先问自己：**你想通过健身达到什么目的？**\n- **减脂塑形**：降低体脂率，让身材更紧致。\n- **增肌增力**：增加肌肉量，提升力量。\n- **提升体能**：增强耐力、灵活性或心肺功能。\n- **保持健康**：缓解压力，改善体态，提升免疫力。\n\n### **第二步：制定训练计划**\n一个平衡的计划应包含以下要素：\n\n#### **1. 有氧运动（减脂、心肺健康）**\n- **频率**：每周3-5次，每次20-45分钟。\n- **选择**：慢跑、游泳、骑行、跳绳、跳操等。\n- **建议**：初学者可从低强度开始（如快走），逐渐增加强度。\n\n#### **2. 力量训练（增肌、塑形、提升代谢）**\n- **频率**：每周2-4次，针对不同肌群轮流训练。\n- **基础动作**：\n  - **下肢**：深蹲、箭步蹲、臀桥\n  - **上肢**：俯卧撑（可从跪姿开始）、哑铃划船、哑铃卧推\n  - **核心**：平板支撑、卷腹、鸟狗式\n- **关键原则**：\n  - **渐进超负荷**：逐渐增加重量、次数或组数。\n  - **动作质量**：确保姿势正确，避免受伤。\n  - **休息**：每组间休息60-90秒，同一肌群训练后休息48小时。\n\n#### **3. 柔韧性与恢复**\n- **拉伸**：每次训练后动态拉伸（训练前）和静态拉伸（训练后）。\n- **泡沫轴放松**：缓解肌肉紧张。\n- **休息日**：每周至少安排1-2天完全休息，避免过度训练。\n\n### **第三步：营养与饮食**\n- **均衡膳食**：\n  - **蛋白质**：鸡胸肉、鱼、鸡蛋、豆制品（修复肌肉）。\n  - **碳水化合物**：糙米、燕麦、薯类（提供能量）。\n  - **健康脂肪**：牛油果、坚果、橄榄油。\n  - **蔬菜水果**：补充维生素和纤维。\n- **饮水**：每天饮水量（毫升）≈ 体重（kg）× 30。\n- **减脂期**：适量控制热量，避免极端节食。\n- **增肌期**：保证热量盈余，注重蛋白质摄入。\n\n### **第四步：生活习惯与恢复**\n- **睡眠**：每天7-9小时高质量睡眠，促进肌肉修复。\n- **压力管理**：长期压力会影响健身效果，可通过冥想、散步调节。\n- **保持活跃**：非训练日多走动，避免久坐。\n\n### **给初学者的建议**\n1. **循序渐进**：从低强度开始，逐步提升，避免受伤。\n2. **记录进展**：记录训练内容、饮食和身体变化，便于调整。\n3. **寻求指导**：如有条件，可请专业教练指导动作。\n4. **保持耐心**：健身效果通常需要3-6个月才能明显显现，坚持是关键！\n\n### **示例周计划（初学者）**\n- **周一**：全身力量训练（深蹲、俯卧撑、划船等） + 拉伸\n- **周二**：有氧运动（慢跑30分钟）\n- **周三**：休息或轻度活动（散步、瑜伽）\n- **周四**：全身力量训练（调整动作或增加难度）\n- **周五**：有氧运动（跳绳、游泳）\n- **周六**：核心训练 + 拉伸\n- **周日**：休息\n\n### **注意事项**\n- **热身与冷身**：每次训练前热身5-10分钟（如开合跳、动态拉伸），训练后冷身拉伸。\n- **倾听身体**：疼痛（非正常酸痛）时应停止训练并检查动作。\n- **如有健康问题**：咨询医生后再开始新的运动计划。\n\n如果你有更具体的目标（如想重点练某个部位）、或遇到执行困难，可以告诉我更多细节，我会为你进一步定制方案！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题。', 38654, '2026-03-15 18:22:21');
INSERT INTO `ai_chat_history` VALUES (23, 1, '在吗', '您好！在的，随时为您提供专业的健身建议。无论您是想开始运动、调整训练计划，还是改善饮食营养，都可以告诉我您的具体需求或目标。例如：\n\n1. **健身入门**：建议从每周3次全身性训练开始，结合力量（如深蹲、俯卧撑）和有氧（快走、慢跑），注意动作规范以防受伤。\n2. **营养搭配**：运动后30分钟内补充蛋白质（如鸡蛋、乳清蛋白）和碳水（全麦面包），帮助肌肉修复。每日饮水需充足（约2-3升）。\n3. **常见问题**：如时间有限，可尝试高强度间歇训练（HIIT），每次20分钟高效燃脂；睡眠和拉伸同样重要，能提升恢复效果。\n\n请告诉我您的具体情况，我会为您定制方案！', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右', 5507, '2026-04-08 17:49:27');
INSERT INTO `ai_chat_history` VALUES (24, 1, '怎么练出蜜桃臀', '要练出饱满上翘的“蜜桃臀”，关键在于**针对性力量训练+充足营养+科学恢复**。以下是核心建议：\n\n**1. 重点训练动作（每周2-3次）：**\n- **复合动作**：深蹲、硬拉（特别是罗马尼亚硬拉）、臀推——这些是增肌王牌，能全面刺激臀大肌、臀中肌。\n- **孤立强化**：箭步蹲、跪姿后踢腿、髋外展（弹力带或器械）——精准雕刻上臀与侧臀线条，提升圆润度。\n\n**2. 关键技巧：**\n- **注重离心收缩**：动作下放时慢速控制（如深蹲下蹲时），增加肌肉微撕裂。\n- **顶峰收缩**：在动作顶端（如臀推最高点）夹紧臀部保持1-2秒。\n- **渐进超负荷**：逐步增加负重、组数或次数（建议每组8-12次，做3-4组）。\n\n**3. 营养与恢复：**\n- **蛋白质充足**：每日摄入1.6-2.2克/公斤体重（如鸡胸肉、鸡蛋、蛋白粉），支持肌肉修复。\n- **热量适度盈余**：增肌需略超日常消耗（约多300卡），避免过量脂肪堆积。\n- **充分休息**：保证睡眠，训练间隔48小时让肌肉生长，可搭配泡沫轴放松。\n\n**注意**：动作质量优于重量，避免腰部代偿。建议初期跟随专业教练学习发力模式，结合有氧（如爬坡走）减少脂肪覆盖，让臀形更清晰。坚持3-6个月可见明显变化！', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右', 10751, '2026-04-08 17:50:27');
INSERT INTO `ai_chat_history` VALUES (25, 17, '你好', '您好！作为您的健身顾问，很高兴为您提供专业建议。\n\n**关于健身入门：** 建议每周进行3-4次训练，结合力量训练（如深蹲、俯卧撑）和有氧运动（如快走、慢跑）。初期以掌握正确动作为主，避免受伤。\n\n**关于营养：** 饮食需均衡，每餐包含优质蛋白质（鸡胸肉、豆类）、复合碳水（糙米、燕麦）和蔬菜。运动后及时补充蛋白质和水分，帮助身体恢复。\n\n**关键提醒：** 睡眠和恢复同样重要，保证每晚7-8小时睡眠。如有特殊健康问题，请先咨询医生。坚持与耐心是取得长期效果的核心！\n\n如需具体计划，请告诉我您的目标和身体状况，我会进一步为您定制方案。祝您健康旅程顺利！ 💪', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右', 6875, '2026-04-08 19:17:26');
INSERT INTO `ai_chat_history` VALUES (26, 17, '请帮我制定一个个性化的健身计划', '为了制定真正适合你的个性化计划，我需要先了解几个关键信息：**你的当前体能水平**（如是否有运动基础）、**主要目标**（减脂、增肌、塑形还是提升耐力）、**可投入时间**（每周几次，每次多久）、**可用器械**（健身房/居家/无器械）以及**有无伤病或限制**。\n\n**作为通用参考**，一个均衡的健身计划通常包含：\n- **力量训练**（每周3-4次）：如深蹲、俯卧撑、划船等复合动作，每组8-12次，做3-4组。\n- **有氧运动**（每周2-3次）：如快走、慢跑或骑行，每次20-40分钟，保持心率在最大心率的60%-70%。\n- **营养配合**：保证每公斤体重摄入1.2-2克蛋白质，多吃蔬菜和全谷物，控制精制糖和油炸食品。\n\n请告诉我你的具体情况，我会为你细化方案。比如：想减脂且每周能去3次健身房，我会设计以复合动作为主、结合间歇有氧的计划。', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右', 3585, '2026-04-24 19:16:19');
INSERT INTO `ai_chat_history` VALUES (27, 15, '如何去减脂', '减脂的核心是**制造热量缺口**（摄入<消耗），但需科学进行避免代谢损伤。\n\n**饮食方面**：调整结构而非单纯节食。提高蛋白质（鸡胸、鱼虾、蛋奶）和膳食纤维（蔬菜）比例，用粗粮（燕麦、红薯）替代精米白面。控制油盐糖，戒掉含糖饮料和加工零食。建议每餐七分饱，每天热量缺口控制在300-500大卡。\n\n**运动方面**：**力量训练+有氧结合**效果最佳。力量训练（深蹲、卧推、划船）能保留肌肉、提高基础代谢；有氧（慢跑、游泳、HIIT）直接消耗脂肪。每周安排3-4次力量+2-3次有氧，每次40-60分钟。\n\n**生活细节**：保证7-8小时睡眠，皮质醇过高会阻碍减脂；多喝水（每公斤体重30-40ml）；不要追求快速掉秤，体脂率下降比体重更重要。\n\n记住：减脂是长期习惯养成，坚持3个月以上才能看到稳定效果。', '你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右', 4003, '2026-04-25 17:19:12');

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '系统上线通知', '大学生健身管理系统正式上线，欢迎使用！', 'SYSTEM', NULL, NULL, 'ACTIVE', 'HIGH', '2026-05-03 16:48:53', 10, 1, '2026-04-03 16:48:53', '2026-04-25 15:56:30');
INSERT INTO `announcements` VALUES (2, '教练培训通知', '本周六将进行新教练培训，请各位教练准时参加。', 'ROLE', '[\"COACH\"]', NULL, 'ACTIVE', 'MEDIUM', '2026-04-10 16:48:53', 0, 1, '2026-04-03 16:48:53', '2026-04-03 16:48:53');
INSERT INTO `announcements` VALUES (3, '学生活动通知', '下周将举办健身比赛，欢迎同学们积极参与！', 'ROLE', '[\"STUDENT\"]', NULL, 'ACTIVE', 'MEDIUM', '2026-04-17 16:48:53', 0, 1, '2026-04-03 16:48:53', '2026-04-03 16:48:53');

-- ----------------------------
-- Table structure for audit_logs
-- ----------------------------
DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE `audit_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审计日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源类型',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '资源ID',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作详情',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_action`(`action` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 232 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审计日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of audit_logs
-- ----------------------------
INSERT INTO `audit_logs` VALUES (1, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:43:25');
INSERT INTO `audit_logs` VALUES (2, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:43:30');
INSERT INTO `audit_logs` VALUES (3, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:47:28');
INSERT INTO `audit_logs` VALUES (4, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:47:35');
INSERT INTO `audit_logs` VALUES (5, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:49:08');
INSERT INTO `audit_logs` VALUES (6, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 13:49:11');
INSERT INTO `audit_logs` VALUES (7, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 14:14:02');
INSERT INTO `audit_logs` VALUES (8, 1, 'UPDATE', 'USER', 12, '方法: UserController.updateUser', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 14:26:00');
INSERT INTO `audit_logs` VALUES (9, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 14:29:04');
INSERT INTO `audit_logs` VALUES (10, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 15:34:44');
INSERT INTO `audit_logs` VALUES (11, 1, 'UPDATE', 'COURSE', 3, '方法: CourseController.updateCourse', '111.85.57.126', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 15:47:55');
INSERT INTO `audit_logs` VALUES (12, 1, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '182.150.47.149', 'Apifox/1.0.0 (https://apifox.com)', '2026-03-05 15:54:17');
INSERT INTO `audit_logs` VALUES (13, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '111.85.59.94', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 16:00:28');
INSERT INTO `audit_logs` VALUES (14, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '182.150.47.149', 'Apifox/1.0.0 (https://apifox.com)', '2026-03-05 16:52:13');
INSERT INTO `audit_logs` VALUES (15, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.94', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 16:53:26');
INSERT INTO `audit_logs` VALUES (16, 11, 'DELETE', 'RESERVATION', 14, '方法: ReservationController.cancelReservation', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 17:30:04');
INSERT INTO `audit_logs` VALUES (17, 11, 'DELETE', 'RESERVATION', 15, '方法: ReservationController.cancelReservation', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 17:39:58');
INSERT INTO `audit_logs` VALUES (18, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 17:40:02');
INSERT INTO `audit_logs` VALUES (19, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 18:32:16');
INSERT INTO `audit_logs` VALUES (20, 11, 'DELETE', 'RESERVATION', 17, '方法: ReservationController.cancelReservation', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 18:34:19');
INSERT INTO `audit_logs` VALUES (21, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 18:35:07');
INSERT INTO `audit_logs` VALUES (22, 1, 'DELETE', 'EQUIPMENT', 7, '方法: EquipmentController.deleteEquipment', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-05 18:41:19');
INSERT INTO `audit_logs` VALUES (23, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 21:04:05');
INSERT INTO `audit_logs` VALUES (24, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.57.252', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-05 21:08:23');
INSERT INTO `audit_logs` VALUES (25, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-06 22:05:44');
INSERT INTO `audit_logs` VALUES (26, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-06 22:05:49');
INSERT INTO `audit_logs` VALUES (27, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-06 22:05:59');
INSERT INTO `audit_logs` VALUES (28, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-06 22:06:25');
INSERT INTO `audit_logs` VALUES (29, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-07 19:42:33');
INSERT INTO `audit_logs` VALUES (30, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:46:37');
INSERT INTO `audit_logs` VALUES (31, 1, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:47:25');
INSERT INTO `audit_logs` VALUES (32, 1, 'DELETE', 'COURSE', 5, '方法: CourseController.deleteCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:48:36');
INSERT INTO `audit_logs` VALUES (33, 1, 'DELETE', 'EQUIPMENT', 8, '方法: EquipmentController.deleteEquipment', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:48:39');
INSERT INTO `audit_logs` VALUES (34, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:49:40');
INSERT INTO `audit_logs` VALUES (35, 1, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:50:28');
INSERT INTO `audit_logs` VALUES (36, 1, 'DELETE', 'USER', 10, '方法: UserController.deleteUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:50:40');
INSERT INTO `audit_logs` VALUES (37, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-07 19:52:08');
INSERT INTO `audit_logs` VALUES (38, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 19:52:36');
INSERT INTO `audit_logs` VALUES (39, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-07 19:52:57');
INSERT INTO `audit_logs` VALUES (40, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-07 21:04:07');
INSERT INTO `audit_logs` VALUES (41, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:04:07');
INSERT INTO `audit_logs` VALUES (42, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:47:11');
INSERT INTO `audit_logs` VALUES (43, 1, 'DELETE', 'COURSE', 6, '方法: CourseController.deleteCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:47:19');
INSERT INTO `audit_logs` VALUES (44, 1, 'DELETE', 'USER', 9, '方法: UserController.deleteUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:54:22');
INSERT INTO `audit_logs` VALUES (45, 1, 'DELETE', 'USER', 8, '方法: UserController.deleteUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:54:24');
INSERT INTO `audit_logs` VALUES (46, 1, 'DELETE', 'USER', 7, '方法: UserController.deleteUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 21:54:26');
INSERT INTO `audit_logs` VALUES (47, 1, 'DELETE', 'COURSE', 7, '方法: CourseController.deleteCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:15:10');
INSERT INTO `audit_logs` VALUES (48, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:27:34');
INSERT INTO `audit_logs` VALUES (49, 1, 'DELETE', 'COURSE', 8, '方法: CourseController.deleteCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:27:38');
INSERT INTO `audit_logs` VALUES (50, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:28:45');
INSERT INTO `audit_logs` VALUES (51, 1, 'DELETE', 'USER', 4, '方法: UserController.deleteUser', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:29:44');
INSERT INTO `audit_logs` VALUES (52, 12, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:34:09');
INSERT INTO `audit_logs` VALUES (53, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:34:39');
INSERT INTO `audit_logs` VALUES (54, 11, 'DELETE', 'RESERVATION', 20, '方法: ReservationController.cancelReservation', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:34:49');
INSERT INTO `audit_logs` VALUES (55, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:34:55');
INSERT INTO `audit_logs` VALUES (56, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '116.171.172.46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-07 22:35:14');
INSERT INTO `audit_logs` VALUES (57, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '182.150.47.149', 'Apifox/1.0.0 (https://apifox.com)', '2026-03-07 23:12:24');
INSERT INTO `audit_logs` VALUES (58, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '182.150.47.149', 'Apifox/1.0.0 (https://apifox.com)', '2026-03-07 23:12:45');
INSERT INTO `audit_logs` VALUES (59, 1, 'UPDATE', 'USER', 14, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 10:43:34');
INSERT INTO `audit_logs` VALUES (60, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:09:16');
INSERT INTO `audit_logs` VALUES (61, 12, 'UPDATE', 'USER', 12, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:11:05');
INSERT INTO `audit_logs` VALUES (62, 11, 'DELETE', 'RESERVATION', 25, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:35:23');
INSERT INTO `audit_logs` VALUES (63, 11, 'DELETE', 'RESERVATION', 22, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:35:25');
INSERT INTO `audit_logs` VALUES (64, 11, 'DELETE', 'RESERVATION', 19, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:35:26');
INSERT INTO `audit_logs` VALUES (65, 11, 'DELETE', 'RESERVATION', 23, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:36:10');
INSERT INTO `audit_logs` VALUES (66, 11, 'DELETE', 'RESERVATION', 24, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:36:12');
INSERT INTO `audit_logs` VALUES (67, 11, 'DELETE', 'RESERVATION', 21, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:36:14');
INSERT INTO `audit_logs` VALUES (68, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:36:29');
INSERT INTO `audit_logs` VALUES (69, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:41:43');
INSERT INTO `audit_logs` VALUES (70, 1, 'UPDATE', 'COURSE', 9, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:47:10');
INSERT INTO `audit_logs` VALUES (71, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:48:30');
INSERT INTO `audit_logs` VALUES (72, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:54:49');
INSERT INTO `audit_logs` VALUES (73, 1, 'UPDATE', 'EQUIPMENT', 10, '方法: EquipmentController.updateEquipment', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:56:39');
INSERT INTO `audit_logs` VALUES (74, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 13:57:04');
INSERT INTO `audit_logs` VALUES (75, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 13:57:57');
INSERT INTO `audit_logs` VALUES (76, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:02:19');
INSERT INTO `audit_logs` VALUES (77, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:03:46');
INSERT INTO `audit_logs` VALUES (78, 11, 'DELETE', 'RESERVATION', 27, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:04:02');
INSERT INTO `audit_logs` VALUES (79, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:04:12');
INSERT INTO `audit_logs` VALUES (80, 11, 'DELETE', 'RESERVATION', 28, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:05:43');
INSERT INTO `audit_logs` VALUES (81, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:05:53');
INSERT INTO `audit_logs` VALUES (82, 1, 'UPDATE', 'COURSE', 2, '方法: CourseController.updateCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 14:06:43');
INSERT INTO `audit_logs` VALUES (83, 11, 'DELETE', 'RESERVATION', 29, '方法: ReservationController.cancelReservation', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:07:17');
INSERT INTO `audit_logs` VALUES (84, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:08:48');
INSERT INTO `audit_logs` VALUES (85, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:24:17');
INSERT INTO `audit_logs` VALUES (86, 1, 'UPDATE', 'EQUIPMENT', 10, '方法: EquipmentController.updateEquipment', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36', '2026-03-11 14:31:55');
INSERT INTO `audit_logs` VALUES (87, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:32:48');
INSERT INTO `audit_logs` VALUES (88, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:43:50');
INSERT INTO `audit_logs` VALUES (89, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:44:09');
INSERT INTO `audit_logs` VALUES (90, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 14:44:15');
INSERT INTO `audit_logs` VALUES (91, 11, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 15:05:30');
INSERT INTO `audit_logs` VALUES (92, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 15:05:52');
INSERT INTO `audit_logs` VALUES (93, 11, 'UPDATE', 'USER', 11, '方法: UserController.updateUser', '111.85.59.154', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '2026-03-11 15:06:02');
INSERT INTO `audit_logs` VALUES (94, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-16 02:16:25');
INSERT INTO `audit_logs` VALUES (95, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-16 02:17:15');
INSERT INTO `audit_logs` VALUES (96, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-16 02:17:28');
INSERT INTO `audit_logs` VALUES (97, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-16 02:21:01');
INSERT INTO `audit_logs` VALUES (98, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-17 00:46:11');
INSERT INTO `audit_logs` VALUES (99, 15, 'CREATE', 'UNKNOWN', 15, '方法: SocialController.createPost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-17 20:36:28');
INSERT INTO `audit_logs` VALUES (100, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 11:28:43');
INSERT INTO `audit_logs` VALUES (101, 15, 'CREATE', 'UNKNOWN', 1, '方法: SocialController.createComment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:14:38');
INSERT INTO `audit_logs` VALUES (102, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:23:13');
INSERT INTO `audit_logs` VALUES (103, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:31:36');
INSERT INTO `audit_logs` VALUES (104, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:33:19');
INSERT INTO `audit_logs` VALUES (105, 15, 'DELETE', 'UNKNOWN', 1, '方法: SocialController.deletePost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:34:00');
INSERT INTO `audit_logs` VALUES (106, 15, 'CREATE', 'UNKNOWN', 15, '方法: SocialController.createPost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:48:03');
INSERT INTO `audit_logs` VALUES (107, 15, 'CREATE', 'UNKNOWN', 2, '方法: SocialController.createComment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:48:14');
INSERT INTO `audit_logs` VALUES (108, 15, 'CREATE', 'UNKNOWN', 15, '方法: SocialController.createPost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:51:41');
INSERT INTO `audit_logs` VALUES (109, 15, 'CREATE', 'UNKNOWN', 15, '方法: SocialController.createPost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-19 19:52:11');
INSERT INTO `audit_logs` VALUES (110, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:29:46');
INSERT INTO `audit_logs` VALUES (111, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:29:47');
INSERT INTO `audit_logs` VALUES (112, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:29:48');
INSERT INTO `audit_logs` VALUES (113, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:29:49');
INSERT INTO `audit_logs` VALUES (114, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:29:49');
INSERT INTO `audit_logs` VALUES (115, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-20 11:35:19');
INSERT INTO `audit_logs` VALUES (116, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-20 11:35:31');
INSERT INTO `audit_logs` VALUES (117, 15, 'CREATE', 'UNKNOWN', NULL, '方法: ExerciseRecordController.createRecord', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-20 11:35:35');
INSERT INTO `audit_logs` VALUES (118, 15, 'CREATE', 'UNKNOWN', 15, '方法: SocialController.createPost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:55:28');
INSERT INTO `audit_logs` VALUES (119, 15, 'CREATE', 'UNKNOWN', 5, '方法: SocialController.createComment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 11:55:38');
INSERT INTO `audit_logs` VALUES (120, 1, 'UPDATE', 'USER', 1, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 19:08:43');
INSERT INTO `audit_logs` VALUES (121, 16, 'UPDATE', 'USER', 16, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 19:12:06');
INSERT INTO `audit_logs` VALUES (122, 16, 'UPDATE', 'USER', 16, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 19:26:53');
INSERT INTO `audit_logs` VALUES (123, 15, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 19:29:31');
INSERT INTO `audit_logs` VALUES (124, 1, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-20 21:07:07');
INSERT INTO `audit_logs` VALUES (125, 15, 'UPDATE', 'UNKNOWN', 5, '方法: SocialController.updatePost', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-23 20:14:20');
INSERT INTO `audit_logs` VALUES (126, 15, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-24 13:17:03');
INSERT INTO `audit_logs` VALUES (127, 17, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-24 20:33:27');
INSERT INTO `audit_logs` VALUES (128, 15, 'DELETE', 'RESERVATION', 36, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 20:58:52');
INSERT INTO `audit_logs` VALUES (129, 17, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:06:02');
INSERT INTO `audit_logs` VALUES (130, 17, 'DELETE', 'RESERVATION', 38, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:08:39');
INSERT INTO `audit_logs` VALUES (131, 17, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:11:29');
INSERT INTO `audit_logs` VALUES (132, 17, 'DELETE', 'RESERVATION', 39, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:11:38');
INSERT INTO `audit_logs` VALUES (133, 17, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:11:43');
INSERT INTO `audit_logs` VALUES (134, 17, 'DELETE', 'RESERVATION', 40, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:11:52');
INSERT INTO `audit_logs` VALUES (135, 17, 'CREATE', 'RESERVATION', NULL, '方法: ReservationController.reserveCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:12:24');
INSERT INTO `audit_logs` VALUES (136, 17, 'DELETE', 'RESERVATION', 41, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:12:37');
INSERT INTO `audit_logs` VALUES (137, 1, 'DELETE', 'COURSE', 2, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:07');
INSERT INTO `audit_logs` VALUES (138, 1, 'DELETE', 'COURSE', 4, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:14');
INSERT INTO `audit_logs` VALUES (139, 1, 'DELETE', 'COURSE', 9, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:16');
INSERT INTO `audit_logs` VALUES (140, 1, 'DELETE', 'COURSE', 10, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:18');
INSERT INTO `audit_logs` VALUES (141, 1, 'DELETE', 'COURSE', 11, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:21');
INSERT INTO `audit_logs` VALUES (142, 1, 'DELETE', 'USER', 3, '方法: UserController.deleteUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:44');
INSERT INTO `audit_logs` VALUES (143, 1, 'DELETE', 'USER', 5, '方法: UserController.deleteUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:49');
INSERT INTO `audit_logs` VALUES (144, 1, 'DELETE', 'USER', 6, '方法: UserController.deleteUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-26 21:26:50');
INSERT INTO `audit_logs` VALUES (145, 1, 'UPDATE', 'COURSE', 3, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-28 10:49:08');
INSERT INTO `audit_logs` VALUES (146, 1, 'UPDATE', 'COURSE', 1, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-28 10:49:33');
INSERT INTO `audit_logs` VALUES (147, 1, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-28 10:51:01');
INSERT INTO `audit_logs` VALUES (148, 1, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-28 10:51:25');
INSERT INTO `audit_logs` VALUES (149, 15, 'CREATE', 'HEALTH_PLAN', NULL, '方法: HealthPlanController.createHealthPlan', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-28 11:11:19');
INSERT INTO `audit_logs` VALUES (150, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 22:16:13');
INSERT INTO `audit_logs` VALUES (151, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 22:35:18');
INSERT INTO `audit_logs` VALUES (152, 15, 'DELETE', 'RESERVATION', 35, '方法: ReservationController.cancelReservation', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 22:58:25');
INSERT INTO `audit_logs` VALUES (153, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 23:08:08');
INSERT INTO `audit_logs` VALUES (154, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-30 23:09:27');
INSERT INTO `audit_logs` VALUES (155, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 23:19:51');
INSERT INTO `audit_logs` VALUES (156, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 23:27:43');
INSERT INTO `audit_logs` VALUES (157, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 23:46:06');
INSERT INTO `audit_logs` VALUES (158, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-30 23:54:44');
INSERT INTO `audit_logs` VALUES (159, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:02:37');
INSERT INTO `audit_logs` VALUES (160, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 20:07:11');
INSERT INTO `audit_logs` VALUES (161, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 20:08:28');
INSERT INTO `audit_logs` VALUES (162, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 20:08:45');
INSERT INTO `audit_logs` VALUES (163, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 20:09:16');
INSERT INTO `audit_logs` VALUES (164, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:10:36');
INSERT INTO `audit_logs` VALUES (165, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 20:18:55');
INSERT INTO `audit_logs` VALUES (166, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:25:22');
INSERT INTO `audit_logs` VALUES (167, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:32:05');
INSERT INTO `audit_logs` VALUES (168, 1, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:37:48');
INSERT INTO `audit_logs` VALUES (169, 1, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:43:41');
INSERT INTO `audit_logs` VALUES (170, 1, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:49:36');
INSERT INTO `audit_logs` VALUES (171, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 20:56:22');
INSERT INTO `audit_logs` VALUES (172, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:02:14');
INSERT INTO `audit_logs` VALUES (173, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:18:55');
INSERT INTO `audit_logs` VALUES (174, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:30:39');
INSERT INTO `audit_logs` VALUES (175, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:34:01');
INSERT INTO `audit_logs` VALUES (176, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:37:09');
INSERT INTO `audit_logs` VALUES (177, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:37:23');
INSERT INTO `audit_logs` VALUES (178, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:39:00');
INSERT INTO `audit_logs` VALUES (179, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-03-31 21:45:27');
INSERT INTO `audit_logs` VALUES (180, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 21:49:51');
INSERT INTO `audit_logs` VALUES (181, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 21:53:23');
INSERT INTO `audit_logs` VALUES (182, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 21:57:01');
INSERT INTO `audit_logs` VALUES (183, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-03-31 22:20:57');
INSERT INTO `audit_logs` VALUES (184, 1, 'UPDATE', 'EQUIPMENT', 10, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:42:13');
INSERT INTO `audit_logs` VALUES (185, 1, 'UPDATE', 'EQUIPMENT', 1, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:43:22');
INSERT INTO `audit_logs` VALUES (186, 1, 'UPDATE', 'EQUIPMENT', 2, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:43:35');
INSERT INTO `audit_logs` VALUES (187, 1, 'UPDATE', 'EQUIPMENT', 3, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:44:26');
INSERT INTO `audit_logs` VALUES (188, 1, 'UPDATE', 'EQUIPMENT', 4, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:44:33');
INSERT INTO `audit_logs` VALUES (189, 1, 'UPDATE', 'EQUIPMENT', 5, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:45:31');
INSERT INTO `audit_logs` VALUES (190, 1, 'UPDATE', 'EQUIPMENT', 6, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:47:18');
INSERT INTO `audit_logs` VALUES (191, 1, 'UPDATE', 'EQUIPMENT', 11, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 17:58:48');
INSERT INTO `audit_logs` VALUES (192, 1, 'UPDATE', 'EQUIPMENT', 11, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:01:33');
INSERT INTO `audit_logs` VALUES (193, 1, 'UPDATE', 'EQUIPMENT', 1, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:03:19');
INSERT INTO `audit_logs` VALUES (194, 1, 'UPDATE', 'EQUIPMENT', 2, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:05:42');
INSERT INTO `audit_logs` VALUES (195, 1, 'UPDATE', 'EQUIPMENT', 5, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:05:53');
INSERT INTO `audit_logs` VALUES (196, 1, 'UPDATE', 'EQUIPMENT', 4, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:06:55');
INSERT INTO `audit_logs` VALUES (197, 1, 'UPDATE', 'EQUIPMENT', 6, '方法: EquipmentController.updateEquipment', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:08:01');
INSERT INTO `audit_logs` VALUES (198, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:10:07');
INSERT INTO `audit_logs` VALUES (199, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:10:09');
INSERT INTO `audit_logs` VALUES (200, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-08 18:10:21');
INSERT INTO `audit_logs` VALUES (201, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 17:07:46');
INSERT INTO `audit_logs` VALUES (202, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 17:47:46');
INSERT INTO `audit_logs` VALUES (203, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 17:53:33');
INSERT INTO `audit_logs` VALUES (204, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 18:49:36');
INSERT INTO `audit_logs` VALUES (205, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 18:50:29');
INSERT INTO `audit_logs` VALUES (206, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 18:59:27');
INSERT INTO `audit_logs` VALUES (207, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:00:13');
INSERT INTO `audit_logs` VALUES (208, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-04-24 19:00:42');
INSERT INTO `audit_logs` VALUES (209, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-04-24 19:01:06');
INSERT INTO `audit_logs` VALUES (210, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-04-24 19:01:15');
INSERT INTO `audit_logs` VALUES (211, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Mobile Safari/537.36', '2026-04-24 19:08:53');
INSERT INTO `audit_logs` VALUES (212, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:18:01');
INSERT INTO `audit_logs` VALUES (213, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:21:34');
INSERT INTO `audit_logs` VALUES (214, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateStudentProfile', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:21:34');
INSERT INTO `audit_logs` VALUES (215, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:21:38');
INSERT INTO `audit_logs` VALUES (216, 15, 'UPDATE', 'USER', 15, '方法: UserController.updateStudentProfile', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 19:21:38');
INSERT INTO `audit_logs` VALUES (217, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 23:37:36');
INSERT INTO `audit_logs` VALUES (218, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 23:43:09');
INSERT INTO `audit_logs` VALUES (219, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 23:44:44');
INSERT INTO `audit_logs` VALUES (220, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-24 23:51:57');
INSERT INTO `audit_logs` VALUES (221, 16, 'CREATE', 'COURSE', NULL, '方法: CourseController.createCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', '2026-04-25 00:06:01');
INSERT INTO `audit_logs` VALUES (222, 16, 'UPDATE', 'COURSE', 13, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', '2026-04-25 00:06:43');
INSERT INTO `audit_logs` VALUES (223, 16, 'UPDATE', 'COURSE', 13, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', '2026-04-25 00:07:23');
INSERT INTO `audit_logs` VALUES (224, 16, 'DELETE', 'COURSE', 13, '方法: CourseController.deleteCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', '2026-04-25 00:07:32');
INSERT INTO `audit_logs` VALUES (225, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', '2026-04-25 00:25:12');
INSERT INTO `audit_logs` VALUES (226, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 00:27:38');
INSERT INTO `audit_logs` VALUES (227, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 00:27:56');
INSERT INTO `audit_logs` VALUES (228, 16, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 00:29:52');
INSERT INTO `audit_logs` VALUES (229, 1, 'UPDATE', 'COURSE', 12, '方法: CourseController.updateCourse', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 00:47:12');
INSERT INTO `audit_logs` VALUES (230, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateUser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 13:46:31');
INSERT INTO `audit_logs` VALUES (231, 17, 'UPDATE', 'USER', 17, '方法: UserController.updateStudentProfile', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36', '2026-04-25 13:46:31');

-- ----------------------------
-- Table structure for check_ins
-- ----------------------------
DROP TABLE IF EXISTS `check_ins`;
CREATE TABLE `check_ins`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `reservation_id` bigint NOT NULL COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `check_in_time` timestamp NOT NULL COMMENT '签到时间',
  `check_out_time` timestamp NULL DEFAULT NULL COMMENT '签退时间',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '活动时长(分钟)',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签到位置',
  `calories_burned` decimal(8, 2) NULL DEFAULT NULL COMMENT '消耗卡路里',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `coach_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reservation_id`(`reservation_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_check_in_time`(`check_in_time` ASC) USING BTREE,
  CONSTRAINT `check_ins_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `check_ins_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签到表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of check_ins
-- ----------------------------
INSERT INTO `check_ins` VALUES (6, 34, 11, '2026-03-11 15:05:34', '2026-03-11 15:07:15', 1, '健身房A区', 5.00, '2026-03-11 15:05:34', '2026-03-11 15:05:34', 12);
INSERT INTO `check_ins` VALUES (7, 36, 15, '2026-03-24 13:17:28', '2026-03-26 20:58:33', 3341, '二楼B区05号', 16705.00, '2026-03-24 13:17:28', '2026-03-24 13:17:28', NULL);
INSERT INTO `check_ins` VALUES (8, 35, 15, '2026-03-27 10:02:38', NULL, NULL, '健身房A区', NULL, '2026-03-27 10:02:38', '2026-03-27 10:02:38', NULL);

-- ----------------------------
-- Table structure for coach_profiles
-- ----------------------------
DROP TABLE IF EXISTS `coach_profiles`;
CREATE TABLE `coach_profiles`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教练信息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `specialization` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业领域',
  `certification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资格证书',
  `experience_years` int NULL DEFAULT NULL COMMENT '从业年限',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `intro_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教练介绍图片URL列表（JSON数组）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `coach_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coach_profiles
-- ----------------------------
INSERT INTO `coach_profiles` VALUES (5, 16, '普拉提', '国家一级教练', 15, NULL, '2026-03-20 19:11:23', '2026-03-20 19:11:23', NULL, NULL);

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程描述',
  `coach_id` bigint NOT NULL COMMENT '授课教练ID',
  `course_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程类型',
  `capacity` int NOT NULL COMMENT '容量',
  `current_enrollment` int NULL DEFAULT 0 COMMENT '当前报名人数',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课地点',
  `status` enum('AVAILABLE','FULL','CANCELLED','COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE' COMMENT '课程状态',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程图片URL',
  `class_schedule` json NULL COMMENT '课程上课时间安排',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_course_type`(`course_type` ASC) USING BTREE,
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `courses_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (1, '力量训练基础课', '适合初学者的力量训练课程，学习基本动作和训练方法', 12, '力量训练', 20, 3, '2026-04-10 21:48:16', '2028-01-06 02:30:00', '健身房A区', 'AVAILABLE', 2, '2026-03-02 23:18:39', '2026-03-02 23:18:39', 'files/course/77792ade-c30b-4aa2-922a-f969b428c0fe.jpg', NULL);
INSERT INTO `courses` VALUES (3, 'HIIT高强度间歇训练', '高强度间歇训练，快速燃脂塑形', 12, 'HIIT', 25, 1, '2026-05-01 02:00:00', '2026-06-16 03:00:00', '健身房B区', 'AVAILABLE', 2, '2026-03-02 23:18:39', '2026-03-02 23:18:39', 'files/course/96dbb109-920f-4bbc-9090-2a01c5e79188.jpg', NULL);
INSERT INTO `courses` VALUES (12, '普拉提', '针对身体的塑性和提升核心力量', 16, '塑性', 10, 1, '2026-04-24 16:00:00', '2026-04-30 13:00:00', '瑜伽室', 'AVAILABLE', 1, '2026-03-20 21:07:07', '2026-03-20 21:07:07', 'files/course/bea9af61-1f7c-48ab-aaf6-3d88ea048727.jpeg', '[{\"endTime\": \"10\", \"dayOfWeek\": \"6\", \"startTime\": \"9\"}]');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '器材名称',
  `equipment_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '器材描述',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存放位置',
  `status` enum('AVAILABLE','IN_USE','MAINTENANCE','DAMAGED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE' COMMENT '器材状态',
  `purchase_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `last_maintenance_date` date NULL DEFAULT NULL COMMENT '最后维护日期',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材图片URL',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `intro_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '器材介绍图片URL列表（JSON数组）',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '负责教练ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_equipment_type`(`equipment_type` ASC) USING BTREE,
  INDEX `idx_equipment_coach_id`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '器材表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '跑步机-01', '有氧器械', '跑步机是健身房有氧区核心基础器械，也是居家减脂健身的主流选择，为电动智能型健身设备，核心由跑台、操控面板、减震底座、调速 / 调坡组件构成，主流分商用款（健身房专用，机身厚重、减震性强、承重大，适配高频次多人使用）和家用款（机身轻便、占地小，带折叠 / 静音设计，适配居家低频次使用），操作无门槛，新手可快速上手。\n机身核心配置贴合不同训练需求：操控面板可显示速度、时间、里程、卡路里、心率等数据，支持速度调节（0.5-20km/h） 和坡度调节（0-18°），部分智能款带蓝牙、投屏、预设减脂 / 燃脂 / 爬坡等训练模式；跑台搭配硅胶 / 气垫减震层，能有效缓冲跑步时对膝盖、脚踝、腰椎的地面冲击力，相比户外跑步更护关节，适配全年龄段人群。\n核心作用以提升心肺功能、高效燃脂减脂为主，可通过调节速度和坡度实现多样化有氧训练：低速走步（3-6km/h）适合热身、放松或中老年人群；中速慢跑（6-10km/h）为常规燃脂模式，适配日常减脂；高速快跑（10km/h 以上）可提升心肺耐力；爬坡模式（调坡 + 中低速）能兼顾有氧燃脂与臀腿塑形，还可作为力量训练后的收尾有氧，是健身入门、日常减脂、体能基础训练的万能器械，也是健身房有氧区的必备款。', '有氧区A-01', 'AVAILABLE', '2023-01-01', NULL, 'files/equipment/0de66372-3e63-422f-b6ac-6265fe9ff9ef.png', '2026-03-02 23:18:39', '2026-04-10 01:47:43', NULL, 16);
INSERT INTO `equipment` VALUES (2, '跑步机-02', '有氧器械', '跑步机是健身房有氧区核心基础器械，也是居家减脂健身的主流选择，为电动智能型健身设备，核心由跑台、操控面板、减震底座、调速 / 调坡组件构成，主流分商用款（健身房专用，机身厚重、减震性强、承重大，适配高频次多人使用）和家用款（机身轻便、占地小，带折叠 / 静音设计，适配居家低频次使用），操作无门槛，新手可快速上手。\n机身核心配置贴合不同训练需求：操控面板可显示速度、时间、里程、卡路里、心率等数据，支持速度调节（0.5-20km/h） 和坡度调节（0-18°），部分智能款带蓝牙、投屏、预设减脂 / 燃脂 / 爬坡等训练模式；跑台搭配硅胶 / 气垫减震层，能有效缓冲跑步时对膝盖、脚踝、腰椎的地面冲击力，相比户外跑步更护关节，适配全年龄段人群。\n核心作用以提升心肺功能、高效燃脂减脂为主，可通过调节速度和坡度实现多样化有氧训练：低速走步（3-6km/h）适合热身、放松或中老年人群；中速慢跑（6-10km/h）为常规燃脂模式，适配日常减脂；高速快跑（10km/h 以上）可提升心肺耐力；爬坡模式（调坡 + 中低速）能兼顾有氧燃脂与臀腿塑形，还可作为力量训练后的收尾有氧，是健身入门、日常减脂、体能基础训练的万能器械，也是健身房有氧区的必备款。', '有氧区A-02', 'IN_USE', '2023-01-01', NULL, 'files/equipment/7bda3ba9-2205-4009-997c-01d4deeb83ef.png', '2026-03-02 23:18:39', '2026-03-02 23:18:39', NULL, NULL);
INSERT INTO `equipment` VALUES (3, '哑铃组-10kg', '力量器械', '哑铃组是健身房力量区核心的自由重量器材，也是居家力量训练的经典配置，由多副不同重量的哑铃搭配专属收纳架组成，按重量调节方式分固定重量哑铃和可调节重量哑铃两类，材质、重量梯度适配健身房高频使用与居家灵活训练需求，是覆盖新手到进阶人群，实现全身肌群增肌、塑形、提升基础力量的万能器械。\n核心分类与规格\n固定重量哑铃：健身房主流款，单只重量从 1kg 起，以 2.5kg/5kg 为梯度递增（如 1kg、2.5kg、5kg、8kg、10kg…50kg+），部分健身房配备 1.25kg 小梯度款适配新手；材质分包胶款（防滑、减震、静音，避免砸伤地面 / 器械，健身房首选）、电镀款（光滑耐磨、颜值高，适合居家），握柄多为防滑滚花设计，防止训练时手滑脱落。\n可调节重量哑铃：居家优选，单只可通过增减配重片调节重量（常规调节范围 5kg-30kg），占地小、性价比高，替代多副固定哑铃，适合训练需求多变但空间有限的场景，部分高端款带快速调节卡扣，切换重量更便捷。\n收纳架：哑铃组标配，分立式架/层架/圆形架，按重量梯度分层摆放，方便取放与收纳，保持训练区域整洁，健身房多为大容量层架，居家多为小型立式架。\n核心特性\n操作无场地限制，轨迹灵活可自由控制，能实现单肌群精准训练与多肌群协同复合训练，相比固定力量器械，更能激活核心稳定性，避免肌肉代偿；重量梯度丰富，新手可从低重量（1-5kg）起步找发力感，进阶人群可通过大重量实现增肌突破，适配全年龄段、不同训练水平人群，且训练动作多样，无需复杂调试，拿起即可训练。\n核心训练作用\n覆盖全身所有肌群的塑形与力量提升，无训练盲区：\n上肢：弯举练肱二头肌、臂屈伸练肱三头肌、侧平举练三角肌、推胸 / 划船辅助练肩背；\n下肢：高脚杯深蹲练股四头肌 / 臀部、箭步蹲辅助提升核心稳定；\n核心：俄罗斯转体、平板支撑夹铃等动作，强化腰腹深层肌肉；\n全身复合：搭配训练凳可完成哑铃卧推、哑铃划船、哑铃硬拉等动作，兼顾增肌、塑形、提升基础力量，既是新手力量入门的首选器械，也是进阶人群突破训练瓶颈的核心器材，可单独训练，也可作为固定器械、杠铃训练的补充，适配热身、正式训练、收尾塑形全流程。', '力量区B-01', 'AVAILABLE', '2023-02-01', NULL, NULL, '2026-03-02 23:18:39', '2026-04-10 01:47:43', NULL, 16);
INSERT INTO `equipment` VALUES (4, '哑铃组-20kg', '力量器械', '哑铃组是健身房力量区核心的自由重量器材，也是居家力量训练的经典配置，由多副不同重量的哑铃搭配专属收纳架组成，按重量调节方式分固定重量哑铃和可调节重量哑铃两类，材质、重量梯度适配健身房高频使用与居家灵活训练需求，是覆盖新手到进阶人群，实现全身肌群增肌、塑形、提升基础力量的万能器械。\n核心分类与规格\n固定重量哑铃：健身房主流款，单只重量从 1kg 起，以 2.5kg/5kg 为梯度递增（如 1kg、2.5kg、5kg、8kg、10kg…50kg+），部分健身房配备 1.25kg 小梯度款适配新手；材质分包胶款（防滑、减震、静音，避免砸伤地面 / 器械，健身房首选）、电镀款（光滑耐磨、颜值高，适合居家），握柄多为防滑滚花设计，防止训练时手滑脱落。\n可调节重量哑铃：居家优选，单只可通过增减配重片调节重量（常规调节范围 5kg-30kg），占地小、性价比高，替代多副固定哑铃，适合训练需求多变但空间有限的场景，部分高端款带快速调节卡扣，切换重量更便捷。\n收纳架：哑铃组标配，分立式架/层架/圆形架，按重量梯度分层摆放，方便取放与收纳，保持训练区域整洁，健身房多为大容量层架，居家多为小型立式架。\n核心特性\n操作无场地限制，轨迹灵活可自由控制，能实现单肌群精准训练与多肌群协同复合训练，相比固定力量器械，更能激活核心稳定性，避免肌肉代偿；重量梯度丰富，新手可从低重量（1-5kg）起步找发力感，进阶人群可通过大重量实现增肌突破，适配全年龄段、不同训练水平人群，且训练动作多样，无需复杂调试，拿起即可训练。\n核心训练作用\n覆盖全身所有肌群的塑形与力量提升，无训练盲区：\n上肢：弯举练肱二头肌、臂屈伸练肱三头肌、侧平举练三角肌、推胸 / 划船辅助练肩背；\n下肢：高脚杯深蹲练股四头肌 / 臀部、箭步蹲辅助提升核心稳定；\n核心：俄罗斯转体、平板支撑夹铃等动作，强化腰腹深层肌肉；\n全身复合：搭配训练凳可完成哑铃卧推、哑铃划船、哑铃硬拉等动作，兼顾增肌、塑形、提升基础力量，既是新手力量入门的首选器械，也是进阶人群突破训练瓶颈的核心器材，可单独训练，也可作为固定器械、杠铃训练的补充，适配热身、正式训练、收尾塑形全流程。', '力量区B-02', 'AVAILABLE', '2023-02-01', NULL, 'files/equipment/32a5680e-64eb-46cb-8c41-d2d23f5e9764.png', '2026-03-02 23:18:39', '2026-03-02 23:18:39', NULL, NULL);
INSERT INTO `equipment` VALUES (5, '瑜伽垫-01', '瑜伽器材', '瑜伽垫是瑜伽、普拉提通用的基础运动器械，也是地面健身训练的必备载体，为薄款弹性垫体，核心作用是防滑缓冲、隔凉减震、界定训练空间，适配健身房、居家、户外等全场景，新手友好且无使用门槛，是瑜伽 / 普拉提入门的核心基础器材。\n核心规格与材质\n厚度：主流分 3mm、4mm、6mm、8mm 四档，按需选择：3-4mm 薄款便携，适合户外 / 有瑜伽辅具的进阶人群；6-8mm 厚款缓冲性强，是新手首选，能有效避免膝盖、手肘、腰椎等部位因接触硬地面产生硌痛，普拉提训练更推荐 6-8mm 厚款，适配核心地面训练的缓冲需求。\n材质：市面主流为TPE 材质（环保无异味、防滑性好、回弹性佳，性价比最高，新手首选），其次是天然橡胶材质（防滑性顶级、抓地牢固，适合高温瑜伽 / 大出汗场景，偏高端），基础款PVC 材质（价格低廉，防滑性一般，适合临时使用），均为防水易清洁设计，擦拭即可打理。\n尺寸：常规标准款 183×61cm，适配大多数人群；加长款 183×80cm，适合身高 180cm 以上或需要更大训练空间的人群，宽度足够避免训练时身体超出垫面。\n核心特性\n双重防滑：正面（接触身体）为亲肤防滑纹理，防止训练时身体打滑偏移；背面（接触地面）为吸盘 / 网格防滑设计，紧密贴合地面不位移，即使出汗也能保证动作稳定性，适配瑜伽体式、普拉提核心训练等各类地面动作。\n轻量便携：整体重量轻，多数款式带便携绑带 / 收纳包，可折叠或卷放，方便外出携带、收纳，不占用空间，居家可随手收纳至角落，户外可直接铺展使用。\n隔凉减震：有效隔绝地面凉气，同时缓冲身体与地面的接触冲击力，既保护关节和骨骼，又能提升训练舒适度，让体式 / 训练动作更易坚持。\n核心适用场景与延伸用途\n核心用途：瑜伽全品类训练（流瑜伽、阴瑜伽、高温瑜伽等）、普拉提地面核心训练，作为动作基础载体，保证训练稳定性与安全性；\n延伸用途：可作为健身热身、拉伸、徒手训练（平板支撑、卷腹等）的缓冲垫，也可作为儿童运动、居家防滑的辅助垫，实用性强。', '瑜伽室C-01', 'AVAILABLE', '2023-03-01', NULL, 'files/equipment/1a7dc6fc-ad79-48f0-adc5-150416b4ed79.png', '2026-03-02 23:18:39', '2026-04-10 01:47:43', NULL, 16);
INSERT INTO `equipment` VALUES (6, '动感单车-01', '有氧器械', '动感单车是健身房有氧区核心器械，为模拟户外骑行的固定式健身设备，分健身房商用款（机身稳固、飞轮重 18-25kg、承重大）和居家款（轻便静音、可折叠、飞轮 8-15kg），操作简单，全年龄段适配，是高效燃脂、塑形臀腿的优选有氧器材。\n核心配备可调座椅 / 车把，可精准匹配身高，保证发力姿势；主流磁控阻力系统，旋钮无级调阻，搭配带防滑绑带的脚踏，蹬踏稳定高效；机身带数据显示屏，可看速度、卡路里、心率等，智能款还能连蓝牙、投屏练课。\n飞轮转动的惯性还原真实骑行感，骑行时全身联动，侧重激活臀腿肌群，同时收紧核心，燃脂效率高，30 分钟可消耗 300-500 大卡，且无地面反冲力，对膝盖、腰椎冲击极小，体重基数大、关节不适者也可使用。\n可通过阻力和节奏切换训练模式，低阻低速热身、中阻中速常规燃脂、高阻低速塑形臀腿，也能做间歇训练；健身房有专属单车教室开展团体课，氛围感强，居家款小巧不扰民，适配碎片化训练，既能提升心肺耐力，又能紧致下肢线条、强化核心稳定，是减脂塑形、提升体能的万能有氧器械。', '有氧区A-03', 'AVAILABLE', '2023-04-01', NULL, 'files/equipment/9edd313f-5386-464b-8f69-200ab9f2e67f.png', '2026-03-02 23:18:39', '2026-03-02 23:18:39', NULL, NULL);
INSERT INTO `equipment` VALUES (9, '测试器材', '有氧器材', '测试测试测试测试测试测试', '二楼B区05号', 'AVAILABLE', '2026-03-04', NULL, '', '2026-03-07 19:50:05', '2026-03-07 19:50:05', NULL, NULL);
INSERT INTO `equipment` VALUES (10, '测器材-2', '自由重量', '测试测试测试测试测试测试测试测试测试测试', '大门左侧', 'IN_USE', '2026-03-03', NULL, '', '2026-03-07 22:29:09', '2026-03-07 22:29:09', NULL, NULL);
INSERT INTO `equipment` VALUES (11, '瑜伽球', '瑜伽器材', '瑜伽球是瑜伽专属核心辅助塑形器材，也常称健身球，为充气式圆形弹性器械，常规主流规格分65cm/75cm/85cm（按身高适配，160cm 以下选 65cm、160-175cm 选 75cm、175cm 以上选 85cm），材质多为防滑耐磨的 PVC / 防爆橡胶，按压有弹性且承重性强，适配健身房、居家全场景，新手友好无操作门槛。\n核心作用围绕瑜伽体式辅助、核心激活、平衡训练展开，兼顾塑形与体态优化：可作为体式支撑支点，辅助完成球上平板支撑、球上背伸等动作，激活腰腹深层核心，紧致腰腹线条；也能借力舒展脊柱、放松腰背肌肉，改善久坐带来的腰背僵硬，让肌肉线条更流畅；同时通过平衡类动作提升身体协调性，搭配瑜伽体式实现 “塑形 + 柔韧性 + 核心稳定性” 三合一训练效果，是瑜伽入门到进阶的常用器材，区别于普拉提的迷你款瑜伽球，尺寸更大、更侧重体式舒展与平衡支撑。', '瑜伽室', 'AVAILABLE', '2025-04-30', NULL, 'files/equipment/0f7d60b7-d970-463a-ba30-7556805c927e.png', '2026-04-08 17:42:00', '2026-04-10 01:47:43', NULL, 16);

-- ----------------------------
-- Table structure for health_advice
-- ----------------------------
DROP TABLE IF EXISTS `health_advice`;
CREATE TABLE `health_advice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '健康建议ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `advice_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '建议类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '建议内容',
  `based_on_data` json NULL COMMENT '基于的数据',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `health_advice_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康建议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_advice
-- ----------------------------

-- ----------------------------
-- Table structure for health_plans
-- ----------------------------
DROP TABLE IF EXISTS `health_plans`;
CREATE TABLE `health_plans`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '健康计划ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '计划描述',
  `target_weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '目标体重(kg)',
  `target_duration_minutes` int NULL DEFAULT NULL COMMENT '目标运动时长(分钟/周)',
  `current_weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '当前体重(kg)',
  `current_duration_minutes` int NULL DEFAULT 0 COMMENT '当前运动时长(分钟/周)',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `status` enum('ACTIVE','COMPLETED','ABANDONED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE' COMMENT '计划状态',
  `completion_percentage` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成百分比',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `coach_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `health_plans_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_plans
-- ----------------------------
INSERT INTO `health_plans` VALUES (1, 1, '1', NULL, NULL, NULL, NULL, 0, '2026-03-03', '2026-03-04', 'ACTIVE', 0.00, '2026-03-03 23:57:09', '2026-03-03 23:57:09', NULL);
INSERT INTO `health_plans` VALUES (2, 12, '瘦身', '减肥瘦身减脂', 80.00, 500, 100.00, 0, '2026-03-05', '2026-06-05', 'ACTIVE', 0.00, '2026-03-05 14:14:02', '2026-03-05 14:14:02', NULL);
INSERT INTO `health_plans` VALUES (3, 12, '1', '1', 1.00, 1, 1.00, 0, '2026-03-05', '2026-06-05', 'ACTIVE', 0.00, '2026-03-05 14:29:04', '2026-03-05 14:29:04', NULL);
INSERT INTO `health_plans` VALUES (4, 13, '减脂计划', '通过有氧运动和力量训练，在3个月内减重5kg', 60.00, 300, 65.50, 0, '2024-01-01', '2024-03-31', 'ACTIVE', 0.00, '2026-03-05 15:54:17', '2026-03-05 15:56:55', NULL);
INSERT INTO `health_plans` VALUES (5, 11, '减肥', '减肥计划', 90.00, 900, 100.00, 0, '2026-03-05', '2026-06-05', 'ACTIVE', 0.00, '2026-03-05 16:00:28', '2026-03-05 16:25:00', 12);
INSERT INTO `health_plans` VALUES (6, 11, '测试计划', '测试计划测试计划测试计划测试计划', 85.00, 500, 90.00, 0, '2026-03-07', '2026-06-07', 'ACTIVE', 0.00, '2026-03-07 19:42:33', '2026-03-07 19:42:33', 12);
INSERT INTO `health_plans` VALUES (7, 11, '减肥计划B', '减肥减脂减重', 85.00, 500, 90.00, 0, '2026-03-08', '2026-06-15', 'ACTIVE', 0.00, '2026-03-07 19:52:08', '2026-03-07 19:52:08', 12);
INSERT INTO `health_plans` VALUES (8, 11, 'test-plan', 'xxxxxxxxxxxxx', 90.00, 500, 100.00, 0, '2026-03-07', '2026-06-07', 'ACTIVE', 0.00, '2026-03-07 22:34:09', '2026-03-07 22:34:09', 12);
INSERT INTO `health_plans` VALUES (9, 15, '00', '00', 50.00, 840, 55.00, 0, '2026-03-28', '2026-04-03', 'ACTIVE', 42.86, '2026-03-28 11:11:19', '2026-03-28 11:11:19', NULL);

-- ----------------------------
-- Table structure for plan_check_ins
-- ----------------------------
DROP TABLE IF EXISTS `plan_check_ins`;
CREATE TABLE `plan_check_ins`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `plan_id` bigint NOT NULL COMMENT '健康计划ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `check_date` date NOT NULL COMMENT '打卡日期',
  `check_in_time` timestamp NOT NULL COMMENT '开始打卡时间',
  `check_out_time` timestamp NULL DEFAULT NULL COMMENT '结束打卡时间',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '打卡时长(分钟)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN_PROGRESS' COMMENT '打卡状态(IN_PROGRESS, COMPLETED)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `exercise_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运动方式',
  `calories_burned` decimal(8, 2) NULL DEFAULT NULL COMMENT '消耗卡路里',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_check_date`(`check_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `plan_check_ins_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `health_plans` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `plan_check_ins_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康计划打卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plan_check_ins
-- ----------------------------
INSERT INTO `plan_check_ins` VALUES (1, 9, 15, '2026-03-28', '2026-03-28 18:54:48', '2026-03-28 20:14:11', 79, 'COMPLETED', '2026-03-28 18:54:48', '2026-03-28 18:54:48', NULL, NULL);
INSERT INTO `plan_check_ins` VALUES (2, 9, 15, '2026-03-28', '2026-03-28 20:25:44', '2026-03-28 20:29:44', 3, 'COMPLETED', '2026-03-28 20:25:44', '2026-03-28 20:25:44', '慢跑', NULL);
INSERT INTO `plan_check_ins` VALUES (3, 9, 15, '2026-03-28', '2026-03-28 21:23:44', '2026-03-28 21:39:41', 15, 'COMPLETED', '2026-03-28 21:23:44', '2026-03-28 21:23:44', '慢跑', 120.00);

-- ----------------------------
-- Table structure for reservations
-- ----------------------------
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `reservation_type` enum('COURSE','EQUIPMENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约类型',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '器材ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` enum('PENDING','CONFIRMED','CANCELLED','COMPLETED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CONFIRMED' COMMENT '预约状态',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `cancelled_at` timestamp NULL DEFAULT NULL COMMENT '取消时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `coach_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_composite`(`student_id` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `reservations_ibfk_3` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reservations
-- ----------------------------
INSERT INTO `reservations` VALUES (34, 11, 'COURSE', 1, NULL, '2026-03-11 05:48:16', '2028-01-06 02:30:00', 'CONFIRMED', NULL, NULL, '2026-03-11 15:05:30', '2026-03-11 15:05:30', NULL);
INSERT INTO `reservations` VALUES (35, 15, 'COURSE', 1, NULL, '2026-03-11 05:48:16', '2028-01-06 02:30:00', 'CANCELLED', '零零零零', '2026-03-30 22:58:25', '2026-03-20 19:29:31', '2026-03-20 19:29:31', NULL);
INSERT INTO `reservations` VALUES (36, 15, 'EQUIPMENT', NULL, 9, '2026-03-24 13:16:32', '2026-03-24 14:00:00', 'CANCELLED', NULL, '2026-03-26 20:58:52', '2026-03-24 13:17:03', '2026-03-24 13:17:03', NULL);
INSERT INTO `reservations` VALUES (37, 17, 'COURSE', 12, NULL, '2026-03-22 12:00:00', '2026-04-19 16:00:00', 'COMPLETED', NULL, NULL, '2026-03-24 20:33:27', '2026-03-24 20:33:27', NULL);
INSERT INTO `reservations` VALUES (38, 17, 'COURSE', 12, NULL, '2026-03-22 12:00:00', '2026-04-19 16:00:00', 'CANCELLED', NULL, '2026-03-26 21:08:39', '2026-03-26 21:06:02', '2026-03-26 21:06:02', NULL);
INSERT INTO `reservations` VALUES (39, 17, 'COURSE', 12, NULL, '2026-03-22 12:00:00', '2026-04-19 16:00:00', 'CANCELLED', NULL, '2026-03-26 21:11:38', '2026-03-26 21:11:29', '2026-03-26 21:11:29', NULL);
INSERT INTO `reservations` VALUES (40, 17, 'COURSE', 12, NULL, '2026-03-22 12:00:00', '2026-04-19 16:00:00', 'CANCELLED', NULL, '2026-03-26 21:11:52', '2026-03-26 21:11:43', '2026-03-26 21:11:43', NULL);
INSERT INTO `reservations` VALUES (41, 17, 'COURSE', 3, NULL, '2024-01-16 10:00:00', '2024-01-16 11:00:00', 'CANCELLED', NULL, '2026-03-26 21:12:37', '2026-03-26 21:12:24', '2026-03-26 21:12:24', NULL);

-- ----------------------------
-- Table structure for social_comment
-- ----------------------------
DROP TABLE IF EXISTS `social_comment`;
CREATE TABLE `social_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_comment
-- ----------------------------
INSERT INTO `social_comment` VALUES (4, 1, 15, '不错', '2026-03-19 19:14:37');
INSERT INTO `social_comment` VALUES (5, 2, 15, '很好', '2026-03-19 19:48:14');
INSERT INTO `social_comment` VALUES (6, 5, 15, '嗯嗯', '2026-03-20 11:55:37');

-- ----------------------------
-- Table structure for social_follow
-- ----------------------------
DROP TABLE IF EXISTS `social_follow`;
CREATE TABLE `social_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `following_id` bigint NOT NULL COMMENT '被关注者ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_following_id`(`following_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_follow
-- ----------------------------
INSERT INTO `social_follow` VALUES (2, 15, 2, '2026-03-19 10:57:02');
INSERT INTO `social_follow` VALUES (3, 15, 14, '2026-03-28 10:54:00');

-- ----------------------------
-- Table structure for social_like
-- ----------------------------
DROP TABLE IF EXISTS `social_like`;
CREATE TABLE `social_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_like
-- ----------------------------
INSERT INTO `social_like` VALUES (1, 1, 15, '2026-03-19 11:53:23');
INSERT INTO `social_like` VALUES (2, 2, 15, '2026-03-19 19:48:06');
INSERT INTO `social_like` VALUES (3, 5, 15, '2026-03-20 11:55:31');

-- ----------------------------
-- Table structure for social_post
-- ----------------------------
DROP TABLE IF EXISTS `social_post`;
CREATE TABLE `social_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `images` json NULL COMMENT '图片URL列表',
  `video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频URL',
  `exercise_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运动类型',
  `exercise_duration` int NULL DEFAULT NULL COMMENT '运动时长（分钟）',
  `exercise_calories` int NULL DEFAULT NULL COMMENT '消耗卡路里',
  `likes` int NULL DEFAULT 0 COMMENT '点赞数',
  `comments` int NULL DEFAULT 0 COMMENT '评论数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_post
-- ----------------------------
INSERT INTO `social_post` VALUES (2, 15, '你好', '[]', '', NULL, NULL, NULL, 1, 1, '2026-03-19 19:48:03', '2026-03-19 19:48:03');
INSERT INTO `social_post` VALUES (3, 15, '', '[\"files/social/176ab1a4-45b0-4a45-af19-f05e39e0fe0a.jpg\"]', '', NULL, NULL, NULL, 0, 0, '2026-03-19 19:51:40', '2026-03-19 19:51:40');
INSERT INTO `social_post` VALUES (4, 15, '今天的自己', '[\"files/social/917948a5-ffc4-49c2-9792-ed03a99cd654.jpg\"]', '', NULL, NULL, NULL, 0, 0, '2026-03-19 19:52:10', '2026-03-19 19:52:10');
INSERT INTO `social_post` VALUES (5, 15, '今天你好', '[\"files/social/3521ea04-2c23-4faf-8f57-b48ca1459ee5.jpg\"]', '', NULL, NULL, NULL, 1, 1, '2026-03-20 11:55:27', '2026-03-20 11:55:27');

-- ----------------------------
-- Table structure for social_post_topic
-- ----------------------------
DROP TABLE IF EXISTS `social_post_topic`;
CREATE TABLE `social_post_topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `topic_id` bigint NOT NULL COMMENT '话题ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_topic`(`post_id` ASC, `topic_id` ASC) USING BTREE,
  INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子-话题关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_post_topic
-- ----------------------------

-- ----------------------------
-- Table structure for social_report
-- ----------------------------
DROP TABLE IF EXISTS `social_report`;
CREATE TABLE `social_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '举报用户ID',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING-待处理, PROCESSED-已处理, IGNORED-已忽略',
  `processed_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `processed_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `social_report_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `social_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `social_report_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社交举报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of social_report
-- ----------------------------
INSERT INTO `social_report` VALUES (1, 5, 15, 'other', '', 'PROCESSED', 1, '2026-04-24 16:38:46', '2026-03-23 20:27:50', '2026-03-23 20:27:50');

-- ----------------------------
-- Table structure for social_topic
-- ----------------------------
DROP TABLE IF EXISTS `social_topic`;
CREATE TABLE `social_topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '话题ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '话题标题',
  `posts` int NULL DEFAULT 0 COMMENT '帖子数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_title`(`title` ASC) USING BTREE,
  INDEX `idx_posts`(`posts` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 768 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社交话题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of social_topic
-- ----------------------------
INSERT INTO `social_topic` VALUES (1, '健身打卡', 0, '2026-03-17 17:41:12');
INSERT INTO `social_topic` VALUES (2, '饮食健康', 0, '2026-03-17 17:41:12');
INSERT INTO `social_topic` VALUES (3, '运动技巧', 0, '2026-03-17 17:41:12');
INSERT INTO `social_topic` VALUES (4, '健身装备', 0, '2026-03-17 17:41:12');
INSERT INTO `social_topic` VALUES (5, '健身心得', 0, '2026-03-17 17:41:12');

-- ----------------------------
-- Table structure for student_profiles
-- ----------------------------
DROP TABLE IF EXISTS `student_profiles`;
CREATE TABLE `student_profiles`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生信息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `student_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '负责教练ID',
  `gender` enum('MALE','FEMALE','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `late_cancellation_count` int NULL DEFAULT 0 COMMENT '迟到取消次数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_number`(`student_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_student_number`(`student_number` ASC) USING BTREE,
  CONSTRAINT `student_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_profiles_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student_profiles
-- ----------------------------
INSERT INTO `student_profiles` VALUES (10, 15, '2224120305', NULL, NULL, 20, 165.00, 68.00, 0, '2026-03-14 21:32:49', '2026-03-14 21:32:49', NULL);
INSERT INTO `student_profiles` VALUES (11, 17, '12345678', NULL, NULL, 23, 158.00, 63.00, 0, '2026-03-24 20:33:07', '2026-03-24 20:33:07', NULL);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` enum('ADMIN','COACH','STUDENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$/lhPiCU5vB85Q0xzwpLaC.IrjsgrjtqFPMR2PvFrV3J9lyC2W786u', 'admin@fitness.edu.cn', '13800138009', '管理员', 'ADMIN', 'files/avatar/1f709d3c-d4bc-4c74-8fae-e18c27d6ddeb.jpg', 1, '2026-03-02 23:18:39', '2026-03-03 10:25:15');
INSERT INTO `users` VALUES (2, 'coach1', '$2a$10$/lhPiCU5vB85Q0xzwpLaC.IrjsgrjtqFPMR2PvFrV3J9lyC2W786u', 'coach1@fitness.edu.cn', '13800138001', '张教练', 'COACH', NULL, 1, '2026-03-02 23:18:39', '2026-03-03 10:25:15');
INSERT INTO `users` VALUES (11, 'aaron', '$2a$10$Rij30xrcC1wbYgJROh4m8uyu9OZNYQb4yznTVgvuk52D38i05oFmW', 'aaron@qq.com', '18988889999', '测试学生', 'STUDENT', 'files/avatar/cc1d96ad-29c1-454f-baf3-38377177e8cb.jpg', 1, '2026-03-05 11:04:35', '2026-03-05 11:04:35');
INSERT INTO `users` VALUES (12, 'coach', '$2a$10$NKg5QkkepacVNjocf4sq0umamtDKHSwg3XO.5ANK6Wm6mO6YqpCEq', 'coach@qq.com', '18889999999', '测试教练', 'COACH', 'files/avatar/6a6ac2a4-69de-4d28-a23e-de78348cdfbe.jpg', 1, '2026-03-05 11:18:28', '2026-03-05 11:18:28');
INSERT INTO `users` VALUES (13, 'xs1', '$2a$10$2lrBGhKcMTEp48RhlCTVWOgoJui459tOAgNrhoXnEBaKX26rPQH6K', 'zhangsan@exampl55e.com', '13800138000', '张三', 'STUDENT', NULL, 1, '2026-03-05 15:55:30', '2026-03-05 15:55:30');
INSERT INTO `users` VALUES (14, 'jl1', '$2a$10$rFUjElI16Rc.rm9PDQZone9IiXXJg4ScPaep51qc0xDt7lQtPJbn6', 'zhangsan@exam2pl55e.com', '13800138000', '张三', 'COACH', 'files/avatar/de4ae941-7e21-459b-8020-55b190acdcb4.jpg', 1, '2026-03-05 15:55:56', '2026-03-05 15:55:56');
INSERT INTO `users` VALUES (15, 'student', '$2a$10$FMh6uw/pO0cZpsaUCtSlSuLqq.hJECDLhubdYDgQLoLA0scknjWRi', '3365286127@qq.com', '13471429252', '婉儿', 'STUDENT', 'files/avatar/c290b6c6-de02-4a19-ab03-a30539cabc55.jpg', 1, '2026-03-14 21:32:49', '2026-03-14 21:32:49');
INSERT INTO `users` VALUES (16, 'coach001', '$2a$10$OPxrRssRzMtZorRUGG9JteL0mY1RaoXh/GkZp2URvMIGpHEZ7AfWu', '12345678@qq.com', '13471429252', '美美', 'COACH', 'files/avatar/35bcbb30-f52a-44cd-851e-9d7b21a6636c.jpeg', 1, '2026-03-20 19:11:23', '2026-03-20 19:11:23');
INSERT INTO `users` VALUES (17, 'student001', '$2a$10$9bIIr8X0lRfa/0YxhvqvaeXct6rGgG8AfUgJ1vxWPG4b9N28kku1e', '123456780@qq.com', '19127543979', '奶龙', 'STUDENT', 'files/avatar/02ac5301-fdce-4c1e-bb70-7c17dad61eaa.jpeg', 1, '2026-03-24 20:33:07', '2026-03-24 20:33:07');

SET FOREIGN_KEY_CHECKS = 1;
