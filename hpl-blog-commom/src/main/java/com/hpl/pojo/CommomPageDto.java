package com.hpl.pojo;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/6/30 17:17
 */
@Data
public class CommomPageDto {
    private static final Long DEFAULT_PAGE_NUM = 1L;
    private static final Long DEFAULT_PAGE_SIZE = 10L;
    private static final Long TOP_PAGE_SIZE = 4L;

    private long pageNum;

    private long pageSize;

    private long offset;

    private long limit;

    public static CommomPageDto newInstance(){
        return newInstance(DEFAULT_PAGE_NUM,DEFAULT_PAGE_SIZE);
    }

    public static CommomPageDto newInstance(Integer pageNum,Integer pageSize){
        return newInstance(pageNum.longValue(),pageSize.longValue());
    }

    /**
     * 创建一个新的CommonPageDto实例，用于分页查询。
     *
     * @param pageNum  查询的页码，如果为null或小于等于0，则使用默认页码。
     * @param pageSize 每页的大小，如果为null或小于等于0或大于默认最大每页大小，则使用默认每页大小。
     * @return 返回一个新的CommonPageDto实例，其中包含了根据输入参数计算得到的页码、每页大小、偏移量和限制。
     */
    public static CommomPageDto newInstance(Long pageNum,Long pageSize){
        // 检查并修正页码，确保其为有效值
        if(pageNum == null || pageNum <= 0){
            pageNum = DEFAULT_PAGE_NUM;
        }

        // 检查并修正每页大小，确保其为有效值
        if(pageSize == null || pageSize <= 0 || pageSize > DEFAULT_PAGE_SIZE){
            pageSize = DEFAULT_PAGE_SIZE;
        }

        // 创建一个新的CommonPageDto实例
        final CommomPageDto commomPageDto = new CommomPageDto();
        // 设置实例的页码和每页大小属性
        commomPageDto.pageNum=pageNum;
        commomPageDto.pageSize=pageSize;
        // 计算查询的偏移量，用于数据库查询的偏移定位
        commomPageDto.offset=(pageNum-1)*pageSize;
        // 设置查询的限制，即每页的大小
        commomPageDto.limit=pageSize;

        // 返回构造完成的CommonPageDto实例
        return commomPageDto;
    }

    /**
     * 根据分页参数生成SQL语句中的limit子句。
     * <p>
     * 该方法用于根据提供的CommomPageDto对象，格式化生成SQL查询语句中用于分页的limit子句。
     * limit子句用于限制查询结果的数量，通过指定开始位置（偏移量）和每页的最大记录数来实现。
     *
     * @param commomPageDto 分页参数对象，包含偏移量和每页记录数。
     * @return 返回格式化后的limit子句字符串。
     */
    public static String getLimitSql(CommomPageDto commomPageDto) {
        // 使用String.format方法格式化生成limit子句，其中%s会被替换为commomPageDto对象的offset和limit属性值
        return String.format("limit %s,%s", commomPageDto.offset, commomPageDto.limit);
    }

}
