package com.birdcopy.BirdCopyApp.Content;

import android.content.*;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.afollestad.materialdialogs.MaterialDialog;
import com.birdcopy.BirdCopyApp.Comment.FlyingCommentData;
import com.birdcopy.BirdCopyApp.Comment.FlyingCommentListAdapter;
import com.birdcopy.BirdCopyApp.DataManager.Product;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_TOUCH_RECORD;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingLessonDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingTouchDAO;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Document.CommonIntent;
import com.birdcopy.BirdCopyApp.Component.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.db.DownloadDao;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils.DownloadConstants;
import com.birdcopy.BirdCopyApp.Component.Download.HttpDownloader.utils.MyIntents;
import com.birdcopy.BirdCopyApp.Component.UI.tagview.OnTagClickListener;
import com.birdcopy.BirdCopyApp.Component.UI.tagview.Tag;
import com.birdcopy.BirdCopyApp.Component.UI.tagview.TagView;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.ContentList.LessonParser;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.Media.FlyingPlayerActivity;
import com.birdcopy.BirdCopyApp.R;
import com.artifex.mupdfdemo.AsyncTask;
import com.dgmltn.shareeverywhere.ShareView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pingplusplus.libone.PayActivity;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class ContentActivity extends FragmentActivity
{
    public static final String SAVED_DATA_KEY   = "SAVED_DATA_KEY";
    public final static int HTTP_RESPONSE = 0;
    private  String mResponseStr=null;
    public static final Uri   DOWNLOADCONTENTOBSERURI= Uri.parse("content://downloads/my_downloads");

    public BE_PUB_LESSON mLessonData;

    private ImageView mBackView;
    private TextView  mTitleView;
    private ShareView mShareView;

    private ImageView mCoverView;
    private TextView  mLessonTitleView;
    private Button    mBuyButton;
    private Button    mPlayButton;
    private TagView   mTagView;
    private TextView  mDescView;

    //评论
    private ListView  mCommentListView;
    private View      mFooterView;

    private View mInputCommnetBox;
    private EditText  mCommnetEditText;
    private ImageView mInputImagView;

    //private ImageView mLessonQRImageview;

    //用户数据相关
    boolean mIsMembership=false;
    private UserDataChangeReceiver mUserDataReceiver;
    private ArrayList<String> mTagList=null;

    //评论数据
    public final static int SET_COMMENTLIST = 0;

    private FlyingCommentListAdapter mAdapter;

    private ArrayList<FlyingCommentData> mData= new ArrayList<FlyingCommentData>();
    int     mMaxNumOfComments= ShareDefine.MAX_INT;
    int     currentLodingIndex=0;
    Boolean mIsLastRow=false;

    //下载用
    private boolean mHasRight=false;
    private boolean mPlayonline=false;

    private FlyingLessonDAO mDao;

    public  int mDownloadStatus = DownloadConstants.STATUS_DEFAULT;
    public  double mDownlaodProress = 0;

    private DownloadDao mDownloadDao;

    private HttpDownloadReceiver mDownloadReceiver;

    /** 手势监听 */
    GestureDetector mGestureDetector;
    /** 是否需要监听手势关闭功能 */
    private boolean mNeedBackGesture = false;
    private List<View> mIgnoredViews= new ArrayList<View>();

    public ContentActivity()
    {
        // Empty constructor required for Fragment subclasses
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson);

        setNeedBackGesture(true);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(savedInstanceState!=null)
        {
            mLessonData =(BE_PUB_LESSON)savedInstanceState.getSerializable(SAVED_DATA_KEY);
        }
        else
        {
            Bundle b = getIntent().getExtras();
            mLessonData=(BE_PUB_LESSON)b.getSerializable(ShareDefine.SAVED_OBJECT_KEY);
        }

        mDao= new FlyingLessonDAO();
        mDownloadDao = new DownloadDao(MyApplication.getInstance());

        initData();
        initView();

        mUserDataReceiver = new UserDataChangeReceiver();
        IntentFilter UserDataChangefilter = new IntentFilter();
        UserDataChangefilter.addAction(ShareDefine.getKUSERDATA_CHNAGE_RECEIVER_ACTION());
        registerReceiver(mUserDataReceiver, UserDataChangefilter);

        mDownloadReceiver = new HttpDownloadReceiver();
        IntentFilter downloadfilter = new IntentFilter();
        downloadfilter.addAction(ShareDefine.getKRECEIVER_ACTION());
        registerReceiver(mDownloadReceiver, downloadfilter);
    }

    private void initData()
    {
        //权限问题
        mHasRight=false;
        if(mLessonData.getBECoinPrice()==0) mHasRight=true;

        if(mHasRight==false)
        {
            inquiryRightWithUserID();
        }

        //解析Tag云
        mTagList =new ArrayList<String>(Arrays.asList(mLessonData.getBETAG().split(" ")));

        //下载以及是否自动播放问题
        mPlayonline=false;
        mLessonData.setBEDLSTATE(false);
    }

    private void inquiryRightWithUserID()
    {
        //获取年费会员数据
        new FlyingHttpTool().getMembership(FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                new FlyingHttpTool.GetMembershipListener() {
                    @Override
                    public void completion(Date startDate, Date endDate) {

                        if (endDate.after(new Date())) {
                            mIsMembership = true;
                            mHasRight = true;
                        } else {
                            mIsMembership = false;

                            Toast.makeText(ContentActivity.this, "抱歉，你没有相关权限。请在个人帐户购买会员或者直接点击课程标题的购买图标！", Toast.LENGTH_LONG).show();
                        }

                        initBuyButton();
                    }
                });
    }

    private void initView()
    {
        //顶部菜单
        mBackView  = (ImageView)findViewById(R.id.top_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        mTitleView = (TextView)findViewById(R.id.lesson_top_title);
        mTitleView.setText(R.string.lesson_top_title);

        mShareView = (ShareView) findViewById(R.id.share_view);
        mShareView.setShareIntent(getTxtIntent());

        //封面和播放按钮
        mCoverView = (ImageView)findViewById(R.id.lessonPageCover);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mLessonData.getBEIMAGEURL(), mCoverView);

        mPlayButton = (Button)findViewById(R.id.lessonPagePlay);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playLesson();
            }
        });

        initPlayButton();

        //内容标题和购买下载按钮
        mLessonTitleView =  (TextView)findViewById(R.id.lessonPageTitle);
        mLessonTitleView.setText(mLessonData.getBETITLE());

        mBuyButton = (Button)findViewById(R.id.buyButton);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadORBuyLesson();
            }
        });

        initBuyButton();

        //课程详情
        mDescView  = (TextView)findViewById(R.id.lessonpageDesc);

        String desc = mLessonData.getBEDESC();

        if(desc!=null && desc.length()>0)
        {
            mDescView.setText(mLessonData.getBEDESC());
        }

        //关键词标签云
        mTagView =  (TagView)findViewById(R.id.tagview);
        mTagView.addTags(mTagList.toArray(new String[mTagList.size()]));
        mTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result",mTagList.get(position));
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        mIgnoredViews.add(mTagView);

        //评论
        mCommentListView = (ListView)findViewById(R.id.commentList);

        if (mAdapter == null) {
            mAdapter = new FlyingCommentListAdapter(ContentActivity.this, R.id.comment_item_content);
        }
        mCommentListView.setAdapter(mAdapter);
        mCommentListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                                                 @Override
                                                 public void onScrollStateChanged(AbsListView view, int scrollState) {
                                                     if (scrollState == SCROLL_STATE_IDLE && mIsLastRow == true) {
                                                         onLoadMoreItems();
                                                         mIsLastRow = false;
                                                     }

                                                     switch (scrollState) {
                                                         // 当不滚动时
                                                         case SCROLL_STATE_IDLE: {
                                                             break;
                                                         }
                                                     }
                                                 }

                                                 @Override
                                                 public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                                                     //判断是否滚到最后一行
                                                     if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                                                         mIsLastRow = true;
                                                     }
                                                 }
                                             }
        );

        mCommentListView.setOnItemClickListener(new AbsListView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final FlyingCommentData commentData = mData.get(position);

                if(!commentData.userID.equalsIgnoreCase(FlyingDataManager.getCurrentPassport()))
                {

                    FlyingHttpTool.getUserInfoByopenID(commentData.userID, ShareDefine.getLocalAppID(), new FlyingHttpTool.GetUserInfoByopenIDListener() {
                        @Override
                        public void completion(UserInfo userInfo) {

                            if (RongIM.getInstance() != null)
                            {
                                RongIM.getInstance().startPrivateChat(ContentActivity.this, ShareDefine.getMD5(commentData.userID), commentData.nickName);
                            }
                        }
                    });
                }
            }
        });

        onLoadMoreItems();


        //添加评论输入框
        mInputCommnetBox = findViewById(R.id.inputcomment_box);
        mInputCommnetBox.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        mCommnetEditText = (EditText)findViewById(R.id.comment_edit_text);

        mCommnetEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            onCommentClick(v.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });

        mInputImagView = (ImageView)findViewById(R.id.comment_btn);
        mInputImagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCommentClick(mCommnetEditText.getText().toString());
            }
        });

        //二维码
        /*
        mLessonQRImageview = (ImageView)findViewById(R.id.lessonQRImag);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度

        final TextToBitmap qrBitImage =new TextToBitmap(mLessonData.getBEWEBURL(),width,width);
        mLessonQRImageview.setImageBitmap(qrBitImage.getBitmap());
        mLessonQRImageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialogWrapper.Builder(ContentActivity.this)
                        .setTitle("友情提醒")
                        .setMessage(getString(R.string.save_QRCode_ACK))
                        .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == AlertDialog.BUTTON_POSITIVE) {

                                    mLessonQRImageview.buildDrawingCache();
                                    mCoverView.buildDrawingCache();
                                    Bitmap logo = mCoverView.getDrawingCache();
                                    Bitmap output = TextToBitmap.createQRCodeBitmapWithPortrait(qrBitImage.getBitmap(), logo);

                                    ShareDefine.savePhoto(output, mLessonData.getBETITLE());
                                    Toast.makeText(ContentActivity.this, "已经成功保存图片", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        */
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            switch (msg.what)
            {
                case SET_COMMENTLIST:
                {
                    // notify the adapter that we can update now
                    mAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void onLoadMoreItems()
    {
        if (mData.size() <mMaxNumOfComments)
        {
            currentLodingIndex++;
            FlyingHttpTool.getCommentList(mLessonData.getBELESSONID(),
                    mLessonData.getBECONTENTTYPE(),
                    currentLodingIndex,
                    new FlyingHttpTool.GetCommentListListener() {
                        @Override
                        public void completion(ArrayList<FlyingCommentData> commentList, String allRecordCount) {

                            if(commentList!=null && commentList.size()!=0)
                            {
                                for (FlyingCommentData data : commentList) {
                                    mAdapter.add(data);
                                }
                                // stash all the data in our backing store
                                mData.addAll(commentList);

                                handler.obtainMessage(SET_COMMENTLIST).sendToTarget();

                                mMaxNumOfComments = Integer.parseInt(allRecordCount);

                                if(mMaxNumOfComments>0)
                                {
                                    mFooterView = LayoutInflater.from(ContentActivity.this).inflate(R.layout.comment_foot, null);
                                    mCommentListView.addFooterView(mFooterView);
                                }
                            }
                        }
                    });
        }
    }

    private void initBuyButton()
    {
        if(mHasRight)
        {
            if (mLessonData.getLocalURLOfContent()!=null||
                    mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
            {
                mBuyButton.setBackgroundResource(R.drawable.greenbutton);
                mBuyButton.setText("马上欣赏");
                mDownloadStatus = DownloadConstants.STATUS_INSTALL;
            }
            else
            {
                mDownloadStatus = mDownloadDao.getStatusByUrl(mLessonData.getBECONTENTURL());

                if (mDownloadStatus == DownloadConstants.STATUS_DEFAULT)
                {
                    mBuyButton.setText("离线收藏");
                }
                else if (mDownloadStatus== DownloadConstants.STATUS_DOWNLOADING)
                {
                    mBuyButton.setText("下载中...");
                }
                else if (mDownloadStatus== DownloadConstants.STATUS_PAUSE)
                {
                    mBuyButton.setText("暂停状态...");
                }
            }
        }
        else{

            mBuyButton.setText("会员专享");
        }
    }

    private void initPlayButton()
    {
        if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
        {
            mPlayButton.setBackgroundResource(R.drawable.ic_drawer_doc);
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
        {
            mPlayButton.setBackgroundResource(R.drawable.ic_drawer_audio);
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeVideo))
        {
            mPlayButton.setBackgroundResource(R.drawable.ic_drawer_video);
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
        {
            mPlayButton.setBackgroundResource(R.drawable.ic_drawer_web);
        }
    }

    //监听下载状态变化
    public class HttpDownloadReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null
                    && intent.getAction().equals(
                    ShareDefine.getKRECEIVER_ACTION())
                    && intent.getStringExtra(MyIntents.URL).equals(mLessonData.getBECONTENTURL())
                    )
            {
                int type = intent.getIntExtra(MyIntents.TYPE, -1);
                switch (type)
                {
                    case MyIntents.Types.WAIT:
                    {// 下载之前的等待
                        mBuyButton.setText("等待...");
                        mDownloadStatus=DownloadConstants.STATUS_DOWNLOADING;

                        mBuyButton.setClickable(true);
                    }
                    break;
                    case MyIntents.Types.PROCESS:
                    {
                        String progress = intent.getStringExtra(MyIntents.PROCESS_PROGRESS);
                        mBuyButton.setText(progress + "%");

                        mDownloadStatus=DownloadConstants.STATUS_DOWNLOADING;
                        mDownlaodProress =Double.parseDouble(progress)/100.00;

                        updateLessonAndDB();

                        mBuyButton.setClickable(true);
                    }
                    break;
                    case MyIntents.Types.COMPLETE:
                    {
                        mBuyButton.setBackgroundResource(R.drawable.greenbutton);
                        mBuyButton.setText("马上欣赏");

                        mDownloadStatus=DownloadConstants.STATUS_INSTALL;
                        mDownlaodProress =1.00;

                        updateLessonAndDB();
                        mBuyButton.setClickable(true);

                        //自动直接打开
                        playLesson();
                        mBuyButton.setText("...");
                        mLessonData.setBEDLSTATE(false);
                    }
                    break;
                    case MyIntents.Types.ERROR:
                    {
                        Toast.makeText(ContentActivity.this, "下载失败，重新试试吧：）",
                                Toast.LENGTH_SHORT).show();

                        mBuyButton.setBackgroundResource(R.drawable.graybutton);
                        mBuyButton.setText("重新下载");

                        mDownloadStatus=DownloadConstants.STATUS_DEFAULT;

                        updateLessonAndDB();
                        mBuyButton.setClickable(true);
                    }
                    break;
                }
            }
        }
    }

    //监听用户数据状态变化
    public class UserDataChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: React to the Intent received.
            initData();
            initBuyButton();
        }
    }

    private void updateLessonAndDB()
    {
        switch(mDownloadStatus)
        {
            case DownloadConstants.STATUS_DOWNLOADING:
                //正在下载，不做任何事情
                mLessonData.setBEDLPERCENT(mDownlaodProress);
                mLessonData.setBEDLSTATE(true);

                mDao.savelLesson(mLessonData);
                break;

            case DownloadConstants.STATUS_DEFAULT:
                mLessonData.setLocalURLOfContent(null);
                mLessonData.setBEDLPERCENT(0.0);
                mLessonData.setBEDLSTATE(false);

                mDao.savelLesson(mLessonData);
                break;

            case DownloadConstants.STATUS_INSTALL:
                try
                {
                    String path =ShareDefine.getLessonContentPath(mLessonData.getBELESSONID(),mLessonData.getBECONTENTURL());

                    mLessonData.setLocalURLOfContent(path);
                    mLessonData.setBEDLPERCENT(1.0);
                    mLessonData.setBEDLSTATE(false);

                    mDao.savelLesson(mLessonData);

                    BE_TOUCH_RECORD touchData = new FlyingTouchDAO().selectWithUserID(FlyingDataManager.getCurrentPassport(),mLessonData.getBELESSONID());
                    touchData.setBETOUCHTIMES(touchData.getBETOUCHTIMES()+1);
                    new FlyingTouchDAO().savelTouch(touchData);
                }
                catch (Exception e)
                {}

                break;
        }
    }

    private void playLesson()
    {

        if(!mHasRight)
        {
            Toast.makeText(ContentActivity.this, getString(R.string.lesson_right_alert),
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
            {
                openLesson();
            }
            else if (mLessonData.getLocalURLOfContent()!=null)
            {
                openLesson();
            }
            else
            {
                if (mLessonData.getBEDLSTATE() == true) {

                    Toast.makeText(ContentActivity.this, getString(R.string.lesson_playonline_alert),
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    //在线播放,暂时没有区分在线还是下载
                    mPlayonline = true;

                    if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
                    {
                        downloadORBuyLesson();
                    }
                    else
                    {
                        String playURL=mLessonData.getBECONTENTURL();
                        if(!mLessonData.getBEDOWNLOADTYPE().equals(ShareDefine.KDownloadTypeMagnet))
                        {

                            //Uri uri = Uri.parse(playURL);

                            int downloadType = FlyingPlayerActivity.TYPE_OTHER;
                            if ( mLessonData.getBECONTENTTYPE().contentEquals(ShareDefine.KDownloadTypeM3U8))
                            {
                                downloadType= FlyingPlayerActivity.TYPE_HLS;
                            }

                            Intent mpdIntent = new Intent(this, FlyingPlayerActivity.class)
                                    .setData(Uri.parse(playURL))
                                    .putExtra(FlyingPlayerActivity.CONTENT_ID_EXTRA, mLessonData.getBELESSONID())
                                    .putExtra(FlyingPlayerActivity.CONTENT_TYPE_EXTRA, downloadType);

                            startActivity(mpdIntent);
                        }
                    }
                }
            }
        }
    }

    private void downloadORBuyLesson()
    {
        if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
        {
            Intent intent = new Intent(this,WebViewActivity.class);

            intent.putExtra("url", mLessonData.getBECONTENTURL());
            intent.putExtra("title", mLessonData.getBETITLE());
            intent.putExtra("lessonID",mLessonData.getBELESSONID());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else
        {
            mPlayonline=false;

            Boolean activeMembership = MyApplication.getSharedPreference().getBoolean("activeMembership", false);

            if(mHasRight)
            {
                if (mDownloadStatus == DownloadConstants.STATUS_INSTALL)
                {
                    File contenFile = new File(mLessonData.getLocalURLOfContent());
                    if (contenFile.exists())
                    {
                        mBuyButton.setText("尝试打开");
                        mLessonData.setBEDLSTATE(false);

                        //直接打开
                        openLesson();
                        return;
                    }
                    else
                    {
                        Toast.makeText(ContentActivity.this, "下载失败，重新试试吧：）",
                                Toast.LENGTH_SHORT).show();

                        mBuyButton.setClickable(true);
                        mBuyButton.setText("重新下载");

                        mDownloadStatus=DownloadConstants.STATUS_DEFAULT;
                    }
                }
                else if (mDownloadStatus== DownloadConstants.STATUS_DOWNLOADING)
                {
                    //暂停
                    getFlyingDownloadManager().pauseDownloder(mLessonData.getBELESSONID());

                    mBuyButton.setBackgroundResource(R.drawable.graybutton);
                    mBuyButton.setText("暂停状态");

                    mDownloadStatus=DownloadConstants.STATUS_PAUSE;
                }
                else if (mDownloadStatus== DownloadConstants.STATUS_PAUSE)
                {
                    //继续下载
                    getFlyingDownloadManager().continueDownload(mLessonData.getBELESSONID());

                    mBuyButton.setBackgroundResource(R.drawable.graybutton);
                    mBuyButton.setText("下载..");

                    mDownloadStatus=DownloadConstants.STATUS_DOWNLOADING;
                }
                else if (mDownloadStatus == DownloadConstants.STATUS_DEFAULT)
                {
                    mDao.savelLesson(mLessonData);
                    //默认 需要下载
                    getFlyingDownloadManager().startDownloaderForID(mLessonData.getBELESSONID());

                    mBuyButton.setBackgroundResource(R.drawable.graybutton);
                    mBuyButton.setText("准备..");
                    mDownloadStatus=DownloadConstants.STATUS_DEFAULT;
                }

                updateLessonAndDB();

                downloadBackgroundResource();
            }
            else
            {
                inquiryRightWithUserID();
            }
        }
    }

    private void downloadBackgroundResource()
    {
        String url = ShareDefine.getLessonResource(mLessonData.getBELESSONID(),ShareDefine.kResource_Background);

        Ion.with(ContentActivity.this)
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
                            String urlString = result.getResult();

                            if(urlString !=null)
                            {
                                Ion.with(ContentActivity.this)
                                        .load(urlString)
                                        .write(new File(ShareDefine.getLessonContentPathWithFileName(mLessonData.getBELESSONID(),ShareDefine.kResource_Background_filenmae)
                                        ))
                                        .setCallback(new FutureCallback<File>() {
                                            @Override
                                            public void onCompleted(Exception e, File file) {
                                                // download done...
                                                // do stuff with the File or error
                                            }
                                        });

                            }
                        }
                    }
                });
    }

    private void openLesson()
    {
        if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
        {
            if(mLessonData.getLocalURLOfContent().contains("pdf"))
            {
                MainActivity.startPDFActivity(this,mLessonData.getLocalURLOfContent(),mLessonData.getBETITLE(),mLessonData.getBELESSONID(),true);
            }
            else if(mLessonData.getLocalURLOfContent().contains("doc") || mLessonData.getLocalURLOfContent().contains("docx"))
            {
                Intent intent = CommonIntent.getWordFileIntent(mLessonData.getLocalURLOfContent());
                startActivity(intent);
            }
            else if(mLessonData.getLocalURLOfContent().contains("ppt") || mLessonData.getLocalURLOfContent().contains("pptx"))
            {
                Intent intent = CommonIntent.getPptFileIntent(mLessonData.getLocalURLOfContent());
                startActivity(intent);
            }
            else if(mLessonData.getLocalURLOfContent().contains("xls") || mLessonData.getLocalURLOfContent().contains("xlsx"))
            {
                Intent intent = CommonIntent.getExcelFileIntent(mLessonData.getLocalURLOfContent());
                startActivity(intent);
            }
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeVideo)||
                mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
        {
            String playURL=mLessonData.getLocalURLOfContent();
            if(!mLessonData.getBEDOWNLOADTYPE().equals(ShareDefine.KDownloadTypeMagnet))
            {
                int downloadType = FlyingPlayerActivity.TYPE_OTHER;
                if ( mLessonData.getBECONTENTTYPE().contentEquals(ShareDefine.KDownloadTypeM3U8))
                {
                    downloadType= FlyingPlayerActivity.TYPE_HLS;
                }

                Intent mpdIntent = new Intent(this, FlyingPlayerActivity.class)
                        .setData(Uri.parse(playURL))
                        .putExtra(FlyingPlayerActivity.CONTENT_ID_EXTRA, mLessonData.getBELESSONID())
                        .putExtra(FlyingPlayerActivity.CONTENT_TYPE_EXTRA, downloadType);

                startActivity(mpdIntent);
            }
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
        {
            Intent intent = new Intent(this,WebViewActivity.class);

            intent.putExtra("url", mLessonData.getBECONTENTURL());
            intent.putExtra("title", mLessonData.getBETITLE());
            intent.putExtra("lessonID",mLessonData.getBELESSONID());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void onCommentClick(String commentContent)
    {
        if(commentContent!=null && commentContent.length()>0)
        {
            //隐藏键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            FlyingCommentData commentData = new FlyingCommentData();
            commentData.contentID   = mLessonData.getBELESSONID();
            commentData.contentType = mLessonData.getBECONTENTTYPE();
            commentData.userID      = FlyingDataManager.getCurrentPassport();
            commentData.nickName    = FlyingDataManager.getNickName();
            commentData.portraitURL = FlyingDataManager.getPortraitUri();
            commentData.commentContent = commentContent;
            commentData.commentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            final FlyingCommentData  finalCommentData = commentData;
            FlyingHttpTool.updateComment(finalCommentData,
                    ShareDefine.getLocalAppID(),
                    new FlyingHttpTool.UpdateCommentListener() {
                        @Override
                        public void completion(boolean isOK) {

                            if(isOK)
                            {
                                //清空输入框
                                mCommnetEditText.setText("");

                                mAdapter.insert(finalCommentData,0);
                                mData.add(0,finalCommentData);

                                handler.obtainMessage(SET_COMMENTLIST).sendToTarget();

                                mMaxNumOfComments = mMaxNumOfComments+1;

                                if(mFooterView == null)
                                {
                                    mFooterView = LayoutInflater.from(ContentActivity.this).inflate(R.layout.comment_foot, null);
                                    mCommentListView.addFooterView(mFooterView);
                                }

                            }
                        }
                    });
        }
    }

    private void alertBuyMember()
    {
        Product good =new Product("年费会员",ShareDefine.KPricePerYear,1);

        FlyingHttpTool.toBuyProduct(ContentActivity.this,
                FlyingDataManager.getCurrentPassport(),
                ShareDefine.getLocalAppID(),
                good);
    }

    private void alertBuyAction()
    {
        new MaterialDialog.Builder(this)
                .title("购买确认")
                .items(R.array.chooseCameralOrAlbum)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/

                        if (which == 0) {
                            buyLessonWithCoin();
                        }
                        return true;
                    }
                })
                .backgroundColorRes(R.color.background_material_light)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .show();
    }

    private void  buyLessonWithCoin()
    {
        BE_STATISTIC statistic = new  FlyingStatisticDAO().selectWithUserID(FlyingDataManager.getCurrentPassport());

        int balance =statistic.getBEQRCOUNT()+statistic.getBEMONEYCOUNT()+statistic.getBEGIFTCOUNT()-statistic.getBETOUCHCOUNT();

        if((balance-mLessonData.getBECoinPrice())>=0)
        {
            //更新点击次数和单词纪录
            statistic.setBETOUCHCOUNT(statistic.getBETOUCHCOUNT()+mLessonData.getBECoinPrice());
            new FlyingStatisticDAO().saveStatic(statistic);

            BE_TOUCH_RECORD touchData = new FlyingTouchDAO().selectWithUserID(FlyingDataManager.getCurrentPassport(),mLessonData.getBELESSONID());

            if(touchData==null)
            {
                touchData = new BE_TOUCH_RECORD();
                touchData.setBETOUCHTIMES(new Integer(1));
                touchData.setBEUSERID(FlyingDataManager.getCurrentPassport());
                touchData.setBELESSONID(mLessonData.getBELESSONID());
            }
            else
            {
                touchData.setBETOUCHTIMES(touchData.getBETOUCHTIMES()+1);
            }

            new FlyingTouchDAO().savelTouch(touchData);

            //向服务器备份消费数据
            FlyingHttpTool.uploadContentStatistic(FlyingDataManager.getCurrentPassport(),
                    ShareDefine.getLocalAppID(),null);

            mHasRight=true;
            initBuyButton();
            downloadORBuyLesson();
            MyApplication.getInstance().playCoinSound();

            //广播通知用户数据状态变化,比如账户界面可以及时更新
            ShareDefine.broadUserDataChange();
        }
        else
        {
            Toast.makeText(ContentActivity.this, "没有金币了，需要充值了：）",
                    Toast.LENGTH_LONG).show();
        }
    }

    private FlyingDownloadManager getFlyingDownloadManager()
    {
        return  MyApplication.getLeesonDownloadManager();
    }

    private void  shareCurrentContent()
    {
        String title = "来自"+getString(R.string.app_name)+"的精彩分享";
        String desc  = "我也有自己的App了：）";
        String urlStr = "wwww.birdcopy.com/vip/"+ ShareDefine.getLessonOwner();


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        if(mLessonData!=null)
        {
            title = mLessonData.getBETITLE();
            urlStr = mLessonData.getBEWEBURL();
        }

        shareIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + desc + "\n" + urlStr);

        startActivity(Intent.createChooser(shareIntent, "分享精彩"));

        MainActivity.awardCoin();
    }

    private Intent[] getTxtIntent()
    {
        String subject = "来自"+getString(R.string.app_name)+"的精彩分享";
        String body  = "就差你没有来了：）";

        if(mLessonData!=null)
        {
            subject = mLessonData.getBETITLE();
            body = mLessonData.getBEDESC()+mLessonData.getBEWEBURL();
        }

        Intent shareIntent=new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        List<Intent> targetShareIntents=new ArrayList<Intent>();

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("sms_body",subject+":"+body);
        targetShareIntents.add(smsIntent);


        List<ResolveInfo> resInfos=getPackageManager().queryIntentActivities(shareIntent, 0);
        if(!resInfos.isEmpty())
        {
            for(ResolveInfo resInfo : resInfos){
                String packageName=resInfo.activityInfo.packageName;
                if(packageName.contains("com.tencent.mobileqq") || packageName.contains("com.tencent.mm")
                        ||packageName.contains("com.sina.weibo")||packageName.contains("com.android.email")){
                    Intent intent=new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if(!targetShareIntents.isEmpty()){

                return targetShareIntents.toArray(new Intent[targetShareIntents.size()]);
            }
        }

        return null;
    }

    /*
    private void scanNow()
    {
        Intent intent = new Intent(this,ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ShareDefine.SCANNIN_REQUEST_CODE);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode)
        {
            case ShareDefine.SCANNIN_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();

                    String scanStr = bundle.getString("result");
                    String type = ShareDefine.judgeScanType(scanStr);

                    if (type.equals(ShareDefine.KQRTyepeWebURL)) {
                        String lessonID = ShareDefine.getLessonIDFromOfficalURL(scanStr);
                        if (lessonID != null)
                        {
                            showLessonViewWithID(lessonID);
                        }
                        else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(scanStr));
                            startActivity(intent);
                        }
                    }
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

    Handler myHandler = new Handler()
    {
        // 接收到消息后处理
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case HTTP_RESPONSE:
                    MyTask dTask = new MyTask();
                    dTask.execute(mResponseStr);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void showLessonViewWithID(String lessonID)
    {

        String url = ShareDefine.getLessonDataByID(lessonID);
        try
        {
            AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url), new AsyncHttpClient.StringCallback() {

                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s)
                {
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
        }
        catch (Exception e)
        {
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

                return myXMLHandler.entries;
            }
            catch (Exception e)
            {

                System.out.println("XML Pasing Excpetion = " + e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<BE_PUB_LESSON> result)
        {
            super.onPostExecute(result);

            BE_PUB_LESSON lessonData =result.get(0);

            if(lessonData!=null)
            {
                mLessonData=lessonData;

                initData();
                initView();
            }
        }
    }

    private void initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(ContentActivity.this,
                    new BackGestureListener(this));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if(mNeedBackGesture && !isInIgnoredView(ev))
        {
            return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 设置是否进行手势监听
     */
    public void setNeedBackGesture(boolean mNeedBackGesture){
        this.mNeedBackGesture = mNeedBackGesture;
        initGestureDetector();
    }

    /*
     * 返回
     */
    public void doBack(View view)
    {
        onBackPressed();
    }

    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : mIgnoredViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        BE_PUB_LESSON tempData = mDao.selectWithLessonID(mLessonData.getBELESSONID());

        if (tempData!=null)
        {
            mLessonData=tempData;
        }

        initBuyButton();
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
        unregisterReceiver(mDownloadReceiver);
        unregisterReceiver(mUserDataReceiver);
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
