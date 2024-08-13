package com.hpl.controller.user;

import com.hpl.pojo.CommonResult;
import com.hpl.user.pojo.dto.LoginPwdDTO;
import com.hpl.user.pojo.dto.RegisterPwdDTO;
import com.hpl.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : rbe
 * @date : 2024/7/26 9:16
 */
@RestController
@RequestMapping("/user")
@Tag(name = "登录相关接口")
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public CommonResult<?> loginByUserPwd(@RequestBody LoginPwdDTO loginPwdDTO) {
        log.warn(String.valueOf(loginPwdDTO));
        String session = userService.loginByUserPwd(loginPwdDTO.getUsername(), loginPwdDTO.getPassword());
        return CommonResult.data(session);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResult<?> registerByUserPwd(@RequestBody @Valid RegisterPwdDTO registerPwdDto) {
        String session = userService.registerByUserPwd(registerPwdDto);
        return CommonResult.data(session);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public CommonResult<?> logout(String session) {
        userService.logout(session);
        return CommonResult.success();
    }
}
