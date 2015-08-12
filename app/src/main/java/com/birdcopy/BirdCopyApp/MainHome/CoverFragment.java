package com.birdcopy.BirdCopyApp.MainHome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by birdcopy on 22/9/14.
 */
public class CoverFragment  extends Fragment
{
    private View coverView;

    private BE_PUB_LESSON mLessonData;

    public  void  setLessonData(BE_PUB_LESSON lessonData)
    {
        mLessonData = lessonData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        coverView = (View) inflater.inflate(R.layout.homecover, container, false);

        ImageView imageView =(ImageView)coverView.findViewById(R.id.homecoverimage);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mLessonData!=null){

                    MainActivity mainActivity = (MainActivity)getActivity();

                    mainActivity.showLessonViewWithData(mLessonData);
                }
            }
        });

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mLessonData.getBEIMAGEURL(), imageView);

        return coverView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
