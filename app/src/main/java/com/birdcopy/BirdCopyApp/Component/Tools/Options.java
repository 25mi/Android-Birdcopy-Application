package com.birdcopy.BirdCopyApp.Component.Tools;

import com.birdcopy.BirdCopyApp.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Options {

	public static DisplayImageOptions getListOptions() {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.icon) // resource or drawable
                .showImageOnFail(R.drawable.error_image) // resource or drawable
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
                .displayer(new FadeInBitmapDisplayer(100))// 淡入
                .build();

		return options;
	}
}
