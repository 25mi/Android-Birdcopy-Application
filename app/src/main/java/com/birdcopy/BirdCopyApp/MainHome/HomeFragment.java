package com.birdcopy.BirdCopyApp.MainHome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.birdcopy.BirdCopyApp.ChannelManage.AlbumData;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.Adapter.HomeFragmentPagerAdapter;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.UI.DynamicHeightViewPager;
import com.birdcopy.BirdCopyApp.Component.UI.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

import com.birdcopy.BirdCopyApp.R;


/**
 * Created by birdcopy on 23/9/14.
 */
public class HomeFragment extends Fragment
{
    private View mHomeContent;

    private DynamicHeightViewPager mViewPager;

    private LinearLayout mCoverIndex;
    HomeFragmentPagerAdapter mCoverLessonAdapetr;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ArrayList<BE_PUB_LESSON> mCoverLessonList=null;
    private TextView mCoverTitle;
    private List<View> dots;

    public final static int SET_TAG_LIST = 0;
    private StaggeredGridView mGridView;
    private AlbumListAdapter mAdapter;

    private ArrayList<AlbumData> mAlbumLisData= new ArrayList<AlbumData>();

    int     mMaxNumOfTags= ShareDefine.MAX_INT;
    int     currentLodingIndex=0;

    Boolean mIsLastRow=false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        mHomeContent = (View)inflater.inflate(R.layout.homecontent, null);

        mViewPager = (DynamicHeightViewPager) mHomeContent.findViewById(R.id.wrapContentHeightViewPager);

        mCoverIndex = (LinearLayout)mHomeContent.findViewById(R.id.cover_index);
        mCoverTitle= (TextView) mHomeContent.findViewById(R.id.cover_title);

        /*
        dots.add(mHomeContent.findViewById(R.id.v_dot0));
        dots.add(mHomeContent.findViewById(R.id.v_dot1));
        dots.add(mHomeContent.findViewById(R.id.v_dot2));
        dots.add(mHomeContent.findViewById(R.id.v_dot3));
        dots.add(mHomeContent.findViewById(R.id.v_dot4));
        dots.add(mHomeContent.findViewById(R.id.v_dot5));
        */

        mGridView= (StaggeredGridView) mHomeContent.findViewById(R.id.cover_grid_view);

        if (mAdapter == null) {
            mAdapter = new AlbumListAdapter(getActivity(), R.id.album_grid_content);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener( new AbsListView.OnScrollListener(){

                                           @Override
                                           public void onScrollStateChanged(AbsListView view, int scrollState)
                                           {
                                               if(scrollState==SCROLL_STATE_IDLE && mIsLastRow==true)
                                               {
                                                   onLoadMoreAlbums();
                                                   mIsLastRow=false;
                                               }

                                               switch (scrollState) {
                                                   // 当不滚动时
                                                   case SCROLL_STATE_IDLE:
                                                   {
                                                       break;
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                                           {

                                               //判断是否滚到最后一行
                                               if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                                                   mIsLastRow = true;
                                               }
                                           }
                                       }
        );

        mGridView.setOnItemClickListener(new AbsListView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                AlbumData albumData =  mAlbumLisData.get(position);

                if (albumData.getTagString()!=null){

                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.showSearchContent(albumData.getTagString());
                }

                //Toast.makeText(getActivity(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        onLoadMorCoverLesson();
        onLoadMoreAlbums();

        return mHomeContent;
    }

    private void onLoadMorCoverLesson()
    {

        FlyingHttpTool.getCoverList(FlyingDataManager.getLessonOwner(), 1, new FlyingHttpTool.GetCoverListListener() {
            @Override
            public void completion(final ArrayList<BE_PUB_LESSON> lessonList, String allRecordCount) {

               getActivity().runOnUiThread(new Runnable() {
	               @Override
	               public void run() {

		               if (lessonList != null && lessonList.size() != 0) {
			               mCoverLessonList = lessonList;
		               } else {
			               mCoverLessonList = null;
			               mViewPager.setVisibility(View.GONE);
			               mCoverIndex.setVisibility(View.GONE);
		               }

		               initCover();
	               }
               });
            }
        });
    }

    /**
     *  初始化Fragment
     * */
    private void initCover()
    {
        fragments.clear();//清空

        if(mCoverLessonList!=null)
        {
            int count =  mCoverLessonList.size();
            for(int i = 0; i< count;i++)
            {
                CoverFragment coverFragment =new CoverFragment();
                coverFragment.setLessonData(mCoverLessonList.get(i));
                fragments.add(coverFragment);
            }

            mCoverLessonAdapetr = new HomeFragmentPagerAdapter(getChildFragmentManager(), fragments);
            //		mViewPager.setOffscreenPageLimit(0);
            mViewPager.setAdapter(mCoverLessonAdapetr);
            mViewPager.setOnPageChangeListener(pageListener);
        }

        if(mCoverLessonList!=null)
        {
            mCoverTitle.setText(mCoverLessonList.get(0).getBETITLE());
        }

        initCirclePoint();
    }

    /**
     *  ViewPager切换监听方法
     * */
    public ViewPager.OnPageChangeListener pageListener= new ViewPager.OnPageChangeListener()
    {
        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0)
        {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
            if(arg0==5 && arg1>0.5)
            {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.showUpdateFeatureContent();
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            int currentPosition=position % 6;

            mCoverTitle.setText(mCoverLessonList.get(currentPosition).getBETITLE());

            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(currentPosition).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = currentPosition;
            
            if(currentPosition==0)
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

    private void onLoadMoreAlbums()
    {
        if (mAlbumLisData.size() <mMaxNumOfTags)
        {
            currentLodingIndex++;

            FlyingHttpTool.getAlbumList(null, currentLodingIndex, true, true, new FlyingHttpTool.GetAlbumListListener() {
                @Override
                public void completion(ArrayList<AlbumData> albumList, String allRecordCount) {

                    if(albumList!=null || albumList.size()!=0)
                    {
                        for (AlbumData data : albumList) {
                            mAdapter.add(data);
                        }
                        // stash all the data in our backing store
                        mAlbumLisData.addAll(albumList);

                        mMaxNumOfTags= Integer.parseInt(allRecordCount);

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                // notify the adapter that we can update now
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }
    }

    private void initCirclePoint()
    {
        dots = new ArrayList<View>();

        ViewGroup group = (ViewGroup) mHomeContent.findViewById(R.id.dotViewGroup);

        //广告栏的小圆点图标
        for (int i = 0; i < mCoverLessonList.size(); i++) {
            //创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            View dotView = new View(getActivity());
            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(8,8);
            params.setMargins(0,4,4,0);

            //初始值, 默认第0个选中
            if (i == 0)
            {
                dotView.setBackgroundResource(R.drawable.dot_focused);
            }
            else
            {
                dotView.setBackgroundResource(R.drawable.dot_normal);
            }

            //将小圆点放入到布局中
            group.addView(dotView,params);
            dots.add(dotView);
        }
    }
}
