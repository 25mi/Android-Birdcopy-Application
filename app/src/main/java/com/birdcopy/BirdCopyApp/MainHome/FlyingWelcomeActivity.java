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
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.squareup.picasso.Picasso;

public class FlyingWelcomeActivity extends Activity
{
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
	    broadPic = (ImageView)view.findViewById(R.id.broadpic);

	    FlyingHttpTool.getAPPBroadPic(FlyingDataManager.getCurrentPassport(),
			    FlyingDataManager.getBirdcopyAppID(),
			    new FlyingHttpTool.GetAPPBroadPicListener() {
				    @Override
				    public void completion(boolean isOK, String downloadURL) {

					    if (downloadURL != null && ShareDefine.checkURL(downloadURL)) {

						    Picasso.with(getApplicationContext())
								    .load(downloadURL)
								    .into(broadPic);
					    }
					    else
					    {
						    broadPic.setImageResource(R.drawable.icon);
					    }
				    }
			    });
    }

	private void getUserData()
    {
        FlyingHttpTool.verifyOpenUDID(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                new FlyingHttpTool.VerifyOpenUDIDListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if (isOK)
                        {
                            //第一次运行切有注册记录
                            if(MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
                            {
                                FlyingDataManager.creatLocalUserProfileWithServer();

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
                FlyingDataManager.getBirdcopyAppID(),
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
