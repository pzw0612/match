package com.viewfin.match.core.util;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/29
 **/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import com.viewfin.match.core.exception.ErrorInfo;
import com.viewfin.match.core.enums.StatusCodeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONUtil {
    public static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    // 定义jackson对象
    private static final ObjectMapper mapper = new ObjectMapper();
    {

        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将对象转换成json字符串
     *
     * @param data
     * @return
     */
    public static String toJSONString(Object data) {
        try {
            String string = mapper.writeValueAsString(data);
            return string;
        } catch (JsonProcessingException e) {
            logger.error("fastjson 解析出错", e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> T parseObject(String jsonData, Class<T> beanType) {
        try {
            T t = mapper.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            logger.error("fastjson 解析出错", e);
        }
        return null;
    }

    /**
     * 将json数据转换成list
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> parseArray(String jsonData, Class<T> beanType,ErrorInfo errorInfo) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = mapper.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            logger.error("fastjson 解析出错", e);
            errorInfo.setCode(StatusCodeEnum.JSON_PARSE_FAILED.getCode());
            errorInfo.setMsg(StatusCodeEnum.JSON_PARSE_FAILED.getMsg());

        }
        return null;
    }

    public static <T> T toObject(String jsonStr, TypeReference<T> valueTypeRef) {
        T object = null;

        if (StringUtils.isEmpty(jsonStr)) {
            return object;
        }

        try {
            object = mapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            logger.error("fastjson 解析出错", e);

        }

        return object;
    }

    public static <T> List<T> toList(String jsonStr,ErrorInfo errorInfo) {
        return toObject(jsonStr, new TypeReference<List<T>>() {});
    }

    public static <T> List<T> toList(String jsonStr, Class<T> clazz,ErrorInfo errorInfo) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return mapper.readValue(jsonStr, collectionType);
        } catch (Exception e) {
            logger.error("fastjson 解析出错", e);
            errorInfo.setCode(StatusCodeEnum.JSON_PARSE_FAILED.getCode());
            errorInfo.setMsg(StatusCodeEnum.JSON_PARSE_FAILED.getMsg());
        }
        return null;
    }

    public static Map<String, Object> toMap(String jsonStr) {
        return toObject(jsonStr, new TypeReference<Map<String, Object>>() {});
    }

}
