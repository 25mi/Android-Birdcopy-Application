package com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO;

import android.content.Context;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_LOCAl_LESSONDao;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSONDao;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by songbaoqiang on 6/13/14.
 */

public class FlyingLessonDAO {

    private BE_PUB_LESSONDao lessonDao;

    public FlyingLessonDAO ()
    {

        lessonDao = MyApplication.getDaoSession(MyApplication.getInstance()).getBE_PUB_LESSONDao();

    }

    public  FlyingLessonDAO(Context context)
    {

        lessonDao = MyApplication.getDaoSession(context).getBE_PUB_LESSONDao();
    }


    public BE_PUB_LESSON loadLesson(long id)
    {
        return lessonDao.load(id);
    }

    public List<BE_PUB_LESSON> loadAllData()
    {
        return lessonDao.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_PUB_LESSON> querylLesson(String where, String... params)
    {
        return lessonDao.queryRaw(where, params);
    }

    /**
     * insert or update localLessonData
     * @param localLessonData
     * @return insert or update localLessonData id
     */
    public long savelLesson(BE_PUB_LESSON localLessonData)
    {

        if (localLessonData!=null)
        {

            BE_PUB_LESSON lesson = selectWithLessonID(localLessonData.getBELESSONID());

            if (lesson==null)
            {
                return lessonDao.insertOrReplace(localLessonData);
            }
            else
            {
                long id =lesson.getId();
                localLessonData.setId(id);
                lessonDao.update(localLessonData);

                return id;
            }
        }
        else {

            return 0;
        }
    }

    /**
     * insert or update localLessonDataList use transaction
     * @param list
     */
    public void saveLessonLists(final List<BE_PUB_LESSON> list)
    {
        if(list == null || list.isEmpty()){
            return;
        }
        lessonDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++){
                    BE_PUB_LESSON localLessonData = list.get(i);
                    lessonDao.insertOrReplace(localLessonData);
                }
            }
        });

    }

    /**
     * delete all localLessonData
     */
    public void deleteAllData(){
        lessonDao.deleteAll();
    }

    /**
     * delete localLessonData by id
     * @param id
     */
    public void deleteLesson(long id)
    {
        lessonDao.deleteByKey(id);
    }

    public void deleteLesson(BE_PUB_LESSON note)
    {
        lessonDao.delete(note);
    }

    public  BE_PUB_LESSON  selectWithLessonID(String lessonID)
    {


        //BE_PUB_LESSONDao.

        BE_PUB_LESSON localLesson = lessonDao.queryBuilder()
                .where(BE_PUB_LESSONDao.Properties.BELESSONID.eq(lessonID))
                .unique();

        return localLesson;
    }

    public  void  deleteWithLessonID(String lessonID)
    {

        QueryBuilder<BE_PUB_LESSON> qb = lessonDao.queryBuilder();
        DeleteQuery<BE_PUB_LESSON> bd = qb.where(BE_LOCAl_LESSONDao.Properties.BELESSONID.eq(lessonID)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    public void updateDowloadPercent(String lessonID,double downloadPercent )
    {

        if (lessonID!=null)
        {
            BE_PUB_LESSON lessondata =selectWithLessonID(lessonID);
            lessondata.setBEDLPERCENT(downloadPercent);

            savelLesson(lessondata);
         }
    }

    public void updateLocalContentURL(String lessonID,String localURLOfContent)
    {


        if (lessonID!=null)
        {
            BE_PUB_LESSON lessondata =selectWithLessonID(lessonID);
            lessondata.setLocalURLOfContent(localURLOfContent);

            savelLesson(lessondata);
        }
    }

    public void updateDownloadState(String lessonID,boolean downloadState)
    {


        if (lessonID!=null)
        {
            BE_PUB_LESSON lessondata =selectWithLessonID(lessonID);
            lessondata.setBEDLSTATE(downloadState);

            savelLesson(lessondata);
        }
    }
}
