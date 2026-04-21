package com.sky;

import lombok.extern.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //開啟註解方式的事務管理
@Slf4j
@EnableCaching //開發緩存註解功能
public class SkyApplication {
    public static void main(String[] args) {
        //前端需要打開nginx.exe才連得上
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
