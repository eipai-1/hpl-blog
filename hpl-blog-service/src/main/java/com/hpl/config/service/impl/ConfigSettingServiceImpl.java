package com.hpl.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.config.mapper.ConfigMapper;
import com.hpl.config.pojo.dto.ConfigDTO;
import com.hpl.config.pojo.dto.SearchConfigDTO;
import com.hpl.config.pojo.entity.Config;
import com.hpl.config.pojo.enums.ConfigTypeEnum;
import com.hpl.config.service.ConfigSettingService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;
import com.hpl.util.NumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/9 8:46
 */
@Service
public class ConfigSettingServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigSettingService {

//    @Autowired
//    private ConfigDao configDao;

    @Override
    public void saveConfig(ConfigDTO configDTO) {
        Config config = toEntity(configDTO);

        if (NumUtil.eqZero(configDTO.getId())) {
            this.save(config);
        } else {
            config.setId(configDTO.getId());
            this.updateById(config);
        }
    }

    private Config toEntity(ConfigDTO configDTO) {
        if ( configDTO == null ) {
            return null;
        }

        Config configDO = new Config();

        configDO.setType( configDTO.getType() );
        configDO.setName( configDTO.getName() );
        configDO.setBannerUrl( configDTO.getBannerUrl() );
        configDO.setJumpUrl( configDTO.getJumpUrl() );
        configDO.setContent( configDTO.getContent() );
        configDO.setRank( configDTO.getRank() );
        configDO.setTags( configDTO.getTags() );

        return configDO;
    }

    @Override
    public void deleteConfig(Integer configId) {

        Config config = lambdaQuery()
                .eq(Config::getId, configId)
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .one();


        if (config != null){
            config.setDeleted(CommonDeletedEnum.YES.getCode());
            this.updateById(config);
        }
    }

    @Override
    public void operateConfig(Integer configId, Integer pushStatus) {
        Config config = lambdaQuery()
                .eq(Config::getId, configId)
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .one();

        if (config != null){
            config.setStatus(pushStatus);
            this.updateById(config);
        }
    }

    /**
     * 根据搜索条件获取配置列表。
     *
     * @param searchConfigDTO 搜索条件，包含配置名称、类型、分页信息等。
     * @return 返回搜索结果的分页对象，包含配置的DTO列表。
     */
    @Override
    public CommonPageVo<ConfigDTO> getConfigList(SearchConfigDTO searchConfigDTO) {
        // 根据搜索条件查询未删除的配置列表，按更新时间降序、排名升序排序，并根据分页参数限制返回结果数量。
        List<Config> configs = lambdaQuery()
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .like(StringUtils.isNotBlank(searchConfigDTO.getName()), Config::getName, searchConfigDTO.getName())
                .eq(searchConfigDTO.getType() != null && searchConfigDTO.getType() != -1, Config::getType, searchConfigDTO.getType())
                .orderByDesc(Config::getUpdateTime)
                .orderByAsc(Config::getRank)
                .last(CommonPageParam.getLimitSql(
                        CommonPageParam.newInstance(searchConfigDTO.getPageNumber(), searchConfigDTO.getPageSize())))
                .list();

        // 将查询到的Config实体转换为ConfigDTO，并收集到列表中。
        List<ConfigDTO> dtos = configs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // 统计满足搜索条件的配置总数。
        Long totalCount = lambdaQuery()
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .like(StringUtils.isNotBlank(searchConfigDTO.getName()), Config::getName, searchConfigDTO.getName())
                .eq(searchConfigDTO.getType() != null && searchConfigDTO.getType() != -1, Config::getType, searchConfigDTO.getType())
                .count();

        // 根据查询到的数据列表、每页数量、当前页码和总数，构建并返回分页对象。
        return CommonPageVo.build(dtos, searchConfigDTO.getPageSize(), searchConfigDTO.getPageNumber(), totalCount);
    }



    private ConfigDTO toDTO(Config config) {
        if ( config == null ) {
            return null;
        }

        ConfigDTO configDTO = new ConfigDTO();

        configDTO.setId( config.getId() );
        configDTO.setCreateTime( config.getCreateTime() );
        configDTO.setUpdateTime( config.getUpdateTime() );
        configDTO.setType( config.getType() );
        configDTO.setName( config.getName() );
        configDTO.setBannerUrl( config.getBannerUrl() );
        configDTO.setJumpUrl( config.getJumpUrl() );
        configDTO.setContent( config.getContent() );
        configDTO.setRank( config.getRank() );
        configDTO.setStatus( config.getStatus() );
        configDTO.setExtra( config.getExtra() );
        configDTO.setTags( config.getTags() );

        return configDTO;
    }



    /**
     * 根据分页参数获取通知配置列表。
     *
     * @param pageParam 分页参数，包含页码和每页数量。
     * @return 返回分页后的通知配置DTO列表。
     */
    @Override
    public CommonPageVo<ConfigDTO> getNoticeList(CommonPageParam pageParam) {
        // 根据类型为通知且未被删除的条件查询配置列表，按创建时间降序排列，并根据分页参数限制返回结果数量。
        List<Config> configs = lambdaQuery()
                .eq(Config::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByDesc(Config::getCreateTime)
                .last(CommonPageParam.getLimitSql(pageParam))
                .list();

        // 将查询到的Config实体转换为ConfigDTO，并收集到列表中。
        List<ConfigDTO> dtos = configs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // 查询满足条件的通知配置总数（不分页）。
        long totalCount = lambdaQuery()
                .eq(Config::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(Config::getDeleted, CommonDeletedEnum.NO.getCode())
                .count();

        // 根据查询到的数据列表、每页数量、当前页码和总数量构建并返回分页信息。
        return CommonPageVo.build(dtos, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }

}
