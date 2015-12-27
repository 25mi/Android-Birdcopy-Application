package com.birdcopy.BirdCopyApp.MainHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FlyingWelcomeActivity extends Activity
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
        FlyingHttpTool.verifyOpenUDID(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                new FlyingHttpTool.VerifyOpenUDIDListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if (isOK)
                        {
                            //第一次运行切有注册记录
                            if(MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
                            {
                                FlyingDataManager.creatLocalUSerProfileWithServer();

                                SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                                editor.putBoolean("firstLaunch", false);
                            }

                            letsGo();
                        }
                        else
                        {
                            tryActive();
                        }
                    }
                });
	}

    public void letsGo()
    {
        if (MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
        {
            String msg = "恭喜你，账户已经激活！";
            Toast.makeText(FlyingWelcomeActivity.this, msg,
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

        FlyingHttpTool.regOpenUDID(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                new FlyingHttpTool.RegOpenUDIDListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if (isOK) {
                            letsGo();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(FlyingWelcomeActivity.this);

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
}
