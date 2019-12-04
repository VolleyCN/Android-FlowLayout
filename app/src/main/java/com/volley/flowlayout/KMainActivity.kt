package com.volley.flowlayout

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.volley.flowlayout.R.layout
import com.volley.flowlayout.domain.KSelectBean
import com.volley.flowlayout.utils.textSpan
import com.volley.library.flowtag.FlowTagLayout
import com.volley.library.flowtag.adapter.BaseFlowAdapter
import com.volley.library.flowtag.adapter.BaseTagHolder

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
class KMainActivity : AppCompatActivity() {
    private lateinit var adapter: BaseQuickAdapter<KSelectBean, BaseViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_kmain)
        initView()
        initData()
    }

    var titles = arrayOf("热门", "产品", "男生", "女生", "其他")

    private fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = object : BaseQuickAdapter<KSelectBean, BaseViewHolder>(layout.adapter_select_item) {
            override fun convert(helper: BaseViewHolder, item: KSelectBean) {
                helper.getView<View>(R.id.expand).isSelected = item.isExpand
                helper.addOnClickListener(R.id.expand)
                val tags = item.getShowTags()
                var textSpan = textSpan(item.title, Color.parseColor("#ff8fb7"), 40)
                textSpan.append(textSpan("${tags?.size!!}", Color.parseColor("#0071ba"), 28))
                helper.setText(R.id.title, textSpan)
                val tagLayout = helper.getView<FlowTagLayout<KSelectBean.KTagBean>>(R.id.taglayout)
                tagLayout.visibility = if (item.isExpand) View.VISIBLE else View.GONE
                when (item.title) {
                    titles[2] -> {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE)
                        tagLayout.setTagCancelable(true)
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_FREE)
                    }
                    titles[1] -> {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI)
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_SINGLE_LINE)
                    }
                    else -> {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI)
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_SPAN)
                        tagLayout.setSpanCount(3)
                    }
                }
                tagLayout.setAdapter(object : BaseFlowAdapter<KSelectBean.KTagBean, BaseTagHolder>(layout.adapter_radius_25_select_tag_item, tags) {
                    override fun convert(tagHelper: BaseTagHolder, item: KSelectBean.KTagBean?) {
                        tagHelper.setText(R.id.tag_item, item?.tagName)
                        tagHelper.getView<View>(R.id.tag_item).isSelected = item?.isChecked()
                                ?: false
                    }
                })
            }
        }
        recyclerView.adapter = adapter

        recyclerView.addOnItemTouchListener(object : OnItemChildClickListener() {
            override fun onSimpleItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                if (view.id == R.id.expand) {
                    val selectBean: KSelectBean = selects[position]
                    selectBean.isExpand = !selectBean.isExpand
                    adapter.notifyItemChanged(position)
                }
            }
        })

        findViewById<View>(R.id.tag_add).setOnClickListener {
            try {
                if (selects.size > 0) {
                    val position = selects.size - 1
                    val kSelectBean = selects[position]
                    kSelectBean.tags.add(KSelectBean.KTagBean("我是新添加", kSelectBean.title, false))
                    kSelectBean.isExpand = true
                    adapter.notifyItemChanged(position)
                } else {
                    initData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        findViewById<View>(R.id.tag_remove).setOnClickListener {
            try {
                val position = selects.size - 1
                val kSelectBean = selects[position]
                if (kSelectBean.tags.size > 0) {
                    kSelectBean.tags.removeAt(kSelectBean.tags.size - 1)
                    adapter.notifyItemChanged(position)
                } else {
                    adapter.remove(position)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val t_tags = arrayListOf(KSelectBean.KTagBean("hello", "hello", false),
            KSelectBean.KTagBean("helloWorld", "helloWorld", false),
            KSelectBean.KTagBean("boyFriend", "boyFriend", false),
            KSelectBean.KTagBean("boyGod", "boyGod", false),
            KSelectBean.KTagBean("boy", "boy", false),
            KSelectBean.KTagBean("boyFriend", "boyFriend", false),
            KSelectBean.KTagBean("girl", "girl", false),
            KSelectBean.KTagBean("helloWorld", "helloWorld", false),
            KSelectBean.KTagBean("boyFriend", "boyFriend", false),
            KSelectBean.KTagBean("boyGod", "boyGod", false),
            KSelectBean.KTagBean("boy", "boy", false),
            KSelectBean.KTagBean("boyFriend", "boyFriend", false),
            KSelectBean.KTagBean("girl", "girl", false),
            KSelectBean.KTagBean("helloWorld", "helloWorld", false)
    )
    private val selects: MutableList<KSelectBean> = ArrayList()
    private fun initData() {
        for (i in titles.indices) {
            val selectBean = KSelectBean()
            val title = titles[i]
            selectBean.title = title
            val tags = selectBean.tags
            if (title == "男生") {
                selectBean.tags.addAll(t_tags)
            } else {
                for (i1 in 0..6) {
                    tags.add(KSelectBean.KTagBean(title + "" + i1, title, false))
                }
            }
            selectBean.tags = tags
            selects.add(selectBean)
        }
        adapter.setNewData(selects)
    }

}
