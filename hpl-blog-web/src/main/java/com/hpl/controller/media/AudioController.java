package com.hpl.controller.media;

import com.hpl.media.pojo.dto.ImagePostDTO;
import com.hpl.media.pojo.dto.SearchAudioDTO;
import com.hpl.media.pojo.entity.Audio;
import com.hpl.media.service.AudioService;
import com.hpl.pojo.CommonController;
import com.hpl.pojo.CommonPageParam;
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
 * @date : 2024/7/30 13:37
 */
@Tag(name="音频媒资管理接口")
@RestController
@Slf4j
@RequestMapping("/audio")
public class AudioController extends CommonController {

    // 单个文件最大大小
    private final Long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    @Resource
    private AudioService audioService;

    @Operation(summary = "列表查看所有音频")
    @PostMapping("/audios")
    public CommonResult<List<Audio>> listImages(@RequestBody SearchAudioDTO searchAudioDTO){
        log.warn("searchImageDTO:{}",searchAudioDTO);

        // 校验页码和每页大小
        CommonPageParam pageParam =this.buildPageParam(searchAudioDTO.getPageNum(), searchAudioDTO.getPageSize());



        List<Audio> list = audioService.listAudios(searchAudioDTO.getAudioName(), pageParam);
        return CommonResult.data(list);
    }


    @Operation(summary = "上传音频")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<?> upload(@RequestPart("filedata") MultipartFile upload,
                                  @RequestParam(value = "imageName", required = false) String imageName,
                                  @RequestParam(value = "remark", required = false) String remark) throws IOException {

        String fileMD5 = DigestUtils.md5DigestAsHex(upload.getBytes());
        // 1、先查一遍，如果有则不重复添加
        Audio audio = audioService.getById(fileMD5);
        if (audio != null) {
            return CommonResult.error("该音频已存在，请勿重复添加");
        }


        String contentType = upload.getContentType();
        log.warn("contentType:{}",contentType);
        if (contentType == null || !contentType.contains("audio")) {
            log.error("上传文件类型不符合要求，请上传音频类型");
            return CommonResult.error("上传文件类型不符合要求，请上传音频类型");
        }

        ImagePostDTO imagePostDTO = new ImagePostDTO();
        imagePostDTO.setFileSize(upload.getSize());
        imagePostDTO.setRemark(remark);
        imagePostDTO.setFileName(upload.getOriginalFilename());
        imagePostDTO.setContentType(contentType);

        if(upload.getSize()>MAX_FILE_SIZE){
            log.warn("上传大小超过50mb，转至分块处理");

            //todo 音频分块上传

        }else{
            try {
                audioService.uploadImage(imagePostDTO, upload.getBytes(),imageName);
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
    @Operation(summary = "删除音频")
    @DeleteMapping("/{id}")
    public CommonResult<?> deleteImage(@PathVariable String id) {
        audioService.deleteById(id);
        return CommonResult.success();
    }

    //图片重命名
    @Operation(summary = "音频重命名")
    @PutMapping("/rename/{id}")
    public CommonResult<?> renameImage(@PathVariable String id, @RequestParam String newName) {
        audioService.renameById(id,newName);
        return CommonResult.success();
    }

    @Operation(summary = "编辑备注")
    @PutMapping("/remark/{id}")
    public CommonResult<?> editRemark(@PathVariable String id, @RequestParam String newRemark) {
        audioService.editRemarkById(id,newRemark);
        return CommonResult.success();
    }
}
