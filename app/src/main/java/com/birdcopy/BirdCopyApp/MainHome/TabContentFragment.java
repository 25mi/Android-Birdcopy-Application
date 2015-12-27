package com.birdcopy.BirdCopyApp.MainHome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.birdcopy.BirdCopyApp.ChannelManage.AlbumData;
import com.birdcopy.BirdCopyApp.ChannelManage.AlbumDataModle;
import com.birdcopy.BirdCopyApp.ChannelManage.ChannelItem;
import com.birdcopy.BirdCopyApp.Component.Adapter.HomeFragmentPagerAdapter;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.UI.ColumnHorizontalScrollView;
import com.birdcopy.BirdCopyApp.ContentList.LessonListFragment;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Component.Tools.BaseTools;

import java.util.ArrayList;

/**
 * Created by BirdCopyApp on 22/7/14.
 */
public class TabContentFragment extends Fragment implements AlbumDataModle.DealResult
{
    public final static int SET_TAGLIST = 0;

    private View tablConent;

    //记录菜单位置
    private int mDrawMenuPosition=MainActivity.homeMenuPostion;

    private AlbumDataModle albumDataModle;
    /** 用户选择的新闻分类列表*/
    private ArrayList<ChannelItem> mUserChannelList=null;

    /** 左阴影部分*/
    private ImageView mShadeleft;
    /** 右阴影部分 */
    private ImageView mShaderight;

    /** 当前选中的栏目*/
    private int columnSelectIndex = 0;
    
    /** 自定义HorizontalScrollView */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    RelativeLayout rl_column;
    private ImageView button_more_columns;
    LinearLayout ll_more_columns;

    private ViewPager mViewPager;

    /** 屏幕宽度 */
    private int mScreenWidth = 0;
    /** Item宽度 */
    //private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mScreenWidth = BaseTools.getWindowsWidth(getActivity());
        //mItemWidth = mScreenWidth /4;// 一个Item宽度为屏幕的1/4
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        tablConent = (View)inflater.inflate(R.layout.tabcontent, null);

        mShadeleft = (ImageView) tablConent.findViewById(R.id.shade_left);
        mShaderight = (ImageView) tablConent.findViewById(R.id.shade_right);

        mColumnHorizontalScrollView =  (ColumnHorizontalScrollView)tablConent.findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) tablConent.findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) tablConent.findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) tablConent.findViewById(R.id.rl_column);
        button_more_columns = (ImageView) tablConent.findViewById(R.id.button_more_columns);

        button_more_columns.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                MainActivity mainActivity=(MainActivity)getActivity();
                mainActivity.showMoreColumn();
            }
        });

        mViewPager = (ViewPager) tablConent.findViewById(R.id.mTableContentViewPager);

        return tablConent;
    }
    
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            switch (msg.what) 
            {
                case SET_TAGLIST:
                {
                    // notify the adapter that we can update now
                    initAndShowView();
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /** 此方法意思为fragment是否可见 ,可见时候加载数据 */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser)
        {
            //fragment可见时加载数据
            if(mShadeleft !=null && mUserChannelList.size() !=0)
            {
                handler.obtainMessage(SET_TAGLIST).sendToTarget();
            }
            else
            {
                if (ShareDefine.checkNetWorkStatus()==true)
                {
                    loadChannelData(mDrawMenuPosition);
                }
                else
                {
                    loadChannelData(mDrawMenuPosition);
                }
            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    public void  setDrawMenuPosition(int drawMenuPosition)
    {
        if(mDrawMenuPosition!=drawMenuPosition)
        {
            mDrawMenuPosition=drawMenuPosition;

            if(mUserChannelList!=null)
            {
                mUserChannelList.clear();
            }

            if (ShareDefine.checkNetWorkStatus()==true)
            {
                loadChannelData(drawMenuPosition);
            }
            else
            {
                loadChannelData(drawMenuPosition);
            }
        }
    }

    /** 获取Column栏目 数据*/
    public void loadChannelData(int drawMenuPosition)
    {
        mDrawMenuPosition= drawMenuPosition;
        
        String contentType=null;

        /*
        switch (drawMenuPosition)
        {
            case MainActivity.docMenuPostion:
            {
                contentType=ShareDefine.KContentTypeText;
                break;
            }
            case MainActivity.audioMenuPostion:
            {
                contentType=ShareDefine.KContentTypeAudio;
                break;
            }
            case MainActivity.vedioMenuPostion:
            {
                contentType=ShareDefine.KContentTypeVideo;
                break;
            }
        }
        */

        if (albumDataModle ==null)
        {
            albumDataModle = new AlbumDataModle();
            albumDataModle.setDelegate(this);
        }

        albumDataModle.loadChannelAlbumListData(contentType);
    }

    public void parseALbumDataOK(ArrayList<AlbumData> list)
    {
        if(list!=null && list.size()!=0)
        {
            ArrayList<ChannelItem> channelList = new ArrayList<ChannelItem>();

            for (int i=0;i<list.size();i++)
            {
                ChannelItem navigate = new ChannelItem();
                navigate.setId(i);
                navigate.setName(list.get(i).getTagString());
                navigate.setOrderId(i);
                navigate.setSelected(0);
                channelList.add(navigate);
            }

            mUserChannelList=channelList;
        }
        else
        {
            mUserChannelList=null;
        }

        handler.obtainMessage(SET_TAGLIST).sendToTarget();
    }

    public void setMaxAlbums(int itemCount)
    {}

    /**
     *  当栏目项发生变化时候调用
     * */
    public void initAndShowView()
    {
        initTabColumn();
        initFragment();
    }

    /**
     *  初始化Column栏目项
     * */
    private void initTabColumn()
    {
        mRadioGroup_content.removeAllViews();

        if(mUserChannelList!=null)
        {
            int count =  mUserChannelList.size();
            mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth, mRadioGroup_content, mShadeleft, mShaderight, ll_more_columns, rl_column);
            for(int i = 0; i< count; i++)
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                params.rightMargin = 5;
//			TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
                TextView columnTextView = new TextView(getActivity());
                columnTextView.setTextAppearance(getActivity(), R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
                columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
                columnTextView.setGravity(Gravity.CENTER);
                columnTextView.setPadding(5, 5, 5, 5);
                columnTextView.setId(i);
                columnTextView.setText(mUserChannelList.get(i).getName());
                columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
                if(columnSelectIndex == i){
                    columnTextView.setSelected(true);
                }
                columnTextView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
                            View localView = mRadioGroup_content.getChildAt(i);
                            if (localView != v)
                                localView.setSelected(false);
                            else{
                                localView.setSelected(true);
                                mViewPager.setCurrentItem(i);
                            }
                        }
                        Toast.makeText(getActivity().getApplicationContext(), mUserChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                mRadioGroup_content.addView(columnTextView, i ,params);
            }
        }
    }
    /**
     *  选择的Column里面的Tab
     * */
    private void selectTab(int tab_postion)
    {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }

        //判断是否选中
        for (int j = 0; j <  mRadioGroup_content.getChildCount(); j++)
        {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            }
            else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }
    /**
     *  初始化Fragment
     * */
    private void initFragment()
    {
        fragments.clear();//清空

        if(mUserChannelList!=null)
        {
            int count =  mUserChannelList.size();
            for(int i = 0; i< count;i++)
            {
                LessonListFragment lessonListFragment =new LessonListFragment();
                /*
                if(mDrawMenuPosition==MainActivity.docMenuPostion)
                {
                    lessonListFragment.setContentType(ShareDefine.KContentTypeText);
                }
                else if(mDrawMenuPosition==MainActivity.audioMenuPostion)
                {
                    lessonListFragment.setContentType(ShareDefine.KContentTypeAudio);
                }
                else if(mDrawMenuPosition==MainActivity.vedioMenuPostion)
                {
                    lessonListFragment.setContentType(ShareDefine.KContentTypeVideo);
                }
                */

                Bundle data = new Bundle();
                String tagName = mUserChannelList.get(i).getName();
                data.putString("text",tagName);
                data.putInt("id", mUserChannelList.get(i).getId());
                lessonListFragment.setArguments(data);
                lessonListFragment.setTagString(tagName);
                //lessonListFragment.tabContentFragment=this;

                fragments.add(lessonListFragment);
            }
            HomeFragmentPagerAdapter mAdapetr = new HomeFragmentPagerAdapter(getChildFragmentManager(), fragments);
            //		mViewPager.setOffscreenPageLimit(0);
            mViewPager.setAdapter(mAdapetr);
            mViewPager.setOnPageChangeListener(pageListener);
            mViewPager.setCurrentItem(columnSelectIndex);
        }
    }
    /**
     *  ViewPager切换监听方法
     * */
    public ViewPager.OnPageChangeListener pageListener= new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
           // columnSelectIndex=arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
            //columnSelectIndex=arg0;
        }

        @Override
        public void onPageSelected(int position)
        {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
            selectTab(position);

            if(position==0)
            {
                MainActivity mainActivity=(MainActivity)getActivity();
                mainActivity.resetMenuGesture();
            }
            else
            {
                MainActivity mainActivity=(MainActivity)getActivity();
                mainActivity.cancelMenuGesture();
            }
        }
    };
}
