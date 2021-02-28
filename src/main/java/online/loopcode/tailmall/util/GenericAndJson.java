/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-03-03 21:05
 */
package online.loopcode.tailmall.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.loopcode.tailmall.exception.http.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericAndJson {

    private static ObjectMapper mapper;

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        GenericAndJson.mapper = mapper;
    }

    /**
     * 序列化
     * @param o
     * @param <T>
     * @return
     */
    public static <T> String objectToJson(T o) {
        try {
            return GenericAndJson.mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }

    /**
     * 反序列化
     * @param s
     * @param tr
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String s,  TypeReference<T> tr) {
        if (s == null) {
            return null;
        }
        try {
            T o = GenericAndJson.mapper.readValue(s, tr);
            return o;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }
    }


//    public static <T> List<T> jsonToList(String s ) {
//        if (s == null) {
//            return null;
//        }
//        try {
////            T.class
//            List<T> list = GenericAndJson.mapper.readValue(s, new TypeReference<List<T>>() {
//            });
//            return list;
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            throw new ServerErrorException(9999);
//        }
//    }

//    public static <T> T jsonToList(String s, TypeReference<T> tr) {
//        if (s == null) {
//            return null;
//        }
//        try {
//            T list = GenericAndJson.mapper.readValue(s, tr);
//            return list;
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            throw new ServerErrorException(9999);
//        }
//    }
}

