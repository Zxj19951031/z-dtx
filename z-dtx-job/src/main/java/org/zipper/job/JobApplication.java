package org.zipper.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "org.zipper.job",
        "org.zipper.helper.web.cross"
})
@MapperScan(basePackages = "org.zipper.job.mapper")
@EnableEurekaClient
@EnableFeignClients
public class JobApplication {

    public static void main(String[] args) {

        SpringApplication.run(JobApplication.class, args);
    }

}
