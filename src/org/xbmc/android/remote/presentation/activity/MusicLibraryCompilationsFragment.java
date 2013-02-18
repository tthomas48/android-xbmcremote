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
import org.xbmc.android.remote.presentation.controller.AlbumListController;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class MusicLibraryCompilationsFragment extends MusicLibraryAbstractFragment {

	//private SlidingTabHost mTabHost;
	private AlbumListController mCompilationsController;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.musiclibraryalbum);
		
		mCompilationsController = new AlbumListController();
		mCompilationsController.findMessageView(getActivity().findViewById(R.id.albumlist_outer_layout));
		mCompilationsController.setCompilationsOnly(true);

		mCompilationsController.onCreate(MusicLibraryCompilationsFragment.this, mHandler, (GridView)getActivity().findViewById(R.id.albumlist_list));
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(mCompilationsController.getListView());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.addPreMenu(menu);
		mCompilationsController.onCreateOptionsMenu(menu);
		super.addPostMenu(menu);
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		
		mCompilationsController.onOptionsItemSelected(item);
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mCompilationsController.onCreateContextMenu(menu, v, menuInfo);
		}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		mCompilationsController.onContextItemSelected(item);
		return super.onContextItemSelected(item);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mCompilationsController.onActivityResume(this);
		mConfigurationManager.onActivityResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mCompilationsController.onActivityPause();
		mConfigurationManager.onActivityPause();
	}
}