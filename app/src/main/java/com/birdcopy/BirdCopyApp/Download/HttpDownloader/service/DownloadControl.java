/**
 * Copyright (c) www.bugull.com
 */
package com.birdcopy.BirdCopyApp.Download.HttpDownloader.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.db.DownloadDao;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.service.DownloadTask.DownloadTaskListener;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.utils.MyIntents;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.utils.NetworkUtils;
import com.birdcopy.BirdCopyApp.Download.HttpDownloader.utils.HttpStorageUtils;

/**
 * 下载核心控制器
 * 
 * @author longdw(longdawei1988@gmail.com)
 * 
 *         2014-1-13
 */
public class DownloadControl extends Thread {
	
	private static final String TAG = DownloadControl.class.getSimpleName();
	private static final int MAX_TASK_COUNT = 100;
	private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

	private Context mContext;
	/** 等待下载的下载队列 */
	private TaskQueue mTaskQueue;
	/** 正在下载的任务 */
	private List<DownloadTask> mDownloadingTasks;
	/** 已经暂停的任务 */
	private List<DownloadTask> mPausedTasks;

	private boolean isRunning = false;
	
	private DownloadDao mDao;
	
	public DownloadControl(Context context) {
		mContext = context;
		mDao = new DownloadDao(context);
		mTaskQueue = new TaskQueue();
		mDownloadingTasks = new ArrayList<DownloadTask>();
		mPausedTasks = new ArrayList<DownloadTask>();
		
		try {
            HttpStorageUtils.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	@Override
	public void run() {
		super.run();
		while (isRunning) {
			DownloadTask task = mTaskQueue.poll();
			mDownloadingTasks.add(task);
			task.execute();
		}
	}

	private DownloadTask newDownloadTask(String url)
			throws MalformedURLException
    {
		DownloadTaskListener listener = new DownloadTaskListener()
        {

			@Override
			public void updateProgress(DownloadTask task) {
				Intent updateIntent = new Intent(
                        ShareDefine.getKRECEIVER_ACTION());
				updateIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PROCESS);
//				updateIntent.putExtra(MyIntents.PROCESS_SPEED,
//						task.getDownloadSpeed());
//				updateIntent.putExtra("speed",
//						task.getDownloadSpeed());
				long percent = task.getDownloadPercent();
				mDao.updateCurrentSizeByUrl(task.getUrl(), task.getDownloadSize());
				updateIntent.putExtra(MyIntents.PROCESS_PROGRESS,
						String.valueOf(percent));
				updateIntent.putExtra(MyIntents.URL, task.getUrl());
//				LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(updateIntent);
				mContext.sendBroadcast(updateIntent);
			}

			@Override
			public void finishDownload(DownloadTask task) {
				completeTask(task, MyIntents.Types.COMPLETE);
			}

			@Override
			public void errorDownload(DownloadTask task, Throwable error) {
				errorTask(task, error);
			}

		};

		return new DownloadTask(mContext, url, HttpStorageUtils.getDownloadDir(url), listener);
	}

	public void addTask(String url) {
		if (!HttpStorageUtils.isSDCardPresent()) {
			Toast.makeText(mContext, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}

		if (!HttpStorageUtils.isSdCardWrittenable()) {
			Toast.makeText(mContext, "SD卡不能读写", Toast.LENGTH_LONG).show();
			return;
		}

		if (getTotalTaskCount() >= MAX_TASK_COUNT) {
			Toast.makeText(mContext, "任务列表已满", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			addTask(newDownloadTask(url));
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void addTask(DownloadTask task) {
		waitTask(task);
		mTaskQueue.offer(task);

		if (!this.isAlive()) {
			isRunning = true;
			this.start();
		}
	}

	public void pauseTask(String url) {
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			DownloadTask task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				pauseTask(task);
				break;
			}
		}
	}

	private void pauseTask(DownloadTask task) {
		if (task != null) {
			task.pause();
			String url = task.getUrl();
			try {
				mDownloadingTasks.remove(task);
				task = newDownloadTask(url);
				mPausedTasks.add(task);
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public void deleteTask(String url) {

		DownloadTask task;
		// 如果是正在下载的任务删除了
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				File file = new File(HttpStorageUtils.FILE_ROOT
						+ NetworkUtils.getFileNameFromUrl(task.getUrl()));
				if (file.exists())
					file.delete();

				task.delete();
				completeTask(task, MyIntents.Types.DELETE);
				break;
			}
		}
		// 如果是待下载的任务删除了
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mTaskQueue.remove(task);
				break;
			}
		}
		// 如果是暂停的任务删除了
		for (int i = 0; i < mPausedTasks.size(); i++) {
			task = mPausedTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mPausedTasks.remove(task);
				break;
			}
		}
	}
	
	/**
	 * addTask到真正开始下载有个等待时间
	 */
	private void waitTask(DownloadTask task) {
		Intent nofityIntent = new Intent(ShareDefine.getKRECEIVER_ACTION());
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.WAIT);
		nofityIntent.putExtra(MyIntents.URL, task.getUrl());
		mContext.sendBroadcast(nofityIntent);
	}

	private void completeTask(DownloadTask task, int type) {

		if (mDownloadingTasks.contains(task)) {
			mDownloadingTasks.remove(task);
			Intent nofityIntent = new Intent(ShareDefine.getKRECEIVER_ACTION());
			nofityIntent.putExtra(MyIntents.TYPE, type);
			nofityIntent.putExtra(MyIntents.URL, task.getUrl());
			mContext.sendBroadcast(nofityIntent);
		}
	}
	
	private void errorTask(DownloadTask task, Throwable error) {
		if(mDownloadingTasks.contains(task)) {
			mDownloadingTasks.remove(task);
			Intent errorIntent = new Intent(ShareDefine.getKRECEIVER_ACTION());
			errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
			if (error != null) {
//				errorIntent.putExtra(MyIntents.ERROR_CODE, error);
				errorIntent.putExtra(MyIntents.ERROR_INFO,
						error.getMessage());
			}
			errorIntent.putExtra(MyIntents.URL, task.getUrl());
			mContext.sendBroadcast(errorIntent);
		}
	}

	public void continueTask(String url) {
		for (int i = 0, length = mPausedTasks.size(); i < length; i++) {
			DownloadTask task = mPausedTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				continueTask(task);
				break;
			}

		}
	}

	private void continueTask(DownloadTask task) {
		if (task != null) {
			mPausedTasks.remove(task);
			mTaskQueue.offer(task);
		}
	}

	private int getTotalTaskCount() {
		return mTaskQueue.size() + mDownloadingTasks.size()
				+ mPausedTasks.size();
	}

	class TaskQueue {

		private Queue<DownloadTask> taskQueue;

		public TaskQueue() {

			taskQueue = new LinkedList<DownloadTask>();
		}

		public void offer(DownloadTask task) {

			taskQueue.offer(task);
		}

		public DownloadTask poll() {
			DownloadTask task = null;
			while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
					|| (task = taskQueue.poll()) == null) {
				try {
					Thread.sleep(1000); // sleep
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return task;
		}

		public DownloadTask get(int position) {

			if (position >= size()) {
				return null;
			}
			return ((LinkedList<DownloadTask>) taskQueue).get(position);
		}

		public int size() {

			return taskQueue.size();
		}

		public boolean remove(int position) {

			return taskQueue.remove(get(position));
		}

		public boolean remove(DownloadTask task) {

			return taskQueue.remove(task);
		}
	}

}
