/*****************************************************************************
 * SearchActivity.java
 *****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.birdcopy.BirdCopyApp.Search;

import java.lang.Override;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Component.listener.BackGestureListener;

public class SearchActivity extends ListActivity implements SearchData.DealResult
{

    public final static String TAG = "VLC/SearchActivity";

    private ImageView mBackView;
    private TextView  mTitleView;

    private ImageView mSearchView;

    private SearchData mSearchTagdata;
    private ArrayList<String> mData;

    private boolean mFirstWord=true;

    private EditText mSearchText;
    private SearchHistoryAdapter mHistoryAdapter;
    private SearchResultAdapter mResultAdapter;
    private LinearLayout mListHeader;

    /** 手势监听 */
    GestureDetector mGestureDetector;
    /** 是否需要监听手势关闭功能 */
    private boolean mNeedBackGesture = true;

    public SearchActivity()
    {
        // Empty constructor required for fragment subclasses

        super();

        mData = new ArrayList<String>();
        mSearchTagdata = new SearchData();
        mSearchTagdata.setDelegate(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        setNeedBackGesture(true);

        // TODO: create layout
        mHistoryAdapter = new SearchHistoryAdapter(this);
        mResultAdapter = new SearchResultAdapter(this);

        mSearchTagdata = new SearchData();
        mSearchTagdata.setDelegate(this);

        initDataAndView();

        initGestureDetector();
    }

    private void initDataAndView()
    {

        mSearchText = (EditText) findViewById(R.id.search_text);
        mSearchText.setOnEditorActionListener(searchTextListener);
        mSearchText.addTextChangedListener(searchTextWatcher);

        mBackView  = (ImageView)findViewById(R.id.top_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        mTitleView = (TextView)findViewById(R.id.search_top_title);
        mTitleView.setText(R.string.search_top_title);

        mSearchView = (ImageView) findViewById(R.id.search_btn);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if( mSearchText.getText().toString()==null|| mSearchText.getText().toString().length()==0)
                {
                    if(mData.size()==0)
                    {
                        showSearchHistory();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "没有更多了：）", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "没有更多了：）", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mSearchText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearchText, InputMethodManager.SHOW_IMPLICIT);

        showSearchHistory();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.search_view).getWindowToken(), 0);
    }

    private void search(String key)
    {

        int results=0;

        if(mFirstWord==true)
        {
            mSearchTagdata.getTagListStrByTag(key,1000);
        }
        else{

            // set result adapter to the list
            mResultAdapter.clear();

            for (int i = 0; i < mData.size(); i++)
            {
                String item = mData.get(i);

                if (item.contains(key))
                {
                    mResultAdapter.add(item);
                    results++;
                }
            }

            mResultAdapter.sort();
        }

        String headerText = getResources().getQuantityString(R.plurals.search_found_results_quantity, results, results);
        showListHeader(headerText);

        setListAdapter(mResultAdapter);
    }

    private void showListHeader(String text) {
        ListView lv = getListView();

        // Create a new header if it doesn't already exist
        if (mListHeader == null) {
            LayoutInflater infalter =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mListHeader = (LinearLayout) infalter.inflate(R.layout.list_header, lv, false);
            lv.addHeaderView(mListHeader, null, false);
        }

        // Set header text
        TextView headerText = (TextView) mListHeader.findViewById(R.id.text);
        headerText.setText(text);
    }

    private void showSearchHistory()
    {
        if(mData.size()==0)
        {
            mSearchTagdata.getTagListStrByTag(null,1000);
        }
        else
        {
            // Add header to the history
            String headerText = getString(R.string.search_history);
            showListHeader(headerText);

            mHistoryAdapter.clear();
            for (String s : mData) {
                mHistoryAdapter.add(s);
            }

            mHistoryAdapter.notifyDataSetChanged();
            setListAdapter(mHistoryAdapter);
        }
    }

    private final TextWatcher searchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

            if (s.length() > 0)
            {
                mFirstWord = false;
                search(s.toString());
            } else
            {
                mFirstWord = true;
                showSearchHistory();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final OnEditorActionListener searchTextListener = new OnEditorActionListener()
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            return false;
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        if (getListAdapter() == mHistoryAdapter) {
            String selection = ((TextView) v.findViewById(android.R.id.text1)).getText().toString();
            mSearchText.setText(selection);
            mSearchText.setSelection(selection.length());
            mSearchText.requestFocus();
        }
        else if (getListAdapter() == mResultAdapter)
        {
            String item = (String) getListView().getItemAtPosition(position);

            if(item!=null && !item.equals(""))
            {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", item);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                SearchActivity.this.finish();
            }

            super.onListItemClick(l, v, position, id);
        }
    };

    public void onSearchKeyPressed()
    {
        if (mSearchText == null)
            return;
        mSearchText.requestFocus();
        mSearchText.setSelection(mSearchText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearchText, InputMethodManager.RESULT_SHOWN);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event)
    {

        if(keyCode == KeyEvent.KEYCODE_ENTER){

            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm.isActive()){

                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );
            }

            return true;
        }

        return false;
    }

    public void parseOK(ArrayList<String> list)
    {
        if (list.size()>0)
        {
            mResultAdapter.clear();
            mData.clear();

            for (String data : list) {
                mResultAdapter.add(data);
            }
            // stash all the data in our backing store
            mData.addAll(list);
            // notify the adapter that we can update now
            mResultAdapter.notifyDataSetChanged();
        }
    }

    private void initGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getApplicationContext(),
                    new BackGestureListener(this));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if(mNeedBackGesture){
            return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 设置是否进行手势监听
     */
    public void setNeedBackGesture(boolean mNeedBackGesture){
        this.mNeedBackGesture = mNeedBackGesture;
        initGestureDetector();
    }

    /*
     * 返回
     */
    public void doBack(View view) {
        onBackPressed();
    }
}
