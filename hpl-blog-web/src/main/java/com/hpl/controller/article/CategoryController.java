package com.hpl.controller.article;

import com.hpl.article.pojo.dto.Category1TreeDTO;
import com.hpl.article.service.Category1Service;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/8/1 14:53
 */
@RestController
@RequestMapping("/category")
@Slf4j
@Tag(name = "文章分类相关接口")
public class CategoryController {

    @Resource
    private Category1Service category1Service;

    @Operation(summary = "获取课程分类树")
    @GetMapping("/course-category/tree-nodes")
    public CommonResult<?> queryTreeNodes() {
        return CommonResult.data(category1Service.getTreeCategories("0"));
    }

    @Operation(summary = "获取leafs")
    @PostMapping("/leafs")
    public CommonResult<?> queryTreeNodesLeaf(@RequestBody Category1TreeDTO category1TreeDTO) {
        log.warn("leafs");
        List<String> leafIds = category1Service.getLeafIds(category1TreeDTO);
        return CommonResult.data(leafIds);
    }
}
