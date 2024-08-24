package com.hpl.controller.article;

import com.hpl.article.pojo.dto.CategoryTreeDTO;
import com.hpl.article.pojo.entity.Category;
import com.hpl.article.service.CategoryService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/1 14:53
 */
@RestController
@RequestMapping("/category")
@Slf4j
@Tag(name = "article-文章分类相关接口")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取课程分类树")
    @GetMapping("/course-category/tree-nodes")
    public CommonResult<?> queryTreeNodes() {
        return CommonResult.data(categoryService.getTreeCategories("0"));
    }

//    @Operation(summary = "获取leafs")
//    @PostMapping("/leafs")
//    public CommonResult<?> queryTreeNodesLeaf(@RequestBody CategoryTreeDTO categoryTreeDTO) {
//        log.warn("leafs");
//        List<String> leafIds = categoryService.getLeafIds(categoryTreeDTO);
//        return CommonResult.data(leafIds);
//    }

    @Operation(summary = "获取所有leafs")
    @GetMapping("/leafs")
    public CommonResult<Object> getAllLeafs() {
        log.warn("leafs");
        List<Category> leafs = categoryService.getAllLeafs();
        return CommonResult.data(leafs);
    }
}
