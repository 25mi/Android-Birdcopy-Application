package com.birdcopy.BirdCopyApp.IM;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;
import com.birdcopy.BirdCopyApp.R;

import java.util.Locale;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by birdcopy on 6/20/15.
 */

public class ConversationActivity extends FragmentActivity {

    private ImageView mBackView;
    private TextView mTitleView;

    private Uri rongUri;

    private Conversation.ConversationType mConversationType;
    private String mTitle;

    /**
     * 手势监听
     */
    GestureDetector mGestureDetector;
    /**
     * 是否需要监听手势关闭功能
     */
    private boolean mNeedBackGesture = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation);

        setNeedBackGesture(true);

        initData();
        initView();
    }

    private void initData() {

        Intent intent = getIntent();
        rongUri=intent.getData();

        String targetId = intent.getData().getQueryParameter("targetId");
        String targetIds = intent.getData().getQueryParameter("targetIds");
        String mDiscussionId = intent.getData().getQueryParameter("discussionId");

        if (targetId != null)
        {
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        }
        else if (targetIds != null)
        {
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        }

        if(mConversationType== Conversation.ConversationType.PRIVATE)
        {
            UserInfo userInfo =FlyingContext.getInstance().getUserInfoByRongId(targetId);

            if(userInfo!=null)
            {
                mTitle = userInfo.getName();
            }
        }

        if (mTitle == null)
        {
            if (mConversationType == Conversation.ConversationType.CHATROOM)
            {
                mTitle = "聊天室";
            }
            else
            {
                mTitle = "聊天";
            }
        }
    }

    private void initView() {
        //顶部菜单
        mBackView = (ImageView) findViewById(R.id.top_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleView = (TextView) findViewById(R.id.converation_top_title);
        mTitleView.setText(mTitle);

        ConversationFragment fragment =  (ConversationFragment)getSupportFragmentManager().findFragmentById(R.id.conversation);
        Uri uri =rongUri.buildUpon().appendQueryParameter("title", mTitle).build();

        fragment.setUri(uri);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    /*
     * 设置是否进行手势监听
     */
    public void setNeedBackGesture(boolean mNeedBackGesture) {
        this.mNeedBackGesture = mNeedBackGesture;
        initGestureDetector();
    }

    private void initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(ConversationActivity.this,
                    new BackGestureListener(this));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mNeedBackGesture) {
            return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
    * 返回
    */
    public void doBack(View view) {

        onBackPressed();
    }
}