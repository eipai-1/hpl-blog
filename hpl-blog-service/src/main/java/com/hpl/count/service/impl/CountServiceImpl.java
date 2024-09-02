package com.hpl.count.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.count.mapper.CountMapper;
import com.hpl.count.pojo.dto.DocumentCntInfoDTO;
import com.hpl.count.pojo.entity.Count;
import com.hpl.count.service.CountService;
import com.hpl.redis.RedisClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author : rbe
 * @date : 2024/9/1 22:28
 */
@Service
public class CountServiceImpl extends ServiceImpl<CountMapper, Count> implements CountService {

    @Resource
    private RedisClient redisClient;

    private final String preKey = "statistics";

    @Override
    public DocumentCntInfoDTO getDocumentCntInfo(Long documentId){
        Integer readCnt = getReadCnt(documentId);
        Integer praiseCnt = getPraiseCnt(documentId);
        Integer collectionCnt = getCollectionCnt(documentId);
        Integer commentCnt = getCommentCnt(documentId);

        return DocumentCntInfoDTO.builder()
                .readCount(readCnt)
                .praiseCount(praiseCnt)
                .collectionCount(collectionCnt)
                .commentCount(commentCnt)
                .build();
    }

    private Integer getReadCnt(Long documentId){
        String readCnt = (redisClient.get(preKey + ":read:docId-" + documentId));
        if(readCnt != null) {
            return Integer.valueOf(readCnt);
        }

        Count count = lambdaQuery()
                .eq(Count::getDocumentId, documentId)
                .one();

        if(count == null) {
            doInitCache(documentId);
        }else{
            doCache(count);
        }

        return Integer.valueOf(redisClient.get(preKey + ":read:docId-" + documentId));
    }

    private Integer getPraiseCnt(Long documentId){
        String praiseCnt = redisClient.get(preKey + ":praised:docId-" + documentId);
        if(praiseCnt != null) {
            return Integer.valueOf(praiseCnt);
        }

        Count count = lambdaQuery()
                .eq(Count::getDocumentId, documentId)
                .one();

        if(count == null) {
            doInitCache(documentId);
        }else{
            doCache(count);
        }

        return Integer.valueOf(redisClient.get(preKey + ":praised:docId-" + documentId));
    }

    private Integer getCollectionCnt(Long documentId){
        String collectionCnt = redisClient.get(preKey + ":collected:docId-" + documentId);
        if(collectionCnt != null) {
            return Integer.valueOf(collectionCnt);
        }

        Count count = lambdaQuery()
                .eq(Count::getDocumentId, documentId)
                .one();

        if(count == null) {
            doInitCache(documentId);
        }else{
            doCache(count);
        }

        return Integer.valueOf(redisClient.get(preKey + ":collected:docId-" + documentId));
    }

    private Integer getCommentCnt(Long documentId){
        String commentCnt = redisClient.get(preKey + ":commented:docId-" + documentId);
        if(commentCnt != null) {
            return Integer.valueOf(commentCnt);
        }

        Count count = lambdaQuery()
                .eq(Count::getDocumentId, documentId)
                .one();

        if(count == null) {
            doInitCache(documentId);
        }else{
            doCache(count);
        }

        return Integer.valueOf(redisClient.get(preKey + ":commented:docId-" + documentId));
    }

    /**
     * 对于不存在的记录，初始化缓存
     * @param documentId
     */
    private void doInitCache(Long documentId) {
        redisClient.set(preKey + ":read:docId-" + documentId, "0", 3L, TimeUnit.DAYS);
        redisClient.set(preKey + ":praised:docId-" + documentId, "0", 3L, TimeUnit.DAYS);
        redisClient.set(preKey + ":collected:docId-" + documentId, "0", 3L, TimeUnit.DAYS);
        redisClient.set(preKey + ":commented:docId-" + documentId, "0", 3L, TimeUnit.DAYS);
        // 加锁标记
        redisClient.sAdd("lock:statistics", documentId.toString());
    }

    /**
     * 缓存文档数据
     * @param count
     */
    private void doCache(Count count){

        if(redisClient.get(preKey + ":read:docId-" + count.getDocumentId())==null){
            redisClient.set(preKey + ":read:docId-" + count.getDocumentId(), count.getReadCnt().toString(), 3L, TimeUnit.DAYS);
        }

        if(redisClient.get(preKey + ":praised:docId-" + count.getDocumentId())==null){
            redisClient.set(preKey + ":praised:docId-" + count.getDocumentId(), count.getPraiseCnt().toString(), 3L, TimeUnit.DAYS);
        }

        if(redisClient.get(preKey + ":collected:docId-" + count.getDocumentId())==null){
            redisClient.set(preKey + ":collected:docId-" + count.getDocumentId(), count.getCollectionCnt().toString(), 3L, TimeUnit.DAYS);
        }

        if(redisClient.get(preKey + ":commented:docId-" + count.getDocumentId())==null){
            redisClient.set(preKey + ":commented:docId-" + count.getDocumentId(), count.getCommentCnt().toString(), 3L, TimeUnit.DAYS);
        }


        // 加锁标记
        redisClient.sAdd("lock:statistics", count.getDocumentId().toString());
    }

    @Override
    public void incrReadCount(Long documentId){
        // 文章阅读计数加1并锁定30分钟（即30分钟内重复点击不会加1）
        if (redisClient.setIfAbsent("lock:read:"+documentId,"locked",30L,TimeUnit.MINUTES)){
            redisClient.incr(preKey + ":read:docId-"+documentId);
        }
    }
}
