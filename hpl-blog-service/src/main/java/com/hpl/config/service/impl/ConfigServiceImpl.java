package com.hpl.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.pojo.enums.PushStatusEnum;
import com.hpl.config.mapper.ConfigMapper;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.entity.Config;
import com.hpl.config.pojo.enums.ConfigTypeEnum;
import com.hpl.config.service.ConfigService;
import com.hpl.pojo.CommonDeletedEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/8 9:56
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper,Config> implements ConfigService {

//    @Autowired
//    private ConfigMapper configMapper;

    /**
     * 根据配置类型获取配置列表。
     *
     * 此方法根据配置类型、状态和删除状态查询配置信息，并将查询结果转换为ConfigDTO列表返回。
     * 查询条件包括配置的类型为指定类型，状态为在线，且未被删除。
     * 查询结果按照配置的排名升序排序。
     * 如果查询结果为空，则返回空列表。
     *
     * @param configTypeEnum 配置类型的枚举值，用于指定查询的配置类型。
     * @return ConfigDTO列表，包含符合条件的配置信息。
     */
    @Override
    public List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum) {

        // 根据类型、状态和删除状态查询配置信息，并按照排名升序排序
        List<Config> configs = lambdaQuery()
                .eq(Config::getType, configTypeEnum.getCode())
                .eq(Config::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByAsc(Config::getRank)
                .list();

        // 如果查询结果为空，返回空列表
        if (CollectionUtils.isEmpty(configs)) {
            return Collections.emptyList();
        }
        // 将查询结果转换为ConfigDTO列表并返回
        return configs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    private ConfigDTO toDTO(Config configDO) {
        if (configDO == null) {
            return null;
        }
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setType(configDO.getType());
        configDTO.setName(configDO.getName());
        configDTO.setBannerUrl(configDO.getBannerUrl());
        configDTO.setJumpUrl(configDO.getJumpUrl());
        configDTO.setContent(configDO.getContent());
        configDTO.setRank(configDO.getRank());
        configDTO.setStatus(configDO.getStatus());
        configDTO.setId(configDO.getId());
        configDTO.setTags(configDO.getTags());
        configDTO.setExtra(configDO.getExtra());
        configDTO.setCreateTime(configDO.getCreateTime());
        configDTO.setUpdateTime(configDO.getUpdateTime());
        return configDTO;
    }


    /**
     * 配置发生变更之后，失效本地缓存，这里主要是配合 SidebarServiceImpl 中的缓存使用
     *
     * @param configId
     * @param extra
     */
    @Override
    public void updateVisit(long configId, String extra) {

        lambdaUpdate()
                .set(Config::getExtra, extra)
                .eq(Config::getId, configId)
                .update();
    }
}