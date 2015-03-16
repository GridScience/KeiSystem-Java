package com.mic.keisystem.tracker;

/**
 * Created by MIC/Headcrabbed on 2015/3/15.
 */
public enum HttpResponseCode {

    ok(200), serverError(500);

    private final int code;

    HttpResponseCode(int code) {
        this.code = code;
    }

    public static final int OK = 200;

    public static final int SERVER_ERRROR = 500;

}
