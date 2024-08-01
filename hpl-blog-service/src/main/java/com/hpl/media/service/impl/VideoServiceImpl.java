package com.hpl.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.media.mapper.VideoMapper;
import com.hpl.media.pojo.dto.SearchVideoDTO;
import com.hpl.media.pojo.dto.VideoPostDTO;
import com.hpl.media.pojo.entity.Video;
import com.hpl.media.service.VideoService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/31 9:02
 */
@Service
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Resource
    private VideoMapper videoMapper;

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket.video}")
    private String VIDEO_BUCKET;

    @Value("${minio.endpoint}")
    private String END_POINT;

    @Override
    public List<Video> listVideos(SearchVideoDTO searchVideoDTO) {

        //todo
        Long userId = 1L;
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId)
                //todo
                .eq(Video::getDeleted, 0);

        if (searchVideoDTO != null) {
            if (StringUtils.hasText(searchVideoDTO.getVideoName())) {
                queryWrapper.like(Video::getVideoName, searchVideoDTO.getVideoName());
            }

        }

        queryWrapper.orderByAsc(Video::getUpdateTime);

        return videoMapper.selectList(queryWrapper);

    }


    /**
     * @param videoPostDTO 文件信息
     * @param bytes        文件字节数组
     */
    @Override
    @Transactional
    public void uploadVideo(VideoPostDTO videoPostDTO, byte[] bytes) {
        String fileMD5 = DigestUtils.md5DigestAsHex(bytes);

        // 如果文件名为空，则设置其默认文件名为 userid + 文件的md5码 + 文件后缀名
        //todo 因为后面要和用户绑定 但可以id加上后太长了 无法保存 看看怎么优化

        // 处理数据库保存名称
        String extendName = videoPostDTO.getFileName().substring(videoPostDTO.getFileName().lastIndexOf("."));

//        // 如果视频名为空，则使用原文件作为文件名
//        if (StringUtils.isEmpty(videoName)) {
//            videoName = videoPostDTO.getFileName();
//        } else {
//            videoName += extendName;
//        }
        String videoName = videoPostDTO.getFileName();

        // 处理minio视频名 根据时间生成

        String urlMinio = LocalDateTime.now().toString()
                .replace(":", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(".", "")
                + extendName;

        log.warn("视频名：{}", urlMinio);
        log.warn("视频名：{}", videoName);


        try {
            this.addVideoToMiniO(bytes, VIDEO_BUCKET, urlMinio);
            this.addVideoToDB(videoPostDTO, videoName, urlMinio, fileMD5);
        } catch (Exception e) {
            //todo xxx.of
            log.debug("上传过程中出错：{}", e.getMessage());

//            XueChengPlusException.cast("上传过程中出错" + e.getMessage());
        }

    }

    /**
     * @param bytes     文件字节数组
     * @param bucket    桶
     * @param videoName 对象名称 23/02/15/porn.mp4
     */
    public void addVideoToMiniO(byte[] bytes, String bucket, String videoName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        String contentType = getContentType(videoName);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(videoName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.debug("上传到文件系统出错:{}", e.getMessage());
            // todo xxx.of
//            throw new XueChengPlusException("上传到文件系统出错");
            log.error("上传到文件系统出错");
        }
    }

    /**
     * 根据objectName获取对应的MimeType
     *
     * @param videoName 对象名称
     * @return
     */
    private static String getContentType(String videoName) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 默认content-type为未知二进制流
        if (videoName.contains(".")) { // 判断对象名是否包含 .
            // 有 .  则划分出扩展名
            String extension = videoName.substring(videoName.lastIndexOf("."));
            // 根据扩展名得到content-type，如果为未知扩展名，例如 .abc之类的东西，则会返回null
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            // 如果得到了正常的content-type，则重新赋值，覆盖默认类型
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

    /**
     * 将图片信息添加到Video表
     *
     * @param videoPostDTO
     * @param videoName    对象名称
     * @param fileMD5      文件的md5码
     */
    @Transactional
//    @Override
    public void addVideoToDB(VideoPostDTO videoPostDTO, String videoName, String urlMinio, String fileMD5) {
        // 根据文件名获取Content-Type
        String contentType = getContentType(videoName);
        // 保存到数据库


        // 2、如果没有则添加

        Video video = new Video();

        video.setId(fileMD5);

        //TODO 展示固定用户 ， 待优化
        video.setUserId(1L);
        video.setVideoName(videoName);
        video.setVideoExtend(contentType);

        //给url添加前缀 http://127.0.0.1:9000/test1-video/
        video.setUrlMinio(END_POINT + "/" + VIDEO_BUCKET + "/" + urlMinio);


        video.setCreateTime((LocalDateTime.now()));
        video.setUpdateTime((LocalDateTime.now()));

        video.setRemark(videoPostDTO.getRemark());
        video.setFileSize(videoPostDTO.getFileSize());


        int insert = videoMapper.insert(video);
        if (insert <= 0) {
            //todo xxx.of
//            XueChengPlusException.cast("保存文件信息失败");
            log.error("保存文件信息失败");
        }
    }


    @Override
    public void renameById(String id, String newName) {
        //先查询原信息
        Video video = videoMapper.selectById(id);
        //拼接后缀
        String oldName = video.getVideoName();
        newName += oldName.substring(oldName.lastIndexOf("."));
        video.setVideoName(newName);

        videoMapper.updateById(video);
    }

    @Override
    public void deleteById(String id) {

        //todo 这个为什么不行
//        this.lambdaUpdate().set(Video::getDeleted,1)
//                .eq(Video::getId,id);

        Video video = videoMapper.selectById(id);
        video.setDeleted(1);
        videoMapper.updateById(video);
    }

}
