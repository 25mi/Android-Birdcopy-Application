package com.birdcopy.BirdCopyApp.UpdateVersion;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.*;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Download.FlyingHttpDownLoader;

import javax.xml.transform.Result;

/**
 * Created by songbaoqiang on 6/20/14.
 */
public class UpdateAppDownload extends AsyncTask<Void, Void, Result>
{

    public UpdateAppDownload()
    {

    }

    @Override
    protected Result doInBackground(Void... arg0) {
        if(ShareDefine.checkNetWorkStatus()&&ShareDefine.checkSDcardStatus()){
            downloadNewApp();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }

    public boolean compareVersionCode(String serverCode)
    {
        if(serverCode!=null)
        {
            try
            {
                if(Integer.parseInt(serverCode)>ShareDefine.getVersionCode()){
                    return true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void downloadNewApp()
    {
        FlyingHttpDownLoader downLoader = new FlyingHttpDownLoader();

        downLoader.setmWifiOnly(true);
        downLoader.setmContentURL(ShareDefine.downloadURL);
        downLoader.startDownload();

        UpdateAppReceiver receiver = new UpdateAppReceiver();
        IntentFilter intent = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        MyApplication.getInstance().registerReceiver(receiver,intent);
    }
}