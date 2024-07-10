package com.hpl.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.config.pojo.dto.GlobalConfigDTO;
import com.hpl.config.pojo.dto.SearchGlobalConfigDTO;
import com.hpl.config.pojo.entity.GlobalConfig;
import com.hpl.pojo.CommonPageVo;

/**
 * @author : rbe
 * @date : 2024/7/8 19:04
 */
public interface GlobalConfigService extends IService<GlobalConfig> {
    CommonPageVo<GlobalConfigDTO> getList(SearchGlobalConfigDTO searchGlobalConfigDTO);

    void save(GlobalConfigDTO globalConfigDTO);

    void delete(Long id);

    /**
     * 添加敏感词白名单
     *
     * @param word
     */
    void addSensitiveWhiteWord(String word);
}
