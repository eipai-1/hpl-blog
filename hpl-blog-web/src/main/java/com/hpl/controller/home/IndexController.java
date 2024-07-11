package com.hpl.controller.home;

import com.hpl.controller.home.helper.IndexRecommendHelper;
import com.hpl.controller.home.vo.IndexVo;
import com.hpl.global.service.GlobalInitService;
import com.hpl.pojo.CommonController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * @author : rbe
 * @date : 2024/7/10 18:48
 */
@Controller
@Slf4j
public class IndexController extends CommonController {

    @Autowired
    private IndexRecommendHelper indexRecommendHelper;

    @Autowired
    private GlobalInitService globalInitService;

    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        log.warn("等你走进来就知道了");
        String activeTab = request.getParameter("category");
        model.addAttribute("global", globalInitService.globalAttr());
        log.warn("global   :{}",globalInitService.globalAttr());
        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
        model.addAttribute("vo", vo);
//        log.warn("vo:   {}",vo);
        log.warn("lalalalala");
        return "views/home/index";

    }
}
