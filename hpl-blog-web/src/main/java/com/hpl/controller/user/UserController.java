package com.hpl.controller.user;

import cn.hutool.core.util.StrUtil;
import com.hpl.pojo.CommonResult;
import com.hpl.rabbitmq.RabbitMqSender;
import com.hpl.redis.RedisClient;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.user.permission.Permission;
import com.hpl.user.permission.UserRole;
import com.hpl.user.pojo.dto.AuthorDTO;
import com.hpl.user.pojo.dto.AuthorDetailDTO;
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
@Tag(name = "user-用户信息相关接口")
@Slf4j
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisClient redisClient;

    @Resource
    private RabbitMqSender rabbitMqSender;

    @Operation(summary = "获取文章作者信息")
    @GetMapping("/{articleId}")
    public CommonResult<?> getAuthorByAId(@PathVariable Long articleId) {
        AuthorDTO authorDTO = userInfoService.getAuthorByArticleId(articleId);

        return CommonResult.data(authorDTO);
    }

    @Operation(summary = "获取作者详细信息")
    @GetMapping("/detail/{userId}")
    public CommonResult<?> getAuthorDetail(@PathVariable Long userId) {
        if (StrUtil.isBlank(userId.toString())) {
            return CommonResult.error("用户id不能为空");
        }
        AuthorDetailDTO authorDetailDTO = userInfoService.getAuthorDetailById(userId);

        return CommonResult.data(authorDetailDTO);
    }

    @Operation(summary = "根据token获取用户信息")
    @GetMapping("/info")
    @Permission(role = UserRole.USER)
    public CommonResult<?> getUserInfo(HttpServletRequest request) {
        log.warn("info: {}", ReqInfoContext.getReqInfo());
        return CommonResult.data(ReqInfoContext.getReqInfo());
    }

    @Operation(summary = "测试")
    @GetMapping("/test")
    public CommonResult<?> getTest(String token) {
        rabbitMqSender.sengMessage("123",token);
        return CommonResult.success();
    }


}
