package com.birdcopy.BirdCopyApp.Lesson;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.*;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.birdcopy.BirdCopyApp.Buy.Product;
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
import com.birdcopy.BirdCopyApp.Component.UI.ColumnHorizontalScrollView;
import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingSysWithCenter;
import com.birdcopy.BirdCopyApp.Component.UserManger.SSKeychain;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;
import com.birdcopy.BirdCopyApp.LessonList.LessonParser;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Scan.TextToBitmap;
import com.artifex.mupdfdemo.AsyncTask;
import com.birdcopy.BirdCopyApp.Media.VideoPlayActivity;
import com.dgmltn.shareeverywhere.ShareView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class

        LessonActivity extends FragmentActivity
{
    public static final String SAVED_DATA_KEY   = "SAVED_DATA_KEY";
    public final static int HTTP_RESPONSE = 0;
    private  String mResponseStr=null;
    public static final Uri   DOWNLOADCONTENTOBSERURI= Uri.parse("content://downloads/my_downloads");

    public BE_PUB_LESSON mLessonData;

    private ImageView mBackView;
    private TextView  mTitleView;
    private ImageView mChatView;
    private ShareView mShareView;

    private ImageView mCoverView;
    private TextView  mLessonTitleView;
    private Button    mBuyButton;
    private Button    mPlayButton;
    private TextView  mDescView;
    private ImageView mLessonQRImageview;

    private SysMemberShipBroadReciever memberShipBroadReciever;

    //下载用
    private boolean mHasRight=false;
    private boolean mPlayonline=false;
    private boolean mHasHistoryRecord=false;
    private boolean mHasCheckedHistoryRecord=false;

    private FlyingLessonDAO mDao;

    public  int mDownloadStatus = DownloadConstants.STATUS_DEFAULT;
    public  double mDownlaodProress = 0;

    private DownloadDao mDownloadDao;

    private HttpDownloadReceiver mReceiver;

    /** 自定义HorizontalScrollView */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    private ArrayList<String> mTagList=null;
    /** 屏幕宽度 */
    private int mScreenWidth = 0;
    /** 左阴影部分*/
    private ImageView mShadeleft;
    /** 右阴影部分 */
    private ImageView mShaderight;
    RelativeLayout rl_column;
    LinearLayout ll_more_columns;

    /** 手势监听 */
    GestureDetector mGestureDetector;
    /** 是否需要监听手势关闭功能 */
    private boolean mNeedBackGesture = false;
    private List<View> mIgnoredViews= new ArrayList<View>();

    public LessonActivity()
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
    }

    private void initView()
    {
        //顶部菜单
        mBackView  = (ImageView)findViewById(R.id.top_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        mTitleView = (TextView)findViewById(R.id.lesson_top_title);
        mTitleView.setText(R.string.lesson_top_title);

        mChatView  = (ImageView)findViewById(R.id.top_chat);
        mChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chatNow();
            }
        });

        mShareView = (ShareView) findViewById(R.id.share_view);
        mShareView.setShareIntent(getTxtIntent());

        //封面和播放按钮
        mCoverView = (ImageView)findViewById(R.id.lessonPageCover);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mLessonData.getBEIMAGEURL(),mCoverView);

        mPlayButton = (Button)findViewById(R.id.lessonPagePlay);
        mPlayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playLesson();
            }
        });

        initPlayButton();

        //标题和购买下载按钮
        mLessonTitleView =  (TextView)findViewById(R.id.lessonPageTitle);
        mLessonTitleView.setText(mLessonData.getBETITLE());

        mBuyButton = (Button)findViewById(R.id.buyButton);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOrBuyLesson();
            }
        });

        if (mLessonData.getBEDOWNLOADTYPE().equals(ShareDefine.KDownloadTypeNormal))
        {
            mReceiver = new HttpDownloadReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ShareDefine.getKRECEIVER_ACTION());
            registerReceiver(mReceiver, filter);
        }

        initBuyButton();

        //关键词标签云
        mColumnHorizontalScrollView =  (ColumnHorizontalScrollView)findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        mShadeleft = (ImageView) findViewById(R.id.shade_left);
        mShaderight = (ImageView)findViewById(R.id.shade_right);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);

        initTagColumn();
        mIgnoredViews.add(mColumnHorizontalScrollView);

        //课程详情
        mDescView  = (TextView)findViewById(R.id.lessonpageDesc);
        mDescView.setText(mLessonData.getBEDESC());

        //二维码
        mLessonQRImageview = (ImageView)findViewById(R.id.lessonQRImag);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度

        final TextToBitmap qrBitImage =new TextToBitmap(mLessonData.getBEWEBURL(),width,width);
        mLessonQRImageview.setImageBitmap(qrBitImage.getBitmap());
        mLessonQRImageview.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {

                new AlertDialogWrapper.Builder(LessonActivity.this)
                        .setTitle("友情提醒")
                        .setMessage(getString(R.string.save_QRCode_ACK))
                        .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == AlertDialog.BUTTON_POSITIVE) {

                                    mLessonQRImageview.buildDrawingCache();
                                    mCoverView.buildDrawingCache();
                                    Bitmap logo=mCoverView.getDrawingCache();
                                    Bitmap output =TextToBitmap.createQRCodeBitmapWithPortrait(qrBitImage.getBitmap(),logo);

                                    ShareDefine.savePhoto(output,mLessonData.getBETITLE());
                                    Toast.makeText(LessonActivity.this, "已经成功保存图片", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initData()
    {
        mHasRight=false;

        if(mLessonData.getBECoinPrice()==0) mHasRight=true;

        if(!mHasRight){

            BE_TOUCH_RECORD touchData =new FlyingTouchDAO().selectWithUserID(SSKeychain.getPassport(),mLessonData.getBELESSONID());

            if ( (touchData!=null) && (touchData.getBETOUCHTIMES()!=0) )
            {
                mHasRight=true;
            }
        }

        mPlayonline=false;

        //解析Tag云
        mTagList =new ArrayList<String>(Arrays.asList(mLessonData.getBETAG().split(" ")));

        //获取年费会员数据
        Boolean sysMembership = MyApplication.getSharedPreference().getBoolean("sysMembership", false);

        if(!sysMembership)
        {
            memberShipBroadReciever = new SysMemberShipBroadReciever();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ShareDefine.getKMembershipRECEIVER_ACTION());
            MyApplication.getInstance().registerReceiver(memberShipBroadReciever, filter);

            FlyingSysWithCenter.sysMembershipWithCenter();
        }
    }

    private void initBuyButton()
    {
        //校验是否有内容权限
        if(!mHasRight)
        {

            String text =mLessonData.getBECoinPrice()+"个金币";

            if(ShareDefine.getpakagename().equalsIgnoreCase("it.birdcopy.inet"))
            {
                text ="会员专享";
            }

            mBuyButton.setText(text);

            inquiryRightWithUserID();
        }
        else{

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

        mLessonData.setBEDLSTATE(false);
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


    /**
     *  初始化标签栏目项
     * */
    private void initTagColumn()
    {
        mRadioGroup_content.removeAllViews();

        if(mTagList!=null)
        {
            int count =  mTagList.size();
            mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, mShadeleft, mShaderight, ll_more_columns, rl_column);
            for(int i = 0; i< count; i++)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                params.rightMargin = 5;
//			TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
                final TextView columnTextView = new TextView(this);
                columnTextView.setTextAppearance(this, R.style.tag_text_style);
                columnTextView.setBackgroundColor(getResources().getColor(R.color.red));
                columnTextView.setGravity(Gravity.CENTER);
                columnTextView.setBackgroundResource(R.drawable.tag_textview);
                columnTextView.setId(i);
                columnTextView.setText(mTagList.get(i));
                columnTextView.setTextColor(getResources().getColorStateList(R.color.white));
                columnTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("result",columnTextView.getText().toString());
                        resultIntent.putExtras(bundle);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
                mRadioGroup_content.addView(columnTextView, i ,params);
            }
        }
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

        initPlayButton();
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
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable(SAVED_DATA_KEY, mLessonData);
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

                    BE_TOUCH_RECORD touchData = new FlyingTouchDAO().selectWithUserID(SSKeychain.getPassport(),mLessonData.getBELESSONID());
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
            Toast.makeText(LessonActivity.this, getString(R.string.lesson_right_alert),
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

                    Toast.makeText(LessonActivity.this, getString(R.string.lesson_playonline_alert),
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    //在线播放,暂时没有区分在线还是下载
                    mPlayonline = true;

                    if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
                    {
                        downloadOrBuyLesson();
                    }
                    else
                    {
                        String playURL=mLessonData.getBECONTENTURL();
                        if(!mLessonData.getBEDOWNLOADTYPE().equals(ShareDefine.KDownloadTypeMagnet))
                        {

                            Uri uri = Uri.parse(playURL);

                            Intent intent = new Intent(this, VideoPlayActivity.class);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(uri);

                            if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
                            {
                                intent.putExtra(ShareDefine.KIntenCorParameter,true);
                            }

                            startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    private void downloadOrBuyLesson()
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

            if(mHasRight || mHasHistoryRecord || activeMembership)
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
                        Toast.makeText(LessonActivity.this, "下载失败，重新试试吧：）",
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
                Boolean sysMembership = MyApplication.getSharedPreference().getBoolean("sysMembership", false);

                if(!sysMembership)
                {
                    FlyingSysWithCenter.sysMembershipWithCenter();
                    mBuyButton.setText("同步会员信息");
                }
                else
                {
                    if (mHasCheckedHistoryRecord &&  !activeMembership)
                    {
                        if(ShareDefine.getpakagename().equalsIgnoreCase("it.birdcopy.inet"))
                        {
                            alertBuyMember();
                        }
                        else
                        {
                            alertBuyAction();
                        }
                    }
                    else
                    {
                        inquiryRightWithUserID();
                    }
                }
            }
        }
    }

    private void downloadBackgroundResource()
    {
        String url = ShareDefine.getLessonResource(mLessonData.getBELESSONID(),ShareDefine.kResource_Background);

        Ion.with(LessonActivity.this)
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
                                Ion.with(LessonActivity.this)
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
                Intent intent = new Intent(this, com.birdcopy.BirdCopyApp.Media.VideoPlayActivity.class);
                intent.setAction(Intent.ACTION_VIEW);

                Uri  uri = Uri.parse(playURL);
                intent.setData(uri);

                if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
                {
                    intent.putExtra(ShareDefine.KIntenCorParameter,true);
                }

                startActivity(intent);
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

    private Boolean inquiryRightWithUserID()
    {

        Boolean sysMembership = MyApplication.getSharedPreference().getBoolean("sysMembership", false);

        if(!sysMembership)
        {
            FlyingSysWithCenter.sysMembershipWithCenter();
        }

        BE_TOUCH_RECORD touchData =new FlyingTouchDAO().selectWithUserID(SSKeychain.getPassport(),mLessonData.getBELESSONID());

        if ( (touchData!=null) && (touchData.getBETOUCHTIMES()!=0) )
        {
            mHasRight=true;
            initBuyButton();
            return true;
        }

        //向服务器获取相关数据
        String url = ShareDefine.getTouchDataForUserID(SSKeychain.getPassport(),mLessonData.getBELESSONID());
        Ion.with(LessonActivity.this)
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            mHasCheckedHistoryRecord=true;
                            BE_TOUCH_RECORD touchData = new BE_TOUCH_RECORD();
                            touchData.setBETOUCHTIMES(Integer.parseInt(resultStr));
                            touchData.setBEUSERID(SSKeychain.getPassport());
                            touchData.setBELESSONID(mLessonData.getBELESSONID());
                            new FlyingTouchDAO().savelTouch(touchData);

                            if (touchData.getBETOUCHTIMES()>0)
                            {
                                mHasHistoryRecord=true;
                                mHasRight=true;
                                initBuyButton();
                            }
                            else
                            {
                                mHasHistoryRecord=false;
                            }
                        }
                    }
                });

        return false;
    }

    private void alertBuyMember()
    {
        Product good =new Product("年费会员",ShareDefine.KPricePerYear,1);

        MainActivity.toBuyProduct(this,good);
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
        BE_STATISTIC statistic = new  FlyingStatisticDAO().selectWithUserID(SSKeychain.getPassport());

        int balance =statistic.getBEQRCOUNT()+statistic.getBEMONEYCOUNT()+statistic.getBEGIFTCOUNT()-statistic.getBETOUCHCOUNT();

        if((balance-mLessonData.getBECoinPrice())>=0)
        {
            //更新点击次数和单词纪录
            statistic.setBETOUCHCOUNT(statistic.getBETOUCHCOUNT()+mLessonData.getBECoinPrice());
            new FlyingStatisticDAO().saveStatic(statistic);

            BE_TOUCH_RECORD touchData = new FlyingTouchDAO().selectWithUserID(SSKeychain.getPassport(),mLessonData.getBELESSONID());

            if(touchData==null)
            {
                touchData = new BE_TOUCH_RECORD();
                touchData.setBETOUCHTIMES(new Integer(1));
                touchData.setBEUSERID(SSKeychain.getPassport());
                touchData.setBELESSONID(mLessonData.getBELESSONID());
            }
            else
            {
                touchData.setBETOUCHTIMES(touchData.getBETOUCHTIMES()+1);
            }

            new FlyingTouchDAO().savelTouch(touchData);

            //向服务器备份消费数据
            FlyingSysWithCenter.uploadContentStatData();

            mHasRight=true;
            initBuyButton();
            downloadOrBuyLesson();
            MyApplication.getInstance().playCoinSound();

            //广播通知用户数据状态变化,比如账户界面可以及时更新
            ShareDefine.broadUserDataChange();
        }
        else
        {
            Toast.makeText(LessonActivity.this, "没有金币了，需要充值了：）",
                    Toast.LENGTH_LONG).show();
        }
    }

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
                        Toast.makeText(LessonActivity.this, "下载失败，重新试试吧：）",
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, title +"\n"+desc+"\n"+urlStr);

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
            body = mLessonData.getBEDESC();
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

    private void chatNow()
    {
        inquiryRightWithUserID();

        //校验是否有内容权限
        if(!mHasRight)
        {
            Toast.makeText(LessonActivity.this, "收费内容，需要先购买！", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (RongIM.getInstance() != null)
            {
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.CHATROOM, mLessonData.getBELESSONID(), mLessonData.getBETITLE());

                /*
                Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation").appendPath(io.rong.imlib.model.Conversation.ConversationType.CHATROOM.getName().toLowerCase())
                        .appendQueryParameter("targetId", mLessonData.getBELESSONID()).appendQueryParameter("title", mLessonData.getBETITLE()).build();

                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                */
            }
        }
    }

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
            mGestureDetector = new GestureDetector(LessonActivity.this,
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

    public class SysMemberShipBroadReciever extends BroadcastReceiver {

        //如果接收的事件发生
        @Override
        public void onReceive(Context context, Intent intent) {

            Boolean activeMembership = MyApplication.getSharedPreference().getBoolean("activeMembership", false);

            if(activeMembership)
            {
                mHasRight=true;
                initBuyButton();
            }
        }
    }
}
