package com.hpl.media.service.impl;

import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.hpl.markdowm.MdImageLoader;
import com.hpl.exception.StatusEnum;
import com.hpl.media.service.ImageMdService;
import com.hpl.util.AsyncUtil;
import com.hpl.exception.ExceptionUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : rbe
 * @date : 2024/8/6 8:41
 */
@Slf4j
@Service
public class ImageMdServiceImpl implements ImageMdService {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket.mdImage}")
    private String MD_IMAGE_BUCKET;

    @Value("${minio.endpoint}")
    private String END_POINT;

    /**
     * 外网图片转存缓存
     */
    private LoadingCache<String, String> imgReplaceCache = CacheBuilder
            .newBuilder()
            .maximumSize(300)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
        @Override
        public String load(String img) {
            try {
                InputStream stream = FileReadUtil.getStreamByFileName(img);
                URI uri = URI.create(img);
                String path = uri.getPath();
                int index = path.lastIndexOf(".");
                String fileType = null;
                if (index > 0) {
                    // 从url中获取文件类型
                    fileType = path.substring(index + 1);
                    log.warn("...",path,fileType);
                }

                //todo
                return upload(fileType, fileType, StreamUtils.copyToByteArray(stream));
            } catch (Exception e) {
                log.error("外网图片转存异常! img:{}", img, e);
                return "";
            }
        }
    });

    /**
     * 保存图片。
     * 该方法接收一个HttpServletRequest请求，从中提取上传的图片并保存到指定的位置。
     *
     * @param request HTTP请求，期望其中包含一个名为"image"的文件项。
     * @return 返回保存图片的URL。
     * @throws Exception 如果请求中没有包含图片文件，或者图片格式不被支持，或者上传图片过程中发生错误，则抛出异常。
     */
    @Override
    public String saveImg(HttpServletRequest request) throws Exception {

        // 尝试将请求转换为MultipartHttpServletRequest，以便获取上传的文件
        MultipartFile file = null;
        if (request instanceof MultipartHttpServletRequest) {
            file = ((MultipartHttpServletRequest) request).getFile("image");
        }

        // 如果没有获取到文件，则抛出异常
        if (file == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "缺少需要上传的图片");
        }

        // 验证上传的图片格式是否支持
        // 目前只支持 jpg, png, webp 等静态图片格式
        String contentType = file.getContentType();
        if (contentType == null || !contentType.contains("image")) {
            log.error("上传文件类型不符合要求，请上传图片类型");
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "图片只支持png,jpg,gif");
        }else{
            try {
                // 调用图片上传服务上传图片，并返回保存后的图片URL
                return upload(contentType,file.getOriginalFilename(),file.getBytes());
            } catch (IOException e) {
                // 如果在上传过程中发生IO异常，则记录错误并抛出上传失败的异常
                log.error("Parse img from httpRequest to BufferedImage error! e:", e);
                throw ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FAILED);
            }
        }
    }

    /**
     * 外网图片转存
     *
     * @param img
     * @return
     */
    @Override
    public String saveImg(String img) {
//        if (this.uploadIgnore(img)) {
//            // 已经转存过，不需要再次转存；非http图片，不处理
//            return img;
//        }

        try {
            String ans = imgReplaceCache.get(img);
            if (StringUtils.isBlank(ans)) {
                return buildUploadFailImgUrl(img);
            }
            return ans;
        } catch (Exception e) {
            log.error("外网图片转存异常! img:{}", img, e);
            return buildUploadFailImgUrl(img);
        }
    }

    /**
     * 外网图片自动转存，添加了执行日志，超时限制；避免出现因为超时导致发布文章异常
     *
     * @param content
     * @return
     */
    @Override
    public String mdImgReplace(String content) {
        List<MdImageLoader.MdImage> imgList = MdImageLoader.loadImages(content);
        if (CollectionUtils.isEmpty(imgList)) {
            return content;
        }

        if (imgList.size() == 1) {
            // 只有一张图片时，没有必要走异步，直接转存并返回
            MdImageLoader.MdImage img = imgList.get(0);
            String newImg = saveImg(img.getUrl());
            return StringUtils.replace(content, img.getOrigin(), "![" + img.getDesc() + "](" + newImg + ")");
        }

        // 超过1张图片时，做并发的图片转存，提升性能
        AsyncUtil.CompletableFutureBridge bridge = AsyncUtil.concurrentExecutor("MdImageReplace");
        Map<MdImageLoader.MdImage, String> imgReplaceMap = Maps.newHashMapWithExpectedSize(imgList.size());
        for (MdImageLoader.MdImage img : imgList) {
            bridge.runAsyncWithTimeRecord(() -> {
                imgReplaceMap.put(img, saveImg(img.getUrl()));
            }, img.getUrl());
        }
        bridge.allExecuted().prettyPrint();

        // 图片替换
        for (Map.Entry<MdImageLoader.MdImage, String> entry : imgReplaceMap.entrySet()) {
            MdImageLoader.MdImage img = entry.getKey();
            String newImg = entry.getValue();
            content = StringUtils.replace(content, img.getOrigin(), "![" + img.getDesc() + "](" + newImg + ")");
        }
        return content;
    }

    private String buildUploadFailImgUrl(String img) {
        return img.contains("saveError") ? img : img + "?&cause=saveError!";
    }

//    /**
//     * 图片格式校验
//     *
//     * @param mime
//     * @return
//     */
//    private String validateStaticImg(String mime) {
//        if ("svg".equalsIgnoreCase(mime)) {
//            // fixme 上传文件保存到服务器本地时，做好安全保护, 避免上传了要给攻击性的脚本
//            return "svg";
//        }
//
//        if (mime.contains(MediaType.ImageJpg.getExt())) {
//            mime = mime.replace("jpg", "jpeg");
//        }
//        for (MediaType type : ImageUploader.STATIC_IMG_TYPE) {
//            if (type.getMime().equals(mime)) {
//                return type.getExt();
//            }
//        }
//        return null;
//    }


    /**
     * 上传至minio存储，md文章图片
     *
     * @param bytes
     * @return
     */
    @Override
    public String upload(String contentType, String fileName, byte[] bytes) {

        // 处理后缀保存名称
        String extendName = fileName.substring(fileName.lastIndexOf("."));


        // 处理minio图片名 根据时间生成

        String urlMinio = LocalDateTime.now().toString()
                .replace(":", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(".", "")
                + extendName;

        log.warn("图片名：{}", urlMinio);


        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(MD_IMAGE_BUCKET)
                    .object(urlMinio)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FAILED,"md上传过程");
        }


        return END_POINT+"/"+MD_IMAGE_BUCKET+"/"+urlMinio;
    }


//    public String upload(InputStream input, String fileType) {
//        try {
//            // 创建PutObjectRequest对象。
//            byte[] bytes = StreamUtils.copyToByteArray(input);
//            return upload(bytes, fileType);
//        } catch (OSSException oe) {
//            log.error("Oss rejected with an error response! msg:{}, code:{}, reqId:{}, host:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
//            return "";
//        } catch (Exception ce) {
//            log.error("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network. {}", ce.getMessage());
//            return "";
//        }
//    }
//
//    public String upload(byte[] bytes, String fileType) {
//        StopWatchUtil stopWatchUtil = StopWatchUtil.init("图片上传");
//        try {
//            // 计算md5作为文件名，避免重复上传
//            String fileName = stopWatchUtil.record("md5计算", () -> Md5Util.encode(bytes));
//            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
//            fileName = properties.getOss().getPrefix() + fileName + "." + getFileType(input, fileType);
//            // 创建PutObjectRequest对象。
//            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getOss().getBucket(), fileName, input);
//            // 设置该属性可以返回response。如果不设置，则返回的response为空。
//            putObjectRequest.setProcess("true");
//
//            // 上传文件
//            PutObjectResult result = stopWatchUtil.record("文件上传", () -> ossClient.putObject(putObjectRequest));
//            if (SUCCESS_CODE == result.getResponse().getStatusCode()) {
//                return properties.getOss().getHost() + fileName;
//            } else {
//                log.error("upload to oss error! response:{}", result.getResponse().getStatusCode());
//                // Guava 不允许回传 null
//                return "";
//            }
//        } catch (OSSException oe) {
//            log.error("Oss rejected with an error response! msg:{}, code:{}, reqId:{}, host:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
//            return  "";
//        } catch (Exception ce) {
//            log.error("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network. {}", ce.getMessage());
//            return  "";
//        } finally {
//            if (log.isDebugEnabled()) {
//                log.debug("upload image size:{} cost: {}", bytes.length, stopWatchUtil.prettyPrint());
//            }
//        }
//    }

//    private boolean uploadIgnore(String imageUrl) {
//        if (StringUtils.isNotBlank(properties.getOss().getHost()) && imageUrl.startsWith(properties.getOss().getHost())) {
//            return true;
//        }
//
//        return !imageUrl.startsWith("http");
//    }
}
