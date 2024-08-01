//package com.hpl.controller.article.rest;
//
//
//import com.hpl.article.pojo.dto.ColumnDTO;
//import com.hpl.article.pojo.dto.SimpleArticleDTO;
//import com.hpl.article.service.ColumnService;
//import com.hpl.global.component.TemplateEngineHelper;
//import com.hpl.pojo.CommonPageListVo;
//import com.hpl.pojo.CommonPageParam;
//import com.hpl.pojo.CommonResVo;
//import com.hpl.pojo.NextPageHtmlVo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * @author YiHui
// * @date 2022/9/15
// */
//@RestController
//@RequestMapping(path = "column/api")
//public class ColumnRestController {
//
//    @Autowired
//    private ColumnService columnService;
//
//    @Autowired
//    private TemplateEngineHelper templateEngineHelper;
//
//    /**
//     * 翻页的专栏列表
//     *
//     * @param page
//     * @param size
//     * @return
//     */
//    @GetMapping(path = "list")
//    public CommonResVo<NextPageHtmlVo> list(@RequestParam(name = "page") Long page,
//                                            @RequestParam(name = "size", required = false) Long size) {
//        if (page <= 0) {
//            page = 1L;
//        }
//        size = Optional.ofNullable(size).orElse(CommonPageParam.DEFAULT_PAGE_SIZE);
//        size = Math.min(size, CommonPageParam.DEFAULT_PAGE_SIZE);
//        CommonPageListVo<ColumnDTO> list = columnService.listColumn(CommonPageParam.newInstance(page, size));
//
//        String html = templateEngineHelper.renderToVo("biz/column/list", "columns", list);
//        return CommonResVo.success(new NextPageHtmlVo(html, list.getHasMore()));
//    }
//
//    /**
//     * 详情页的菜单栏(即专栏的文章列表)
//     *
//     * @param columnId
//     * @return
//     */
//    @GetMapping(path = "menu/{column}")
//    public CommonResVo<NextPageHtmlVo> columnMenus(@PathVariable("column") Long columnId) {
//        List<SimpleArticleDTO> articleList = columnService.queryColumnArticles(columnId);
//        String html = templateEngineHelper.renderToVo("biz/column/menus", "menu", articleList);
//        return CommonResVo.success(new NextPageHtmlVo(html, false));
//    }
//}
