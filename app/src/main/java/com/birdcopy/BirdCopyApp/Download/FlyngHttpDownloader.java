package com.birdcopy.BirdCopyApp.Download;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.service.ServiceManager;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.utils.MyIntents;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

/**
 * Created by vincentsung on 1/8/16.
 */
public class FlyngHttpDownloader {

    private   String mLessonID;
    private   String mContentURL=null;

    private   String mFileName="unknown";
    private   String mFolderName="downloads";

    public FlyngHttpDownloader(String mLessonID)
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

            Ion.with(MyApplication.getInstance())
                    .load(mContentURL)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {

                            long process = 100*downloaded/total;

                            Intent updateIntent = new Intent(mLessonID);
                            updateIntent.putExtra(ShareDefine.KIntenCorParameter,process );
                            MyApplication.getInstance().sendBroadcast(updateIntent);
                        }
                    })
                    .write(new File(""))
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {

                        }
                    });
        }
    }

    public void continueDownload()
    {

    }

    public void cancelDownload()
    {
    }

    public void pauseDownload()
    {
        
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
}
