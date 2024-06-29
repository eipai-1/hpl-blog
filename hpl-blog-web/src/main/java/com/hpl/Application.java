package com.hpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author : rbe
 * @date : 2024/6/29 14:53
 */
@Slf4j
@RestController
@SpringBootApplication
public class Application implements ApplicationRunner {

    @Value("${server.port}")
    private Integer webPort;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        String index="http://127.0.0.1:" + webPort;
        log.info("启动成功，点击访问首页：{}",index);
    }


    @GetMapping("/")
    public String index() {
        return "WELCOME TO HYPER PERSONAL LEARNING BLOG";
    }
}
