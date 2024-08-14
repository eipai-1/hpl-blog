package com.hpl.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : rbe
 * @date : 2024/7/1 9:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonStatus {

    /** 业务状态码 */
    private int code;

    /** 描述信息 */
    private String msg;

    public static CommonStatus newStatus(int code, String msg) {
        return new CommonStatus(code, msg);
    }

    public static CommonStatus newStatus(StatusEnum status, Object... msgs) {
        String msg;
        if (msgs.length > 0) {
            msg = String.format(status.getMsg(), msgs);
        } else {
            msg = status.getMsg();
        }
        return newStatus(status.getCode(), msg);
    }
}
