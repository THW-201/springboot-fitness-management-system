package com.fitness;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 大学生健身管理系统主应用类
 * 
 * @author Fitness Team
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.fitness")
@EnableCaching
@EnableScheduling
@MapperScan("com.fitness.mapper")
public class FitnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnessApplication.class, args);
        System.out.println("========================================");
        System.out.println("大学生健身管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/v1/swagger-ui.html");
        System.out.println("========================================");
    }
}
