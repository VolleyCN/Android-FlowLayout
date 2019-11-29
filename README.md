# FlowLayout
流式布局，适合用于标签，分类筛选等。适配器方式调用，支持单选，多选，自用显示，单行显示（类似listview），指定显示条数（类似gridview）<br><br>
<br><br>
![demo](https://github.com/VolleyCN/Android-FlowLayout/blob/master/image/demo.gif)
<br><br>
怎么样使用？
1.在项目根build.gradle中
allprojects { <br>
	repositories { <br>
		... <br>
		maven { url 'https://jitpack.io' } <br>
	} <br>
} <br>

2.在项目build.gradle中 <br>
kotlin 版本 
dependencies {
	 implementation 'com.github.VolleyCN.Android-FlowLayout:librarykotlin:1.0.1'
}
	
java 版本
dependencies {
	 implementation 'com.github.VolleyCN.Android-FlowLayout:library:1.0.1'
}
