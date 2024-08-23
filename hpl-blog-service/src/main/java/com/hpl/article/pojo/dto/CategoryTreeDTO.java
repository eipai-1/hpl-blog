package com.hpl.article.pojo.dto;

import com.hpl.article.pojo.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/20 10:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeDTO extends Category {
    List<CategoryTreeDTO> childrenTreeNodes;
}


