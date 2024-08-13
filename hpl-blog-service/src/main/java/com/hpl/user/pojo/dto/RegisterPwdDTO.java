package com.hpl.user.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/2 8:30
 */
@Data
@Accessors(chain = true)
public class RegisterPwdDTO implements Serializable {

    private static final long serialVersionUID = 2139742660700910738L;

//    /** 用户id */
//    private Long userId;

    @Schema(description = "注册用户名",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "注册用户名不能为空")
    private String username;

    /** 注册密码 */
    @Schema(description = "注册密码",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "注册密码不能为空")
    private String password;

    /** 二次密码 */
    @Schema(description = "二次密码",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "二次密码不能为空")
    private String twicePwd;

}
