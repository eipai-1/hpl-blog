package com.hpl.exception;

/**
 * @author : rbe
 * @date : 2024/7/1 9:46
 */

import lombok.Getter;

/**
 * 异常码规范：
 * xxx - xxx - xxx
 * 业务 - 状态 - code
 * <p>
 * 业务取值
 * - 100 全局
 * - 200 文章相关
 * - 300 评论相关
 * - 400 用户相关
 * <p>
 * 状态：基于http status的含义
 * - 4xx 调用方使用姿势问题
 * - 5xx 服务内部问题
 * <p>
 * code: 具体的业务code
 *
 * @author YiHui
 * @date 2022/7/27
 */
@Getter
public enum StatusEnum {

    // 成功
    //todo 这个好像永远用不上，成功了根本不走这层封装，
    SUCCESS(0, "OK"),

    /**************************************************
     * 通用相关异常类型，前缀为100_
     **************************************************/

    // 全局传参异常
    ILLEGAL_ARGUMENTS(100_400_001, "参数异常"),
    ILLEGAL_ARGUMENTS_MIXED(100_400_002, "参数异常:%s"),

    // 全局权限相关
    FORBID_ERROR(100_403_001, "无权限"),
    FORBID_ERROR_MIXED(100_403_002, "无权限:%s"),
    FORBID_NOT_LOGIN(100_403_003, "未登录"),
    FORBID_ERROR_LOGIN(100_403_004, "登录失效，请重新登录"),

    // 全局，数据不存在
    RECORDS_NOT_EXISTS(100_404_001, "记录不存在:%s"),

    // 系统异常
    UNEXPECT_ERROR(100_500_001, "非预期异常:%s"),
    // 图片相关异常类型
    UPLOAD_PIC_FAILED(100_500_002, "图片上传失败！"),





    /**************************************************
     * 文章相关异常类型，前缀为200_
     **************************************************/

    // 教程文章不存在
    ARTICLE_NOT_EXISTS(200_404_001, "文章不存在:%s"),
    COLUMN_NOT_EXISTS(200_404_002, "教程不存在:%s"),
    COLUMN_QUERY_ERROR(200_500_003, "教程查询异常:%s"),
    // 教程文章已存在
    COLUMN_ARTICLE_EXISTS(200_500_004, "专栏教程已存在:%s"),
    ARTICLE_RELATION_TUTORIAL(200_500_006, "文章已被添加为教程:%s"),




    /**************************************************
     * 评论相关异常类型，前缀为300_
     **************************************************/

    // 评论相关异常类型
    COMMENT_NOT_EXISTS(300_404_001, "评论不存在:%s"),




    /**************************************************
     * 用户相关异常类型，前缀为400_
     **************************************************/

    // 用户登录失败
    LOGIN_FAILED_MIXED(400_403_001, "登录失败:%s"),
    // 用户不存在或已存在
    USER_NOT_EXISTS(400_404_001, "用户不存在:%s"),
    USER_EXISTS(400_404_002, "用户已存在:%s"),
    // 用户登录名重复
    USER_LOGIN_NAME_REPEAT(400_404_003, "用户登录名重复:%s"),
    // 星球编号不存在
    USER_STAR_NOT_EXISTS(400_404_002, "星球编号不存在:%s"),
    // 星球编号重复
    USER_STAR_REPEAT(400_404_002, "星球编号重复:%s"),
    // 待审核
    USER_NOT_AUDIT(400_500_001, "用户未审核:%s"),
    USER_PWD_ERROR(400_500_002, "用户名or密码错误");



    private int code;

    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static boolean is5xx(int code) {
        return code % 1000_000 / 1000 >= 500;
    }

    public static boolean is403(int code) {
        return code % 1000_000 / 1000 == 403;
    }

    public static boolean is4xx(int code) {
        return code % 1000_000 / 1000 < 500;
    }
}
