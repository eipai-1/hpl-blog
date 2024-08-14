package com.hpl.user.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.service.ArticleService;
import com.hpl.exception.StatusEnum;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.user.helper.UserRandomGenHelper;
import com.hpl.user.helper.UserSessionHelper;
import com.hpl.user.pojo.dto.AuthorDTO;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.mapper.UserInfoMapper;
import com.hpl.user.service.UserInfoService;
import com.hpl.exception.ExceptionUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Resource
    UserRandomGenHelper userRandomGenHelper;

    @Resource
    UserSessionHelper userSessionHelper;

    /**
     * 初始化用户信息。
     * 该方法用于根据用户ID生成并插入一个新的用户信息记录到数据库中，包括用户ID、昵称和头像。
     * 昵称和头像是通过随机生成的方式获得。
     */
    @Override
    public void initUserInfo(Long userId){
        // 创建一个新的用户信息对象
        UserInfo userInfo=new UserInfo();
        // 设置用户的唯一标识ID
        userInfo.setUserId(userId);
        // 通过随机生成工具设置用户的昵称
        userInfo.setNickName(userRandomGenHelper.genNickName());
        // 通过随机生成工具设置用户的头像
        userInfo.setPhoto(userRandomGenHelper.genAvatar());
        // TODO: 这里需要考虑是否需要根据用户IP来设置或记录相关信息
        // 插入新的用户信息到数据库
        userInfoMapper.insert(userInfo);
    }


    /**
     * 根据session获取用户信息
     *
     * @param session
     * @return
     */
    @Override
    public UserInfo getUserInfoBySessionId(String session, String clientIp){
        // 检查会话ID是否为空
        if (StringUtils.isBlank(session)) {
            return null;
        }

        // 通过会话ID获取用户ID
        Long userId = userSessionHelper.getUserIdBySession(session);
        // 如果用户ID为空，则返回null
        if (userId == null) {
            return null;
        }

        // 1.根据用户ID查询用户信息
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId)
                .eq(UserInfo::getDeleted,0);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        // 如果用户信息不存在，则抛出异常
        if (userInfo == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }

//        // 2.并更新最后一次使用的ip
//
//        // 获取用户的IP信息
//        IpInfo ip = userInfo.getIp();
//        // 如果客户端IP不为空且与最新的IP不同，则更新最新的IP和地域信息
//        if (clientIp != null && !Objects.equals(ip.getLatestIp(), clientIp)) {
//            // 更新最新的IP和地域信息
//            ip.setLatestIp(clientIp);
//            ip.setLatestRegion(IpUtil.getLocationByIp(clientIp).toRegionStr());
//
//            // 如果首次IP为空，则设置为当前最新的IP，并更新首次地域信息
//            if (ip.getFirstIp() == null) {
//                ip.setFirstIp(clientIp);
//                ip.setFirstRegion(ip.getLatestRegion());
//            }
//
//            // 更新用户信息
//            userInfo.setIp(ip);
//            userInfoMapper.updateById(userInfo);
//        }


        // 将用户信息返回
        return userInfo;
    }


    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfo getByUserId(Long userId){

        LambdaQueryWrapper<UserInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserInfo::getUserId, userId)
                .eq(UserInfo::getDeleted, CommonDeletedEnum.NO.getCode());
        UserInfo userInfo =  userInfoMapper.selectOne(wrapper);

        if (userInfo == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }

        return userInfo;
    }


    /**
     * 获取用户总数
     * @return
     */
    @Override
    public Long getCount(){
        return lambdaQuery()
                .eq(UserInfo::getDeleted, CommonDeletedEnum.NO.getCode())
                .count();
    }


    @Override
    public AuthorDTO getAuthorByArticleId(Long articleId){

        // 先根据文章id查询作者id
        Long authorId = SpringUtil.getBean(ArticleService.class).getAuthorIdById(articleId);

        // 根据作者id查询作者信息
        UserInfo userInfo = getByUserId(authorId);

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setUserId(userInfo.getUserId());
        authorDTO.setNickName(userInfo.getNickName());
        authorDTO.setAvatar(userInfo.getPhoto());
        authorDTO.setProfile(userInfo.getProfile());
        authorDTO.setCreateTime(userInfo.getCreateTime());

        // 后面再看看还有什么要加的


        return authorDTO;


    }



}
