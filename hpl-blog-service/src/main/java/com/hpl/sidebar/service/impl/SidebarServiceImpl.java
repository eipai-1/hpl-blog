package com.hpl.sidebar.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.base.Splitter;
import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.article.service.ArticleReadService;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.enums.ConfigTypeEnum;
import com.hpl.config.pojo.enums.SidebarStyleEnum;
import com.hpl.config.service.ConfigService;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.sidebar.pojo.dto.RateVisitDTO;
import com.hpl.sidebar.pojo.dto.SideBarDTO;
import com.hpl.sidebar.pojo.dto.SideBarItemDTO;
import com.hpl.sidebar.service.SidebarService;
import com.hpl.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/7 17:42
 */
@Service
public class SidebarServiceImpl implements SidebarService {

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ConfigService configService;

//    @Autowired
//    private UserActivityRankService userActivityRankService;



    /**
     * 使用caffeine本地缓存，来处理侧边栏不怎么变动的消息
     * <p>
     * cacheNames -> 类似缓存前缀的概念
     * key -> SpEL 表达式，可以从传参中获取，来构建缓存的key
     * cacheManager -> 缓存管理器，如果全局只有一个时，可以省略
     *
     * @return
     */
    @Override
    @Cacheable(key = "'homeSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "home")
    public List<SideBarDTO> queryHomeSidebarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(noticeSideBar());
        list.add(columnSideBar());
        list.add(hotArticles());
//        SideBarDTO bar = rankList();
        SideBarDTO bar = new SideBarDTO();
        if (bar != null) {
            list.add(bar);
        }
        return list;
    }

    /**
     * 公告信息
     *
     * @return
     */
    private SideBarDTO noticeSideBar() {
        List<ConfigDTO> noticeList = configService.getConfigList(ConfigTypeEnum.NOTICE);
        List<SideBarItemDTO> items = new ArrayList<>(noticeList.size());
        noticeList.forEach(configDTO -> {
            List<Integer> configTags;
            if (StringUtils.isBlank(configDTO.getTags())) {
                configTags = Collections.emptyList();
            } else {
                configTags = Splitter.on(",").splitToStream(configDTO.getTags()).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
            }
            items.add(new SideBarItemDTO()
                    .setName(configDTO.getName())
                    .setTitle(configDTO.getContent())
                    .setUrl(configDTO.getJumpUrl())
                    .setTime(configDTO.getCreateTime().getTime())
                    .setTags(configTags)
            );
        });
        return new SideBarDTO()
                .setTitle("关于技术派")
                // TODO 知识星球的
                .setImg("https://cdn.tobebetterjavaer.com/paicoding/main/paicoding-zsxq.jpg")
                .setUrl("https://paicoding.com/article/detail/169")
                .setItems(items)
                .setStyle(SidebarStyleEnum.NOTICE.getStyle());
    }


    /**
     * 推荐教程的侧边栏
     *
     * @return
     */
    private SideBarDTO columnSideBar() {
        List<ConfigDTO> columnList = configService.getConfigList(ConfigTypeEnum.COLUMN);
        List<SideBarItemDTO> items = new ArrayList<>(columnList.size());
        columnList.forEach(configDTO -> {
            SideBarItemDTO item = new SideBarItemDTO();
            item.setName(configDTO.getName());
            item.setTitle(configDTO.getContent());
            item.setUrl(configDTO.getJumpUrl());
            item.setImg(configDTO.getBannerUrl());
            items.add(item);
        });
        return new SideBarDTO().setTitle("精选教程").setItems(items).setStyle(SidebarStyleEnum.COLUMN.getStyle());
    }


    /**
     * 热门文章
     *
     * @return
     */
    private SideBarDTO hotArticles() {
        CommonPageListVo<SimpleArticleDTO> vo = articleReadService.listHotArticlesForRecommend(CommonPageParam.newInstance(1, 8));

        List<SideBarItemDTO> items = vo.getList().stream()
                .map(s -> new SideBarItemDTO().setTitle(s.getTitle()).setUrl("/article/detail/" + s.getId()).setTime(s.getCreateTime().getTime()))
                .collect(Collectors.toList());

        return new SideBarDTO()
                .setTitle("热门文章")
                .setItems(items)
                .setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }



    /**
     * 排行榜
     *
     * @return
     */
//    private SideBarDTO rankList() {
//        List<RankItemDTO> list = userActivityRankService.queryRankList(ActivityRankTimeEnum.MONTH, 8);
//        if (list.isEmpty()) {
//            return null;
//        }
//        SideBarDTO sidebar = new SideBarDTO().setTitle("月度活跃排行榜").setStyle(SidebarStyleEnum.ACTIVITY_RANK.getStyle());
//        List<SideBarItemDTO> itemList = new ArrayList<>();
//        for (RankItemDTO item : list) {
//            SideBarItemDTO sideItem = new SideBarItemDTO().setName(item.getUser().getName())
//                    .setUrl(String.valueOf(item.getUser().getUserId()))
//                    .setImg(item.getUser().getAvatar())
//                    .setTime(item.getScore().longValue());
//            itemList.add(sideItem);
//        }
//        sidebar.setItems(itemList);
//        return sidebar;
//    }

    /**
     * 以用户 + 文章维度进行缓存设置
     *
     * @param author    文章作者id
     * @param articleId 文章id
     * @return
     */
    @Override
    @Cacheable(key = "'sideBar_' + #articleId", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public List<SideBarDTO> queryArticleDetailSidebarList(Long author, Long articleId) {
        List<SideBarDTO> list = new ArrayList<>(2);
        // 不能直接使用 pdfSideBar()的方式调用，会导致缓存不生效
        list.add(SpringUtil.getBean(SidebarServiceImpl.class).pdfSideBar());
        list.add(recommendByAuthor(author, articleId, CommonPageParam.DEFAULT_PAGE_SIZE));
        return list;

    }



    /**
     * PDF 优质资源
     *
     * @return
     */
    @Cacheable(key = "'sideBar'", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public SideBarDTO pdfSideBar() {
        List<ConfigDTO> pdfList = configService.getConfigList(ConfigTypeEnum.PDF);
        List<SideBarItemDTO> items = new ArrayList<>(pdfList.size());
        pdfList.forEach(configDTO -> {
            SideBarItemDTO dto = new SideBarItemDTO();
            dto.setName(configDTO.getName());
            dto.setUrl(configDTO.getJumpUrl());
            dto.setImg(configDTO.getBannerUrl());
            RateVisitDTO visit;
            if (StringUtils.isNotBlank(configDTO.getExtra())) {
                visit = (JsonUtil.strToObj(configDTO.getExtra(), RateVisitDTO.class));
            } else {
                visit = new RateVisitDTO();
            }
            visit.incrVisit();
            // 更新阅读计数
            configService.updateVisit(configDTO.getId(), JsonUtil.objToStr(visit));
            dto.setVisit(visit);
            items.add(dto);
        });

        return new SideBarDTO()
                .setTitle("优质PDF")
                .setItems(items)
                .setStyle(SidebarStyleEnum.PDF.getStyle());
    }





    /**
     * 查询教程的侧边栏信息
     *
     * @return
     */
    @Override
    @Cacheable(key = "'columnSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "column")
    public List<SideBarDTO> queryColumnSidebarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(subscribeSideBar());
        return list;
    }


    /**
     * 订阅公众号
     *
     * @return
     */
    private SideBarDTO subscribeSideBar() {
        return new SideBarDTO().setTitle("订阅")
                .setSubTitle("楼仔")
                .setImg("//cdn.tobebetterjavaer.com/paicoding/a768cfc54f59d4a056f79d1c959dcae9.jpg")
                .setContent("10本校招必刷八股文")
                .setStyle(SidebarStyleEnum.SUBSCRIBE.getStyle());
    }





    /**
     * 根据作者ID和文章ID推荐侧边栏文章。
     * 该方法用于生成侧边栏的推荐文章列表，不包括给定文章ID的文章。
     * 推荐的文章是根据阅读量热度进行排序的。
     *
     * @param authorId 作者ID，用于指定推荐文章的作者。
     * @param articleId 当前文章ID，用于排除在推荐列表中的文章。
     * @param size 推荐文章的数量。
     * @return SideBarDTO 包含推荐文章信息的侧边栏数据对象。
     */
    public SideBarDTO recommendByAuthor(Long authorId, Long articleId, long size) {
        // 查询热门推荐文章列表，默认分页参数，指定每页的文章数量为size
        CommonPageListVo<SimpleArticleDTO> listVo = articleReadService.listHotArticlesForRecommend(CommonPageParam.newInstance(CommonPageParam.DEFAULT_PAGE_NUM, size));

        // 获取文章列表
        List<SimpleArticleDTO> list = listVo.getList();

        // 过滤掉与当前文章ID相同的文章，并转换为SideBarItemDTO对象，用于填充侧边栏的项
        List<SideBarItemDTO> items = list.stream().filter(s -> !s.getId().equals(articleId))
                .map(s -> new SideBarItemDTO()
                        .setTitle(s.getTitle()).setUrl("/article/detail/" + s.getId())
                        .setTime(s.getCreateTime().getTime()))
                .collect(Collectors.toList());

        // 创建并返回SideBarDTO对象，包含相关文章的标题、文章列表和样式
        return new SideBarDTO().setTitle("相关文章").setItems(items).setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }

}
