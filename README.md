# FlowLayout

**流式布局，适合用于标签，分类筛选等。适配器方式调用，支持单选，多选，自用显示，单行显示（类似listview），指定显示条数（类似gridview）**

![demo展示](https://github.com/VolleyCN/Android-FlowLayout/blob/master/image/demo.gif "demo展示")

------------

### 怎么样使用？

### 1.在项目根build.gradle中
    allprojects {
        repositories {
            jcenter()
            mavenCentral()
            maven { url 'https://jitpack.io' }
        }
    }
### 2.在项目build.gradle中
### kotlin 版本 
    dependencies {
    	 implementation 'com.github.VolleyCN.Android-FlowLayout:librarykotlin:1.0.2'
    }
### java 版本
    dependencies {
    	 implementation 'com.github.VolleyCN.Android-FlowLayout:library:1.0.2'
    }
### 布局文件
```XML
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="@dimen/space_5"
    tools:context=".MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="@dimen/space_10">

        <TextView
            android:id="@+id/title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1" />

        <ImageView
            android:id="@+id/expand"
            android:layout_width="@dimen/space_20"
            android:layout_height="@dimen/space_20"
            android:src="@drawable/custom_arrow_up_down_selector" />
    </LinearLayout>

    <View
        android:layout_width="match_parent"
        android:layout_height="@dimen/space_0.5"
        android:background="@color/color_f0f0f0" />

    <com.volley.library.flowtag.FlowTagLayout
        android:id="@+id/taglayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```
### 代码使用
```Kotlin
tagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI)//多选模式
tagLayout.setTagShowMode(FlowTagLayout.FLOW_TAG_SHOW_SINGLE_LINE)//单行显示
tagLayout.setTagCancelable(true)//选中不可取消
tagLayout.setAdapter(object : BaseFlowAdapter<KSelectBean.KTagBean, BaseTagHolder>(layout.adapter_radius_25_select_tag_item, tags) {
    override fun convert(tagHelper: BaseTagHolder, item: KSelectBean.KTagBean?) {
        tagHelper.setText(R.id.tag_item, item?.tagName)
        tagHelper.getView<View>(R.id.tag_item).isSelected = item?.isChecked()
                ?: false
    }
})
```
