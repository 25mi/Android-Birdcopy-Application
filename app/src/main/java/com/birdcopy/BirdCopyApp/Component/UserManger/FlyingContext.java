package com.birdcopy.BirdCopyApp.Component.UserManger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.Component.Base.MyApplication;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_RongUser;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.DAO.FlyingRongUserDAO;
import com.birdcopy.BirdCopyApp.IM.SOSOLocationActivity;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * Created by vincent sung on 2015/7/9.
 */
public class FlyingContext {

    private static FlyingContext mDemoContext;
    public Context mContext;
    private HashMap<String, Group> groupMap;
    private FlyingRongUserDAO mRongUserDAO = new FlyingRongUserDAO();
    private ArrayList<UserInfo> mFriendInfos;
    private SharedPreferences mPreferences;
    private RongIM.LocationProvider.LocationCallback mLastLocationCallback;


    public static FlyingContext getInstance() {

        if (mDemoContext == null) {
            mDemoContext = new FlyingContext();
        }

        return mDemoContext;
    }

    private FlyingContext() {

    }

    private FlyingContext(Context context) {
        mContext = context;
        mDemoContext = this;

        mPreferences = MyApplication.getSharedPreference();

        RongIM.setLocationProvider(new LocationProvider());
    }

    public static void init(Context context) {
        mDemoContext = new FlyingContext(context);
    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.mPreferences = sharedPreferences;
    }

    public void setGroupMap(HashMap<String, Group> groupMap) {
        this.groupMap = groupMap;
    }

    public HashMap<String, Group> getGroupMap() {
        return groupMap;
    }

    public ArrayList<UserInfo> getUserList()
    {

        ArrayList<UserInfo> result =new ArrayList<UserInfo>();

        for(BE_RongUser rongUser:mRongUserDAO.loadAllData())
        {
            UserInfo userInfo= new UserInfo(rongUser.getUserid(),rongUser.getName(),Uri.parse(rongUser.getPortraitUri()));

            result.add(userInfo);
        }

        return result;
    }
    /*
    public void setUserInfos(ArrayList<UserInfo> userInfos) {
        mUserInfos = userInfos;
    }
    */

    /**
     * 临时存放用户数据
     *
     * @param userInfos
     */
    public void setFriends(ArrayList<UserInfo> userInfos) {

        this.mFriendInfos = userInfos;
    }

    public ArrayList<UserInfo> getFriends() {
        return mFriendInfos;
    }

    /*
    public DemoApi getDemoApi() {
        return mDemoApi;
    }
    */


    /**
     * 增加用户信息
     *
     * @param userInfo
     * @return
     */
    public void addOrReplaceRongUserInfo(UserInfo userInfo)
    {
        BE_RongUser user = new BE_RongUser();
        user.setUserid(userInfo.getUserId());
        user.setName(userInfo.getName());
        user.setPortraitUri(userInfo.getPortraitUri().toString());

        mRongUserDAO.saveRongUser(user);

        RongIM.getInstance().refreshUserInfoCache(userInfo);
    }


    /**
     * 获取用户信息
     *
     * @param rongID
     * @return
     */
    public UserInfo getUserInfoByRongId(final String rongID)
    {
        BE_RongUser rongUser =mRongUserDAO.selectWithUserID(rongID);

        if(rongUser==null)
        {
            String url = ShareDefine.getUsrInfoByRongID(rongID);

            Ion.with(MyApplication.getInstance().getApplicationContext())
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error

                            if (e != null) {
                                Toast.makeText(mContext, "Error get Rong User Info", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String code = result.get("rc").getAsString();

                            if (code.equals("1")) {

                                String name = result.get("name").getAsString();
                                Uri uri= Uri.parse(result.get("portraitUri").getAsString());
                                UserInfo userInfo = new UserInfo(rongID,name,uri);

                                addOrReplaceRongUserInfo(userInfo);
                            }
                            else
                            {
                                String errorInfo = result.get("rm").getAsString();
                                Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            return null;
        }
        else
        {

            return new UserInfo(rongUser.getUserid(),rongUser.getName(),Uri.parse(rongUser.getPortraitUri()));
        }
    }

    /**
     * 通过userid 获得username
     *
     * @param userId
     * @return
     */
    public String getUserNameByUserId(String userId) {


        UserInfo userInfo =getUserInfoByRongId(userId);

        if (userInfo!=null)
        {

            return userInfo.getName();
        }
        else
        {

            return null;
        }
    }

    /**
     * 获取用户信息列表
     *
     * @param userIds
     * @return
     */
    public List<UserInfo> getUserInfoByIds(String[] userIds) {

        List<UserInfo> userInfoList = new ArrayList<UserInfo>();

        if (userIds != null && userIds.length > 0) {
            for (String userId : userIds)
            {
                userInfoList.add(getUserInfoByRongId(userId));
            }
        }
        return userInfoList;
    }

    /**
     * 通过groupid 获得groupname
     *
     * @param groupid
     * @return
     */
    public String getGroupNameById(String groupid) {
        Group groupReturn = null;
        if (!TextUtils.isEmpty(groupid) && groupMap != null) {

            if (groupMap.containsKey(groupid)) {
                groupReturn = groupMap.get(groupid);
            }else
                return null;

        }
        return groupReturn.getName();
    }


    public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }

    class LocationProvider implements RongIM.LocationProvider {

        /**
         * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
         *
         * @param context  上下文
         * @param callback 回调
         */
        @Override
        public void onStartLocation(Context context, RongIM.LocationProvider.LocationCallback callback) {
            /**
             * demo 代码  开发者需替换成自己的代码。
             */
            FlyingContext.getInstance().setLastLocationCallback(callback);
            Intent intent = new Intent(context, SOSOLocationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);//SOSO地图
        }
    }

}
