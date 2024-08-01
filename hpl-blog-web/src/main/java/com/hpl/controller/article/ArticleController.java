package com.hpl.controller.article;

import com.hpl.article.pojo.dto.ArticleDTO;
import com.hpl.article.pojo.vo.ArticleListVo;
import com.hpl.article.pojo.vo.CategoryVo;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.ArticleService;
import com.hpl.article.service.CategoryService;
import com.hpl.article.service.TagService;
import com.hpl.converter.MarkdownConverter;
import com.hpl.pojo.CommonController;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/24 9:34
 */
/**
 * 文章列表视图
 *
 * @author yihui
 */
@RequestMapping(path = "article")
@RestController
@Tag(name = "文章列表视图")
@Slf4j
public class ArticleController extends CommonController {

    @Autowired
    private ArticleReadService articleService1;

    @Resource
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

//    @Autowired
//    private TagService tagService;


    @GetMapping(path = "categories")
    @Operation(summary = "获取所有文章分类")
    public CommonResult<?> getCategories(){
        List<CategoryVo> res= categoryService.getAllCategories();
        return CommonResult.data(res);
    }


    /**
     * 查询某个分类下的文章列表
     *
     * @param category
     * @return
     */
    @GetMapping(path = "category/{category}")
    @Operation(summary = "查询某个分类下的文章列表")
    public CommonResult<?> categoryList(@PathVariable("category") String category) {
        log.warn("lailelail");
        Long categoryId = categoryService.getIdByName(category);

        CommonPageListVo<ArticleListVo> listVo = articleService.listArticlesByCategory(categoryId, CommonPageParam.newInstance());
//        CommonPageListVo<ArticleListVo> listVo = null;
        return CommonResult.data(listVo);
    }




    @GetMapping(path = "/detail/{articleId}")
    @Operation(summary = "获取文章详情")
    public CommonResult<?> getArticleDetail(@PathVariable("articleId") Long articleId) {
        ArticleDTO article = articleService1.getArticleInfoById(articleId);

        //返回给前端页面是，将文章内容由md格式转为html格式
        article.setContent(MarkdownConverter.markdownToHtml(article.getContent()));
        return CommonResult.data(article);
    }

//    @GetMapping(path = "/top/author")
//    @Operation(summary = "获取作者排行")
//    public CommonResult<?> getTopAuthor() {
//        TopAuthorVO topAuthorVo = articleService.getTopAuthor();
//        return CommonResult.data();
//    }

}