package com.sky.config;

import com.sky.properties.CloudinaryProperties;
import com.sky.utils.CloudinaryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置類，用於創建cloudinaryUtil對象
 */
@Configuration
@Slf4j
public class CloudinaryConfiguration {
    @Bean
    public CloudinaryUtil cloudinaryUtil(CloudinaryProperties props) {
        log.info("開始創建 Cloudinary 工具類對象: {}", props.getCloudName());
        return new CloudinaryUtil(
                props.getCloudName(),
                props.getApiKey(),
                props.getApiSecret());
    }
}
