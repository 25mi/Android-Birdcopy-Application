package com.birdcopy.BirdCopyApp.MainHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.IM.RongCloudEvent;
import com.birdcopy.BirdCopyApp.R;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class Welcome extends Activity
{
    private  String mResponseStr=null;
    View view;
    ImageView broadPic;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.welcome, null);
		setContentView(view);

        jumpToNext();
	}

    private void jumpToNext()
    {
        initBroadcast();
        getUserData();
    }

    private void initBroadcast()
    {
        String url = ShareDefine.getAppBroadURL();

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result != null) {
                            mResponseStr = result.getResult();

                            if (mResponseStr != null) {
                                ImageLoader.getInstance().displayImage(mResponseStr, broadPic);
                            }
                        }
                    }
                });

        broadPic = (ImageView)view.findViewById(R.id.broadpic);
    }

	private void getUserData()
    {
        FlyingHttpTool.verifyOpenUDID(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                new FlyingHttpTool.VerifyOpenUDIDListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if (isOK)
                        {
                            //第一次运行切有注册记录
                            if(MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
                            {
                                getUserDataFormServer();

                                SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                                editor.putBoolean("firstLaunch", false);
                            }

                            letsGo();
                        }
                        else {
                            tryActive();
                        }
                    }
                });
	}

    public  void  getUserDataFormServer()
    {
        FlyingDataManager.creatLocalUSerProfileWithServer();

        //获取当前用户头像\资料
        FlyingContext.getInstance().getUserInfoByRongId(ShareDefine.getMD5(FlyingDataManager.getPassport()));
    }

    public void letsGo()
    {
        //准备融云环境
        connectWithRongCloud();

        if (MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
        {
            String msg = "恭喜你，账户已经激活！";
            Toast.makeText(Welcome.this, msg,
                    Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
            editor.putBoolean("firstLaunch",false);
            editor.commit();
        }

        //自动打开Menu
        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
        editor.putBoolean("showMenuDemo", false);
        editor.commit();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

        //记录当前金币奖励情况
        editor.putInt("awardCoin", 0);
        editor.commit();

        MainActivity.awardCoin();
    }

    private void tryActive()
    {
        String msg = "尝试激活账户中...";
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();

        FlyingHttpTool.regOpenUDID(FlyingDataManager.getPassport(),
                ShareDefine.getLocalAppID(),
                new FlyingHttpTool.RegOpenUDIDListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if (isOK)
                        {
                            letsGo();
                        }
                        else
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Welcome.this);

                            alert.setTitle("友情提醒");
                            alert.setMessage("请联网再试，会自动创建和激活你的账户！");
                            alert.setPositiveButton("知道了", null);

                            alert.setPositiveButton("已经联网",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            tryActive();
                                        }
                                    });
                            alert.setNeutralButton("退出程序",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            finish();
                                        }
                                    });

                            alert.show();
                        }
                    }
                });
    }

    private void connectWithRongCloud()
    {
        final String  currentPassport=FlyingDataManager.getPassport();

        String url = ShareDefine.getRongTokenURL(currentPassport);

        Ion.with(Welcome.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error

                        if (e != null) {
                            Toast.makeText(Welcome.this, "Error get RongToken", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String code = result.get("rc").getAsString();

                        if (code.equals("1")) {

                            String rongDeviceKoken = result.get("token").getAsString();

                            //保存Rong Token
                            SharedPreferences.Editor edit = MyApplication.getSharedPreference().edit();
                            edit.putString("rongToken", rongDeviceKoken);
                            edit.apply();

                            httpGetTokenSuccess(rongDeviceKoken);

                        } else {
                            String errorInfo = result.get("rm").getAsString();
                            Toast.makeText(Welcome.this, errorInfo, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void httpGetTokenSuccess(String token) {

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
                            edit.putString("rongToken", "");
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
}
