package com.volley.flowlayout.domain;

import java.util.ArrayList;
import java.util.List;
/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
public class SelectBean {
    private String title;
    private String key;
    private boolean isExpand;
    private List<TagBean> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public List<TagBean> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public List<TagBean> getShowTags() {
        List<TagBean> tags = getTags();
        List<TagBean> showtags = new ArrayList<>();
        if (tags.size() > 10 && !isExpand) {
            for (int i = 0; i < 10; i++) {
                showtags.add(tags.get(i));
            }
        } else {
            showtags.addAll(tags);
        }
        return showtags;
    }

    public void setTags(List<TagBean> tags) {
        this.tags = tags;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
