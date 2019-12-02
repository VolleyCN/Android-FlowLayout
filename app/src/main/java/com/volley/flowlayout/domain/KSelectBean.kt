package com.volley.flowlayout.domain

import com.volley.library.flowtag.OptionCheck

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
class KSelectBean {
    lateinit var title: String
    var tags: MutableList<KTagBean> = ArrayList()
    var isExpand: Boolean = false

    data class KTagBean(val tagName: String, val tagKey: String, var checked: Boolean?) : OptionCheck {
        override fun isChecked(): Boolean {
            return checked ?: false
        }

        override fun setChecked(check: Boolean) {
            this.checked = check
        }
    }

    fun getShowTags(): List<KTagBean>? {
        val tags: List<KTagBean> = tags
        val showtags: MutableList<KTagBean> = ArrayList()
        if (tags.size > 10 && !isExpand) {
            for (i in 0..9) {
                showtags.add(tags[i])
            }
        } else {
            showtags.addAll(tags)
        }
        return showtags
    }
}


