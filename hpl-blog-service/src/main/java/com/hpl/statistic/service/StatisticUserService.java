package com.hpl.statistic.service;

/**
 * @author : rbe
 * @date : 2024/7/1 9:01
 */
public interface StatisticUserService {

    /**
     * 添加在线人数
     *
     * @param add 正数，表示添加在线人数；负数，表示减少在线人数
     * @return
     */
    int incrOnlineUserCnt(int add);


    /**
     * 查询在线用户人数
     *
     * @return
     */
    int getOnlineUserCnt();
}
