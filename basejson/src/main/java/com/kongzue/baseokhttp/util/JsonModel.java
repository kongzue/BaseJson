package com.kongzue.baseokhttp.util;

import com.kongzue.baseokhttp.util.interfaces.JsonValue;

import java.lang.reflect.Field;
import java.util.Map;

public class JsonModel extends JsonMap{

    public JsonModel() {
    }

    public JsonModel(boolean preParsing) {
        super(preParsing);
    }

    public JsonModel(String jsonStr) {
        super(jsonStr);
    }

    public JsonModel(String jsonStr, boolean preParsing) {
        super(jsonStr, preParsing);
    }

    public JsonModel(Map map) {
        super(map);
    }

    public JsonModel(Map map, boolean preParsing) {
        super(map, preParsing);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            JsonValue annotation = field.getAnnotation(JsonValue.class);
            if (annotation != null) {
                String key = annotation.value();
                Object value = null;

                Class<?> type = field.getType();
                if (type == String.class) {
                    value = getString(key);
                } else if (type == int.class || type == Integer.class) {
                    value = getInt(key);
                } else if (type == boolean.class || type == Boolean.class) {
                    value = getBoolean(key);
                } else if (type == double.class || type == Double.class) {
                    value = getDouble(key);
                } else if (type == long.class || type == Long.class) {
                    value = getLong(key);
                } else if (type == short.class || type == Short.class) {
                    value = getShort(key);
                } else if (type == float.class || type == Float.class) {
                    value = getFloat(key);
                } else if (type == JsonMap.class) {
                    value = getJsonMap(key);
                } else if (type == JsonList.class) {
                    value = getList(key);
                }

                if (value != null) {
                    try {
                        field.setAccessible(true);
                        field.set(this, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
