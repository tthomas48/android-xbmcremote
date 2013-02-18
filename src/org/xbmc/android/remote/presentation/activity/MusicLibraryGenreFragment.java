/*
 *      Copyright (C) 2005-2009 Team XBMC
 *      http://xbmc.org
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XBMC Remote; see the file license.  If not, write to
 *  the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *  http://www.gnu.org/copyleft/gpl.html
 *
 */

package org.xbmc.android.remote.presentation.activity;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.presentation.controller.MusicGenreListController;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;

public class MusicLibraryGenreFragment extends SherlockFragment implements ViewTreeObserver.OnGlobalLayoutListener {

	//private SlidingTabHost mTabHost;
	private MusicGenreListController mGenresController;
	
	private static final int MENU_NOW_PLAYING = 301;
	private static final int MENU_UPDATE_LIBRARY = 302;
	private static final int MENU_REMOTE = 303;
	
	private static final String PREF_REMEMBER_TAB = "setting_remember_last_tab";
	private static final String LAST_MUSIC_TAB_ID = "last_music_tab_id";
	
    private ConfigurationManager mConfigurationManager;
    private Handler mHandler;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.musiclibrarygenre);
		
		// remove nasty top fading edge
		FrameLayout topFrame = (FrameLayout)getActivity().findViewById(android.R.id.content);
		topFrame.setForeground(null);
		
		// assign the gui logic to each tab
		mHandler = new Handler();
		mGenresController = new MusicGenreListController();
		mGenresController.findMessageView(getActivity().findViewById(R.id.genres_outer_layout));

		mGenresController.onCreate(MusicLibraryGenreFragment.this, mHandler, (AbsListView) getActivity().findViewById(R.id.genres_list));
/*		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				initTab(tabId);
				
				final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if (prefs.getBoolean(PREF_REMEMBER_TAB, false)) {
					getSharedPreferences("global", Context.MODE_PRIVATE).edit().putString(LAST_MUSIC_TAB_ID, tabId).commit();
				}
			}
		});
*/		
		mConfigurationManager = ConfigurationManager.getInstance(this.getActivity());
	}
	
	public void onGlobalLayout() {
		// TODO: Migrate this up to the actual parent activity
//        mTabHost.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		
		String lastTab = "tab_albums";
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		if (prefs.getBoolean(PREF_REMEMBER_TAB, false)) {
			lastTab = (getActivity().getSharedPreferences("global", Context.MODE_PRIVATE).getString(LAST_MUSIC_TAB_ID, "tab_albums"));
//			mTabHost.selectTabByTag(lastTab);
		}
		
		initTab(lastTab);
	}
	
	private void initTab(String tabId) {
//		if (tabId.equals("tab_albums")) {
//			
//		}
//		if (tabId.equals("tab_files")) {
//			mFileController.onCreate(MusicAlbumFragment.this, mHandler, (ListView)getActivity().findViewById(R.id.filelist_list));
//		}
//		if (tabId.equals("tab_artists")) {
//			mArtistController.onCreate(MusicLibraryActivity.this, mHandler, (ListView)findViewById(R.id.artists_list));
//		}
//		if (tabId.equals("tab_genres")) {
//			mGenreController.onCreate(MusicLibraryActivity.this, mHandler, (ListView)findViewById(R.id.genres_list));
//		}
//		if (tabId.equals("tab_compilations")) {
//			mCompilationsController.onCreate(MusicLibraryActivity.this, mHandler, (ListView)findViewById(R.id.compilations_list));
//		}
	}
	
	/*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_NOW_PLAYING, 0, "Now playing").setIcon(R.drawable.menu_nowplaying);
		switch (mTabHost.getCurrentTab()) {
			case 0:
				mAlbumController.onCreateOptionsMenu(menu);
				break;
			case 1:
				mArtistController.onCreateOptionsMenu(menu);
				break;
			case 2:
				mGenreController.onCreateOptionsMenu(menu);
				break;
			case 3:
				mCompilationsController.onCreateOptionsMenu(menu);
				break;
			case 4:
				mFileController.onCreateOptionsMenu(menu);
				break;
		}
		menu.add(0, MENU_UPDATE_LIBRARY, 0, "Update Library").setIcon(R.drawable.menu_refresh);
		menu.add(0, MENU_REMOTE, 0, "Remote control").setIcon(R.drawable.menu_remote);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// first, process individual menu events
		switch (mTabHost.getCurrentTab()) {
		case 0:
			mAlbumController.onOptionsItemSelected(item);
			break;
		case 1:
			mArtistController.onOptionsItemSelected(item);
			break;
		case 2:
			mGenreController.onOptionsItemSelected(item);
			break;
		case 3:
			mCompilationsController.onOptionsItemSelected(item);
			break;
		case 4:
			mFileController.onOptionsItemSelected(item);
			break;
		}
		
		// then the generic ones.
		switch (item.getItemId()) {
		case MENU_REMOTE:
			final Intent intent;
			if (getSharedPreferences("global", Context.MODE_PRIVATE).getInt(RemoteController.LAST_REMOTE_PREFNAME, -1) == RemoteController.LAST_REMOTE_GESTURE) {
				intent = new Intent(this, GestureRemoteActivity.class);
			} else {
				intent = new Intent(this, RemoteActivity.class);
			}
			intent.addFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			return true;
		case MENU_UPDATE_LIBRARY:
			mAlbumController.updateLibrary();
			return true;
		case MENU_NOW_PLAYING:
			startActivity(new Intent(this,  NowPlayingActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
/*		switch (mTabHost.getCurrentTab()) {
			case 0:
				mAlbumController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 1:
				mArtistController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 2:
				mGenreController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 3:
				mCompilationsController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 4:
				mFileController.onCreateContextMenu(menu, v, menuInfo);
				break;
		}
*/	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
/*		switch (mTabHost.getCurrentTab()) {
		case 0:
			mAlbumController.onContextItemSelected(item);
			break;
		case 1:
			mArtistController.onContextItemSelected(item);
			break;
		case 2:
			mGenreController.onContextItemSelected(item);
			break;
		case 3:
			mCompilationsController.onContextItemSelected(item);
			break;
		case 4:
			mFileController.onContextItemSelected(item);
			break;
		}
*/		return super.onContextItemSelected(item);
	}
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		IEventClientManager client = ManagerFactory.getEventClientManager(mAlbumController);
//		switch (keyCode) {
//			case KeyEvent.KEYCODE_VOLUME_UP:
//				client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_PLUS, false, true, true, (short)0, (byte)0);
//				return true;
//			case KeyEvent.KEYCODE_VOLUME_DOWN:
//				client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_MINUS, false, true, true, (short)0, (byte)0);
//				return true;
//		}
//		client.setController(null);
//		return super.onKeyDown(keyCode, event);
//	}
	

	@Override
	public void onResume() {
		super.onResume();
		// TODO: FIX THESE TO TAKE FRAGMENTS
		mGenresController.onActivityResume(this);
		mConfigurationManager.onActivityResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mGenresController.onActivityPause();
		mConfigurationManager.onActivityPause();
	}
}
