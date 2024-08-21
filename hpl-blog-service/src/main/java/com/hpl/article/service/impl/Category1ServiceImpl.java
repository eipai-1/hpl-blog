package com.hpl.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.mapper.Category1Mapper;
import com.hpl.article.pojo.dto.Category1TreeDTO;
import com.hpl.article.service.Category1Service;
import com.hpl.article.pojo.entity.Category1;
import com.hpl.redis.RedisClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 类目Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class Category1ServiceImpl extends ServiceImpl<Category1Mapper, Category1> implements Category1Service {


    @Resource
    private Category1Mapper category1Mapper;

    @Resource
    private RedisClient redisClient;

    private final String  CATEGORY_CACHE_KEY = "category:all";

    @Override
    public List<Category1TreeDTO> getTreeCategories(String id) {
        // 获取所有的子节点
        List<Category1TreeDTO> dto = category1Mapper.selectTreeCategories(id);
        // 定义一个List，作为最终返回的数据
        List<Category1TreeDTO> result = new ArrayList<>();
        // 为了方便找子节点的父节点，这里定义一个HashMap，key是节点的id，value是节点本身
        HashMap<String, Category1TreeDTO> nodeMap = new HashMap<>();
        // 将数据封装到List中，只包括根节点的下属节点（1-1、1-2 ···），这里遍历所有节点
        dto.forEach(item -> {
            // 这里寻找父节点的直接下属节点（1-1、1-2 ···）
            if (item.getParentId().equals(id)) {
                nodeMap.put(item.getId(), item);
                result.add(item);
            }
            // 获取每个子节点的父节点
            String parentId = item.getParentId();
            Category1TreeDTO parentNode = nodeMap.get(parentId);
            // 判断HashMap中是否存在该父节点（按理说必定存在，以防万一）
            if (parentNode != null) {
                // 为父节点设置子节点（将1-1-1设为1-1的子节点）
                List childrenTreeNodes = parentNode.getChildrenTreeNodes();
                // 如果子节点暂时为null，则初始化一下父节点的子节点（给个空集合就行）
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<Category1TreeDTO>());
                }
                // 将子节点设置给父节点
                parentNode.getChildrenTreeNodes().add(item);
            }
        });
        // 返回根节点的直接下属节点（1-1、1-2 ···）
        return result;
    }

    /**
     * 根据某分类获取叶子节点
     *
     * @param category1TreeDTO
     * @return
     */
    @Override
    public List<String> getLeafIds(Category1TreeDTO category1TreeDTO){
        List<String> listIsd = redisClient.getList("category-leafIds:" + category1TreeDTO.getId(), String.class);
        if(listIsd!=null){
            return listIsd;
        }

        List<String> leafIds = new ArrayList<>();
        dfsGetLeafIds(category1TreeDTO,leafIds);
        redisClient.set("category-leafIds:" + category1TreeDTO.getId(), new ArrayList<>(leafIds), 60*60*24*7L, TimeUnit.SECONDS);
        return leafIds;
    }

    // 递归获取叶子节点ID
    private void dfsGetLeafIds(Category1TreeDTO category1TreeDTO, List<String> leafIds){
        // todo
        if(category1TreeDTO.getIsLeaf()==1){
            leafIds.add(category1TreeDTO.getId());
            return;
        }

        for(Category1TreeDTO child:category1TreeDTO.getChildrenTreeNodes()){
            dfsGetLeafIds(child,leafIds);
        }
    }

}
