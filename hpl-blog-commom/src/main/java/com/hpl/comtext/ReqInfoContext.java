package com.hpl.comtext;

import lombok.Data;

/**
 * 请求上下文，携带用户身份相关信息
 *
 * @author : rbe
 * @date : 2024/6/29 17:23
 */
public class ReqInfoContext {
    private static ThreadLocal<ReqInfo> contexts = new InheritableThreadLocal<>();

    public static void addReqInfo(ReqInfo reqInfo) {
        contexts.set(reqInfo);
    }

    public static void clear() {
        contexts.remove();
    }

    public static ReqInfo getReqInfo() {
        return contexts.get();
    }

    @Data
    public static class ReqInfo {
        private Long userId;

//        private User userInfo;
    }
}
