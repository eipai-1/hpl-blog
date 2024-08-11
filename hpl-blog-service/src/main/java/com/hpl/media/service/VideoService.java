package com.hpl.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.media.pojo.dto.SearchVideoDTO;
import com.hpl.media.pojo.dto.VideoPostDTO;
import com.hpl.media.pojo.entity.Video;
import com.hpl.pojo.CommonPageParam;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/31 9:01
 */
public interface VideoService extends IService<Video> {
    void uploadVideo(VideoPostDTO videoPostDTO, byte[] bytes);

    List<Video> listVideos(String searchName, CommonPageParam pageParam);

    void renameById(String id,String newName);

    void deleteById(String id);

    void editRemarkById(String id, String newRemark);
}
