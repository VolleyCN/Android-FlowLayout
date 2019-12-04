package com.volley.flowlayout.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan

/**
 * @Describe
 * @Date : 2019-12-03
 * @Email : volleychzm@gmail.com
 * @Author : MENG
 */

fun textSpan(strs: Array<String>, colors: Array<String>, sizes: Array<Int>): CharSequence {
    if (colors.size != colors.size) {
        return ""
    }
    val span = SpannableStringBuilder()
    for (index in 0..strs.size) {
        if (strs.size == colors.size && strs.size == sizes.size) {
            span.append(textSpan(strs[index], Color.parseColor(colors[index]), sizes[index]))
        } else if (strs.size == colors.size) {
            span.append(textSpan(str = strs[index], color = Color.parseColor(colors[index]), size = 0))
        } else if (strs.size == sizes.size) {
            span.append(textSpan(str = strs[index], color = 0, size = sizes[index]))
        }
    }
    return span
}

fun textSpan(str: String?, color: Int, size: Int): SpannableStringBuilder {
    val span = SpannableStringBuilder("")
    if (str == null) {
        return span
    }
    span.append(str)
    if (size != 0) {
        span.setSpan(AbsoluteSizeSpan(size), (span.length - str.length), span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    if (color != 0) {
        span.setSpan(ForegroundColorSpan(color), (span.length - str.length), span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return span
}