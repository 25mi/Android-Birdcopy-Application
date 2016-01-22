package com.birdcopy.BirdCopyApp.Component.Adapter;

import java.util.ArrayList;

import com.birdcopy.BirdCopyApp.Component.UI.imageshow.TouchImageView;

import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Component.UI.imageshow.ImageShowViewPager;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 图片浏览的PagerAdapter
 */
public class ImagePagerAdapter extends PagerAdapter {
	Context context;
	ArrayList<String> imgsUrl;
	LayoutInflater inflater = null;

	//view内控件
	TouchImageView full_image;
	TextView progress_text;
	ProgressBar progress;
	TextView retry;
	
	public ImagePagerAdapter(Context context, ArrayList<String> imgsUrl) {
		this.context = context;
		this.imgsUrl = imgsUrl;
		inflater = LayoutInflater.from(context);
	}
	
	/** 动态加载数据 */
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((ImageShowViewPager) container).mCurrentView = ((TouchImageView) ((View)object).findViewById(R.id.full_image));
	}
	
	@Override
	public int getCount() {
		return imgsUrl == null ? 0 : imgsUrl.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = inflater.from(context).inflate(R.layout.details_imageshow_item, null);
		full_image = (TouchImageView)view.findViewById(R.id.full_image);
		progress_text= (TextView)view.findViewById(R.id.progress_text);
		progress= (ProgressBar)view.findViewById(R.id.progress);
		retry= (TextView)view.findViewById(R.id.retry);//加载失败
		progress_text.setText(String.valueOf(position));

		Picasso.with(MyApplication.getInstance().getApplicationContext())
				.load(imgsUrl.get(position))
				.into(full_image);

		((ViewPager) container).addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);  
	}
}
