package com.hpl.media.service;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/8/6 8:41
 */
public interface ImageMdService {
    /**
     * 图片转存
     * @param content
     * @return
     */
    String mdImgReplace(String content);


    /**
     * 外网图片转存
     *
     * @param img
     * @return
     */
    String saveImg(String img);

    /**
     * 保存图片
     *
     * @param request
     * @return
     */
    String saveImg(HttpServletRequest request) throws Exception;
}
