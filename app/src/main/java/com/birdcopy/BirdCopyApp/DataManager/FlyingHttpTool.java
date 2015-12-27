package com.birdcopy.BirdCopyApp.DataManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.Comment.CommentDataResult;
import com.birdcopy.BirdCopyApp.Comment.FlyingCommentData;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_TOUCH_RECORD;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingTouchDAO;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Tools.DateTools;
import com.birdcopy.BirdCopyApp.IM.RongCloudEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.pingplusplus.libone.PayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by vincentsung on 12/16/15.
 */
public class FlyingHttpTool {

    static public void connectWithRongCloud()
    {
        final String  currentPassport=FlyingDataManager.getCurrentPassport();

        String url = ShareDefine.getRongTokenURL(currentPassport);

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        if (e != null) {
                            //Toast.makeText(FlyingWelcomeActivity.this, "Error get RongToken", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String code = result.get("rc").getAsString();

                        if (code.equals("1")) {

                            String rongDeviceKoken = result.get("token").getAsString();

                            //保存Rong Token
                            SharedPreferences.Editor edit = MyApplication.getSharedPreference().edit();
                            edit.putString(ShareDefine.RONG_TOKEN, rongDeviceKoken);
                            edit.apply();

                            httpGetTokenSuccess(rongDeviceKoken);

                        } else {
                            String errorInfo = result.get("rm").getAsString();
                            //Toast.makeText(FlyingWelcomeActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    static private void httpGetTokenSuccess(String token) {

        Log.e("LoginActivity", "---------httpGetTokenSuccess----------:" + token);
        try {
            /**
             * IMKit SDK调用第二步
             *
             * 建立与服务器的连接
             *
             * 详见API
             * http://docs.rongcloud.cn/api/android/imkit/index.html
             */

//            token = "dNcIdu8Eqtu7iNca1gMhzs2yq+hfEluLjZ78E1qo4hGRHcB01HLt4SCyc1P/x3rYpMLVNO7rD0vC99se33P+Aw==";
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            Log.e("LoginActivity", "---------onTokenIncorrec----------:");
                            SharedPreferences.Editor edit = MyApplication.getSharedPreference().edit();
                            edit.putString(ShareDefine.RONG_TOKEN, "");
                            edit.apply();
                        }

                        @Override
                        public void onSuccess(String userId) {
                            Log.e("LoginActivity", "---------onSuccess userId----------:" + userId);
                            SharedPreferences.Editor edit = MyApplication.getSharedPreference().edit();
                            edit.putString("rongUserId", userId);
                            edit.apply();

                            RongCloudEvent.getInstance().setOtherListener();
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                            Log.e("LoginActivity", "---------onError ----------:" + e);
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //#pragma 个人信息
    public interface GetUserInfoByopenIDListener {

        void completion(UserInfo userInfo);
    }

    static public void getUserInfoByopenID(final String account,
                                   String appID,
                                   final GetUserInfoByopenIDListener delegate ) {

        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/tu_rc_get_usr_from_hp.action?tuser_key=" +
                account;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        UserInfo userInfo = null;

                        String code = result.get("rc").getAsString();

                        if (code.equals("1")) {

                            String name = result.get("name").getAsString();
                            Uri uri = Uri.parse(result.get("portraitUri").getAsString());
                            userInfo = new UserInfo(ShareDefine.getMD5(account), name, uri);

                            FlyingContext.getInstance().addOrReplaceRongUserInfo(userInfo);
                        }

                        if (delegate != null) {
                            delegate.completion(userInfo);
                        }
                    }
                });

    }

    public interface RequestUploadPotraitListener {

        void completion(boolean isOK);
    }

    static public void requestUploadPotrait(final String acount,
                                            final String appID,
                                            File portraitFile,
                                            final RequestUploadPotraitListener delegate)
    {
        String url="http://"+ ShareDefine.getServerNetAddress()+"/tu_rc_sync_urp_from_hp.action";

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .setMultipartParameter("tuser_key", acount)
                .setMultipartFile("portrait", portraitFile)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        if (e != null) {
                            //Toast.makeText(getActivity(), "upload portarit", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //上传图片到服务器，成功后通知融云服务器更新用户信息
                        String code = result.get("rc").getAsString();
                        if (code.equals("1")) {

                            String  portraitUri =  result.get("portraitUri").getAsString();

                            if(portraitUri==null)
                            {
                                portraitUri="http://www.birdcopy.com/img/logo.png";
                            }

                            //更新本地信息
                            FlyingDataManager.setPortraitUri(portraitUri);

                            refreshUesrInfo(acount,
                                    appID,
                                    null,
                                    portraitUri,
                                    null,
                                    new RefreshUesrInfoListener() {
                                        @Override
                                        public void completion(boolean isOK) {
                                            delegate.completion(isOK);
                                        }
                                    }
                            );
                        }
                        else
                        {
                            delegate.completion(false);
                        }
                    }
                });
    }

    public interface RefreshUesrInfoListener {

        void completion(boolean isOK);
    }

    static public void refreshUesrInfo(final String acount,
                                       String appID,
                                       final String nickName,
                                       final String portraitUri,
                                       final String br_intro,
                                       final RefreshUesrInfoListener delegate)
    {
        String url = "http://" +
                ShareDefine.getServerNetAddress() +
                "/tu_rc_sync_urb_from_hp.action?tuser_key=" +
                acount+
                "&app_id="+
                appID;

        if (nickName != null) {
            url += "&name=";
            url += nickName;
        }

        if (portraitUri != null) {
            url += "&portrait_uri=";
            url += portraitUri;
        }

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        boolean resultCode=false;
                        if (e != null) {
                            //Toast.makeText(getActivity(), "Error get RongToken", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String code = result.get("rc").getAsString();

                        if (code.equals("1")) {

                            resultCode=true;

                            //更新本地信息
                            if(nickName!=null)
                            {
                                FlyingDataManager.setNickName(nickName);
                            }

                            if(portraitUri!=null)
                            {
                                FlyingDataManager.setPortraitUri(portraitUri);
                            }

                            //更新融云信息
                            String tempNickName = nickName;
                            if (tempNickName == null) {
                                tempNickName=FlyingDataManager.getNickName();
                            }

                            String tempPortraitURL = portraitUri;
                            if(tempPortraitURL==null)
                            {
                                tempPortraitURL=FlyingDataManager.getPortraitUri();
                            }

                            String rongID = ShareDefine.getMD5(acount);

                            FlyingContext.getInstance().addOrReplaceRongUserInfo(new UserInfo(rongID, tempNickName, Uri.parse(tempPortraitURL)));
                        }

                        if (delegate != null) {
                            delegate.completion(resultCode);
                        }
                    }
                });
    }

    //pragma  用户激活相关
    public interface RegOpenUDIDListener {

        void completion(boolean isOK);
    }

    static public void regOpenUDID(String acount,
                                   String appID,
                                   final RegOpenUDIDListener delegate )
    {
        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/ua_reg_user_from_hp.action?user_key="+
                acount+
                "&app_id="+
                appID+
                "&type=reg";

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result != null) {
                            String resultStr = result.getResult();

                            if (resultStr.equals("0") || resultStr.equals("1")) {
                                if (delegate != null) {

                                    delegate.completion(true);
                                }
                            } else {
                                if (delegate != null) {

                                    delegate.completion(false);
                                }
                            }
                        }
                    }
                });
    }

    public interface VerifyOpenUDIDListener {

        void completion(boolean isOK);
    }

    static public void verifyOpenUDID(String account,
                                   String appID,
                                   final VerifyOpenUDIDListener delegate )
    {

        String url =  "http://"+
                ShareDefine.getServerNetAddress()+
                "/tu_ua_get_status_from_tn.action?tuser_key="+
                account+
                "&app_id="+
                appID;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        boolean isOK =false;
                        if (result!=null)
                        {
                            String resultCode=result.get("rs").getAsString();

                            if (!resultCode.equalsIgnoreCase("-1")) {

                                isOK=true;
                            }
                        }

                        if(delegate!=null)
                        {
                            delegate.completion(isOK);
                        }
                    }
                });
    }

    //用终端登录官网后台
    public interface LoginWithQRListener {

        void completion(boolean isOK);
    }

    static public void loginWithQR(String loginID,
                                   String account,
                                   String appID,
                                   final LoginWithQRListener delegate )
    {

        String url =  "http://"+
                ShareDefine.getServerNetAddress()+
                "/ua_send_prelogin_info_from_hp.action?user_key="+
                account+
                "&oth1="+
                loginID+
                "&app_id="+
                appID;

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

                        boolean isOK =false;

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            if(resultStr.matches("[0-9]+"))
                            {
                                // 登录成功
                                if(Integer.parseInt(resultStr)==1)
                                {
                                    Toast.makeText(MyApplication.getInstance().getApplicationContext(), "扫描登录成功", Toast.LENGTH_SHORT).show();
                                }

                                isOK=true;
                            }
                        }

                        if(delegate!=null)
                        {
                            delegate.completion(isOK);
                        }

                    }
                });
    }

    //pragma 购买行为,反馈信息在Activity
    public static void toBuyProduct(final Activity activity,
                                    final String account,
                                    final String appID,
                                    final Product good)
    {
        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/pa_get_on_from_tn.action?"+
                "tuser_key="+
                account+
                "&app_id="+
                appID;

        Ion.with(activity)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        // 产生个订单号
                        String orderNo=result.get("order_no").getAsString();

                        // 计算总金额（以分为单位）
                        int amount = good.productPrice*good.count;
                        JSONArray billList = new JSONArray();

                        billList.put(good.productName+ " x " + good.count);

                        // 构建账单json对象
                        JSONObject bill = new JSONObject();
                        JSONObject displayItem = new JSONObject();
                        try {
                            displayItem.put("name", "商品");
                            displayItem.put("contents", billList);
                            JSONArray display = new JSONArray();
                            display.put(displayItem);

                            // 自定义的额外信息 选填
                            JSONObject extras = new JSONObject();
                            extras.put("subject", "商品");
                            extras.put("body", billList);
                            extras.put("tuser_key",account);
                            extras.put("app_id",appID);

                            bill.put("order_no", orderNo);
                            bill.put("amount", amount);
                            bill.put("display", display);
                            bill.put("extras", extras);// 该字段选填
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        String URL = ShareDefine.getPingplusOnePayURL();

                        // 发起支付
                        PayActivity.SHOW_CHANNEL_ALIPAY = true;
                        PayActivity.CallPayActivity(activity, bill.toString(), URL);
                    }
                });
    }

    //pragma  会员相关

    public static final String MEMBERSHIP_STARTTIME   = "membershipStartTime";
    public static final String MEMBERSHIP_ENDTIME   = "membershipEndTime";

    public interface GetMembershipListener {

        void completion(Date startDate,Date endDate);
    }

    static public void getMembership(String account,
                              String appID,
                              final GetMembershipListener delegate)
    {
        String url ="http://"+
                ShareDefine.getServerNetAddress()+
                "/ua_get_user_info_from_hp.action?user_key="+
                account+
                "&app_id="+
                appID+
                "&type=validth";

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

                                try
                                {
                                    Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
                                    Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr);

                                    //更新本地数据
                                    editor.putString(MEMBERSHIP_STARTTIME, startTime);
                                    editor.putString(MEMBERSHIP_ENDTIME, endTimeStr);

                                    editor.commit();

                                    if(delegate!=null)
                                    {
                                        delegate.completion(startDate,endDate);
                                    }
                                }
                                catch (Exception exception)
                                {
                                    //
                                }
                            }
                        }
                    }
                });

    }

    public interface UpdateMembershipListener {

        void completion(boolean isOK);
    }

    static public void updateMembership(String account,
                                           String appID,
                                           Date startDate,
                                           Date endDate,
                                           final UpdateMembershipListener delegate)
    {
        final String startDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
        final String endDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate);

        try
        {
            String startDateUTFStr= URLEncoder.encode(startDateStr, "UTF-8");
            String endDateUTFStr = URLEncoder.encode(endDateStr,"UTF-8");

            String url ="http://"+
                    ShareDefine.getServerNetAddress()+
                    "/ua_sync_validth_from_hp.action?"+
                    "tuser_key="+
                    account+
                    "&app_id="+
                    appID+
                    "&vthg_type=21"+
                    "&start_time="+
                    startDateUTFStr+
                    "&end_time="+
                    endDateUTFStr;

            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error

                            String resultCode=result.get("rc").getAsString();

                            if (resultCode.equalsIgnoreCase("1")) {
                                //更新本地数据
                                SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();

                                editor.putString(MEMBERSHIP_STARTTIME, startDateStr);
                                editor.putString(MEMBERSHIP_ENDTIME, endDateStr);
                                editor.commit();

                                ShareDefine.broadUserDataChange();

                                if(delegate!=null)
                                {
                                    delegate.completion(true);
                                }
                            }
                            else
                            {
                                if(delegate!=null)
                                {
                                    delegate.completion(false);
                                }
                            }
                        }
                    });
        }
        catch (Exception e)
        {
            //
            if(delegate!=null)
            {
                delegate.completion(false);
            }
        }
    }

    //pragma  金币相关
    public interface GetMoneyDataListener {

        void completion(boolean isOK);
    }

    static public void getMoneyData(String account,
                          String appID,
                          final GetMoneyDataListener delegate) {

        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/ua_get_user_info_from_hp.action?user_key="+
                account+
                "&app_id="+
                appID+
                "&type=accobk";

        final String currentAccount=account;

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
                                userData.setBEUSERID(currentAccount);
                                userData.setBETIMES(0);
                                userData.setBETIMESTAMP(DateTools.getTime());
                                userData.setBEQRCOUNT(BEQRCOUNT);
                                userData.setBEMONEYCOUNT(BEMONEYCOUNT);
                                userData.setBETOUCHCOUNT(BETOUCHCOUNT);
                                userData.setBEGIFTCOUNT(BEGIFTCOUNT);

                                if(delegate!=null)
                                {
                                    delegate.completion(false);
                                }
                            }
                        }
                    }
                });
    }

    public interface UploadMoneyDataListener {

        void completion(boolean isOK);
    }

    static public void uploadMoneyData(String account,
                             String appID,
                             final UploadMoneyDataListener delegate) {

        BE_STATISTIC userData = new FlyingStatisticDAO().selectWithUserID(account);

        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/ua_sync_accobk_from_hp.action?user_key="+
                account+
                "&app_id="+
                appID+
                "&appletpp_sum="+
                userData.getBEMONEYCOUNT()+
                "&reward_sum="+
                userData.getBEGIFTCOUNT()+
                "&consume_sum="+
                userData.getBETOUCHCOUNT();

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
                        if (result!=null) {
                            String resultStr = result.getResult();

                            if(resultStr.contentEquals("1"))
                            {
                                if(delegate!=null)
                                {
                                    delegate.completion(true);
                                }
                            }
                            else
                            {
                                if(delegate!=null)
                                {
                                    delegate.completion(false);
                                }
                            }
                        }
                        else
                        {
                            if(delegate!=null)
                            {
                                delegate.completion(false);
                            }
                        }
                    }
                });
    }

    //获取充值卡数据
    public interface GetQRDataListener {

        void completion(boolean isOK);
    }

    static public void getQRData(String account,
                             String appID,
                             final GetQRDataListener delegate) {

        String url= "http://"+
                ShareDefine.getServerNetAddress()+
                "/la_get_user_info_from_hp.action?user_key="+
                account+
                "&app_id="+
                appID+
                "&type=topup_pwd_total";

        final String currentAccount=account;

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
                                BE_STATISTIC userData = statisticDAO.selectWithUserID(currentAccount);
                                userData.setBEQRCOUNT(resultCode);
                                statisticDAO.saveStatic(userData);

                                ShareDefine.broadUserDataChange();

                                if(delegate!=null)
                                {
                                    delegate.completion(true);
                                }
                            }
                        }
                    }
                });
    }

    //向服务器帐户进行充值
    public interface ChargingCradListener {

        void completion(String resultStr);
    }

    static public void chargingCrad(String cardID,
                             String account,
                             String appID,
                             final ChargingCradListener delegate) {

        String url= "http://"+
                ShareDefine.getServerNetAddress()+
                "/la_topup_pwd_from_hp.action?user_key="+
                account+
                "&app_id="+
                appID+
                "&topup_pwd="+
                cardID;

        final String currentAccount=account;

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

                                    BE_STATISTIC userData = statisticDAO.selectWithUserID(currentAccount);
                                    chargCode =resultCode-userData.getBEQRCOUNT();

                                    userData.setBEQRCOUNT(resultCode);
                                    statisticDAO.saveStatic(userData);
                            }

                            String showResult;

                            if(chargCode>0)
                            {
                                showResult="你目前充值总额是:"+resultStr;
                                ShareDefine.broadUserDataChange();
                            }
                            else
                            {
                                showResult="充值失败，请重试！"+" 原因："+responseStr;
                            }

                            if(delegate!=null)
                            {
                                delegate.completion(showResult);
                            }
                        }
                    }
                });
    }

    //向服务器获课程统计数据
    public interface GetContentStatisticListener {

        void completion(boolean isOK);
    }

    static public void getContentStatistic(String account,
                             String appID,
                             final GetContentStatisticListener delegate) {

        ArrayList<BE_PUB_LESSON> allData =(ArrayList<BE_PUB_LESSON>)new FlyingLessonDAO().loadAllData();

        final String currentAccount=account;

        int i=0;

        for(final BE_PUB_LESSON lesson:allData)
        {
            //向服务器获取最新用户课程活跃统计数据

            String tempurl =  "http://"+
                    ShareDefine.getServerNetAddress()+
                    "/ua_get_user_info_from_hp.action?user_key="+
                    account+
                    "&app_id="+
                    appID+
                    "&type=lnclick"+
                    "&ln_id="+
                    lesson.getBELESSONID();


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
                                touchData.setBEUSERID(currentAccount);
                                touchData.setBELESSONID(lesson.getBELESSONID());
                                touchData.setBETOUCHTIMES(Integer.parseInt(resultStr));

                                new FlyingTouchDAO().savelTouch(touchData);
                            }
                        }
                    });

            i=i+1;
        }

        if (i==allData.size())
        {
            if(delegate!=null)
            {
                delegate.completion(true);
            }
        }
        else
        {
            if(delegate!=null)
            {
                delegate.completion(false);
            }
        }
    }

    public interface UploadContentStatisticListener {

        void completion(boolean isOK);
    }

    static public void uploadContentStatistic(String account,
                                    String appID,
                                    final UploadContentStatisticListener delegate) {

        FlyingTouchDAO touchDAO = new FlyingTouchDAO();

        Boolean first=true;
        String updateStr="";
        for(BE_TOUCH_RECORD touchDat:touchDAO.selectWithUserID(account))
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
            String url =  "http://"+
                    ShareDefine.getServerNetAddress()+
                    "/ua_sync_lnclick_from_hp.action?user_key="+
                    account+
                    "&app_id="+
                    appID+
                    "&lncks="+
                    updateStr;

            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result)
                        {
                            if (result!=null) {
                                String resultStr = result.getResult();

                                if(resultStr.contentEquals("1"))
                                {
                                    if(delegate!=null)
                                    {
                                        delegate.completion(true);
                                    }
                                }
                                else
                                {
                                    if(delegate!=null)
                                    {
                                        delegate.completion(false);
                                    }
                                }
                            }
                            else
                            {
                                if(delegate!=null)
                                {
                                    delegate.completion(false);
                                }
                            }
                        }
                    });
        }
    }

    public interface GetCommentListListener {

        void completion(ArrayList<FlyingCommentData> commentList,String allRecordCount);
    }

    static public void getCommentList(String contentID,
                                      String contentType,
                                      int pageNumber,
                                      final GetCommentListListener delegate )
    {
        String sortBy="ins_time desc";

        try
        {
            sortBy=URLEncoder.encode(sortBy,"utf-8");
        }
        catch (Exception e)
        {
            //
        }

        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/tu_cm_get_ct_list_from_tn.action?perPageCount="+
                ShareDefine.kperpageLessonCount+
                "&page="+
                pageNumber+
                "&ct_id="+
                contentID+
                "&ct_type="+
                contentType+
                "&page="+
                pageNumber+
                "&sortindex="+
                sortBy;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        if (result!=null) {
                            String resultStr = result.getResult();

                            Gson gson = new Gson();
                            java.lang.reflect.Type type = new TypeToken<CommentDataResult>() {
                            }.getType();
                            CommentDataResult commentDataResult = gson.fromJson(resultStr, type);

                            if (delegate != null) {
                                delegate.completion(commentDataResult.rs, commentDataResult.allRecordCount);
                            }
                        }
                    }
                });
    }

    public interface UpdateCommentListener {

        void completion(boolean isOK);
    }

    static public void updateComment(FlyingCommentData commentData,
                                      String appID,
                                      final UpdateCommentListener delegate )
    {
        String url = "http://"+
                ShareDefine.getServerNetAddress()+
                "/tu_add_ct_from_tn.action?tuser_key="+
                commentData.userID+
                "&ct_id="+
                commentData.contentID+
                "&ct_type="+
                commentData.contentType+
                "&name="+
                commentData.nickName+
                "&portrait_url="+
                commentData.portraitURL+
                "&content="+
                commentData.commentContent+
                "&app_id="+
                ShareDefine.getLocalAppID();


        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        boolean isOK=false;

                        String code = result.get("rc").getAsString();

                        if (code.equals("1")) {

                            isOK=true;
                        }

                        if(delegate!=null)
                        {
                            delegate.completion(isOK);
                        }
                    }
                });
    }
}
