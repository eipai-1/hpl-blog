package com.hpl.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.notify.pojo.dtos.NotifyMsgDTO;
import com.hpl.notify.pojo.entity.NotifyMsg;
import com.hpl.pojo.CommonPageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/1 8:53
 */
@Mapper
public interface NotifyMsgMapper extends BaseMapper<NotifyMsg> {
    /**
     * 查询文章相关的通知列表
     *
     * @param userId
     * @param type
     * @param pageParam   分页
     * @return
     */
    List<NotifyMsgDTO> listArticleRelatedNotices(@Param("userId") long userId, @Param("type") int type, @Param("pageParam") CommonPageParam pageParam);

    List<NotifyMsgDTO> listNormalNotices(Long userId, int type, CommonPageParam pageParam);

    void updateNoticeRead(List<Long> ids);
}
