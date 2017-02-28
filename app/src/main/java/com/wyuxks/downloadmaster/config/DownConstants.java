package com.wyuxks.downloadmaster.config;

import android.os.Environment;

/**
 * 创建者     xieshao
 * 创建时间   2017/2/27 23:15
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DownConstants {
    public static final String DOWN_PATH = Environment.getExternalStorageDirectory().toString(); //未下载
    public static final int UNDOWNLOAD = 1; //未下载
    public static final int DOWNLOADING = 2; //正在下载
    public static final int STOP_DOWNLOADING = 3; //暂停下载
    public static final int DOWNLOADEDFINISH = 4; //已下载
    public static final int DOWNLOADFAIL= 5; //下载失败
    public static final int WAIT_DOWNLOAD = 6; //等待下载

}
