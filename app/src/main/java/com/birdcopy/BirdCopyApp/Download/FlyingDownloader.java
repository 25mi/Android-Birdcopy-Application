package com.birdcopy.BirdCopyApp.Download;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.ShareDefine;

/**
 * Created by songbaoqiang on 6/13/14.
 */
public class FlyingDownloader {

    public    String mLessonID;
    protected BE_PUB_LESSON lessonData;
    protected FlyingContentDAO mDao;

    public String mDownloadType;

    private FlyngDirectDownloader mDirectDownloader;

    public FlyingDownloader(String mLessonID)
    {
        this.mLessonID =mLessonID;
        mDao = new FlyingContentDAO();
        lessonData =mDao.selectWithLessonID(mLessonID);

        mDownloadType = lessonData.getBEDOWNLOADTYPE();

        if (mDownloadType.equals(ShareDefine.KDownloadTypeNormal)){

            mDirectDownloader = new FlyngDirectDownloader(mLessonID);
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

            mDirectDownloader.startDownload();
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

            mDirectDownloader.cancelDownload();
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

            mDirectDownloader.pauseDownload();
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

            mDirectDownloader.continueDownload ();
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
