package com.birdcopy.BirdCopyApp.ChannelManage;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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

public class AlbumDataModle
{
    public final static int HTTP_RESPONSE = 0;
    private  String mResponseStr=null;
    public interface DealResult
    {

        public void parseALbumDataOK(ArrayList<AlbumData> list);
        public void setMaxAlbums(int itemCount);
    }

    private DealResult delegate;

    public void setDelegate(DealResult delegate) {

        this.delegate = delegate;
    }

    Handler myHandler = new Handler()
    {
        // 接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HTTP_RESPONSE:
                    MyTask dTask = new MyTask();
                    dTask.execute(mResponseStr);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public void loadChannelAlbumListData(String contentType)
    {
        String url = ShareDefine.getAlbumListByTagURL(contentType, 0, true,false);

        if (ShareDefine.checkNetWorkStatus()==true)
        {
            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

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
            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

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

    public void loadHomlAlbumListData(String contentType)
    {
        String url = ShareDefine.getAlbumListByTagURL(contentType, 1, true,true);

        if (ShareDefine.checkNetWorkStatus()==true)
        {
            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .noCache()
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

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
            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

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

    private class MyTask extends AsyncTask<String, Void, ArrayList<AlbumData>> {
        @Override
        protected ArrayList<AlbumData> doInBackground(String... params) {
            try {
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                AlbumParser myXMLHandler = new AlbumParser();
                xr.setContentHandler(myXMLHandler);
                xr.parse(new InputSource(new ByteArrayInputStream(params[0].getBytes())));

                delegate.setMaxAlbums(myXMLHandler.entries.size());
                return myXMLHandler.entries;
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<AlbumData> result)
        {

            super.onPostExecute(result);

            delegate.parseALbumDataOK(result);
        }
    }
}



