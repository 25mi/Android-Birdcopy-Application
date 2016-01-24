package com.birdcopy.BirdCopyApp.MainHome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by birdcopy on 22/9/14.
 */
public class CoverFragment  extends Fragment
{
    private View coverView;
    private ImageView contentType;

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

        contentType = (ImageView)coverView.findViewById(R.id.contentType);

        ImageView imageView =(ImageView)coverView.findViewById(R.id.homecoverimage);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLessonData != null) {

                    MainActivity mainActivity = (MainActivity) getActivity();

                    mainActivity.showLessonViewWithData(mLessonData);
                }
            }
        });

        String url = mLessonData.getBEIMAGEURL();

        if(url!=null && ShareDefine.checkURL(url))
        {
            Picasso.with(getContext())
                    .load(url)
                    .into(imageView);
        }
	    else
        {
	        imageView.setImageResource(R.drawable.icon);
        }

        if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
        {
            contentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_drawer_audio));
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeVideo))
        {
            contentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_drawer_video));
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
        {
            contentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_drawer_doc));
        }
        else if(mLessonData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
        {
            contentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_drawer_web));
        }

        return coverView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
