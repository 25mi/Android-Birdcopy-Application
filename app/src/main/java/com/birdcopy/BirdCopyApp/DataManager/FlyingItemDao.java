package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUB;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUBDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingItemDao {

    private BE_DIC_PUBDao itemDao;

    public FlyingItemDao ()
    {
        itemDao = FlyingDBManager.getItemDao();
    }

    public BE_DIC_PUB loadItem(long id)
    {
        return itemDao.load(id);
    }

    public List<BE_DIC_PUB> loadAllData()
    {
        return itemDao.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_DIC_PUB> querylItem(String where, String... params)
    {
        return itemDao.queryRaw(where, params);
    }

    /**
     * insert or update item
     * @param item
     * @return insert or update item id
     */
    public long saveItemn(BE_DIC_PUB item)
    {

        if (item!=null)
        {
            return itemDao.insertOrReplace(item);
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
        itemDao.deleteAll();
    }

    /**
     * delete rongUser by id
     * @param id
     */
    public void deleteItem(long id)
    {
        itemDao.deleteByKey(id);
    }

    public void deleteItem(BE_DIC_PUB item)
    {
        itemDao.delete(item);
    }

    public  List<BE_DIC_PUB>  selectWith(String word,Integer index)
    {
        //BE_STATISTICDao.

        return itemDao.queryBuilder()
                .where(BE_DIC_PUBDao.Properties.BEWORD.eq(word),BE_DIC_PUBDao.Properties.BEINDEX.eq(index)).list();
    }

    public  List<BE_DIC_PUB>  selectWith(String word)
    {
        //BE_STATISTICDao.

        return  itemDao.queryBuilder()
                .where(BE_DIC_PUBDao.Properties.BEWORD.eq(word)).list();
    }

    public  void  deleteWithUse(String word)
    {

        QueryBuilder<BE_DIC_PUB> qb = itemDao.queryBuilder();
        DeleteQuery<BE_DIC_PUB> bd = qb.where(BE_DIC_PUBDao.Properties.BEWORD.eq(word)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
}
