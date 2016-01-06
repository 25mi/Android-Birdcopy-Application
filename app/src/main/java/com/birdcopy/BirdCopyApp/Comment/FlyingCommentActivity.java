package com.birdcopy.BirdCopyApp.Comment;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;
import com.birdcopy.BirdCopyApp.R;

/**
 * Created by vincentsung on 12/18/15.
 */
public class FlyingCommentActivity extends Activity {

    public static final String SAVED_DATA_KEY   = "SAVED_DATA_KEY";
    public BE_PUB_LESSON mLessonData;

    private ImageView mBackView;
    private TextView  mTitleView;

    /** 手势监听 */
    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);

        initGestureDetector();

        if(savedInstanceState!=null)
        {
            mLessonData =(BE_PUB_LESSON)savedInstanceState.getSerializable(SAVED_DATA_KEY);
        }
        else
        {
            Bundle b = getIntent().getExtras();
            mLessonData=(BE_PUB_LESSON)b.getSerializable(ShareDefine.SAVED_OBJECT_KEY);
        }

        initView();
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

        mTitleView = (TextView) findViewById(R.id.comment_top_title);
        mTitleView.setText("相关评论");

        //评论
    }

    private void initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(FlyingCommentActivity.this,
                    new BackGestureListener(this));
        }
    }

    /*
     * 返回
     */
    public void doBack(View view)
    {
        onBackPressed();
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(SAVED_DATA_KEY, mLessonData);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
