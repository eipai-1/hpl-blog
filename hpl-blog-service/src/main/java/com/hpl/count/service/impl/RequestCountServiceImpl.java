package com.hpl.count.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.count.mapper.RequestCountMapper;
import com.hpl.count.pojo.dto.StatisticsDayDTO;
import com.hpl.count.pojo.entity.RequestCount;
import com.hpl.count.service.RequestCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


/**
 * @author : rbe
 * @date : 2024/7/9 10:55
 */
@Slf4j
@Service
public class RequestCountServiceImpl extends ServiceImpl<RequestCountMapper, RequestCount> implements RequestCountService {

    @Autowired
    private RequestCountMapper requestCountMapper;

    /**
     * 根据主机名和当前日期获取请求计数。
     *
     * @param host 主机名，用于精确匹配请求计数中的主机字段。
     * @return 返回匹配主机名和当前日期的请求计数对象。
     *         如果不存在匹配的记录，则行为根据底层数据库查询框架的配置而定，
     *         可能返回null或抛出异常。
     */
    @Override
    public RequestCount getRequestCount(String host) {
        // 使用Lambda查询语法，查询请求计数表中主机名等于参数host且日期等于当前日期的记录
        return lambdaQuery()
                .eq(RequestCount::getHost, host)
                .eq(RequestCount::getDate, Date.valueOf(LocalDate.now()))
                .one();
    }

    @Override
    public void insert(String host) {
        RequestCount requestCountDO = null;
        try {
            requestCountDO = new RequestCount();
            requestCountDO.setHost(host);
            requestCountDO.setCnt(1);
            requestCountDO.setDate(Date.valueOf(LocalDate.now()));
            this.save(requestCountDO);
        } catch (Exception e) {
            // fixme 非数据库原因的异常，则大概率是0点的并发访问，导致同一天写入多条数据的问题； 可以考虑使用分布式锁来避免
            // todo 后续考虑使用redis自增来实现pv计数统计
            log.error("save requestCount error: {}", requestCountDO, e);
        }
    }

    @Override
    public void incrementCount(Long id) {
        RequestCount requestCount = this.getById(id);

        lambdaUpdate().set(RequestCount::getCnt, requestCount.getCnt()+1)
                .eq(RequestCount::getId, id)
                .update();
    }

    @Override
    public Long getPvTotalCount() {
        return requestCountMapper.getPvTotalCount();
    }

    @Override
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return requestCountMapper.getPvUvDayList(day);
    }

}
