package com.hpl.controller.media;


import com.hpl.media.pojo.dto.ImagePostDTO;
import com.hpl.media.pojo.dto.SearchImageDTO;
import com.hpl.media.pojo.entity.Image;
import com.hpl.media.service.ImageService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/28 18:23
 */
@Tag(name="图片媒资管理接口")
@RestController
@Slf4j
@RequestMapping("/image")
public class ImageController {

    @Resource
    private ImageService imageService;

    @Operation(summary = "列表查看所有图片")
    @PostMapping("/images")
    public CommonResult<List<Image>> listImages(@RequestBody SearchImageDTO searchImageDTO) {
        List<Image> list = imageService.listImages(searchImageDTO);
        return CommonResult.data(list);
    }


    //查看所有隐藏图片
    @Operation(summary = "查看所有隐藏图片")
    @PostMapping("/hidden")
    public CommonResult<List<Image>> listHiddenImages() {
        List<Image> list = imageService.listHiddenImages();
        return CommonResult.data(list);
    }



    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<?> upload(@RequestPart("filedata") MultipartFile upload,
                                      @RequestParam(value = "imageName", required = false) String imageName,
                                      @RequestParam(value = "remark", required = false) String remark) {

        ImagePostDTO imagePostDTO = new ImagePostDTO();
        imagePostDTO.setFileSize(upload.getSize());
        String contentType = upload.getContentType();
        log.warn("contentType:{}",contentType);
        if (contentType == null || !contentType.contains("image")) {
            log.error("上传文件类型不符合要求，请上传图片类型");
            return CommonResult.error("上传文件类型不符合要求，请上传图片类型");
        }
        imagePostDTO.setRemark(remark);
        imagePostDTO.setFileName(upload.getOriginalFilename());
        imagePostDTO.setContentType(contentType);


        try {
            imageService.uploadImage(imagePostDTO, upload.getBytes(),imageName);
            return CommonResult.success();
        } catch (IOException e) {
            // todo xxx.of
//            XueChengPlusException.cast("上传文件过程出错:" + e.getMessage());
            log.error("上传图片过程出错:{}",e.getMessage());
        }
        return CommonResult.error("上传失败");
    }

    @Operation(summary = "隐藏图片")
    @PutMapping("/hide/{id}")
    public CommonResult<?> hideImage(@PathVariable String id) {
        imageService.hideImage(id);
        return CommonResult.success();
    }

    @Operation(summary = "正常显示图片")
    @PutMapping("/show/{id}")
    public CommonResult<?> showImage(@PathVariable String id) {
        imageService.showImage(id);
        return CommonResult.success();
    }

    //删除图片
    @Operation(summary = "删除图片")
    @DeleteMapping("/{id}")
    public CommonResult<?> deleteImage(@PathVariable String id) {
        imageService.removeById(id);
        return CommonResult.success();
    }

    //图片重命名
    @Operation(summary = "图片重命名")
    @PutMapping("/rename/{id}")
    public CommonResult<?> renameImage(@PathVariable String id, @RequestParam String newName) {
        imageService.renameById(id,newName);
        return CommonResult.success();
    }
}
