package com.volley.library.flowtag.adapter

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import java.util.*

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
class BaseTagHolder(view: View) : android.support.v7.widget.RecyclerView.ViewHolder(view) {
    private lateinit var adapter: BaseFlowAdapter<*, *>
    private val views: SparseArray<View?>
    private val nestViews: HashSet<Int>
    private var convertView: View
    fun getConvertView(): View {
        return convertView
    }

    fun getNestViews(): Set<Int> {
        return nestViews
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): BaseTagHolder {
        val view = getView<TextView>(viewId)
        view.text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseTagHolder {
        val view = getView<TextView>(viewId)
        view.setText(strId)
        return this
    }

    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseTagHolder {
        val view = getView<ImageView>(viewId)
        view.setImageResource(imageResId)
        return this
    }

    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseTagHolder {
        val view = getView<View>(viewId)
        view.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseTagHolder {
        val view = getView<View>(viewId)
        view.setBackgroundResource(backgroundRes)
        return this
    }

    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): BaseTagHolder {
        val view = getView<TextView>(viewId)
        view.setTextColor(textColor)
        return this
    }

    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BaseTagHolder {
        val view = getView<ImageView>(viewId)
        view.setImageDrawable(drawable)
        return this
    }

    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BaseTagHolder {
        val view = getView<ImageView>(viewId)
        view.setImageBitmap(bitmap)
        return this
    }

    fun setAlpha(@IdRes viewId: Int, value: Float): BaseTagHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId).alpha = value
        } else {
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    fun setGone(@IdRes viewId: Int, visible: Boolean): BaseTagHolder {
        val view = getView<View>(viewId)
        view.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setVisible(@IdRes viewId: Int, visible: Boolean): BaseTagHolder {
        val view = getView<View>(viewId)
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun linkify(@IdRes viewId: Int): BaseTagHolder {
        val view = getView<TextView>(viewId)
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    fun setTypeface(@IdRes viewId: Int, typeface: Typeface?): BaseTagHolder {
        val view = getView<TextView>(viewId)
        view.typeface = typeface
        view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        return this
    }

    fun setTypeface(typeface: Typeface?, vararg viewIds: Int): BaseTagHolder {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)
            view.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int): BaseTagHolder {
        val view = getView<ProgressBar>(viewId)
        view.progress = progress
        return this
    }

    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BaseTagHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        view.progress = progress
        return this
    }

    fun setMax(@IdRes viewId: Int, max: Int): BaseTagHolder {
        val view = getView<ProgressBar>(viewId)
        view.max = max
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float): BaseTagHolder {
        val view = getView<RatingBar>(viewId)
        view.rating = rating
        return this
    }

    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BaseTagHolder {
        val view = getView<RatingBar>(viewId)
        view.max = max
        view.rating = rating
        return this
    }

    @Deprecated("")
    fun setOnClickListener(@IdRes viewId: Int, listener: View.OnClickListener?): BaseTagHolder {
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setTag(@IdRes viewId: Int, tag: Any?): BaseTagHolder {
        val view = getView<View>(viewId)
        view.tag = tag
        return this
    }

    fun setTag(@IdRes viewId: Int, key: Int, tag: Any?): BaseTagHolder {
        val view = getView<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    fun setChecked(@IdRes viewId: Int, checked: Boolean): BaseTagHolder {
        val view = getView<View>(viewId)
        if (view is Checkable) {
            (view as Checkable).isChecked = checked
        }
        return this
    }


    fun <T : View?> getView(@IdRes viewId: Int): T {
        var view = views[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }

    fun setAdapter(adapter: BaseFlowAdapter<*, *>): BaseTagHolder? {
        this.adapter = adapter
        return this
    }

    init {
        views = SparseArray()
        nestViews = HashSet()
        convertView = view
    }
}