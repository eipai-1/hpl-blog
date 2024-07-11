package com.hpl.pojo;

import com.hpl.enums.StatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/10 9:18
 */
@Data
public class CommonResVo<T> implements Serializable {

    private static final long serialVersionUID = -510306209659393854L;

//    @ApiModelProperty(value = "返回结果说明", required = true)
    private CommonStatus status;

//    @ApiModelProperty(value = "返回的实体结果", required = true)
    private T result;


    public CommonResVo() {
    }

    public CommonResVo(CommonStatus status) {
        this.status = status;
    }

    public CommonResVo(T t) {
        status = CommonStatus.newStatus(StatusEnum.SUCCESS);
        this.result = t;
    }

    public static <T> CommonResVo<T> success(T t) {
        return new CommonResVo<T>(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> CommonResVo<T> fail(StatusEnum status, Object... args) {
        return new CommonResVo<>(CommonStatus.newStatus(status, args));
    }

    public static <T> CommonResVo<T> fail(CommonStatus status) {
        return new CommonResVo<>(status);
    }
}

