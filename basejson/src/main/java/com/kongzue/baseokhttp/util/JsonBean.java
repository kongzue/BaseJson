package com.kongzue.baseokhttp.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/8/19 15:16
 */
public class JsonBean<T> {
    
    /**
     * 由 JsonMap 转换为 JavaBean
     *
     * @param jsonData  JsonMap 数据
     * @param beanClass 要转换的 JavaBean Class
     * @return 实例化的 JavaBean
     */
    public static <T> T getBean(JsonMap jsonData, Class<T> beanClass) {
        T tInstance = null;
        try {
            tInstance = beanClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        invokeData(jsonData, tInstance, beanClass);
        return tInstance;
    }
    
    /**
     * 由 JavaBean 转换为 JsonMap
     *
     * @param bean JavaBean 的实例化对象
     * @return JsonMap
     */
    public static JsonMap setBean(Object bean) {
        JsonMap result = new JsonMap();
        Class bClass = bean.getClass();
        Method[] methods = bClass.getDeclaredMethods();
        if (methods.length == 0) {
            return result;
        }
        result = addReturnValueToJsonMap(bean);
        return result;
    }
    
    private static JsonMap addReturnValueToJsonMap(Object bean) {
        JsonMap result = new JsonMap();
        Class bClass = bean.getClass();
        Method[] methods = bClass.getDeclaredMethods();
        for (Method method : methods) {
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
                        JsonList list = addReturnValueToList(((List) returnValue));
                        result.set(attributeName, list);
                    } else {
                        JsonMap value = addReturnValueToJsonMap(returnValue);
                        result.set(attributeName, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    
    private static JsonList addReturnValueToList(List returnValue) {
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
                JsonList childList = addReturnValueToList((List) child);
                list.add(childList);
            } else {
                JsonMap value = addReturnValueToJsonMap(child);
                list.add(value);
            }
        }
        return list;
    }
    
    private static void invokeData(JsonMap data, Object o, Class tClass) {
        Object tInstance = o;
        
        Method[] methods = tClass.getDeclaredMethods();
        if (methods.length == 0) {
            return;
        }
        
        for (Method method : methods) {
            String methodName = method.getName();
            
            if (methodName.startsWith("set")) {
                String attributeName = methodName;
                if (attributeName.length() > 4) {
                    attributeName = methodName.substring(3);
                    attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                }
                
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    Class paramType = paramTypes[0];
                    
                    if (paramType != null) {
                        if (paramType.isAssignableFrom(String.class)) {
                            String value = data.getString(attributeName);
                            if (value != null) {
                                try {
                                    method.invoke(tInstance, new Object[]{value});
                                } catch (Exception e) {
                                }
                            }
                        } else if (paramType.isInstance(Integer.class) || paramType.getName().equals("int")) {
                            int value = data.getInt(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(Boolean.class) || paramType.getName().equals("boolean")) {
                            boolean value = data.getBoolean(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(Double.class) || paramType.getName().equals("double")) {
                            double value = data.getDouble(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(Float.class) || paramType.getName().equals("float")) {
                            float value = data.getFloat(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(Long.class) || paramType.getName().equals("long")) {
                            long value = data.getLong(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(Short.class) || paramType.getName().equals("short")) {
                            short value = data.getShort(attributeName);
                            try {
                                method.invoke(tInstance, new Object[]{value});
                            } catch (Exception e) {
                            }
                        } else if (paramType.isAssignableFrom(List.class) || paramType.isAssignableFrom(ArrayList.class)) {
                            JsonList attributeNameArray = data.getList(attributeName);
                            
                            List result = new ArrayList();
                            if (!attributeNameArray.isEmpty()) {
                                for (int i = 0; i < attributeNameArray.size(); i++) {
                                    JsonMap jsonObj = attributeNameArray.getJsonMap(i);
                                    if (!jsonObj.isEmpty()) {
                                        try {
                                            Field listField = tClass.getDeclaredField(attributeName);
                                            ParameterizedType listGenericType = (ParameterizedType) listField.getGenericType();
                                            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                                            
                                            Object cInstance = null;
                                            Class cClass = (Class) listActualTypeArguments[0];
                                            cInstance = cClass.newInstance();
                                            
                                            invokeData(jsonObj, cInstance, cClass);
                                            
                                            result.add(cInstance);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Object value = attributeNameArray.get(i);
                                        result.add(value);
                                    }
                                }
                                try {
                                    method.invoke(tInstance, new Object[]{result});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            JsonMap attributeNameObj = data.getJsonMap(attributeName);
                            
                            if (!attributeNameObj.isEmpty()) {
                                Object cInstance = null;
                                try {
                                    cInstance = paramType.newInstance();
                                    invokeData(attributeNameObj, cInstance, paramType);
                                    method.invoke(tInstance, new Object[]{cInstance});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}