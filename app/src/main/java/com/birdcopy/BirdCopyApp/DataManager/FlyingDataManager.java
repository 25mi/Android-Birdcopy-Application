package com.birdcopy.BirdCopyApp.DataManager;

import android.content.SharedPreferences;
import android.view.View;

import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.rong.imlib.model.UserInfo;

/**
 * Created by vincentsung on 12/16/15.
 */
public class FlyingDataManager {

    static public String getCurrentPassport()
    {
        String  currentPassport=MyApplication.getSharedPreference().getString("passport", null);

        if(currentPassport==null)
        {
            currentPassport = OpenUDID_manager.getOpenUDID();

            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
            editor.putString("passport", currentPassport);
            editor.commit();
        }

        return currentPassport;
    }

    static public String getCurrentRongID()
    {
        String  currentPassport = FlyingDataManager.getCurrentPassport();

        if(currentPassport!=null && currentPassport.length()>1)
        {
            return ShareDefine.getMD5(currentPassport);
        }
        else
        {
            return null;
        }
    }

    static public String getNickName()
    {
        String nikename = MyApplication.getSharedPreference().getString(ShareDefine.KIMNIKENAME, null);
        if (nikename == null) {
            nikename = "我的昵称";
        }

        return nikename;
    }

    static public void setNickName(String nickName)
    {
        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
        editor.putString(ShareDefine.KIMNIKENAME, nickName);
        editor.commit();
    }

    static  public String getPortraitUri()
    {
        String portraitUri = MyApplication.getSharedPreference().getString(ShareDefine.KIMPORTRAITURI, null);

        if (portraitUri == null)
        {
            String rongID = FlyingDataManager.getCurrentRongID();

            if (rongID!=null)
            {
                UserInfo userInfo= FlyingContext.getInstance().getUserInfoByRongId(rongID);

                if(userInfo!=null && userInfo.getPortraitUri()!=null)
                {
                    portraitUri=userInfo.getPortraitUri().toString();
                    setPortraitUri(portraitUri);
                }
            }
        }

        return portraitUri;
    }

    static  public void setPortraitUri(String portraitUri)
    {
        SharedPreferences.Editor edit=MyApplication.getSharedPreference().edit();
        edit.putString(ShareDefine.KIMPORTRAITURI,portraitUri);
        edit.commit();
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

        FlyingHttpTool.updateMembership(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                newStartDate,
                newEnddate, null);
    }

    //向服务器获获取备份数据和最新充值数据，在本地激活用户ID
    static  public void creatLocalUSerProfileWithServer()
    {
        //从服务器获取会员资格
        FlyingHttpTool.getMembership(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //苹果渠道购买、金币消费、点击单词统计
        FlyingHttpTool.getMoneyData(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                null);
        //充值卡记录
        FlyingHttpTool.getQRData(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //内容相关数据
        FlyingHttpTool.getContentStatistic(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                null);

        //获取头像、昵称
        FlyingHttpTool.getUserInfoByopenID(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                null);
    }
}
