package com.birdcopy.BirdCopyApp.Component.Download;

import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.FlyingNormalDownLoader;

/**
 * Created by songbaoqiang on 6/13/14.
 */
public class FlyingDownloader {

    public    String mLessonID;
    protected BE_PUB_LESSON lessonData;
    protected FlyingLessonDAO mDao;

    public String mDownloadType;

    private FlyingNormalDownLoader mNormalDownloader;

    public FlyingDownloader(String mLessonID)
    {
        this.mLessonID =mLessonID;
        mDao = new FlyingLessonDAO();
        lessonData =mDao.selectWithLessonID(mLessonID);

        mDownloadType = lessonData.getBEDOWNLOADTYPE();

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mNormalDownloader = new FlyingNormalDownLoader(mLessonID);
        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeMagnet))
        {

        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeM3U8))
        {

        }
    }

    public void startDownload()
    {

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mNormalDownloader.startDownload();
        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeMagnet))
        {

        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeM3U8))
        {

        }
    }

    public void cancelDownload()
    {

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mNormalDownloader.cancelDownload();
        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeMagnet))
        {

        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeM3U8))
        {

        }
    }

    public void pauseDownload()
    {

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mNormalDownloader.pauseDownload();
        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeMagnet))
        {

        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeM3U8))
        {

        }
    }

    public void continueDownload()
    {

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mNormalDownloader.continueDownload ();
        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeMagnet))
        {

        }
        else if (mDownloadType.equals(ShareDefine.KDownloadTypeM3U8))
        {

        }
    }

    public String getDownloadType()
    {
        return mDownloadType;
    }

}
