package com.birdcopy.BirdCopyApp.Lesson;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Document.WebFragment;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.R;

import java.util.ArrayList;

/**
 * Created by birdcopy on 18/10/14.
 */
public class WebViewActivity extends FragmentActivity
{
    private WebFragment   mMainWebFragment;

    private ImageView mBackView;
    private TextView  mTitleView;
    private ImageView mShareView;

    private String web_url;
    private String web_title;
    private String mLessonID;

    /** 手势监听 */
    GestureDetector mGestureDetector;
    /** 是否需要监听手势关闭功能 */
    private boolean mNeedBackGesture = true;

    public static void startWithUrl(Context context, String url)
    {
        String lessonID = ShareDefine.getLessonIDFromOfficalURL(url);
        if (lessonID != null)
        {
            Uri uri = Uri.parse(url);
            Intent mainActity = new Intent(context, MainActivity.class);
            mainActity.putExtra("lessonID", lessonID);

            context.startActivity(mainActity);
        }
        else
        {
            Intent webAdvertisingActivityIntent = new Intent(context, WebViewActivity.class);
            webAdvertisingActivityIntent.putExtra("url", url);
            context.startActivity(webAdvertisingActivityIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normalwebview);

        setNeedBackGesture(true);

        web_url   = getIntent().getStringExtra("url");
        web_title = getIntent().getStringExtra("title");
        mLessonID = getIntent().getStringExtra("lessonID");

        initView();
    }

    private void initView()
    {
        //顶部菜单
        mBackView = (ImageView) findViewById(R.id.top_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleView = (TextView) findViewById(R.id.normal_webview_top_title);
        mTitleView.setText(web_title);
        mTitleView.setSingleLine();

        mShareView = (ImageView) findViewById(R.id.top_share);
        mShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCurrentContent();
            }
        });

        mMainWebFragment = new WebFragment();
        mMainWebFragment.webURL=web_url;


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout, mMainWebFragment).commit();
    }


    private void  shareCurrentContent()
    {
        String title = "来自"+getString(R.string.app_name)+"的精彩分享";
        String desc  = "我也有自己的App了：）";
        String urlStr = "wwww.birdcopy.com/vip/"+ ShareDefine.getLessonOwner();

        if(web_url!=null)
        {
            desc=web_title;
            urlStr = web_url;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title +"\n"+desc+"\n"+urlStr);
        startActivity(Intent.createChooser(shareIntent, "分享精彩"));

        MainActivity.awardCoin();
    }

    private void initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(WebViewActivity.this,
                    new BackGestureListener(this));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if(mNeedBackGesture){
            return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 设置是否进行手势监听
     */
    public void setNeedBackGesture(boolean mNeedBackGesture)
    {
        this.mNeedBackGesture = mNeedBackGesture;
        initGestureDetector();
    }

    /*
     * 返回
     */
    public void doBack(View view) {
        onBackPressed();
    }

}
