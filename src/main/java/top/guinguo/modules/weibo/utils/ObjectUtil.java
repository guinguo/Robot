package top.guinguo.modules.weibo.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对数据库查询结果数据进行简单封装
 * @author guin_uo
 * Created by guin_uo on 17-5-25.
 */
public class ObjectUtil {

    static {
        Converter dateConverter = (clz, value)->{
            if(clz != Date.class){
                return null;
            }
            try{
                if(value instanceof String){
                    return new SimpleDateFormat("yyyy-MM-dd").parse((String)value);
                }
            }catch (ParseException e){
                e.printStackTrace();
            }
            return null;
        };
        //注册日期转换器
        ConvertUtils.register(dateConverter, Date.class);
        ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
    }

    public static <T> T getObject(ResultSet rs, Class<T> clz){
        T obj = null;
        try {
            obj = clz.newInstance();
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields){
                try {
                    int index = rs.findColumn(field.getName());
                    if (field.getType().equals(JSONObject.class)) {
                        BeanUtils.copyProperty(obj, field.getName(), JSONObject.parseObject(rs.getObject(index).toString()));
                        continue;
                    }
                    BeanUtils.copyProperty(obj, field.getName(),rs.getObject(index));
                }catch (SQLException ex){
                    continue;
                } catch (InvocationTargetException e) {
                    continue;
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
