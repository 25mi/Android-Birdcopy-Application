package com.birdcopy.BirdCopyApp.Download;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDBManager;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.util.*;

/**
 * Created by songbaoqiang on 6/13/14.
 */
public class FlyingDownloadManager {

    private static FlyingDownloadManager mInstance;

    public static FlyingDownloadManager getInstance() {

        if (mInstance == null) {
            mInstance = new FlyingDownloadManager();
        }

        return mInstance;
    }

    private HashMap<String,FlyingDownloader> mDownloadingOperationList = new HashMap<String, FlyingDownloader>();

    public void startDownloaderForID(String lessonID)
    {

        BE_PUB_LESSON lesson =new FlyingContentDAO().selectWithLessonID(lessonID);

        if (lesson.getBEDLPERCENT()==1){

            mDownloadingOperationList.remove(lessonID);

            return;
        }

        FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);;
        if (downloader!=null)
        {
            //如果正在下载任务队列中有这个课程的任务，继续下载
            downloader.continueDownload();
        }
        else
        {
            downloader = new FlyingDownloader(lessonID);
            //加入执行队列
            mDownloadingOperationList.put(lessonID,downloader);
            downloader.startDownload();

            downloadRelated(lesson);
        }
    }

    public void closeAndReleaseDownloaderForID(String lessonID)
    {

        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.cancelDownload();;
                mDownloadingOperationList.remove(downloader);
            }
        }
    }

    public void pauseDownloder(String lessonID)
    {
        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.pauseDownload();;
            }
        }
    }

    public void continueDownload(String lessonID)
    {

        if (mDownloadingOperationList.size()!=0) {

            FlyingDownloader downloader = mDownloadingOperationList.get(lessonID);

            if (downloader!=null) {

                downloader.continueDownload();
            }
        }
    }

    public  int getTaskCount()
    {
        int result =0;

        if(mDownloadingOperationList!=null)
        {
            result = mDownloadingOperationList.size();
        }

        return result;
    }

    public static void downloadRelated(BE_PUB_LESSON lessonData) {

        //缓存字幕
        FlyingDownloadManager.getSrtForLesson(lessonData, null);

        //缓存课程字典
        //FlyingDownloadManager.getDicForLesson(lessonData, null);

        //缓存背景音乐
        FlyingDownloadManager.getBackMp3ForLesson(lessonData, null);

        //缓存课程辅助资源
        //FlyingDownloadManager.getRelatedForLesson(lessonData, null);
    }

    public static void getSrtForLesson(BE_PUB_LESSON lessonData,String title) {

        String lessonID = lessonData.getBELESSONID();

        String targetPath =  FlyingFileManager.getLessonSubTargetPath(lessonID);

        FlyingHttpTool.downloadFile(lessonData.getBESUBURL(), targetPath, null);
    }

    public static void getDicForLesson(final BE_PUB_LESSON lessonData,String title) {

        final String lessonID = lessonData.getBELESSONID();
        final String targetPath =  FlyingFileManager.getLessonDicPatchTargetPath(lessonID);

        FlyingHttpTool.downloadFile(lessonData.getBEPROURL(), targetPath, new FlyingHttpTool.DownloadFileListener() {
            @Override
            public void completion(boolean isOK) {
                //
                if(isOK)
                {
                    String outputDir = FlyingFileManager.getLessonDownloadPath(lessonData.getBELESSONID());

                    try {
                        FlyingFileManager.unzip(targetPath, outputDir, true);

                        //升级课程补丁
                        FlyingDBManager.updateBaseDic(lessonID);

                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    public static void getRelatedForLesson(final BE_PUB_LESSON lessonData,String title) {

        String lessonID = lessonData.getBELESSONID();
        final String targetPath =  FlyingFileManager.getLessonRelatedTargetPath(lessonID);

        FlyingHttpTool.downloadFile(lessonData.getBERELATIVEURL(), targetPath, new FlyingHttpTool.DownloadFileListener() {
            @Override
            public void completion(boolean isOK) {
                //
                if (isOK)
                {
                    String outputDir = FlyingFileManager.getLessonDownloadPath(lessonData.getBELESSONID());

                    try {
                        FlyingFileManager.unzip(targetPath, outputDir, true);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        });
    }

    private static void getBackMp3ForLesson(final BE_PUB_LESSON lessonData,String title)
    {
        if (lessonData.getBECONTENTTYPE().equalsIgnoreCase(ShareDefine.KContentTypeText) &&
        lessonData.getBEOFFICIAL()==true)
        {
            FlyingHttpTool.getContentResource(lessonData.getBELESSONID(), ShareDefine.kResource_Background, new FlyingHttpTool.GetContentResourceListener() {
                @Override
                public void completion(String resultURL) {

                    if(resultURL!=null)
                    {
                        final String targetPath = FlyingFileManager.getLessonBackgroundTargetPath(lessonData.getBELESSONID());

                        FlyingHttpTool.downloadFile(resultURL, targetPath, new FlyingHttpTool.DownloadFileListener() {
                            @Override
                            public void completion(boolean isOK) {
                                //
                            }
                        });
                    }
                }
            });
        }
    }

    public static void  downloadShareDicData() {

        FlyingHttpTool.getShareBaseZIPURL(ShareDefine.KBaseDicAllType, new FlyingHttpTool.GetShareBaseZIPURLListener() {
            @Override
            public void completion(String resultURL) {

                final String targetPath = FlyingFileManager.getUserShareTargetPath();

                FlyingHttpTool.downloadFile(resultURL, targetPath, new FlyingHttpTool.DownloadFileListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if(isOK)
                        {
                            String outputDir = FlyingFileManager.getUserSharePath();

                            try {
                                FlyingFileManager.unzip(targetPath, outputDir, true);

                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                });
            }
        });
    }
}
