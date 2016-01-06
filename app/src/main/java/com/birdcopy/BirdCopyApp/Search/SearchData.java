package com.birdcopy.BirdCopyApp.Search;

import android.os.Handler;
import android.os.Message;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.artifex.mupdfdemo.AsyncTask;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchData
{
    public final static int HTTP_RESPONSE = 0;
    private  String mResponseStr=null;

    public interface DealResult {

        public void parseOK(ArrayList<String> list);
    }

    private DealResult delegate;


    public void setDelegate(DealResult delegate ){

        this.delegate = delegate;
    }

    Handler myHandler = new Handler()
    {
        // 接收到消息后处理
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case HTTP_RESPONSE:
                    MyTask dTask = new MyTask();
                    dTask.execute(mResponseStr);
                    break;
            }

            super.handleMessage(msg);
        }
    };
    public void getTagListStrByTag(String tag,int count)
    {
        String url = ShareDefine.getTagListStrByTag(tag, count);

        try
        {
            AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(url), new AsyncHttpClient.StringCallback() {

                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s)
                {
                    mResponseStr = s;
                    Message message = new Message();
                    message.what = HTTP_RESPONSE;
                    myHandler.sendMessage(message);
                }

                @Override
                public void onConnect(AsyncHttpResponse response) {
                    //
                }
            });
        }
        catch (Exception e)
        {
            String msg = "联网失败提醒";
            System.out.println(msg);
        }
    }

    private class MyTask extends AsyncTask<String, Void, ArrayList<String>>
    {
        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            try
            {
                String tagStr = params[0];;
                String[] seperated = tagStr.split("\\,");

                ArrayList<String> taglist = new ArrayList<String>();
                taglist.addAll(Arrays.asList(seperated));

                return  taglist;
            }
            catch (Exception e)
            {
                System.out.println("XML Pasing Excpetion = " + e);

                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<String> result)
        {
            super.onPostExecute(result);

            if (result.size()>0){
                delegate.parseOK(result);
            }
        }
    }
}
