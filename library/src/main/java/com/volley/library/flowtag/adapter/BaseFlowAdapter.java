package com.volley.library.flowtag.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
public abstract class BaseFlowAdapter<T, K extends BaseTagHolder> extends RecyclerView.Adapter<K> {
    protected Context mContext;
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;

    public BaseFlowAdapter(@LayoutRes int layoutResId, @Nullable List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : data;
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }

    public BaseFlowAdapter(@Nullable List<T> data) {
        this(0, data);
    }

    public BaseFlowAdapter(@LayoutRes int layoutResId) {
        this(layoutResId, null);
    }

    private List<K> mViewHolders = new ArrayList<>();

    public View getBaseItemView(int position, LayoutInflater inflater, ViewGroup parent) {
        if (position < mViewHolders.size()) {
            return mViewHolders.get(position).getConvertView();
        } else {
            View view = inflater.inflate(mLayoutResId, parent, false);
            K holder = createBaseViewHolder(view);
            bindViewHolder(holder, position);
            return view;
        }
    }

    public void setNewData(@Nullable List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : data;
        notifyDataSetChanged();
    }

    @Deprecated
    public void add(@IntRange(from = 0) int position, @NonNull T item) {
        addData(position, item);
    }

    public void addData(@IntRange(from = 0) int position, @NonNull T data) {
        mData.add(position, data);
        notifyItemInserted(position);
        compatibilityDataSizeChanged(1);
    }

    public void addData(@NonNull T data) {
        mData.add(data);
        notifyItemInserted(mData.size());
        compatibilityDataSizeChanged(1);
    }

    public void remove(@IntRange(from = 0) int position) {
        mData.remove(position);
        int internalPosition = position;
        notifyItemRemoved(internalPosition);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(internalPosition, mData.size() - internalPosition);
    }

    public void setData(@IntRange(from = 0) int index, @NonNull T data) {
        mData.set(index, data);
        notifyItemChanged(index);
    }

    public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> newData) {
        mData.addAll(position, newData);
        notifyItemRangeInserted(position, newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    public void addData(@NonNull Collection<? extends T> newData) {
        mData.addAll(newData);
        notifyItemRangeInserted(mData.size() - newData.size(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    public void replaceData(@NonNull Collection<? extends T> data) {
        if (data != mData) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = mData == null ? 0 : mData.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    public List<T> getData() {
        return mData;
    }

    @Nullable
    public T getItem(@IntRange(from = 0) int position) {
        if (position >= 0 && position < mData.size())
            return mData.get(position);
        else
            return null;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getDefItemViewType(position);

    }

    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        K baseViewHolder = onCreateDefViewHolder(parent, viewType);
        bindViewClickListener(baseViewHolder);
        baseViewHolder.setAdapter(this);
        return baseViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(K holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(K holder, int position) {
        convert(holder, getItem(position));
        mViewHolders.add(holder);
    }

    private void bindViewClickListener(final BaseTagHolder baseViewHolder) {
        if (baseViewHolder == null) {
            return;
        }
        final View view = baseViewHolder.itemView;
        if (view == null) {
            return;
        }
    }


    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mLayoutResId;
        return createBaseViewHolder(parent, layoutId);
    }

    protected K createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(getItemView(layoutResId, parent));
    }

    protected K createBaseViewHolder(View view) {
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        K k;
        if (z == null) {
            k = (K) new BaseTagHolder(view);
        } else {
            k = createGenericKInstance(z, view);
        }
        return k != null ? k : (K) new BaseTagHolder(view);
    }

    private K createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(this, view);
            } else {
                constructor = z.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types) {
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (BaseTagHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                } else if (temp instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) temp).getRawType();
                    if (rawType instanceof Class && BaseTagHolder.class.isAssignableFrom((Class<?>) rawType)) {
                        return (Class<?>) rawType;
                    }
                }
            }
        }
        return null;
    }

    protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    protected abstract void convert(K tagHelper, T item);

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getItemPosition(T item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }

}