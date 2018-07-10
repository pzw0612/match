package com.viewfin.match.core.util;

import java.io.*;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/1
 **/

public class BeanUtil {

    public static <T> T populate(Map<String, Object> srcMap, Class<?> descBeanClass) throws Exception {
        if (srcMap == null){
            return null;
        }
        Object obj = descBeanClass.newInstance();
        BeanUtils.populate(obj, srcMap);

        return (T)obj;
    }

    public  static Map<?, ?> describe(Object obj) {
        if (obj == null)
            return null;
        return new BeanMap(obj);
    }

    /**
     * @param obj Object对象
     * @return byte[] 字节数组
     * @Description 对象转字节数组
     */
    public static byte[] ObjectToBytes(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oo = null;
        try {
            bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bo != null) {
                    bo.close();
                }
                if (oo != null) {
                    oo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * @param bytes 字节数组
     * @return Object Object对象
     * @Description 字节数组转对象
     * @date 2017年9月8日
     */
    public static Object BytesToObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        try {
            bi = new ByteArrayInputStream(bytes);
            oi = new ObjectInputStream(bi);
            obj = oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bi != null) {
                    bi.close();
                }
                if (oi != null) {
                    oi.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


}
