package com.hpl.controller.column;

import com.hpl.column.pojo.dto.ColumnDirectoryDTO;
import com.hpl.column.service.ColumnArticleService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/3 13:36
 */
@RestController
@RequestMapping("/column-article")
@Tag(name = "专栏文章控制器")
@Slf4j
public class ColumnArticleController {

    @Resource
    private ColumnArticleService columnArticleService;

//    @Operation(summary = "获取专栏文章列表")
//    @GetMapping("/{columnId}")
//    public void getCountByColumnId(@PathVariable("columnId") Long columnId) {
//
//    }

    @Operation(summary = "获取专栏目录列表")
    @GetMapping("directory/{columnId}")
    public CommonResult<?> getDirectoryById(@PathVariable("columnId") Long columnId) {
        List<ColumnDirectoryDTO> res= columnArticleService.getDirectoryById(columnId);

        return CommonResult.data(res);
    }

}
