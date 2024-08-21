package com.hpl.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.user.pojo.dto.AuthorDTO;
import com.hpl.user.pojo.dto.AuthorDetailDTO;
import com.hpl.user.pojo.entity.UserInfo;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 初始化用户信息
     * @param userId
     */
    void initUserInfo(Long userId);
    
    

    /**
     * 根据session获取用户信息
     *
     * @param session
     * @return
     */
    UserInfo getUserInfoBySessionId(String session, String clientIp);

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    UserInfo getByUserId(Long userId);


    /**
     * 获取用户总数
     * @return
     */
    Long getCount();


    AuthorDTO getAuthorByArticleId(Long articleId);

    AuthorDetailDTO getAuthorDetailById(Long userId);
}
