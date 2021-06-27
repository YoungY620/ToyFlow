package cn.yy.toyflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.yy.toyflow.mapper")
public class ToyFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToyFlowApplication.class, args);
    }

}
