package com.neo.types.sdk.weixin;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtil {
    private static final XmlMapper xmlMapper = new XmlMapper();

    /**
     * bean转成微信的xml消息格式
     */
    public static String beanToXml(Object object) {
        try {
            return xmlMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * xml转成bean泛型方法
     */
    public static <T> T xmlToBean(String resultXml, Class clazz) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            return (T) xmlMapper.readValue(resultXml, clazz);
        } catch (Exception e) {
            return null;
        }
    }

}