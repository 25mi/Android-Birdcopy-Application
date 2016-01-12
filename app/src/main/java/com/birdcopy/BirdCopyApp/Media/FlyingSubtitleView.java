package com.birdcopy.BirdCopyApp.Media;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.DataManager.FlyingItemDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingItemData;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vincentsung on 12/28/15.
 */
public class FlyingSubtitleView extends EditText
{
    //默认支持取词
    public boolean isSupportExtractWord = true;
    private boolean isLongPressState;

    public Activity activity;


    Context context;

    // ---------三个构造----------------------------------------------$构造
    // 当设置,指定样式时调用
    public FlyingSubtitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    // 布局文件初始化的时候,调用-------该构造方法,重用------------★
    // 布局文件里面定义的属性都放在 AttributeSet attrs
    public FlyingSubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    // 该方法,一般,在代码中 new 该类的时候_使用
    public FlyingSubtitleView(Context context) {
        super(context);
        initialize(context);
    }

    // --------------------------------------------------------------$初始
    private void initialize(Context context) {

        this.context = context;

        setGravity(Gravity.CENTER);
        //setBackgroundColor(Color.TRANSPARENT);// 背景透明-去掉底部输入框

        initMagnifier();
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        // 不做任何处理，为了阻止长按的时候弹出上下文菜单
    }


    private final int BEGINTOUCH = 1;
    private Handler mPressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //长按->初次启动--->显示放大镜&提词
                case BEGINTOUCH: {

                    if(onTouchListenerDelegate!=null)
                    {
                        onTouchListenerDelegate.onTouch();
                    }

                    isLongPressState = true;
                    Bundle data = msg.getData();
                    int X = data.getInt("X");
                    int RawX = data.getInt("RawX");
                    int Y = data.getInt("Y");
                    int RawY = data.getInt("RawY");
                    word = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), X, Y));
                    resBitmap = getBitmap(activity, RawX - WIDTH / 2, RawY - HEIGHT / 2, WIDTH, HEIGHT);
                    //放大镜-初次显示
                    calculate(RawX, RawY, MotionEvent.ACTION_MOVE);
                    break;
                }
            }
        }

    };

    private int mLastMotionX,
            mLastMotionY;
    // 是否移动了
    private boolean isMoved;
    // 移动的阈值
    private static final int TOUCH_SLOP = 20;

    private String word;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isSupportExtractWord)
            return super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                isMoved = false;

                Message message = mPressHandler != null ? mPressHandler.obtainMessage()
                        : new Message();
                //传对象,过去后,getRawY,不是相对的Y轴.
//                message.obj = event;
                Bundle bundle = new Bundle();
                bundle.putInt("X", (int) event.getX());
                bundle.putInt("RawX", (int) event.getRawX());
                bundle.putInt("Y", (int) event.getY());
                bundle.putInt("RawY", (int) event.getRawY());
                message.setData(bundle);
                message.what = BEGINTOUCH;
                mPressHandler.sendMessageDelayed(message, 500);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLongPressState)
                    if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                            || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                        //提词
                        Log.e("J", x + "-ET--move--ET-" + y + "cont:" + getEditableText().toString().charAt(0));
                        word = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), x, y));
                        //放大镜
                        resBitmap = getBitmap(activity, (int) event.getRawX() - WIDTH / 2, (int) event.getRawY() - HEIGHT / 2, WIDTH, HEIGHT);
                        calculate((int) event.getRawX(), (int) event.getRawY(), MotionEvent.ACTION_MOVE);
                        return true;
                    }
                if (isMoved && !isLongPressState)
                    break;
                //如果移动超过阈值
                if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                        || Math.abs(mLastMotionY - y) > TOUCH_SLOP)
                    //并且非长按状态下
                    if (!isLongPressState) {
                        // 则表示移动了
                        isMoved = true;
                        cleanLongPress();// 如果超出规定的移动范围--取消[长按事件]

                    }
                break;
            case MotionEvent.ACTION_UP:
                if (isLongPressState) {
                    //dis掉放大镜
                    removeCallbacks(showZoom);
                    //drawLayout();
                    popup.dismiss();

                    //TODO --单词pop
                    cleanLongPress();

                    if (!TextUtils.isEmpty(word) )
                        onLongPressWord(word);
                    break;
                }
                cleanLongPress();// 只要一抬起就释放[长按事件]
                break;
            case MotionEvent.ACTION_CANCEL:
                // 事件一取消也释放[长按事件],解决在ListView中滑动的时候长按事件的激活
                cleanLongPress();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    // 其实当前控件并没有获得焦点，我只是欺骗Android系统，让Android系统以我获得焦点的方式去处理
    // 用于将该控件Add到其他View下,导致失去焦点.
    @Override
    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return super.isFocused();// return true一定有焦点
    }

    private void cleanLongPress() {
        isLongPressState = false;
        mPressHandler.removeMessages(BEGINTOUCH);
    }

    private boolean calculate(int x, int y, int action) {
        dstPoint.set(x - WIDTH / 2, y - 3 * HEIGHT);
        if (y < 0) {
            // hide popup if out of bounds
            popup.dismiss();
            return true;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            removeCallbacks(showZoom);
            postDelayed(showZoom, DELAY_TIME);
        } else if (!popup.isShowing()) {
            showZoom.run();
        }
        popup.update(getLeft() + dstPoint.x, getTop() + dstPoint.y, -1, -1);
        magnifier.invalidate();
        return true;
    }
    // --------------------------------------------------------------$方法

    // 单词提取

    /**
     * @param layout
     * @param x      相对自己ev.getX()
     * @param y
     * @return
     */
    private int extractWordCurOff(Layout layout, int x, int y) {
        int line;
        line = layout
                .getLineForVertical(getScrollY() + y );
        int curOff = layout.getOffsetForHorizontal(line, x);
        return curOff;
    }

    private String getSelectWord(Editable content, int curOff) {
        String word = "";
        int start = getWordLeftIndex(content, curOff);
        int end = getWordRightIndex(content, curOff);
        if (start >= 0 && end >= 0) {
            word = content.subSequence(start, end).toString();
            if (!"".equals(word)) {
                // setFocusable(false);
                setFocusableInTouchMode(true);
                requestFocus();
                Selection.setSelection(content, start, end);// 设置当前具有焦点的文本字段的选择范围,当前文本必须具有焦点，否则此方法无效
            }
        }
        return word;
    }

    private int getWordLeftIndex(Editable content, int cur) {
        // --left
        String editableText = content.toString();// getText().toString();
        if (cur >= editableText.length())
            return cur;

        int temp = 0;
        if (cur >= 20)
            temp = cur - 20;
        Pattern pattern = Pattern.compile("[^'A-Za-z]");
        Matcher m = pattern.matcher(editableText.charAt(cur) + "");
        if (m.find())
            return cur;

        String text = editableText.subSequence(temp, cur).toString();
        int i = text.length() - 1;
        for (; i >= 0; i--) {
            Matcher mm = pattern.matcher(text.charAt(i) + "");
            if (mm.find())
                break;
        }
        int start = i + 1;
        start = cur - (text.length() - start);
        return start;
    }

    private int getWordRightIndex(Editable content, int cur) {
        // --right
        String editableText = content.toString();
        if (cur >= editableText.length())
            return cur;

        int templ = editableText.length();
        if (cur <= templ - 20)
            templ = cur + 20;
        Pattern pattern = Pattern.compile("[^'A-Za-z]");
        Matcher m = pattern.matcher(editableText.charAt(cur) + "");
        if (m.find())
            return cur;

        String text1 = editableText.subSequence(cur, templ).toString();
        int i = 0;
        for (; i < text1.length(); i++) {
            Matcher mm = pattern.matcher(text1.charAt(i) + "");
            if (mm.find())
                break;
        }
        int end = i;
        end = cur + end;
        return end;
    }

    // ----------------------------------------------------$放大镜

    View parentView;

    private PopupWindow popup;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 100;
    private static final long DELAY_TIME = 250;
    private Magnifier magnifier;

    public  void setParentView(View parentView)
    {
        this.parentView=parentView;
    }

    public View getMagnifView()
    {
        return parentView;
    }

    private void initMagnifier() {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);

        BitmapDrawable resDrawable = new BitmapDrawable(context.getResources(),bm);;
        resBitmap = resDrawable.getBitmap();

        magnifier = new Magnifier(context);

        //pop在宽高的基础上多加出边框的宽高
        popup = new PopupWindow(magnifier, WIDTH + 2, HEIGHT + 10);
        popup.setAnimationStyle(android.R.style.Animation_Toast);

        dstPoint = new Point(0, 0);
    }

    Runnable showZoom = new Runnable() {
        public void run() {
            popup.showAtLocation(getMagnifView(),
                    Gravity.NO_GRAVITY,
                    getLeft() + dstPoint.x,
                    getTop() + dstPoint.y);
        }
    };


    private Bitmap resBitmap;
    private Point dstPoint;

    class Magnifier extends View {
        private Paint mPaint;

        public Magnifier(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(0xff008000);
            mPaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            // draw popup
            mPaint.setAlpha(255);
            canvas.drawBitmap(resBitmap, 0, 0, mPaint);
            canvas.restore();

            //draw popup frame
            mPaint.reset();//重置
            mPaint.setColor(Color.LTGRAY);
            mPaint.setStyle(Paint.Style.STROKE);//设置空心
            mPaint.setStrokeWidth(2);
            Path path1 = new Path();
            path1.moveTo(0, 0);
            path1.lineTo(WIDTH, 0);
            path1.lineTo(WIDTH, HEIGHT);
            path1.lineTo(WIDTH / 2 + 15, HEIGHT);
            path1.lineTo(WIDTH / 2, HEIGHT + 10);
            path1.lineTo(WIDTH / 2 - 15, HEIGHT);
            path1.lineTo(0, HEIGHT);
            path1.close();//封闭
            canvas.drawPath(path1, mPaint);
        }
    }


    private Bitmap bitmap;//生成的位图
    //截图

    /**
     * @param activity
     * @param x        截图起始的横坐标
     * @param y        截图起始的纵坐标
     * @param width
     * @param height
     * @return
     */
    private Bitmap getBitmap(Activity activity, int x, int y, int width, int height) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        //边界处理,否则会崩滴
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (x + width > bitmap.getWidth()) {
//            x = x + WIDTH / 2;
//            width = bitmap.getWidth() - x;
            //保持不改变,截取图片宽高的原则
            x = bitmap.getWidth() - width;
        }
        if (y + height > bitmap.getHeight()) {
//            y = y + HEIGHT / 2;
//            height = bitmap.getHeight() - y;
            y = bitmap.getHeight() - height;
        }
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int toHeight = frame.top;
        bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }


    private void onLongPressWord( String oldWord) {


        final String word = oldWord.toLowerCase();

        if (!"".equals(word)) {

            String description = getDescription(word);

            if(description!=null && description.length()!=0)
            {

                Toast.makeText(context,description, Toast.LENGTH_LONG).show();
            }
            else
            {
                FlyingHttpTool.getItems(word, new FlyingHttpTool.GetItemsListener() {
                    @Override
                    public void completion(boolean isOK) {
                        if(isOK)
                        {
                            final String description = getDescription(word);

                            if(description!=null && description.length()!=0)
                            {
                                FlyingSubtitleView.this.post(new Runnable() {
	                                @Override
	                                public void run() {

		                                Toast.makeText(context, description, Toast.LENGTH_LONG).show();
	                                }
                                });
                            }
                        }
                    }
                });
            }
        }
        else {
            requestFocus();
            setFocusable(false);
            // ewe.setFocusableInTouchMode(false);
        }
    }

    private String getDescription(String word)
    {

        List<FlyingItemData>  itemList = new FlyingItemDAO().getItems(word);

        if (itemList.size()==1) {

            return descriptionOnly(itemList.get(0));
        }
        else{

            String result="";

            for(FlyingItemData item:itemList)
            {

                result = result+"\n"+descriptionOnly(item);
            }

            return result;
        }
    }

    private String descriptionOnly(FlyingItemData item)
    {

        String entry = item.getBEENTRY();

        String aStr = "<description>";
        String bStr = "</description>";

        int aRange = entry.indexOf(aStr);
        int bRange = entry.indexOf(bStr);

        if(aRange!=-1 && bRange!=-1)
        {
            return entry.substring(aRange+aStr.length(),bRange);
        }
        else
        {
            return "";
        }
    }

    OnTouchListener onTouchListenerDelegate=null;

    public interface OnTouchListener {

        void onTouch();
    }
}
