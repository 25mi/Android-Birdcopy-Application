package com.birdcopy.BirdCopyApp.MainHome;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.Component.Document.WebFragment;
import com.birdcopy.BirdCopyApp.Component.UI.ResideMenu.ResideMenu;
import com.birdcopy.BirdCopyApp.Component.UI.ResideMenu.ResideMenuItem;
import com.birdcopy.BirdCopyApp.Content.ContentActivity;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.Content.WebViewActivity;
import com.birdcopy.BirdCopyApp.ContentList.LessonListFragment;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.birdcopy.BirdCopyApp.Account.ProfileFragment;
import com.birdcopy.BirdCopyApp.Search.SearchActivity;
import com.birdcopy.BirdCopyApp.SettingsFragment;
import com.birdcopy.BirdCopyApp.UpdateVersion.UpdateAppDownload;
import com.artifex.mupdfdemo.AsyncTask;
import android.content.Intent;
import android.os.Bundle;

import android.net.Uri;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.birdcopy.BirdCopyApp.ChannelActivity;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.ContentList.LessonParser;
import com.birdcopy.BirdCopyApp.MyLessons.MyLeesonsFragment;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Scan.ScanActivity;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.pingplusplus.libone.PayActivity;

import android.view.MotionEvent;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;


public class MainActivity extends FragmentActivity implements View.OnClickListener
{
    //顶部按钮
    private ImageView mDrawMenu;
    private TextView mTopTitle;
    private ImageView mTopScan;
    private ImageView mTopSearch;

    //菜单
    private ResideMenuItem home_btn;
    //private ResideMenuItem doc_btn;
    //private ResideMenuItem video_btn;
    //private ResideMenuItem audio_btn;

    //private ResideMenuItem update_btn;

    private ResideMenuItem local_btn;
    private ResideMenuItem profile_btn;
    //private ResideMenuItem search_btn;

    private ResideMenuItem scan_btn;
    private ResideMenuItem chat_btn;

    protected ResideMenu resideMenu;
    static final public int noMenuPostion = -1;
    static final public int homeMenuPostion = 0;

    static final public int updateMenuPostion = 4;

    static final public int localContentMenuPostion = 5;
    static final public int profilePostion = 6;

    //static final public int searchMenuPostion = 7;
    static final public int scanMenuPostion= 8;
    static final public int settingMenuPostion = 9;
    static final public int chatMenuPostion = 10;

    private int mDrawMenuPosition = noMenuPostion;

    public final static int HTTP_RESPONSE = 0;
    public final static int MSG_NO_TEXT = 1;
    public final static int MSG_NO_VIDEO = 2;
    public final static int MSG_NO_AUDIO = 3;

    private  String mResponseStr=null;

    private Fragment fg;

    //RongCloud
    private  int mMessageCount=0;
    public static final String ACTION_RONGCLOUD_RECEIVE_MESSAGE = "action_rongcloud_receive_message";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initSlidingMenu();
        initView();

        initRongCloud();
    }

    /**
     * 初始化layout控件
     */
    private void initView()
    {
        mDrawMenu = (ImageView) findViewById(R.id.top_menu);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopScan = (ImageView) findViewById(R.id.top_scan);
        mTopSearch = (ImageView) findViewById(R.id.top_search);

        mDrawMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

        mTopScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scanNow();
            }
        });

        mTopSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchNow();
            }
        });

        mDrawMenuPosition = homeMenuPostion;
        mTopTitle.setText(getString(R.string.left_drawer_item_home));


        String lessonID = getIntent().getStringExtra("lessonID");
        String webURL = getIntent().getStringExtra("url");

        if(lessonID!=null)
        {
            showLessonViewWithID(lessonID);
        }
        else if(webURL!=null)
        {
            lessonID =ShareDefine.getLessonIDFromOfficalURL(webURL);

            if(lessonID!=null)
            {
                showLessonViewWithID(lessonID);
            }
            else
            {
                Intent webAdvertisingActivityIntent = new Intent(this, WebViewActivity.class);
                webAdvertisingActivityIntent.putExtra("url", webURL);
                startActivity(webAdvertisingActivityIntent);
            }
        }
        else
        {
            showHomeContent();

            Boolean  showMenuDemo =MyApplication.getSharedPreference().getBoolean("showMenuDemo",false);

            if (!showMenuDemo)
            {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                editor.putBoolean("showMenuDemo", true);
                editor.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void initSlidingMenu()
    {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        //resideMenu.setShadowVisible(false);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        // create menu items;
        home_btn         = new ResideMenuItem(this, R.drawable.ic_drawer_home, getString(R.string.left_drawer_item_home));
        //doc_btn          = new ResideMenuItem(this, R.drawable.ic_drawer_doc, getString(R.string.left_drawer_item_doc));
        //video_btn        = new ResideMenuItem(this, R.drawable.ic_drawer_video, getString(R.string.left_drawer_item_video));
        //audio_btn        = new ResideMenuItem(this,R.drawable.ic_drawer_audio,getString(R.string.left_drawer_item_audio));
        //update_btn       = new ResideMenuItem(this, R.drawable.ic_drawer_update, getString(R.string.left_drawer_item_update));

        local_btn        = new ResideMenuItem(this, R.drawable.ic_drawer_fav, getString(R.string.left_drawer_item_localconcent));
        profile_btn      = new ResideMenuItem(this, R.drawable.ic_drawer_profile, getString(R.string.left_drawer_item_profile));

        //search_btn       = new ResideMenuItem(this, R.drawable.ic_drawer_search, getString(R.string.left_drawer_item_search));
        scan_btn         = new ResideMenuItem(this, R.drawable.ic_drawer_scan, getString(R.string.left_drawer_item_scan));
        chat_btn         = new ResideMenuItem(this, R.drawable.ic_drawer_chat, getString(R.string.left_drawer_item_chat));

        home_btn.setOnClickListener(this);
        //doc_btn.setOnClickListener(this);
        //video_btn.setOnClickListener(this);
        //audio_btn.setOnClickListener(this);
        //update_btn.setOnClickListener(this);
        local_btn.setOnClickListener(this);
        profile_btn.setOnClickListener(this);
        scan_btn.setOnClickListener(this);
        //search_btn.setOnClickListener(this);
        chat_btn.setOnClickListener(this);

        resideMenu.addMenuItem(home_btn, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(doc_btn, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(video_btn, ResideMenu.DIRECTION_LEFT);
        //resideMenu.addMenuItem(audio_btn, ResideMenu.DIRECTION_LEFT);

        resideMenu.addMenuItem(local_btn, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(profile_btn, ResideMenu.DIRECTION_LEFT);

        //resideMenu.addMenuItem(search_btn, ResideMenu.DIRECTION_LEFT);

        resideMenu.addMenuItem(scan_btn, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(chat_btn, ResideMenu.DIRECTION_LEFT);

        initActiveMenu();
    }

    public void cancelMenuGesture()
    {
        FrameLayout ignored_view = (FrameLayout) findViewById(R.id.fragment_container);
        resideMenu.addIgnoredView(ignored_view);
    }
    public void resetMenuGesture()
    {
        resideMenu.clearIgnoredViewList();
    }

    void initActiveMenu()
    {
        if(ShareDefine.checkNetWorkStatus())
        {
            String texturl = ShareDefine.getLessonAccount(ShareDefine.KContentTypeText,null);

            Ion.with(MainActivity.this)
                    .load(texturl)
                    .noCache()
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
                                int number = Integer.parseInt(result.getResult());

                                if(number==0)
                                {
                                    Message message = new Message();
                                    message.what = MSG_NO_TEXT;
                                    myHandler.sendMessage(message);
                                }
                            }
                        }
                    });

            String videourl = ShareDefine.getLessonAccount(ShareDefine.KContentTypeVideo,null);

            Ion.with(MainActivity.this)
                    .load(videourl)
                    .noCache()
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
                                int number = Integer.parseInt(result.getResult());

                                if(number==0)
                                {
                                    Message message = new Message();
                                    message.what = MSG_NO_VIDEO;
                                    myHandler.sendMessage(message);
                                }
                            }
                        }
                    });

            String audiourl = ShareDefine.getLessonAccount(ShareDefine.KContentTypeAudio,null);

            Ion.with(MainActivity.this)
                    .load(audiourl)
                    .noCache()
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
                                int number = Integer.parseInt(result.getResult());

                                if(number==0)
                                {
                                    Message message = new Message();
                                    message.what = MSG_NO_AUDIO;
                                    myHandler.sendMessage(message);
                                }
                            }
                        }
                    });
        }

    }

    void initSubscript()
    {
        // Successfully got a response
        try {
            MenuTask dTask = new MenuTask();
            dTask.execute();
        }
        catch (Exception e)
        {}
    }

    private class MenuTask extends AsyncTask<String, Void,String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            int a =new FlyingLessonDAO().loadAllData().size();

            return String.valueOf(a);
        }
        @Override
        protected void onPostExecute(String result)
        {

            super.onPostExecute(result);

            int foo = Integer.parseInt(result);

            local_btn.setSubCount(Integer.toString(foo));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view)
    {

        String title = mTopTitle.getText().toString();

        if (view == home_btn)
        {
            mDrawMenuPosition = homeMenuPostion;

            title = getString(R.string.left_drawer_item_home);
            showHomeContent();
        }
        /*
        else if (view == doc_btn)
        {
            mDrawMenuPosition = docMenuPostion;

            title = getString(R.string.left_drawer_item_doc);
            showTableContent();
        }
        else if(view == video_btn)
        {
            mDrawMenuPosition = vedioMenuPostion;

            title = getString(R.string.left_drawer_item_video);
            showTableContent();
        }
        else if(view == audio_btn)
        {
            mDrawMenuPosition = audioMenuPostion;

            title = getString(R.string.left_drawer_item_audio);
            showTableContent();
        }
        else if (view == update_btn)
        {
            mDrawMenuPosition = updateMenuPostion;

            title = getString(R.string.left_drawer_item_update);
            showUpdateContent();
        }
        */
        else if (view == local_btn)
        {
            mDrawMenuPosition = localContentMenuPostion;

            title = getString(R.string.left_drawer_item_localconcent);
            showMyLessons();
        }
        else if (view == profile_btn)
        {
            mDrawMenuPosition = profilePostion;

            title = getString(R.string.left_drawer_item_profile);
            showMyProfile();
        }
        else if (view == scan_btn)
        {
            mDrawMenuPosition = scanMenuPostion;

            scanNow();
        }

        else if (view == chat_btn)
        {
            mDrawMenuPosition = chatMenuPostion;

            showChatListNow();
        }

                /*

                else if (view == search_btn)
        {
            mDrawMenuPosition = searchMenuPostion;

            searchNow();
        }
        */


        resideMenu.closeMenu();
        resetMenuGesture();

        mTopTitle.setText(title);

        updateApp();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener()
    {
        @Override
        public void openMenu()
        {
            initSubscript();
        }

        @Override
        public void closeMenu()
        {
        }
    };


    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (resideMenu.isOpened())
            {
                resideMenu.closeMenu();
            }
            else
            {
                if(fg instanceof WebFragment)
                {
                    //WebFragment webFragment=(WebFragment)fg;
                    //webFragment.onKeyDown(keyCode, event);

                    return super.onKeyDown(keyCode, event);
                }
                else
                {
                    if ((System.currentTimeMillis() - mExitTime) > 2000)
                    {
                        String msg = "再按一次退出" + getString(R.string.app_name);
                        Toast.makeText(this, msg,
                                Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    }
                    else
                    {
                        finish();
                    }
                }
            }
            return true;
        }
        //拦截MENU按钮点击事件，让他无任何操作
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler myHandler = new Handler()
    {
        // 接收到消息后处理
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case HTTP_RESPONSE:
                    MyTask dTask = new MyTask();
                    dTask.execute(mResponseStr);
                    break;

                case MSG_NO_TEXT:
                {
                     //doc_btn.setVisibility(View.GONE);
                    break;
                }

                case MSG_NO_VIDEO:
                {
                    //video_btn.setVisibility(View.GONE);
                    break;
                }

                case MSG_NO_AUDIO:
                {
                    //audio_btn.setVisibility(View.GONE);
                    break;
                }
            }

            super.handleMessage(msg);
        }
    };

    private void showLessonViewWithID(String lessonID)
    {

        String url = ShareDefine.getLessonDataByID(lessonID);

        try {
            AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url), new AsyncHttpClient.StringCallback() {

                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                    mResponseStr = s;
                    Message message = new Message();
                    message.what = HTTP_RESPONSE;
                    myHandler.sendMessage(message);
                }

                @Override
                public void onConnect(AsyncHttpResponse response) {
                    //
                }
            });

        } catch (Exception e) {
            String msg = "联网失败提醒";
            System.out.println(msg);
        }
    }

    private class MyTask extends AsyncTask<String, Void, ArrayList<BE_PUB_LESSON>>
    {
        @Override
        protected ArrayList<BE_PUB_LESSON> doInBackground(String... params)
        {
            try
            {
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                LessonParser myXMLHandler = new LessonParser();
                xr.setContentHandler(myXMLHandler);
                xr.parse(new InputSource(new ByteArrayInputStream(params[0].getBytes())));

                return  myXMLHandler.entries;
            }
            catch (Exception e)
            {
                System.out.println("XML Pasing Excpetion = " + e);
                return  null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<BE_PUB_LESSON> result)
        {
            super.onPostExecute(result);

            if(result!=null)
            {
                BE_PUB_LESSON lessonData =result.get(0);

                if(lessonData!=null)
                {
                    showLessonViewWithData(lessonData);
                }
            }
        }
    }

    public void showLessonViewWithData(BE_PUB_LESSON lessonData)
    {
        if (lessonData != null)
        {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra(ShareDefine.SAVED_OBJECT_KEY, lessonData);
            startActivityForResult(intent, ShareDefine.SHOWLESSON_REQUEST_CODE);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void showWebView(BE_PUB_LESSON lessonData)
    {
        /*
        WebFragment contentFragment = new WebFragment();
        fg=contentFragment;
        contentFragment.lessonData=lessonData;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
        */

        Intent intent = new Intent(this,WebViewActivity.class);

        intent.putExtra("url", lessonData.getBECONTENTURL());
        intent.putExtra("title", lessonData.getBETITLE());
        intent.putExtra("lessonID", lessonData.getBELESSONID());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void showHomeContent()
    {
        Fragment contentFragment = new HomeFragment();
        fg=contentFragment;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMoreColumn()
    {
        Intent intent_channel = new Intent(MainActivity.this, ChannelActivity.class);
        startActivityForResult(intent_channel, ShareDefine.CHANNEL_REQUEST);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showTableContent()
    {
        TabContentFragment contentFragment = new TabContentFragment();
        fg=contentFragment;
        contentFragment.setDrawMenuPosition(mDrawMenuPosition);

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showUpdateFeatureContent()
    {
        FreshFragment contentFragment = new FreshFragment();
        fg=contentFragment;
        contentFragment.setLoadDataDefault(true);

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showSearchContent(String tag)
    {
        mDrawMenuPosition = noMenuPostion;
        mTopTitle.setText(tag);

        LessonListFragment contentFragment = new LessonListFragment();
        fg=contentFragment;
        contentFragment.setTagString(tag);
        contentFragment.setLoadDataDefault(true);

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }

    public void showMyLessons()
    {
        Fragment contentFragment = new MyLeesonsFragment();
        fg=contentFragment;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMyProfile()
    {
        Fragment contentFragment = new ProfileFragment();
        fg=contentFragment;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void searchNow()
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ShareDefine.SEARCHING_REQUEST_CODE);
    }

    public void scanNow()
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ShareDefine.SCANNIN_REQUEST_CODE);

        /*
        SharedPreferences.Editor edit=MyApplication.getSharedPreference().edit();

        String test = MyApplication.getSharedPreference().getString("rongUserId","test");

        if(RongIM.getInstance()!= null){
            RongIM.getInstance().startPrivateChat(MainActivity.this,test,"test");
        }
        */
    }

    public void showChatListNow()
    {
        ConversationListFragment contentFragment = ConversationListFragment.getInstance();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "true")
                .build();
        contentFragment.setUri(uri);

        fg=contentFragment;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMySettings()
    {
        Fragment contentFragment = new SettingsFragment();
        fg=contentFragment;

        android.support.v4.app.FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, contentFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void  shareMyApp()
    {
        String title = "来自"+getString(R.string.app_name) +"的精彩分享";
        String desc  = "我也有自己的App了：）";

        String url = ShareDefine.getAppWebURL();

        try
        {
            String webURL= AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url),null).get();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title +"\n"+desc+"\n"+webURL);
            startActivity(Intent.createChooser(shareIntent, "分享精彩"));

            awardCoin();
            //MyApplication.getInstance().playCoinSound();
        }
        catch (Exception e)
        {
        }
    }

    public static void awardCoin()
    {
        int  awardCoin=MyApplication.getSharedPreference().getInt("awardCoin",0);
        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
        editor.putInt("awardCoin", awardCoin+1);
        editor.commit();

        BE_STATISTIC statistic = new FlyingStatisticDAO().selectWithUserID(FlyingDataManager.getCurrentPassport());

        if(statistic==null)
        {
            return;
        }

        int payaccount =statistic.getBEMONEYCOUNT()+statistic.getBEQRCOUNT();
        int giftcount =statistic.getBEGIFTCOUNT();
        if(awardCoin<10
                &&
                (
                    ( payaccount>0 && giftcount<payaccount/4 )
                    ||giftcount<100
                ))
        {
            //奖励金币数加一
            int times = statistic.getBEGIFTCOUNT() + 1;
            statistic.setBEGIFTCOUNT(times);
            new FlyingStatisticDAO().saveStatic(statistic);

            MyApplication.getInstance().playCoinSound();

            ShareDefine.broadUserDataChange();
            FlyingHttpTool.uploadMoneyData(FlyingDataManager.getCurrentPassport(),
                    FlyingDataManager.getLocalAppID(),
                    null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode)
        {
            case ShareDefine.CHANNEL_REQUEST:
            {
                /*
                if (resultCode == ShareDefine.CHANNELRESULT)
                {
                    showMenuContent(mDrawMenuPosition);
                }
                break;
                */
            }

            case ShareDefine.SCANNIN_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();

                    String scanStr = bundle.getString("result");

                    dealWithScanString(scanStr);
                }
                break;
            }

            case ShareDefine.SEARCHING_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK)
                {
                    Bundle bundle = data.getExtras();

                    String result = bundle.getString("result");
                    showSearchContent(result);
                }
                break;
            }

            case ShareDefine.SHOWLESSON_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK)
                {
                    Bundle bundle = data.getExtras();

                    String result = bundle.getString("result");
                    showSearchContent(result);
                }
                break;
            }
            case PayActivity.PAYACTIVITY_REQUEST_CODE:
            {
                if (resultCode == PayActivity.PAYACTIVITY_RESULT_CODE) {

                    if (data.getExtras().getString("result").equalsIgnoreCase("pay_successed"))
                    {
                        FlyingDataManager.addMembership();
                    }
                    else
                    {
                        Toast.makeText(
                                this,
                                data.getExtras().getString("result") + "  "
                                        + data.getExtras().getInt("code"),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void dealWithScanString(String scanStr)
    {
        String type = ShareDefine.judgeScanType(scanStr);

        if (type.equals(ShareDefine.KQRTyepeWebURL))
        {
            String lessonID = ShareDefine.getLessonIDFromOfficalURL(scanStr);
            if (lessonID != null)
            {
                showLessonViewWithID(lessonID);
            }
            else
            {
                Intent webAdvertisingActivityIntent = new Intent(this, WebViewActivity.class);
                webAdvertisingActivityIntent.putExtra("url", scanStr);
                startActivity(webAdvertisingActivityIntent);
            }
        }
        if (type.equals(ShareDefine.KQRTyepeChargeCard))
        {
            if (scanStr != null)
            {

                FlyingHttpTool.chargingCrad(FlyingDataManager.getCurrentPassport(),
                        FlyingDataManager.getLocalAppID(),
                        scanStr,
                        new FlyingHttpTool.ChargingCradListener() {
                            @Override
                            public void completion(String resultStr) {

                                Toast.makeText(MainActivity.this, resultStr, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }

        if (type.equals(ShareDefine.KQRTypeLogin))
        {
            if (scanStr != null)
            {

                String loginID = ShareDefine.getLoginIDFromQR(scanStr);

                if (loginID!=null) {

                    FlyingHttpTool.loginWithQR(loginID,
                            FlyingDataManager.getCurrentPassport(),
                            FlyingDataManager.getLocalAppID(),
                            new FlyingHttpTool.LoginWithQRListener() {
                                @Override
                                public void completion(boolean isOK) {

                                    if (isOK)
                                    {
                                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    );
                }
            }
        }

        if (type.equals(ShareDefine.KQRTyepeCode))
        {
            if (scanStr != null)
            {
                showLessonViewWithID(scanStr);
            }
        }
    }

    public  void updateApp()
    {
        MyApplication.upgradeCount++;

        if(MyApplication.upgradeCount>10)
        {

            checkNewApp();
        }
    }

    public  void checkNewApp()
    {
        String urlStr = ShareDefine.getAppVersionAndURL();
        try
        {
            AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(urlStr), new AsyncHttpClient.StringCallback() {

                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s)
                {
                    try
                    {
                        String str = s;
                        String[] separated = str.split(";");

                        String version = separated[0]; // this will contain "Fruit"
                        ShareDefine.downloadURL = separated[1]; // this will contain "Fruit"

                        MyApplication.getSharedPreference().getString("downloadURL", ShareDefine.downloadURL);

                        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                        editor.putString(ShareDefine.KAppDownloadURL, ShareDefine.downloadURL);
                        editor.commit();

                        if(compareVersionCode(version))
                        {
                            new AlertDialogWrapper.Builder(MainActivity.this)
                                    .setTitle("友情提醒")
                                    .setMessage(getString(R.string.upgrade_app__ACK))
                                    .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == AlertDialog.BUTTON_POSITIVE){

                                                new UpdateAppDownload().execute();
                                                MyApplication.upgradeCount=0;
                                            }
                                            dialog.dismiss();
                                        }
                                    }).show();

                        }
                    }
                    catch (Exception exception)
                    {
                        System.out.println("XML Pasing Excpetion = " + e);
                    }
                }

                @Override
                public void onConnect(AsyncHttpResponse response) {
                    //
                }
            });
        }
        catch (Exception e)
        {
            String msg = "联网失败提醒";
            System.out.println(msg);
        }
    }

    public boolean compareVersionCode(String serverCode)
    {
        if(serverCode!=null)
        {
            try
            {
                if(Integer.parseInt(serverCode)>ShareDefine.getVersionCode()){
                    return true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void startPDFActivity(Context context, String filePath, String title, String lessonID,boolean showThumbnails){
        try{
            Uri uri = Uri.parse(filePath);
            Intent intent = new Intent(context,MuPDFActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.putExtra(ShareDefine.KLessonTitle, title);
            intent.putExtra(ShareDefine.KLessonID,lessonID);
            intent.putExtra(MuPDFActivity.SHOW_THUMBNAILS_EXTRA, showThumbnails);

            context.startActivity(intent);
        } catch (Exception e) {
            //.e("Problem with starting PDF-activity, path: "+filePath,e);
        }
    }

    public void  initRongCloud()
    {
        //准备融云环境
        FlyingHttpTool.connectWithRongCloud();
        getConversationPush();
        getPushMessage();

        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new RongIM.OnReceiveUnreadCountChangedListener() {
            @Override
            public void onMessageIncreased(int i) {

                chat_btn.setSubCount(Integer.toString(i));

                mMessageCount=i;
            }
        });
    }

    /**
     *
     */
    private void getConversationPush() {

        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") &&getIntent().hasExtra("PUSH_TARGETID") ) {

            final String conversationType  = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");

            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {

                RongIM.getInstance().getRongIMClient().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {

                        if (conversation != null) {

                            if (conversation.getLatestMessage() instanceof ContactNotificationMessage) {
                                //startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                            } else {
                                Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                        .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            }
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            String content = intent.getData().getQueryParameter("pushContent");
            String data = intent.getData().getQueryParameter("pushData");
            String id = intent.getData().getQueryParameter("pushId");
            RongIMClient.recordNotificationEvent(id);
            Log.e("RongPushActivity", "--content--" + content + "--data--" + data + "--id--" + id);

            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {

                RongIM.getInstance().getRongIMClient().clearNotifications();
            }
            if (FlyingContext.getInstance() != null) {

                String token = FlyingDataManager.getRongToken();

                if (token.equals(ShareDefine.RONG_DEFAULT_TOKEN)) {
                    //startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    showChatListNow();
                }
                else {

                    if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null)
                    {
                        RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus();

                        if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED.equals(status)) {

                            return;
                        }
                        else if (RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING.equals(status)) {

                            return;
                        }
                        else {

                            showChatListNow();
                        }
                    }
                    else {

                        showChatListNow();
                    }
                }
            }
        }
    }
}
