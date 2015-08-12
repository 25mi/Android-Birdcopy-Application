package com.birdcopy.BirdCopyApp.Component.Download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.R;

import java.io.File;

/**
 * Created by songbaoqiang on 6/14/14.
 */
public class FlyingHttpDownLoader
{

    public  long mDownloadID = -1;

    private String mContentURL=null;
    private boolean mWifiOnly=false;

    private String mTitle;
    private String mDesc="没有简介";

    public FlyingHttpDownLoader()
    {
        super();
    }

    public void startDownload()
    {
        if(mDownloadID !=-1)
        {
            return;
        }

        if(mTitle==null)
        {
            mTitle=MyApplication.getInstance().getResources().getString(R.string.app_name);
        }

        String fileName="unknown";
        String folderName="downloads";

        if(mContentURL!=null)
        {
            fileName = mContentURL.substring( mContentURL.lastIndexOf('/')+1, mContentURL.length() );
            folderName = ShareDefine.getUserDownloadPath();
            File folder = Environment.getExternalStoragePublicDirectory(folderName);
            if(!folder.exists())
            {
                return;
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mContentURL));

        request.setTitle(mTitle);
        request.setDescription(mDesc);

        // we just want to download silently
        request.setVisibleInDownloadsUi(false);
        if(mWifiOnly)
        {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }

        request.setDestinationInExternalPublicDir(folderName,fileName);

        // enqueue this request
        Context context = MyApplication.getInstance();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadID = downloadManager.enqueue(request);

        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
        editor.putLong(mContentURL, mDownloadID);
        editor.commit();

    }

    public long getmDownloadID()
    {
        return mDownloadID;
    }

    public void setmWifiOnly(boolean wifiOnly)
    {
        mWifiOnly = wifiOnly;
    }

    public void setmContentURL(String contentURL)
    {
        this.mContentURL = contentURL;
    }

    public void cancelDownload()
    {
        Context context = MyApplication.getInstance();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        downloadManager.remove(mDownloadID);

        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
        editor.remove(mContentURL);
        editor.commit();
    }
}
