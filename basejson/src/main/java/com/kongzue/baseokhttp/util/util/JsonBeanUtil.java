package com.kongzue.baseokhttp.util.util;

import android.util.Log;

import com.kongzue.baseokhttp.util.JsonList;
import com.kongzue.baseokhttp.util.JsonMap;
import com.kongzue.baseokhttp.util.interfaces.JsonValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class JsonBeanUtil {

    public enum Mode {
        METHOD, ANNOTATION
    }

    /**
     * Bean 转 Map
     */
    public static JsonMap toJsonMap(Object bean, Mode mode) {
        if (bean == null) return null;
        JsonMap result = new JsonMap();

        if (mode == Mode.METHOD) {
            // 通过 getter 方法
            for (Method method : bean.getClass().getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.startsWith("get") || methodName.startsWith("is")) {
                    String attributeName = methodName;
                    if (methodName.startsWith("get")) {
                        if (attributeName.length() > 4) {
                            attributeName = methodName.substring(3);
                            attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                        }
                    } else if (methodName.startsWith("is")) {
                        if (attributeName.length() > 3) {
                            attributeName = methodName.substring(2);
                            attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                        }
                    }
                    try {
                        Object returnValue = method.invoke(bean);
                        if (returnValue != null) {
                            if (returnValue instanceof String ||
                                    returnValue instanceof Integer ||
                                    returnValue instanceof Boolean ||
                                    returnValue instanceof Double ||
                                    returnValue instanceof Float ||
                                    returnValue instanceof Long ||
                                    returnValue instanceof Short
                            ) {
                                result.set(attributeName, returnValue);
                            } else if (returnValue instanceof List) {
                                JsonList list = addListValue2JsonList(((List) returnValue), mode);
                                result.set(attributeName, list);
                            } else if (returnValue.getClass().isArray()) {
                                JsonList list = addArrayValue2JsonList(returnValue, mode);
                                result.set(attributeName, list);
                            } else {
                                JsonMap value = toJsonMap(returnValue, Mode.METHOD);
                                result.set(attributeName, value);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (mode == Mode.ANNOTATION) {
            // 通过注解
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonValue.class)) {
                    field.setAccessible(true);
                    JsonValue ann = field.getAnnotation(JsonValue.class);
                    try {
                        Object returnValue = field.get(bean);
                        if (returnValue != null) {
                            if (returnValue instanceof String ||
                                    returnValue instanceof Integer ||
                                    returnValue instanceof Boolean ||
                                    returnValue instanceof Double ||
                                    returnValue instanceof Float ||
                                    returnValue instanceof Long ||
                                    returnValue instanceof Short
                            ) {
                                result.put(ann.value(), returnValue);
                            } else if (returnValue instanceof List) {
                                JsonList list = addListValue2JsonList(((List) returnValue), mode);
                                result.set(ann.value(), list);
                            } else if (returnValue.getClass().isArray()) {
                                JsonList list = addArrayValue2JsonList(returnValue, mode);
                                result.set(ann.value(), list);
                            } else {
                                JsonMap value = toJsonMap(returnValue, Mode.ANNOTATION);
                                result.set(ann.value(), value);
                            }
                        }

                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    private static JsonList addListValue2JsonList(List returnValue, Mode mode) {
        JsonList list = new JsonList();
        for (Object child : (returnValue)) {
            if (child instanceof String ||
                    child instanceof Integer ||
                    child instanceof Boolean ||
                    child instanceof Double ||
                    child instanceof Float ||
                    child instanceof Long ||
                    child instanceof Short
            ) {
                list.add(child);
            } else if (child instanceof List) {
                JsonList childList = addListValue2JsonList((List) child, mode);
                list.add(childList);
            } else {
                JsonMap value = toJsonMap(child, mode);
                list.add(value);
            }
        }
        return list;
    }

    private static JsonList addArrayValue2JsonList(Object returnValue, Mode mode) {
        JsonList list = new JsonList();
        Class<?> componentType = returnValue.getClass().getComponentType();
        if (componentType != null) {
            if (componentType == int.class) {
                int[] arr = (int[]) returnValue;
                for (int child : arr) {
                    list.add(child);
                }
            } else if (componentType == String.class) {
                String[] arr = (String[]) returnValue;
                for (String child : arr) {
                    list.add(child);
                }
            } else if (componentType == double.class) {
                double[] arr = (double[]) returnValue;
                for (double child : arr) {
                    list.add(child);
                }
            } else if (componentType == boolean.class) {
                boolean[] arr = (boolean[]) returnValue;
                for (boolean child : arr) {
                    list.add(child);
                }
            } else if (componentType == float.class) {
                float[] arr = (float[]) returnValue;
                for (float child : arr) {
                    list.add(child);
                }
            } else if (componentType == long.class) {
                long[] arr = (long[]) returnValue;
                for (long child : arr) {
                    list.add(child);
                }
            } else if (componentType == short.class) {
                short[] arr = (short[]) returnValue;
                for (short child : arr) {
                    list.add(child);
                }
            } else {
                int length = java.lang.reflect.Array.getLength(returnValue);
                for (int i = 0; i < length; i++) {
                    Object value = java.lang.reflect.Array.get(returnValue, i);
                    JsonMap jsonValue = toJsonMap(value, mode);
                    list.add(jsonValue);
                }
            }
        }
        return list;
    }

    /**
     * Map 转 Bean
     */
    public static <T> T fromJsonMap(JsonMap map, Class<T> clazz, Mode mode) {
        if (map == null) return null;
        try {
            T bean = clazz.getDeclaredConstructor().newInstance();

            if (mode == Mode.METHOD) {
                // 通过 setter 方法
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
                        String fieldName = method.getName().substring(3);
                        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                        if (map.containsKey(fieldName)) {
                            try {
                                method.invoke(bean, map.get(fieldName));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            } else if (mode == Mode.ANNOTATION) {
                // 通过注解
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(JsonValue.class)) {
                        field.setAccessible(true);
                        JsonValue annotation = field.getAnnotation(JsonValue.class);

                        if (annotation == null) continue;

                        String key = annotation.value();
                        Class<?> type = field.getType();
                        Object value = null;

                        if (type == String.class) {
                            value = map.getString(key);
                        } else if (type == int.class || type == Integer.class) {
                            value = map.getInt(key);
                        } else if (type == boolean.class || type == Boolean.class) {
                            value = map.getBoolean(key);
                        } else if (type == double.class || type == Double.class) {
                            value = map.getDouble(key);
                        } else if (type == long.class || type == Long.class) {
                            value = map.getLong(key);
                        } else if (type == short.class || type == Short.class) {
                            value = map.getShort(key);
                        } else if (type == float.class || type == Float.class) {
                            value = map.getFloat(key);
                        } else if (type == JsonMap.class) {
                            value = map.getJsonMap(key);
                        } else if (type == JsonList.class) {
                            value = map.getList(key);
                        } else {
                            JsonMap source = map.getJsonMap(key);
                            value = fromJsonMap(source, type, Mode.ANNOTATION);
                        }

                        if (value != null) {
                            field.setAccessible(true);
                            field.set(bean, value);
                        }
                    }
                }
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}