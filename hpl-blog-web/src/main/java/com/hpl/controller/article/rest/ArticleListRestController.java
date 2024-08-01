//
//package com.hpl.controller.article.rest;
//
//
//import com.hpl.article.pojo.dto.ArticleDTO;
//import com.hpl.article.service.ArticleReadService;
//import com.hpl.global.component.TemplateEngineHelper;
//import com.hpl.pojo.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 文章列表
// *
// * @author yihui
// */
//@RequestMapping(path = "article/api/list")
//@RestController
//public class ArticleListRestController extends CommonController {
//
//    @Autowired
//    private ArticleReadService articleService;
//
//    @Autowired
//    private TemplateEngineHelper templateEngineHelper;
//
//    /**
//     * 分类下的文章列表
//     *
//     * @param categoryId 类目id
//     * @param page 请求页
//     * @param size 分页数
//     * @return 文章列表
//     */
//    @GetMapping(path = "data/category/{category}")
//    public CommonResVo<CommonPageListVo<ArticleDTO>> categoryDataList(@PathVariable("category") Long categoryId,
//                                                                       @RequestParam(name = "page") Long page,
//                                                                       @RequestParam(name = "size", required = false) Long size) {
//        CommonPageParam pageParam = buildPageParam(page, size);
//        CommonPageListVo<ArticleDTO> list = articleService.listArticlesByCategory(categoryId, pageParam);
//        return CommonResVo.success(list);
//    }
//
//
//    /**
//     * 分类下的文章列表
//     *
//     * @param categoryId
//     * @return
//     */
//    @GetMapping(path = "category/{category}")
//    public CommonResVo<NextPageHtmlVo> categoryList(@PathVariable("category") Long categoryId,
//                                              @RequestParam(name = "page") Long page,
//                                              @RequestParam(name = "size", required = false) Long size) {
//        CommonPageParam pageParam = buildPageParam(page, size);
//        CommonPageListVo<ArticleDTO> list = articleService.listArticlesByCategory(categoryId, pageParam);
//        String html = templateEngineHelper.renderToVo("views/article-category-list/article/list", "articles", list);
//        return CommonResVo.success(new NextPageHtmlVo(html, list.getHasMore()));
//    }
//
//    /**
//     * 标签下的文章列表
//     *
//     * @param tagId
//     * @param page
//     * @param size
//     * @return
//     */
//    @GetMapping(path = "tag/{tag}")
//    public CommonResVo<NextPageHtmlVo> tagList(@PathVariable("tag") Long tagId,
//                                         @RequestParam(name = "page") Long page,
//                                         @RequestParam(name = "size", required = false) Long size) {
//        CommonPageParam pageParam = buildPageParam(page, size);
//        CommonPageListVo<ArticleDTO> list = articleService.listArticlesByTag(tagId, pageParam);
//        String html = templateEngineHelper.renderToVo("views/article-tag-list/article/list", "articles", list);
//        return CommonResVo.success(new NextPageHtmlVo(html, list.getHasMore()));
//    }
//}
