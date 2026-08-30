package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.dto.CourseDTO;
import com.fitness.dto.CreateCourseRequest;
import com.fitness.dto.UpdateCourseRequest;
import com.fitness.entity.enums.CourseStatus;
import com.fitness.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CourseController 集成测试
 * 测试所有课程管理相关的 API 端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("课程控制器测试")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    private CreateCourseRequest createCourseRequest;
    private UpdateCourseRequest updateCourseRequest;
    private CourseDTO courseDTO;
    private IPage<CourseDTO> coursePage;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);

        createCourseRequest = new CreateCourseRequest();
        createCourseRequest.setName("瑜伽基础课");
        createCourseRequest.setDescription("适合初学者的瑜伽课程");
        createCourseRequest.setCoachId(2L);
        createCourseRequest.setCourseType("瑜伽");
        createCourseRequest.setCapacity(30);
        createCourseRequest.setStartTime(startTime);
        createCourseRequest.setEndTime(endTime);
        createCourseRequest.setLocation("健身房A区");

        updateCourseRequest = new UpdateCourseRequest();
        updateCourseRequest.setName("瑜伽进阶课");
        updateCourseRequest.setDescription("适合有基础的学员");
        updateCourseRequest.setCapacity(25);

        courseDTO = CourseDTO.builder()
                .id(1L)
                .name("瑜伽基础课")
                .description("适合初学者的瑜伽课程")
                .coachId(2L)
                .coachName("李教练")
                .courseType("瑜伽")
                .capacity(30)
                .currentEnrollment(15)
                .availableSlots(15)
                .startTime(startTime)
                .endTime(endTime)
                .location("健身房A区")
                .status(CourseStatus.AVAILABLE)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 创建分页数据
        Page<CourseDTO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(courseDTO));
        page.setTotal(1);
        coursePage = page;
    }

    @Test
    @DisplayName("测试获取课程列表 - 成功")
    @WithMockUser(username = "1")
    void testGetCourseList_Success() throws Exception {
        // Mock service
        when(courseService.getCourseList(any(Page.class), any()))
                .thenReturn(coursePage);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.records[0].name").value("瑜伽基础课"))
                .andExpect(jsonPath("$.data.records[0].coachName").value("李教练"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("测试获取课程列表 - 带搜索关键词")
    @WithMockUser(username = "1")
    void testGetCourseList_WithKeyword() throws Exception {
        // Mock service
        when(courseService.getCourseList(any(Page.class), any()))
                .thenReturn(coursePage);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses")
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", "瑜伽"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("瑜伽基础课"));
    }

    @Test
    @DisplayName("测试获取课程列表 - 按类型筛选")
    @WithMockUser(username = "1")
    void testGetCourseList_WithCourseType() throws Exception {
        // Mock service
        when(courseService.getCourseList(any(Page.class), any()))
                .thenReturn(coursePage);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses")
                        .param("page", "1")
                        .param("size", "10")
                        .param("courseType", "瑜伽"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].courseType").value("瑜伽"));
    }

    @Test
    @DisplayName("测试获取课程列表 - 按状态筛选")
    @WithMockUser(username = "1")
    void testGetCourseList_WithStatus() throws Exception {
        // Mock service
        when(courseService.getCourseList(any(Page.class), any()))
                .thenReturn(coursePage);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses")
                        .param("page", "1")
                        .param("size", "10")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("测试获取课程详情 - 成功")
    @WithMockUser(username = "1")
    void testGetCourseById_Success() throws Exception {
        // Mock service
        when(courseService.getCourseById(1L)).thenReturn(courseDTO);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("瑜伽基础课"))
                .andExpect(jsonPath("$.data.coachName").value("李教练"))
                .andExpect(jsonPath("$.data.capacity").value(30))
                .andExpect(jsonPath("$.data.currentEnrollment").value(15))
                .andExpect(jsonPath("$.data.availableSlots").value(15));
    }

    @Test
    @DisplayName("测试创建课程 - 成功（管理员）")
    @WithMockUser(username = "1", roles = {"ADMIN"})
    void testCreateCourse_SuccessAsAdmin() throws Exception {
        // Mock service
        when(courseService.createCourse(any(CreateCourseRequest.class), eq(1L)))
                .thenReturn(courseDTO);

        // 执行请求并验证
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("瑜伽基础课"))
                .andExpect(jsonPath("$.data.capacity").value(30));
    }

    @Test
    @DisplayName("测试创建课程 - 成功（教练）")
    @WithMockUser(username = "2", roles = {"COACH"})
    void testCreateCourse_SuccessAsCoach() throws Exception {
        // Mock service
        when(courseService.createCourse(any(CreateCourseRequest.class), eq(2L)))
                .thenReturn(courseDTO);

        // 执行请求并验证
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("瑜伽基础课"));
    }

    @Test
    @DisplayName("测试创建课程 - 参数验证失败")
    @WithMockUser(username = "1", roles = {"ADMIN"})
    void testCreateCourse_ValidationFailed() throws Exception {
        // 创建无效的请求（缺少必填字段）
        CreateCourseRequest invalidRequest = new CreateCourseRequest();
        invalidRequest.setName(""); // 空名称

        // 执行请求并验证
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试更新课程 - 成功")
    @WithMockUser(username = "1", roles = {"ADMIN"})
    void testUpdateCourse_Success() throws Exception {
        // Mock service
        CourseDTO updatedCourse = CourseDTO.builder()
                .id(1L)
                .name("瑜伽进阶课")
                .description("适合有基础的学员")
                .capacity(25)
                .build();
        when(courseService.updateCourse(eq(1L), any(UpdateCourseRequest.class), eq(1L)))
                .thenReturn(updatedCourse);

        // 执行请求并验证
        mockMvc.perform(put("/api/v1/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("瑜伽进阶课"))
                .andExpect(jsonPath("$.data.capacity").value(25));
    }

    @Test
    @DisplayName("测试删除课程 - 成功")
    @WithMockUser(username = "1", roles = {"ADMIN"})
    void testDeleteCourse_Success() throws Exception {
        // Mock service
        doNothing().when(courseService).deleteCourse(eq(1L), eq(1L));

        // 执行请求并验证
        mockMvc.perform(delete("/api/v1/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("课程删除成功"));
    }

    @Test
    @DisplayName("测试搜索课程 - 成功")
    @WithMockUser(username = "1")
    void testSearchCourses_Success() throws Exception {
        // Mock service
        when(courseService.getCourseList(any(Page.class), any()))
                .thenReturn(coursePage);

        // 执行请求并验证
        mockMvc.perform(get("/api/v1/courses/search")
                        .param("keyword", "瑜伽")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("瑜伽基础课"));
    }

    @Test
    @DisplayName("测试获取课程列表 - 未认证")
    void testGetCourseList_Unauthorized() throws Exception {
        // 执行请求并验证（未认证）
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("测试创建课程 - 学生角色无权限")
    @WithMockUser(username = "3", roles = {"STUDENT"})
    void testCreateCourse_ForbiddenAsStudent() throws Exception {
        // 执行请求并验证（学生角色）
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourseRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("测试删除课程 - 学生角色无权限")
    @WithMockUser(username = "3", roles = {"STUDENT"})
    void testDeleteCourse_ForbiddenAsStudent() throws Exception {
        // 执行请求并验证（学生角色）
        mockMvc.perform(delete("/api/v1/courses/1"))
                .andExpect(status().isForbidden());
    }
}
