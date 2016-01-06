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
import com.birdcopy.BirdCopyApp.R;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingDBManager {

    public static void init()
    {
        try{

            copyDicDataBase(ShareDefine.KBaseDatdbaseFilename);
            FlyingDownloadManager.downloadShareDicData();
        }
        catch (Exception e)
        {

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

            try {
                OpenHelper helper = new OpenHelper(MyApplication.getInstance(),ShareDefine.KBaseDatdbaseFilename, null);
                dicDaoMaster = new DicDaoMaster(helper.getWritableDatabase());
            }
            catch (Exception e)
            {

            }
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

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    private static void copyDicDataBase(String dbname) throws IOException {

        // Open your local db as the input stream

        InputStream myInput = MyApplication.getInstance().getResources().openRawResource(R.raw.mydic);
        // Path to the just created empty db
        File outFileName =MyApplication.getInstance().getDatabasePath(dbname);
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName.getPath());
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    //数据库相关操作
    public static void updateBaseDic(String lessonID)
    {
        String fileName = FlyingFileManager.getLessonDicXMLTargetPath(lessonID);

        try
        {
            MyTask dTask = new MyTask();

            String mResponseStr = FlyingFileManager.getStringFromFile(fileName);
            dTask.execute(mResponseStr);
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
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                FlyingItemparser myXMLHandler = new FlyingItemparser();
                myXMLHandler.initIndexTagDic();
                xr.setContentHandler(myXMLHandler);
                xr.parse(new InputSource(new ByteArrayInputStream(params[0].getBytes())));

                mAllRecordCount = myXMLHandler.allRecordCount;
                return myXMLHandler.entries;
            }
            catch (Exception e)
            {
                System.out.println("XML Pasing Excpetion = " + e);
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
