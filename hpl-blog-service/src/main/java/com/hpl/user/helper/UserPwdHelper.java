package com.hpl.user.helper;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Component;



import java.util.Objects;
import java.util.Random;

/**
 * 密码加密器，后续接入SpringSecurity之后，可以使用 PasswordEncoder 进行替换
 *
 * @author YiHui
 * @date 2022/12/5
 */
@Component
public class UserPwdHelper {

    private final Random RANDOM = new Random();

    public boolean match(String plainPwd, String encPwd) {
        return BCrypt.checkpw(plainPwd, encPwd);
    }

    /**
     * 使用Bcrypt对明文密码进行加密处理。
     * Bcrypt是一种安全的密码哈希函数，能够抵抗彩虹表攻击，每次加密都会产生略有差异的密文，
     * 即使对于相同的明文密码。这提高了密码存储的安全级别，是当前推荐的密码处理方式之一。
     *
     * @param plainPwd 明文密码，需要进行加密处理。
     * @return 返回Bcrypt加密后的密码字符串。
     */
    public String encodePwd(String plainPwd) {
        // 提供了强大的Bcrypt加密功能，这里使用默认强度进行加密
        System.out.println(plainPwd);
        return BCrypt.hashpw(plainPwd);
    }

    public String genSalt(){
        // 从BCrypt生成的随机盐值中获取一个子字符串作为最终的盐值
        return BCrypt.gensalt().substring(8,9 + RANDOM.nextInt(8));
    }


}
