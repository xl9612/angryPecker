package com.definesys.angrypecker.util.common;

import java.util.*;

/**
 * 字符串的一个自定义工具方法
 */
public class DragonStringUtils {

    /**
     * List<Map>转Map(得到两个字符串)
     * @param list
     * @return
     */
    public static Map listToString(List<Map> list){
        Map map = null;
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            map = list.get(i);
            if (!ValidateUtils.checkIsNull(map.get("name")+"")){
                nameBuilder.append(map.get("name"));
            }
            if (!ValidateUtils.checkIsNull(map.get("url")+"")){
                urlBuilder.append(map.get("url"));
            }
            if (i != list.size() - 1 ) {
                nameBuilder.append(",");
                urlBuilder.append(",");
            }
        }
        map = new HashMap();
        map.put("usernames",nameBuilder.toString());
        map.put("urls",urlBuilder.toString());

        return map;

    }

    /**
     * 自定义格式字符串(两个字符串)转List
     * @return
     */
    public static List stringToList(String userString,String urlString){
        if (ValidateUtils.checkIsNull(userString) && ValidateUtils.checkIsNull(urlString)){
            return null;
        }else if (ValidateUtils.checkIsNull(userString)){
            userString = "";
        }else if (ValidateUtils.checkIsNull(urlString)){
            urlString = "";
        }
        String[] usernames = userString.split(",");
        String[] urls = urlString.split(",");
        int frequency = 0;
        List data = new ArrayList();

        if (urls.length>usernames.length){
            frequency = urls.length;
        }else {
            frequency = usernames.length;
        }


        Map map = null;
        for (int i = 0;i< frequency;i++){
            map = new HashMap();
            if (i < usernames.length){
                map.put("name",usernames[i]);
            }
            if (i < urls.length){
                map.put("url",urls[i]);
            }
            data.add(map);
        }

        return data;

    }

    /**
     * 将@Link Collection里面的元素遍历出一个字符串,中间用connector
     * @param collection
     * @return
     */
    public static String collectionToString(Collection collection,String connector){
        if (collection == null || collection.size() <= 0){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (Object o : collection){
            stringBuilder.append(o+connector);
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param data 数据,一串字符串
     * @param regex 要分隔的字符,如果数据里没有该字符,则把数据中每个字符进行分隔
     * @param collection 返回的数据类型,必须是Collection类
     * @return
     */
    public static Collection strToCollection(String data,String regex,String value,Collection collection){

        if (ValidateUtils.checkIsNull(data)){
            return null;
        }
        String[] split = data.split(regex);
        if (split == null || split.length <= 0){
            return null;
        }
        if (ValidateUtils.checkIsNull(value)){
            for (String str : split){
                if (!ValidateUtils.checkIsNull(str)){
                    collection.add(str);
                }

            }
        }else {
            for (String str : split){
                if (!ValidateUtils.checkIsNull(str)){
                    if (value.equals(str)){
                        collection.add(str);
                    }
                }

            }

        }

        return collection;
    }

}
