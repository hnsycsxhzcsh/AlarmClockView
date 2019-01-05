# AlarmClockView
Android自定义控件时钟、钟表AlarmClockView，仿华为手机世界时钟控件效果

<a href="https://github.com/hnsycsxhzcsh/AlarmClockView/blob/master/myres/alarmclock.apk">Download Apk</a>

效果图

<img src="https://github.com/hnsycsxhzcsh/AlarmClockView/blob/master/myres/alarmclock.gif" width="300" height="612">

项目中引用的步骤：

步骤1.将JitPack存储库添加到构建文件中

项目的根build.gradle中添加以下代码：

 	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

步骤2.build.gradle添加依赖项


	dependencies {
         implementation 'com.github.hnsycsxhzcsh:AlarmClockView:v1.3'
	}

步骤3. 布局中引用控件

	<com.alarmclockview.AlarmClockView
        android:id="@+id/clock"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

步骤4. activity中添加监听

    mClock = findViewById(R.id.clock);
    //运行闹钟
    mClock.start(new TimeChangeListener() {
             @Override
             public void onTimeChange(Calendar calendar) {
                  //根据calendar获取当前时间

              }
        });

其他功能：

setIsNight(boolean)  设置是否是夜间效果true是夜间效果，false不是夜间效果

stop()  停止自动运行闹钟

setCurrentTime(Calendar)  自定义当前时间

我的博客地址：https://blog.csdn.net/m0_38074457/article/details/85790550
        
如果有帮助到大家希望点下右上角Star，谢谢！

