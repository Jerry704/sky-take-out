package com.sky.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Map;

@Data
@AllArgsConstructor
@Slf4j
public class CloudinaryUtil {
    private final Cloudinary cloudinary;

    public CloudinaryUtil(String cloudName, String apiKey, String apiSecret) {
        // 1. 先建立配置 Map
        Map<String, Object>  config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        );

        // 2. 直接建立 Cloudinary 物件
        this.cloudinary = new Cloudinary(config);


    }

    public String upload(byte[] bytes, String folder) {
        try {
            Map result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap("folder", folder));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary 上傳失敗", e);
            throw new RuntimeException("文件上傳失敗");
        }
    }
}

