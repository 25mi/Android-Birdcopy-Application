package com.birdcopy.BirdCopyApp.IM;

/**
 * Created by birdcopy on 6/20/15.
 */

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContext;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.IM.photo.PhotoCollectionsProvider;
import com.birdcopy.BirdCopyApp.Content.WebViewActivity;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.Scan.BitmapToText;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AlterDialogFragment;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.VoIPInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.RealTimeLocationConstant;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationManager;
import io.rong.notification.PushNotificationMessage;

import com.birdcopy.BirdCopyApp.R;
import com.koushikdutta.ion.Ion;


/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 6、会话界面操作的监听器：ConversationBehaviorListener。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */
public final class RongCloudEvent implements RongIMClient.OnReceiveMessageListener, RongIM.OnSendMessageListener,
        RongIM.UserInfoProvider, RongIM.GroupInfoProvider, RongIM.ConversationBehaviorListener,
        RongIMClient.ConnectionStatusListener, RongIM.LocationProvider, RongIMClient.OnReceivePushMessageListener, RongIM.ConversationListBehaviorListener
{
    private static final String TAG = RongCloudEvent.class.getSimpleName();

    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        initDefaultListener();
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener()
    {
        RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
        RongIM.setGroupInfoProvider(this, true);//设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
        RongIM.setConversationListBehaviorListener(this);
        //消息体内是否有 userinfo 这个属性
//        RongIM.getInstance().setMessageAttachedUserInfo(true);
//        RongIM.getInstance().getRongIMClient().setOnReceivePushMessageListener(this);//自定义 push 通知。
    }

    /*
     * 连接成功注册。
     * <p/>
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);//设置消息接收监听器。
        RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);//设置连接状态监听器。

        //扩展功能自定义
        InputProvider.ExtendProvider[] provider = {
                new PhotoCollectionsProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new RealTimeLocationInputProvider(RongContext.getInstance()),//地理位置
                new VoIPInputProvider(RongContext.getInstance()),// 语音通话
                new ContactsProvider(RongContext.getInstance()),//通讯录
                new SurveyProvider(RongContext.getInstance())
        };

        InputProvider.ExtendProvider[] provider1 = {
                new PhotoCollectionsProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new RealTimeLocationInputProvider(RongContext.getInstance()),//地理位置
        };

        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, provider);

    }

    /**
     * 自定义 push 通知。
     *
     * @param msg
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage msg) {
        Log.d(TAG, "onReceived-onPushMessageArrive:" + msg.getContent());

        PushNotificationManager.getInstance().onReceivePush(msg);

//        Intent intent = new Intent();
//        Uri uri;
//
//        intent.setAction(Intent.ACTION_VIEW);
//
//        Conversation.ConversationType conversationType = msg.getConversationType();
//
//        uri = Uri.parse("rong://" + RongContext.getInstance().getPackageName()).buildUpon().appendPath("conversationlist").build();
//        intent.setData(uri);
//        Log.d(TAG, "onPushMessageArrive-url:" + uri.toString());
//
//        Notification notification=null;
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(RongContext.getInstance(), 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        if (android.os.Build.VERSION.SDK_INT < 11) {
//            notification = new Notification(RongContext.getInstance().getApplicationInfo().icon, "自定义 notification", System.currentTimeMillis());
//
//            notification.setLatestEventInfo(RongContext.getInstance(), "自定义 title", "这是 Content:"+msg.getObjectName(), pendingIntent);
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            notification.defaults = Notification.DEFAULT_SOUND;
//        } else {
//
//             notification = new Notification.Builder(RongContext.getInstance())
//                    .setLargeIcon(getAppIcon())
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setTicker("自定义 notification")
//                    .setContentTitle("自定义 title")
//                    .setContentText("这是 Content:"+msg.getObjectName())
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_ALL).build();
//
//        }
//
//        NotificationManager nm = (NotificationManager) RongContext.getInstance().getSystemService(RongContext.getInstance().NOTIFICATION_SERVICE);
//
//        nm.notify(0, notification);

        return true;
    }

    private Bitmap getAppIcon() {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo().loadIcon(RongContext.getInstance().getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {

        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage)
        {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            textMessage.getExtra();
            Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        }
        else if (messageContent instanceof ImageMessage)
        {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage)
        {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage)
        {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage)
        {//小灰条消息
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            Log.d(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
            //if (FlyingContext.getInstance() != null)
              //  getFriendByUserIdHttpRequest = FlyingContext.getInstance().getUserInfoByUserId(message.getSenderUserId(), (ApiCallback<User>) this);
        }
        else if (messageContent instanceof DeAgreedFriendRequestMessage)
        {//好友添加成功消息
            //DeAgreedFriendRequestMessage deAgreedFriendRequestMessage = (DeAgreedFriendRequestMessage) messageContent;
            //Log.d(TAG, "onReceived-deAgreedFriendRequestMessage:" + deAgreedFriendRequestMessage.getMessage());
            //receiveAgreeSuccess(deAgreedFriendRequestMessage);
        } else if (messageContent instanceof ContactNotificationMessage)
        {//好友添加消息
            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
            Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
            Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
//            RongIM.getInstance().getRongIMClient().deleteMessages(new int[]{message.getMessageId()});
//            if(FlyingContext.getInstance()!=null) {
//                RongIM.getInstance().getRongIMClient().removeConversation(Conversation.ConversationType.SYSTEM, "10000");
//                String targetname = FlyingContext.getInstance().getUserNameByUserId(contactContentMessage.getSourceUserId());
//                RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.SYSTEM, "10000", contactContentMessage.getSourceUserId(), contactContentMessage, null);
//
//            }

            Intent in = new Intent();
            in.setAction(MainActivity.ACTION_RONGCLOUD_RECEIVE_MESSAGE);
            in.putExtra("rongCloud", contactContentMessage);
            in.putExtra("has_message", true);
            mContext.sendBroadcast(in);
        } else {
            Log.d(TAG, "onReceived-其他消息，自己来判断处理");
        }

        return false;

    }

    /**
     * @param deAgreedFriendRequestMessage
     */
    private void receiveAgreeSuccess(DeAgreedFriendRequestMessage deAgreedFriendRequestMessage) {
//        if (DemoContext.getInstance() != null) {
//            if(deAgreedFriendRequestMessage.getUserInfo()!=null) {
//                if(DemoContext.getInstance().hasUserId(deAgreedFriendRequestMessage.getUserInfo().getUserId())){
//                    DemoContext.getInstance().updateUserInfos(deAgreedFriendRequestMessage.getUserInfo().getUserId(), "1");
//                }else{
//                    DemoContext.getInstance().insertOrReplaceUserInfo(deAgreedFriendRequestMessage.getUserInfo(), "1");
//                }
//
//            }
//        }

        //Intent in = new Intent();
        //in.setAction(MainActivity.ACTION_DMEO_AGREE_REQUEST);
        //in.putExtra("AGREE_REQUEST", true);
        //mContext.sendBroadcast(in);
    }

    @Override
    public Message onSend(Message message) {
        message.setExtra("my extra");
        Log.e("qinxiao", "onSend:" + message.getObjectName() + ", extra=" + message.getExtra());
        return message;
    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {


        if (message.getSentStatus() == Message.SentStatus.FAILED) {

            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {//不在聊天室

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {//不在讨论组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {//不在群组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {//你在他的黑名单中
                //WinToast.toast(mContext, "你在对方的黑名单中");
            }
        }


        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
        } else {
            Log.d(TAG, "onSent-其他消息，自己来判断处理");
        }
        return false;
    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */
    @Override
    public UserInfo getUserInfo(String userId) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        Log.e(TAG, "0604---------getUserInfo----userId---:" + userId);
        return FlyingContext.getInstance().getUserInfoByRongId(userId);
//        return new UserInfo("10000","新好友消息", Uri.parse("test"));
    }


    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public Group getGroupInfo(String groupId) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if (FlyingContext.getInstance().getGroupMap() == null)
            return null;

        return FlyingContext.getInstance().getGroupMap().get(groupId);
//        return null;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user)
    {
        Log.d(TAG, "onUserPortraitClick");

        String currentRongID= FlyingContext.getInstance().getSharedPreferences().getString("rongUserId","");

        if(conversationType==Conversation.ConversationType.CHATROOM||
                conversationType== Conversation.ConversationType.GROUP||
                conversationType== Conversation.ConversationType.DISCUSSION)
        {
            if (RongIM.getInstance() != null &&
                    user!=null &&
                    !user.getUserId().endsWith(currentRongID)
                    )
            {
                RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, user.getUserId(), user.getName());
            }
        }
        else
        {

            Toast.makeText(context, "查看用户详细信息－》下个版本推出", Toast.LENGTH_SHORT).show();
            /*
            Log.d("Begavior", conversationType.getName() + ":" + user.getName());
            Intent in = new Intent(context, DePersonalDetailActivity.class);
            in.putExtra("USER", user);
            in.putExtra("SEARCH_USERID", user.getUserId());
            context.startActivity(in);
            */
        }

        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {

        final  Context finalContext= context;
        final  Message finalMessage=message;

        Log.d(TAG, "onMessageClick");


            //real-time location message begin
            if (message.getContent() instanceof RealTimeLocationStartMessage) {
                RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

//            if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
//                startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
//            } else
                if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {


                    final AlterDialogFragment alterDialogFragment = AlterDialogFragment.newInstance("", "加入位置共享", "取消", "加入");
                    alterDialogFragment.setOnAlterDialogBtnListener(new AlterDialogFragment.AlterDialogBtnListener() {

                        @Override
                        public void onDialogPositiveClick(AlterDialogFragment dialog) {
                            RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(finalMessage.getConversationType(), finalMessage.getTargetId());

                            if (status == null || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
                                startRealTimeLocation(finalContext, finalMessage.getConversationType(), finalMessage.getTargetId());
                            } else {
                                joinRealTimeLocation(finalContext, finalMessage.getConversationType(), finalMessage.getTargetId());
                            }

                        }

                        @Override
                        public void onDialogNegativeClick(AlterDialogFragment dialog) {
                            alterDialogFragment.dismiss();
                        }
                    });

                    alterDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager());
                } else {

                    if (status != null && (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_OUTGOING || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_CONNECTED)) {

                        Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
                        intent.putExtra("conversationType", message.getConversationType().getValue());
                        intent.putExtra("targetId", message.getTargetId());
                        context.startActivity(intent);
                    }
                }
                return true;
            }


            //real-time location message end
            /**
             * demo 代码  开发者需替换成自己的代码。
             */
            if (message.getContent() instanceof LocationMessage) {
                Intent intent = new Intent(context, SOSOLocationActivity.class);
                intent.putExtra("location", message.getContent());
                context.startActivity(intent);
            }
         else if (message.getContent() instanceof RichContentMessage)
        {
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();

            String urlString =mRichContentMessage.getUrl();

            String lessonID = ShareDefine.getLessonIDFromOfficalURL(urlString);

            if (lessonID.length()!=0)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", lessonID);
                context.startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(context,WebViewActivity.class);
                intent.putExtra("url", urlString);
                context.startActivity(intent);
            }

            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());

        } else if (message.getContent() instanceof ImageMessage)
        {

            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Intent intent = new Intent(context, ShowPhotoActivity.class);

            intent.putExtra("photo", imageMessage.getRemoteUri());

            context.startActivity(intent);
        }

        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

        return true;
    }

    private void startRealTimeLocation(Context context, Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().startRealTimeLocation(conversationType, targetId);

        Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
        intent.putExtra("conversationType", conversationType.getValue());
        intent.putExtra("targetId", targetId);
        context.startActivity(intent);
    }

    private void joinRealTimeLocation(Context context, Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().joinRealTimeLocation(conversationType, targetId);

        Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
        intent.putExtra("conversationType", conversationType.getValue());
        intent.putExtra("targetId", targetId);
        context.startActivity(intent);
    }


    @Override
    public boolean onMessageLinkClick(Context var1, String var2)
    {
       /**
         * demo 代码  开发者需替换成自己的代码。
         */
        return false;
    }

    @Override
    public boolean onMessageLongClick(final Context context, View view, Message message) {

        if (message.getContent() instanceof LocationMessage)
        {
            Intent intent = new Intent(context, SOSOLocationActivity.class);
            intent.putExtra("location", message.getContent());
            context.startActivity(intent);

            return true;
        }
        else if (message.getContent() instanceof RichContentMessage)
        {
            /*
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();

            String urlString =mRichContentMessage.getUrl();

            String lessonID = ShareDefine.getLessonIDFromOfficalURL(urlString);

            if (lessonID.length()!=0)
            {
            Intent intent = new Intent(context, SOSOLocationActivity.class);
            intent.putExtra("location", message.getContent());
            context.startActivity(intent);

            }
            else
            {
                Intent intent = new Intent(context,WebViewActivity.class);
                intent.putExtra("url", urlString);
                context.startActivity(intent);
            }

            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
            */

            return false;
        }
        else if (message.getContent() instanceof ImageMessage)
        {
            final ImageMessage imageMessage = (ImageMessage) message.getContent();
            final Uri uri = imageMessage.getRemoteUri();

            int itmesRes= R.array.dealWtihPicWaysQR;

            new MaterialDialog.Builder(context)
                    .items(itmesRes)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/
                            switch (which) {
                                case 0:

                                {
                                    try {
                                        Bitmap bitmap = Ion.with(context)
                                                .load(uri.toString())
                                                .withBitmap()
                                                .asBitmap()
                                                .get();

                                        ShareDefine.savePhoto(bitmap, null);
                                        Toast.makeText(context, "已经成功保存图片", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (Exception e) {
                                        //
                                        System.out.println(e.getMessage());
                                    }

                                    break;
                                }

                                case 1:

                                    break;

                                case 2:
                                {
                                    try {

                                        Bitmap bitmap = Ion.with(context)
                                                .load(uri.toString())
                                                .withBitmap()
                                                .asBitmap()
                                                .get();

                                        String barcode = new BitmapToText(bitmap).getText();

                                        if (barcode != null) {
                                            dealWithScanString(context,barcode);
                                        }
                                        else
                                        {
                                            Toast.makeText(context, "没有什么解析结果！", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        //
                                        System.out.println(e.getMessage());
                                    }

                                    break;
                                }
                            }

                            return true;
                        }
                    })
                    .show();

            return true;
        }
        else if (message.getContent() instanceof TextMessage) {

            final TextMessage textMessage = (TextMessage) message.getContent();

            int itmesRes=R.array.dealWtihTextWays;

            new MaterialDialog.Builder(context)
                    .items(itmesRes)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/
                            switch (which)
                            {
                                case 0:

                                    ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData textCd = ClipData.newPlainText("clipboardText",textMessage.getContent());
                                    clipboard.setPrimaryClip(textCd);

                                    break;

                                case 1:

                                    break;
                            }

                            return true;
                        }
                    })
                    .show();

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        Log.d(TAG, "onChanged:" + status);
        if (status.getMessage().equals(ConnectionStatus.DISCONNECTED.getMessage())) {
        }
    }


    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context  上下文
     * @param callback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback callback) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        FlyingContext.getInstance().setLastLocationCallback(callback);
        context.startActivity(new Intent(context, SOSOLocationActivity.class));//SOSO地图
    }

    /**
     * 点击会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation conversation)
    {
        if (conversation.getConversationType()== Conversation.ConversationType.PRIVATE ||
                conversation.getConversationType()== Conversation.ConversationType.SYSTEM||
                conversation.getConversationType()== Conversation.ConversationType.CUSTOMER_SERVICE||
                conversation.getConversationType()== Conversation.ConversationType.APP_PUBLIC_SERVICE||
                conversation.getConversationType()== Conversation.ConversationType.PUBLIC_SERVICE)
        {

            MessageContent messageContent=conversation.getMessageContent();

            UserInfo userInfo =messageContent.getUserInfo();

            if (RongIM.getInstance() != null && userInfo!=null)
            {
                Intent intent = new Intent(context, com.birdcopy.BirdCopyApp.IM.FlyingConversationActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("ConversationType", "PRIVATE");
                intent.putExtra("title", userInfo.getName());
                intent.putExtra("targetId",userInfo.getUserId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(intent);

                //RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, user.getUserId(), user.getName());
            }
        }
        return true;
    }

    /**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
        return false;
    }

    public  void dealWithScanString(final Context context,String scanStr)
    {
        String type = ShareDefine.judgeScanType(scanStr);

        if (type.equals(ShareDefine.KQRTyepeWebURL))
        {
            String lessonID = ShareDefine.getLessonIDFromOfficalURL(scanStr);
            if (lessonID != null)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", lessonID);
                context.startActivity(intent);
            }
            else
            {
                Intent webAdvertisingActivityIntent = new Intent(context, WebViewActivity.class);
                webAdvertisingActivityIntent.putExtra("url", scanStr);
                context.startActivity(webAdvertisingActivityIntent);
            }
        }
        if (type.equals(ShareDefine.KQRTyepeChargeCard))
        {
            if (scanStr != null)
            {
                FlyingHttpTool.chargingCrad(FlyingDataManager.getCurrentPassport(),
                        ShareDefine.getLocalAppID(),
                        scanStr,
                        new FlyingHttpTool.ChargingCradListener() {
                            @Override
                            public void completion(String resultStr) {

                                Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }

        if (type.equals(ShareDefine.KQRTypeLogin))
        {
            if (scanStr != null)
            {

                String loginID = ShareDefine.getLoginIDFromQR(scanStr);

                if (loginID!=null) {

                    FlyingHttpTool.loginWithQR(loginID,
                            FlyingDataManager.getCurrentPassport(),
                            ShareDefine.getLocalAppID(),
                            new FlyingHttpTool.LoginWithQRListener() {
                        @Override
                        public void completion(boolean isOK) {

                            if (isOK)
                            {
                                Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }

        if (type.equals(ShareDefine.KQRTyepeCode))
        {
            if (scanStr != null)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", scanStr);
                context.startActivity(intent);
            }
        }
    }

}

