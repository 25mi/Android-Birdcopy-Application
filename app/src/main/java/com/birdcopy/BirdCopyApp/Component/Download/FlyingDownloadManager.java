package com.birdcopy.BirdCopyApp.Component.Download;

import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;

import java.util.*;

/**
 * Created by songbaoqiang on 6/13/14.
 */
public class FlyingDownloadManager {


    private static int KMaxDownloadLessonThread=10;

    private HashMap<String,FlyingDownloader> mDownloadingOperationList = new HashMap<String, FlyingDownloader>(KMaxDownloadLessonThread);
    private Set  mWaittingDownloadJobs = new HashSet();

    public void startDownloaderForID(String lessonID)
    {
        //如果正在下载任务队列中没有这个课程的任务，加入等待队列
        FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);;
        if (downloader!=null)
        {
            downloader.startDownload();
        }
        else
        {
            mWaittingDownloadJobs.add(lessonID);
        }

        //正在下载任务队列没有可执行任务
        if (mDownloadingOperationList.size()<KMaxDownloadLessonThread) {

             pushwaittingJobToWorkList();
        }
    }

    private void pushwaittingJobToWorkList()
    {
        if(mWaittingDownloadJobs.size()!=0){

            String lessonID = (String)mWaittingDownloadJobs.toArray()[0];

            FlyingLessonDAO dao = new FlyingLessonDAO() ;

            BE_PUB_LESSON lessonData = dao.selectWithLessonID(lessonID);

            double percent=lessonData.getBEDLPERCENT();

            if (percent>0) {

                //移出队列
                mDownloadingOperationList.remove(lessonID);
                mWaittingDownloadJobs.remove(lessonID);

                if(mWaittingDownloadJobs.size()!=0)
                {
                    pushwaittingJobToWorkList();
                }
            }
            else{

                //移出等待队列
                mWaittingDownloadJobs.remove(lessonID);

                FlyingDownloader downloader = new FlyingDownloader(lessonID);
                //加入执行队列，移出等待队列
                mDownloadingOperationList.put(lessonID,downloader);
                downloader.startDownload();
            }
        }
    }


    public void closeAndReleaseDownloaderForID(String lessonID)
    {

        if (mWaittingDownloadJobs!=null) {

            mWaittingDownloadJobs.remove(lessonID);
        }

        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.cancelDownload();;
                mDownloadingOperationList.remove(downloader);
            }
        }

        triggerNextTask();
    }

    public void pauseDownloder(String lessonID)
    {

        if(mWaittingDownloadJobs.contains(lessonID))
        {

            mWaittingDownloadJobs.remove(lessonID);
        }

        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.pauseDownload();;
            }
        }
    }

    public void continueDownload(String lessonID)
    {

        if(!mWaittingDownloadJobs.contains(lessonID))
        {

            mWaittingDownloadJobs.add(lessonID);
        }

        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.continueDownload();;
            }
        }
    }


    public void triggerNextTask()
    {

        //如果下载任务没有达到最大值，添加新任务，
        if (mDownloadingOperationList.size()<=KMaxDownloadLessonThread) {

            pushwaittingJobToWorkList();
        }
    }

    public  int getCurrentTask()
    {
        int result =0;

        if(mDownloadingOperationList!=null)
        {
            result = mDownloadingOperationList.size();
        }

        return result;
    }
}
