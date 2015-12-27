package com.birdcopy.BirdCopyApp.IM;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.birdcopy.BirdCopyApp.Content.WebViewActivity;
import com.birdcopy.BirdCopyApp.R;

import io.rong.imkit.RongContext;
import io.rong.imkit.widget.provider.InputProvider;

/**
 * Created by birdcopy on 7/2/15.
 */

public class SurveyProvider extends InputProvider.ExtendProvider {

    private int REQUEST_CONTACT = 30;

    public SurveyProvider(RongContext context) {
        super(context);
    }

    @Override
    public Drawable obtainPluginDrawable(Context context) {

        return context.getResources().getDrawable(R.drawable.help);
    }

    @Override
    public CharSequence obtainPluginTitle(Context context) {
        return context.getString(R.string.add_help);
    }

    @Override
    public void onPluginClick(View view) {

        Intent intent = new Intent(getContext(),WebViewActivity.class);

        intent.putExtra("url", "http://www.mikecrm.com/f.php?t=UkWGrx");
        intent.putExtra("title", "我要参与设计");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CONTACT);
    }
}
