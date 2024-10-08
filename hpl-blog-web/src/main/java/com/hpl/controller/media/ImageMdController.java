package com.hpl.controller.media;

import com.hpl.exception.ExceptionUtil;
import com.hpl.exception.StatusEnum;
import com.hpl.media.pojo.dto.SimpleMdImageDTO;
import com.hpl.media.service.ImageMdService;
import com.hpl.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : rbe
 * @date : 2024/8/15 9:10
 */
@Tag(name="media-md图片管理接口")
@RestController
@Slf4j
@RequestMapping("/md-image")
public class ImageMdController {

    @Resource
    private ImageMdService imageMdService;

    @Operation(summary = "上传md图片")
    @PostMapping(value = "/upload")
    public CommonResult<?> uploadMdImage(@RequestPart("file") MultipartFile upload) {

        String contentType = upload.getContentType();
        log.warn("contentType:{}",contentType);
        if (contentType == null || !contentType.contains("image")) {
            log.error("上传文件类型不符合要求，请上传图片类型");
            return CommonResult.error("上传文件类型不符合要求，请上传图片类型");
        }

        try {
            String minioUrl = imageMdService.upload(contentType,upload.getOriginalFilename(),upload.getBytes());
            SimpleMdImageDTO simpleMdImageDTO = new SimpleMdImageDTO();
            simpleMdImageDTO.setUrl(minioUrl);
            simpleMdImageDTO.setDesc(upload.getOriginalFilename());
            return CommonResult.data(simpleMdImageDTO);
        } catch (IOException e) {
            ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FAILED,e.getMessage());
            log.error("上传图片过程出错:{}",e.getMessage());
        }
        return CommonResult.error("上传失败");
    }

    @Operation(summary = "md图片替换")
    @PostMapping(value = "/replace")
    public CommonResult<?> replaceMdImage(@RequestParam String originalUrl) {
        log.warn("originalUrl:{}",originalUrl);
        return CommonResult.data(originalUrl);
    }
}
