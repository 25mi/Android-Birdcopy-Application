package com.birdcopy.BirdCopyApp.Component.Document;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.ImageShowActivity;

import java.util.ArrayList;

/**
 * Created by birdcopy on 25/9/14.
 */


public class WebFragment extends Fragment
{
    public BE_PUB_LESSON lessonData;
    public String webURL;

    public WebView mWebView;
    private boolean mIsWebViewAvailable;
    public WebFragment()
    {
    }
    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getActivity());
        mIsWebViewAvailable = true;

        if(lessonData!=null)
        {
            webURL=lessonData.getBECONTENTURL();
        }

        initWebView();
        return mWebView;
    }
    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }
    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        mWebView.resumeTimers();
        super.onResume();
    }
    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }
    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    public  boolean onKeyDown(int keyCode, KeyEvent event)
    {
      if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
      {
          mWebView.goBack();
         return true;
      }

      return true;
    }

    private void initWebView()
    {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (!TextUtils.isEmpty(webURL))
        {
            WebSettings settings = mWebView.getSettings();

            settings.setJavaScriptEnabled(true);//设置使用够执行JS脚本
            settings.setBuiltInZoomControls(true);//设置使支持缩放

            // 添加js交互接口类，并起别名 imagelistner
            mWebView.addJavascriptInterface(new JavascriptInterface(getActivity()),"imagelistner");
            mWebView.loadUrl(webURL);
        }

        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;//true表示此事件在此处被处理，不需要再广播
            }
            @Override   //转向错误时的处理
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(view, url);
                // html加载完成之后，添加监听图片的点击js函数
                addImageClickListner();
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.getSettings().setJavaScriptEnabled(true);
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    // 注入js函数监听
    private void addImageClickListner()
    {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        mWebView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"img\");"
                + "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
                + "imgurl+=objs[i].src+',';"
                + "    objs[i].onclick=function()  " + "    {  "
                + "        window.imagelistner.openImage(imgurl);  "
                + "    }  " + "}" + "})()");
    }

    // js通信接口
    public class JavascriptInterface
    {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {

            //
            String[] imgs = img.split(",");
            ArrayList<String> imgsUrl = new ArrayList<String>();
            for (String s : imgs) {
                imgsUrl.add(s);
                Log.i("图片的URL>>>>>>>>>>>>>>>>>>>>>>>", s);
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("infos", imgsUrl);
            intent.setClass(context, ImageShowActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}