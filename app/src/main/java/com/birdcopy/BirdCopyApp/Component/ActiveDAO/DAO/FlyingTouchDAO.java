package com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO;

import android.content.Context;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.*;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by BirdCopyApp on 29/7/14.
 */

public class FlyingTouchDAO {

    private BE_TOUCH_RECORDDao touchDAO;

    public FlyingTouchDAO()
    {
        touchDAO = MyApplication.getDaoSession(MyApplication.getInstance()).getBE_TOUCH_RECORDDao();
    }

    public FlyingTouchDAO(Context context)
    {
        touchDAO = MyApplication.getDaoSession(context).getBE_TOUCH_RECORDDao();
    }

    public BE_TOUCH_RECORD loadTouchData(long id)
    {
        return touchDAO.load(id);
    }

    public List<BE_TOUCH_RECORD> loadAllData()
    {
        return touchDAO.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_TOUCH_RECORD> querylStatistic(String where, String... params)
    {
        return touchDAO.queryRaw(where, params);
    }

    /**
     * insert or update touchData
     * @param touchData
     * @return insert or update touchData id
     */
    public long savelTouch(BE_TOUCH_RECORD touchData)
    {

        if (touchData!=null)
        {

            BE_TOUCH_RECORD temp = selectWithUserID(touchData.getBEUSERID(),touchData.getBELESSONID());

            if (temp==null)
            {
                return touchDAO.insertOrReplace(touchData);
            }
            else
            {
                long id =temp.getId();
                touchData.setId(id);
                touchDAO.update(touchData);
                return id;
            }
        }
        else {

            return 0;
        }
    }


    /**
     * delete all touchData
     */
    public void deleteAllData(){
        touchDAO.deleteAll();
    }

    /**
     * delete touchData by id
     * @param id
     */
    public void deleteStatistic(long id)
    {
        touchDAO.deleteByKey(id);
    }

    public void deleteStatistic(BE_TOUCH_RECORD note)
    {
        touchDAO.delete(note);
    }

    public  List<BE_TOUCH_RECORD>  selectWithUserID(String UserID)
    {

        List<BE_TOUCH_RECORD> list = touchDAO.queryBuilder()
                .where(BE_TOUCH_RECORDDao.Properties.BEUSERID.eq(UserID))
                .list();

        return list;
    }

    public  BE_TOUCH_RECORD  selectWithUserID(String UserID,String lessonID)
    {

        //BE_TOUCH_RECORDDao.

        BE_TOUCH_RECORD localLesson = touchDAO.queryBuilder()
                .where(BE_TOUCH_RECORDDao.Properties.BEUSERID.eq(UserID),
                        BE_TOUCH_RECORDDao.Properties.BELESSONID.eq(lessonID))
                .unique();

        return localLesson;
    }

    public  void  deleteWithUserID(String UserID,String lessonID)
    {
        QueryBuilder<BE_TOUCH_RECORD> qb = touchDAO.queryBuilder();
        DeleteQuery<BE_TOUCH_RECORD> bd = qb
                .where(BE_LOCAl_LESSONDao.Properties.BEUSERID.eq(UserID),
                BE_LOCAl_LESSONDao.Properties.BELESSONID.eq(lessonID))
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

}
