//package com.hpl.controller.article.view;
//
//import com.hpl.article.pojo.dto.ArticleDTO;
//import com.hpl.article.pojo.vo.ArticleListVo;
//import com.hpl.article.service.ArticleReadService;
//import com.hpl.article.service.CategoryService;
//import com.hpl.article.service.TagService;
//import com.hpl.pojo.CommonController;
//import com.hpl.pojo.CommonPageListVo;
//import com.hpl.pojo.CommonPageParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// * 文章列表视图
// *
// * @author yihui
// */
//@RequestMapping(path = "article")
//@Controller
//public class ArticleListViewController extends CommonController {
//
//    @Autowired
//    private ArticleReadService articleService;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private TagService tagService;
//
//    /**
//     * 查询某个分类下的文章列表
//     *
//     * @param category
//     * @return
//     */
//    @GetMapping(path = "category/{category}")
//    public String categoryList(@PathVariable("category") String category, Model model) {
//        Long categoryId = categoryService.getIdByName(category);
//        CommonPageListVo<ArticleDTO> list = categoryId != null ? articleService.listArticlesByCategory(categoryId, CommonPageParam.newInstance()) : CommonPageListVo.emptyVo();
//        ArticleListVo vo = new ArticleListVo();
//        vo.setArchives(category);
//        vo.setArchiveId(categoryId);
//        vo.setArticles(list);
//        model.addAttribute("vo", vo);
//        return "views/article-category-list/index";
//    }
//
//    /**
//     * 查询某个标签下文章列表
//     *
//     * @param tag
//     * @param model
//     * @return
//     */
//    @GetMapping(path = "tag/{tag}")
//    public String tagList(@PathVariable("tag") String tag, Model model) {
//        Long tagId = tagService.queryTagId(tag);
//        CommonPageListVo<ArticleDTO> list = tagId != null ? articleService.listArticlesByTag(tagId, CommonPageParam.newInstance()) : CommonPageListVo.emptyVo();
//        ArticleListVo vo = new ArticleListVo();
//        vo.setArchives(tag);
//        vo.setArchiveId(tagId);
//        vo.setArticles(list);
//        model.addAttribute("vo", vo);
//        return "views/article-tag-list/index";
//    }
//}
