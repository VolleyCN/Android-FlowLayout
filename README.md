# FlowLayout
流式布局，适合用于标签，分类筛选等。适配器方式调用，支持单选，多选，自用显示，单行显示（类似listview），指定显示条数（类似gridview）

怎么样使用？
1.在项目根build.gradle中
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

2.在项目build.gradle中
dependencies {
	        implementation 'com.github.VolleyCN:FlowLayout:1.0.13'
}
