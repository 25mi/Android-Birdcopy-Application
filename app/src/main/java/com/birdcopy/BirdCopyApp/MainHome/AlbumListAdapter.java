package com.birdcopy.BirdCopyApp.MainHome;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdcopy.BirdCopyApp.ChannelManage.AlbumData;
import com.birdcopy.BirdCopyApp.Component.Tools.Options;
import com.birdcopy.BirdCopyApp.Component.UI.grid.util.DynamicHeightImageView;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/***
 * ADAPTER
 */

public class AlbumListAdapter extends ArrayAdapter<AlbumData> {

    private static final String TAG = "AlbumListAdapter";

    DisplayImageOptions options= Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    static class ViewHolder {
        TextView albumTitleTextView;
        DynamicHeightImageView albumCoverImageView;
    }

    private final LayoutInflater mLayoutInflater;

    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public AlbumListAdapter(final Context context, final int textViewResourceId) {
        super(context, textViewResourceId);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {

        final ViewHolder vh;
        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.albumlist_item, parent, false);
            vh = new ViewHolder();
            vh.albumTitleTextView = (TextView)convertView.findViewById(R.id.albumTitle);

            vh.albumCoverImageView = (DynamicHeightImageView) convertView.findViewById(R.id.albumCoverImage);
            vh.albumCoverImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vh.albumCoverImageView.setHeightRatio(9/16);

            convertView.setTag(vh);
        }
        else
        {
            vh = (ViewHolder) convertView.getTag();
        }

        AlbumData itemData = (AlbumData)getItem(position);

        vh.albumTitleTextView.setText(itemData.getTagString());

        vh.albumCoverImageView.setVisibility(View.INVISIBLE);
        imageLoader.displayImage(itemData.getImageURL(), vh.albumCoverImageView);

        int fadeInDuration = 2000; // Configure time values here

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                vh.albumCoverImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        vh.albumCoverImageView.setAnimation(fadeIn);
        convertView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        return convertView;
    }
}