package org.xbmc.android.remote.presentation.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ListTabListener<T extends Fragment> implements TabListener {
	private Fragment mFragment;
	private final SherlockFragmentActivity mActivity;
	private final String mTag;
	private final Class<T> mClass;

	/**
	 * Constructor used each time a new tab is created.
	 * 
	 * @param activity
	 *            The host Activity, used to instantiate the fragment
	 * @param tag
	 *            The identifier tag for the fragment
	 * @param clz
	 *            The fragment's Class, used to instantiate the fragment
	 */
	public ListTabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
		mFragment= mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
	}

	/* The following are each of the ActionBar.TabListener callbacks */
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    if (mFragment == null){ // check to see if the fragment has already been initialised. If not create a new one.
	        mFragment = android.support.v4.app.Fragment.instantiate(mActivity, mClass.getName()); 
	        ft.replace(android.R.id.content,mFragment,mTag); 
	    } else { 
	    	ft.replace(android.R.id.content,mFragment,mTag);
	    }
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}
