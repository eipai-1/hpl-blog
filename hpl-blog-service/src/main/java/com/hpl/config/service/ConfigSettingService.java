package com.hpl.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.dto.SearchConfigDTO;
import com.hpl.config.pojo.entity.Config;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;

/**
 * @author : rbe
 * @date : 2024/7/9 8:46
 */
public interface ConfigSettingService extends IService<Config> {

    /**
     * 保存
     *
     * @param configDTO
     */
    void saveConfig(ConfigDTO configDTO);

    /**
     * 删除
     *
     * @param bannerId
     */
    void deleteConfig(Integer bannerId);

    /**
     * 操作（上线/下线）
     *
     * @param bannerId
     */
    void operateConfig(Integer bannerId, Integer pushStatus);

    /**
     * 获取 Banner 列表
     */
    CommonPageVo<ConfigDTO> getConfigList(SearchConfigDTO searchConfigDTO);

    /**
     * 获取公告列表
     *
     * @param pageParam
     * @return
     */
    CommonPageVo<ConfigDTO> getNoticeList(CommonPageParam pageParam);
}