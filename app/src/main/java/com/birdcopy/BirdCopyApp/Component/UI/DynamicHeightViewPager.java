package com.birdcopy.BirdCopyApp.Component.UI;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by birdcopy on 14/10/14.
 */

public class DynamicHeightViewPager extends ViewPager {

    public DynamicHeightViewPager(Context context) {
        super(context);
    }

    public DynamicHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec)
                == MeasureSpec.AT_MOST;

        if(wrapHeight) {
            /**
             * The first super.onMeasure call made the pager take up all the
             * available height. Since we really wanted to wrap it, we need
             * to remeasure it. Luckily, after that call the first child is
             * now available. So, we take the height from it.
             */

            int width = getMeasuredWidth(), height = getMeasuredWidth()*9/16;

            // Use the previously measured width but simplify the calculations
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

            //If the pager actually has any children, take the first child's
            //height and call that our own */

            /*
             if(getChildCount() > 0) {
                View firstChild = getChildAt(0);

                //The child was previously measured with exactly the full height.
                //Allow it to wrap this time around.
                firstChild.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

                height = firstChild.getMeasuredHeight();
            }
            */

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}