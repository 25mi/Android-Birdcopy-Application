package com.birdcopy.BirdCopyApp.Account;

import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.afollestad.materialdialogs.MaterialDialog;
import com.birdcopy.BirdCopyApp.DataManager.Product;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_STATISTIC;
import com.birdcopy.BirdCopyApp.DataManager.FlyingStatisticDAO;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by BirdCopyApp on 29/7/14.
 */
public class ProfileFragment extends Fragment {

    private View mProfileView;

    //头像、昵称相关
    private MyPortraitBroadcastReceiver mPortraitReceiver;

    private ImageView mUserCover;
    private TextView mCoverTitle;

    private TextView mNikeName;
    private Button mChangenameButton;

    //用户数据相关
    boolean mIsMembership=false;
    private MyBroadcastReceiver mReceiver;

    int buyCount = 0;
    int giftCount = 0;
    int usedCount = 0;

    //private TextView mBuyCoin;
    //private TextView mGiftCoin;
    //private TextView mUsedCoin;
    //private TextView mTotalCoin;

    //相关操作
    private Button mBuyButton;
    //private Button mScanButton;
    private Button mShareButton;

    // 存储路径
    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/DCIM";


    public ProfileFragment() {
        // Empty constructor required for fragment subclasses
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        initData();

        mPortraitReceiver = new MyPortraitBroadcastReceiver();
        IntentFilter portraitFilter = new IntentFilter();
        portraitFilter.addAction(ShareDefine.KMessagerPortrait);
        getActivity().registerReceiver(mReceiver, portraitFilter);

        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShareDefine.getKUSERDATA_CHNAGE_RECEIVER_ACTION());
        getActivity().registerReceiver(mReceiver, filter);
    }

    public void initData() {

        //获取会员金币数据
        FlyingHttpTool.getMoneyData(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                new FlyingHttpTool.GetMoneyDataListener() {
                    @Override
                    public void completion(boolean isOK) {

                        BE_STATISTIC data = new FlyingStatisticDAO().selectWithUserID(FlyingDataManager.getCurrentPassport());

                        if(data!=null)
                        {
                            buyCount = data.getBEMONEYCOUNT() + data.getBEQRCOUNT();
                            giftCount = data.getBEGIFTCOUNT();
                            usedCount = data.getBETOUCHCOUNT();
                        }
                    }
                });

        //获取年费会员数据
        FlyingHttpTool.getMembership(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                new FlyingHttpTool.GetMembershipListener() {
                    @Override
                    public void completion(Date startDate, Date endDate) {

                        if (endDate.after(new Date())) {
                            mIsMembership = true;

                        } else {
                            mIsMembership = false;
                        }

                        initStaticViewAndMembership();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mProfileView = (View) inflater.inflate(R.layout.profile, container, false);

        //头像
        mUserCover = (ImageView) mProfileView.findViewById(R.id.userheadimage);
        mUserCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getActivity())
                        .title("选择照片来源")
                        .items(R.array.chooseCameralOrAlbum)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/

                                if (which == 0) {
                                    camera();
                                } else {
                                    pickPhoto();
                                }
                                return true;
                            }
                        })
                        .backgroundColorRes(R.color.background_material_light)
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();

            }
        });
        initUserCover();

        mCoverTitle = (TextView) mProfileView.findViewById(R.id.userheadalert);

        //昵称显示
        mNikeName = (TextView) mProfileView.findViewById(R.id.usernikename);
        mNikeName.setText(FlyingDataManager.getNickName());

        //昵称修改
        mChangenameButton = (Button) mProfileView.findViewById(R.id.changeNanmeButton);
        mChangenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });

        //统计图
        //View staticView = (View) mProfileView.findViewById(R.id.profilestatic);
        //staticView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_light);

        //相关按钮
        mBuyButton = (Button) mProfileView.findViewById(R.id.buycoinbutton);
        initStaticViewAndMembership();

        mShareButton = (Button) mProfileView.findViewById(R.id.sharecoinbutton);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShareApp();
            }
        });

        return mProfileView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void initUserCover()
    {
        String portraitUri = FlyingDataManager.getPortraitUri();

        if (portraitUri != null && portraitUri.contains("http"))
        {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(portraitUri, mUserCover);
        }
        else
        {
            mUserCover.setImageResource(R.drawable.default_head);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(portraitUri, mUserCover);
        }
    }

    private void initStaticViewAndMembership()
    {
        if(mBuyButton!=null)
        {
            if(mIsMembership)
            {
                mBuyButton.setText("你是年费会员");
                mBuyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getActivity(), "你已经是年费会员！测试！！！！！！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                mBuyButton.setText("现在购买会员");
                mBuyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toBuyMember();
                    }
                });
            }
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: React to the Intent received.
            initData();
            initStaticViewAndMembership();
        }
    }

    public class MyPortraitBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: React to the Intent received.
            initUserCover();
        }
    }

    private void changeName()
    {

        String nickName = FlyingDataManager.getNickName();

        if(nickName==null || (nickName!=null && nickName.length()==1))
        {
            nickName="请输入昵称";
        }

        new MaterialDialog.Builder(getActivity())
                .title("请输入新的昵称")
                .content("昵称")
                .inputType(InputType.TYPE_CLASS_TEXT )
                .input("", nickName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                        String nickName = input.toString();
                        FlyingDataManager.setNickName(nickName);
                        mNikeName.setText(nickName);

                        FlyingHttpTool.refreshUesrInfo(FlyingDataManager.getCurrentPassport(),
                                FlyingDataManager.getBirdcopyAppID(),
                                nickName,
                                null,
                                null,
                                new FlyingHttpTool.RefreshUesrInfoListener() {
                                    @Override
                                    public void completion(boolean isOK) {
                                        //
                                        mNikeName.setText(FlyingDataManager.getNickName());
                                    }
                                });
                    }
                })
                .positiveText("确定")
                .negativeText("取消").show();
    }

    private void toBuyMember()
    {

        Product good =new Product("年费会员",ShareDefine.KPricePerYear,1);

        MainActivity mainActivity = (MainActivity) getActivity();
        FlyingHttpTool.toBuyProduct(mainActivity,
                FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                good);
    }

    private void toScanCoin() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.scanNow();
    }

    private void toShareApp() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.shareMyApp();
    }

    public void camera() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);

        Uri imageUri = Uri.fromFile(new File(PATH, "portraitImage.jpg"));
        //下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, ShareDefine.CAMERA_WITH_DATA_REQUEST);
    }

    public void pickPhoto() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ShareDefine.PHOTO_WITH_DATA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case ShareDefine.CAMERA_WITH_DATA_REQUEST:

                if (resultCode==-1)
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(PATH + "/" + "portraitImage.jpg");

                    // 获取屏幕分辨率
                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

                    // 图片分辨率与屏幕分辨率
                    float scale = bitmap.getWidth() / (float) dm.widthPixels;

                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap newBitmap = zoomBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上
                    mUserCover.setImageBitmap(newBitmap);
                    savePhotoTOLocal(newBitmap);

                    uploadPortImage(new File(PATH, "portraitImage.jpg"));
                }

                break;

            case ShareDefine.PHOTO_WITH_DATA_REQUEST: {

                if (resultCode == -1) {

                    ContentResolver resolver = getActivity().getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                        // 获取屏幕分辨率
                        DisplayMetrics dm = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

                        float scale = photo.getWidth() / (float) dm.widthPixels;

                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / scale, photo.getHeight() / scale);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            mUserCover.setImageBitmap(smallBitmap);
                            savePhotoTOLocal(smallBitmap);

                            uploadPortImage(new File(PATH, "portraitImage.jpg"));
                        }
                    }
                    catch (FileNotFoundException e) {
                        //
                   }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public void uploadPortImage(File portraitFile)
    {
        FlyingHttpTool.requestUploadPotrait(FlyingDataManager.getCurrentPassport(),
                FlyingDataManager.getBirdcopyAppID(),
                portraitFile,
                new FlyingHttpTool.RequestUploadPotraitListener() {
                    @Override
                    public void completion(boolean isOK) {

                        if(isOK)
                        {
                            initUserCover();
                        }
                    }
                });
    }

    // 对分辨率较大的图片进行缩放
    public Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {

        int w = bitmap.getWidth();

        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();

        float scaleWidth = ((float) width / w);

        float scaleHeight = ((float) height / h);

        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出

        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

        return newbmp;

    }

    private static void savePhotoTOLocal(Bitmap bitmap)
    {
        File imageFile = new File(PATH, "portraitImage.jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        }
        catch (Exception e)
        {
            //Log.i("Error writing bitmap", e);
        }
    }
}
