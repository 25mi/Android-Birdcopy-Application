package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_LOCAl_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSONDao;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by songbaoqiang on 6/13/14.
 */

public class FlyingContentDAO {

    private BE_PUB_LESSONDao contentDao;

    public FlyingContentDAO()
    {

        contentDao = FlyingDBManager.getContentDao();

    }

    public BE_PUB_LESSON loadLesson(long id)
    {
        return contentDao.load(id);
    }

    public List<BE_PUB_LESSON> loadAllData()
    {
        return contentDao.loadAll();
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
        return contentDao.queryRaw(where, params);
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
                return contentDao.insertOrReplace(localLessonData);
            }
            else
            {
                long id =lesson.getId();
                localLessonData.setId(id);
                contentDao.update(localLessonData);

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
        contentDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++){
                    BE_PUB_LESSON localLessonData = list.get(i);
                    contentDao.insertOrReplace(localLessonData);
                }
            }
        });

    }

    /**
     * delete all localLessonData
     */
    public void deleteAllData(){
        contentDao.deleteAll();
    }

    /**
     * delete localLessonData by id
     * @param id
     */
    public void deleteLesson(long id)
    {
        contentDao.deleteByKey(id);
    }

    public void deleteLesson(BE_PUB_LESSON note)
    {
        contentDao.delete(note);
    }

    public  BE_PUB_LESSON  selectWithLessonID(String lessonID)
    {


        //BE_PUB_LESSONDao.

        BE_PUB_LESSON localLesson = contentDao.queryBuilder()
                .where(BE_PUB_LESSONDao.Properties.BELESSONID.eq(lessonID))
                .unique();

        return localLesson;
    }

    public  void  deleteWithLessonID(String lessonID)
    {

        QueryBuilder<BE_PUB_LESSON> qb = contentDao.queryBuilder();
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
