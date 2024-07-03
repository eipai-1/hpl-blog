package com.hpl.util;

/**
 * @author : rbe
 * @date : 2024/6/30 12:32
 */
public class NumUtil {

    public static boolean eqZero(Long num){
        return num == null || num == 0L;
    }

    public static boolean eqZero(Integer num){
        return num == null || num == 0;
    }

    public static boolean gtZero(Long num){
        return num != null && num > 0L;
    }

    public static boolean gtZero(Integer num){
        return num != null && num > 0;
    }


}
