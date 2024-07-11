package com.hpl.global.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : rbe
 * @date : 2024/6/30 10:17
 */

/**
 * SEO标签值对象类
 * 用于封装SEO标签的键和值，提供数据传输和操作的便利。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeoTagVo {

    /**
     * SEO标签的键
     * 代表一个特定的SEO属性，例如页面标题、描述等。
     */
    private String key;

    /**
     * SEO标签的值
     * 与键对应的具体内容，用于展示或配置在页面上。
     */
//    private String value;
    private String val;
}

