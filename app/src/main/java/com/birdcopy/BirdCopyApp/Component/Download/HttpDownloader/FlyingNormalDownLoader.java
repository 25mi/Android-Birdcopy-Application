package com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader;

import android.content.*;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.db.DownloadDao;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.service.ServiceManager;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils.DownloadConstants;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils.MyIntents;

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

            BE_PUB_LESSON lessonData = new FlyingLessonDAO().selectWithLessonID(mLessonID);
            mContentURL=lessonData.getBECONTENTURL();

            //删除以前同样内容
            String path=ShareDefine.getLessonContentPath(mLessonID,mContentURL);
            if(path!=null)
            {
                File contenFile = new File(path);
                if (contenFile.exists())
                {
                    ShareDefine.deleteFile(path);
                }
            }

            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
            editor.putString(mContentURL, mLessonID);
            editor.commit();

            getHttpDownloadServiceManager().addTask(mContentURL);

            mReceiver = new HttpDownloadReceiver();
            IntentFilter filter = new IntentFilter();

            filter.addAction(ShareDefine.getKRECEIVER_ACTION());
            MyApplication.getInstance().registerReceiver(mReceiver, filter);
        }
    }

    public void continueDownload()
    {
        getHttpDownloadServiceManager().continueTask(mContentURL);
    }

    public void cancelDownload()
    {

        getHttpDownloadServiceManager().deleteTask(mContentURL);

        MyApplication.getInstance().unregisterReceiver(mReceiver);
    }

    public void pauseDownload()
    {
        getHttpDownloadServiceManager().pauseTask(mContentURL);
    }

    private ServiceManager getHttpDownloadServiceManager()
    {
        return  MyApplication.getHttpDownloadServiceManager();
    }

    private void  finishDownloadTask()
    {
        if(mLessonID!=null)
        {
            BE_PUB_LESSON lessonData = new FlyingLessonDAO().selectWithLessonID(mLessonID);

            if(lessonData.getLocalURLOfContent()==null)
            {
                try
                {
                    String path =ShareDefine.getLessonContentPath(mLessonID,lessonData.getBECONTENTURL());

                    lessonData.setLocalURLOfContent(path);
                    lessonData.setBEDLPERCENT(1.00);
                    lessonData.setBEDLSTATE(false);

                    new FlyingLessonDAO().savelLesson(lessonData);
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
                        getHttpDownloadServiceManager().deleteTask(mContentURL);
                        MyApplication.getInstance().unregisterReceiver(mReceiver);

                        DownloadDao dao= new DownloadDao(MyApplication.getInstance());
                        dao.deleteByUrl(mContentURL);

                        MyApplication.disConnectHttpDownloadServiceManager();
                    }
                    break;
                    case MyIntents.Types.ERROR:
                    {
                       getHttpDownloadServiceManager().deleteTask(mContentURL);
                        MyApplication.getInstance().unregisterReceiver(mReceiver);

                        DownloadDao dao= new DownloadDao(MyApplication.getInstance());
                        dao.deleteByUrl(mContentURL);

                        MyApplication.disConnectHttpDownloadServiceManager();
                    }
                    break;
                }
            }
        }
    }
}
