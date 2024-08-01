package com.hpl.controller.home;


import com.hpl.controller.home.helper.IndexRecommendHelper;
import com.hpl.global.service.GlobalInitService;
import com.hpl.pojo.CommonController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author : rbe
 * @date : 2024/7/10 18:48
 */
@RestController
@Slf4j
@Tag(name = "C端登录控制器")
public class IndexController extends CommonController {

    @Autowired
    private IndexRecommendHelper indexRecommendHelper;

    @Autowired
    private GlobalInitService globalInitService;

    @Operation(summary = "首页")
    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(HttpServletRequest request) {
        log.warn("等你走进来就知道了");
        String activeTab = request.getParameter("category");
//        model.addAttribute("global", globalInitService.globalAttr());
        log.warn("global   :{}",globalInitService.globalAttr());
//        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
//        model.addAttribute("vo", vo);
//        log.warn("vo:   {}",vo);
        log.warn("lalalalala");
//        return "views/home/index";
        return "请用前端工程访问！";
    }
}
