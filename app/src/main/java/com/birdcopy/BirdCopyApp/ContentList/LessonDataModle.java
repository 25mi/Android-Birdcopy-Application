package com.birdcopy.BirdCopyApp.ContentList;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class LessonDataModle
{
    public final static int HTTP_RESPONSE = 0;
    private  String mResponseStr=null;

    public interface DealResult
    {
        public void parseOK(ArrayList<BE_PUB_LESSON> list);
        public void setMaxItems(int itemCount);
    }

    private DealResult delegate;
    private int mAllRecordCount=0;

    public void setDelegate(DealResult delegate)
    {
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

    public void loadMoreLessonData(String contentType,
                                   String downloadType,
                                   String tag,
                                   int pageNumber,
                                   boolean sortByTime)
    {
        String url = ShareDefine.getLessonListByTagURL(contentType,
                downloadType,
                tag,
                pageNumber,
                sortByTime);

        if (ShareDefine.checkNetWorkStatus()==true)
        {
            Ion.with( MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded

                            if (result!=null)
                            {
                                mResponseStr = result.getResult();
                                Message message = new Message();
                                message.what = HTTP_RESPONSE;
                                myHandler.sendMessage(message);
                            }
                        }
                    });
        }
        else
        {
            Ion.with( MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded

                            if (result!=null)
                            {
                                mResponseStr = result.getResult();
                                Message message = new Message();
                                message.what = HTTP_RESPONSE;
                                myHandler.sendMessage(message);
                            }
                        }
                    });
        }
    }

    private class MyTask extends AsyncTask<String, Void, ArrayList<BE_PUB_LESSON>>
    {
        @Override
        protected ArrayList<BE_PUB_LESSON> doInBackground(String... params)
        {
            try
            {
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                LessonParser myXMLHandler = new LessonParser();
                xr.setContentHandler(myXMLHandler);
                xr.parse(new InputSource(new ByteArrayInputStream(params[0].getBytes())));

                mAllRecordCount = myXMLHandler.allRecordCount;
                return myXMLHandler.entries;
            }
            catch (Exception e)
            {
                System.out.println("XML Pasing Excpetion = " + e);
                return  null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<BE_PUB_LESSON> result)
        {
            super.onPostExecute(result);

            delegate.setMaxItems(mAllRecordCount);
            delegate.parseOK(result);
        }
    }

    public void loadcoverlessonlist(int pagenumber)
    {

        String url = ShareDefine.getCoverLessonList(true,pagenumber);

        if (ShareDefine.checkNetWorkStatus()==true)
        {
            Ion.with( MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded

                            if (result!=null)
                            {
                                mResponseStr = result.getResult();
                                Message message = new Message();
                                message.what = HTTP_RESPONSE;
                                myHandler.sendMessage(message);
                            }
                        }
                    });
        }
        else
        {
            Ion.with( MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            // print the response code, ie, 200
                            //System.out.println(result.getHeaders().getResponseCode());
                            // print the String that was downloaded

                            if (result!=null)
                            {
                                mResponseStr = result.getResult();
                                Message message = new Message();
                                message.what = HTTP_RESPONSE;
                                myHandler.sendMessage(message);
                            }
                        }
                    });
        }
    }
}
