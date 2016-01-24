package com.birdcopy.BirdCopyApp.MainHome;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdcopy.BirdCopyApp.ChannelManage.AlbumData;
import com.birdcopy.BirdCopyApp.Component.UI.grid.util.DynamicHeightImageView;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.squareup.picasso.Picasso;

/***
 * ADAPTER
 */

public class AlbumListAdapter extends ArrayAdapter<AlbumData> {

    private static final String TAG = "AlbumListAdapter";

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

        String url = itemData.getImageURL();

        if(url!=null && ShareDefine.checkURL(url))
        {
            Picasso.with(getContext())
                    .load(url)
                    .into(vh.albumCoverImageView);
        }
        else
        {
            vh.albumCoverImageView.setImageResource(R.drawable.icon);
        }

        convertView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        return convertView;
    }
}