package com.hpl.statistic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.statistic.pojo.dto.ArticleFootCountDTO;
import com.hpl.statistic.pojo.dto.CountAllDTO;
import com.hpl.statistic.pojo.entity.ReadCount;
import com.hpl.statistic.pojo.entity.TraceCount;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 18:29
 */
public interface TraceCountService extends IService<TraceCount> {


    CountAllDTO getAllCountByArticleId(Long userId, Long articleId);
}