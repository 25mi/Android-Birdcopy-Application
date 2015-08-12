package com.birdcopy.BirdCopyApp.IM.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

public class PhotoCollectionsProvider extends InputProvider.ExtendProvider {
    HandlerThread mWorkThread;

    Handler mUploadHandler;

    private RongContext mContext;

    public PhotoCollectionsProvider(RongContext context) {
        super(context);
        this.mContext = context;
        mWorkThread = new HandlerThread("RongDemo");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());

    }

    @Override
    public Drawable obtainPluginDrawable(Context arg0) {
        // TODO Auto-generated method stub
        return arg0.getResources().getDrawable(R.drawable.rc_ic_picture);
    }

    @Override
    public CharSequence obtainPluginTitle(Context arg0) {
        return "相册";
    }

    @Override
    public void onPluginClick(View arg0) {
        // TODO Auto-generated method stub
        // 点击跳转至图片选择界面

        Intent intent = new Intent(mContext, ChoosePictureActivity.class);
        intent.putExtra("max", 9);
        startActivityForResult(intent, 86);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 根据选择完毕的图片返回值，直接上传文件
        if (requestCode == 86 && data != null) {

            ArrayList<String> pathList = data.getStringArrayListExtra("data");
            if (pathList != null && pathList.size() > 0) {
                int intSize = pathList.size();
                for (int i = 0; i <= intSize - 1; i++) {
                    String localStrPath = pathList.get(i);
//					localStrPath = "file:/" + localStrPath;
                    byte[] compressBitmap = BitmapUtils.compressBitmap(480*480,
                            localStrPath);
                    if (null != compressBitmap) {
                        Bitmap bmPhoto = BitmapUtils
                                .Bytes2Bimap(compressBitmap);
                        if (null != bmPhoto) {
                            String strTempPhotoPath;
                            try {
                                strTempPhotoPath = BitmapUtils
                                        .saveFile(bmPhoto,
                                                UUID.randomUUID()
                                                        + ".jpeg");
                                if(bmPhoto != null){
                                    bmPhoto.recycle();
                                    bmPhoto = null;
                                }
                                if (null != strTempPhotoPath
                                        && !"".equals(strTempPhotoPath)) {
                                    localStrPath = strTempPhotoPath;
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        }
                    }
                    localStrPath = "file://" + localStrPath;
                    Uri pathUri = Uri.parse(localStrPath);
                    mUploadHandler.post(new MyRunnable(pathUri));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 用于显示文件的异步线程
     *
     * @ClassName: MyRunnable
     * @Description: 用于显示文件的异步线程
     *
     */
    class MyRunnable implements Runnable {

        Uri mUri;

        public MyRunnable(Uri uri) {
            mUri = uri;
        }

        @Override
        public void run() {

            // 封装image类型的IM消息
            final ImageMessage content = ImageMessage.obtain(mUri, mUri);

            if (RongIM.getInstance() != null&& RongIM.getInstance().getRongIMClient() != null)
                RongIM.getInstance().getRongIMClient().sendImageMessage(getCurrentConversation().getConversationType(),getCurrentConversation().getTargetId(),content,null,null,new RongIMClient.SendImageMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode code) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onProgress(Message message, int progress) {

                    }
                });

        }
    }


}
