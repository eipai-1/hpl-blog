package com.hpl.user.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/2 8:30
 */
@Data
@Accessors(chain = true)
public class RegisterPwdDto implements Serializable {

    private static final long serialVersionUID = 2139742660700910738L;

//    /** 用户id */
//    private Long userId;

    /** 注册用户名 */
    private String username;

    /** 注册密码 */
    private String password;

    /** 二次密码 */
    private String twicePwd;

}
