/**
 * Copyright (c) www.bugull.com
 */
package com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils;

public class DownloadConstants {

	/** 还没有下载 初始默认状态 */
	public static final int STATUS_DEFAULT = 0;//这种状态要注意加一个判断：是否已经下载完成并安装完成
	/** 正在下载 */
	public static final int STATUS_DOWNLOADING = 1;

    /** 下载完成 */
    public static final int STATUS_INSTALL = 2;

	/** 暂停下载 */
	public static final int STATUS_PAUSE = 3;
	
	/** 下载完成并安装完成  注：这种状态不在数据库中体现，需要结合数据库和安装情况来决定 */
	public static final int STATUS_COMPLETE = 5;
}
