package com.birdcopy.BirdCopyApp.LocalContent;


import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.UI.grid.StaggeredGridView;
import com.birdcopy.BirdCopyApp.Download.FlyingDownloadManager;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.R;


import java.util.ArrayList;

/**
 * Created by songbaoqiang on 6/8/14.
 */
public class MyLeesonsFragment extends Fragment implements MylessonsData.DealResult
{
    public static final String SAVED_DATA_KEY   = "SAVED_DATA_KEY";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore=false;
    private MyLessonsAdapter mAdapter;

    private ArrayList<BE_PUB_LESSON> mData;

    public  String mTagString;

    public  String mDownloadType;
    public  boolean mSortByTime;

    private MylessonsData mylessonsData;
    private AlertDialog.Builder mAlertBuilder;

    int     mMaxNumOfLessons;
    int     mCurrentLodingIndex;

    private  View mFooterView;

    private String mFooterTitle;


    public MyLeesonsFragment(){
        // Empty constructor required for fragment subclasses

        super();

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init()
    {
        mTagString="";
        mDownloadType=ShareDefine.KDownloadTypeNormal;
        mSortByTime=true;

        mMaxNumOfLessons= ShareDefine.MAX_INT;
        mCurrentLodingIndex=-1;

        mFooterTitle="加载中....";

        mData = new ArrayList<BE_PUB_LESSON>();
        mylessonsData = new MylessonsData();
        mylessonsData.setDelegate(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        super.onCreateView(inflater,container,savedInstanceState);

        if(savedInstanceState!=null)
        {

            String temp = savedInstanceState.getString(SAVED_DATA_KEY);

            if(temp!=null)
            {
                mFooterTitle = temp;
            }
        }

        mAlertBuilder = new AlertDialog.Builder(getActivity());
        mGridView = (StaggeredGridView)inflater.inflate(R.layout.gridview, container, false);

        mFooterView = inflater.inflate(R.layout.list_item_header_footer, null);
        TextView txtFooterTitle =  (TextView) mFooterView.findViewById(R.id.txt_title);
        txtFooterTitle.setText(mFooterTitle);
        mGridView.addFooterView(mFooterView);

        mAdapter = new MyLessonsAdapter( getActivity(), R.id.lesson_grid_content);
        if (mData != null && mData.size()!=0){

            for (BE_PUB_LESSON data : mData) {
                mAdapter.add(data);
            }
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener( new AbsListView.OnScrollListener(){


                                           @Override
                                           public void onScrollStateChanged(AbsListView view, int scrollState)
                                           {
                                               if(scrollState==SCROLL_STATE_FLING)
                                               {

                                                   onLoadMoreItems();
                                               }
                                           }

                                           @Override
                                           public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                                               // our handling
                                               if (mHasRequestedMore) {
                                                   int lastInScreen = firstVisibleItem + visibleItemCount;
                                                   if (lastInScreen >= totalItemCount) {
                                                       onLoadMoreItems();
                                                   }
                                               }
                                           }

                                       }
        );

        mGridView.setOnItemClickListener(new AbsListView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                BE_PUB_LESSON lesson =  mData.get(position);

                if (lesson!=null){

                    MainActivity mainActivity = (MainActivity)getActivity();

                    mainActivity.showLessonViewWithData(lesson);
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final BE_PUB_LESSON lessonData =  mData.get(position);

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE){

                            deleteLesson(lessonData);
                        }
                    }
                };
                AlertDialog alert = mAlertBuilder.create();
                alert.setTitle("友情提醒");
                alert.setMessage(getString(R.string.document_delete_ACK));
                alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), listener);
                alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), listener);
                alert.show();
                return false;
            }
        });

        onLoadMoreItems();

        return mGridView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void deleteLesson(BE_PUB_LESSON lessonData)
    {
        if (lessonData!=null){

            //释放下载资源
            if (lessonData.getBEDLPERCENT().doubleValue()<1 )
            {
                FlyingDownloadManager.getInstance().closeAndReleaseDownloaderForID(lessonData.getBELESSONID());
            }

            //删除数据库本地纪录，资源自动释放
            new FlyingContentDAO().deleteWithLessonID(lessonData.getBELESSONID());

            //删除相关本地内容
            if(lessonData.getLocalURLOfContent()!=null)
            {
                FlyingFileManager.deleteFile(lessonData.getLocalURLOfContent());
            }

            mData.remove(lessonData);
            // notify the adapter that we can update now
            mAdapter.remove(lessonData);
            mAdapter.notifyDataSetChanged();

            updateNoteView();
        }
    }

    private void onLoadMoreItems()
    {
        if (mData.size() <mMaxNumOfLessons) {

            mCurrentLodingIndex++;

            mylessonsData.loadMoreLessonData (mCurrentLodingIndex);
        }
    }

    public void parseOK(ArrayList<BE_PUB_LESSON> list)
    {

        if (list!=null && list.size()>0)
        {
            for (BE_PUB_LESSON data : list) {
                mAdapter.add(data);
            }
            // stash all the data in our backing store
            mData.addAll(list);
            // notify the adapter that we can update now
            mAdapter.notifyDataSetChanged();
        }

        updateNoteView();
    }

    @Override
    public void setMaxItems(int itemCount)
    {
        mMaxNumOfLessons = itemCount;
    }

    private void updateNoteView()
    {
        if (mData.size() == mMaxNumOfLessons )
        {

            TextView txtFooterTitle =  (TextView) mFooterView.findViewById(R.id.txt_title);
            if(mData.size()==0){
                mFooterTitle="没有离线内容！";
            }
            else
            {
                mFooterTitle="长按下载内容可以删除！";
            }
            txtFooterTitle.setText(mFooterTitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Store the game state
        outState.putString(SAVED_DATA_KEY, mFooterTitle);
    }
}
