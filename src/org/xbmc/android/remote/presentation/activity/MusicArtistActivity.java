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
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.api.business.IEventClientManager;
import org.xbmc.eventclient.ButtonCodes;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MusicArtistActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_artist);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar
				.addTab(getSupportActionBar()
						.newTab()
						.setText("Albums")
						.setIcon(R.drawable.st_album_off)
						.setTabListener(
								new ListTabListener<MusicLibraryAlbumFragment>(
										this, "tab_albums",
										MusicLibraryAlbumFragment.class)), true);

		// TODO Add the song fragment
		actionBar.addTab(
				getSupportActionBar()
						.newTab()
						.setText("Songs")
						.setIcon(R.drawable.st_album_off)
						.setTabListener(
								new ListTabListener<MusicLibrarySongFragment>(
										this, "tab_songs",
										MusicLibrarySongFragment.class)), false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO: this technically should have a controller attached so that it
		// can update
		// the screen. Somehow we should be able to get the controller from the
		// current tab
		IEventClientManager client = ManagerFactory.getEventClientManager(null);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_PLUS, false,
					true, true, (short) 0, (byte) 0);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_MINUS, false,
					true, true, (short) 0, (byte) 0);
			return true;
		}
		client.setController(null);
		return super.onKeyDown(keyCode, event);
	}
}
