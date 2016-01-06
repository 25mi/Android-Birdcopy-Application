package com.birdcopy.BirdCopyApp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;

import com.birdcopy.BirdCopyApp.DataManager.FlyingDBManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingIMContext;
import com.birdcopy.BirdCopyApp.DataManager.OpenUDID_manager;

import com.birdcopy.BirdCopyApp.IM.ContactNotificationMessageProvider;
import com.birdcopy.BirdCopyApp.IM.DeAgreedFriendRequestMessage;
import com.birdcopy.BirdCopyApp.IM.NewDiscussionConversationProvider;
import com.birdcopy.BirdCopyApp.IM.RealTimeLocationMessageProvider;
import com.birdcopy.BirdCopyApp.IM.RongCloudEvent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.io.IOException;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;

public class MyApplication extends MultiDexApplication
{
    private static MyApplication mInstance;

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

        FlyingDataManager.init();
        FlyingDBManager.init();
        initRongCloud();

        initImageLoader(getApplicationContext());

        //CrashHandler crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());
    }

    public void initRongCloud()
    {
        /**
         * 注意：
         *
         * IMKit SDK调用第一步 初始化
         *
         * context上下文
         *
         * 只有两个进程需要初始化，主进程和 push 进程
         */

        if (getString(R.string.KPakagename).equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            RongIM.init(this);

            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (getString(R.string.KPakagename).equals(getCurProcessName(getApplicationContext()))) {

                RongCloudEvent.init(this);
                FlyingIMContext.init(this);

                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

                try {
                    RongIM.registerMessageType(DeAgreedFriendRequestMessage.class);

                    RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                    RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());

                    //@ 消息模板展示
                    RongContext.getInstance().registerConversationTemplate(new NewDiscussionConversationProvider());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


    public static SharedPreferences getSharedPreference()
    {
        return MyApplication.getInstance().getSharedPreferences("mySetting",MODE_PRIVATE);
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
