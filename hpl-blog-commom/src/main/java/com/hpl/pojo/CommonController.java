package com.hpl.pojo;

/**
 * @author : rbe
 * @date : 2024/6/30 17:04
 */
public class CommonController {

    /**
     * @param pageNum 请求的页码，如果小于等于0，则使用默认页码。
     * @param pageSize 每页的大小，如果为null或大于默认最大值，则使用默认每页大小。
     * @return 返回一个新的分页参数对象，包含校验和设置后的页码和每页大小。
     */
    public CommonPageParam buildPageParam(Long pageNum, Long pageSize) {

        // 创建并返回新的分页参数实例，包含校验后的页码和每页大小
        return CommonPageParam.newInstance(pageNum, pageSize);
    }

//  推荐使用它替代 GlobalViewInterceptor 中的全局属性设置
//    /**
//     * 全局属性配置
//     *
//     * @param model
//     */
//    @ModelAttribute
//    public void globalAttr(Model model) {
//        model.addAttribute("global", globalInitService.globalAttr());
//    }
}
