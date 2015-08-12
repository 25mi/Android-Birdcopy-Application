/**
 * Copyright (c) www.bugull.com
 */
package com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils.DownloadConstants;
import com.birdcopy.service.IDownloadService;

public class ServiceManager {

	private final String TAG = ServiceManager.class.getSimpleName();
	private Context mContext;
	private ServiceConnection mConn;
	private IDownloadService mService;
	
	private static ServiceManager mManager;
	
	public static ServiceManager getInstance(Context context) {
		if(mManager == null) {
			mManager = new ServiceManager(context);
		}
		return mManager;
	}
	
	private ServiceManager(Context context) {
		this.mContext = context;
		initConn();
	}

	private void initConn() {
		mConn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IDownloadService.Stub.asInterface(service);
			}
		};
		connectService();
	}
	
	public void connectService()
    {

        Intent intent = new Intent(ShareDefine.getKSERVICE_ACTION());
        intent.setClass(MyApplication.getInstance().getApplicationContext(), com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.service.DownloadService.class);
		mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
	}
	
	public void disConnectService()
    {
        mContext.unbindService(mConn);

        Intent intent = new Intent(ShareDefine.getKSERVICE_ACTION());
        intent.setClass(MyApplication.getInstance().getApplicationContext(), com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.service.DownloadService.class);
		mContext.stopService(intent);
	}
	
	public void addTask(String url) {
		if(mService != null) {
			try {
				mService.addTask(url);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	public void pauseTask(String url) {
		if(mService != null) {
			try {
				mService.pauseTask(url);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	public void deleteTask(String url) {
		if(mService != null) {
			try {
				mService.deleteTask(url);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	public void continueTask(String url) {
		if(mService != null) {
			try {
				mService.continueTask(url);
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
}
