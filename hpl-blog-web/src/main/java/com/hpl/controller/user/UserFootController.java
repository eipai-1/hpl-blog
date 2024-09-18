package com.hpl.controller.user;

import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.pojo.CommonResult;
import com.hpl.user.permission.Permission;
import com.hpl.user.permission.UserRole;
import com.hpl.user.service.UserFootService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/27 9:19
 */
@RestController
@RequestMapping(path = "user-foot")
@Tag(name = "user-foot-用户足迹相关操作")
@Slf4j
public class UserFootController {

    @Resource
    private UserFootService userFootService;

    @Operation(summary = "（取消）点赞文章")
    @PutMapping("/praise/{articleId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> praiseArticle(@PathVariable Long articleId) {
        userFootService.praiseArticle(articleId);
        return CommonResult.success();
    }

    @Operation(summary = "（取消）收藏文章")
    @PutMapping("/collect/{articleId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> collectArticle(@PathVariable Long articleId) {
        userFootService.collectArticle(articleId);
        return CommonResult.success();
    }

    @Operation(summary = "获取用户最近阅读文章")
    @GetMapping("/recent")
    @Permission(role = UserRole.USER)
    public CommonResult<?> getReadRecent() {
        List<SimpleArticleDTO> recents = userFootService.getReadRecent();
        return CommonResult.data(recents);
    }

}
