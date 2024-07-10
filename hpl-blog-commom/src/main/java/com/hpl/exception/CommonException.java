package com.hpl.exception;

import com.hpl.enums.StatusEnum;
import com.hpl.pojo.CommonStatus;
import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/1 9:38
 */
public class CommonException extends RuntimeException{

    @Getter
    private CommonStatus status;

    public CommonException(CommonStatus status) {
        this.status = status;
    }

    public CommonException(int code, String msg) {
        this.status = CommonStatus.newStatus(code,msg);
    }

    public CommonException(StatusEnum statusEnum, Object... args) {
        this.status = CommonStatus.newStatus(statusEnum, args);
    }
}
