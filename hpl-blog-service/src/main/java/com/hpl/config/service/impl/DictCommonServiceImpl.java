package com.hpl.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hpl.article.pojo.dto.CategoryDTO;
import com.hpl.article.service.CategoryService;
import com.hpl.config.mapper.DictCommonMapper;
import com.hpl.config.pojo.dto.DictCommonDTO;
import com.hpl.config.pojo.entity.DictCommon;
import com.hpl.config.service.DictCommonService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/8 10:41
 */
@Service
public class DictCommonServiceImpl extends ServiceImpl<DictCommonMapper, DictCommon> implements DictCommonService {

    @Resource
    private DictCommonMapper dictCommonMapper ;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Map<String, Object> getDict() {
        Map<String, Object> result = Maps.newLinkedHashMap();

        List<DictCommonDTO> dictCommonList = new ArrayList<>();

        List<DictCommon> list = lambdaQuery().list();

        if (!CollectionUtils.isEmpty(list)) {
            dictCommonList = list.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }



        Map<String, Map<String, String>> dictCommonMap = Maps.newLinkedHashMap();
        for (DictCommonDTO dictCommon : dictCommonList) {
            Map<String, String> codeMap = dictCommonMap.get(dictCommon.getTypeCode());
            if (codeMap == null || codeMap.isEmpty()) {
                codeMap = Maps.newLinkedHashMap();
                dictCommonMap.put(dictCommon.getTypeCode(), codeMap);
            }
            codeMap.put(dictCommon.getDictCode(), dictCommon.getDictDesc());
        }

        // 获取分类的字典信息
        List<CategoryDTO> categoryDTOS = categoryService.loadAllCategories();
        Map<String, String> codeMap = new HashMap<>();
        categoryDTOS.forEach(categoryDTO -> codeMap.put(categoryDTO.getCategoryId().toString(), categoryDTO.getCategory()));
        dictCommonMap.put("CategoryType", codeMap);

        result.putAll(dictCommonMap);
        return result;
    }

    private DictCommonDTO toDTO(DictCommon dictCommon) {
        if (dictCommon == null) {
            return null;
        }
        DictCommonDTO dictCommonDTO = new DictCommonDTO();
        dictCommonDTO.setTypeCode(dictCommon.getTypeCode());
        dictCommonDTO.setDictCode(dictCommon.getDictCode());
        dictCommonDTO.setDictDesc(dictCommon.getDictDesc());
        dictCommonDTO.setSortNo(dictCommon.getSortNo());
        return dictCommonDTO;
    }

}
