package com.hpl.count.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.count.mapper.CountMapper;
import com.hpl.count.pojo.dto.DocumentCntInfoDTO;
import com.hpl.count.pojo.entity.Count;

/**
 * @author : rbe
 * @date : 2024/9/1 22:26
 */
public interface CountService extends IService<Count> {

    DocumentCntInfoDTO getDocumentCntInfo(Long documentId);

    void incrReadCount(Long documentId);

    void doInitCache(Long documentId);

    void handleUpdateCountInfo();
}
