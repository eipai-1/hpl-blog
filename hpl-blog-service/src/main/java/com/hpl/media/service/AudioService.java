package com.hpl.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.media.pojo.dto.ImagePostDTO;
import com.hpl.media.pojo.dto.SearchAudioDTO;
import com.hpl.media.pojo.entity.Audio;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/30 13:35
 */
public interface AudioService extends IService<Audio> {
    void uploadImage(ImagePostDTO imagePostDTO, byte[] bytes, String imageName);

    List<Audio> listAudios(SearchAudioDTO searchAudioDTO);

    void renameById(String id,String newName);

    void deleteById(String id);
}
