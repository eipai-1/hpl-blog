package com.hpl.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.config.pojo.entity.DictCommon;

import java.util.Map;

/**
 * @author : rbe
 * @date : 2024/7/8 10:40
 */
public interface DictCommonService extends IService<DictCommon> {

    /**
     * 获取字典值
     * @return
     */
    Map<String, Object> getDict();
}
