package com.birdcopy.BirdCopyApp.IM.photo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.birdcopy.BirdCopyApp.R;


public class MyAdapter extends CommonAdapter<String> {

	public List<String> mSelectedImage = new ArrayList<String>();

	private String mDirPath;
	private int maxPic;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath, int maxPic) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.maxPic = maxPic;
	}

	public ArrayList<String> getPicList() {
		if (mSelectedImage == null) {
			mSelectedImage = new ArrayList<String>();
		}
		Log.e("list", mSelectedImage.toString());
		return (ArrayList<String>) mSelectedImage;
	}

	public void setReset() {
		mSelectedImage.clear();
	}
	
	
	@Override
	public void convert(final ViewHolder helper, final String item) {
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no_white);
		helper.setImageResource(R.id.id_item_select,
				R.drawable.picture_unselected);
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mSelectedImage.contains(mDirPath + "/" + item)) {
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else {
					if (mSelectedImage.size() < maxPic) {
						mSelectedImage.add(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					} else {
//						Toast.makeText(mContext, "最多显示" + maxPic + "张",
//								Toast.LENGTH_SHORT).show();
						Toast.makeText(mContext, "最多显示" + 9 + "张",
								Toast.LENGTH_SHORT).show();
					}
				}
				Log.i("size", "count-->" + mSelectedImage.size());

			}
		});

		if (mSelectedImage.contains(mDirPath + "/" + item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
