package com.hpl.image.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : rbe
 * @date : 2024/7/6 9:44
 */
public interface ImageService {
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
