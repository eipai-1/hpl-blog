package com.hpl.notify.pojo.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/2 15:27
 */
@Getter
public enum NotifyStateEnum {

    UNREAD(0, "未读"),

    READ(1, "已读");


    private int state;
    private String msg;

    NotifyStateEnum(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }
}
