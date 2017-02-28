package com.wyuxks.downloadmaster.factory;


import com.wyuxks.downloadmaster.manager.ThreadPoolProxy;

/**
 * 创建者     xks
 * 创建时间   2016/8/22 16:23
 * 描述	      ${TODO}
 * 更新者     $Author: admin $
 * 更新描述   ${TODO}
 */
public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ThreadPoolProxy mDownLoadThreadPoolProxy;

    /**
     * 返回普通线程池代理
     *
     * @return
     */
    public static ThreadPoolProxy createNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(1, 1, 3000);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    /**
     * 返回下载线程池代理
     *
     * @return
     */
    public static ThreadPoolProxy createDownLoadThreadPoolProxy() {
        if (mDownLoadThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mDownLoadThreadPoolProxy == null) {
                    mDownLoadThreadPoolProxy = new ThreadPoolProxy(3, 3, 3000);
                }
            }
        }
        return mDownLoadThreadPoolProxy;
    }
}
