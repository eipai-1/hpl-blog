package com.hpl.exception;

/**
 * @author : rbe
 * @date : 2024/7/1 9:55
 */
public class ExceptionUtil {

    public static CommonException of(StatusEnum status, Object... args) {
        throw new CommonException(status, args);
    }

}