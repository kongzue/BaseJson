package com.kongzue.baseokhttp.util.interfaces;

import com.kongzue.baseokhttp.util.JsonMap;

public interface JsonMapPreprocessingEvents {

    /**
     * 预处理数据接口
     *
     * @param originData 原始数据
     * @return 处理后的数据
     */
    JsonMap processingData(JsonMap originData);
}
