package com.birdcopy.BirdCopyApp.Component.Base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DicDaoMaster;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DicDaoSession;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DaoMaster;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DaoSession;

import com.birdcopy.BirdCopyApp.Component.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.service.ServiceManager;
import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingContext;
import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingSysWithCenter;
import com.birdcopy.BirdCopyApp.Component.UserManger.OpenUDID_manager;

import com.birdcopy.BirdCopyApp.Component.UserManger.SSKeychain;
import com.birdcopy.BirdCopyApp.IM.DeContactNotificationMessageProvider;
import com.birdcopy.BirdCopyApp.IM.RongCloudEvent;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.io.IOException;

import io.rong.imkit.RongIM;

public class MyApplication extends Application
{

    private static MyApplication mInstance;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private static DicDaoMaster dicDaoMaster;
    private static DicDaoSession dicDaoSession;

    private static FlyingDownloadManager downloadManager;

    private static ServiceManager serviceManager;

    //private static AsyncHttpClient nonNIOasyncHttpClient;

    public  static int upgradeCount=0;

    public static MyApplication getInstance() {

        return mInstance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        if(mInstance == null)
        {
            mInstance = this;
        }

        setOPENUDID();

        initRongCloud();

        initImageLoader(getApplicationContext());

        //CrashHandler crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());
    }

    public void setOPENUDID()
    {
        OpenUDID_manager.sync(getApplicationContext());
    }

    public void initRongCloud()
    {
        /**
         * IMKit SDK调用第一步 初始化
         * context上下文
         */
        RongIM.init(this);

        /**d
         * 融云SDK事件监听处理
         */
        RongCloudEvent.init(this);

        FlyingContext.init(this);

        //注册消息类型的时候判断当前的进程是否在主进程
        if ("io.rong.app".equals(getCurProcessName(getApplicationContext()))) {
            try {
                //注册自定义消息,注册完消息后可以收到自定义消息
                //RongIM.registerMessageType(DeAgreedFriendRequestMessage.class);
                //注册消息模板，注册完消息模板可以在会话列表上展示
                RongIM.registerMessageTemplate(new DeContactNotificationMessageProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得当前进程号
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    /** 初始化ImageLoader */
    public static void initImageLoader(Context context)
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(1920, 1080) // default = device screen dimensions
            .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 取得userDaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,ShareDefine.KUserDatdbaseFilename, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得userDaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 取得dicDaoMaster
     *
     * @param context
     * @return
     */
    public static DicDaoMaster getDicDaoMaster(Context context) {
        if (dicDaoMaster == null) {
            DicDaoMaster.OpenHelper helper = new DicDaoMaster.DevOpenHelper(context,ShareDefine.KBaseDatdbaseFilename, null);
            dicDaoMaster = new DicDaoMaster(helper.getWritableDatabase());
        }
        return dicDaoMaster;
    }

    /**
     * 取得dicDaoSession
     *
     * @param context
     * @return
     */
    public static DicDaoSession getDicDaoSession(Context context) {
        if (dicDaoSession == null) {
            if (dicDaoMaster == null) {
                dicDaoMaster = getDicDaoMaster(context);
            }
            dicDaoSession = dicDaoMaster.newSession();
        }
        return dicDaoSession;
    }

    public static FlyingDownloadManager getLeesonDownloadManager()
    {
        if (downloadManager == null) {

            downloadManager = new FlyingDownloadManager();
        }
        return downloadManager;
    }

    public static SharedPreferences getSharedPreference()
    {
        return MyApplication.getInstance().getSharedPreferences("mySetting",MODE_PRIVATE);
    }

    public static ServiceManager getHttpDownloadServiceManager()
    {

        if(serviceManager==null)
        {
            serviceManager = ServiceManager.getInstance(MyApplication.getInstance());
        }

        return  serviceManager;
    }

    public static void disConnectHttpDownloadServiceManager()
    {
        if(downloadManager!=null && serviceManager!=null)
        {

            if(downloadManager.getCurrentTask()==0)
            {
                serviceManager.disConnectService();
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 0.10f;

    public void playbeepAndsVibrate()
    {
        playSound(R.raw.beep);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_DURATION);
    }

    public void playCoinSound()
    {
        playSound(R.raw.coin);
    }

    private void playSound(int ID)
    {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        AssetFileDescriptor file = getResources().openRawResourceFd(ID);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
        }
        catch (IOException e)
        {
            mediaPlayer = null;
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
        }
    }

    public void onLowMemory()
    {
        super.onLowMemory();

        //系统内存降低的时候，回收bitmap缓存
        ImageLoader.getInstance().clearMemoryCache();
    }
}
