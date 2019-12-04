package com.volley.flowlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.volley.flowlayout.domain.SelectBean;
import com.volley.flowlayout.domain.TagBean;
import com.volley.library.flowtag.FlowTagLayout;
import com.volley.library.flowtag.adapter.BaseFlowAdapter;
import com.volley.library.flowtag.adapter.BaseTagHolder;

import java.util.ArrayList;
import java.util.List;
/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
public class MainActivity extends AppCompatActivity {
    private BaseQuickAdapter mSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.addLogAdapter(new AndroidLogAdapter());
        testSelect();
//        testGaiLou();
    }

    private void testSelect() {
        RecyclerView recycler = findViewById(R.id.recyclerview);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        this.mSelectAdapter = new BaseQuickAdapter<SelectBean, BaseViewHolder>(R.layout.adapter_select_item) {
            @Override
            protected void convert(final BaseViewHolder helper, SelectBean item) {
                try {
                    helper.setText(R.id.title, item.getTitle());
                    helper.getView(R.id.expand).setSelected(item.isExpand());
                    helper.addOnClickListener(R.id.expand);
                    final List<TagBean> tags = item.getShowTags();
                    final FlowTagLayout tagLayout = helper.getView(R.id.taglayout);
                    if (item.getTitle().equals(titles[2])) {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
                        tagLayout.setTagCancelable(true);
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_FREE);
                    } else if (item.getTitle().equals(titles[1])) {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_SINGLE_LINE);
                    } else {
                        tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
                        tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_SPAN);
                        tagLayout.setSpanCount(3);
                    }

                    tagLayout.setAdapter(new BaseFlowAdapter<TagBean, BaseTagHolder>(R.layout.adapter_select_tag_item, tags) {
                        @Override
                        protected void convert(BaseTagHolder tagHelper, TagBean item) {
                            tagHelper.setText(R.id.tag_item, item.getTagName());
                            tagHelper.getView(R.id.tag_item).setSelected(item.isChecked());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        recycler.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.expand) {
                    SelectBean selectBean = selects.get(position);
                    selectBean.setExpand(!selectBean.isExpand());
                    adapter.notifyItemChanged(position);
                }
            }
        });
        recycler.setAdapter(mSelectAdapter);
        initData();
    }
    String[] titles = {"热门", "产品", "男生", "女生", "其他"};
    private List<SelectBean> selects = new ArrayList<>();
    private void initData() {
        for (int i = 0; i < titles.length; i++) {
            SelectBean selectBean = new SelectBean();
            String title = titles[i];
            selectBean.setTitle(title);
            List<TagBean> tags = selectBean.getTags();
            for (int i1 = 0; i1 < 20; i1++) {
                tags.add(new TagBean(title + "" + i1, title, false));
            }
            selectBean.setTags(tags);
            selects.add(selectBean);
        }
        mSelectAdapter.setNewData(selects);
    }
}
