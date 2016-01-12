package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.DicSQLiteOpenHelper;

import java.util.List;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingItemDAO {

	private DicSQLiteOpenHelper dicSQLiteOpenHelper;

	public FlyingItemDAO() {

		dicSQLiteOpenHelper = FlyingDBManager.getDicSQLiteOpenHelper();
	}

	public void saveItem(FlyingItemData itemData) {

		if(dicSQLiteOpenHelper==null)
		{
			dicSQLiteOpenHelper = FlyingDBManager.getDicSQLiteOpenHelper();
		}

		List<FlyingItemData>  list = dicSQLiteOpenHelper.get_items(itemData.getBEWORD(), itemData.getBEINDEX());

		if(list==null || list.size()==0)
		{
			dicSQLiteOpenHelper.add_DicItem(itemData);
		}
	}

	public List<FlyingItemData> getItems(String word) {

		if(dicSQLiteOpenHelper==null)
		{
			dicSQLiteOpenHelper = FlyingDBManager.getDicSQLiteOpenHelper();
		}

		return dicSQLiteOpenHelper.get_items(word);
	}
}
