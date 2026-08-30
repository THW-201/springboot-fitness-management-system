package com.fitness.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.fitness.interceptor.DataIsolationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
/**
 * MyBatis Plus配置类
 * 配置分页插件和其他拦截器
 */
@Configuration
@RequiredArgsConstructor
public class MyBatisPlusConfig {

    private final DataIsolationInterceptor dataIsolationInterceptor;

    /**
     * 配置MyBatis Plus拦截器
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L); // 单页最大数量限制
        paginationInterceptor.setOverflow(false); // 溢出总页数后是否进行处理
        
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
}
