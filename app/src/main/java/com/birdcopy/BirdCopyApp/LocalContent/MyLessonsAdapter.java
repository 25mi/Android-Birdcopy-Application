package com.birdcopy.BirdCopyApp.LocalContent;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.Component.UI.grid.util.DynamicHeightImageView;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.squareup.picasso.Picasso;

/***
 * ADAPTER
 */

public class MyLessonsAdapter extends ArrayAdapter<BE_PUB_LESSON> {

    static class ViewHolder
    {
        DynamicHeightImageView mMyLessonCoverImageView;
        TextView mMyLessonTitleTextView;
    }

    private final LayoutInflater mLayoutInflater;

    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public MyLessonsAdapter(final Context context, final int textViewResourceId)
    {
        super(context, textViewResourceId);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {

        ViewHolder vh;

        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.mylessons_item, parent, false);
            vh = new ViewHolder();

            vh.mMyLessonCoverImageView = (DynamicHeightImageView) convertView.findViewById(R.id.mylessonCoverImage);
            vh.mMyLessonCoverImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vh.mMyLessonCoverImageView.setHeightRatio(9/16);

            vh.mMyLessonTitleTextView = (TextView)convertView.findViewById(R.id.mylessonTitle);

            convertView.setTag(vh);
        }
        else
        {
            vh = (ViewHolder) convertView.getTag();
            vh.mMyLessonCoverImageView.setImageBitmap( BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.icon));
            vh.mMyLessonTitleTextView.setText(R.string.grid_item_title);
        }

        BE_PUB_LESSON itemData = (BE_PUB_LESSON)getItem(position);

	    String url = itemData.getBEIMAGEURL();

	    if(url!=null && ShareDefine.checkURL(url)) {

		    Picasso.with(getContext())
				    .load(url)
				    .into(vh.mMyLessonCoverImageView);
	    }
	    else
	    {
		    vh.mMyLessonCoverImageView.setImageResource(R.drawable.icon);
	    }

        vh.mMyLessonTitleTextView.setText(itemData.getBETITLE());
        convertView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        return convertView;
    }

    private double getPositionRatio(final int position,TextView textView) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio(textView);
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio(TextView textView)
    {
        return 0.5;
                //textView.getLineHeight()*textView.getLineCount()/textView.getWidth();
    }
}

