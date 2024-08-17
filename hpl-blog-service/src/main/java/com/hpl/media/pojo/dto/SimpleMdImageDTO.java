package com.hpl.media.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/8/15 10:06
 */
@Data
public class SimpleMdImageDTO implements Serializable {

    private String url;

    private String desc;
}
