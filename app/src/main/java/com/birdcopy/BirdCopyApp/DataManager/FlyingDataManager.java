package com.birdcopy.BirdCopyApp.DataManager;

import android.content.SharedPreferences;

import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vincentsung on 12/16/15.
 */
public class FlyingDataManager {

    static public String getPassport()
    {
        String  currentPassport=MyApplication.getSharedPreference().getString("passport",null);

        if(currentPassport==null)
        {
            currentPassport = OpenUDID_manager.getOpenUDID();

            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
            editor.putString("passport", currentPassport);
            editor.commit();
        }

        return currentPassport;
    }

    static  public  void  addMembership()
    {
        Date newStartDate = new Date();

        String endDateStr = MyApplication.getSharedPreference().getString(FlyingHttpTool.MEMBERSHIP_ENDTIME, null);

        if(endDateStr!=null)
        {
            try {
                newStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDateStr);
            }
            catch (Exception exception)
            {
                //
            }
        }

        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(newStartDate);   //设置时间为当前时间
        ca.add(Calendar.YEAR, +1); //年份加1

        Date newEnddate = ca.getTime();

        FlyingHttpTool.updateMembership(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                newStartDate,
                newEnddate, null);
    }

    //向服务器获获取备份数据和最新充值数据，在本地激活用户ID
    static  public void creatLocalUSerProfileWithServer()
    {
        //从服务器获取会员资格
        FlyingHttpTool.getMembership(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //苹果渠道购买、金币消费、点击单词统计
        FlyingHttpTool.getMoneyData(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                null);
        //充值卡记录
        FlyingHttpTool.getQRData(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //内容相关数据
        FlyingHttpTool.getContentStatistic(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //获取头像、昵称

    }
}
