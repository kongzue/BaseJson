package com.kongzue.baseokhttp.util.interfaces;

import com.kongzue.baseokhttp.util.JsonMap;

public abstract class JsonMapPreprocessingEvents {

    boolean deleteWhenDataIsNull;

    /**
     * 预处理数据接口
     *
     * @param originData 原始数据
     * @return 处理后的数据
     */
   public abstract JsonMap processingData(JsonMap originData);

    public boolean isDeleteWhenDataIsNull() {
        return deleteWhenDataIsNull;
    }

    public JsonMapPreprocessingEvents setDeleteWhenDataIsNull(boolean deleteWhenDataIsNull) {
        this.deleteWhenDataIsNull = deleteWhenDataIsNull;
        return this;
    }

    public JsonMapPreprocessingEvents() {
    }

    public JsonMapPreprocessingEvents(boolean deleteWhenDataIsNull) {
        this.deleteWhenDataIsNull = deleteWhenDataIsNull;
    }
}
