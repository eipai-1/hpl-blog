package com.hpl.controller.user;

import com.hpl.pojo.CommonResult;
import com.hpl.user.pojo.dto.AuthorDTO;
import com.hpl.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    @Operation(summary = "获取文章作者信息")
    @GetMapping("/{articleId}")
    public CommonResult<?> getAuthorByAId(@PathVariable Long articleId) {
        AuthorDTO authorDTO = userInfoService.getAuthorByArticleId(articleId);

        return CommonResult.data(authorDTO);
    }
}
