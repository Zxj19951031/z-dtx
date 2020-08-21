package org.zipper.transport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@SpringBootApplication(scanBasePackages = {
        "org.zipper.transport",
        "org.zipper.helper.web.cross",
        "org.zipper.helper.swagger",

})
@ImportResource("classpath*:spring-aop.xml")
@MapperScan(basePackages = "org.zipper.transport.mapper")
@EnableEurekaClient
@EnableFeignClients
public class DtxTransportApplication {

    public static void main(String[] args) {
        SpringApplication.run(DtxTransportApplication.class, args);
    }
}
