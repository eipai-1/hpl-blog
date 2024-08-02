package com.hpl.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.dto1.SimpleUserInfoDTO;
import com.hpl.statistic.pojo.dto.ArticleFootCountDTO;
import com.hpl.statistic.pojo.dto.StatisticUserFootDTO;
import com.hpl.user.pojo.entity.UserFoot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/6/29 19:24
 */
@Mapper
public interface UserFootMapper extends BaseMapper<UserFoot> {


    /**
     * 查询文章的点赞列表
     *
     * @param documentId
     * @param type
     * @param size
     * @return
     */
    List<SimpleUserInfoDTO> listSimpleUserInfosByArticleId(@Param("documentId") Long documentId,
                                                           @Param("type") Integer type,
                                                           @Param("size") int size);


    StatisticUserFootDTO getFootCount();


    /**
     * 查询文章计数信息
     *
     * @param articleId
     * @return
     */
    ArticleFootCountDTO countArticleByArticleId(Long articleId);

    ArticleFootCountDTO countArticleByUserId(Long userId);
}
