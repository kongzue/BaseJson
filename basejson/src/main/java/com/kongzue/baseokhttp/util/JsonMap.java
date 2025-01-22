package com.kongzue.baseokhttp.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/16 17:57
 */
public class JsonMap extends LinkedHashMap<String, Object> {

    /**
     * 预解析开关
     * <p>
     * 开启此功能将在通过 String 文本创建 JsonMap/JsonList 的一开始就对所有内容进行逐级解析，
     * 可以有效增加解析速度，例如在网络请求的异步线程先解析后返回主线程即可直接读取内部的值。
     * 但这有可能带来一些问题：
     * 例如对于文本 "objData":"{sdf123R.4:98}"，该值可能在预解析时由于 org.json 的影响，
     * 被认为是一个 JsonObject，此时若 .getString("objData")则可能返回错误的结果：
     * {"sdf123R.4":"98"}
     * 值的内容被添加了引号，这显然不符合预期。
     * 关闭预解析开关可以避免此问题，但也会带来解析过程性能的损失。
     */
    public static boolean preParsing = true;

    public boolean privateParsing = true;

    /**
     * 创建一个空的 JsonMap 对象
     */
    public JsonMap() {
        privateParsing = preParsing;
    }

    public JsonMap(boolean preParsing) {
        privateParsing = preParsing;
    }

    /**
     * 通过 Json 文本创建 JsonMap 对象
     *
     * @param jsonStr Json 文本
     */
    public JsonMap(String jsonStr) {
        privateParsing = preParsing;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next() + "";
                String value = jsonObject.optString(key);
                if (privateParsing) {
                    if (value.startsWith("{") && value.endsWith("}")) {
                        JsonMap object = new JsonMap(value);
                        object.setParentJsonMap(this);
                        put(key, object.isEmpty() ? value : object);
                    } else if (value.startsWith("[") && value.endsWith("]")) {
                        JsonList array = new JsonList(value);
                        array.setParentJsonMap(this);
                        put(key, array.isEmpty() ? value : array);
                    } else {
                        put(key, value);
                    }
                } else {
                    put(key, value);
                }
            }
            if (isEmpty()) {
                onEmpty(this, null);
            } else {
                onSuccess(this);
            }
        } catch (Exception e) {
            onEmpty(this, e);
        }
    }

    public JsonMap(String jsonStr, boolean preParsing) {
        privateParsing = preParsing;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next() + "";
                String value = jsonObject.optString(key);
                if (privateParsing) {
                    if (value.startsWith("{") && value.endsWith("}")) {
                        JsonMap object = new JsonMap(value);
                        put(key, object.isEmpty() ? value : object);
                    } else if (value.startsWith("[") && value.endsWith("]")) {
                        JsonList array = new JsonList(value);
                        put(key, array.isEmpty() ? value : array);
                    } else {
                        put(key, value);
                    }
                } else {
                    put(key, value);
                }
            }
            if (isEmpty()) {
                onEmpty(this, null);
            } else {
                onSuccess(this);
            }
        } catch (Exception e) {
            onEmpty(this, e);
        }
    }

    /**
     * 通过 Map 创建 JsonMap 对象
     *
     * @param map Map 实例化对象
     */
    public JsonMap(Map map) {
        privateParsing = preParsing;
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Object value = map.get(key);
            set(key, value);
        }
    }

    public JsonMap(Map map, boolean preParsing) {
        privateParsing = preParsing;
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Object value = map.get(key);
            set(key, value);
        }
    }

    /**
     * 通过 Json 文本静态方法创建 JsonMap 对象
     *
     * @param jsonObjString Json 文本
     * @return 创建的 JsonMap 对象
     */
    public static JsonMap parse(String jsonObjString) {
        return new JsonMap(jsonObjString);
    }

    public static JsonMap parse(String jsonObjString, boolean preParsing) {
        return new JsonMap(jsonObjString, preParsing);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object value = get(key);
        if (isNull(String.valueOf(value))) {
            return defaultValue;
        }
        return value == null ? "" : String.valueOf(value);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int emptyValue) {
        int result = emptyValue;
        try {
            return Integer.parseInt(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean emptyValue) {
        boolean result = emptyValue;
        try {
            return Boolean.parseBoolean(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long emptyValue) {
        long result = emptyValue;
        try {
            return Long.parseLong(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short emptyValue) {
        short result = emptyValue;
        try {
            return Short.parseShort(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double emptyValue) {
        double result = emptyValue;
        try {
            return Double.parseDouble(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float emptyValue) {
        float result = emptyValue;
        try {
            return Float.parseFloat(get(key) + "");
        } catch (Exception e) {
        }
        return result;
    }

    public JsonList getList(String key) {
        Object value = get(key);
        try {
            return value == null ? new JsonList().preBuild(key, this) : (value instanceof JsonList ? (JsonList) value : new JsonList(String.valueOf(value)).setParentJsonMap(this));
        } catch (Exception e) {
            return new JsonList().preBuild(key, this);
        }
    }

    public JsonMap getJsonMap(String key) {
        Object value = get(key);
        try {
            return value == null ? new JsonMap().preBuild(key, this) : (value instanceof JsonMap ? (JsonMap) value : new JsonMap(String.valueOf(value)).setParentJsonMap(this));
        } catch (Exception e) {
            return new JsonMap().preBuild(key, this);
        }
    }

    public JsonMap set(String key, Object value) {
        callParentRelease();
        put(key, value);
        return this;
    }

    @Override
    public Object put(String key, Object value) {
        callParentRelease();
        if (value == null) {
            value = "";
        } else {
            if (value instanceof JsonMap) {
                ((JsonMap) value).setParentJsonMap(this);
            }
            if (value instanceof JsonList) {
                ((JsonList) value).setParentJsonMap(this);
            }
        }
        return super.put(key, value);
    }

    /**
     * 输出 Json 文本
     *
     * @return Json 文本
     */
    @Override
    public String toString() {
        return getJsonObj().toString();
    }

    public JSONObject getJsonObj() {
        JSONObject main = null;
        try {
            main = new JSONObject();

            Set<String> keys = keySet();
            for (String key : keys) {
                Object value = get(key);
                if (value instanceof JsonMap) {
                    main.put(key, ((JsonMap) value).getJsonObj());
                } else if (value instanceof JsonList) {
                    main.put(key, ((JsonList) value).getJsonArray());
                } else if (value instanceof List) {
                    main.put(key, getListValue((List) value));
                } else {
                    main.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return main;
    }

    private JSONArray getListValue(List valueList) {
        JSONArray array = new JSONArray();
        if (valueList != null) {
            for (int i = 0; i < valueList.size(); i++) {
                Object value = valueList.get(i);
                if (value instanceof Serializable) {
                    array.put(value);
                }
            }
        } else {
            return null;
        }
        return array;
    }

    private boolean isNull(String s) {
        if (s == null || s.trim().isEmpty() || "null".equals(s)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

    public boolean equals(String key, Object value) {
        return get(key) == value;
    }

    private boolean preCreated = false;
    private String preBuildKey;
    private JsonMap parentJsonMap;
    private int preBuildIndex;
    private JsonList parentJsonList;

    private void callParentRelease() {
        if (preCreated) {
            return;
        }
        if (parentJsonMap != null && preBuildKey != null) {
            if (parentJsonMap.get(preBuildKey) != this) {
                parentJsonMap.set(preBuildKey, this);
            }
            preCreated = true;
        }
        if (parentJsonList != null) {
            if (!parentJsonList.contains(this)) {
                if (preBuildIndex >= 0) {
                    parentJsonList.set(preBuildIndex, this);
                } else {
                    parentJsonList.set(this);
                }
            }
            preCreated = true;
        }
    }

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonMap preBuild(String key, JsonMap parentJsonMap) {
        this.preBuildKey = key;
        this.parentJsonMap = parentJsonMap;
        return this;
    }

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonMap preBuild(int preBuildIndex, JsonList parentJsonList) {
        this.preBuildIndex = preBuildIndex;
        this.parentJsonList = parentJsonList;
        return this;
    }

    // 构建中用于复写的成功状态
    public void onSuccess(JsonMap thisJson) {

    }

    // 构建中用于复写的空/异常内容状态
    public void onEmpty(JsonMap thisJson, Exception e) {

    }

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonMap setParentJsonMap(JsonMap parentJsonMap) {
        this.parentJsonMap = parentJsonMap;
        return this;
    }

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonMap setParentJsonList(JsonList parentJsonList) {
        this.parentJsonList = parentJsonList;
        return this;
    }

    public JsonMap getParentJsonMap() {
        return parentJsonMap == null ? new JsonMap() : parentJsonMap;
    }

    public JsonList getParentJsonList() {
        return parentJsonList == null ? new JsonList() : parentJsonList;
    }

    public void cleanParent() {
        parentJsonMap = null;
        parentJsonList = null;
        preCreated = false;
    }
}
