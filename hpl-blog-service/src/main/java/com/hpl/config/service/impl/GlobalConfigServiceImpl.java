package com.hpl.config.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.config.mapper.GlobalConfigMapper;
import com.hpl.config.pojo.dto.GlobalConfigDTO;
import com.hpl.config.pojo.dto.SearchGlobalConfigDTO;
import com.hpl.config.pojo.entity.GlobalConfig;
import com.hpl.config.pojo.event.ConfigRefreshEvent;
import com.hpl.config.service.GlobalConfigService;
import com.hpl.enums.StatusEnum;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageVo;
import com.hpl.util.ExceptionUtil;
import com.hpl.util.NumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/8 19:04
 */
@Service
public class GlobalConfigServiceImpl extends ServiceImpl<GlobalConfigMapper, GlobalConfig> implements GlobalConfigService {

    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    /**
     * 根据条件查询全局配置列表，并分页返回结果。
     *
     * @param searchGlobalConfigDTO 查询条件对象，包含关键字、值和备注等过滤条件。
     * @return 返回分页后的全局配置DTO列表。
     */
    @Override
    public CommonPageVo<GlobalConfigDTO> getList(SearchGlobalConfigDTO searchGlobalConfigDTO) {
        // 构建查询条件，使用LambdaQueryWrapper进行链式查询设置
        LambdaQueryWrapper<GlobalConfig> query = Wrappers.lambdaQuery();
        // 根据关键字、值和备注进行模糊查询
        query.and(!StringUtils.isEmpty(searchGlobalConfigDTO.getKeywords()),
                        k -> k.like(GlobalConfig::getKey, searchGlobalConfigDTO.getKeywords()))
                .and(!StringUtils.isEmpty(searchGlobalConfigDTO.getValue()),
                        v -> v.like(GlobalConfig::getValue, searchGlobalConfigDTO.getValue()))
                .and(!StringUtils.isEmpty(searchGlobalConfigDTO.getComment()),
                        c -> c.like(GlobalConfig::getComment, searchGlobalConfigDTO.getComment()))
                // 只查询未删除的记录
                .eq(GlobalConfig::getDeleted, CommonDeletedEnum.NO.getCode())
                // 按更新时间降序排序
                .orderByDesc(GlobalConfig::getUpdateTime);

        // 选择需要查询的字段
        query.select(GlobalConfig::getId,
                GlobalConfig::getKey,
                GlobalConfig::getValue,
                GlobalConfig::getComment);

        // 根据构建的查询条件查询全局配置列表
        List<GlobalConfig> list =  globalConfigMapper.selectList(query);

        // 将查询到的GlobalConfig实体转换为GlobalConfigDTO DTO列表
        List<GlobalConfigDTO> dtos = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // 构建总数查询条件，与列表查询条件相同
        LambdaQueryWrapper<GlobalConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(!StringUtils.isEmpty(searchGlobalConfigDTO.getKeywords()),
                            k -> k.like(GlobalConfig::getKey, searchGlobalConfigDTO.getKeywords()))
                .and(!StringUtils.isEmpty(searchGlobalConfigDTO.getValue()),
                            v -> v.like(GlobalConfig::getValue, searchGlobalConfigDTO.getValue()))
                .and(!StringUtils.isEmpty(searchGlobalConfigDTO.getComment()),
                            c -> c.like(GlobalConfig::getComment, searchGlobalConfigDTO.getComment()))
                .eq(GlobalConfig::getDeleted, CommonDeletedEnum.NO.getCode())
                .orderByDesc(GlobalConfig::getUpdateTime);

        // 计算满足条件的记录总数
        // 总数
        Long total = globalConfigMapper.selectCount(wrapper);

        // 使用DTO列表、每页大小、当前页码和总数构建分页对象
        return CommonPageVo.build(dtos, searchGlobalConfigDTO.getPageSize(), searchGlobalConfigDTO.getPageNumber(), total);
    }



    private GlobalConfigDTO toDTO(GlobalConfig config) {
        if ( config == null ) {
            return null;
        }

        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();

        globalConfigDTO.setKeywords( config.getKey() );
        globalConfigDTO.setId( config.getId() );
        globalConfigDTO.setValue( config.getValue() );
        globalConfigDTO.setComment( config.getComment() );

        return globalConfigDTO;
    }



    /**
     * 保存或更新全局配置信息。
     *
     * @param globalConfigDTO 全局配置的DTO（数据传输对象），包含配置的关键字、ID、值和备注信息。
     * 此方法首先将DTO的信息映射到GlobalConfig实体对象中，然后根据ID的存在与否决定是保存新配置还是更新已有的配置。
     * 最后，发布一个事件以触发配置的重新加载。
     */
    @Override
    public void save(GlobalConfigDTO globalConfigDTO) {
        // 创建一个新的GlobalConfig实例
        GlobalConfig globalConfig = new GlobalConfig();

        // 当传入的DTO不为空时，从DTO中提取信息并设置到GlobalConfig实例中
        if (globalConfigDTO != null) {
            globalConfig.setKey(globalConfigDTO.getKeywords());
            globalConfig.setId(globalConfigDTO.getId());
            globalConfig.setValue(globalConfigDTO.getValue());
            globalConfig.setComment(globalConfigDTO.getComment());
        }

        // 根据ID是否为0（新配置的标志）来决定是保存还是更新配置
        // id 不为空
        if (NumUtil.eqZero(globalConfig.getId())) {
            this.save(globalConfig);
        } else {
            this.updateById(globalConfig);
        }

        // 发布一个事件，以通知监听器配置已更新，需要重新加载配置
        // 配置更新之后，主动触发配置的动态加载
        SpringUtil.publishEvent(new ConfigRefreshEvent(this, globalConfigDTO.getKeywords(), globalConfigDTO.getValue()));
    }


    /**
     * 根据ID删除配置信息。
     *
     * @param id 配置信息的唯一标识符。
     * @throws RuntimeException 如果指定的记录不存在，则抛出运行时异常。
     */
    @Override
    public void delete(Long id) {
        // 构建查询条件，只查询未被删除的记录
        // 查询的时候 deleted 为 0
        LambdaQueryWrapper<GlobalConfig> query = Wrappers.lambdaQuery();
        query.select(GlobalConfig::getId, GlobalConfig::getKey, GlobalConfig::getValue, GlobalConfig::getComment)
                .eq(GlobalConfig::getId, id)
                .eq(GlobalConfig::getDeleted, CommonDeletedEnum.NO.getCode());
        GlobalConfig globalConfig = globalConfigMapper.selectOne(query);

        // 检查记录是否存在，如果存在则删除，否则抛出异常
        if (globalConfig != null) {
            this.removeById(globalConfig);
        } else {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "记录不存在");
        }
    }


    /**
     * 添加敏感词白名单
     *
     * @param word
     */
    @Override
    public void addSensitiveWhiteWord(String word) {
//        String key = SensitiveProperty.SENSITIVE_KEY_PREFIX + ".allow";
        //todo
        String key = "mg.allow";
        GlobalConfigDTO globalConfigDTO = new GlobalConfigDTO();
        globalConfigDTO.setKeywords(key);


        LambdaQueryWrapper<GlobalConfig> query = Wrappers.lambdaQuery();
        query.select(GlobalConfig::getId, GlobalConfig::getKey, GlobalConfig::getValue, GlobalConfig::getComment)
                .eq(GlobalConfig::getKey, key)
                .eq(GlobalConfig::getDeleted, CommonDeletedEnum.NO.getCode());
        GlobalConfig config = globalConfigMapper.selectOne(query);

        if (config == null) {
            globalConfigDTO.setValue(word);
            globalConfigDTO.setComment("敏感词白名单");
        } else {
            globalConfigDTO.setValue(config.getValue() + "," + word);
            globalConfigDTO.setComment(config.getComment());
            globalConfigDTO.setId(config.getId());
        }
        // 更新敏感词白名单
        save(globalConfigDTO);

//        // 移除敏感词记录
        //todo
//        SpringUtil.getBean(SensitiveService.class).removeSensitiveWord(word);
    }
}
