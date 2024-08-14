package com.hpl.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.exception.StatusEnum;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.user.helper.UserPwdHelper;
import com.hpl.user.helper.UserSessionHelper;
import com.hpl.user.pojo.dto.RegisterPwdDTO;
import com.hpl.user.pojo.entity.User;
import com.hpl.user.mapper.UserMapper;
import com.hpl.user.service.UserInfoService;
import com.hpl.user.service.UserService;
import com.hpl.exception.ExceptionUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserInfoService userInfoService;

    @Resource
    UserPwdHelper userPwdHelper;

    @Resource
    UserSessionHelper userSessionHelper;


    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUserName, username)
                .eq(User::getDeleted, 0);

        return userMapper.selectOne(wrapper);
    }



    /**
     * 用户名密码方式登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public String loginByUserPwd(String username, String password) {

//        User user =getUserByUserName(username);
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(User::getUserName, username)
                .eq(User::getDeleted, CommonDeletedEnum.NO.getCode());
        User user=userMapper.selectOne(queryWrapper);


        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userName=" + username);
        }

        //密码加盐
        String salt=user.getSalt();
        password+=salt;

        log.warn(userPwdHelper.encodePwd(password));
        log.warn(user.getPassword());

        if (!userPwdHelper.match(password, user.getPassword())) {
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        Long userId = user.getId();

        // 登录成功，返回对应的session

        //todo 暂时这样处理 ，但只有reqinfo信息，应该写个拦截器，补全相关信息
        ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
        ReqInfoContext.addReqInfo(reqInfo);

        ReqInfoContext.getReqInfo().setUserId(userId);
        return userSessionHelper.genSession(userId);
    }

    /**
     * 退出登录
     * @param session 用户会话
     */
    @Override
    public void logout(String session) {
        userSessionHelper.removeSession(session);
    }

    /**
     * 用户名密码方式进行注册
     */
    @Override
    public String registerByUserPwd(RegisterPwdDTO registerPwdDto) {
        // 1. 前置校验
        registerPreCheck(registerPwdDto);


        // 2. 判断当前用户是否登录，若已经登录，则直接走绑定流程 (就是已经第三方登录的用户）

        //todo 因为这里是注册，也还没弄第三方登录，所以没登录，所以直接走id为空流程
//        Long userId = ReqInfoContext.getReqInfo().getUserId();

//        Long userId = null;
//
//        if (userId != null) {
//            // 已登录用户绑定新用户名和密码
//            // 2.1 如果用户已经登录，则走绑定用户信息流程
//            User user=userMapper.selectById(userId);
//            user.setUserName(registerPwdDto.getUsername());
//            user.setPassword(userPwdHelper.encodePwd(registerPwdDto.getPassword()));
//            user.setSalt(userPwdHelper.genSalt());
//            userMapper.updateById(user);
//
//            // 2.2 初始化用户详细信息
//            userInfoService.initUserInfo(user.getId());
//
//            return ReqInfoContext.getReqInfo().getSession();
//        }

        // 3. 新用户注册流程
        User user=new User();
        user.setUserName(registerPwdDto.getUsername());
        user.setPassword(userPwdHelper.encodePwd(registerPwdDto.getPassword()));
        user.setSalt(userPwdHelper.genSalt());
        user.setLoginType(1);
        userMapper.insert(user);

        // 初始化用户详细信息
        user=getByUsername(registerPwdDto.getUsername());
        userInfoService.initUserInfo(user.getId());

        // 设置用户ID和生成用户会话

        //todo 暂时这样跳过处理 ，应该写个拦截器，补全相关信息
//        ReqInfoContext.getReqInfo().setUserId(user.getId());

        return userSessionHelper.genSession(user.getId());
    }


    /**
     * 在注册前进行信息校验，确保用户名、密码及确认密码的合法性和一致性。
     * 此方法旨在防止无效或不安全的注册信息提交，保障系统用户数据的安全性和完整性。
     */
    private void registerPreCheck(RegisterPwdDTO registerPwdDto) {
        // 检查用户名、密码和确认密码是否为空，如果为空则抛出异常
        if (StringUtils.isBlank(registerPwdDto.getUsername()) || StringUtils.isBlank(registerPwdDto.getPassword())
                        || StringUtils.isBlank(registerPwdDto.getTwicePwd())) {
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        // 检查密码和确认密码是否一致，如果不一致则抛出异常
        if(ObjectUtils.notEqual(registerPwdDto.getPassword(), registerPwdDto.getTwicePwd())){
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        // 检查用户名是否已存在，如果已存在则抛出异常
        User user = getByUsername(registerPwdDto.getUsername());
        if (user != null) {
            throw ExceptionUtil.of(StatusEnum.USER_EXISTS, registerPwdDto.getUsername());
        }
    }


}
