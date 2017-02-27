package com.eegsmart.eegenjoysleep.manager;


import android.util.Log;

import com.eegsmart.eegenjoysleep.config.MusicConstants;
import com.eegsmart.eegenjoysleep.control.config.AppConfig;
import com.eegsmart.eegenjoysleep.entry.DownLoadInfo;
import com.eegsmart.eegenjoysleep.entry.MusicInfo;
import com.eegsmart.eegenjoysleep.factory.ThreadPoolProxyFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * 创建者      xks
 * 创建时间   2016/8/23 14:12
 * 描述	      歌曲下载管理器,负责下载模块,封装所有和下载相关的逻辑
 * 描述	      1.需要`时刻记录`当前的状态
 * 描述	      2.根据传递过来的ItemInfoBean信息返回一个DownLoadInfo对象
 * 描述	      3.当DownLoadInfo里面的状态发生改变的时候,发布广播-->EventBus
 */
public class DownLoadManger {

    private static final String TAG = DownLoadManger.class.getSimpleName();
    private static DownLoadManger instance;
    private HttpURLConnection urlConnection;
    private Map<String, DownLoadInfo> mCacheDownLoadInfoMap = new HashMap<>();

    private DownLoadManger() {
    }

    public static DownLoadManger getInstance() {
        if (instance == null) {
            synchronized (DownLoadManger.class) {
                if (instance == null) {
                    instance = new DownLoadManger();
                }
            }
        }
        return instance;
    }


    /**
     * @param musicInfo
     * @des 触发加载
     * @called 用户点击了下载按钮的时候
     */
    public void downLoad(MusicInfo musicInfo) {

        DownLoadInfo downLoadInfo = getDownLoadInfo(musicInfo);
        //保存到集合中
        mCacheDownLoadInfoMap.put(downLoadInfo.name, downLoadInfo);

        /*############### 当前状态:未下载 ###############*/
        musicInfo.setDownStatus(MusicConstants.DOWNLOADING);

        /*############### 当前状态:等待状态 ###############*/
        musicInfo.setDownStatus(MusicConstants.WAIT_DOWNLOAD);

        /**
         预先把状态设置为等待状态
         1.假如任务立马执行-->立马切换到-->下载中的状态
         2.假如任务没有执行-->保持预先设置的等待状态
         */
        //异步下载
        DownLoadTask downLoadTask = new DownLoadTask(downLoadInfo);

        //downLoadInfo中记录对应的下载任务
        downLoadInfo.downLoadTask = downLoadTask;
        ThreadPoolProxyFactory.createDownLoadThreadPoolProxy().execute(downLoadTask);

    }

    class DownLoadTask implements Runnable {
        private final DownLoadInfo downLoadInfo;

        public DownLoadTask(DownLoadInfo downLoadInfo) {
            this.downLoadInfo = downLoadInfo;
        }

        @Override
        public void run() {
            /*############### 当前状态:下载中 ###############*/
            downLoadInfo.curState = MusicConstants.DOWNLOADING;

            //downLoadInfo里面的状态发生改变了.通知其他界面

            long initRange = 0;
            File saveApk = new File(downLoadInfo.savePath);
            //等于部分文件的大小
            if (saveApk.exists()) {
                initRange = saveApk.length();
            }

            //处理上一次的ui进度
            downLoadInfo.progress = initRange;
            InputStream in = null;
            FileOutputStream out = null;
            try {
                //真正的开始下载数据
                //发起请求
                URL url = new URL(downLoadInfo.url);
                urlConnection = (HttpURLConnection) url.openConnection();
                // 设置连接超时时间
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                // 获取线程已经下载的进度
                urlConnection.setRequestProperty("range", "bytes=" + initRange + "-" + downLoadInfo.max);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200 || responseCode ==206) {
                    in = urlConnection.getInputStream();
                    out = new FileOutputStream(saveApk, true);//写文件的时候已追加的方式进行写入
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = in.read(buffer)) != -1) {
                        /*############### 当前状态:下载中 ###############*/
                        downLoadInfo.curState = MusicConstants.DOWNLOADING;
                        downLoadInfo.progress += len;
                        //downLoadInfo里面的状态发生改变了.通知其他界面
                        EventBus.getDefault().post(downLoadInfo);
                        out.write(buffer, 0, len);
                        //使用okHttpClient进行文件的读写的时候加上如下代码
                        if (saveApk.length() == downLoadInfo.max) {//写完了,主动跳出while循环
                            break;
                        }
                    }
                    downLoadInfo.curState = MusicConstants.DOWNLOADEDFINISH;
                    mCacheDownLoadInfoMap.remove(downLoadInfo.name);
                    Log.e(TAG, downLoadInfo.name + " download finish...");
                    //downLoadInfo里面的状态发生改变了.通知其他界面
                    EventBus.getDefault().post(downLoadInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                downLoadInfo.curState = MusicConstants.DOWNLOADFAIL;
                //downLoadInfo里面的状态发生改变了.通知界面
                EventBus.getDefault().post(downLoadInfo);
                Log.e(TAG, "download fail...");
            } finally {
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * @param itemInfoBean 限制条件
     * @return
     * @des 根据传递过来的ItemInfoBean信息返回一个DownLoadInfo对象
     */
    public DownLoadInfo getDownLoadInfo(MusicInfo itemInfoBean) {
        DownLoadInfo downLoadInfo = new DownLoadInfo();
        //赋值-->常规的赋值-->其他字段赋值
        String dir = AppConfig.getInstance().getMusicDirPath();
        String url = itemInfoBean.getUrl();
        String substring = url.substring(url.length() - 3);
        String fileName = itemInfoBean.getName() + "." + substring;
        File saveFile = new File(dir, fileName);


        downLoadInfo.max = itemInfoBean.getSize();
        downLoadInfo.name = itemInfoBean.getName();
        downLoadInfo.savePath = saveFile.getAbsolutePath();
        downLoadInfo.url = itemInfoBean.getUrl();


//        //下载状态赋值-->重要的赋值
//        //下载完成
        File saveMusic = new File(downLoadInfo.savePath);
        if (saveMusic.exists() && saveMusic.length() == downLoadInfo.max) {
//            Log.e(TAG, "saveMusic.length():" + saveMusic.length() + " downLoadInfo.max : " + downLoadInfo.max);
            downLoadInfo.curState = MusicConstants.DOWNLOADEDFINISH;//已下载
            return downLoadInfo;
        }
        /**
         下载中
         暂停下载
         等待下载
         下载失败
         */
        //说明,itemInfoBean对应的DownLoadInfo用户肯定点击了下载按钮进行了触发下载
        if (mCacheDownLoadInfoMap.containsKey(itemInfoBean.getName())) {
            downLoadInfo = mCacheDownLoadInfoMap.get(itemInfoBean.getName());
            return downLoadInfo;
        }
        //未下载
        downLoadInfo.curState = MusicConstants.UNDOWNLOAD;
        return downLoadInfo;
    }

}
