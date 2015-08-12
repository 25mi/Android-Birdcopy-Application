package com.birdcopy.BirdCopyApp.Component.UserManger;

import android.content.SharedPreferences;
import android.widget.Toast;
import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/**
 * Created by BirdCopyApp on 29/7/14.
 */
public class SSKeychain {

    static public String getPassport()
    {
        String  currentPassport=MyApplication.getSharedPreference().getString("passport",null);

        if(currentPassport==null)
        {
            final String passport = OpenUDID_manager.getOpenUDID();

            String url = ShareDefine.getRegUserIDURL(passport);

            Ion.with(MyApplication.getInstance().getApplicationContext())
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
                                String resultStr = result.getResult();

                                if(resultStr.equals("0"))
                                {
                                    SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                                    editor.putString("passport", passport);
                                    editor.commit();
                                }
                                else if(resultStr.equals("1"))
                                {
                                    Toast.makeText(MyApplication.getInstance().getApplicationContext(), "此终端设备已被激活，可以直接登录!", Toast.LENGTH_LONG).show();

                                    SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();
                                    editor.putString("passport", passport);
                                    editor.commit();
                                }
                            }
                        }
                    });

            return passport;
        }

        return currentPassport;
    }
}
