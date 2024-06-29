package com.hpl.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/6/29 18:46
 */
@Data
public class IpInfo implements Serializable {
    private static final long serialVersionUID = -4612222921661930429L;

    /** 第一次登录ip */
    private String firstIp;

    /** 第一次登录地区 */
    private String firstRegion;

    /** 最后一次登录ip */
    private String latestIp;

    /** 最后一次登录地区 */
    private String latestRegion;
}
