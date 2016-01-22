package com.birdcopy.BirdCopyApp.Http;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.ChannelManage.AlbumData;
import com.birdcopy.BirdCopyApp.ChannelManage.FlyingAlbumParser;
import com.birdcopy.BirdCopyApp.Comment.CommentDataResult;
import com.birdcopy.BirdCopyApp.Comment.FlyingCommentData;
import com.birdcopy.BirdCopyApp.Content.FlyingItemparser;
import com.birdcopy.BirdCopyApp.ContentList.FlyingContentParser;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_TOUCH_RECORD;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingItemDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingItemData;
import com.birdcopy.BirdCopyApp.DataManager.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingTouchDAO;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Component.Tools.DateTools;
import com.birdcopy.BirdCopyApp.DataManager.FlyingIMContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.Product;
import com.birdcopy.BirdCopyApp.IM.RongCloudEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.pingplusplus.libone.PayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * Created by vincentsung on 12/16/15.
 */
public class FlyingHttpTool {

	//////////////////////////////////////////////////////////////////////////////////
	//基础封装Retrofit
	//////////////////////////////////////////////////////////////////////////////////
	private static final String API_URL = FlyingDataManager.getServerNetAddress();

	public interface BirdCopyService {

		@GET("aa_get_app_info_from_hp.action")
		Call<ResponseBody> checkNewVersionAPP(@QueryMap Map<String, String> params);

		@GET("tu_rc_get_urt_from_hp.action")
		Call<RongCloudTokenResult> connectWithRongCloud(@QueryMap Map<String, String> params);

		@GET("tu_rc_get_usr_from_hp.action")
		Call<UserInfoResult>  getUserInfoByopenID(@QueryMap Map<String, String> params);

		@GET("tu_rc_sync_urb_from_hp.action")
		Call<CallResult> refreshUesrInfo(@QueryMap Map<String, String> params);

		@GET("ua_reg_user_from_hp.action")
		Call<ResponseBody> regOpenUDID(@QueryMap Map<String, String> params);

		@GET("tu_ua_get_status_from_tn.action")
		Call<CallResultCode> verifyOpenUDID(@QueryMap Map<String, String> params);

		@GET("ua_send_prelogin_info_from_hp.action")
		Call<ResponseBody> loginWithQR(@QueryMap Map<String, String> params);

		@GET("pa_get_on_from_tn.action")
		Call<GetOrderNoResult> getOrderNo(@QueryMap Map<String, String> params);

		@GET("ua_get_user_info_from_hp.action")
		Call<ResponseBody> getMembership(@QueryMap Map<String, String> params);

		@GET("ua_sync_validth_from_hp.action")
		Call<CallResult> updateMembership(@QueryMap Map<String, String> params);

		@GET("ua_get_user_info_from_hp.action")
		Call<ResponseBody> getMoneyData(@QueryMap Map<String, String> params);

		@GET("ua_sync_accobk_from_hp.action")
		Call<ResponseBody> uploadMoneyData(@QueryMap Map<String, String> params);


		@GET("la_get_user_info_from_hp.action")
		Call<ResponseBody> getQRData(@QueryMap Map<String, String> params);


		@GET("la_topup_pwd_from_hp.action")
		Call<ResponseBody> chargingCrad(@QueryMap Map<String, String> params);

		@GET("ua_get_user_info_from_hp.action")
		Call<ResponseBody> getContentStatistic(@QueryMap Map<String, String> params);


		@GET("ua_sync_lnclick_from_hp.action")
		Call<ResponseBody> uploadContentStatistic(@QueryMap Map<String, String> params);

		@GET("la_get_ln_detail_for_hp.action")
		Call<ResponseBody> getLessonData(@QueryMap Map<String, String> params);

		@GET("la_get_ln_list_for_hp.action")
		Call<ResponseBody> getLessonList(@QueryMap Map<String, String> params);


		@GET("tu_add_ct_from_tn.action")
		Call<CallResult> uploadComment(@QueryMap Map<String, String> params);


		@Multipart
		@POST("tu_rc_sync_urp_from_hp.action")
		Call<UpUserProtraitResult> uploadPotrait(@Part("tuser_key") RequestBody userId
				, @Part("portrait\"; filename=\"portrait.png ")RequestBody image);
	}

	static private Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(new OkHttpClient())
			.build();

	static private BirdCopyService birdCopyService = retrofit.create(BirdCopyService.class);

    //////////////////////////////////////////////////////////////////////////////////
    //#pragma 检查APP是否有新版本
    //////////////////////////////////////////////////////////////////////////////////
    public interface CheckNewVersionAPPDListener {

        void completion(final boolean isOK, final String downloadURL);
    }

    static public void checkNewVersionAPP(final String account,
                                          final String appID,
                                          final CheckNewVersionAPPDListener delegate )
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("type", "max");

	    Call<ResponseBody> apiCall = birdCopyService.checkNewVersionAPP(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try{

				    String resultStr = response.body().string();

				    if (resultStr != null) {
					    String[] separated = resultStr.split(";");

					    String version = separated[0]; // this will contain "Fruit"
					    String downloadURL = separated[1]; // this will contain "Fruit"

					    boolean isOK = false;

					    if (version != null) {
						    try {
							    if (Integer.parseInt(version) > ShareDefine.getVersionCode()) {

								    isOK = true;
							    } else {
								    isOK = false;

							    }
						    } catch (Exception ex) {
							    ex.printStackTrace();
						    }
					    }

					    if (delegate != null) {

						    delegate.completion(isOK, downloadURL);
					    }
				    }

			    }
			    catch (IOException e)
			    {
				    if (delegate != null) {

					    delegate.completion(false, null);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {

				    delegate.completion(false, null);
			    }
		    }
	    });

    }

    //////////////////////////////////////////////////////////////////////////////////
    //#pragma  登录问题
    //////////////////////////////////////////////////////////////////////////////////

    public interface ConnectWithRongCloudIDListener {

        void completion(final Boolean result);
    }

    static public void connectWithRongCloud(final String account,
                                            String appID,
                                            final ConnectWithRongCloudIDListener delegate)
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);

	    Call<RongCloudTokenResult> apiCall = birdCopyService.connectWithRongCloud(map);

	    apiCall.enqueue(new Callback<RongCloudTokenResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<RongCloudTokenResult> response) {

			    RongCloudTokenResult result = response.body();

			    if ("1".equals(result.getRc())) {

				    String rongDeviceKoken = result.getToken();
				    httpGetTokenSuccess(rongDeviceKoken, delegate);

			    } else {
				    String errorInfo = result.getRm();
				    Toast.makeText(MyApplication.getInstance().getApplicationContext(), errorInfo, Toast.LENGTH_SHORT).show();
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    Log.e("Network Avail Error", t.getMessage());

		    }
	    });

    }

    static private void httpGetTokenSuccess(final String token,final ConnectWithRongCloudIDListener delegate) {

        try {
            /**
             * IMKit SDK调用第二步
             *
             * 建立与服务器的连接
             *
             * 详见API
             * http://docs.rongcloud.cn/api/android/imkit/index.html
             */

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {

                            Log.e("httpGetTokenSuccess", "---------onTokenIncorrect ----------:");

                            //清除Rong Token
                            FlyingDataManager.setRongToken(ShareDefine.RONG_DEFAULT_TOKEN);

                            if(delegate!=null)
                            {
                                delegate.completion(false);
                            }
                        }

                        @Override
                        public void onSuccess(String userId) {

                            //在RongIM-connect-onSuccess后调用。
                            RongCloudEvent.getInstance().setOtherListener();

                            //保存Rong Token
                            FlyingDataManager.setRongToken(token);
                            if(delegate!=null)
                            {
                                delegate.completion(true);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                            Log.e("httpGetTokenSuccess", "---------onError ----------:" + e);
                            if(delegate!=null)
                            {
                                delegate.completion(false);
                            }
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //#pragma 个人账户昵称头像
    //////////////////////////////////////////////////////////////////////////////////
    public interface GetUserInfoByopenIDListener {

        void completion(final UserInfo userInfo);
    }

    static public void getUserInfoByopenID(final String account,
                                   String appID,
                                           final GetUserInfoByopenIDListener delegate ) {

	    Map<String,String> map = new HashMap<>();
	    map.put("tuser_key", account);
	    map.put("app_id", appID);

	    Call<UserInfoResult> apiCall = birdCopyService.getUserInfoByopenID(map);
	    apiCall.enqueue(new Callback<UserInfoResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<UserInfoResult> response) {

			    UserInfoResult result = response.body();
			    UserInfo userInfo = null;

			    if ("1".equals(result.getRc())) {

				    userInfo = new UserInfo(ShareDefine.getMD5(account), result.getName(), Uri.parse(result.getPortraitUri()));

				    FlyingIMContext.getInstance().addOrReplaceRongUserInfo(userInfo);
			    }

			    if (delegate != null) {
				    delegate.completion(userInfo);
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(null);
			    }
		    }
	    });
    }

    public interface RequestUploadPotraitListener {

        void completion(final boolean isOK);
    }

    static public void requestUploadPotrait(final String account,
                                            final String appID,
                                            File portraitFile,
                                            final RequestUploadPotraitListener delegate)
    {
		/*

	    String url = FlyingDataManager.getServerNetAddress() + "/tu_rc_sync_urp_from_hp.action";

	    Ion.with(MyApplication.getInstance().getApplicationContext())
			    .load(url)
			    .setMultipartParameter("tuser_key", account)
			    .setMultipartFile("portrait", portraitFile)
			    .asJsonObject()
			    .setCallback(new FutureCallback<JsonObject>() {
				    @Override
				    public void onCompleted(Exception e, JsonObject result) {
					    // do stuff with the result or error

					    if (e != null) {
						    //Toast.makeText(getActivity(), "upload portarit", Toast.LENGTH_SHORT).show();
						    return;
					    }

					    //上传图片到服务器，成功后通知融云服务器更新用户信息
					    String code = result.get("rc").getAsString();
					    if (code.equals("1")) {

						    String portraitUri = result.get("portraitUri").getAsString();

						    if (portraitUri == null) {
							    portraitUri = "http://www.birdcopy.com/img/logo.png";
						    }

						    //更新本地信息
						    FlyingDataManager.setPortraitUri(portraitUri);

						    refreshUesrInfo(account,
								    appID,
								    null,
								    portraitUri,
								    null,
								    new RefreshUesrInfoListener() {
									    @Override
									    public void completion(boolean isOK) {
										    delegate.completion(isOK);
									    }
								    }
						    );
					    } else
					    {
						    delegate.completion(false);
					    }
				    }
			    });

	    RequestBody file = RequestBody
			    .create(MediaType.parse("image/*"), portraitFile);

	    RequestBody requestBody = new MultipartBody.Builder()
			    .setType(MultipartBody.FORM)
			    .addFormDataPart("tuser_key", account)
			    .addFormDataPart("portrait", "portrait.jpg", file)
			    .build();*/

        RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), account);

		RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), portraitFile);


	    Call<UpUserProtraitResult> apiCall = birdCopyService.uploadPotrait(userid,body);

	    apiCall.enqueue(new Callback<UpUserProtraitResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<UpUserProtraitResult> response) {

			    UpUserProtraitResult result = response.body();
			    //上传图片到服务器，成功后通知融云服务器更新用户信息
			    if ("1".equals(result.getRc())) {

				    String portraitUri = result.getPortraitUri();

				    if (portraitUri == null) {
					    portraitUri = "http://www.birdcopy.com/img/logo.png";
				    }

				    //更新本地信息
				    FlyingDataManager.setPortraitUri(portraitUri);

				    refreshUesrInfo(account,
						    appID,
						    null,
						    portraitUri,
						    null,
						    new RefreshUesrInfoListener() {
							    @Override
							    public void completion(boolean isOK) {
								    delegate.completion(isOK);
							    }
						    }
				    );
			    } else {
				    if (delegate != null) {

					    delegate.completion(false);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {

				    delegate.completion(false);
			    }
		    }
	    });
    }

    public interface RefreshUesrInfoListener {

        void completion(final boolean isOK);
    }

    static public void refreshUesrInfo(final String account,
                                       String appID,
                                       final String nickName,
                                       final String portraitUri,
                                       final String br_intro,
                                       final RefreshUesrInfoListener delegate)
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);

	    if (nickName != null && !nickName.equalsIgnoreCase("")) {

		    map.put("name", nickName);
	    }

	    if (portraitUri != null && !portraitUri.equalsIgnoreCase("")) {

		    map.put("portrait_uri", portraitUri);
	    }

	    Call<CallResult> apiCall = birdCopyService.refreshUesrInfo(map);

	    apiCall.enqueue(new Callback<CallResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<CallResult> response) {

			    CallResult result = response.body();

			    if ("1".equals(result.getRc())) {

				    String rongID = ShareDefine.getMD5(account);
				    UserInfo userInfo = FlyingIMContext.getInstance().getUserInfoByRongId(rongID);

				    //更新本地信息
				    if (nickName != null) {
					    FlyingDataManager.setNickName(nickName);
					    userInfo.setName(nickName);
				    }

				    if (portraitUri != null) {
					    FlyingDataManager.setPortraitUri(portraitUri);
					    userInfo.setPortraitUri(Uri.parse(portraitUri));
				    }

				    //更新融云信息
				    FlyingIMContext.getInstance().addOrReplaceRongUserInfo(userInfo);
			    }

			    if (delegate != null) {
				    delegate.completion(true);
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });
    }

    //////////////////////////////////////////////////////////////
    //#pragma  用户注册、登录、激活相关
    //////////////////////////////////////////////////////////////

    public interface RegOpenUDIDListener {

        void completion(final boolean isOK);
    }

    static public void regOpenUDID(final String account,
                                   final String appID,
                                   final RegOpenUDIDListener delegate )
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("type", "reg");

	    Call<ResponseBody> apiCall = birdCopyService.regOpenUDID(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try{

				    String resultStr = response.body().string();

				    if (resultStr != null) {
					    if (resultStr.equals("0") || resultStr.equals("1")) {
						    if (delegate != null) {

							    delegate.completion(true);
						    }
					    } else {
						    if (delegate != null) {

							    delegate.completion(false);
						    }
					    }
				    }
				    else
				    {
					    if (delegate != null) {

						    delegate.completion(false);
					    }
				    }
			    }
			    catch (IOException e)
			    {
				    //
				    if (delegate != null) {

					    delegate.completion(false);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {

				    delegate.completion(false);
			    }
		    }
	    });

    }

    public interface VerifyOpenUDIDListener {

        void completion(final boolean isOK);
    }

    static public void verifyOpenUDID(String account,
                                   String appID,
                                   final VerifyOpenUDIDListener delegate )
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);

	    Call<CallResultCode> apiCall = birdCopyService.verifyOpenUDID(map);
	    apiCall.enqueue(new Callback<CallResultCode>() {
		    @Override
		    public void onResponse(retrofit2.Response<CallResultCode> response) {

			    CallResultCode result = response.body();

			    if ("1".equals(result.getRc()) &&
					    !"-1".equalsIgnoreCase(result.getRs())) {
				    if (delegate != null) {
					    delegate.completion(true);
				    }
			    }

		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });
    }

    //用终端登录官网后台
    public interface LoginWithQRListener {

        void completion(final boolean isOK);
    }

    static public void loginWithQR(String loginID,
                                   String account,
                                   String appID,
                                   final LoginWithQRListener delegate )
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("oth1", loginID);

	    Call<ResponseBody> apiCall = birdCopyService.loginWithQR(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try {
				    String resultStr = response.body().string();

				    if (resultStr.matches("[0-9]+")) {
					    // 登录成功
					    if (Integer.parseInt(resultStr) == 1) {
						    Toast.makeText(MyApplication.getInstance().getApplicationContext(), "扫描登录成功", Toast.LENGTH_SHORT).show();
					    }

					    if (delegate != null) {
						    delegate.completion(true);
					    }
				    }
			    } catch (IOException e) {
				    if (delegate != null) {
					    delegate.completion(false);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });
    }

    //pragma 购买行为,反馈信息在Activity
    public static void toBuyProduct(final Activity activity,
                                    final String account,
                                    final String appID,
                                    final Product good)
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key", account);
	    map.put("app_id", appID);

	    Call<GetOrderNoResult> apiCall = birdCopyService.getOrderNo(map);
	    apiCall.enqueue(new Callback<GetOrderNoResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<GetOrderNoResult> response) {

			    GetOrderNoResult result = response.body();

			    if ("1".equals(result.getRc())) {
				    // 产生个订单号
				    String orderNo = result.getOrderNo();

				    // 计算总金额（以分为单位）
				    int amount = good.productPrice * good.count;
				    JSONArray billList = new JSONArray();

				    billList.put(good.productName + " x " + good.count);

				    // 构建账单json对象
				    JSONObject bill = new JSONObject();
				    JSONObject displayItem = new JSONObject();
				    try {
					    displayItem.put("name", "商品");
					    displayItem.put("contents", billList);
					    JSONArray display = new JSONArray();
					    display.put(displayItem);

					    // 自定义的额外信息 选填
					    JSONObject extras = new JSONObject();
					    extras.put("subject", "商品");
					    extras.put("body", billList);
					    extras.put("tuser_key", account);
					    extras.put("app_id", appID);

					    bill.put("order_no", orderNo);
					    bill.put("amount", amount);
					    bill.put("display", display);
					    bill.put("extras", extras);// 该字段选填
				    } catch (JSONException ex) {
					    ex.printStackTrace();
				    }

				    String URL = ShareDefine.getPingplusOnePayURL();

				    // 发起支付
				    PayActivity.SHOW_CHANNEL_ALIPAY = true;
				    PayActivity.CallPayActivity(activity, bill.toString(), URL);
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

		    }
	    });
    }

    //////////////////////////////////////////////////////////////
    //#pragma  会员相关
    //////////////////////////////////////////////////////////////
    public static final String MEMBERSHIP_STARTTIME   = "membershipStartTime";
    public static final String MEMBERSHIP_ENDTIME   = "membershipEndTime";

    public interface GetMembershipListener {

        void completion(final Date startDate,final Date endDate);
    }

    static public void getMembership(String account,
                              String appID,
                              final GetMembershipListener delegate)
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key", account);
	    map.put("app_id", appID);
	    map.put("type", "validth");

	    Call<ResponseBody> apiCall = birdCopyService.getMembership(map);
		apiCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(retrofit2.Response<ResponseBody> response) {

				try {
					String resultStr = response.body().string();

					String[] separated = resultStr.split(";");
					if (separated.length==3) {
						String startTime = separated[0];
						String endTimeStr = separated[1];

						try {
							Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
							Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr);

							SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();

							//更新本地数据
							editor.putString(MEMBERSHIP_STARTTIME, startTime);
							editor.putString(MEMBERSHIP_ENDTIME, endTimeStr);

							editor.commit();

							if (delegate != null) {
								delegate.completion(startDate, endDate);
							}
						} catch (Exception exception) {
							//
						}
					}
				}
				catch (IOException e)
				{
					//
				}
			}

			@Override
			public void onFailure(Throwable t) {

			}
		});
    }

    public interface UpdateMembershipListener {

        void completion(final boolean isOK);
    }

    static public void updateMembership(String account,
                                           String appID,
                                           Date startDate,
                                           Date endDate,
                                           final UpdateMembershipListener delegate)
    {
        final String startDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
        final String endDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate);

        try
        {
            String startDateUTFStr= URLEncoder.encode(startDateStr, "UTF-8");
            String endDateUTFStr = URLEncoder.encode(endDateStr,"UTF-8");

	        Map<String,String> map=new HashMap<>();
	        map.put("tuser_key",account);
	        map.put("app_id", appID);
	        map.put("vthg_type", ShareDefine.KPAYCHANNEL_ALIPAY);
	        map.put("start_time", startDateUTFStr);
	        map.put("end_time", endDateUTFStr);

	        Call<CallResult> apiCall = birdCopyService.updateMembership(map);
	        apiCall.enqueue(new Callback<CallResult>() {
		        @Override
		        public void onResponse(retrofit2.Response<CallResult> response) {

			        CallResult result = response.body();

			        if ("1".equals(result.getRc())) {

				        //更新本地数据
				        SharedPreferences.Editor editor = MyApplication.getSharedPreference().edit();

				        editor.putString(MEMBERSHIP_STARTTIME, startDateStr);
				        editor.putString(MEMBERSHIP_ENDTIME, endDateStr);
				        editor.commit();

				        ShareDefine.broadUserDataChange();

				        if (delegate != null) {
					        delegate.completion(true);
				        }
			        } else {
				        if (delegate != null) {
					        delegate.completion(false);
				        }
			        }
		        }

		        @Override
		        public void onFailure(Throwable t) {

			        if (delegate != null) {
				        delegate.completion(false);
			        }
		        }
	        });
        }
        catch (Exception e)
        {
            //
            if(delegate!=null)
            {
                delegate.completion(false);
            }
        }
    }

    //////////////////////////////////////////////////////////////
    //#pragma  金币相关
    //////////////////////////////////////////////////////////////

    public interface GetMoneyDataListener {

        void completion(final boolean isOK);
    }

    static public void getMoneyData(final String account,
                                    final String appID,
                                    final GetMoneyDataListener delegate)
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("type", "accobk");

		Call<ResponseBody> apiCall = birdCopyService.getMoneyData(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try {
				    String resultStr = response.body().string();

				    String[] separated = resultStr.split(";");
				    if (separated.length == 4) {
					    int BEMONEYCOUNT = Integer.parseInt(separated[0]);
					    int BEGIFTCOUNT = Integer.parseInt(separated[1]);
					    int BETOUCHCOUNT = Integer.parseInt(separated[2]);
					    int BEQRCOUNT = Integer.parseInt(separated[3]);

					    //更新本地数据
					    BE_STATISTIC userData = new BE_STATISTIC();
					    userData.setBEUSERID(account);
					    userData.setBETIMES(0);
					    userData.setBETIMESTAMP(DateTools.getTime());
					    userData.setBEQRCOUNT(BEQRCOUNT);
					    userData.setBEMONEYCOUNT(BEMONEYCOUNT);
					    userData.setBETOUCHCOUNT(BETOUCHCOUNT);
					    userData.setBEGIFTCOUNT(BEGIFTCOUNT);

					    if (delegate != null) {
						    delegate.completion(false);
					    }
				    }

			    } catch (IOException e) {
				    //
				    if (delegate != null) {
					    delegate.completion(false);
				    }
			    }

		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });
    }

    public interface UploadMoneyDataListener {

        void completion(final boolean isOK);
    }

    static public void uploadMoneyData(String account,
                             String appID,
                             final UploadMoneyDataListener delegate) {

        BE_STATISTIC userData = new FlyingStatisticDAO().selectWithUserID(account);

	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("appletpp_sum",Integer.toString(userData.getBEMONEYCOUNT()));
	    map.put("reward_sum",Integer.toString(userData.getBEGIFTCOUNT()));
	    map.put("consume_sum", Integer.toString(userData.getBETOUCHCOUNT()));

	    Call<ResponseBody> apiCall = birdCopyService.uploadMoneyData(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try {
				    String resultStr = response.body().string();

				    if ("1".contentEquals(resultStr)) {

					    if (delegate != null) {
						    delegate.completion(true);
					    }
				    } else {

					    if (delegate != null) {
						    delegate.completion(false);
					    }
				    }

			    } catch (IOException e) {

				    if (delegate != null) {
					    delegate.completion(false);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });

    }

    //获取充值卡数据
    public interface GetQRDataListener {

        void completion(final boolean isOK);
    }

    static public void getQRData(final String account,
                                 final String appID,
                                 final GetQRDataListener delegate)
    {

	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("type", "topup_pwd_total");

	    Call<ResponseBody> apiCall = birdCopyService.getQRData(map);
		apiCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(retrofit2.Response<ResponseBody> response) {

				try {
					String resultStr = response.body().string();

					int resultCode = Integer.parseInt(resultStr);

					if (resultCode > 0) {
						FlyingStatisticDAO statisticDAO = new FlyingStatisticDAO();
						BE_STATISTIC userData = statisticDAO.selectWithUserID(account);
						userData.setBEQRCOUNT(resultCode);
						statisticDAO.saveStatic(userData);

						ShareDefine.broadUserDataChange();

						if (delegate != null) {
							delegate.completion(true);
						}
					}
				} catch (IOException e) {
					if (delegate != null) {
						delegate.completion(false);
					}
				}
			}

			@Override
			public void onFailure(Throwable t) {

				if (delegate != null) {
					delegate.completion(false);
				}
			}
		});
    }

    //向服务器帐户进行充值
    public interface ChargingCradListener {

        void completion(final String resultStr);
    }

    static public void chargingCrad(final String cardID,
                                    final String account,
                                    final String appID,
                                    final ChargingCradListener delegate)
    {

	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",account);
	    map.put("app_id", appID);
	    map.put("topup_pwd", cardID);

	    Call<ResponseBody> apiCall = birdCopyService.chargingCrad(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try {

				    String resultStr = response.body().string();

				    int resultCode = Integer.parseInt(resultStr);

				    String responseStr = "充值失败，请重试！";
				    int chargCode = 0;

				    FlyingStatisticDAO statisticDAO = new FlyingStatisticDAO();

				    switch (resultCode) {
					    case -1:
						    responseStr = "必须参数缺少";
						    break;
					    case -11:
						    responseStr = "充值卡无效";
						    break;
					    case -12:
						    responseStr = "充值卡无效";
						    break;
					    case -13:
						    responseStr = "充值卡无效";
						    break;
					    case -21:
						    responseStr = "充值卡无效";
						    break;
					    case -22:
						    responseStr = "充值卡未出售";
						    break;
					    case -23:
						    responseStr = "充值卡被锁定";
						    break;
					    case -24:
						    responseStr = "充值卡失效";
						    break;
					    case -31:
						    responseStr = "充值卡已充值";
						    break;
					    case -32:
						    responseStr = "充值卡已充值";
						    break;
					    case -99:
						    responseStr = "中途出错(系统原因)";
						    break;
					    default:

						    BE_STATISTIC userData = statisticDAO.selectWithUserID(account);
						    chargCode = resultCode - userData.getBEQRCOUNT();

						    userData.setBEQRCOUNT(resultCode);
						    statisticDAO.saveStatic(userData);
				    }

				    String showResult;

				    if (chargCode > 0) {
					    showResult = "你目前充值总额是:" + resultStr;
					    ShareDefine.broadUserDataChange();
				    } else {
					    showResult = "充值失败，请重试！" + " 原因：" + responseStr;
				    }

				    if (delegate != null) {
					    delegate.completion(showResult);
				    }

			    } catch (IOException e) {

			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			}
		});
    }

    //向服务器获课程统计数据
    public interface GetContentStatisticListener {

        void completion(final boolean isOK);
    }

    static public void getContentStatistic(String account,
                             String appID,
                             final GetContentStatisticListener delegate) {

        ArrayList<BE_PUB_LESSON> allData =(ArrayList<BE_PUB_LESSON>)new FlyingContentDAO().loadAllData();

        final String currentAccount=account;

        int i=0;

        for(final BE_PUB_LESSON lesson:allData)
        {
            //向服务器获取最新用户课程活跃统计数据
	        Map<String,String> map=new HashMap<>();
	        map.put("tuser_key",account);
	        map.put("app_id", appID);
	        map.put("type", "lnclick");
	        map.put("ln_id", lesson.getBELESSONID());

	        Call<ResponseBody> apiCall = birdCopyService.getContentStatistic(map);
	        apiCall.enqueue(new Callback<ResponseBody>() {
		        @Override
		        public void onResponse(retrofit2.Response<ResponseBody> response) {

			        try {
				        String resultStr = response.body().string();

				        BE_TOUCH_RECORD touchData = new BE_TOUCH_RECORD();
				        touchData.setBEUSERID(currentAccount);
				        touchData.setBELESSONID(lesson.getBELESSONID());
				        touchData.setBETOUCHTIMES(Integer.parseInt(resultStr));

				        new FlyingTouchDAO().savelTouch(touchData);
			        } catch (IOException e) {
			        }
		        }

		        @Override
		        public void onFailure(Throwable t) {

		        }
	        });

            i=i+1;
        }

        if (i==allData.size())
        {
            if(delegate!=null)
            {
                delegate.completion(true);
            }
        }
        else
        {
            if(delegate!=null)
            {
                delegate.completion(false);
            }
        }
    }

    public interface UploadContentStatisticListener {

        void completion(final boolean isOK);
    }

    static public void uploadContentStatistic(String account,
                                    String appID,
                                    final UploadContentStatisticListener delegate) {

        FlyingTouchDAO touchDAO = new FlyingTouchDAO();

        Boolean first=true;
        String updateStr="";
        for(BE_TOUCH_RECORD touchDat:touchDAO.selectWithUserID(account))
        {
            if (first) {

                updateStr=touchDat.getBELESSONID()+";"+touchDat.getBETOUCHTIMES();
                first=false;
            }
            else{
                updateStr=updateStr+"|"+touchDat.getBELESSONID()+";"+touchDat.getBETOUCHTIMES();
            }
        }

        if(!updateStr.equals(""))
        {
	        Map<String,String> map=new HashMap<>();
	        map.put("tuser_key",account);
	        map.put("app_id", appID);
	        map.put("lncks", updateStr);

	        Call<ResponseBody> apiCall = birdCopyService.uploadContentStatistic(map);
			apiCall.enqueue(new Callback<ResponseBody>() {
				@Override
				public void onResponse(retrofit2.Response<ResponseBody> response) {

					try {
						String resultStr = response.body().string();

						if ("1".contentEquals(resultStr)) {
							if (delegate != null) {
								delegate.completion(true);
							}
						} else {
							if (delegate != null) {
								delegate.completion(false);
							}
						}
					} catch (IOException e) {
						if (delegate != null) {
							delegate.completion(false);
						}
					}
				}

				@Override
				public void onFailure(Throwable t) {
					if (delegate != null) {
						delegate.completion(false);
					}
				}
			});
        }
    }


    //////////////////////////////////////////////////////////////
    //#pragma  内容相关
    //////////////////////////////////////////////////////////////

    public interface GetLessonDataListener {

        void completion(final ArrayList<BE_PUB_LESSON> lessonList,final String allRecordCount);
    }

    static public void getLessonData(String lessonID,
                                     final GetLessonDataListener delegate ) {
        String url = 
                FlyingDataManager.getServerNetAddress() +
                "/la_get_ln_detail_for_hp.action?ln_id=" +
                lessonID;

	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",FlyingDataManager.getCurrentPassport());
	    map.put("app_id", FlyingDataManager.getBirdcopyAppID());
	    map.put("ln_id", lessonID);

	    Call<ResponseBody> apiCall = birdCopyService.getLessonData(map);
		apiCall.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(retrofit2.Response<ResponseBody> response) {

				try {
					final String resultStr = response.body().string();

					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {

							try {

								FlyingContentParser.parser(resultStr);

								if (delegate != null) {
									delegate.completion(FlyingContentParser.resultList, FlyingContentParser.allRecordCount);
								}
							} catch (Exception ex) {
								//
							}
						}
					});
					thread.start();

				} catch (IOException e) {

				}
			}

			@Override
			public void onFailure(Throwable t) {

			}
		});
    }

    public interface GetLessonListListener {

        void completion(final ArrayList<BE_PUB_LESSON> lessonList,final String allRecordCount);
    }

    static public void getLessonList(String contentType,
                                     String downloadType,
                                     String tag,
                                     int pageNumber,
                                     boolean sortByTime,
                                     final GetLessonListListener delegate )
    {
	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",FlyingDataManager.getCurrentPassport());
	    map.put("app_id", FlyingDataManager.getBirdcopyAppID());

        if (sortByTime)
        {
	        try
	        {
		        map.put("sortindex", URLEncoder.encode("upd_time desc", "utf-8"));
	        } catch (Exception e) {
		        //
	        }
        }
        else
        {
	        map.put("sortindex", "upd_time");
        }

        if (contentType != null)
        {
	        map.put("res_type",contentType);
        }

        if (downloadType != null)
        {
	        map.put("url_2_type",downloadType);
        }
        if (tag != null)
        {
	        try {

		        map.put("ln_tag",URLEncoder.encode(tag, "utf-8"));

	        } catch (Exception e) {
		        //
	        }
        }

	    map.put("vc", "3");
	    map.put("perPageCount", ShareDefine.kperpageLessonCount);
	    map.put("page", Integer.toString(pageNumber));
	    map.put("ln_owner", FlyingDataManager.getLessonOwner());

	    Call<ResponseBody> apiCall = birdCopyService.getLessonList(map);
	    apiCall.enqueue(new Callback<ResponseBody>() {
		    @Override
		    public void onResponse(retrofit2.Response<ResponseBody> response) {

			    try
			    {
				    final String resultStr = response.body().string();
				    if (resultStr != null) {
					    Thread thread = new Thread(new Runnable() {
						    @Override
						    public void run() {

							    try {

								    FlyingContentParser.parser(resultStr);
							    } catch (Exception ex) {
								    //
								    Log.e("getCoverList", ex.getMessage());
							    }

							    if (delegate != null) {
								    delegate.completion(FlyingContentParser.resultList, FlyingContentParser.allRecordCount);
							    }
						    }
					    });
					    thread.start();
				    }
			    }
			    catch (IOException e)
			    {

			    }
			}

		    @Override
		    public void onFailure(Throwable t) {

		    }
	    });
    }

    public interface GetCoverListListener {

        void completion(final ArrayList<BE_PUB_LESSON> lessonList,final String allRecordCount);
    }

    static public void getCoverList(String author,
                                      int pageNumber,
                                      final GetCoverListListener delegate ) {

        String url =
                FlyingDataManager.getServerNetAddress() +
                "/la_get_ln_list_for_hp.action?vc=3&perPageCount=" +
                ShareDefine.kperpageCoverCount +
                "&page=" +
                pageNumber +
                "&ln_owner=" +
                author;

        url += "&owner_recom=1";

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

	                    final String resultStr =result.getResult();

	                    if (resultStr!=null)
                        {
                            Thread thread  = new Thread(new Runnable() {
	                            @Override
	                            public void run() {

		                            try {

			                            FlyingContentParser.parser(resultStr);

			                            if(delegate!=null)
			                            {
				                            delegate.completion(FlyingContentParser.resultList, FlyingContentParser.allRecordCount);
			                            }
		                            }
		                            catch (Exception ex)
		                            {
			                            //
			                            Log.e("getCoverList", ex.getMessage());
		                            }
	                            }
                            });
	                        thread.start();
                        }
                    }
                });
    }

    public interface GetAlbumListListener {

        void completion(final ArrayList<AlbumData> albumList,final String allRecordCount);
    }

    static public void getAlbumList(String lessonType,
                                    int pageNumber,
                                    boolean sortByTime,
                                    boolean homeRec,
                                    final GetAlbumListListener delegate ) {

        String sortBy;

        if (sortByTime) {

            sortBy = "upd_time desc";
        } else {
            sortBy = "upd_time";
        }

        try {
            sortBy = URLEncoder.encode(sortBy, "utf-8");
        } catch (Exception e) {
            //
        }

        if (lessonType == null) lessonType = "";

        String url =
                FlyingDataManager.getServerNetAddress() +
                "/la_get_tag_list_for_hp.action?perPageCount=" +
                ShareDefine.kperpageLessonCount +
                "&page=" +
                pageNumber +
                "&res_type=" +
                lessonType +
                "&tag_owner=" +
                FlyingDataManager.getLessonOwner();

        if (homeRec) {
            url += "&owner_recom=1";
        }

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {

	                    final String resultStr =result.getResult();

	                    if (resultStr!=null)
                        {
	                        Thread thread = new Thread(new Runnable() {
		                        @Override
		                        public void run() {

			                        try {

				                        FlyingAlbumParser.parser(resultStr);

				                        if(delegate!=null)
				                        {
					                        delegate.completion(FlyingAlbumParser.resultList, FlyingAlbumParser.allRecordCount);
				                        }
			                        }
			                        catch (Exception ex)
			                        {
				                        //
			                        }
		                        }
	                        });
	                        thread.start();
                        }
                    }
                });
    }

    //////////////////////////////////////////////////////////////
    //#pragma  内容的评论相关
    //////////////////////////////////////////////////////////////

    public interface GetCommentListListener {

        void completion(final ArrayList<FlyingCommentData> commentList,final String allRecordCount);
    }

    static public void getCommentList(String contentID,
                                      String contentType,
                                      int pageNumber,
                                      final GetCommentListListener delegate )
    {
        String sortBy="ins_time desc";

        try
        {
            sortBy=URLEncoder.encode(sortBy,"utf-8");
        }
        catch (Exception e)
        {
            //
        }

        String url = FlyingDataManager.getServerNetAddress()+
                "/tu_cm_get_ct_list_from_tn.action?perPageCount="+
                ShareDefine.kperpageLessonCount+
                "&page="+
                pageNumber+
                "&ct_id="+
                contentID+
                "&ct_type="+
                contentType+
                "&page="+
                pageNumber+
                "&sortindex="+
                sortBy;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {

	                    final String resultStr =result.getResult();

	                    if (resultStr!=null) {

		                    Thread thread = new Thread(new Runnable() {
			                    @Override
			                    public void run() {

				                    Gson gson = new Gson();
				                    java.lang.reflect.Type type = new TypeToken<CommentDataResult>() {
				                    }.getType();

				                    CommentDataResult commentDataResult = gson.fromJson(resultStr, type);

				                    if (delegate != null) {
					                    delegate.completion(commentDataResult.rs, commentDataResult.allRecordCount);
				                    }
			                    }
		                    });
		                    thread.start();
	                    }
                    }
                });
    }

    public interface UploadCommentListener {

        void completion(final boolean isOK);
    }

    static public void uploadComment(FlyingCommentData commentData,
                                      String appID,
                                      final UploadCommentListener delegate )
    {

	    Map<String,String> map=new HashMap<>();
	    map.put("tuser_key",commentData.userID);
	    map.put("app_id", appID);
	    map.put("ct_id", commentData.contentID);
	    map.put("ct_type", commentData.contentType);
	    map.put("name", commentData.nickName);
	    map.put("portrait_url", commentData.portraitURL);
	    map.put("content", commentData.commentContent);


	    Call<CallResult> apiCall = birdCopyService.uploadComment(map);
	    apiCall.enqueue(new Callback<CallResult>() {
		    @Override
		    public void onResponse(retrofit2.Response<CallResult> response) {

			    CallResult result = response.body();
			    if ("1".equals(result.getRc())) {

				    if (delegate != null) {
					    delegate.completion(true);
				    }
			    } else {
				    if (delegate != null) {
					    delegate.completion(false);
				    }
			    }
		    }

		    @Override
		    public void onFailure(Throwable t) {

			    if (delegate != null) {
				    delegate.completion(false);
			    }
		    }
	    });
    }

    public interface GetContentResourceListener {

        void completion(final String resultURL);
    }

    static public void getContentResource(String contentID,
                                         String resourceType,
                                          String appID,
                                         final GetContentResourceListener delegate) {

        String url = FlyingDataManager.getServerNetAddress()+
                "/la_get_ln_rel_url_for_hp.action?getType=url"+
                "&type="+
                resourceType+
                "&md5_value="+
                contentID+
                "&app_id="+
                appID;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded
                        String resultURL=null;

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            if(ShareDefine.checkURL(resultStr))
                            {
                                resultURL = resultStr;
                            }
                        }

                        if (delegate != null) {
                            delegate.completion(resultURL);
                        }
                    }
                });
    }


    //////////////////////////////////////////////////////////////
    //#pragma  普通下载文件功能
    //////////////////////////////////////////////////////////////
    public interface DownloadFileListener {

        void completion(final boolean isOK,final  String targetpath);
    }

    static public void downloadFile(String url,
                                    final String targetpath,
                                    final DownloadFileListener delegate)
    {

	    File targetFile = FlyingFileManager.getFile(targetpath);

        if(FlyingFileManager.fileExists(targetpath)){

	        if (delegate != null) {
		        delegate.completion(true, targetpath);
	        }
        }
        else
        {
	        Ion.with(MyApplication.getInstance().getApplicationContext())
			        .load(url)
			        .write(targetFile)
			        .setCallback(new FutureCallback<File>() {
				        @Override
				        public void onCompleted(Exception e, File file) {

					        boolean isOK = false;

					        if (file != null && file.exists()) {
						        isOK = true;
					        }

					        if (delegate != null) {
						        delegate.completion(isOK, targetpath);
					        }
				        }
			        });
        }
    }

    //////////////////////////////////////////////////////////////
    //#pragma  字典相关
    //////////////////////////////////////////////////////////////

    public interface GetShareBaseZIPURLListener {

        void completion(final String resultURL);
    }

    static public void getShareBaseZIPURL(String resourceType,
                                          String appID,
                                final GetShareBaseZIPURLListener delegate) {

        String url = FlyingDataManager.getServerNetAddress()+
                "/la_get_res_url_from_hp.action?type="+
                resourceType+
                "&app_id="+
                appID;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded
                        String resultURL=null;

                        if (result!=null)
                        {
                            String resultStr =result.getResult();

                            if(ShareDefine.checkURL(resultStr))
                            {
                                resultURL = resultStr;
                            }
                        }

                        if (delegate != null) {
                            delegate.completion(resultURL);
                        }
                    }
                });
    }

    public interface GetItemsListener {

        void completion(final boolean isOK);
    }

    static public void getItems(String word,
                                String appID,
                                final GetItemsListener delegate)
    {
        String url =  FlyingDataManager.getServerNetAddress()+
                "/la_get_dic_list_for_hp.action?word="+
                word+
                "&app_id="+
                appID;

        Ion.with(MyApplication.getInstance().getApplicationContext())
                .load(url)
                .noCache()
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result)
                    {
                        // print the response code, ie, 200
                        //System.out.println(result.getHeaders().getResponseCode());
                        // print the String that was downloaded

	                    final String resultStr =result.getResult();

	                    if (resultStr!=null)
                        {
							Thread thread = new Thread(new Runnable() {
								@Override
								public void run() {
									try
									{
										FlyingItemparser.parser(resultStr);

										for(FlyingItemData item:FlyingItemparser.resultList){

											new FlyingItemDAO().saveItem(item);
										}

										if(delegate!=null)
										{
											delegate.completion(true);
										}
									}
									catch (Exception exception)
									{
										if(delegate!=null)
										{
											delegate.completion(false);
										}
									}
								}
							});
	                        thread.start();
                        }
                        else
                        {
                            if(delegate!=null)
                            {
                                delegate.completion(false);
                            }
                        }

                    }
                });
    }

    //////////////////////////////////////////////////////////////
    //#pragma   辅助功能
    //////////////////////////////////////////////////////////////

    public static boolean checkNetWorkStatus() {

        Context context = MyApplication.getInstance().getBaseContext();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //check for wifi also
        WifiManager connec = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (cm != null) {
            NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();
            if (connec.isWifiEnabled()
                    && wifi.toString().equalsIgnoreCase("CONNECTED")) {
                return true;
            }
        }
        //check for network
        try {
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e("Network Avail Error", ex.getMessage());
        }
        return false;
    }

}
