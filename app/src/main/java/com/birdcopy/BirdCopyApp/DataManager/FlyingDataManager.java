package com.birdcopy.BirdCopyApp.DataManager;

import android.content.SharedPreferences;

import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.rong.imlib.model.UserInfo;

/**
 * Created by vincentsung on 12/16/15.
 */
public class FlyingDataManager {

    public static void init()
    {
        synchronized (MyApplication.getInstance()) {

            setOPENUDID();
        }
    }

    public static void setOPENUDID()
    {
        OpenUDID_manager.sync(MyApplication.getInstance().getApplicationContext());
    }

    public static String getServerNetAddress() {
        return MyApplication.getInstance().getResources().getString(R.string.KServerNetAddress);
    }

    public static String getLessonOwner() {
        return MyApplication.getInstance().getResources().getString(R.string.KlessonQwner);
    }

    public static String getBirdcopyAppID() {
        return MyApplication.getInstance().getResources().getString(R.string.KBirdCopyAppID);
    }

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

    static  public void setRongToken(String rongToken)
    {
        SharedPreferences.Editor edit=MyApplication.getSharedPreference().edit();
        edit.putString(ShareDefine.RONG_TOKEN,rongToken);
        edit.commit();
    }

    static  public String getRongToken()
    {
        return MyApplication.getSharedPreference().getString(ShareDefine.KIMPORTRAITURI, ShareDefine.RONG_DEFAULT_TOKEN);
    }

    static public String getNickName()
    {
        return MyApplication.getSharedPreference().getString(ShareDefine.KIMNIKENAME, ShareDefine.KNIKENAMEDEFAULT);
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
                UserInfo userInfo= FlyingIMContext.getInstance().getUserInfoByRongId(rongID);

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
                FlyingDataManager.getBirdcopyAppID(),
                newStartDate,
                newEnddate, null);
    }

    //向服务器获获取备份数据和最新充值数据，在本地激活用户ID
    static  public void creatLocalUserProfileWithServer()
    {
        //获取头像、昵称
        FlyingHttpTool.getUserInfoByopenID(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                new FlyingHttpTool.GetUserInfoByopenIDListener() {
                    @Override
                    public void completion(UserInfo userInfo) {

                        FlyingDataManager.setNickName(userInfo.getName());
                        FlyingDataManager.setPortraitUri(userInfo.getPortraitUri().toString());
                    }
                });

        //从服务器获取会员资格
        FlyingHttpTool.getMembership(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                null);

        //苹果渠道购买、金币消费、点击单词统计
        FlyingHttpTool.getMoneyData(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                null);
        //充值卡记录
        FlyingHttpTool.getQRData(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                null);

        //内容相关数据
        FlyingHttpTool.getContentStatistic(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                null);
    }
}
