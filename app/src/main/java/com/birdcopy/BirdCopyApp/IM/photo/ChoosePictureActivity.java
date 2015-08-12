package com.birdcopy.BirdCopyApp.IM.photo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.birdcopy.BirdCopyApp.R;


public class ChoosePictureActivity extends Activity implements ListImageDirPopupWindow.OnImageDirSelected, OnClickListener {
    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 最多图片路径的集合
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private MyAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir, tv_back, tv_save, tv_title;
    private TextView mImageCount;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            data2View();
            // initListDirPopupWindw();
            initListDirPopupWindwDefault();
            // 设置默认界面
            //设置默认弹出
            showDefaultSelect();

            // initListDirPopupWindw();
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            Toast.makeText(getApplicationContext(), "一张图片没扫描到",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                R.layout.de_ph_grid_item, mImgDir.getAbsolutePath(), getIntent()
                .getIntExtra("max", 9));
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(mImgs.size() + "张");
        mChooseDir.setText(mImgDir.getName());

//        if (mImageFloders.size()>0 && mImageFloders != null) {
//            for (int ) {
//            }
//        }

    };



    /**
     * 初始化展示文件夹的popupWindw
     */
    private void updateListDirPopupWindw() {

        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.de_ph_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindwDefault() {
        // - getStatusBarHeight()

        // 高度手动换算 44dp
        tv_titleHeight = ScreenSizeUtil.Dp2Px(this, 110);

        int dialogHeight = mScreenHeight - tv_titleHeight - mBottomLyHeight
                - getStatusBarHeight();

        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, dialogHeight, mImageFloders,
                LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.de_ph_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ph_choosepic);

        // 初始化屏幕数据
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

//        LogUtil.d("mScreenHeight   ===  " + mScreenHeight);

        // 初始化ui组件
        initView();
        // 获取images组件
        getImages();
        // 初始化响应事件
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_title.setVisibility(View.VISIBLE);
        tv_save.setVisibility(View.VISIBLE);
        tv_back.setVisibility(View.VISIBLE);
        tv_title.setText("");
        tv_back.setText("返回");
        tv_save.setText("发送");
        tv_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);
    }

    MineThread mThread;

    class MineThread extends Thread {

        @Override
        public void run() {

            String firstImage = null;

            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = ChoosePictureActivity.this
                    .getContentResolver();

            // 只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null ,MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[] { "image/jpeg", "image/png" },
                    MediaStore.Images.Media.DATE_ADDED +" DESC");

            Log.e("-----------------", mCursor.getCount() + "");
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));

                Log.e("-----------------", path);


                // 拿到第一张图片的路径
                if (firstImage == null)
                    firstImage = path;
                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();

                if (parentFile == null)
                    continue;
                String dirPath = parentFile.getAbsolutePath();
                ImageFloder imageFloder = null;
                // 利用一个HashSet防止多次扫描同一个文件jia
                if (mDirPaths.contains(dirPath)) {
                    continue;
                } else {
                    mDirPaths.add(dirPath);
                    // 初始化imageFloder
                    imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setFirstImagePath(path);
                }
                if(parentFile.list()==null)continue;
                int picSize = parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg")
                                || filename.endsWith(".png")
                                || filename.endsWith(".jpeg"))
                            return true;
                        return false;
                    }
                }).length;
                totalCount += picSize;

                imageFloder.setCount(picSize);
                mImageFloders.add(imageFloder);

                if (picSize > mPicsSize) {
                    mPicsSize = picSize;
                    mImgDir = parentFile;
                }
            }
            mCursor.close();
            mCursor = null;
            // 扫描完成，辅助的HashSet也就可以释放内存了
            mDirPaths = null;

            // 通知Handler扫描图片完成
            mHandler.sendEmptyMessage(0x110);

        }

    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        mThread = new MineThread();
        mThread.start();

    }

    private int mBottomLyHeight = 10;
    private int tv_titleHeight = 10;

    private LinearLayout ll_title_bar;

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        tv_back = (TextView) findViewById(R.id.title_bar_left);
        tv_save = (TextView) findViewById(R.id.title_bar_rigth);
        tv_title = (TextView) findViewById(R.id.title_bar_center);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        ll_title_bar = (LinearLayout) findViewById(R.id.ll_title_bar);

        ViewTreeObserver vto2 = mBottomLy.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBottomLy.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);

                mBottomLyHeight = mBottomLy.getHeight();
            }
        });

    }

    private void showDefaultSelect() {
        mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
        mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);

    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                updateListDirPopupWindw();
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }



    /**
     * 排序
     *
     *
     */
    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;// 最后修改的照片在后
            } else {
                return -1;
            }
        }

    }

    @Override
    public void selected(ImageFloder floder) {
//        ArrayList<File> mList = new ArrayList<File>();
//        mImgDir = new File(floder.getDir());
//
//        File[] imgfile = mImgDir.listFiles(filefiter);
//        int len = imgfile.length;
//        for (int i = 0; i < len; i++) {
//            mList.add(imgfile[i]);
//        }
//        Collections.sort(mList, new FileComparator());
//        List<String> nameList = new ArrayList<String>();
//
//        for (int i = 0; i < mList.size(); i++) {
//            String name = mList.get(i).getName();
//            nameList.add(name);
//
//        }
//        String[] nameArr = (String[]) nameList.toArray(new String[nameList
//                .size()]);
//
//        mImgs = Arrays.asList(nameArr);
        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));

        Collections.reverse(mImgs);


        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                R.layout.de_ph_grid_item, mImgDir.getAbsolutePath(), getIntent()
                .getIntExtra("max", 9));
        mGirdView.setAdapter(mAdapter);
        mGirdView.setSelection(0);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");

        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mThread = null;
        System.gc();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.title_bar_left) {finish();
        }else if(v.getId() == R.id.title_bar_rigth){
            Intent intent = new Intent();
            intent.putStringArrayListExtra("data", mAdapter.getPicList());
            setResult(10086, intent);
            finish();
        }
    }

}
