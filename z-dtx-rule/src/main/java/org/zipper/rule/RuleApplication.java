package org.zipper.rule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "org.zipper.rule",
        "org.zipper.helper.web.cross"
})
@MapperScan(basePackages = "org.zipper.rule.mapper")
@EnableEurekaClient
@EnableFeignClients
public class RuleApplication {

    public static void main(String[] args) {

        SpringApplication.run(RuleApplication.class, args);
    }

}
