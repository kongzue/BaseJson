package com.kongzue.baseokhttp.util.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongzue.baseokhttp.util.JsonList;
import com.kongzue.baseokhttp.util.JsonMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonListAdapter extends BaseAdapter implements JsonListAdapterEvents {

    public static int VIEW_HOLDER_KEY = Integer.MIN_VALUE;

    JsonList dataList = new JsonList();
    int layoutResId;
    String[] keys;
    Context context;
    List<ViewHolder> viewHolderList = new ArrayList<>();
    JsonListAdapterEvents events;
    boolean ifNullSetGone;

    public JsonListAdapter(Context context, int layoutResId, JsonList dataList) {
        this.dataList = dataList;
        this.layoutResId = layoutResId;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public JsonMap getItem(int position) {
        return dataList.getJsonMap(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createView(layoutResId);
        }
        bindChildData(convertView, dataList.getJsonMap(position), position);
        return convertView;
    }

    private void bindChildData(View convertView, JsonMap data, int position) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag(VIEW_HOLDER_KEY);
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            for (String key : keys) {
                viewHolder.add(convertView.findViewWithTag(key));
            }
            convertView.setTag(VIEW_HOLDER_KEY, viewHolder);
        }
        data = preprocessedData(context, convertView, viewHolder, data, position, dataList);
        for (View childView : viewHolder) {
            String tag = (String) childView.getTag();
            if (!isNull(tag)) {
                String text = data.getString(tag);
                int resId = data.getInt(tag);
                if (tag.contains(".")) {
                    String[] tags = tag.split("\\.");
                    JsonMap childJsonMap = data;
                    for (int i = 0; i < tags.length; i++) {
                        String childTag = tags[i];
                        if (i == tags.length - 1) {
                            String findText = childJsonMap.getString(childTag);
                            int findResId = childJsonMap.getInt(childTag);
                            if (!isNull(findText)) {
                                text = findText;
                            }
                            if (!isNull(findResId)) {
                                resId = findResId;
                            }
                        } else {
                            childJsonMap = childJsonMap.getJsonMap(childTag);
                            if (childJsonMap.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
                if (childView instanceof TextView) {
                    if (!isNull(text)) {
                        ((TextView) childView).setText(text);
                        if (isIfNullSetGone()) childView.setVisibility(View.VISIBLE);
                    } else {
                        if (!isNull(resId)) {
                            ((TextView) childView).setText(resId);
                            if (isIfNullSetGone()) childView.setVisibility(View.VISIBLE);
                        } else {
                            if (isIfNullSetGone()) childView.setVisibility(View.GONE);
                        }
                    }
                }
                if (childView instanceof ImageView) {
                    if (!isNull(resId)) {
                        ((ImageView) childView).setImageResource(resId);
                        if (isIfNullSetGone()) childView.setVisibility(View.VISIBLE);
                    } else {
                        if (!isNull(text)) {
                            ((ImageView) childView).setImageURI(Uri.parse(text));
                            if (isIfNullSetGone()) childView.setVisibility(View.VISIBLE);
                        } else {
                            if (isIfNullSetGone()) childView.setVisibility(View.GONE);
                        }
                    }
                }
                setData(context, tag, childView, data, position, dataList);
            }
        }
        bindEvents(context, convertView, viewHolder, data, position, dataList);
    }

    private View createView(int layoutResId) {
        View itemView = LayoutInflater.from(context).inflate(layoutResId, null);
        if (!dataList.isEmpty()) {
            ViewHolder childViews = new ViewHolder();
            List<String> findKeyArray = new ArrayList<>();
            for (String key : dataList.getJsonMap(0).keySet()) {
                View childView = itemView.findViewWithTag(key);
                if (childView != null) {
                    findKeyArray.add(key);
                    childViews.add(childView);
                }
                findAllChildKeys(findKeyArray, childViews, dataList.getJsonMap(0).getJsonMap(key), key, itemView);
            }
            if (keys == null && !findKeyArray.isEmpty()) {
                keys = findKeyArray.toArray(new String[findKeyArray.size()]);
            }
            itemView.setTag(VIEW_HOLDER_KEY, childViews);
        }
        return itemView;
    }

    private void findAllChildKeys(List<String> findKeyArray, ViewHolder childViews, JsonMap jsonMap, String parentKey, View rootView) {
        if (!jsonMap.isEmpty()) {
            for (String key : jsonMap.keySet()) {
                String tag = parentKey + "." + key;
                View childView = rootView.findViewWithTag(tag);
                if (childView != null) {
                    findKeyArray.add(tag);
                    childViews.add(childView);
                }
                if (!jsonMap.getJsonMap(key).isEmpty()) {
                    findAllChildKeys(findKeyArray, childViews, jsonMap.getJsonMap(key), key, rootView);
                }
            }
        }
    }

    //for override —————————————————————————————————————————————————————————————————————————————————

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
    @Override
    public JsonMap preprocessedData(Context context, View itemView, ViewHolder viewHolder, JsonMap data, int index, JsonList dataList) {
        if (events != null) {
            return events.preprocessedData(context, itemView, viewHolder, data, index, dataList);
        }
        return data;
    }

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
    @Override
    public void bindEvents(Context context, View itemView, ViewHolder viewHolder, JsonMap data, int index, JsonList dataList) {
        if (events != null) {
            events.bindEvents(context, itemView, viewHolder, data, index, dataList);
        }
    }

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
    @Override
    public void setData(Context context, String tag, View view, JsonMap data, int index, JsonList dataList) {
        if (events != null) {
            events.setData(context, tag, view, data, index, dataList);
        }
    }

    //get/set properties ———————————————————————————————————————————————————————————————————————————
    public JsonList getDataList() {
        return dataList;
    }

    public JsonListAdapter setDataList(JsonList dataList) {
        this.dataList = dataList;
        return this;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public JsonListAdapter setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
        return this;
    }

    public String[] getKeys() {
        return keys;
    }

    public JsonListAdapter setKeys(String... keys) {
        this.keys = keys;
        return this;
    }

    public List<ViewHolder> getViewHolderList() {
        return viewHolderList;
    }

    public JsonListAdapter setViewHolderList(List<ViewHolder> viewHolderList) {
        this.viewHolderList = viewHolderList;
        return this;
    }

    private boolean isNull(String data) {
        return data == null || data.isEmpty();
    }

    private boolean isNull(int data) {
        return data == 0;
    }

    public class ViewHolder extends ArrayList<View> {

        public <T extends View> T getView(String tag) {
            for (View v : this) {
                if (Objects.equals(v.getTag(), tag)) {
                    return (T) v;
                }
            }
            return null;
        }
    }

    public JsonListAdapterEvents getEvents() {
        return events;
    }

    public JsonListAdapter setEvents(JsonListAdapterEvents events) {
        this.events = events;
        return this;
    }

    public boolean isIfNullSetGone() {
        return ifNullSetGone;
    }

    public JsonListAdapter setIfNullSetGone(boolean ifNullSetGone) {
        this.ifNullSetGone = ifNullSetGone;
        return this;
    }
}
