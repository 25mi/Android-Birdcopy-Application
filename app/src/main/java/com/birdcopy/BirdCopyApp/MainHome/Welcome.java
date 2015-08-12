package com.birdcopy.BirdCopyApp.MainHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingContext;
import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingSysWithCenter;
import com.birdcopy.BirdCopyApp.Component.UserManger.SSKeychain;
import com.birdcopy.BirdCopyApp.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Welcome extends Activity implements View.OnClickListener
{
	private AlphaAnimation start_anima;
    public final static int HTTP_RESPONSE = 0;
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

		initData();
	}

    private void initData()
    {
        getUserData();
        initBroadcast();
        initView();
    }

	private void getUserData()
    {
        if (MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
        {
            //从本地终端生成账号并注册或者验证
            String passport = SSKeychain.getPassport();

       }

        //判断是否需要升级数据

        //第一次安装或者重装或者没有激活
        if(MyApplication.getSharedPreference().getBoolean("firstLaunch",false)||
                !MyApplication.getSharedPreference().getBoolean("activeBEAccount",false))
        {
            //从服务器获取用户数据
            FlyingSysWithCenter.activeAccount();

            //获取当前用户头像
            FlyingContext.getInstance().getUserInfoByRongId(ShareDefine.getMD5(SSKeychain.getPassport()));
        }
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

                        if (result!=null)
                        {
                            mResponseStr = result.getResult();

                            if(mResponseStr !=null)
                            {
                                ImageLoader.getInstance().displayImage(mResponseStr, broadPic);
                            }
                        }
                    }
                });

        broadPic = (ImageView)view.findViewById(R.id.broadpic);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        view.clearAnimation();
        redirectTo();
    }

    private void initView()
    {
        start_anima = new AlphaAnimation(1.0f, 1.0f);
        start_anima.setDuration(1200);
        start_anima.setRepeatCount(1);
        view.startAnimation(start_anima);
        start_anima.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                redirectTo();
            }
        });
    }

    private void redirectTo()
    {
        boolean activeBEAccount=MyApplication.getSharedPreference().getBoolean("activeBEAccount", false);

        if(activeBEAccount)
        {
            if (MyApplication.getSharedPreference().getBoolean("firstLaunch",true))
            {
                String msg = "恭喜你，账户已经激活！";
                Toast.makeText(this, msg,
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
        else
        {
            if (ShareDefine.checkNetWorkStatus()==true)
            {
               tryActive();
            }
            else
            {
                //broadPic.setImageDrawable(getResources().getDrawable(R.drawable.icon));

                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("友情提醒");
                alert.setMessage("请联网再试，会自动创建和激活你的账户！");
                alert.setPositiveButton("知道了", null);

                alert.setPositiveButton("已经联网",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                tryActive();
                            }
                        });
                alert.setNeutralButton("退出程序",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                finish();
                            }
                        });

                alert.show();

            }
        }
    }

    void tryActive()
    {
        String msg = "尝试激活账户中...";
        Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();

        //从服务器获取用户数据
        FlyingSysWithCenter.activeAccount();

        //获取当前用户头像
        FlyingContext.getInstance().getUserInfoByRongId(ShareDefine.getMD5(SSKeychain.getPassport()));
    }
}
