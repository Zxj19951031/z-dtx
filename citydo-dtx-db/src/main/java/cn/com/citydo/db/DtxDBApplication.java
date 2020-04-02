package cn.com.citydo.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.com.citydo.db.mapper")
public class DtxDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DtxDBApplication.class, args);
    }
}
