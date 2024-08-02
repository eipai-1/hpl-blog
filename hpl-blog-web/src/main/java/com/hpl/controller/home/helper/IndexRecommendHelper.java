package com.hpl.controller.home.helper;

import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.dto1.CategoryDTO;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.CategoryService;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.enums.ConfigTypeEnum;
import com.hpl.config.service.ConfigService;
import com.hpl.controller.home.dto.CarouseDTO;
import com.hpl.controller.home.vo.IndexVo;
import com.hpl.global.context.ReqInfoContext;
import com.hpl.pojo.CommonConstants;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.sidebar.service.SidebarService;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/10 18:50
 */
@Component
public class IndexRecommendHelper {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ConfigService configService;

//    public IndexVo buildIndexVo(String activeTab) {
//        IndexVo vo = new IndexVo();
//        CategoryDTO category = categories(activeTab, vo);
//        vo.setCategoryId(category.getCategoryId());
//        vo.setCurrentCategory(category.getCategory());
//         //并行调度实例，提高响应性能 todo 并行运行不了，待处理，晚点我自己搞一个吧
//        AsyncUtil.concurrentExecutor("首页响应")
//                .runAsyncWithTimeRecord(() -> vo.setArticles(articleList(category.getCategoryId())), "文章列表")
//                .runAsyncWithTimeRecord(() -> vo.setTopArticles(topArticleList(category)), "置顶文章")
//                .runAsyncWithTimeRecord(() -> vo.setHomeCarouselList(homeCarouselList()), "轮播图")
//                .runAsyncWithTimeRecord(() -> vo.setSideBarItems(sidebarService.queryHomeSidebarList()), "侧边栏")
//                .runAsyncWithTimeRecord(() -> vo.setUser(loginInfo()), "用户信息")
//                .allExecuted()
//                .prettyPrint();
//
////        vo.setArticles(articleList(category.getCategoryId()));
////        vo.setTopArticles(topArticleList(category));
////        vo.setHomeCarouselList(homeCarouselList());
////        vo.setSideBarItems(sidebarService.queryHomeSidebarList());
////        vo.setUser(loginInfo());
//        return vo;
//    }

//    /**
//     * 返回分类列表
//     *
//     * @param active 选中的分类
//     * @param vo     返回结果
//     * @return 返回选中的分类；当没有匹配时，返回默认的全部分类
//     */
//    private CategoryDTO categories(String active, IndexVo vo) {
//        List<CategoryDTO> allList = categoryService.getAllCategories();
//        // 查询所有分类的对应的文章数
//        Map<Long, Long> articleCnt = articleReadService.queryArticleCountsAndCategory();
//        // 过滤掉文章数为0的分类
//        allList.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);
//
//        // 刷新选中的分类
//        AtomicReference<CategoryDTO> selectedArticle = new AtomicReference<>();
//        allList.forEach(category -> {
//            if (category.getCategory().equalsIgnoreCase(active)) {
//                category.setSelected(true);
//                selectedArticle.set(category);
//            } else {
//                category.setSelected(false);
//            }
//        });
//
//        // 添加默认的全部分类
//        allList.add(0, new CategoryDTO(0L, CategoryDTO.DEFAULT_TOTAL_CATEGORY));
//        if (selectedArticle.get() == null) {
//            selectedArticle.set(allList.get(0));
//            allList.get(0).setSelected(true);
//        }
//
//        vo.setCategories(allList);
//        return selectedArticle.get();
//    }

    /**
     * 文章列表
     */
    private CommonPageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleReadService.listArticlesByCategory(categoryId, CommonPageParam.newInstance());
    }

    public IndexVo buildSearchVo(String key) {
        IndexVo vo = new IndexVo();
        vo.setArticles(articleReadService.listArticlesBySearchKey(key, CommonPageParam.newInstance()));
        vo.setSideBarItems(sidebarService.queryHomeSidebarList());
        return vo;
    }

    /**
     * 轮播图
     *
     * @return
     */
    private List<CarouseDTO> homeCarouselList() {
        List<ConfigDTO> configList = configService.getConfigList(ConfigTypeEnum.HOME_PAGE);
        return configList.stream()
                .map(configDTO -> new CarouseDTO()
                        .setName(configDTO.getName())
                        .setImgUrl(configDTO.getBannerUrl())
                        .setActionUrl(configDTO.getJumpUrl()))
                .collect(Collectors.toList());
    }


    /**
     * 置顶top 文章列表
     */
    private List<ArticleDTO> topArticleList(CategoryDTO category) {
        List<ArticleDTO> topArticles = articleReadService.getTopArticlesByCategoryId(category.getCategoryId() == 0 ? null : category.getCategoryId());
        if (topArticles.size() < CommonPageParam.TOP_PAGE_SIZE) {
            // 当分类下文章数小于置顶数时，为了避免显示问题，直接不展示
            topArticles.clear();
            return topArticles;
        }

        // 查询分类对应的头图列表
        List<String> topPicList = CommonConstants.HOMEPAGE_TOP_PIC_MAP.getOrDefault(category.getCategory(),
                CommonConstants.HOMEPAGE_TOP_PIC_MAP.get(CommonConstants.CATEGORY_ALL));

        // 替换头图，下面做了一个数组越界的保护，避免当topPageSize数量变大，但是默认的cover图没有相应增大导致数组越界异常
        AtomicInteger index = new AtomicInteger(0);
        topArticles.forEach(s -> s.setCover(topPicList.get(index.getAndIncrement() % topPicList.size())));
        return topArticles;
    }


    private UserInfo loginInfo() {
        if (ReqInfoContext.getReqInfo() != null && ReqInfoContext.getReqInfo().getUserId() != null) {
            return userInfoService.getByUserId(ReqInfoContext.getReqInfo().getUserId());
        }
        return null;
    }
}
