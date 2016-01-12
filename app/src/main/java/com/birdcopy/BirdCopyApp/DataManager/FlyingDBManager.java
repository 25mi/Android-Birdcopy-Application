package com.birdcopy.BirdCopyApp.DataManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DicSQLiteOpenHelper;
import com.birdcopy.BirdCopyApp.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Content.FlyingItemparser;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_LOCAl_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_RongUserDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_STATISTICDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_TOUCH_RECORDDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DaoMaster;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DaoSession;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingDBManager {

	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	private static DicSQLiteOpenHelper dicSQLiteOpenHelper;

    public static void initDB()
    {
	    synchronized (MyApplication.getInstance()) {

		    initDicDB();
	    }
    }

    //数据库操作
    public static BE_PUB_LESSONDao getContentDao() {

        return  getDaoSession().getBE_PUB_LESSONDao();
    }

    public static BE_LOCAl_LESSONDao getLocalContentDao() {
        return  getDaoSession().getBE_LOCAl_LESSONDao();
    }

    public static BE_STATISTICDao getStatisticDao() {
        return  getDaoSession().getBE_STATISTICDao();
    }

    public static BE_TOUCH_RECORDDao getTouchDao() {

        return getDaoSession().getBE_TOUCH_RECORDDao();
    }

    public static BE_RongUserDao getRongUserDao() {

        return getDaoSession().getBE_RongUserDao();
    }

	public static DicSQLiteOpenHelper getDicSQLiteOpenHelper() {

		if(dicSQLiteOpenHelper==null)
		{
			synchronized (MyApplication.getInstance()) {

				initDicDB();
			}
		}

		return dicSQLiteOpenHelper;
	}

	private static void initDicDB()
	{
		if(dicSQLiteOpenHelper==null)
		{
			try{

				final String dicDBpath = FlyingFileManager.getMyDicDBFilePath();

				if (!FlyingFileManager.fileExists(dicDBpath))
				{
					try{

						FlyingDownloadManager.downloadShareDicZip(new FlyingDownloadManager.DownloadShareDicDataListener() {
							@Override
							public void completion(boolean isOK) {

                                if (isOK)
                                {
                                    dicSQLiteOpenHelper = DicSQLiteOpenHelper.getInstance(MyApplication.getInstance().getApplicationContext());
                                }
							}
						});
					}
					catch (Exception e)
					{}
				}
				else
				{
					if(dicSQLiteOpenHelper==null)
					{
						dicSQLiteOpenHelper = DicSQLiteOpenHelper.getInstance(MyApplication.getInstance().getApplicationContext());
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("FlyingDBManager init" + e.getMessage());
			}
		}
	}

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

    private static class OpenHelper extends DaoMaster.OpenHelper {

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
        String fileName = FlyingFileManager.getLessonDicXMLFilePath(lessonID);

        try
        {
            if(fileName!=null && fileName.length()!=0)
            {
                String mResponseStr = FlyingFileManager.getStringFromFile(fileName);

                if(mResponseStr!=null && mResponseStr.length()!=0)
                {
                    FlyingItemparser.parser(mResponseStr);

                    for(FlyingItemData item:FlyingItemparser.resultList){

                        new FlyingItemDAO().saveItem(item);
                    }
                }
            }
        }
        catch (Exception e)
        {}
    }
}
