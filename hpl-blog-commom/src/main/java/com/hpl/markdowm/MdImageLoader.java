package com.hpl.markdowm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * markdown文本中的图片识别
 *
 * @author : rbe
 * @date : 2024/8/6 9:55
 */
public class MdImageLoader {
    private static Pattern IMG_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MdImage {
        /**
         * 原始文本
         */
        private String origin;
        /**
         * 图片描述
         */
        private String desc;
        /**
         * 图片地址
         */
        private String url;
    }

    public static List<MdImage> loadImages(String content) {
        Matcher matcher = IMG_PATTERN.matcher(content);
        List<MdImage> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(new MdImage(matcher.group(0), matcher.group(1), matcher.group(2)));
        }
        return list;
    }
}
