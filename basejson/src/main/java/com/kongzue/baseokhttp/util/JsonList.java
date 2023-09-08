package com.kongzue.baseokhttp.util;

import org.json.JSONArray;

import java.util.List;

import static com.kongzue.baseokhttp.util.JsonMap.preParsing;

import android.content.Context;

import com.kongzue.baseokhttp.util.adapter.JsonListAdapter;
import com.kongzue.baseokhttp.util.interfaces.JsonMapPreprocessingEvents;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/1/16 18:16
 */
public class JsonList extends SimpleArrayList {

    public boolean privateParsing = true;

    /**
     * 创建一个空的 JsonList 对象
     */
    public JsonList() {
        privateParsing = preParsing;
    }

    public JsonList(boolean preParsing) {
        privateParsing = preParsing;
    }

    /**
     * 通过 Json 文本创建 JsonList 对象
     *
     * @param jsonStr Json 文本
     */
    public JsonList(String jsonStr) {
        privateParsing = preParsing;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            grow(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                String o = String.valueOf(jsonArray.get(i));
                if (privateParsing) {
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

    public JsonList(String jsonStr, boolean preParsing) {
        privateParsing = preParsing;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            grow(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                String o = String.valueOf(jsonArray.get(i));
                if (privateParsing) {
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
        privateParsing = preParsing;
        for (Object value : list) {
            set(value);
        }
    }

    public JsonList(List list, boolean preParsing) {
        privateParsing = preParsing;
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

    public static JsonList parse(String jsonObjString, boolean preParsing) {
        return new JsonList(jsonObjString, preParsing);
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
            return value == null ? new JsonList().preBuild(index, this) : (value instanceof JsonList ? (JsonList) value : new JsonList(String.valueOf(value)));
        } catch (Exception e) {
            return new JsonList().preBuild(index, this);
        }
    }

    public JsonMap getJsonMap(int index) {
        Object value = get(index);
        try {
            return value == null ? new JsonMap().preBuild(index, this) : (value instanceof JsonMap ? (JsonMap) value : new JsonMap(String.valueOf(value)));
        } catch (Exception e) {
            return new JsonMap().preBuild(index, this);
        }
    }

    public JsonList set(Object value) {
        callParentRelease();
        super.add(value);
        return this;
    }

    public JsonList set(int index, Object value) {
        callParentRelease();
        super.set(index, value);
        return this;
    }

    public void add(int index, Object value) {
        callParentRelease();
        super.add(index, value);
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

    @Override
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

    @Override
    public Object get(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return super.get(index);
    }

    public JsonMap findJsonMap(String key, Object value) {
        for (int i = 0; i < size(); i++) {
            Object child = get(i);
            if (child instanceof JsonMap) {
                if (((JsonMap) child).getString(key).equals(String.valueOf(value))) {
                    return (JsonMap) child;
                }
            }
        }
        return new JsonMap().preBuild(-1, this);
    }

    public JsonListAdapter createAdapter(Context context, int layoutResId) {
        return new JsonListAdapter(context, layoutResId, this);
    }

    public JsonList preprocessedJsonMapData(JsonMapPreprocessingEvents events) {
        for (Object data : this) {
            if (data instanceof JsonMap) {
                JsonMap jsonMap = (JsonMap) data;
                events.processingData(jsonMap);
            }
        }
        return this;
    }

    private void callParentRelease() {
        if (parentJsonMap != null) {
            parentJsonMap.set(preBuildKey, this);
            parentJsonMap = null;
        }
        if (parentJsonList != null) {
            if (preBuildIndex >= 0) {
                parentJsonList.set(preBuildIndex, this);
            } else {
                parentJsonList.set(this);
            }
            parentJsonList = null;
        }
    }

    private String preBuildKey;
    private JsonMap parentJsonMap;

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonList preBuild(String key, JsonMap parentJsonMap) {
        this.preBuildKey = key;
        this.parentJsonMap = parentJsonMap;
        return this;
    }

    private int preBuildIndex = -1;
    private JsonList parentJsonList;

    /**
     * 内部方法禁止使用
     */
    @Deprecated
    public JsonList preBuild(int preBuildIndex, JsonList parentJsonList) {
        this.preBuildIndex = preBuildIndex;
        this.parentJsonList = parentJsonList;
        return this;
    }
}
