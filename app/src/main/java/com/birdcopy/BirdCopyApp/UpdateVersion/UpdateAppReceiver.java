package com.birdcopy.BirdCopyApp.UpdateVersion;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;

import java.io.File;

/**
 * Created by songbaoqiang on 6/20/14.
 */
public class UpdateAppReceiver extends BroadcastReceiver {

    DownloadManager downloadManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);

            String url = MyApplication.getSharedPreference().getString(ShareDefine.KAppDownloadURL,"");

            long download_id = MyApplication.getSharedPreference().getLong(url, 0);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = downloadManager.query(query);

            String path =null;
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Uri uri = Uri.parse(uriString);
                    path = uri.getPath();
                }
                cursor.close();
            }
            if(id == download_id && path !=null){
                installApk(path,context);
            }
        }
    }

    private void installApk(String path,Context context)
    {
        File file = new File(path);
        if(!file.exists()){
            return ;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
