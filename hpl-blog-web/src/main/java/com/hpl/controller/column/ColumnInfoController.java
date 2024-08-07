package com.hpl.controller.column;

import com.hpl.column.pojo.dto.ColumnListDTO;
import com.hpl.column.pojo.dto.ColumnPostDTO;
import com.hpl.column.service.ColumnInfoService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/3 10:05
 */
@RestController
@RequestMapping("/column")
@Tag(name = "专栏控制器")
@Slf4j
public class ColumnInfoController {

    @Resource
    private ColumnInfoService columnInfoService;

    @Operation(summary = "发布专栏")
    @PostMapping
    public CommonResult<?> publishColumn(@RequestBody ColumnPostDTO columnPostDTO) {
        columnInfoService.publishColumn(columnPostDTO);

        return CommonResult.success();
    }

    @Operation(summary = "获取专栏列表")
    @GetMapping("/list")
    public CommonResult<?> listColumns() {
        List<ColumnListDTO> res =  columnInfoService.listColumns();
        return CommonResult.data(res);
    }
}
