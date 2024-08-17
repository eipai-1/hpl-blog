package com.hpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;





/**
 * @author : rbe
 * @date : 2024/6/29 14:53
 */
@Slf4j
@Controller
@SpringBootApplication(scanBasePackages = {"com.hpl"})
@EnableAsync
@EnableCaching
public class Application implements ApplicationRunner {

    @Value("${server.port}")
    private Integer webPort;

    @Autowired
    private RedisTemplate<String,String> template;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        String index="http://127.0.0.1:" + webPort;
        log.info("后端启动成功，点击访问首页：{}",index);
        log.info("后端启动成功，点击访问文档：{}/doc.html",index);
        log.info("template：{}",template);
    }


//    @GetMapping("/")
//    public String index(HttpServletRequest request) {
//        log.info("index098u08欸点击可能是当年 da");
//        return "error/index";
//    }

}
