package com.hpl.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.mapper.CategoryMapper;
import com.hpl.article.pojo.dto.CategoryTreeDTO;
import com.hpl.article.pojo.enums.CategoryLeafEnum;
import com.hpl.article.pojo.enums.PublishStatusEnum;
import com.hpl.article.service.CategoryService;
import com.hpl.article.pojo.entity.Category;
import com.hpl.redis.RedisClient;
import io.swagger.v3.oas.annotations.Operation;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private RedisClient redisClient;

    @Override
    public List<CategoryTreeDTO> getTreeCategories(String id) {
        // 获取所有的子节点
        List<CategoryTreeDTO> dto = categoryMapper.selectTreeCategories(id);

        // 定义一个List，作为最终返回的数据
        List<CategoryTreeDTO> result = new ArrayList<>();

        // 存放所有叶子结点id
        List<String> leafIds = new ArrayList<>();

        // 为了方便找子节点的父节点，这里定义一个HashMap，key是节点的id，value是节点本身
        HashMap<String, CategoryTreeDTO> nodeMap = new HashMap<>();
        // 将数据封装到List中，只包括根节点的下属节点（1-1、1-2 ···），这里遍历所有节点
        dto.forEach(item -> {
            // 这里寻找父节点的直接下属节点（1-1、1-2 ···）
            if (item.getParentId().equals(id)) {
                nodeMap.put(item.getId(), item);
                result.add(item);
            }

            // 获取每个子节点的父节点
            String parentId = item.getParentId();
            CategoryTreeDTO parentNode = nodeMap.get(parentId);
            // 判断HashMap中是否存在该父节点（按理说必定存在，以防万一）
            if (parentNode != null) {
                // 为父节点设置子节点（将1-1-1设为1-1的子节点）
                List childrenTreeNodes = parentNode.getChildrenTreeNodes();
                // 如果子节点暂时为null，则初始化一下父节点的子节点（给个空集合就行）
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<CategoryTreeDTO>());
                }
                // 将子节点设置给父节点
                parentNode.getChildrenTreeNodes().add(item);
            }

            // 如果本身是叶子结点记录下
            if (item.getIsLeaf() == CategoryLeafEnum.IS_LEAF.getCode()) {
                leafIds.add(item.getId());
            }

        });

        // 将所有叶子id 存入 根结点的缓存
        redisClient.set("category-leafIds:" + id, leafIds, 60*60*24*7L, TimeUnit.SECONDS);

        // 返回根节点的直接下属节点（1、2 ···）
        return result;
    }

    /**
     * 根据某分类获取叶子节点
     *
     * @param categoryTreeDTO
     * @return
     */
    @Override
    public List<String> getLeafIds(CategoryTreeDTO categoryTreeDTO){
        List<String> listIsd = redisClient.getList("category-leafIds:" + categoryTreeDTO.getId(), String.class);
        if(listIsd!=null){
            return listIsd;
        }

        List<String> leafIds = new ArrayList<>();
        dfsGetLeafIds(categoryTreeDTO,leafIds);
        redisClient.set("category-leafIds:" + categoryTreeDTO.getId(), new ArrayList<>(leafIds), 60*60*24*7L, TimeUnit.SECONDS);
        return leafIds;
    }

    // 递归获取叶子节点ID
    private void dfsGetLeafIds(CategoryTreeDTO categoryTreeDTO, List<String> leafIds){
        // 如果本身为叶子结点
        if(categoryTreeDTO.getIsLeaf()== CategoryLeafEnum.IS_LEAF.getCode()){
            leafIds.add(categoryTreeDTO.getId());
            return;
        }

        for(CategoryTreeDTO child: categoryTreeDTO.getChildrenTreeNodes()){
            dfsGetLeafIds(child,leafIds);
        }
    }

    @Override
    public List<Category> getAllLeafs() {
        List<Category> res = redisClient.getList("category-leafs", Category.class);
        if (res != null) {
            return res;
        }

        res = lambdaQuery()
                .eq(Category::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .eq(Category::getIsLeaf, CategoryLeafEnum.IS_LEAF.getCode())
                .list();

        redisClient.set("category-leafs", res, 60 * 60 * 24 * 7L, TimeUnit.SECONDS);
        return res;
    }

}
