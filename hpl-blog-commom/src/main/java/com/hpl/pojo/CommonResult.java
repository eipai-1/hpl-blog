package com.hpl.pojo;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/6/29 16:08
 */
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CORE_SUCCESS = 200;

    public static final int CORE_ERROR = 500;

    /** 状态码 */
    private int code;

    /** 提示语 */
    private String msg;

    /** 返回数据 */
    private T data;

    public CommonResult(){}

    public CommonResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    // ============================  构建  ==================================

    // 构建成功
    public static <T> CommonResult<T> success(){
        return new CommonResult<>(CORE_SUCCESS,"操作成功",null);
    }

    public static <T> CommonResult<T> success(String msg){
        return new CommonResult<>(CORE_SUCCESS,msg,null);
    }

    public static <T> CommonResult<T> code(int code){
        return new CommonResult<>(code,null,null);
    }

    public static <T> CommonResult<T> data(T data){
        return new CommonResult<>(CORE_SUCCESS,"操作成功",data);
    }

    // 构建失败
    public static <T> CommonResult<T> error(){
        return new CommonResult<>(CORE_ERROR,"服务器异常",null);
    }

    public static <T> CommonResult<T> error(String msg){
        return new CommonResult<>(CORE_ERROR,msg,null);
    }

    // 构建指定状态码
    public static <T> CommonResult<T> code(int code, String msg, T data){
        return new CommonResult<>(code,msg,data);
    }


    @Override
    public String toString() {
        return "{"
                + "\"code\":" + this.code
                + ", \"msg\":\"" + this.msg + "\""
                + ", \"data\":" + this.data
                + "}";
    }
}
