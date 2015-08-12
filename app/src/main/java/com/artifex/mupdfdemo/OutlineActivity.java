package com.artifex.mupdfdemo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class OutlineActivity extends ListActivity {

	AbstractLockRotationActivity.OutlineItem mItems[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    mItems = AbstractLockRotationActivity.OutlineActivityData.get().items;
	    setListAdapter(new AbstractLockRotationActivity.OutlineAdapter(getLayoutInflater(),mItems));
	    // Restore the position within the list from last viewing
	    getListView().setSelection(AbstractLockRotationActivity.OutlineActivityData.get().position);
	    setResult(-1);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		AbstractLockRotationActivity.OutlineActivityData.get().position = getListView().getFirstVisiblePosition();
		setResult(mItems[position].page);
		finish();
	}
}
