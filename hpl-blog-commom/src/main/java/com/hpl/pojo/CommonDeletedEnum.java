package com.hpl.pojo;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/5 9:43
 */
@Getter
public enum CommonDeletedEnum {

    NO(0, "N","否", "no"),
    YES(1,"Y" ,"是", "yes");

    CommonDeletedEnum(int code, String desc, String cnDesc, String enDesc) {
        this.code = code;
        this.cnDesc = cnDesc;
        this.enDesc = enDesc;
        this.desc = desc;
    }

    private final int code;
    private final String desc;
    private final String cnDesc;
    private final String enDesc;

    public static CommonDeletedEnum formCode(int code) {
        for (CommonDeletedEnum commonDeletedEnum : CommonDeletedEnum.values()) {
            if (commonDeletedEnum.getCode() == code) {
                return commonDeletedEnum;
            }
        }
        return CommonDeletedEnum.NO;
    }

    /**
     * 是否为是或否，主要用于某些场景字段未赋值的情况
     *
     * @return
     */
    public static boolean equalYN(Integer code) {
        if (code == null) {
            return false;
        }
        if (code != null && (code.equals(YES.code) || code.equals(NO.code))) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是yes
     *
     * @param code
     * @return
     */
    public static boolean isYes(Integer code) {
        if (code == null) {
            return false;
        }
        return CommonDeletedEnum.YES.getCode() == code;
    }

}
