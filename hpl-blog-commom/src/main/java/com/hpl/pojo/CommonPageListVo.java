package com.hpl.pojo;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : rbe
 * @date : 2024/7/5 9:29
 */
@Data
public class CommonPageListVo<T> {

    /**
     * 用户列表
     */
    List<T> list;

    /**
     * 是否有更多
     */
    private Boolean hasMore;

    public static <T> CommonPageListVo<T> emptyVo() {
        CommonPageListVo<T> vo = new CommonPageListVo<>();
        vo.setList(Collections.emptyList());
        vo.setHasMore(false);
        return vo;
    }

    public static <T> CommonPageListVo<T> newVo(List<T> list, long pageSize) {
        CommonPageListVo<T> vo = new CommonPageListVo<>();
        vo.setList(Optional.ofNullable(list).orElse(Collections.emptyList()));
        vo.setHasMore(vo.getList().size() == pageSize);
        return vo;
    }
}

