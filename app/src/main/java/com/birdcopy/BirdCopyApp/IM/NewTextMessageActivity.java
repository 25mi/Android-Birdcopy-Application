package com.birdcopy.BirdCopyApp.IM;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;

import java.util.ArrayList;
import java.util.List;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.UserInfo;

import com.birdcopy.BirdCopyApp.R;

/**
 * Created by Bob on 15/11/16.
 */
public class NewTextMessageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView mReplyListView;
    private String mTargetId;
    private Conversation.ConversationType mConversationType;
    private List<UserInfo> mUserInfoList;
    private List mNumberlist;
    private NewTextReplyAdapter mNewTextReplyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.de_ac_reply);

        getSupportActionBar().setTitle("选择回复的人");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);

        mReplyListView = (ListView) findViewById(R.id.de_reply);

        mNumberlist = new ArrayList();
        mUserInfoList = new ArrayList<UserInfo>();
        initDate();
    }

    private void initDate() {

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra("DEMO_REPLY_CONVERSATIONTYPE")&&intent.hasExtra("DEMO_REPLY_TARGETID")) {

            String conversationType = intent.getStringExtra("DEMO_REPLY_CONVERSATIONTYPE");
            mTargetId = intent.getStringExtra("DEMO_REPLY_TARGETID");

            mConversationType = Conversation.ConversationType.valueOf(conversationType);

            if (mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
                RongIM.getInstance().getRongIMClient().getDiscussion(mTargetId, new RongIMClient.ResultCallback<Discussion>() {
                    @Override
                    public void onSuccess(Discussion discussion) {

                          mNumberlist = discussion.getMemberIdList();

                        if (FlyingContext.getInstance().getSharedPreferences() != null) {
                            String   userId = ShareDefine.getMD5(FlyingDataManager.getPassport());
                            mNumberlist.remove(userId);
                        }

                        mUserInfoList = FlyingContext.getInstance().getUserInfoList(mNumberlist);

                        mNewTextReplyAdapter = new NewTextReplyAdapter(NewTextMessageActivity.this, mUserInfoList);
                        mReplyListView.setAdapter(mNewTextReplyAdapter);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            } else if (mConversationType.equals(Conversation.ConversationType.GROUP)) {

            }
        }

        mReplyListView.setOnItemClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent();
        intent.putExtra("REPLY_ID",mNumberlist.get(i).toString());
        intent.putExtra("REPLY_NAME",mUserInfoList.get(i).getName().toString());
        setResult(ShareDefine.RONG_MESSAGE_REPLY,intent);
        finish();
    }
}
