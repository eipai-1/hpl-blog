package com.hpl.user.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/8/13 9:46
 */
@Data
@Accessors(chain = true)
public class LoginPwdDTO implements Serializable {

    private static final long serialVersionUID = 2139742660700910738L;

    private String username;

    private String password;

}
