package com.volley.library.flowtag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volley.library.flowtag.adapter.BaseFlowAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
public class FlowTagLayout<T extends OptionCheck> extends ViewGroup {
    public static final int FLOW_TAG_CHECKED_NONE = 0;
    public static final int FLOW_TAG_CHECKED_SINGLE = 1;
    public static final int FLOW_TAG_CHECKED_MULTI = 2;
    private LayoutInflater mLayoutInflater;
    private Set<T> mSelects = new HashSet<>();
    private int spanCount;
    protected AdapterDataSetObserver mDataSetObserver;
    protected BaseFlowAdapter mAdapter;
    protected OnTagClickListener mOnTagClickListener;
    protected OnTagSelectListener mOnTagSelectListener;

    public static final int FLOW_TAG_SHOW_SINGLE_LINE = 0;
    public static final int FLOW_TAG_SHOW_FREE = 1;
    public static final int FLOW_TAG_SHOW_SPAN = 2;

    private int mTagShowMode = FLOW_TAG_SHOW_SINGLE_LINE;
    private int mTagCheckMode = FLOW_TAG_CHECKED_NONE;
    private boolean cancel;

    public FlowTagLayout(Context context) {
        super(context);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public FlowTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public FlowTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //FlowLayout最终的宽度和高度值
        int resultWidth = 0;
        int resultHeight = 0;
        //测量时每一行的宽度
        int lineWidth = 0;
        //测量时每一行的高度，加起来就是FlowLayout的高度
        int lineHeight = 0;
        //遍历每个子元素
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            if (mTagShowMode == FLOW_TAG_SHOW_SPAN) {
                if (spanCount <= 0) {
                    spanCount = 1;
                }
                LayoutParams layoutParams = childView.getLayoutParams();
                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
                layoutParams.width = sizeWidth / spanCount - (mlp.leftMargin + mlp.rightMargin);
                childView.setLayoutParams(layoutParams);
            }
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) {
                LayoutParams layoutParams = childView.getLayoutParams();
                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
                layoutParams.width = sizeWidth - (mlp.leftMargin + mlp.rightMargin);
                childView.setLayoutParams(layoutParams);
            }
            //测量每一个子view的宽和高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            int realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin;
            int realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin;
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) {
                resultWidth = Math.max(lineWidth, realChildWidth);
                resultHeight += realChildHeight;
            } else if (mTagShowMode == FLOW_TAG_SHOW_FREE || mTagShowMode == FLOW_TAG_SHOW_SPAN) {
                //如果当前一行的宽度加上要加入的子view的宽度大于父容器给的宽度，就换行
                if ((lineWidth + realChildWidth) > sizeWidth) {
                    //换行
                    resultWidth = Math.max(lineWidth, realChildWidth);
                    resultHeight += realChildHeight;
                    //换行了，lineWidth和lineHeight重新算
                    lineWidth = realChildWidth;
                    lineHeight = realChildHeight;
                } else {
                    //不换行，直接相加
                    lineWidth += realChildWidth;
                    //每一行的高度取二者最大值
                    lineHeight = Math.max(lineHeight, realChildHeight);
                }
                //最好一个不换行
                if (i == childCount - 1) {
                    resultWidth = Math.max(lineWidth, resultWidth);
                    resultHeight += lineHeight;
                }
            }
            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : resultWidth,
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : resultHeight);

        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int flowWidth = getWidth();
        int childLeft = 0;
        int childTop = 0;
        //遍历子控件，记录每个子view的位置
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            //子View为GONE不处理
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) {//第一个不需要换行
                if (i > 0) {
                    childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                    childLeft = 0;
                }
            } else if (mTagShowMode == FLOW_TAG_SHOW_FREE || mTagShowMode == FLOW_TAG_SHOW_SPAN) {
                if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) {
                    //换行处理
                    childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                    childLeft = 0;
                }
            }
            //布局
            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = childLeft + mlp.leftMargin + childWidth;
            int bottom = childTop + mlp.topMargin + childHeight;
            childView.layout(left, top, right, bottom);
            childLeft += (mlp.leftMargin + childWidth + mlp.rightMargin);
        }
    }


    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public BaseFlowAdapter getAdapter() {
        return mAdapter;
    }

    public void setTagCancelable(boolean cancel) {
        this.cancel = cancel;
    }

    class AdapterDataSetObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadData();
        }
    }

    private void reloadData() {
        removeAllViews();
        boolean isSetted = false;
        final List<OptionCheck> allDatas = getAdapter().getData();
        for (int i = 0; i < allDatas.size(); i++) {
            final int position = i;
            final OptionCheck checkItem = allDatas.get(i);
            final View childView = mAdapter.getBaseItemView(i, mLayoutInflater, this);
            addView(childView, new MarginLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
            if (mTagCheckMode == FLOW_TAG_CHECKED_SINGLE) {
                if (checkItem.isChecked() && !isSetted) {
                    isSetted = true;
                } else {
                    checkItem.setChecked(false);
                }
            }
            if (checkItem.isChecked()) {
                mSelects.add((T) checkItem);
            }
            childView.setSelected(checkItem.isChecked());
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlerItemClick(childView, checkItem, position);
                }
            });
        }
    }

    private void handlerItemClick(View childView, OptionCheck checkItem, int position) {
        if (mTagCheckMode == FLOW_TAG_CHECKED_NONE) {
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onItemClick(FlowTagLayout.this, childView, position);
            }
        } else if (mTagCheckMode == FLOW_TAG_CHECKED_SINGLE) {
            if (cancel && mSelects.contains(checkItem)) {
                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onItemClick(FlowTagLayout.this, childView, position);
                }
                return;
            }
            mSelects.clear();
            if (checkItem.isChecked()) {
                checkItem.setChecked(false);
            } else {
                clearAllOption();
                checkItem.setChecked(true);
                mSelects.add((T) checkItem);
            }
            childView.setSelected(checkItem.isChecked());
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onItemClick(FlowTagLayout.this, childView, position);
            }
        } else if (mTagCheckMode == FLOW_TAG_CHECKED_MULTI) {
            checkItem.setChecked(!checkItem.isChecked());
            childView.setSelected(checkItem.isChecked());
            if (checkItem.isChecked()) {
                mSelects.add((T) checkItem);
            } else if (mSelects.contains(checkItem)) {
                mSelects.remove(checkItem);
            }
            if (mOnTagSelectListener != null) {
                mOnTagSelectListener.onItemSelect(FlowTagLayout.this, mSelects);
            }
        }
    }

    public Set<T> getSelects() {
        return mSelects;
    }

    public void clearAllOption() {
        List<OptionCheck> allDatas = getAdapter().getData();
        for (int i = 0; i < allDatas.size(); i++) {
            OptionCheck check = allDatas.get(i);
            check.setChecked(false);
            getChildAt(i).setSelected(false);
        }
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.mOnTagClickListener = onTagClickListener;
    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.mOnTagSelectListener = onTagSelectListener;
    }

    public void setAdapter(BaseFlowAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterAdapterDataObserver(mDataSetObserver);
        }
        removeAllViews();
        this.mAdapter = adapter;
        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerAdapterDataObserver(mDataSetObserver);
            mAdapter.notifyDataSetChanged();
        }
    }

    public int getTagCheckMode() {
        return mTagCheckMode;
    }

    public void setTagCheckedMode(int tagMode) {
        this.mTagCheckMode = tagMode;
    }

    public void setTagShowMode(int tagMode) {
        this.mTagShowMode = tagMode;
    }

    public interface OnTagClickListener {
        void onItemClick(FlowTagLayout parent, View view, int position);
    }

    public interface OnTagSelectListener<T extends OptionCheck> {
        void onItemSelect(FlowTagLayout parent, Set<T> selected);
    }
}
