package com.birdcopy.BirdCopyApp.IM;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingIMContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.MainHome.FlyingWelcomeActivity;
import java.util.Locale;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;

import com.birdcopy.BirdCopyApp.R;

/**
 * Created by Bob on 15/11/3.
 * 会话页面
 * 1，设置 ActionBar title
 * 2，加载会话页面
 * 3，push 和 通知 判断
 */
public class FlyingConversationActivity extends BaseActivity
{

    private String TAG = FlyingConversationActivity.class.getSimpleName();
    /**
     * 对方id
     */
    private String mTargetId;
    /**
     * 刚刚创建完讨论组后获得讨论组的targetIds
     */
    private String mTargetIds;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * title
     */
    private String title;
    /**
     * 是否在讨论组内，如果不在讨论组内，则进入不到讨论组设置页面
     */
    private boolean isDiscussion = false;

    private RelativeLayout mRealTimeBar;//real-time bar
    //private RealTimeLocationConstant.RealTimeLocationStatus currentLocationStatus;
    //private AbstractHttpRequest<Groups> mGetMyGroupsRequest;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        mDialog = new LoadingDialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);

        Intent intent = getIntent();
        Log.e("Rong", "conversation onCreate intent 1");

        if (intent == null || intent.getData() == null)
            return;

        Log.e("Rong", "conversation onCreate intent uri = " + intent.getDataString());

        Uri data = intent.getData();

        mTargetId = intent.getData().getQueryParameter("targetId");

        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        title = intent.getData().getQueryParameter("title");

        mTargetIds = intent.getData().getQueryParameter("targetIds");

        setActionBarTitle(mConversationType, mTargetId);

        //讨论组 @ 消息
        checkTextInputEditTextChanged();

        isPushMessage(intent);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if (intent == null || intent.getData() == null)
            return;

        mTargetId = intent.getData().getQueryParameter("targetId");

        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        title = intent.getData().getQueryParameter("title");

        mTargetIds = intent.getData().getQueryParameter("targetIds");

        setActionBarTitle(mConversationType, mTargetId);

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }


    private String mEditText;

    private void checkTextInputEditTextChanged() {

        InputProvider.MainInputProvider provider = RongContext.getInstance().getPrimaryInputProvider();
        if (provider instanceof TextInputProvider) {
            TextInputProvider textInputProvider = (TextInputProvider) provider;
            textInputProvider.setEditTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {

                        if (s.length() > 0) {
                            String str = s.toString().substring(s.toString().length() - 1, s.toString().length());

                            if (str.equals("@")) {

                                Intent intent = new Intent(FlyingConversationActivity.this, NewTextMessageActivity.class);
                                intent.putExtra("DEMO_REPLY_CONVERSATIONTYPE", mConversationType.toString());

                                if (mTargetIds != null) {
                                    UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
                                    //得到讨论组的 targetId
                                    mTargetId = fragment.getUri().getQueryParameter("targetId");
                                }
                                intent.putExtra("DEMO_REPLY_TARGETID", mTargetId);
                                startActivityForResult(intent, 29);

                                mEditText = s.toString();
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     *
     * @param intent
     */
    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;

        //push
        if (intent.getData().getScheme().equals("rong")
                && intent.getData().getQueryParameter("push") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
                String id = intent.getData().getQueryParameter("pushId");
                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);

                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }

                enterActivity();
            }

        } else {//通知过来
            //程序切到后台，收到消息后点击进入,会执行这里
            if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                enterActivity();
            }
        }
    }


    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 FlyingConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 FlyingConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {

        if (FlyingIMContext.getInstance() == null)
            return;

        String token = FlyingDataManager.getRongToken();

        if (token.equals( ShareDefine.RONG_DEFAULT_TOKEN)) {

            startActivity(new Intent(FlyingConversationActivity.this, FlyingWelcomeActivity.class));
            finish();
        } else {
            reconnect(token);
        }
    }

    private void reconnect(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                if (RongCloudEvent.getInstance() != null)
                    RongCloudEvent.getInstance().setOtherListener();

                if (FlyingIMContext.getInstance() != null) {
                    //mGetMyGroupsRequest = DemoContext.getInstance().getDemoApi().getMyGroups(io.rong.app.ui.activity.FlyingConversationActivity.this);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
            }
        });

    }


    /**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     */
    private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;

        if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null)
            return;

        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            setDiscussionActionBar(targetId, mTargetIds);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            getSupportActionBar().setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            getSupportActionBar().setTitle(R.string.de_actionbar_system);
        } else if (conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
            setAppPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
            setPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            getSupportActionBar().setTitle(R.string.main_customer);
        } else {
            getSupportActionBar().setTitle(R.string.de_actionbar_sub_defult);
        }
    }

    /**
     * 设置群聊界面 ActionBar
     *
     * @param targetId
     */
    private void setGroupActionBar(String targetId) {
        if (targetId == null)
            return;

        if (FlyingIMContext.getInstance() != null) {

            getSupportActionBar().setTitle(FlyingIMContext.getInstance().getGroupNameById(targetId));
        }
    }

    /**
     * 设置应用公众服务界面 ActionBar
     */
    private void setAppPublicServiceActionBar(String targetId) {
        if (targetId == null)
            return;
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {

            RongIM.getInstance().getRongIMClient().getPublicServiceProfile(Conversation.PublicServiceType.APP_PUBLIC_SERVICE
                    , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                @Override
                public void onSuccess(PublicServiceProfile publicServiceProfile) {
                    getSupportActionBar().setTitle(publicServiceProfile.getName().toString());
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    /**
     * 设置公共服务号 ActionBar
     */
    private void setPublicServiceActionBar(String targetId) {

        if (targetId == null)
            return;

        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {

            RongIM.getInstance().getRongIMClient().getPublicServiceProfile(Conversation.PublicServiceType.PUBLIC_SERVICE
                    , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                @Override
                public void onSuccess(PublicServiceProfile publicServiceProfile) {
                    getSupportActionBar().setTitle(publicServiceProfile.getName().toString());
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    /**
     * 设置讨论组界面 ActionBar
     */
    private void setDiscussionActionBar(String targetId, String targetIds) {

        if (targetId != null) {

            RongIM.getInstance().getRongIMClient().getDiscussion(targetId
                    , new RongIMClient.ResultCallback<Discussion>() {
                @Override
                public void onSuccess(Discussion discussion) {
                    getSupportActionBar().setTitle(discussion.getName());
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {
                    if (e.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                        getSupportActionBar().setTitle("不在讨论组中");
                        isDiscussion = true;
                        supportInvalidateOptionsMenu();
                    }
                }
            });
        } else if (targetIds != null) {
            setDiscussionName(targetIds);
        } else {
            getSupportActionBar().setTitle("讨论组");
        }
    }


    /**
     * 设置讨论组名称
     *
     * @param targetIds
     */
    private void setDiscussionName(String targetIds) {

        StringBuilder sb = new StringBuilder();
        getSupportActionBar().setTitle(targetIds);
        String[] ids = targetIds.split(",");

        if (FlyingIMContext.getInstance() != null) {

            for (int i = 0; i < ids.length; i++) {
                sb.append(FlyingIMContext.getInstance().getUserInfoByRongId(ids[i]).getName().toString());
                sb.append(",");
            }

            sb.append(FlyingDataManager.getNickName());
        }

        getSupportActionBar().setTitle(sb);
    }

    /**
     * 设置私聊界面 ActionBar
     */
    private void setPrivateActionBar(String targetId) {

        if (FlyingIMContext.getInstance() != null) {

            UserInfo userInfo = FlyingIMContext.getInstance().getUserInfoByRongId(targetId);

            if (userInfo.getName() == null || userInfo.getName().equalsIgnoreCase("")) {

                getSupportActionBar().setTitle("聊天");
            } else {

                getSupportActionBar().setTitle(userInfo.getName());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_conversation_menu, menu);

        if (mConversationType == null)
            return true;

        if (mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
            menu.getItem(0).setVisible(false);
        } else if (mConversationType.equals(Conversation.ConversationType.DISCUSSION)
                && isDiscussion) {
            menu.getItem(0).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.icon:

	            if (mConversationType == null)
		            return true;

	            enterSettingActivity();

                break;

	        case android.R.id.home:
		        finish();
		        break;
        }

	    return super.onOptionsItemSelected(item);
    }

    /**
     * 根据 targetid 和 ConversationType 进入到设置页面
     */
    private void enterSettingActivity() {

        Toast.makeText(FlyingConversationActivity.this,"预留入口",Toast.LENGTH_LONG);
        /*
        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {

            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        }
        else
        {
            //当你刚刚创建完讨论组以后获得的是 targetIds
            if (!TextUtils.isEmpty(mTargetIds)) {
                UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
                //得到讨论组的 targetId
                mTargetId = fragment.getUri().getQueryParameter("targetId");

                if (TextUtils.isEmpty(mTargetId)) {
                    Toast.makeText(FlyingConversationActivity.this, "讨论组尚未创建成功",Toast.LENGTH_SHORT);
                }
            }

            Uri uri = Uri.parse("demo://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationSetting")
                    .appendPath(mConversationType.getName())
                    .appendQueryParameter("targetId", mTargetId).build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
        */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 29 && resultCode == ShareDefine.RONG_MESSAGE_REPLY) {
            if (data != null && data.hasExtra("REPLY_NAME") && data.hasExtra("REPLY_ID")) {
                String id = data.getStringExtra("REPLY_ID");
                String name = data.getStringExtra("REPLY_NAME");
                TextInputProvider textInputProvider = (TextInputProvider) RongContext.getInstance().getPrimaryInputProvider();
                textInputProvider.setEditTextContent(mEditText + name + " ");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode,event);

    }

}
