package com.birdcopy.BirdCopyApp.ContentList;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.UI.grid.util.DynamicHeightImageView;
import com.birdcopy.BirdCopyApp.Component.UI.grid.util.DynamicHeightTextView;
import com.birdcopy.BirdCopyApp.R;
import com.squareup.picasso.Picasso;

/***
 * ADAPTER
 */

public class LessonListAdapter extends ArrayAdapter<BE_PUB_LESSON> {

    private static final String TAG = "LessonListAdapter";

    static class ViewHolder {
        ImageView lessonContentType;
        TextView lessonTitleTextView;
        DynamicHeightImageView lessonCoverImageView;
        DynamicHeightTextView lessonDescriptionDyTextView;
    }

    private final LayoutInflater mLayoutInflater;

    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public LessonListAdapter(final Context context, final int textViewResourceId) {
        super(context, textViewResourceId);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        final ViewHolder vh;
        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.lessonlist_item, parent, false);
            vh = new ViewHolder();

            vh.lessonContentType=(ImageView)convertView.findViewById(R.id.lessonContentType);

            vh.lessonTitleTextView = (TextView)convertView.findViewById(R.id.lessonTitle);

            vh.lessonCoverImageView = (DynamicHeightImageView) convertView.findViewById(R.id.lessonCoverImage);
            vh.lessonCoverImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vh.lessonCoverImageView.setHeightRatio(9/16);

            vh.lessonDescriptionDyTextView = (DynamicHeightTextView) convertView.findViewById(R.id.lessonDescription);

            convertView.setTag(vh);
        }
        else
        {
            vh = (ViewHolder) convertView.getTag();
        }

        BE_PUB_LESSON itemData = (BE_PUB_LESSON)getItem(position);

        vh.lessonTitleTextView.setText(itemData.getBETITLE());
        vh.lessonDescriptionDyTextView.setText(itemData.getBEDESC());

        Picasso.with(getContext())
                .load(itemData.getBEIMAGEURL())
		        .placeholder(R.drawable.icon)
		        .into(vh.lessonCoverImageView);

        if(itemData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeAudio))
        {
            vh.lessonContentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(),R.drawable.ic_drawer_audio));
        }
        else if(itemData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeVideo))
        {
            vh.lessonContentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(),R.drawable.ic_drawer_video));
        }
        else if(itemData.getBECONTENTTYPE().equals(ShareDefine.KContentTypeText))
        {
            vh.lessonContentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(),R.drawable.ic_drawer_doc));
        }
        else if(itemData.getBECONTENTTYPE().equals(ShareDefine.KContentTypePageWeb))
        {
            vh.lessonContentType.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(),R.drawable.ic_drawer_web));
        }

        convertView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        return convertView;
    }
}