package com.mic.keisystem;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 * 表示日志记录器。
 */
public class Logger implements Loggable {

    /**
     * 全局唯一的空记录器实例。
     */
    public static final Loggable NULL = new NullLogger();

    /**
     * 记录日志。
     * @param text 要记录的日志内容。
     */
    @Override
    public void log(String text) {
    }

    /**
     * 一个空记录器类，该类实现了 {@code Loggable} 接口，但是内部不执行记录操作。
     */
    private static final class NullLogger extends Logger {

        /**
         * 向空记录器写入日志。内部不执行任何操作。
         * @param text 要记录的日志内容。
         */
        @Override
        public void log(String text) {
        }

    }
}
