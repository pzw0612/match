package com.viewfin.match.core.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/12 05:21
 * @Description:
 */
public class MapUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MapUtils.class);

    public static Map<String, Object> toMap(Object obj) {

        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }

            }
        } catch (Exception e) {
            LOGGER.error("toMap Error " ,e);
        }

        return map;

    }


    public static void toBean(Map<String, Object> map, Object obj) {

        if(map==null){
            obj=null;
            return;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (key.equals("class")) {
                    continue;
                }
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }

            }

        } catch (Exception e) {
            LOGGER.error("toBean Error ,map={}",map);

            obj =null;
        }

        return;

    }

}
