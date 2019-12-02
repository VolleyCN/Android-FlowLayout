package com.volley.library.flowtag.adapter

import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.volley.library.flowtag.OptionCheck
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
abstract class BaseFlowAdapter<T : OptionCheck, K : BaseTagHolder?> @JvmOverloads constructor(@LayoutRes layoutResId: Int, data: List<T>? = null) : RecyclerView.Adapter<K>() {
    protected var mContext: Context? = null
    protected var mLayoutResId = 0
    protected var mLayoutInflater: LayoutInflater? = null
    protected var mData: MutableList<T>

    constructor(data: List<T>?) : this(0, data) {}

    private val mViewHolders: MutableList<K> = ArrayList()
    fun getBaseItemView(position: Int, inflater: LayoutInflater, parent: ViewGroup?): View {
        return if (position < mViewHolders.size) {
            mViewHolders[position]?.getConvertView()!!
        } else {
            val view = inflater.inflate(mLayoutResId, parent, false)
            val holder = createBaseViewHolder(view)
            bindViewHolder(holder, position)
            view
        }
    }

    fun setNewData(data: List<T>?) {
        mData = data as MutableList<T>? ?: ArrayList<T>()
        notifyDataSetChanged()
    }

    @Deprecated("")
    fun add(@IntRange(from = 0) position: Int, item: T) {
        addData(position, item)
    }

    fun addData(@IntRange(from = 0) position: Int, data: T) {
        mData.add(position, data)
        notifyItemInserted(position)
        compatibilityDataSizeChanged(1)
    }

    fun addData(data: T) {
        mData.add(data)
        notifyItemInserted(mData.size)
        compatibilityDataSizeChanged(1)
    }

    fun remove(@IntRange(from = 0) position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        compatibilityDataSizeChanged(0)
        notifyItemRangeChanged(position, mData.size - position)
    }

    fun setData(@IntRange(from = 0) index: Int, data: T) {
        mData[index] = data
        notifyItemChanged(index)
    }

    fun addData(@IntRange(from = 0) position: Int, newData: Collection<T>) {
        mData.addAll(position, newData)
        notifyItemRangeInserted(position, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    fun addData(newData: Collection<T>) {
        mData.addAll(newData)
        notifyItemRangeInserted(mData.size - newData.size, newData.size)
        compatibilityDataSizeChanged(newData.size)
    }

    fun replaceData(data: Collection<T>) {
        if (data !== mData) {
            mData.clear()
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }

    private fun compatibilityDataSizeChanged(size: Int) {
        val dataSize = if (mData == null) 0 else mData.size
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }

    fun getItem(@IntRange(from = 0) position: Int): T? {
        return if (position >= 0 && position < mData.size) mData[position] else null
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return getDefItemViewType(position)
    }

    protected fun getDefItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): K {
        mContext = parent.context
        mLayoutInflater = LayoutInflater.from(mContext)
        val baseViewHolder = onCreateDefViewHolder(parent, viewType)
        bindViewClickListener(baseViewHolder)
        baseViewHolder?.setAdapter(this)
        return baseViewHolder
    }

    override fun onViewAttachedToWindow(holder: K) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: K, position: Int) {
        convert(holder, getItem(position))
        mViewHolders.add(holder)
    }

    private fun bindViewClickListener(baseViewHolder: BaseTagHolder?) {
        if (baseViewHolder == null) {
            return
        }
        val view: View = baseViewHolder.itemView ?: return
    }

    protected fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): K {
        val layoutId = mLayoutResId
        return createBaseViewHolder(parent, layoutId)
    }

    protected fun createBaseViewHolder(parent: ViewGroup?, layoutResId: Int): K {
        return createBaseViewHolder(getItemView(layoutResId, parent))
    }

    protected fun createBaseViewHolder(view: View): K {
        var temp: Class<*>? = javaClass
        var z: Class<*>? = null
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp)
            temp = temp.superclass
        }
        val k: K?
        k = z?.let { createGenericKInstance(it, view) }
                ?: BaseTagHolder(view) as K
        return k ?: BaseTagHolder(view) as K
    }

    private fun createGenericKInstance(z: Class<*>, view: View): K? {
        try {
            val constructor: Constructor<*>
            return if (z.isMemberClass && !Modifier.isStatic(z.modifiers)) {
                constructor = z.getDeclaredConstructor(javaClass, View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(this, view) as K
            } else {
                constructor = z.getDeclaredConstructor(View::class.java)
                constructor.isAccessible = true
                constructor.newInstance(view) as K
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getInstancedGenericKClass(z: Class<*>): Class<*>? {
        val type = z.genericSuperclass
        if (type is ParameterizedType) {
            val types = type.actualTypeArguments
            for (temp in types) {
                if (temp is Class<*>) {
                    val tempClass = temp
                    if (BaseTagHolder::class.java.isAssignableFrom(tempClass)) {
                        return tempClass
                    }
                } else if (temp is ParameterizedType) {
                    val rawType = temp.rawType
                    if (rawType is Class<*> && BaseTagHolder::class.java.isAssignableFrom(rawType)) {
                        return rawType
                    }
                }
            }
        }
        return null
    }

    protected fun getItemView(@LayoutRes layoutResId: Int, parent: ViewGroup?): View {
        return mLayoutInflater!!.inflate(layoutResId, parent, false)
    }

    protected abstract fun convert(tagHelper: K, item: T?)
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getItemPosition(item: T?): Int {
        return if (item != null && mData != null && !mData.isEmpty()) mData.indexOf(item) else -1
    }


    open fun getData(): List<T> {
        return mData
    }


    init {
        mData = data as MutableList<T>? ?: ArrayList<T>()
        if (layoutResId != 0) {
            mLayoutResId = layoutResId
        }
    }
}