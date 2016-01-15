package com.birdcopy.BirdCopyApp.Media;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.DataManager.FlyingItemDAO;
import com.birdcopy.BirdCopyApp.DataManager.FlyingItemData;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;

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
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// 不做任何处理，为了阻止长按的时候弹出上下文菜单
	}


	private final int BEGINTOUCH = 1;


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

				if(onTouchListenerDelegate!=null)
				{
					onTouchListenerDelegate.onTouch();
				}

				isLongPressState = true;
				int X =(int) event.getX();
				int Y =  (int) event.getY();
				word = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), X, Y));

				break;
			case MotionEvent.ACTION_MOVE:
				if (isLongPressState)
					if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
							|| Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
						//提词
						Log.e("J", x + "-ET--move--ET-" + y + "cont:" + getEditableText().toString().charAt(0));
						word = getSelectWord(getEditableText(), extractWordCurOff(getLayout(), x, y));
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


	private void onLongPressWord( String oldWord)
	{

		requestFocus();
		setFocusable(false);

		final String word = oldWord.toLowerCase();

		if (!"".equals(word)) {

			String description = getDescription(word);

			if(description!=null && description.length()!=0)
			{
				showPopWordView(description);

				return;
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

								showPopWordView(description);
							}
							else
							{
								showPopWordView("抱歉，目前正在增加词库中...");
							}
						}
					}
				});

			}
		}

		onTouchListenerDelegate.showWordMessageOver();
	}

	public void  showPopWordView(final String description)
	{

		FlyingSubtitleView.this.post(new Runnable() {
			@Override
			public void run() {

				Toast.makeText(context, description, Toast.LENGTH_LONG).show();
			}
		});

		if(onTouchListenerDelegate!=null)
		{
			new Handler().postDelayed(new Runnable() {
				public void run() {
					//execute the task
					onTouchListenerDelegate.showWordMessageOver();
				}
			}, 4000);
		}
	}

	private String getDescription(String word)
	{

		List<FlyingItemData>  itemList = new FlyingItemDAO().getItems(word);

		if (itemList.size()==1) {

			return descriptionOnly(itemList.get(0));
		}
		if (itemList.size()>1) {

			String result="";

			for(FlyingItemData item:itemList)
			{

				result = result+"\n"+descriptionOnly(item);
			}

			return result;
		}
		else{

			return null;
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
		void showWordMessageOver();
	}
}
