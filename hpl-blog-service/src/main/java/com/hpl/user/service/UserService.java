package com.hpl.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.user.pojo.dto.RegisterPwdDTO;
import com.hpl.user.pojo.entity.User;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
public interface UserService extends IService<User>{

    /*************************
     * 登录模块业务
     *************************/
    public static final String SESSION_KEY = "f-session";
    public static final String USER_DEVICE_KEY = "f-device";

    /**
     * 用户名密码方式登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    String loginByUserPwd(String username, String password);

    /**
     * 登出
     *
     * @param session 用户会话
     */
    void logout(String session);

    /**
     * 注册登录
     *
     * @param registerPwdDto 注册信息
     * @return
     */
    String registerByUserPwd(RegisterPwdDTO registerPwdDto);



}
