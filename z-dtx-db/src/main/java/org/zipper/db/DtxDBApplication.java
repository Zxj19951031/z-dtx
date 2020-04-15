package org.zipper.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@SpringBootApplication
@MapperScan(basePackages = "org.zipper.db.mapper")
@EnableEurekaClient
@EnableFeignClients
public class DtxDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DtxDBApplication.class, args);
    }
}
