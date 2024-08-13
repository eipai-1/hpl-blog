package com.hpl.controller.user;

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

    @Operation(summary = "获取文章作者信息")
    @GetMapping("/{articleId}")
    public CommonResult<?> getAuthorByAId(@PathVariable Long articleId) {
        AuthorDTO authorDTO = userInfoService.getAuthorByArticleId(articleId);

        return CommonResult.data(authorDTO);
    }

    @Operation(summary = "根据token获取用户信息")
    @GetMapping("/info")
    public CommonResult<?> getUserInfo(HttpServletRequest request) {
        log.warn("session: {}", request.getHeader("Authorization"));
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            // 去掉Bearer
            String token = authorizationHeader.substring(7);

            return CommonResult.data(userInfoService.getUserInfoBySessionId(token, "127.0.0.1"));
        }
//        return CommonResult.data(userInfoService.getUserInfoBySessionId(session, "127.0.0.1"));
        return CommonResult.error("token无效");
    }
}
