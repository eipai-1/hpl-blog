package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.entity.Article;
import com.hpl.article.entity.ArticleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ArticleDetailMapper extends BaseMapper<ArticleDetail> {

    /**
     * 更新正文
     * fixme: 这里的版本迭代还没有管理起来；后续若存在审核中间态，则可以针对上线之后的文章，修改内容之后生成新的一条审核中的记录，版本 +1；而不是直接在原来的记录上进行版本+1
     *
     * @param articleId
     * @param content
     * @return
     */
    @Update("update article_detail set `content` = #{content}, `version` = `version` + 1 where article_id = #{articleId} and `deleted`=0 order by `version` desc limit 1")
    int updateContent(long articleId, String content);
}
