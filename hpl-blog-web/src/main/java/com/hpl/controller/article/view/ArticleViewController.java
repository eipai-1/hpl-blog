//package com.hpl.controller.article.view;
//
//
//import cn.hutool.extra.spring.SpringUtil;
//import com.hpl.annotation.permission.Permission;
//import com.hpl.annotation.permission.UserRole;
//import com.hpl.article.pojo.entity.ColumnArticle;
//import com.hpl.article.pojo.dto.ArticleDTO;
//import com.hpl.article.pojo.dto.ArticleOtherDTO;
//import com.hpl.article.pojo.dto.CategoryDTO;
//import com.hpl.article.pojo.vo.ArticleDetailVo;
//import com.hpl.article.pojo.vo.ArticleEditVo;
//import com.hpl.article.service.ArticleReadService;
//import com.hpl.article.service.CategoryService;
//import com.hpl.article.service.ColumnService;
//import com.hpl.article.service.TagService;
//import com.hpl.converter.MarkdownConverter;
//import com.hpl.global.comtext.ReqInfoContext;
//import com.hpl.global.service.SeoInjectService;
//import com.hpl.pojo.CommonController;
//import com.hpl.sidebar.pojo.dto.SideBarDTO;
//import com.hpl.sidebar.service.SidebarService;
//import com.hpl.user.pojo.entity.UserInfo;
//import com.hpl.user.service.UserInfoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.util.Collections;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * 文章
// * todo: 所有的入口都放在一个Controller，会导致功能划分非常混乱
// * ： 文章列表
// * ： 文章编辑
// * ： 文章详情
// * ---
// *  - 返回视图 view
// *  - 返回json数据
// *
// * @author yihui
// */
//@Controller
//@RequestMapping(path = "article")
//public class ArticleViewController extends CommonController {
//
//    @Autowired
//    private ArticleReadService articleReadService;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private TagService tagService;
//
//    @Autowired
//    private UserInfoService userInfoService;
//
////    @Autowired
////    private CommentReadService commentService;
//
//    @Autowired
//    private SidebarService sidebarService;
//
//    @Autowired
//    private ColumnService columnService;
//
//    /**
//     * 文章编辑页
//     *
//     * @param articleId
//     * @return
//     */
//    @Permission(role = UserRole.LOGIN)
//    @GetMapping(path = "edit")
//    public String edit(@RequestParam(required = false) Long articleId, Model model) {
//        ArticleEditVo vo = new ArticleEditVo();
//        if (articleId != null) {
//            ArticleDTO article = articleReadService.getArticleInfoById(articleId);
//            vo.setArticle(article);
//            if (!Objects.equals(article.getAuthorId(), ReqInfoContext.getReqInfo().getUserId())) {
//                // 没有权限
//                model.addAttribute("toast", "内容不存在");
//                return "redirect:403";
//            }
//
//            List<CategoryDTO> categoryList = categoryService.getAllCategories();
//            categoryList.forEach(s -> {
//                s.setSelected(s.getCategoryId().equals(article.getCategory().getCategoryId()));
//            });
//            vo.setCategories(categoryList);
//            vo.setTags(article.getTags());
//        } else {
//            List<CategoryDTO> categoryList = categoryService.getAllCategories();
//            vo.setCategories(categoryList);
//            vo.setTags(Collections.emptyList());
//        }
//        model.addAttribute("vo", vo);
//        return "views/article-edit/index";
//    }
//
//
//    /**
//     * 文章详情页
//     * - 参数解析知识点
//     * - fixme * [1.Get请求参数解析姿势汇总 | 一灰灰Learning](https://hhui.top/spring-web/01.request/01.190824-springboot%E7%B3%BB%E5%88%97%E6%95%99%E7%A8%8Bweb%E7%AF%87%E4%B9%8Bget%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0%E8%A7%A3%E6%9E%90%E5%A7%BF%E5%8A%BF%E6%B1%87%E6%80%BB/)
//     *
//     * @param articleId
//     * @return
//     */
//    @GetMapping("detail/{articleId}")
//    public String detail(@PathVariable(name = "articleId") Long articleId, Model model) throws IOException {
//        // 针对专栏文章，做一个重定向
//        ColumnArticle columnArticle = columnService.getColumnArticleRelation(articleId);
//        if (columnArticle != null) {
//            return String.format("redirect:/column/%d/%d", columnArticle.getColumnId(), columnArticle.getSection());
//        }
//
//        ArticleDetailVo vo = new ArticleDetailVo();
//        // 文章相关信息
//        ArticleDTO articleDTO = articleReadService.getFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
//        // 返回给前端页面时，转换为html格式
//        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
//        vo.setArticle(articleDTO);
//
//        // 评论信息 todo
////        List<TopCommentDTO> comments = commentService.getArticleComments(articleId, PageParam.newPageInstance(1L, 10L));
////        vo.setComments(comments);
//        vo.setComments(null);
//
//        // 热门评论 todo
////        TopCommentDTO hotComment = commentService.queryHotComment(articleId);
////        vo.setHotComment(hotComment);
//        vo.setHotComment(null);
//
//
//        // 其他信息封装
//        ArticleOtherDTO other = new ArticleOtherDTO();
//        // 作者信息
//        UserInfo user = userInfoService.getById(articleDTO.getAuthorId());
//        articleDTO.setAuthorName(user.getNickName());
//        articleDTO.setAuthorAvatar(user.getPhoto());
//        vo.setAuthor(user);
//
//        vo.setOther(other);
//
//        // 详情页的侧边推荐信息
//        List<SideBarDTO> sideBars = sidebarService.queryArticleDetailSidebarList(articleDTO.getAuthorId(), articleDTO.getArticleId());
//        vo.setSideBarItems(sideBars);
//        model.addAttribute("vo", vo);
//
//        SpringUtil.getBean(SeoInjectService.class).initColumnSeo(vo);
//        return "views/article-detail/index";
//    }
//
//
//}
