package com.hpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author : rbe
 * @date : 2024/6/29 14:53
 */
@Slf4j
@Controller
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
//        return "WELCOME TO HYPER PERSONAL LEARNING BLOG";
        return "error/test1/test001/index";
//        return "index";
//        return "views/rank/index";
    }

}
