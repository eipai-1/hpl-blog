package com.hpl.controller.media;


import com.hpl.media.pojo.dto.SearchVideoDTO;
import com.hpl.media.pojo.dto.VideoPostDTO;
import com.hpl.media.pojo.entity.Video;
import com.hpl.media.service.VideoService;
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

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author : rbe
 * @date : 2024/7/31 9:05
 */
@Tag(name="media-视频媒资管理接口")
@RestController
@Slf4j
@RequestMapping("/video")
public class VideoController extends CommonController {

    // 单个文件最大大小
    private final Long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    @Resource
    private VideoService videoService;

    @Operation(summary = "列表查看所有视频")
    @PostMapping("/videos")
    public CommonResult<List<Video>> listVideos(@RequestBody SearchVideoDTO searchVideoDTO){
        log.warn("searchImageDTO:{}",searchVideoDTO);

        // 校验页码和每页大小
        CommonPageParam pageParam =this.buildPageParam(searchVideoDTO.getPageNum(), searchVideoDTO.getPageSize());

        List<Video> list = videoService.listVideos(searchVideoDTO.getVideoName(), pageParam);
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

        byte mp4Data[] = upload.getBytes();

        VideoPostDTO videoPostDTO = new VideoPostDTO();
        videoPostDTO.setFileName(upload.getOriginalFilename());

        if (contentType.contains("avi")){
            log.warn("avi类型，做转码处理");
            //todo
            try {
                mp4Data = convertAviToMp4(upload.getBytes());
            } catch (InterruptedException e) {
                log.error("转码过程出错:{}",e.getMessage());
            }
            videoPostDTO.setFileName(videoPostDTO.getFileName().replace(".avi",".mp4"));
        }

        videoPostDTO.setFileSize(upload.getSize());
        videoPostDTO.setRemark(remark);
        videoPostDTO.setContentType(contentType);




        if(upload.getSize()>MAX_FILE_SIZE){
            log.warn("上传大小超过50mb，转至分块处理");

            //todo 视频分块上传

        }else{
            videoService.uploadVideo(videoPostDTO, mp4Data);
            return CommonResult.success();
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

    @Operation(summary = "编辑备注")
    @PutMapping("/remark/{id}")
    public CommonResult<?> editRemark(@PathVariable String id, @RequestParam String newRemark) {
        videoService.editRemarkById(id,newRemark);
        return CommonResult.success();
    }


    private byte[] helper(byte[] aviData) throws IOException, InterruptedException {
        log.info("开始转码");
        File aviFile = File.createTempFile("input", ".avi");
        File mp4File = File.createTempFile("output", ".mp4");

        // 将 AVI 字节流写入临时文件
        try (FileOutputStream fos = new FileOutputStream(aviFile)) {
            fos.write(aviData);
        }

        // 构建 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", aviFile.getAbsolutePath(), "-c:v", "libx264", "-c:a", "aac", mp4File.getAbsolutePath()
        );

        // 启动进程
        Process process = processBuilder.start();
        process.waitFor();

        // 读取生成的 MP4 文件
        byte[] mp4Data;
        try (FileInputStream fis = new FileInputStream(mp4File)) {
            mp4Data = fis.readAllBytes();
        }

        // 删除临时文件
        aviFile.delete();
        mp4File.delete();

        log.info("转码完成");
        return mp4Data;
    }


    public byte[] convertAviToMp42(byte[] aviData) throws IOException, InterruptedException, TimeoutException {
        // 创建临时文件路径
        File aviFile = File.createTempFile("input", ".avi");
        File mp4File = File.createTempFile("output", ".mp4");
        File logFile = File.createTempFile("ffmpeg", ".log");

        // 将 AVI 字节流写入临时文件
        try (FileOutputStream fos = new FileOutputStream(aviFile)) {
            fos.write(aviData);
        }

        // 构建 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-y","-i", aviFile.getAbsolutePath(), "-c:v", "libx264", "-c:a", "aac", mp4File.getAbsolutePath()
        );

        // 重定向错误流到日志文件
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        // 启动进程
        Process process = processBuilder.start();

        // 使用 ExecutorService 来异步等待进程完成
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() ->process.waitFor());

        try {
            // 等待进程完成，设置超时时间为 5 分钟
            int exitCode = future.get(5, TimeUnit.MINUTES);

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg 转换失败，请检查日志文件：" + logFile.getAbsolutePath());
            }

            // 读取生成的 MP4 文件
            byte[] mp4Data;
            try (FileInputStream fis = new FileInputStream(mp4File)) {
                mp4Data = fis.readAllBytes();
            }

            return mp4Data;
        } catch (TimeoutException | ExecutionException e) {
            process.destroy();
            throw new TimeoutException("FFmpeg 转换超时");
        } finally {
            executor.shutdownNow();
            // 删除临时文件
            aviFile.delete();
            mp4File.delete();
            // 保留日志文件以便调试
            System.out.println("FFmpeg 日志文件位置：" + logFile.getAbsolutePath());
        }
    }

    // 将 AVI 字节流转换为 MP4 字节流
    public byte[] convertAviToMp4(byte[] aviData) throws IOException, InterruptedException {
        // 创建临时文件路径
        File aviFile = File.createTempFile("input", ".avi");
        File mp4File = File.createTempFile("output", ".mp4");

        // 将 AVI 字节流写入临时文件
        try (FileOutputStream fos = new FileOutputStream(aviFile)) {
            fos.write(aviData);
        }

        // 构建 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-y", "-i", aviFile.getAbsolutePath(), "-c:v", "libx264", "-c:a", "aac", mp4File.getAbsolutePath()
        );

        // 启动进程
        Process process = processBuilder.start();

        // 创建线程来处理输出流和错误流
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

        // 启动线程处理输出流和错误流
        outputGobbler.start();
        errorGobbler.start();

        // 等待进程完成
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 转换失败，错误信息：" + errorGobbler.getCapturedOutput());
        }

        // 读取生成的 MP4 文件
        byte[] mp4Data;
        try (FileInputStream fis = new FileInputStream(mp4File)) {
            mp4Data = fis.readAllBytes();
        }

        // 删除临时文件
        aviFile.delete();
        mp4File.delete();

        return mp4Data;
    }

    // 处理流的辅助类
    private static class StreamGobbler extends Thread {
        private final InputStream inputStream;
        private final String type;
        private final StringBuilder capturedOutput = new StringBuilder();

        public StreamGobbler(InputStream inputStream, String type) {
            this.inputStream = inputStream;
            this.type = type;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    capturedOutput.append(line).append(System.lineSeparator());
                    // 实时输出到控制台
                    System.out.println(type + "> " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getCapturedOutput() {
            return capturedOutput.toString();
        }
    }
}


