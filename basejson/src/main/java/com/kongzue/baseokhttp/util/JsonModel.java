package com.kongzue.baseokhttp.util;

import android.util.Log;

import com.kongzue.baseokhttp.util.interfaces.JsonValue;

import java.lang.reflect.Field;
import java.util.Map;

public class JsonModel extends JsonMap {

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
    public void onCreate() {
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
                } else if (JsonModel.class.isAssignableFrom(type)) {
                    try {
                        JsonModel nested = (JsonModel) type.getDeclaredConstructor().newInstance();
                        JsonMap source = getJsonMap(key);
                        nested.copyFrom(source);
                        value = nested;
                    } catch (Exception e) {
                        if (e instanceof NoSuchMethodException) {
                            Log.e(">>>", "JsonModel 初始化时失败！" + type + "没有空参构造方法或不是非静态的内部类。");
                        }
                        e.printStackTrace();
                    }
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

    public static <T> T parse(Class<T> targetClass, JsonMap data) {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            for (Field field : targetClass.getDeclaredFields()) {
                JsonValue annotation = field.getAnnotation(JsonValue.class);
                if (annotation == null) continue;

                String key = annotation.value();
                Class<?> type = field.getType();
                Object value = null;

                if (type == String.class) {
                    value = data.getString(key);
                } else if (type == int.class || type == Integer.class) {
                    value = data.getInt(key);
                } else if (type == boolean.class || type == Boolean.class) {
                    value = data.getBoolean(key);
                } else if (type == double.class || type == Double.class) {
                    value = data.getDouble(key);
                } else if (type == long.class || type == Long.class) {
                    value = data.getLong(key);
                } else if (type == short.class || type == Short.class) {
                    value = data.getShort(key);
                } else if (type == float.class || type == Float.class) {
                    value = data.getFloat(key);
                } else if (type == JsonMap.class) {
                    value = data.getJsonMap(key);
                } else if (type == JsonList.class) {
                    value = data.getList(key);
                } else if (JsonModel.class.isAssignableFrom(type)) {
                    try {
                        JsonModel nested = (JsonModel) type.getDeclaredConstructor().newInstance();
                        JsonMap source = data.getJsonMap(key);
                        nested.copyFrom(source);
                        value = nested;
                    } catch (Exception e) {
                        if (e instanceof NoSuchMethodException) {
                            Log.e(">>>", "JsonModel 初始化时失败！" + type + "没有空参构造方法或不是非静态的内部类。");
                        }
                        e.printStackTrace();
                    }
                } else {
                    JsonMap source = data.getJsonMap(key);
                    value = parse(type, source);
                }

                if (value != null) {
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("解析失败: " + targetClass.getName(), e);
        }
    }
}
