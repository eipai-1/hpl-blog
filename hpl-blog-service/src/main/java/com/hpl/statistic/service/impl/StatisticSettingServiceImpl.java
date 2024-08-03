//package com.hpl.statistic.service.impl;
//
//import com.hpl.article.service.ArticleReadService;
//import com.hpl.column.service.ColumnService;
//import com.hpl.statistic.pojo.dto.StatisticUserFootDTO;
//import com.hpl.statistic.pojo.dto.StatisticsCountDTO;
//import com.hpl.statistic.pojo.dto.StatisticsDayDTO;
//import com.hpl.statistic.pojo.entity.RequestCount;
//import com.hpl.statistic.service.RequestCountService;
//import com.hpl.statistic.service.StatisticSettingService;
//import com.hpl.user.service.UserFootService;
//import com.hpl.user.service.UserInfoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * @author : rbe
// * @date : 2024/7/9 10:55
// */
//@Service
//public class StatisticSettingServiceImpl implements StatisticSettingService {
//
//    @Autowired
//    private RequestCountService requestCountService;
//
//    @Autowired
//    private UserInfoService userInfoService;
//
//    @Autowired
//    private ColumnService columnService;
//
//    @Autowired
//    private UserFootService userFootService;
//
//    @Autowired
//    private ArticleReadService articleReadService;
//
//    @Override
//    public void saveRequestCount(String host) {
//        RequestCount requestCount = requestCountService.getRequestCount(host);
//        if (requestCount == null) {
//            requestCountService.insert(host);
//        } else {
//            // 改为数据库直接更新
//            requestCountService.incrementCount(requestCount.getId());
//        }
//    }
//
//    @Override
//    public StatisticsCountDTO getStatisticsCount() {
//        // 从 user_foot 表中查询点赞数、收藏数、留言数、阅读数
//        StatisticUserFootDTO userFootStatisticDTO =  userFootService.getFootCount();
//        if (userFootStatisticDTO == null) {
//            userFootStatisticDTO = new StatisticUserFootDTO();
//        }
//        return StatisticsCountDTO.builder()
//                .userCount(userInfoService.getCount())
//                .articleCount(articleReadService.getCountByAuthorId(null))
//                .pvCount(requestCountService.getPvTotalCount())
//                .tutorialCount(columnService.getTutorialCount())
//                .commentCount(userFootStatisticDTO.getCommentCount())
//                .collectCount(userFootStatisticDTO.getCollectionCount())
//                .likeCount(userFootStatisticDTO.getPraiseCount())
//                .readCount(userFootStatisticDTO.getReadCount())
////                .starPayCount(aiConfig.getMaxNum().getStarNumber())
//                .build();
//    }
//
//    @Override
//    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
//        return requestCountService.getPvUvDayList(day);
//    }
//
//}
