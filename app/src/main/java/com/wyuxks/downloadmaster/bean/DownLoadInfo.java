package com.wyuxks.downloadmaster.bean;


import com.wyuxks.downloadmaster.config.DownConstants;

/**
 * 创建者      xks
 * 创建时间   2016/8/23 15:15
 * 描述	     封装/存放/组合 和下载相关的参数
 * 更新者     $Author: admin $
 * 更新描述   ${TODO}
 */
public class DownLoadInfo {

    public String name;//下载的具体路径
    public String url;//下载的地址
    public String savePath;//保存的具体路径
    public long   max;//文件的最大长度
    public int curState = DownConstants.UNDOWNLOAD;//当前的状态,默认是未下载
    public long   progress;//记录最新的进度

    public Runnable downLoadTask;//下载的任务
}
