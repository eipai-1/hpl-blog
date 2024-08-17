package com.hpl.controller.user;

import cn.hutool.core.util.StrUtil;
import com.hpl.redis.RedisClient;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.user.permission.Permission;
import com.hpl.user.permission.UserRole;
import com.hpl.pojo.CommonResult;
import com.hpl.user.pojo.dto.AuthorDTO;
import com.hpl.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author : rbe
 * @date : 2024/8/2 10:33
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户信息相关接口")
@Slf4j
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisClient redisClient;

    @Operation(summary = "获取文章作者信息")
    @GetMapping("/{articleId}")
    public CommonResult<?> getAuthorByAId(@PathVariable Long articleId) {
        AuthorDTO authorDTO = userInfoService.getAuthorByArticleId(articleId);

        return CommonResult.data(authorDTO);
    }

    @Operation(summary = "根据token获取用户信息")
    @GetMapping("/info")
    @Permission(role = UserRole.USER)
    public CommonResult<?> getUserInfo(HttpServletRequest request) {
        log.warn("info: {}", ReqInfoContext.getReqInfo());
        return CommonResult.data(ReqInfoContext.getReqInfo());
    }

    @Operation(summary = "根据token获取用户信息2")
    @GetMapping("/info2")
    public CommonResult<?> getUserInfo(String token) {
        log.warn("info: {}", ReqInfoContext.getReqInfo());
        log.warn("redisClient:{}", redisClient);
        redisClient.set("test-token", 1, 30L, TimeUnit.SECONDS);
        redisClient.set("token", token, 30L, TimeUnit.MINUTES);
        redisClient.set("test-token2", 2, 3L, TimeUnit.HOURS);


        redisClient.set("test-null", "", 3L, TimeUnit.HOURS);
        Object result = redisClient.get("123",Object.class);
        log.warn("result:{}",result);

        Object result1 = redisClient.get("test-null",Object.class);
        log.warn("result1:{}",result1);

        if(result1!=null){
            log.warn("等于’‘，但不为空");
        }

        return CommonResult.data(userInfoService.getUserInfoBySessionId(token, "127.0.0.1"));
    }


}
