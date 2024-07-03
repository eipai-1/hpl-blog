package com.hpl.util;

import org.springframework.http.server.ServerHttpRequest;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/6/30 13:49
 */
public class SessionUtil {

    /** 一天86400秒
     * 3*86400表示cookie默认3天过期 */
    private static final int COOKIE_EXPIRY = 3*86400;


    public static Cookie newCookie(String key, String session) {
        return newCookie(key, session,"/",COOKIE_EXPIRY);
    }

    /**
     * 创建一个新的Cookie对象，并设置其路径和过期时间。
     *
     * @param key Cookie的键。
     * @param session Cookie的值。
     * @param path Cookie的作用路径，决定cookie在哪些URL下可见。
     * @param maxExpiry Cookie的过期时间，单位为秒。
     * @return 设置好路径和过期时间的Cookie对象。
     */
    public static Cookie newCookie(String key,String session,String path,int maxExpiry){
        Cookie cookie = new Cookie(key, session);
        cookie.setPath(path);
        cookie.setMaxAge(maxExpiry);

        return cookie;
    }

    public static Cookie delCookie(String key) {
        return delCookie(key,"/");
    }

    /**
     * 删除指定名称和路径的Cookie。
     * 通过创建一个值为空、最大生存时间为0的Cookie来实现删除操作。将最大生存时间设置为0，
     * 会导致浏览器在收到这个Cookie后立即将其删除。
     *
     * @param key Cookie的名称。这是要删除的Cookie的关键标识。
     * @param path Cookie的作用路径。必须与原始Cookie设置时的路径匹配，才能成功删除。
     * @return 一个具有删除标志的Cookie对象。
     */
    public static Cookie delCookie(String key,String path){
        Cookie cookie=new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);        // 最大生存时间设置为，表示立即删除。

        return cookie;
    }

    /**
     * 根据名称从HttpServletRequest中获取Cookie。
     * 这个方法处理了Cookie数组为空或不存在具有指定名称的Cookie的情况。
     *
     * @param request HttpServletRequest对象，从中获取Cookie。
     * @param name 指定的Cookie名称。
     * @return 如果找到具有指定名称的Cookie，则返回该Cookie；否则返回null。
     */
    public static Cookie getCookieByName(HttpServletRequest request,String name){
        // 获取请求中的所有Cookie
        Cookie[] cookies=request.getCookies();

        // 检查是否没有Cookie或找不到指定名称的Cookie
        if(cookies == null || cookies.length == 0){
            return null;
        }

        // 使用流式编程过滤并获取第一个名称匹配的Cookie，如果不存在则返回null
        return Arrays.stream(cookies)
                .filter(cookie -> StringUtils.equalsAnyIgnoreCase(cookie.getName(),name))
                .findFirst().orElse(null);
    }


    /**
     * 根据名称从HTTP请求中获取cookie的值。
     * 这个方法处理了cookie字符串的分割和匹配，以找到指定名称的cookie值。
     *
     * @param request HTTP请求对象，从中获取cookie信息。
     * @param name 指定的cookie名称。
     * @return 如果找到指定名称的cookie，则返回其值；否则返回null。
     */
//    public static String getCookieByName(ServletHttpRequest request,String name){
//        // 从请求头中获取名为"cookie"的值列表
//        List<String> list=request.getHeaders().get("cookie");
//
//        // 如果cookie列表为空，则直接返回null
//        if(CollectionUtils.isEmpty(list)){
//            return null;
//        }
//
//        // 遍历cookie列表
//        for(String sub:list){
//            // 分割每个cookie字符串，以";"分隔，得到单个cookie键值对
//            String[] elements=StringUtils.split(sub,";");
//            for(String element:elements){
//                // 分割键值对，以"="分隔，获取cookie的名称和值
//                String[] subs=StringUtils.split(element,"=");
//                // 如果找到了匹配的cookie名称（不区分大小写），则返回对应的值
//                if(subs.length == 2 && StringUtils.equalsAnyIgnoreCase(subs[0].trim(),name)){
//                    return subs[1].trim();
//                }
//            }
//        }
//        // 如果遍历完所有cookie后仍未找到匹配的名称，则返回null
//        return null;
//    }


}
