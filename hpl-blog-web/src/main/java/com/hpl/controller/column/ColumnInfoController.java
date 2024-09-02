package com.hpl.controller.column;

import com.hpl.column.pojo.dto.*;
import com.hpl.column.service.ColumnInfoService;
import com.hpl.pojo.CommonResult;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.user.permission.Permission;
import com.hpl.user.permission.UserRole;
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
@Tag(name = "column-专栏控制器")
@Slf4j
public class ColumnInfoController {

    @Resource
    private ColumnInfoService columnInfoService;


    @Operation(summary = "获取专栏列表")
    @GetMapping("/list")
    public CommonResult<?> listColumns() {
        List<ColumnListDTO> res =  columnInfoService.listColumns();
        return CommonResult.data(res);
    }

    @Operation(summary = "我的专栏")
    @GetMapping("myself-list")
    @Permission(role = UserRole.USER)
    public CommonResult<?> listMyColumns(@RequestBody(required = false) SearchMyColumnDTO searchMyColumnDTO) {

        Long userId = ReqInfoContext.getReqInfo().getUserId();

        List<MyColumnListDTO> res =  columnInfoService.listMyColumns(searchMyColumnDTO,userId);
        return CommonResult.data(res);
    }

    @Operation(summary = "发布专栏")
    @PostMapping
    @Permission(role = UserRole.USER)
    public CommonResult<?> publishColumn(@RequestBody ColumnPostDTO columnPostDTO) {
        columnInfoService.publishColumn(columnPostDTO);

        return CommonResult.success();
    }

    @Operation(summary = "编辑专栏")
    @PutMapping()
    @Permission(role = UserRole.USER)
    public CommonResult<?> editColumn(@RequestBody ColumnEditDTO columnEditDTO) {
        columnInfoService.editColumn(columnEditDTO);

        return CommonResult.success();
    }

    @Operation(summary = "删除专栏")
    @DeleteMapping("/{columnId}")
    @Permission(role = UserRole.USER)
    public CommonResult<?> deleteColumn(@PathVariable("columnId") Long columnId) {
        columnInfoService.deleteById(columnId);

        return CommonResult.success();
    }

    @Operation(summary = "我的简单专栏")
    @GetMapping("/simple-list")
    @Permission(role = UserRole.USER)
    public CommonResult<?> listMySimpleColumns() {

        Long userId = ReqInfoContext.getReqInfo().getUserId();

        List<ColumnSimpleDTO> res =  columnInfoService.listMySimpleColumns(userId);
        return CommonResult.data(res);
    }
}
