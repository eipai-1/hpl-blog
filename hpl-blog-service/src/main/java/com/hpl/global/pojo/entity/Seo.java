package com.hpl.global.pojo.entity;

import com.hpl.global.pojo.vo.SeoTagVo;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author : rbe
 * @date : 2024/6/30 17:02
 */
@Data
@Builder
public class Seo {
    private List<SeoTagVo> ogp;
    private Map<String, Object> jsonLd;
}