package com.hpl.controller.media;


import com.hpl.media.pojo.dto.SearchVideoDTO;
import com.hpl.media.pojo.dto.VideoPostDTO;
import com.hpl.media.pojo.entity.Video;
import com.hpl.media.service.VideoService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/31 9:05
 */
@Tag(name="视频媒资管理接口")
@RestController
@Slf4j
@RequestMapping("/video")
public class VideoController {

    // 单个文件最大大小
    private final Long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    @Resource
    private VideoService videoService;

    @Operation(summary = "列表查看所有视频")
    @PostMapping("/videos")
    public CommonResult<List<Video>> listVideos(@RequestBody SearchVideoDTO searchVideoDTO){
        List<Video> list = videoService.listVideos(searchVideoDTO);
        return CommonResult.data(list);
    }


    @Operation(summary = "上传视频")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<?> upload(@RequestPart("filedata") MultipartFile upload,
                                  @RequestParam(value = "remark", required = false) String remark) throws IOException {

        String fileMD5 = DigestUtils.md5DigestAsHex(upload.getBytes());
        // 1、先查一遍，如果有则不重复添加
        Video video = videoService.getById(fileMD5);
        if (video != null) {
            log.warn("该视频已存在，请勿重复添加");
            return CommonResult.error("该视频已存在，请勿重复添加");
        }



        String contentType = upload.getContentType();
        log.warn("contentType:{}",contentType);
        if (contentType == null || !contentType.contains("video")) {
            log.error("上传文件类型不符合要求，请上传视频类型");
            return CommonResult.error("上传文件类型不符合要求，请上传视频类型");
        }

        VideoPostDTO videoPostDTO = new VideoPostDTO();
        videoPostDTO.setFileSize(upload.getSize());
        videoPostDTO.setRemark(remark);
        videoPostDTO.setFileName(upload.getOriginalFilename());
        videoPostDTO.setContentType(contentType);

        if (contentType.contains("avi")){
            log.warn("avi类型，做转码处理");
            //todo
        }

        if(upload.getSize()>MAX_FILE_SIZE){
            log.warn("上传大小超过50mb，转至分块处理");

            //todo 视频分块上传

        }else{
            try {
                videoService.uploadVideo(videoPostDTO, upload.getBytes());
                return CommonResult.success();
            } catch (IOException e) {
                // todo xxx.of
//            XueChengPlusException.cast("上传文件过程出错:" + e.getMessage());
                log.error("上传图片过程出错:{}",e.getMessage());
            }
        }


        return CommonResult.error("上传失败");
    }



    //删除图片
    @Operation(summary = "删除视频")
    @DeleteMapping("/{id}")
    public CommonResult<?> deleteVideo(@PathVariable String id) {
        videoService.deleteById(id);
        return CommonResult.success();
    }

    //图片重命名
    @Operation(summary = "视频重命名")
    @PutMapping("/rename/{id}")
    public CommonResult<?> renameVideo(@PathVariable String id, @RequestParam String newName) {
        videoService.renameById(id,newName);
        return CommonResult.success();
    }
}
