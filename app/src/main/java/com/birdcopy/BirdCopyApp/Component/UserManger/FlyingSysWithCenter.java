package com.birdcopy.BirdCopyApp.Component.UserManger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_TOUCH_RECORD;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingTouchDAO;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Tools.DateTools;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.pingplusplus.libone.PayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by BirdCopyApp on 29/7/14.
 */
public class FlyingSysWithCenter
{
    static public void sysAllDataWithCenter()
    {
        sysQRMoneyWithCenter();
        uploadUserStatData();
        uploadContentStatData();

        //同步年费会员信息
        sysMembershipWithCenter();
    }

    //用终端登录官网后台
    static public void loginWithQR(String loginID)
    {
        final String  currentPassport=SSKeychain.getPassport();

        String url = ShareDefine.getLoginURL(loginID,currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            if(resultStr.matches("[0-9]+"))
                            {
                                // 登录成功
                                if(Integer.parseInt(resultStr)==1)
                                {
                                    Toast.makeText(MyApplication.getInstance().getApplicationContext(),"扫描登录成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    //向服务器帐户进行充值
    static public void chargingCrad(String cardID)
    {
        final String  currentPassport=SSKeychain.getPassport();
        String url = ShareDefine.getChargingCardSysURL(cardID, currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            int resultCode = Integer.parseInt(resultStr);

                            String responseStr="充值失败，请重试！";
                            int chargCode=0;

                            FlyingStatisticDAO statisticDAO =new FlyingStatisticDAO();

                            switch (resultCode) {
                                case -1:
                                    responseStr = "必须参数缺少";
                                    break;
                                case -11:
                                    responseStr = "充值卡无效";
                                    break;
                                case -12:
                                    responseStr = "充值卡无效";
                                    break;
                                case -13:
                                    responseStr = "充值卡无效";
                                    break;
                                case -21:
                                    responseStr = "充值卡无效";
                                    break;
                                case -22:
                                    responseStr = "充值卡未出售";
                                    break;
                                case -23:
                                    responseStr = "充值卡被锁定";
                                    break;
                                case -24:
                                    responseStr = "充值卡失效";
                                    break;
                                case -31:
                                    responseStr = "充值卡已充值";
                                    break;
                                case -32:
                                    responseStr = "充值卡已充值";
                                    break;
                                case -99:
                                    responseStr = "中途出错(系统原因)";
                                    break;
                                default:

                                    BE_STATISTIC userData = statisticDAO.selectWithUserID(currentPassport);
                                    chargCode =resultCode-userData.getBEQRCOUNT();

                                    userData.setBEQRCOUNT(resultCode);
                                    statisticDAO.saveStatic(userData);
                            }

                            String showResult;

                            if(chargCode>0)
                            {
                                showResult="充值成功:充值金币数目:"+chargCode;
                                ShareDefine.broadUserDataChange();
                            }
                            else
                            {
                                showResult="充值失败，请重试！"+" 原因："+responseStr;
                            }

                            Toast.makeText(MyApplication.getInstance().getApplicationContext(),showResult, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //获取充值卡数据
    static public void sysQRMoneyWithCenter()
    {
        final String  currentPassport=SSKeychain.getPassport();
        String url = ShareDefine.getQRCountForUserIDURL(currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            int resultCode = Integer.parseInt(resultStr);

                            if(resultCode>0)
                            {
                                FlyingStatisticDAO statisticDAO =new FlyingStatisticDAO();
                                BE_STATISTIC userData = statisticDAO.selectWithUserID(currentPassport);
                                userData.setBEQRCOUNT(resultCode);
                                statisticDAO.saveStatic(userData);

                                ShareDefine.broadUserDataChange();
                            }
                        }
                    }
                });
    }

    static public void addMembershipYear()
    {
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date());   //设置时间为当前时间
        ca.add(Calendar.YEAR, +1); //年份加1
        final String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ca.getTime());

        String url = ShareDefine.getUpdateMemberShipURL();

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        String rresultCode=result.get("rc").getAsString();

                        if (rresultCode.equalsIgnoreCase("1")) {
                            //更新本地数据
                            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                            editor.putBoolean("activeMembership", true);
                            editor.putString("membershipEndtime", endTime);
                            editor.commit();

                            ShareDefine.broadUserDataChange();
                        }
                    }
                });
    }

    static public void uploadUserStatData()
    {
        final String  currentPassport=SSKeychain.getPassport();

        BE_STATISTIC userData = new FlyingStatisticDAO().selectWithUserID(currentPassport);
        String url = ShareDefine.getSysOtherMoneyWithAccount(currentPassport, userData.getBEMONEYCOUNT(), userData.getBEGIFTCOUNT(), userData.getBETOUCHCOUNT());

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded
                    }
                });
    }

    static public void uploadContentStatData()
    {
        final String  currentPassport=SSKeychain.getPassport();

        FlyingTouchDAO touchDAO = new FlyingTouchDAO();

        Boolean first=true;
        String updateStr="";
        for(BE_TOUCH_RECORD touchDat:touchDAO.selectWithUserID(currentPassport))
        {
            if (first) {

                updateStr=touchDat.getBELESSONID()+";"+touchDat.getBETOUCHTIMES();
                first=false;
            }
            else{
                updateStr=updateStr+"|"+touchDat.getBELESSONID()+";"+touchDat.getBETOUCHTIMES();
            }
        }

        if(!updateStr.equals(""))
        {
            String url = ShareDefine.getSysLessonTouchWithAccount(currentPassport,updateStr);

            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result)
                        {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded
                        }
                    });
        }
    }

    //向服务器获获取备份数据和最新充值数据，在本地激活用户ID
    static public void activeAccount()
    {
        final String currentPassport= SSKeychain.getPassport();
        //向服务器获取最新用户金币统计数据
        String url = ShareDefine.getAccountDataForUserID(currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            String[] separated = resultStr.split(";");
                            if (separated.length==4)
                            {
                                int BEMONEYCOUNT = Integer.parseInt(separated[0]);
                                int BEGIFTCOUNT = Integer.parseInt(separated[1]);
                                int BETOUCHCOUNT = Integer.parseInt(separated[2]);
                                int BEQRCOUNT = Integer.parseInt(separated[3]);

                                //更新本地数据
                                BE_STATISTIC userData = new BE_STATISTIC();
                                userData.setBEUSERID(currentPassport);
                                userData.setBETIMES(0);
                                userData.setBETIMESTAMP(DateTools.getTime());
                                userData.setBEQRCOUNT(BEQRCOUNT);
                                userData.setBEMONEYCOUNT(BEMONEYCOUNT);
                                userData.setBETOUCHCOUNT(BETOUCHCOUNT);
                                userData.setBEGIFTCOUNT(BEGIFTCOUNT);

                                new FlyingStatisticDAO().saveStatic(userData);
                                ShareDefine.broadUserDataChange();

                                SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                                editor.putBoolean("activeBEAccount", true);
                                editor.commit();
                            }
                        }
                    }
                });

        ArrayList<BE_PUB_LESSON> allData =(ArrayList<BE_PUB_LESSON>)new FlyingLessonDAO().loadAllData();

        for(final BE_PUB_LESSON lesson:allData)
        {
            //向服务器获取最新用户课程活跃统计数据
            String tempurl = ShareDefine.getTouchDataForUserID(currentPassport,lesson.getBELESSONID());

            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(tempurl)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result)
                        {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded

                            if (result!=null)
                            {
                                String resultStr =result.getResult();
                                BE_TOUCH_RECORD touchData = new BE_TOUCH_RECORD();
                                touchData.setBEUSERID(currentPassport);
                                touchData.setBELESSONID(lesson.getBELESSONID());
                                touchData.setBETOUCHTIMES(Integer.parseInt(resultStr));

                                new FlyingTouchDAO().savelTouch(touchData);
                            }
                        }
                    });
        }

        //会员资格
        FlyingSysWithCenter.sysMembershipWithCenter();
    }

    //获取年卡会员数据
    static public void sysMembershipWithCenter()
    {
        final String  currentPassport=SSKeychain.getPassport();
        String url = ShareDefine.getMembershipForUserIDURL(currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();

                            String resultStr =result.getResult();

                            String[] separated = resultStr.split(";");
                            if (separated.length==3)
                            {
                                String startTime = separated[0];
                                String endTimeStr = separated[1];

                                editor.putBoolean("sysMembership", true);

                                Intent membershipIntent = new Intent(
                                        ShareDefine.getKMembershipRECEIVER_ACTION());
                                MyApplication.getInstance().sendBroadcast(membershipIntent);

                                if(endTimeStr.equalsIgnoreCase("0"))
                                {
                                    //更新本地数据
                                    editor.putBoolean("activeMembership", false);
                                    editor.commit();
                                }
                                else
                                {
                                    try
                                    {
                                        Date endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr);

                                        if (endTime.after(new Date()))
                                        {
                                            //更新本地数据
                                            editor.putBoolean("activeMembership", true);
                                            editor.putString("membershipEndtime",endTimeStr);
                                            editor.commit();
                                        }
                                        else
                                        {
                                            editor.putBoolean("activeMembership", false);
                                            editor.commit();
                                        }

                                    }
                                    catch (Exception exception)
                                    {
                                        //
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
