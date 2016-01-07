package com.birdcopy.BirdCopyApp.Download.HttpDownloader;

import android.content.*;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.db.DownloadDao;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.service.ServiceManager;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.utils.MyIntents;

import java.io.File;

/**
 * Created by songbaoqiang on 6/14/14.
 */
public class FlyingNormalDownLoader
{

    private   String mLessonID;
    private   String mContentURL=null;

    private   String mFileName="unknown";
    private   String mFolderName="downloads";

    private HttpDownloadReceiver mReceiver;

    public FlyingNormalDownLoader()
    {
        super();
    }
    
    public FlyingNormalDownLoader(String mLessonID)
    {
        super();
        this.mLessonID=mLessonID;
    }

    public void startDownload()
    {
        if(mLessonID!=null)
        {
            BE_PUB_LESSON lessonData = new FlyingContentDAO().selectWithLessonID(mLessonID);
            mContentURL=lessonData.getBECONTENTURL();

            //删除以前同样内容
            String path= new FlyingContentDAO().selectWithLessonID(mLessonID).getLocalURLOfContent();
            if(path!=null)
            {
                File contenFile = new File(path);
                if (contenFile.exists())
                {
                    FlyingFileManager.deleteFile(path);
                }
            }

            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
            editor.putString(mContentURL, mLessonID);
            editor.commit();

            ServiceManager.getInstance().addTask(mContentURL);

            mReceiver = new HttpDownloadReceiver();
            IntentFilter filter = new IntentFilter();

            filter.addAction(ShareDefine.getKRECEIVER_ACTION());
            MyApplication.getInstance().registerReceiver(mReceiver, filter);
        }
    }

    public void continueDownload()
    {
        ServiceManager.getInstance().continueTask(mContentURL);
    }

    public void cancelDownload()
    {

        ServiceManager.getInstance().deleteTask(mContentURL);

        MyApplication.getInstance().unregisterReceiver(mReceiver);
    }

    public void pauseDownload()
    {
        ServiceManager.getInstance().pauseTask(mContentURL);
    }


    private void  finishDownloadTask()
    {
        if(mLessonID!=null)
        {
            BE_PUB_LESSON lessonData = new FlyingContentDAO().selectWithLessonID(mLessonID);

            if(lessonData.getLocalURLOfContent()==null)
            {
                try
                {
                    lessonData.setBEDLPERCENT(1.00);
                    lessonData.setBEDLSTATE(false);

                    new FlyingContentDAO().savelLesson(lessonData);
                }
                catch (Exception e)
                {
                    //
                }
            }
        }
    }
    
    public class HttpDownloadReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null
                    && intent.getAction().equals(
                    ShareDefine.getKRECEIVER_ACTION())
                    && intent.getStringExtra(MyIntents.URL).equals(mContentURL)
                    )
            {
                int type = intent.getIntExtra(MyIntents.TYPE, -1);
                switch (type)
                {
                    case MyIntents.Types.COMPLETE:
                    {
                        finishDownloadTask();
                        ServiceManager.getInstance().deleteTask(mContentURL);
                        MyApplication.getInstance().unregisterReceiver(mReceiver);

                        DownloadDao dao= new DownloadDao(MyApplication.getInstance());
                        dao.deleteByUrl(mContentURL);

                        disConnectHttpDownloadServiceManager();
                    }
                    break;
                    case MyIntents.Types.ERROR:
                    {
                        ServiceManager.getInstance().deleteTask(mContentURL);
                        MyApplication.getInstance().unregisterReceiver(mReceiver);

                        DownloadDao dao= new DownloadDao(MyApplication.getInstance());
                        dao.deleteByUrl(mContentURL);

                        disConnectHttpDownloadServiceManager();
                    }
                    break;
                }
            }
        }
    }

    public static void disConnectHttpDownloadServiceManager()
    {
        if(FlyingDownloadManager.getInstance().getTaskCount()==0)
        {
            ServiceManager.getInstance().disConnectService();
        }
    }
}
