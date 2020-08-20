package com.kongzue.baseokhttp.util;

import org.json.JSONArray;

import java.util.List;
import static com.kongzue.baseokhttp.util.JsonMap.preParsing;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/16 18:16
 */
public class JsonList extends SimpleArrayList {
    
    /**
     * 创建一个空的 JsonList 对象
     */
    public JsonList() {
    
    }
    
    /**
     * 通过 Json 文本创建 JsonList 对象
     *
     * @param jsonStr Json 文本
     */
    public JsonList(String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            grow(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                String o = String.valueOf(jsonArray.get(i));
                if (preParsing) {
                    if (o.startsWith("{") && o.endsWith("}")) {
                        JsonMap value = new JsonMap(o);
                        set(value.isEmpty() ? o : value);
                    } else if (o.startsWith("[") && o.endsWith("]")) {
                        JsonList value = new JsonList(o);
                        set(value.isEmpty() ? o : value);
                    } else {
                        set(o);
                    }
                } else {
                    set(o);
                }
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * 通过 List 创建 JsonList 对象
     *
     * @param list List 实例化对象
     */
    public JsonList(List list) {
        for (Object value : list) {
            set(value);
        }
    }
    
    /**
     * 通过 Json 文本静态方法创建 JsonList 对象
     *
     * @param jsonObjString Json 文本
     * @return 创建的 JsonList 对象
     */
    public static JsonList parse(String jsonObjString) {
        return new JsonList(jsonObjString);
    }
    
    public String getString(int index) {
        return getString(index, "");
    }
    
    public String getString(int index, String defaultValue) {
        Object value = get(index);
        if (isNull(String.valueOf(value))) {
            return defaultValue;
        }
        return value == null ? "" : String.valueOf(value);
    }
    
    public int getInt(int index) {
        return getInt(index, 0);
    }
    
    public int getInt(int index, int emptyValue) {
        int result = emptyValue;
        try {
            result = Integer.parseInt(get(index) + "");
        } catch (Exception e) {
        }
        return result;
    }
    
    public boolean getBoolean(int index) {
        return getBoolean(index, false);
    }
    
    public boolean getBoolean(int index, boolean emptyValue) {
        boolean result = emptyValue;
        try {
            result = Boolean.parseBoolean(get(index) + "");
        } catch (Exception e) {
        }
        return result;
    }
    
    public long getLong(int index) {
        return getLong(index, 0);
    }
    
    public long getLong(int index, long emptyValue) {
        long result = emptyValue;
        try {
            result = Long.parseLong(get(index) + "");
        } catch (Exception e) {
        }
        return result;
    }
    
    public short getShort(int index) {
        return getShort(index, (short) 0);
    }
    
    public short getShort(int index, short emptyValue) {
        short result = emptyValue;
        try {
            result = Short.parseShort(get(index) + "");
        } catch (Exception e) {
        }
        return result;
    }
    
    public double getDouble(int index) {
        return getDouble(index, 0);
    }
    
    public double getDouble(int index, double emptyValue) {
        double result = emptyValue;
        try {
            result = Double.parseDouble(get(index) + "");
        } catch (Exception e) {
        }
        return result;
    }
    
    public float getFloat(int index) {
        return getFloat(index, 0);
    }
    
    public float getFloat(int index, float emptyValue) {
        float result = emptyValue;
        try {
            result = Float.parseFloat(get(index) + "");
        } catch (Exception e) {
        }
        return emptyValue;
    }
    
    public JsonList getList(int index) {
        Object value = get(index);
        try {
            return value == null ? new JsonList() : new JsonList(String.valueOf(value));
        } catch (Exception e) {
            return new JsonList();
        }
    }
    
    public JsonMap getJsonMap(int index) {
        Object value = get(index);
        try {
            return value == null ? new JsonMap() : new JsonMap(String.valueOf(value));
        } catch (Exception e) {
            return new JsonMap();
        }
    }
    
    public JsonList set(Object value) {
        super.add(value);
        return this;
    }
    
    /**
     * 输出 Json 文本
     *
     * @return Json 文本
     */
    @Override
    public String toString() {
        return getJsonArray().toString();
    }
    
    public JSONArray getJsonArray() {
        JSONArray main = null;
        try {
            main = new JSONArray();
            
            for (int i = 0; i < size(); i++) {
                Object item = get(i);
                if (item instanceof JsonMap) {
                    main.put(((JsonMap) item).getJsonObj());
                } else if (item instanceof JsonList) {
                    main.put(((JsonList) item).getJsonArray());
                } else {
                    main.put(item);
                }
            }
            
        } catch (Exception e) {
        
        }
        return main;
    }
    
    private boolean isNull(String s) {
        if (s == null || s.trim().isEmpty() || "null".equals(s)) {
            return true;
        }
        return false;
    }
}
