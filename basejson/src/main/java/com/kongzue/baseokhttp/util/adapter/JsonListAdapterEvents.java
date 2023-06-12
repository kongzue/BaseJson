package com.kongzue.baseokhttp.util.adapter;

import android.content.Context;
import android.view.View;

import com.kongzue.baseokhttp.util.JsonList;
import com.kongzue.baseokhttp.util.JsonMap;

public interface JsonListAdapterEvents {

    /**
     * 预处理数据
     * 但不建议使用，更推荐在数据加载后，在网络请求或异步线程中完成所有数据的预处理，
     * 否则可能带来列表滑动过程的卡顿和性能问题。
     *
     * @param context    上下文
     * @param itemView   列表项根布局
     * @param viewHolder 所有子布局
     * @param data       数据
     * @param index      索引
     * @param dataList   数据集
     * @return 处理完的数据
     */
    JsonMap preprocessedData(Context context, View itemView, JsonListAdapter.ViewHolder viewHolder, JsonMap data, int index, JsonList dataList);

    /**
     * 绑定事件
     * 除非你要在列表项中处理某些控件的事务，例如单独指定 setOnClickListener，不然不建议使用
     * 原则就是能尽量少的干涉执行过程就尽量少干涉，能提前预处理好就提前预处理好，
     * 别搁运行过程中呼啦啦搞一大堆的逻辑处理，不然不卡你卡谁
     *
     * @param context    上下文
     * @param itemView   列表项根布局
     * @param viewHolder 所有子布局
     * @param data       数据
     * @param index      索引
     * @param dataList   数据集
     */
    void bindEvents(Context context, View itemView, JsonListAdapter.ViewHolder viewHolder, JsonMap data, int index, JsonList dataList);

    /**
     * 设置数据
     * 我们已经预设了两种数据自动绑定的场景：例如，TextView 自动绑定:
     * {当数据为 String 时自动设置 setText；当数据为 int 时自动调用并设置为文本资源}
     * ImageView 自动绑定:
     * {当数据为 int 时自动调用并设置为图像资源；当数据为 String 时自动设置图像 Uri；}
     * 上述过程都是自动支持的，如果你有特殊需求或者其它组件需要设置，可以重写以下方法进行处理
     *
     * @param context  上下文
     * @param tag      子布局 view 的标签
     * @param view     子布局
     * @param data     数据
     * @param index    索引
     * @param dataList 数据集
     */
    void setData(Context context, String tag, View view, JsonMap data, int index, JsonList dataList);
}
