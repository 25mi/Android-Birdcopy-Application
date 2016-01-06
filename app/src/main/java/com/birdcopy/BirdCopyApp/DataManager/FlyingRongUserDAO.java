package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_RongUser;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_RongUserDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by birdcopy on 7/10/15.
 */
public class FlyingRongUserDAO {

    private BE_RongUserDao rongUserDao;

    public FlyingRongUserDAO ()
    {
        rongUserDao = FlyingDBManager.getRongUserDao();
    }

    public BE_RongUser loadRongUser(long id)
    {
        return rongUserDao.load(id);
    }

    public List<BE_RongUser> loadAllData()
    {
        return rongUserDao.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_RongUser> querylRongUser(String where, String... params)
    {
        return rongUserDao.queryRaw(where, params);
    }

    /**
     * insert or update rongUser
     * @param rongUser
     * @return insert or update statistic id
     */
    public long saveRongUser(BE_RongUser rongUser)
    {

        if (rongUser!=null)
        {
            BE_RongUser temp = selectWithUserID(rongUser.getUserid());

            if (temp==null)
            {
                return rongUserDao.insertOrReplace(rongUser);
            }
            else
            {
                long id =temp.getId();
                rongUser.setId(id);
                rongUserDao.update(rongUser);
                return id;
            }
        }
        else {

            return 0;
        }
    }


    /**
     * delete all rongUser
     */
    public void deleteAllData()
    {
        rongUserDao.deleteAll();
    }

    /**
     * delete rongUser by id
     * @param id
     */
    public void deleteRongUser(long id)
    {
        rongUserDao.deleteByKey(id);
    }

    public void deleteRongUser(BE_RongUser note)
    {
        rongUserDao.delete(note);
    }

    public  BE_RongUser  selectWithUserID(String UserID)
    {
        //BE_STATISTICDao.

        BE_RongUser localdata = rongUserDao.queryBuilder()
                .where(BE_RongUserDao.Properties.Userid.eq(UserID))
                .unique();

        return localdata;
    }

    public  void  deleteWithUserID(String UserID)
    {

        QueryBuilder<BE_RongUser> qb = rongUserDao.queryBuilder();
        DeleteQuery<BE_RongUser> bd = qb.where(BE_RongUserDao.Properties.Userid.eq(UserID)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
}
