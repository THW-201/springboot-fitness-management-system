package com.fitness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * DeepSeek AI服务配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek.api")
public class DeepSeekConfig {

    /**
     * API密钥
     */
    private String key;

    /**
     * API端点
     */
    private String endpoint;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 连接超时时间（毫秒），默认 10 秒
     * 作用：与 DeepSeek 服务器建立网络连接的最大等待时间
     */
    private Integer connectTimeout = 10000;

    /**
     * 读取超时时间（毫秒），默认 90 秒 (90000毫秒)
     * 作用：连接建立后，等待 DeepSeek 逐字生成并返回数据的最大等待时间
     */
    private Integer readTimeout = 90000;

    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;

    /**
     * 配置RestTemplate用于HTTP调用
     */
    @Bean(name = "deepSeekRestTemplate")
    public RestTemplate deepSeekRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 分别设置不同的超时时间
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}