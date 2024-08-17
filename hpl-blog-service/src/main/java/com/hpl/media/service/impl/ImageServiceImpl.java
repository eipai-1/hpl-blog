package com.hpl.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.media.mapper.ImageMapper;
import com.hpl.media.pojo.dto.ImagePostDTO;
import com.hpl.media.pojo.entity.Image;
import com.hpl.media.pojo.enums.ImageStatusEnum;
import com.hpl.media.service.ImageService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.snowflake.SnowFlakeIdUtil;
import com.hpl.snowflake.SnowflakeIdGenerator;
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
 * @date : 2024/7/28 18:21
 */
@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Resource
    private ImageMapper imageMapper;

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket.image}")
    private String IMAGE_BUCKET;

    @Value("${minio.endpoint}")
    private String END_POINT;

    @Override
    public List<Image> listImages(String searchName, CommonPageParam pageParam){

        //todo
        Long userId = 1L;

        LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Image::getUserId, userId)
                .eq(Image::getStatus, ImageStatusEnum.SHOW.getCode())
                .eq(Image::getDeleted, CommonDeletedEnum.NO.getCode());

        if (StringUtils.hasText(searchName)){
            log.warn(searchName);
            queryWrapper.like(Image::getImageName, searchName);
        }

        queryWrapper.orderByAsc(Image::getUpdateTime);
        queryWrapper.last(CommonPageParam.getLimitSql(pageParam));


        return imageMapper.selectList(queryWrapper);

    }

    @Override
    public List<Image> listHiddenImages(String searchName, CommonPageParam pageParam){
        //todo
        Long userId = 1L;

        LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Image::getUserId, userId)
                .eq(Image::getStatus, ImageStatusEnum.NOT_SHOW.getCode())
                .eq(Image::getDeleted,CommonDeletedEnum.NO.getCode());

        if (StringUtils.hasText(searchName)){
            log.warn(searchName);
            queryWrapper.like(Image::getImageName, searchName);
        }
        queryWrapper.orderByAsc(Image::getUpdateTime);
        queryWrapper.last(CommonPageParam.getLimitSql(pageParam));

        return imageMapper.selectList(queryWrapper);
    }


    /**
     * @param imagePostDTO 文件信息
     * @param bytes        文件字节数组
     * @param imageName   对象名称
     */
    @Override
    @Transactional
    public void uploadImage(ImagePostDTO imagePostDTO, byte[] bytes, String imageName) {
//        String fileMD5 = DigestUtils.md5DigestAsHex(bytes);


        // 如果文件名为空，则设置其默认文件名为 userid + 文件的md5码 + 文件后缀名
        //todo 因为后面要和用户绑定 但可以id加上后太长了 无法保存 看看怎么优化

        // 处理数据库保存名称
        String extendName = imagePostDTO.getFileName().substring(imagePostDTO.getFileName().lastIndexOf("."));

        // 如果图片名为空，则使用原文件名作为文件名
        if(StringUtils.isEmpty(imageName)){
            imageName = imagePostDTO.getFileName();
        }else{
            imageName += extendName;
        }

        // 处理minio图片名 根据时间生成

        String urlMinio = LocalDateTime.now().toString()
                .replace(":", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(".", "")
                +extendName;

        log.warn("图片名：{}",urlMinio);
        log.warn("图片名：{}",imageName);





        try {
            this.addImageToMiniO(bytes, IMAGE_BUCKET, urlMinio);
            this.addImageToDB(imagePostDTO, imageName,urlMinio,SnowFlakeIdUtil.genStrId());
        } catch (Exception e) {
            //todo xxx.of
            log.debug("上传过程中出错：{}",e.getMessage());

//            XueChengPlusException.cast("上传过程中出错" + e.getMessage());
        }

    }

    /**
     * @param bytes      文件字节数组
     * @param bucket     桶
     * @param imageName 对象名称 23/02/15/porn.mp4
     */
    public void addImageToMiniO(byte[] bytes, String bucket, String imageName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        String contentType = getContentType(imageName);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(imageName)
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
     * @param imageName 对象名称
     * @return
     */
    private static String getContentType(String imageName) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 默认content-type为未知二进制流
        if (imageName.contains(".")) { // 判断对象名是否包含 .
            // 有 .  则划分出扩展名
            String extension = imageName.substring(imageName.lastIndexOf("."));
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
     * 将图片信息添加到Image表
     *
     * @param imagePostDTO
     * @param imageName   对象名称
     * @param fileMD5      文件的md5码
     */
    @Transactional
//    @Override
    public void addImageToDB(ImagePostDTO imagePostDTO, String imageName,String urlMinio, String fileMD5) {
        // 根据文件名获取Content-Type
        String contentType = getContentType(imageName);
        // 保存到数据库
        // 1、先查一遍，如果有则不重复添加
        Image image = imageMapper.selectById(fileMD5);
        if (image != null) {
            return;
        }

        // 2、如果没有则添加

        image = new Image();

        image.setId(fileMD5);

        //TODO 展示固定用户 ， 待优化
        image.setUserId(1L);
        image.setImageName(imageName);
        image.setImageExtend(contentType);

        //给url添加前缀 http://127.0.0.1:9000/test1-image/
        image.setUrlMinio(END_POINT + "/" + IMAGE_BUCKET + "/" + urlMinio);


        image.setCreateTime((LocalDateTime.now()));
        image.setUpdateTime((LocalDateTime.now()));
        image.setStatus(ImageStatusEnum.SHOW.getCode());

        image.setRemark(imagePostDTO.getRemark());
        image.setFileSize(imagePostDTO.getFileSize());


        int insert = imageMapper.insert(image);
        if (insert <= 0) {
            //todo xxx.of
//            XueChengPlusException.cast("保存文件信息失败");
            log.error("保存文件信息失败");
        }
    }


    @Override
    public void renameById(String id,String newName){
        //先查询原信息
        Image image = imageMapper.selectById(id);
        //拼接后缀
        String oldName = image.getImageName();
        newName += oldName.substring(oldName.lastIndexOf("."));
        image.setImageName(newName);

        imageMapper.updateById(image);
    }

    @Override
    public void hideImage(String id){
        lambdaUpdate().set(Image::getStatus,ImageStatusEnum.NOT_SHOW.getCode())
                .eq(Image::getId,id)
                .update();
    }


    @Override
    public void showImage(String id){
        lambdaUpdate().set(Image::getStatus,ImageStatusEnum.SHOW.getCode())
                .eq(Image::getId,id)
                .update();
    }

    @Override
    public void editRemarkById(String id, String newRemark){
       lambdaUpdate().set(Image::getRemark,newRemark)
               .eq(Image::getId,id)
               .update();
    }

}
