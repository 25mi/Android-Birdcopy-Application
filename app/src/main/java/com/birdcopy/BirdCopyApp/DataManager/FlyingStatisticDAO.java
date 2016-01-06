package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.*;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by BirdCopyApp on 29/7/14.
 */

public class FlyingStatisticDAO {

    private BE_STATISTICDao statisticDAO;

    public FlyingStatisticDAO ()
    {
        statisticDAO = FlyingDBManager.getStatisticDao();
    }

    public BE_STATISTIC loadStatic(long id)
    {
        return statisticDAO.load(id);
    }

    public List<BE_STATISTIC> loadAllData()
    {
        return statisticDAO.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_STATISTIC> querylStatistic(String where, String... params)
    {
        return statisticDAO.queryRaw(where, params);
    }

    /**
     * insert or update statistic
     * @param statistic
     * @return insert or update statistic id
     */
    public long saveStatic(BE_STATISTIC statistic)
    {

        if (statistic!=null)
        {

            BE_STATISTIC temp = selectWithUserID(statistic.getBEUSERID());

            if (temp==null)
            {
                return statisticDAO.insertOrReplace(statistic);
            }
            else
            {
                long id =temp.getId();
                statistic.setId(id);
                statisticDAO.update(statistic);
                return id;
            }
        }
        else {

            return 0;
        }
    }


    /**
     * delete all statistic
     */
    public void deleteAllData()
    {
        statisticDAO.deleteAll();
    }

    /**
     * delete statistic by id
     * @param id
     */
    public void deleteStatistic(long id)
    {
        statisticDAO.deleteByKey(id);
    }

    public void deleteStatistic(BE_STATISTIC note)
    {
        statisticDAO.delete(note);
    }

    public  BE_STATISTIC  selectWithUserID(String UserID)
    {
        //BE_STATISTICDao.

        BE_STATISTIC localdata = statisticDAO.queryBuilder()
                .where(BE_STATISTICDao.Properties.BEUSERID.eq(UserID))
                .unique();

        return localdata;
    }

    public  void  deleteWithUserID(String UserID)
    {

        QueryBuilder<BE_STATISTIC> qb = statisticDAO.queryBuilder();
        DeleteQuery<BE_STATISTIC> bd = qb.where(BE_LOCAl_LESSONDao.Properties.BEUSERID.eq(UserID)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

}
