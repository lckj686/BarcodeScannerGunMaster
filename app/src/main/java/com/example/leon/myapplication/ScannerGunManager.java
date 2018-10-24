package com.example.leon.myapplication;

import android.view.KeyEvent;

/**
 * 拦截扫码枪和外接设备 的输入事件
 * <p>
 * 扫码枪和 外接键盘的处理是一样的
 */
public class ScannerGunManager {

    String codeStr = "";
    OnScanListener listener;

    boolean isInterrupt = true;

    public ScannerGunManager(OnScanListener listener) {
        this.listener = listener;
    }

    /**
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


    public interface OnScanListener {

        void onResult(String code);
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }
}
