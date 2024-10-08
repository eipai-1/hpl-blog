package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.dto1.ArticleOtherDTO;
import com.hpl.user.pojo.entity.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 10:53
 */
@Data
public class ArticleDetailVo {

    /** 文章信息 */
    private ArticleDTO article;

//    /** 评论信息 */
//    private List<TopCommentDTO> comments;
//
//    /** 热门评论 */
//    private TopCommentDTO hotComment;

    /** 作者相关信息 */
    private UserInfo author;

    // 其他的信息，比如说翻页，比如说阅读类型
    private ArticleOtherDTO other;


}
