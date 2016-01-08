package com.birdcopy.BirdCopyApp.DataManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.birdcopy.BirdCopyApp.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Content.FlyingItemparser;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUB;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUBDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_LOCAl_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_RongUserDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_STATISTICDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_TOUCH_RECORDDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DaoMaster;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DaoSession;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DicDaoMaster;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DicDaoSession;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingDBManager {

    public static void init()
    {
        try{

            FlyingDownloadManager.downloadShareDicData();
        }
        catch (Exception e)
        {
            System.out.println("FlyingDBManager init" + e.getMessage());
        }
    }

    //数据库操作
    public static BE_DIC_PUBDao getItemDao() {

        return FlyingDBManager.getDicDaoSession().getBE_DIC_PUBDao();
    }

    public static BE_PUB_LESSONDao getContentDao() {

        return  FlyingDBManager.getDaoSession().getBE_PUB_LESSONDao();
    }

    public static BE_LOCAl_LESSONDao getLocalContentDao() {
        return FlyingDBManager.getDaoSession().getBE_LOCAl_LESSONDao();
    }

    public static BE_STATISTICDao getStatisticDao() {
        return FlyingDBManager.getDaoSession().getBE_STATISTICDao();
    }

    public static BE_TOUCH_RECORDDao getTouchDao() {

        return FlyingDBManager.getDaoSession().getBE_TOUCH_RECORDDao();
    }

    public static BE_RongUserDao getRongUserDao() {

        return FlyingDBManager.getDaoSession().getBE_RongUserDao();
    }

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    private static DicDaoMaster dicDaoMaster;
    private static DicDaoSession dicDaoSession;

    /**
     * 取得userDaoMaster
     *
     * @return
     */
    private static DaoMaster getDaoMaster() {

        if (daoMaster == null) {
            OpenHelper helper = new OpenHelper(MyApplication.getInstance(), ShareDefine.KUserDatdbaseFilename, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得userDaoSession
     *
     * @return
     */
    private static DaoSession getDaoSession() {

        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 取得dicDaoMaster
     *
     * @return
     */
    private static DicDaoMaster getDicDaoMaster() {

        if (dicDaoMaster == null) {

            String dicDBpath = FlyingFileManager.getDBDatabasePath();

            if (!new File(dicDBpath).exists())
            {
                try{

                    FlyingDownloadManager.downloadShareDicData();
                }
                catch (Exception e)
                {}
            }

            SQLiteDatabase db =SQLiteDatabase.openOrCreateDatabase(dicDBpath,null);
            dicDaoMaster = new DicDaoMaster(db);
        }

        return dicDaoMaster;
    }

    /**
     * 取得dicDaoSession
     *
     * @return
     */
    private static DicDaoSession getDicDaoSession() {

        if (dicDaoSession == null) {
            if (dicDaoMaster == null) {
                dicDaoMaster = getDicDaoMaster();
            }
            dicDaoSession = dicDaoMaster.newSession();
        }
        return dicDaoSession;
    }

    public static class OpenHelper extends DaoMaster.OpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            onCreate(db);
        }
    }


    //数据库相关操作
    public static void updateBaseDic(String lessonID)
    {
        String fileName = FlyingFileManager.getLessonDicXMLTargetPath(lessonID);

        try
        {
            if(fileName!=null && fileName.length()!=0)
            {
                String mResponseStr = FlyingFileManager.getStringFromFile(fileName);

                if(mResponseStr!=null && mResponseStr.length()!=0)
                {
                    MyTask dTask = new MyTask();
                    dTask.execute(mResponseStr);
                }
            }
        }
        catch (Exception e)
        {}
    }


    static int mAllRecordCount=0;

    static private class MyTask extends AsyncTask<String, Void, ArrayList<BE_DIC_PUB>>
    {
        @Override
        protected ArrayList<BE_DIC_PUB> doInBackground(String... params)
        {
            try
            {
                FlyingItemparser.parser(params[0]);

                mAllRecordCount = FlyingItemparser.allRecordCount;
                return FlyingItemparser.resultList;
            }
            catch (Exception e)
            {
                System.out.println("XML Pasing Excpetion = " + e.getMessage());
                return  null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<BE_DIC_PUB> result)
        {
            super.onPostExecute(result);

            for(BE_DIC_PUB item:result){

                new FlyingItemDao().saveItemn(item);
            }
        }
    }
}
