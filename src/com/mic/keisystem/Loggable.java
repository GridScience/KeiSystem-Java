package com.mic.keisystem;

/**
 * Created by MIC/Headcrabbed on 2015/3/13.
 */

/**
 * 一个简单的日志记录接口。
 */
public interface Loggable {

    /**
     * 记录日志。
     * @param text 要记录的日志内容。
     */
    public void log(String text);

}
