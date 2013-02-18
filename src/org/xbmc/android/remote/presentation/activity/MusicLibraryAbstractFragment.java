package org.xbmc.android.remote.presentation.activity;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.presentation.controller.AlbumListController;
import org.xbmc.android.remote.presentation.controller.RemoteController;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ViewTreeObserver;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class MusicLibraryAbstractFragment extends SherlockFragment
		implements ViewTreeObserver.OnGlobalLayoutListener {
	private static final int MENU_NOW_PLAYING = 301;
	private static final int MENU_UPDATE_LIBRARY = 302;
	private static final int MENU_REMOTE = 303;

	private static final String PREF_REMEMBER_TAB = "setting_remember_last_tab";
	private static final String LAST_MUSIC_TAB_ID = "last_music_tab_id";

	protected ConfigurationManager mConfigurationManager;
	protected Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// assign the gui logic to each tab
		mHandler = new Handler();
		/*
		 * mTabHost.setOnTabChangedListener(new OnTabChangeListener() { public
		 * void onTabChanged(String tabId) { initTab(tabId);
		 * 
		 * final SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences
		 * (getApplicationContext()); if (prefs.getBoolean(PREF_REMEMBER_TAB,
		 * false)) { getSharedPreferences("global",
		 * Context.MODE_PRIVATE).edit().putString(LAST_MUSIC_TAB_ID,
		 * tabId).commit(); } } });
		 */
		mConfigurationManager = ConfigurationManager.getInstance(this
				.getActivity());
	}

	public void onGlobalLayout() {
		String lastTab = "tab_albums";
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		if (prefs.getBoolean(PREF_REMEMBER_TAB, false)) {
			lastTab = (getActivity().getSharedPreferences("global",
					Context.MODE_PRIVATE).getString(LAST_MUSIC_TAB_ID,
					"tab_albums"));
		}

	}

	protected void addPreMenu(com.actionbarsherlock.view.Menu menu) {
		menu.clear();
		menu.add(0, MENU_NOW_PLAYING, 0, "Now playing").setIcon(
				R.drawable.menu_nowplaying);
	}

	protected void addPostMenu(com.actionbarsherlock.view.Menu menu) {
		menu.add(0, MENU_UPDATE_LIBRARY, 0, "Update Library").setIcon(
				R.drawable.menu_refresh);
		menu.add(0, MENU_REMOTE, 0, "Remote control").setIcon(
				R.drawable.menu_remote);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		// push this up the stack
		switch (item.getItemId()) {
		case MENU_REMOTE:
			final Intent intent;
			if (getActivity().getSharedPreferences("global",
					Context.MODE_PRIVATE).getInt(
					RemoteController.LAST_REMOTE_PREFNAME, -1) == RemoteController.LAST_REMOTE_GESTURE) {
				intent = new Intent(getActivity(), GestureRemoteActivity.class);
			} else {
				intent = new Intent(getActivity(), RemoteActivity.class);
			}
			intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			return true;
		case MENU_UPDATE_LIBRARY:
			AlbumListController albumController = new AlbumListController();
			albumController.updateLibrary();
			return true;
		case MENU_NOW_PLAYING:
			startActivity(new Intent(getActivity(), NowPlayingActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
