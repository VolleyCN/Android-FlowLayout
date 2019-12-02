package com.volley.flowlayout.domain;


import com.volley.library.flowtag.OptionCheck;

/**
 * @Describe
 * @Date : 2019-11-29
 * @Email : volleychzm@gmail.com
 * @Author : volley
 */
public class TagBean implements OptionCheck {
    private String tagName;
    private String tagKey;
    private boolean checked;

    public TagBean() {
    }

    public TagBean(String tagName, String tagKey, boolean checked) {
        this.tagName = tagName;
        this.tagKey = tagKey;
        this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tagName='" + tagName + '\'' +
                ", tagKey='" + tagKey + '\'' +
                ", checked=" + checked +
                '}';
    }
}
