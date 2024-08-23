package com.hpl.controller.count;

import com.hpl.pojo.CommonController;
import com.hpl.pojo.CommonResult;
import com.hpl.statistic.pojo.dto.CountAllDTO;
import com.hpl.statistic.service.TraceCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : rbe
 * @date : 2024/8/2 18:18
 */
@RequestMapping(path = "count")
@RestController
@Tag(name = "count-计数控制器")
@Slf4j
public class TraceCountController extends CommonController {

    @Resource
    private TraceCountService traceCountService;


    @Operation(summary = "查询文章的点赞、评论、收藏数量")
    @GetMapping("/all/{articleId}")
    public CommonResult<CountAllDTO> getCountInfoByArticleId(@PathVariable Long articleId) {

        //只查询文章不涉及作者 ， 置空
       CountAllDTO countInfo =  traceCountService.getAllCountById(null, articleId);

       return CommonResult.data(countInfo);
    }
}
