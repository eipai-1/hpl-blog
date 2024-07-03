package com.hpl.exception;

import com.hpl.enums.StatusEnum;
import com.hpl.pojo.CommomStatus;
import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/1 9:38
 */
public class CommomException extends RuntimeException{

    @Getter
    private CommomStatus status;

    public CommomException(CommomStatus status) {
        this.status = status;
    }

    public CommomException(int code, String msg) {
        this.status = CommomStatus.newStatus(code,msg);
    }

    public CommomException(StatusEnum statusEnum, Object... args) {
        this.status = CommomStatus.newStatus(statusEnum, args);
    }
}
