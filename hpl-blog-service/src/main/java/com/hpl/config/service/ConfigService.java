package com.hpl.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.entity.Config;
import com.hpl.config.pojo.enums.ConfigTypeEnum;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/8 9:56
 */
public interface ConfigService extends IService<Config> {

    /**
     * 获取 Banner 列表
     *
     * @param configTypeEnum
     * @return
     */
    List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum);

    /**
     * 阅读次数+1
     *
     * @param configId
     * @param extra
     */
    void updateVisit(long configId, String extra);
}
