package com.hpl.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.media.pojo.dto.ImagePostDTO;
import com.hpl.media.pojo.entity.Image;
import com.hpl.pojo.CommonPageParam;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/28 18:20
 */
public interface ImageService extends IService<Image> {

    void uploadImage(ImagePostDTO imagePostDTO, byte[] bytes, String imageName);

    List<Image> listImages(String searchName, CommonPageParam pageParam);

    List<Image> listHiddenImages(String searchName, CommonPageParam pageParam);

    void renameById(String id,String newName);

    void hideImage(String id);

    void showImage(String id);

    void editRemarkById(String id, String newRemark);
}
