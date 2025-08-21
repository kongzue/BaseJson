package com.kongzue.baseokhttp.util;

import com.kongzue.baseokhttp.util.util.JsonBeanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/8/19 15:16
 */
@Deprecated
public class JsonBean<T> {

    /**
     * 由 JsonMap 转换为 JavaBean
     *
     * @param jsonData  JsonMap 数据
     * @param beanClass 要转换的 JavaBean Class
     * @return 实例化的 JavaBean
     */
    public static <T> T getBean(JsonMap jsonData, Class<T> beanClass) {
        return JsonBeanUtil.fromJsonMap(jsonData, beanClass, JsonBeanUtil.Mode.METHOD);
    }

    /**
     * 由 JavaBean 转换为 JsonMap
     *
     * @param bean JavaBean 的实例化对象
     * @return JsonMap
     */
    public static JsonMap setBean(Object bean) {
        return JsonBeanUtil.toJsonMap(bean, JsonBeanUtil.Mode.METHOD);
    }
}