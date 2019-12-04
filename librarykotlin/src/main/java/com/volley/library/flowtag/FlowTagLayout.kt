package com.volley.library.flowtag

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.volley.library.flowtag.adapter.BaseFlowAdapter
import java.util.*
import kotlin.math.max

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
class FlowTagLayout<T : OptionCheck?> : ViewGroup {
    private var mLayoutInflater: LayoutInflater
    private val mSelects: MutableSet<T> = HashSet()
    private var spanCount = 0
    private var mDataSetObserver: AdapterDataSetObserver? = null
    private var mAdapter: BaseFlowAdapter<*, *>? = null
    private var mOnTagClickListener: OnTagClickListener? = null
    private var mOnTagSelectListener: OnTagSelectListener<*>? = null
    private var mTagShowMode = FLOW_TAG_SHOW_SINGLE_LINE
    private var mTagCheckMode = FLOW_TAG_CHECKED_NONE
    private var cancel = false

    constructor(context: Context?) : super(context) {
        mLayoutInflater = LayoutInflater.from(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mLayoutInflater = LayoutInflater.from(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mLayoutInflater = LayoutInflater.from(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获得它的父容器为它设置的测量模式和大小
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
        //FlowLayout最终的宽度和高度值
        var resultWidth = 0
        var resultHeight = 0
        //测量时每一行的宽度
        var lineWidth = 0
        //测量时每一行的高度，加起来就是FlowLayout的高度
        var lineHeight = 0
        //遍历每个子元素
        var i = 0
        val childCount = childCount
        while (i < childCount) {
            val childView = getChildAt(i)
            if (mTagShowMode == FLOW_TAG_SHOW_SPAN) {
                if (spanCount <= 0) {
                    spanCount = 1
                }
                val layoutParams = childView.layoutParams
                val mlp = childView.layoutParams as MarginLayoutParams
                layoutParams.width = sizeWidth / spanCount - (mlp.leftMargin + mlp.rightMargin)
                childView.layoutParams = layoutParams
            }
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) {
                val layoutParams = childView.layoutParams
                val mlp = childView.layoutParams as MarginLayoutParams
                layoutParams.width = sizeWidth - (mlp.leftMargin + mlp.rightMargin)
                childView.layoutParams = layoutParams
            }
            //测量每一个子view的宽和高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            //获取到测量的宽和高
            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight
            //因为子View可能设置margin，这里要加上margin的距离
            val mlp = childView.layoutParams as MarginLayoutParams
            val realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin
            val realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) {
                resultWidth = max(lineWidth, realChildWidth)
                resultHeight += realChildHeight
            } else if (mTagShowMode == FLOW_TAG_SHOW_FREE || mTagShowMode == FLOW_TAG_SHOW_SPAN) { //如果当前一行的宽度加上要加入的子view的宽度大于父容器给的宽度，就换行
                if (lineWidth + realChildWidth > sizeWidth) { //换行
                    resultWidth = max(lineWidth, realChildWidth)
                    resultHeight += realChildHeight
                    //换行了，lineWidth和lineHeight重新算
                    lineWidth = realChildWidth
                    lineHeight = realChildHeight
                } else { //不换行，直接相加
                    lineWidth += realChildWidth
                    //每一行的高度取二者最大值
                    lineHeight = max(lineHeight, realChildHeight)
                }
                //最后一个不换行
                if (i == childCount - 1) {
                    resultWidth = max(lineWidth, resultWidth)
                    resultHeight += lineHeight
                }
            }
            setMeasuredDimension(if (modeWidth == MeasureSpec.EXACTLY) sizeWidth else resultWidth,
                    if (modeHeight == MeasureSpec.EXACTLY) sizeHeight else resultHeight)
            i++
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val flowWidth = width
        var childLeft = 0
        var childTop = 0
        //遍历子控件，记录每个子view的位置
        var i = 0
        val childCount = childCount
        while (i < childCount) {
            val childView = getChildAt(i)
            //子View为GONE不处理
            if (childView.visibility == View.GONE) {
                i++
                continue
            }
            //获取到测量的宽和高
            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight
            //因为子View可能设置margin，这里要加上margin的距离
            val mlp = childView.layoutParams as MarginLayoutParams
            if (mTagShowMode == FLOW_TAG_SHOW_SINGLE_LINE) { //第一个不需要换行
                if (i > 0) {
                    childTop += mlp.topMargin + childHeight + mlp.bottomMargin
                    childLeft = 0
                }
            } else if (mTagShowMode == FLOW_TAG_SHOW_FREE || mTagShowMode == FLOW_TAG_SHOW_SPAN) {
                if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) { //换行处理
                    childTop += mlp.topMargin + childHeight + mlp.bottomMargin
                    childLeft = 0
                }
            }
            //布局
            val left = childLeft + mlp.leftMargin
            val top = childTop + mlp.topMargin
            val right = childLeft + mlp.leftMargin + childWidth
            val bottom = childTop + mlp.topMargin + childHeight
            childView.layout(left, top, right, bottom)
            childLeft += mlp.leftMargin + childWidth + mlp.rightMargin
            i++
        }
    }

    fun setSpanCount(spanCount: Int) {
        this.spanCount = spanCount
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    fun getAdapter(): BaseFlowAdapter<*, *>? {
        return mAdapter
    }

    fun setTagCancelable(cancel: Boolean) {
        this.cancel = cancel
    }

    inner class AdapterDataSetObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadData()
        }
    }

    private fun reloadData() {
        removeAllViews()
        var isSetted = false
        val allDatas: List<OptionCheck>? = getAdapter()?.getData()
        for (i in allDatas?.indices!!) {
            val checkItem = allDatas[i]
            val childView = mAdapter?.getBaseItemView(i, mLayoutInflater, this)
            addView(childView, MarginLayoutParams(LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)))
            if (mTagCheckMode == FLOW_TAG_CHECKED_SINGLE) {
                if (checkItem.isChecked() && !isSetted) {
                    isSetted = true
                } else {
                    checkItem.setChecked(false)
                }
            }
            if (checkItem.isChecked()) {
                mSelects.add(checkItem as T)
            }
            childView?.isSelected = checkItem.isChecked()
            childView?.setOnClickListener { handlerItemClick(childView, checkItem, i) }
        }
    }

    private fun handlerItemClick(childView: View, checkItem: OptionCheck, position: Int) {
        when (mTagCheckMode) {
            FLOW_TAG_CHECKED_NONE -> {
                mOnTagClickListener?.onItemClick(this@FlowTagLayout, childView, position)
            }
            FLOW_TAG_CHECKED_SINGLE -> {
                if (cancel && mSelects.contains(checkItem as T)) {
                    mOnTagClickListener?.onItemClick(this@FlowTagLayout, childView, position)
                    return
                }
                mSelects.clear()
                if (checkItem.isChecked()) {
                    checkItem.setChecked(false)
                } else {
                    clearAllOption()
                    checkItem.setChecked(true)
                    mSelects.add(checkItem as T)
                }
                childView.isSelected = checkItem.isChecked()
                mOnTagClickListener?.onItemClick(this@FlowTagLayout, childView, position)
            }
            FLOW_TAG_CHECKED_MULTI -> {
                checkItem.setChecked(!checkItem.isChecked())
                childView.isSelected = checkItem.isChecked()
                if (checkItem.isChecked()) {
                    mSelects.add(checkItem as T)
                } else if (mSelects.contains(checkItem as T)) {
                    mSelects.remove(checkItem)
                }
                mOnTagSelectListener?.onItemSelect(this@FlowTagLayout, mSelects as Set<Nothing>)
            }
        }
    }

    fun getSelects(): Set<T> {
        return mSelects
    }

    fun clearAllOption() {
        val allDatas: List<OptionCheck> = getAdapter()?.getData()!!
        for (i in allDatas.indices) {
            val check = allDatas[i]
            check.setChecked(false)
            getChildAt(i).isSelected = false
        }
    }

    fun setOnTagClickListener(onTagClickListener: OnTagClickListener?) {
        mOnTagClickListener = onTagClickListener
    }

    fun setOnTagSelectListener(onTagSelectListener: OnTagSelectListener<*>?) {
        mOnTagSelectListener = onTagSelectListener
    }

    fun setAdapter(adapter: BaseFlowAdapter<*, *>?) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter?.unregisterAdapterDataObserver(mDataSetObserver!!)
        }
        removeAllViews()
        mAdapter = adapter
        mDataSetObserver = AdapterDataSetObserver()
        mAdapter?.registerAdapterDataObserver(mDataSetObserver!!)
        mAdapter?.notifyDataSetChanged()
    }

    fun getTagCheckMode(): Int {
        return mTagCheckMode
    }

    fun setTagCheckedMode(tagMode: Int) {
        mTagCheckMode = tagMode
    }

    fun setTagShowMode(tagMode: Int) {
        mTagShowMode = tagMode
    }

    interface OnTagClickListener {
        fun onItemClick(parent: FlowTagLayout<*>?, view: View?, position: Int)
    }

    interface OnTagSelectListener<T : OptionCheck?> {
        fun onItemSelect(parent: FlowTagLayout<*>?, selected: Set<T>?)
    }

    companion object {
        const val FLOW_TAG_CHECKED_NONE = 0
        const val FLOW_TAG_CHECKED_SINGLE = 1
        const val FLOW_TAG_CHECKED_MULTI = 2
        const val FLOW_TAG_SHOW_SINGLE_LINE = 0
        const val FLOW_TAG_SHOW_FREE = 1
        const val FLOW_TAG_SHOW_SPAN = 2
    }
}