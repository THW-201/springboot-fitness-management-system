//package com.fitness.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fitness.dto.CourseDTO;
//import com.fitness.dto.CreateCourseRequest;
//import com.fitness.dto.UpdateCourseRequest;
//import com.fitness.entity.Course;
//import com.fitness.entity.User;
//import com.fitness.entity.enums.CourseStatus;
//import com.fitness.entity.enums.UserRole;
//import com.fitness.exception.BusinessException;
//import com.fitness.mapper.CourseMapper;
//import com.fitness.mapper.ReservationMapper;
//import com.fitness.mapper.UserMapper;
//import com.fitness.service.impl.CourseServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * CourseService 单元测试
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("课程服务测试")
//class CourseServiceTest {
//
//    @Mock
//    private CourseMapper courseMapper;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private ReservationMapper reservationMapper;
//
//    @Mock
//    private CacheService cacheService;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private CourseServiceImpl courseService;
//
//    private User adminUser;
//    private User coachUser;
//    private User studentUser;
//    private Course course;
//    private CreateCourseRequest createRequest;
//    private UpdateCourseRequest updateRequest;
//
//    @BeforeEach
//    void setUp() {
//        // 准备测试数据
//        adminUser = User.builder()
//                .id(1L)
//                .username("admin")
//                .realName("管理员")
//                .role(UserRole.ADMIN)
//                .build();
//
//        coachUser = User.builder()
//                .id(2L)
//                .username("coach")
//                .realName("李教练")
//                .role(UserRole.COACH)
//                .build();
//
//        studentUser = User.builder()
//                .id(3L)
//                .username("student")
//                .realName("学生")
//                .role(UserRole.STUDENT)
//                .build();
//
//        course = Course.builder()
//                .id(1L)
//                .name("瑜伽基础课")
//                .description("适合初学者")
//                .coachId(2L)
//                .courseType("瑜伽")
//                .capacity(30)
//                .currentEnrollment(10)
//                .startTime(LocalDateTime.now().plusDays(1))
//                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
//                .location("健身房A区")
//                .status(CourseStatus.AVAILABLE)
//                .createdBy(1L)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        createRequest = new CreateCourseRequest();
//        createRequest.setName("瑜伽基础课");
//        createRequest.setDescription("适合初学者");
//        createRequest.setCoachId(2L);
//        createRequest.setCourseType("瑜伽");
//        createRequest.setCapacity(30);
//        createRequest.setStartTime(LocalDateTime.now().plusDays(1));
//        createRequest.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
//        createRequest.setLocation("健身房A区");
//
//        updateRequest = new UpdateCourseRequest();
//        updateRequest.setName("瑜伽进阶课");
//        updateRequest.setCapacity(25);
//    }
//
//    @Test
//    @DisplayName("创建课程 - 管理员成功创建")
//    void testCreateCourse_AdminSuccess() {
//        // Mock
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        when(courseMapper.insert(any(Course.class))).thenAnswer(invocation -> {
//            Course c = invocation.getArgument(0);
//            c.setId(1L);
//            return 1;
//        });
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        CourseDTO result = courseService.createCourse(createRequest, 1L);
//
//        // 验证
//        assertNotNull(result);
//        assertEquals("瑜伽基础课", result.getName());
//        assertEquals(2L, result.getCoachId());
//        assertEquals("李教练", result.getCoachName());
//        assertEquals(CourseStatus.AVAILABLE, result.getStatus());
//        assertEquals(0, result.getCurrentEnrollment());
//
//        verify(courseMapper, times(1)).insert(any(Course.class));
//        verify(cacheService, times(1)).deletePattern(anyString());
//    }
//
//    @Test
//    @DisplayName("创建课程 - 教练成功创建")
//    void testCreateCourse_CoachSuccess() {
//        // Mock
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        when(courseMapper.insert(any(Course.class))).thenAnswer(invocation -> {
//            Course c = invocation.getArgument(0);
//            c.setId(1L);
//            return 1;
//        });
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        CourseDTO result = courseService.createCourse(createRequest, 2L);
//
//        // 验证
//        assertNotNull(result);
//        verify(courseMapper, times(1)).insert(any(Course.class));
//    }
//
//    @Test
//    @DisplayName("创建课程 - 学生无权限")
//    void testCreateCourse_StudentNoPermission() {
//        // Mock
//        when(userMapper.selectById(3L)).thenReturn(studentUser);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.createCourse(createRequest, 3L);
//        });
//
//        assertEquals("只有管理员或教练可以创建课程", exception.getMessage());
//        verify(courseMapper, never()).insert(any(Course.class));
//    }
//
//    @Test
//    @DisplayName("创建课程 - 创建者不存在")
//    void testCreateCourse_CreatorNotFound() {
//        // Mock
//        when(userMapper.selectById(999L)).thenReturn(null);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.createCourse(createRequest, 999L);
//        });
//
//        assertEquals("创建者不存在", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("创建课程 - 教练不存在")
//    void testCreateCourse_CoachNotFound() {
//        // Mock
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(userMapper.selectById(2L)).thenReturn(null);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.createCourse(createRequest, 1L);
//        });
//
//        assertEquals("指定的教练不存在", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("创建课程 - 时间无效")
//    void testCreateCourse_InvalidTime() {
//        // Mock
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//
//        // 设置无效时间
//        createRequest.setEndTime(createRequest.getStartTime().minusHours(1));
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.createCourse(createRequest, 1L);
//        });
//
//        assertEquals("结束时间必须晚于开始时间", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("更新课程 - 管理员成功更新")
//    void testUpdateCourse_AdminSuccess() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        when(courseMapper.updateById(any(Course.class))).thenReturn(1);
//        doNothing().when(cacheService).delete(anyString());
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        CourseDTO result = courseService.updateCourse(1L, updateRequest, 1L);
//
//        // 验证
//        assertNotNull(result);
//        assertEquals("瑜伽进阶课", result.getName());
//        assertEquals(25, result.getCapacity());
//
//        verify(courseMapper, times(1)).updateById(any(Course.class));
//        verify(cacheService, times(1)).delete(anyString());
//        verify(cacheService, times(1)).deletePattern(anyString());
//    }
//
//    @Test
//    @DisplayName("更新课程 - 课程创建者成功更新")
//    void testUpdateCourse_CreatorSuccess() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        when(courseMapper.updateById(any(Course.class))).thenReturn(1);
//        doNothing().when(cacheService).delete(anyString());
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        CourseDTO result = courseService.updateCourse(1L, updateRequest, 1L);
//
//        // 验证
//        assertNotNull(result);
//        verify(courseMapper, times(1)).updateById(any(Course.class));
//    }
//
//    @Test
//    @DisplayName("更新课程 - 非创建者无权限")
//    void testUpdateCourse_NoPermission() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(3L)).thenReturn(studentUser);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.updateCourse(1L, updateRequest, 3L);
//        });
//
//        assertEquals("只有管理员或课程创建者可以更新课程", exception.getMessage());
//        verify(courseMapper, never()).updateById(any(Course.class));
//    }
//
//    @Test
//    @DisplayName("更新课程 - 课程不存在")
//    void testUpdateCourse_CourseNotFound() {
//        // Mock
//        when(courseMapper.selectById(999L)).thenReturn(null);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.updateCourse(999L, updateRequest, 1L);
//        });
//
//        assertEquals("课程不存在", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("更新课程 - 新容量小于当前报名人数")
//    void testUpdateCourse_CapacityTooSmall() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//
//        // 设置新容量小于当前报名人数
//        updateRequest.setCapacity(5); // 当前报名人数是10
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.updateCourse(1L, updateRequest, 1L);
//        });
//
//        assertEquals("新容量不能小于当前报名人数", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("删除课程 - 管理员成功删除")
//    void testDeleteCourse_AdminSuccess() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(reservationMapper.countActiveCourseReservations(1L)).thenReturn(0);
//        when(courseMapper.deleteById(1L)).thenReturn(1);
//        doNothing().when(cacheService).delete(anyString());
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        assertDoesNotThrow(() -> courseService.deleteCourse(1L, 1L));
//
//        // 验证
//        verify(courseMapper, times(1)).deleteById(1L);
//        verify(cacheService, times(1)).delete(anyString());
//        verify(cacheService, times(1)).deletePattern(anyString());
//    }
//
//    @Test
//    @DisplayName("删除课程 - 存在未完成预约")
//    void testDeleteCourse_HasActiveReservations() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(1L)).thenReturn(adminUser);
//        when(reservationMapper.countActiveCourseReservations(1L)).thenReturn(5);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.deleteCourse(1L, 1L);
//        });
//
//        assertEquals("该课程存在未完成的预约，无法删除", exception.getMessage());
//        verify(courseMapper, never()).deleteById(anyLong());
//    }
//
//    @Test
//    @DisplayName("删除课程 - 非创建者无权限")
//    void testDeleteCourse_NoPermission() {
//        // Mock
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(3L)).thenReturn(studentUser);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.deleteCourse(1L, 3L);
//        });
//
//        assertEquals("只有管理员或课程创建者可以删除课程", exception.getMessage());
//        verify(courseMapper, never()).deleteById(anyLong());
//    }
//
//    @Test
//    @DisplayName("获取课程详情 - 成功")
//    void testGetCourseById_Success() {
//        // Mock
//        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
//        when(courseMapper.selectById(1L)).thenReturn(course);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        doNothing().when(cacheService).set(anyString(), anyString(), anyLong(), any());
//
//        // 执行
//        CourseDTO result = courseService.getCourseById(1L);
//
//        // 验证
//        assertNotNull(result);
//        assertEquals(1L, result.getId());
//        assertEquals("瑜伽基础课", result.getName());
//        assertEquals("李教练", result.getCoachName());
//        assertEquals(20, result.getAvailableSlots()); // 30 - 10
//
//        verify(courseMapper, times(1)).selectById(1L);
//    }
//
//    @Test
//    @DisplayName("获取课程详情 - 课程不存在")
//    void testGetCourseById_NotFound() {
//        // Mock
//        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
//        when(courseMapper.selectById(999L)).thenReturn(null);
//
//        // 执行并验证
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            courseService.getCourseById(999L);
//        });
//
//        assertEquals("课程不存在", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("获取课程列表 - 成功")
//    void testGetCourseList_Success() {
//        // Mock
//        when(cacheService.get(anyString(), eq(String.class))).thenReturn(null);
//
//        Page<Course> coursePage = new Page<>(1, 10);
//        coursePage.setRecords(java.util.Collections.singletonList(course));
//        coursePage.setTotal(1);
//
//        when(courseMapper.searchCourses(any(Page.class), any()))
//                .thenReturn(coursePage);
//        when(userMapper.selectById(2L)).thenReturn(coachUser);
//        doNothing().when(cacheService).set(anyString(), anyString(), anyLong(), any());
//
//        // 执行
//        Page<CourseDTO> pageParam = new Page<>(1, 10);
//        com.fitness.dto.CourseQueryDTO query = com.fitness.dto.CourseQueryDTO.builder()
//                .keyword("瑜伽")
//                .build();
//        IPage<CourseDTO> result = courseService.getCourseList(pageParam, query);
//
//        // 验证
//        assertNotNull(result);
//        assertEquals(1, result.getTotal());
//        assertEquals(1, result.getRecords().size());
//        assertEquals("瑜伽基础课", result.getRecords().get(0).getName());
//
//        verify(courseMapper, times(1)).searchCourses(any(Page.class), any());
//    }
//
//    @Test
//    @DisplayName("清除课程缓存 - 清除特定课程")
//    void testClearCourseCache_SpecificCourse() {
//        // Mock
//        doNothing().when(cacheService).delete(anyString());
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        courseService.clearCourseCache(1L);
//
//        // 验证
//        verify(cacheService, times(1)).delete(anyString());
//        verify(cacheService, times(1)).deletePattern(anyString());
//    }
//
//    @Test
//    @DisplayName("清除课程缓存 - 清除所有课程")
//    void testClearCourseCache_AllCourses() {
//        // Mock
//        doNothing().when(cacheService).deletePattern(anyString());
//
//        // 执行
//        courseService.clearCourseCache(null);
//
//        // 验证
//        verify(cacheService, never()).delete(anyString());
//        verify(cacheService, times(1)).deletePattern(anyString());
//    }
//}
