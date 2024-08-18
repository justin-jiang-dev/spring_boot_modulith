package com.nuoson.modulith;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication()
@Modulithic()
@MapperScan(basePackages = {
        "com.nuoson.modulith.infra.**.mapper",
        "com.nuoson.modulith.infra.**.extendmapper"
})
public class Application {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
