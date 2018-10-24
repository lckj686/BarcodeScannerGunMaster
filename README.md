## Android  扫码枪 读取（外接键盘读取）

### 1、概述
android 设备外接一个 标准扫码枪，要把扫码枪扫到的内容取出来。界面上放一个EditTextView 直接就把内容显示到EditTextView中了。 然而有些界面上并不能摆EditTextView 。针对没有EditTextView的界面展开下文。扫码枪和外接键盘原理是一样的，类比，也特意拿了个外接键盘一起调研了。

### 2、扫码枪-输入设备
项目中使用的是标准的扫码枪（实验的是新大陆的NLS-FR40），标准的意思就是它都不给开发文档。查了下说是走的标准“输入事件”，和外接键盘是一样的。既然是输入事件，就掐Activity 的 dispatchKeyEvent 方法了。

```@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
```

 扫码枪在识别到扫的码后，会多一个KEYCODE_ENTER，和KEYCODE_DPAD_DOWN 事件，查阅的资料里都有说到KEYCODE_ENTER，没提到KEYCODE_DPAD_DOWN，也不知道其它扫码枪会不会生成这个事件
### 3、实验结果
在Activity 的dispatchKeyEvent 方法中把 KeyEvent  log 打印了下：（只打出action=ACTION_UP 躺起的log，按下action=ACTION_DOWN 是结队的 忽略）

 - 3.1、android 设备软键盘的log
![在这里插入图片描述](https://img-blog.csdn.net/20181024151230439?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xja2o2ODY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 - 3.2、外接扫码枪的log （新大陆的NLS-FR40）
 ![在这里插入图片描述](https://img-blog.csdn.net/20181024151415991?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xja2o2ODY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

 - 3.2、外接键盘的log （普通的键盘）
![在这里插入图片描述](https://img-blog.csdn.net/20181024151452348?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xja2o2ODY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
这里附一句，若小键盘的num按钮锁住，metaState= meta_num_lock_on

对比结论小结：
1. 标准外接扫描枪和标准外接键盘是类似的输入设备
2. 自带软件盘的输入事件里，deviceId，source，scanCode，flag 和外接的设备不同
3. 扫码枪和外接键盘的 deviceId，source 不同


### 4、查看KeyEvent源码进行比较
简单的查看下keyEvent 的源码，可以明显的看到，设备的虚拟软键盘是把deiveId=KeyCharacterMap.VIRTUAL_KEYBOARD （-1），写死了，所以取该字段的不同来区分是软键盘还是  外接键盘。 目前不打算区分扫描枪和外接键盘。

```/**
     * Create a new key event.
     *
     * @param downTime The time (in {@link android.os.SystemClock#uptimeMillis})
     * at which this key code originally went down.
     * @param eventTime The time (in {@link android.os.SystemClock#uptimeMillis})
     * at which this event happened.
     * @param action Action code: either {@link #ACTION_DOWN},
     * {@link #ACTION_UP}, or {@link #ACTION_MULTIPLE}.
     * @param code The key code.
     * @param repeat A repeat count for down events (> 0 if this is after the
     * initial down) or event count for multiple events.
     */
    public KeyEvent(long downTime, long eventTime, int action,
                    int code, int repeat) {
        mDownTime = downTime;
        mEventTime = eventTime;
        mAction = action;
        mKeyCode = code;
        mRepeatCount = repeat;
        mDeviceId = KeyCharacterMap.VIRTUAL_KEYBOARD;
    }

    

    /**
     * Create a new key event.
     *
     * @param downTime The time (in {@link android.os.SystemClock#uptimeMillis})
     * at which this key code originally went down.
     * @param eventTime The time (in {@link android.os.SystemClock#uptimeMillis})
     * at which this event happened.
     * @param action Action code: either {@link #ACTION_DOWN},
     * {@link #ACTION_UP}, or {@link #ACTION_MULTIPLE}.
     * @param code The key code.
     * @param repeat A repeat count for down events (> 0 if this is after the
     * initial down) or event count for multiple events.
     * @param metaState Flags indicating which meta keys are currently pressed.
     * @param deviceId The device ID that generated the key event.
     * @param scancode Raw device scan code of the event.
     */
    public KeyEvent(long downTime, long eventTime, int action,
                    int code, int repeat, int metaState,
                    int deviceId, int scancode) {
        mDownTime = downTime;
        mEventTime = eventTime;
        mAction = action;
        mKeyCode = code;
        mRepeatCount = repeat;
        mMetaState = metaState;
        mDeviceId = deviceId;
        mScanCode = scancode;
    }
```
### 5、拦截策略
需要一点android “事件传递” 的基础知识，面试必备知识。以前也记录过：[Android 事件传递与焦点处理(tv)](https://blog.csdn.net/lckj686/article/details/44858387)
在Activity 中事件传递，特别是按键的拦截其实很方便，重写dispatchKeyEvent 方法就可以了。重写的思路也很简单：判断是不是扫描枪用deviceId == -1 来判断。
伪代码

```@Override
   public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "event= " + event);

        if (如果是扫描枪的事件) {
         //直接消费掉，不继续向下传，editTextView也不自动填充了，KEYCODE_ENTER 事件也不影响 其它控件了，比如button 的点击事件
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
```
实际使用中，往往没有这么暴力，比如要对是否完全拦截进行控制，单独封装管理工具类，这些属于封装技巧了，在章末有简单封装

```/**
     * 处理输入事件
     *
     * @param event
     * @return true 表示消费掉，拦截不在传递， false 不管
     */
    public boolean dispatchKeyEvent(KeyEvent event) {

        /**
         * 系统的软键盘  按下去是 -1, 不管，不拦截
         */
        if (event.getDeviceId() == -1) {
            return false;
        }

        //按下弹起，识别到弹起的话算一次 有效输入
        //只要是 扫码枪的事件  都要把他消费掉 不然会被editText 显示出来
        if (event.getAction() == KeyEvent.ACTION_UP) {

            //只要数字，一维码里面没有 字母
            int code = event.getKeyCode();
            if (code >= KeyEvent.KEYCODE_0 && code <= KeyEvent.KEYCODE_9) {

                codeStr += (code - KeyEvent.KEYCODE_0);
            }

            //识别到结束，当下使用的设备是  是还会有个KEYCODE_DPAD_DOWN 事件，不知道其它设备有没有  先忽略
            if (code == KeyEvent.KEYCODE_ENTER) {

                if (listener != null) {
                    listener.onResult(codeStr);
                    codeStr = "";
                }
            }

        }
        //都是扫码枪来的事件，选择消费掉

        return isInterrupt;
    }
```
### 6、其它处理
项目需要外接扫码枪，扫码枪有几种模式：
1. 短按触发扫码，松开停止
2. 短按触发，连续扫码
3. 感应触发，超时停止 （项目中会用这种方式）

描述这个的原因是，会涉及不相关界面的误操作，比如在x界面，我们去扫码了。如果不处理会导致KEYCODE_ENTER 会响应该界面中的某个按钮点击事件，造成干扰。so 我们需要在这个项目的基类BaseActivity 中对扫码枪的输入事件进行处理。目前我打算使用的处理策略是，BaseActivity 完全拦截扫码枪事件，需要使用到的界面自行打开。这边的处理算封装上的处理就不熬述了，具体见demo代码

### 7、付例与参考
#### 注：
AccessibilityService 的方式，需要手动在：设置->无障碍->服务，中开启，需要人力培训交互不够友好放弃了

#### 参考：
[1]、https://stackoverflow.com/questions/11349542/handle-barcode-scanner-value-via-android-device
[2]、https://blog.csdn.net/csdnno/article/details/79639426

#### 工程demo
代码：https://github.com/lckj686/BarcodeScannerGunMaster
