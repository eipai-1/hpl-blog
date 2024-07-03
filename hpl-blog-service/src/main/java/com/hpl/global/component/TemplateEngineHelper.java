package com.hpl.global.component;

import com.hpl.global.service.GlobalInitService;
import com.hpl.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * @author : rbe
 * @date : 2024/6/30 11:11
 */
@Component
public class TemplateEngineHelper {

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private GlobalInitService globalInitService;

    /**
     * 渲染模板。
     * 使用给定的模板和属性值，生成最终的字符串输出。
     * 此方法的核心是通过模板引擎，将模板和数据结合，产生指定格式的输出。
     *
     * @param template 模板字符串，包含占位符，用于最终的渲染。
     * @param attrName 属性名称，这个属性将会在模板中被替换或使用。
     * @param attrVal 属性值，对应于attrName，用于填充模板。
     * @param <T> 属性值的泛型类型，允许此方法处理多种类型的属性值。
     * @return 渲染后的字符串，即模板与属性值结合后的结果。
     */
    public <T> String render(String template, String attrName, T attrVal) {

        // 创建上下文对象，用于存储模板渲染过程中所需的数据。
        Context context = new Context();

        // 设置模板渲染中使用的变量，这里将属性名和属性值绑定。
        context.setVariable(attrName, attrVal);

        // 设置全局变量，这个变量可以在任何模板中访问。
        context.setVariable("global", globalInitService.globalAttr());

        // 使用SpringTemplateEngine处理模板，传入上下文对象，返回渲染后的结果。
        return springTemplateEngine.process(template, context);
    }


    public <T> String render(String template, T attr) {
        // 调用重载的render方法，传入模板、一个标识符和属性对象，进行渲染
        return render(template, "vo", attr);
    }

    /**
     * 模板渲染，传参属性放在vo包装类下
     *
     * @param template 模板
     * @param second   实际的data属性
     * @param val      传参
     * @param <T>
     * @return
     */
    public <T> String renderToVo(String template, String second, T val) {
        Context context = new Context();
        context.setVariable("vo", MapUtil.create(second, val));
        return springTemplateEngine.process(template, context);
    }
}