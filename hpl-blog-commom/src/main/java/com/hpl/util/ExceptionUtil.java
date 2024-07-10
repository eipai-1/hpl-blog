package com.hpl.util;

import com.hpl.enums.StatusEnum;
import com.hpl.exception.CommonException;

/**
 * @author : rbe
 * @date : 2024/7/1 9:55
 */
public class ExceptionUtil {

    public static CommonException of(StatusEnum status, Object... args) {
        return new CommonException(status, args);
    }

}