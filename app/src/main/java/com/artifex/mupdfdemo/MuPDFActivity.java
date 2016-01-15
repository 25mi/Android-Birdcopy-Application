package com.artifex.mupdfdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;
import com.birdcopy.BirdCopyApp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//TODO: remove preffix mXXXX from all properties this class
public class MuPDFActivity extends FragmentActivity {
	private static final String TAG = "MuPDFActivity";

//	private static final int SEARCH_PROGRESS_DELAY = 200;
	private static final int WAIT_DIALOG = 0;
	private static final String FILE_NAME = "FileName";

    private static final int START_BILLING_ACTIVITY = 100;
    private static final int START_OUTLINE_ACTIVITY = 101;

	public static final String SHOW_THUMBNAILS_EXTRA = "show_thumbnails";
	public static final String PREF_SAVED_ORIENTATION = "orientation";
	public static final String PREF_BUTTONS_HIDDEN = "ButtonsHidden";

	private MuPDFCore core;
	private String fileName;
	private int mOrientation;

//	private int          mPageSliderRes;
	private boolean      buttonsVisible;
	private boolean      mTopBarIsSearch;

//	private WeakReference<SearchTask> searchTask;
	private ProgressDialog dialog;

	private AlertDialog.Builder alertBuilder;
	private ReaderView   docView;
	private View         buttonsView;
	private EditText     mPasswordView;

	private TextView   mFilenameView;
	private ImageView  mCloseButton;
	private ImageView  mAudioButton;

	private ViewSwitcher mTopBarSwitcher;
	//private ImageButton  mSearchBack;
	//private ImageButton  mSearchFwd;
	//private EditText     mSearchText;
	//private SearchTaskResult mSearchTaskResult;
	//private final Handler mHandler = new Handler();
	private FrameLayout mPreviewBarHolder;
	private RecyclerView mPreview;
	private PDFPreviewPagerAdapter pdfPreviewPagerAdapter;
	private MuPDFPageAdapter mDocViewAdapter;
	private SparseArray<LinkInfoExternal[]> linkOfDocument;
    private LinearLayoutManager listLayoutManager;
    private int currentlyViewing;

    @Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		alertBuilder = new AlertDialog.Builder(this);
	
		core = getMuPdfCore(savedInstanceState);
	
		if (core == null) {
			return;
		}
	
		mOrientation = getResources().getConfiguration().orientation;

		if(mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			core.setDisplayPages(2);
		} else {
			core.setDisplayPages(1);
		}

		createUI(savedInstanceState);

		playBackgroundIfPossible();
	}
	
	private void requestPassword(final Bundle savedInstanceState) {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = alertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (core.authenticatePassword(mPasswordView.getText().toString())) {
					createUI(savedInstanceState);
				} else {
					requestPassword(savedInstanceState);
				}
			}
		});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert.show();
	}

	private MuPDFCore getMuPdfCore(Bundle savedInstanceState) {
		MuPDFCore core = null;
		if (core == null) {
			core = (MuPDFCore)getLastCustomNonConfigurationInstance();

			if (savedInstanceState != null && savedInstanceState.containsKey(FILE_NAME)) {
				fileName = savedInstanceState.getString(FILE_NAME);
			}
		}
		if (core == null) {
			Intent intent = getIntent();
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				core = openFile(Uri.decode(uri.getEncodedPath()));
				SearchTaskResult.recycle();
			}
			if (core != null && core.needsPassword()) {
				requestPassword(savedInstanceState);
				return null;
			}
		}
		if (core == null) {
			AlertDialog alert = alertBuilder.create();
			
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return null;
		}
		return core;
	}

	private void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;
		// Now create the UI.
		// First create the document view making use of the ReaderView's internal
		// gesture recognition
		docView = new DocumentReaderView(this, linkOfDocument) {
			ActivateAutoLinks mLinksActivator = null;
			
			@Override
			protected void onMoveToChild(View view, int i) {
				Log.d(TAG,"onMoveToChild id = "+i);

//				if(core.getDisplayPages() == 1)
//					mPreview.scrollToPosition(i);
//				else
//					mPreview.scrollToPosition(((i == 0) ? 0 : i * 2 - 1));

				if (core == null){
					return;
				} 
				MuPDFPageView pageView = (MuPDFPageView) docView.getDisplayedView();
				if(pageView!=null){
					pageView.cleanRunningLinkList();
				}
				super.onMoveToChild(view, i);
				if(mLinksActivator != null)
					mLinksActivator.cancel(true);
				mLinksActivator = new ActivateAutoLinks(pageView);
				mLinksActivator.safeExecute(i);
				setCurrentlyViewedPreview();
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (!isShowButtonsDisabled()) {
					hideButtons();
				}
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			protected void onContextMenuClick() {
				if (!buttonsVisible) {
					showButtons();
				} else {
					hideButtons();
				}
			}

			@Override
			protected void onBuy(String path) {
				MuPDFActivity.this.onBuy(path);
			}
		};
		mDocViewAdapter = new MuPDFPageAdapter(this, core);
		docView.setAdapter(mDocViewAdapter);

		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		// Set the magazine title text
		String title = getIntent().getStringExtra(ShareDefine.KLessonTitle);
		if (title != null) {
			mFilenameView.setText(title);
		} else {
			mFilenameView.setText(fileName);
		}

		/*
		if (core.hasOutline()) {
			mOutlineButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OutlineItem outline[] = core.getOutline();
					if (outline != null) {
						OutlineActivityData.get().items = outline;
						Intent intent = new Intent(MuPDFActivity.this, OutlineActivity.class);
						startActivityForResult(intent, START_OUTLINE_ACTIVITY);
					}
				}
			});
		} else {
			mOutlineButton.setVisibility(View.GONE);
		}

		*/

		// Reinstate last state if it was recorded
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		int orientation = prefs.getInt(PREF_SAVED_ORIENTATION, mOrientation);
		int pageNum = prefs.getInt("page"+fileName, 0);
		if(orientation == mOrientation)
			docView.setDisplayedViewIndex(pageNum);
		else {
			if(orientation == Configuration.ORIENTATION_PORTRAIT) {
				docView.setDisplayedViewIndex((pageNum + 1) / 2);
			} else {
				docView.setDisplayedViewIndex((pageNum == 0) ? 0 : pageNum * 2 - 1);
			}
		}

		// Give preview thumbnails time to appear before showing bottom bar
		if (savedInstanceState == null
				|| !savedInstanceState.getBoolean(PREF_BUTTONS_HIDDEN, false)) {
			mPreview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showButtons();
                        }
                    });
                }
            }, 100);
		}

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(docView);
		layout.addView(buttonsView);
//		layout.setBackgroundResource(R.drawable.tiled_background);
		//layout.setBackgroundResource(R.color.canvas);
		layout.setBackgroundColor(Color.BLACK);
		setContentView(layout);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_BILLING_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
		if (requestCode == START_OUTLINE_ACTIVITY && resultCode >= 0) {
			if (core.getDisplayPages() == 2) {
				resultCode = (resultCode + 1) / 2;
			}
			docView.setDisplayedViewIndex(resultCode);
        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (fileName != null && docView != null) {
			outState.putString("FileName", fileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+fileName, docView.getDisplayedViewIndex());
			edit.putInt(PREF_SAVED_ORIENTATION, mOrientation);
			edit.commit();
		}

		if (!buttonsVisible)
			outState.putBoolean(PREF_BUTTONS_HIDDEN, true);

//		if (mTopBarIsSearch)
//			outState.putBoolean("SearchMode", true);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//killSearch();

		if (fileName != null && docView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+fileName, docView.getDisplayedViewIndex());
			edit.putInt(PREF_SAVED_ORIENTATION, mOrientation);
			edit.commit();
		}
	}
	
	@Override
	public void onDestroy() {
		if (core != null) {
			core.onDestroy();
		}
		core = null;

		super.onDestroy();
	}

	void showButtons() {
		if (core == null) {
			return;
		}
		
		if (!buttonsVisible) {
            buttonsView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			buttonsVisible = true;
			// Update page number text and slider
			final int index = docView.getDisplayedViewIndex();
//			mPageSlider.setMax((core.countPages()-1)*mPageSliderRes);
//			mPageSlider.setProgress(index*mPageSliderRes);
			if (mTopBarIsSearch) {
				//mSearchText.requestFocus();
				//showKeyboard();
			}

			Animation anim = new TranslateAnimation(0, 0, -mTopBarSwitcher.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mTopBarSwitcher.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {}
			});
			mTopBarSwitcher.startAnimation(anim);
			
			// Don't show thumbnail if not requested
			if (getIntent() != null && !getIntent().getBooleanExtra(SHOW_THUMBNAILS_EXTRA, true)) {
				return;
			}

			// Update listView position
            centerPreviewAtPosition(currentlyViewing);
			anim = new TranslateAnimation(0, 0, mPreviewBarHolder.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPreviewBarHolder.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
//					int page = docView.getCurrentPage();
//					if(core.getDisplayPages() == 1)
//						mPreview.scrollToPosition(docView.getCurrentPage());
//					else
//						mPreview.scrollToPosition((page == 0) ? 0 : page * 2 - 1);
				}
			});
			mPreviewBarHolder.startAnimation(anim);
		}
	}

	void hideButtons() {
		if (buttonsVisible) {
            buttonsView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			buttonsVisible = false;
			//hideKeyboard();

			Animation anim = new TranslateAnimation(0, 0, 0, -mTopBarSwitcher.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mTopBarSwitcher.setVisibility(View.INVISIBLE);
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			// Don't show thumbnail if not requested
			if (getIntent() != null && !getIntent().getBooleanExtra(SHOW_THUMBNAILS_EXTRA, true)) {
				return;
			}

			anim = new TranslateAnimation(0, 0, 0, this.mPreviewBarHolder.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPreviewBarHolder.setVisibility(View.INVISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
				}
			});
			mPreviewBarHolder.startAnimation(anim);
		}
	}

	void makeButtonsView()
	{
		buttonsView = getLayoutInflater().inflate(R.layout.pdf_head,null);
		mFilenameView = (TextView)buttonsView.findViewById(R.id.lesson_top_title);
		mPreviewBarHolder = (FrameLayout) buttonsView.findViewById(R.id.PreviewBarHolder);
		mPreview = new RecyclerView(this);
        listLayoutManager = new LinearLayoutManager(this);
        listLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPreview.setLayoutManager(listLayoutManager);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
				.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mPreview.setLayoutParams(lp);
		pdfPreviewPagerAdapter = new PDFPreviewPagerAdapter(this, core);
		mPreview.setAdapter(pdfPreviewPagerAdapter);

        mPreview.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        hideButtons();
                        docView.setDisplayedViewIndex(position);
                    }
                })
        );

		mPreviewBarHolder.addView(mPreview);

		mCloseButton = (ImageView)buttonsView.findViewById(R.id.closeButton);

		mCloseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				MuPDFActivity.this.finish();

				if(mp!=null)
				{
					mp.stop();
				}
			}
		});

		mAudioButton = (ImageView)buttonsView.findViewById(R.id.audioButton);
		mAudioButton.setVisibility(View.INVISIBLE);
		mAudioButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(mp!=null)
				{
					if(mp.isPlaying())
					{
						mp.pause();
						mAudioButton.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_action_audio));
					}
					else
					{
						mp.start();
						mAudioButton.setImageDrawable(ContextCompat.getDrawable(MyApplication.getInstance(), R.drawable.ic_action_pause));
					}
				}
				else
				{
					playBackgroundIfPossible();
				}
			}
		});

		mTopBarSwitcher = (ViewSwitcher)buttonsView.findViewById(R.id.switcher);
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPreviewBarHolder.setVisibility(View.INVISIBLE);
	}


//	@Override
//	public boolean onSearchRequested() {
//		if (buttonsVisible && mTopBarIsSearch) {
//			hideButtons();
//		} else {
//			showButtons();
//			searchModeOn();
//		}
//		return super.onSearchRequested();
//	}

	private MuPDFCore openFile(String path) {
		int lastSlashPos = path.lastIndexOf('/');
		fileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		Log.d(TAG, "Trying to open " + path);
		PDFParser linkGetter = new PDFParser(this, path);
		linkOfDocument = linkGetter.getLinkInfo();

		try {
			core = new MuPDFCore(path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			Log.e(TAG, "get core failed", e);
			return null;
		}
		return core;
	}

	private void onBuy(String path) {
		/*
		Log.d(TAG, "onBuy event path = " + path);
		DownloadsManager downloadsManager = new DownloadsManager(getContext());
		MagazineItem magazine;
		try {
			magazine = downloadsManager.findByFilePath(path, DataBaseHelper.TABLE_DOWNLOADED_ITEMS);
			if (null != magazine) {
				Intent intent = new Intent(getContext(), BillingActivity.class);
				intent
					.putExtra(BillingActivity.FILE_NAME_KEY, magazine.getFilePath())
					.putExtra(BillingActivity.TITLE_KEY, magazine.getTitle())
					.putExtra(BillingActivity.SUBTITLE_KEY, magazine.getSubtitle());
				startActivityForResult(intent, START_BILLING_ACTIVITY);
			}
		} catch (MagazineNotFoundInDatabaseException e) {
			e.printStackTrace();
		}
		*/
	}

	private Context getContext() {
		return this;
	}

	private void setCurrentlyViewedPreview() {
		int i = docView.getDisplayedViewIndex();
		if (core.getDisplayPages() == 2) {
			i = (i * 2) - 1;
		}
		pdfPreviewPagerAdapter.setCurrentlyViewing(i);
        currentlyViewing = i;
	}

	public void centerPreviewAtPosition(int position) {
        int offset = (mPreview.getWidth() / 2) -
				(getResources().getDimensionPixelSize(R.dimen.page_preview_size_width) / 2);
        listLayoutManager.scrollToPositionWithOffset(position, offset);
	}

	private class ActivateAutoLinks extends TinySafeAsyncTask<Integer, Void, ArrayList<LinkInfoExternal>> {
		private MuPDFPageView pageView;// = (MuPDFPageView) docView.getDisplayedView();
		
		public ActivateAutoLinks(MuPDFPageView pParent) {
			pageView = pParent;
		}
		
		@Override
		protected ArrayList<LinkInfoExternal> doInBackground(Integer... params) {
			int page = params[0].intValue();
			Log.d(TAG, "Page = " + page);
			if (null != core) {
				LinkInfo[] links = core.getPageLinks(page);
				if(null == links){
					return null;
				}
				ArrayList<LinkInfoExternal> autoLinks = new ArrayList<LinkInfoExternal>();
				for (LinkInfo link : links) {
					if(link instanceof LinkInfoExternal) {
						LinkInfoExternal currentLink = (LinkInfoExternal) link;
					
						if (null == currentLink.url) {
							continue;
						}
						Log.d(TAG, "checking link for autoplay: " + currentLink.url);
	
						if (currentLink.isMediaURI()) {
							if (currentLink.isAutoPlay()) {
								autoLinks.add(currentLink);
							}
						}
					}
				}
				return autoLinks;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final ArrayList<LinkInfoExternal> autoLinks) {
			if (isCancelled() || autoLinks == null) {
				return;
			}
			docView.post(new Runnable() {
				public void run() {
					for(LinkInfoExternal link : autoLinks){
						if (pageView != null && null != core) {
							String basePath = core.getFileDirectory();
							MediaHolder mediaHolder = new MediaHolder(getContext(), link, basePath);
							if (link.isLandscapeOnly()) {
								if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
									addMediaHolder(link, mediaHolder);
								}
							} else if (link.isPortraitOnly()) {
								if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
									addMediaHolder(link, mediaHolder);
								}
							} else {
								addMediaHolder(link, mediaHolder);
							}
						}
					}
				}

				private void addMediaHolder(LinkInfoExternal link, MediaHolder mediaHolder) {
					pageView.addMediaHolder(mediaHolder, link.url);
					pageView.addView(mediaHolder);
					mediaHolder.setVisibility(View.VISIBLE);
					mediaHolder.requestLayout();
				}
			});
		}
	}

//	private class SearchTask extends TinySafeAsyncTask<Void, Integer, SearchTaskResult> {
//		private final int increment; 
//		private final int startIndex;
//		private final ProgressDialogX progressDialog;
//		
//		public SearchTask(Context context, int increment, int startIndex) {
//			this.increment = increment;
//			this.startIndex = startIndex;
//			progressDialog = new ProgressDialogX(context);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			progressDialog.setTitle(getString(R.string.searching_));
//			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//				public void onCancel(DialogInterface dialog) {
//					killSearch();
//				}
//			});
//			progressDialog.setMax(core.countPages());
//
//		}

//		@Override
//		protected SearchTaskResult doInBackground(Void... params) {
//			int index = startIndex;
//
//			while (0 <= index && index < core.countPages() && !isCancelled()) {
//				publishProgress(index);
//				RectF searchHits[] = core.searchPage(index, mSearchText.getText().toString());
//
//				if (searchHits != null && searchHits.length > 0) {
//					return SearchTaskResult.init(mSearchText.getText().toString(), index, searchHits);
//				}
//
//				index += increment;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(SearchTaskResult result) {
//			if (isCancelled()) {
//				return;
//			}
//			progressDialog.cancel();
//			if (result != null) {
//				// Ask the ReaderView to move to the resulting page
//				docView.setDisplayedViewIndex(result.pageNumber);
//			    SearchTaskResult.recycle();
//				// Make the ReaderView act on the change to mSearchTaskResult
//				// via overridden onChildSetup method.
//			    docView.resetupChildren();
//			} else {
//				alertBuilder.setTitle(SearchTaskResult.get() == null ? R.string.text_not_found : R.string.no_further_occurences_found);
//				AlertDialog alert = alertBuilder.create();
//				alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
//						(DialogInterface.OnClickListener)null);
//				alert.show();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			super.onCancelled();
//			progressDialog.cancel();
//		}
//
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			super.onProgressUpdate(values);
//			progressDialog.setProgress(values[0].intValue());
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			mHandler.postDelayed(new Runnable() {
//				public void run() {
//					if (!progressDialog.isCancelled())
//					{
//						progressDialog.show();
//						progressDialog.setProgress(startIndex);
//					}
//				}
//			}, SEARCH_PROGRESS_DELAY);
//		}
//	}


	private MediaPlayer mp;

	public void playBackgroundIfPossible()
	{
		//获取本地数据或者把网络数据存到本地
		String lessonID =getIntent().getStringExtra(ShareDefine.KLessonID);

		String audioPath= FlyingFileManager.getLessonBackgroundFilePath(lessonID);

		// 判断目录或文件是否存在
		if (FlyingFileManager.fileExists(audioPath))
		{  // 不存在返回 false

			mAudioButton.setVisibility(View.GONE);
			return;
		}
		else
		{
			mAudioButton.setVisibility(View.VISIBLE);

			// 初始化音乐资源
			try {
				// 创建MediaPlayer对象

				if(mp ==null){

					mp = new MediaPlayer();
				}

				mp.setDataSource(FlyingFileManager.getFile(audioPath).getAbsolutePath());
				mp.prepare();
				mp.start();
			}
			catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 音乐播放完毕的事件处理
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					// 循环播放
					try {
						mp.start();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			// 播放音乐时发生错误的事件处理
			mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					// 释放资源
					try {
						mp.release();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return false;
				}
			});

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// your code

			if(mp!=null)
			{
				mp.stop();
			}

			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
