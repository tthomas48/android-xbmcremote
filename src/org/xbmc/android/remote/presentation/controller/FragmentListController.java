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

package org.xbmc.android.remote.presentation.controller;

import org.xbmc.android.jsonrpc.api.Version.Branch;
import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.AbstractManager;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.api.business.IControlManager;
import org.xbmc.api.business.IInfoManager;
import org.xbmc.api.business.IMusicManager;
import org.xbmc.api.object.Artist;
import org.xbmc.api.object.Genre;
import org.xbmc.api.type.SortType;

import android.content.Context;
import android.content.SharedPreferences;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public abstract class FragmentListController extends ListController {
	protected Artist mArtist;
	protected Genre mGenre;
	
	protected IMusicManager mMusicManager;
	protected IControlManager mControlManager;
	protected IInfoManager mInfoManager;
	
	public static final int MENU_PLAY_ALL = 1;
	public static final int MENU_SORT = 2;

	public static final int MENU_SORT_BY_ARTIST_ASC = 21;
	public static final int MENU_SORT_BY_ARTIST_DESC = 22;
	public static final int MENU_SORT_BY_ALBUM_ASC = 23;
	public static final int MENU_SORT_BY_ALBUM_DESC = 24;
	public static final int MENU_SORT_BY_YEAR_ASC = 25;
	public static final int MENU_SORT_BY_YEAR_DESC = 26;
	public static final int MENU_SORT_BY_PLAYCOUNT_ASC = 27;
	public static final int MENU_SORT_BY_PLAYCOUNT_DESC = 28;
	public static final int MENU_SORT_BY_DATEADDED_ASC = 29;
	public static final int MENU_SORT_BY_DATEADDED_DESC = 30;
	public static final int MENU_SORT_BY_LASTPLAYED_ASC = 31;
	public static final int MENU_SORT_BY_LASTPLAYED_DESC = 32;

	@Override
	public void onCreateOptionsMenu(Menu menu) {
		if (mArtist != null || mGenre != null) {
			menu.add(0, MENU_PLAY_ALL, 0, "Play all").setIcon(
					R.drawable.menu_album);
		}
		SubMenu sortMenu = menu.addSubMenu(0, MENU_SORT, 0, "Sort").setIcon(
				R.drawable.menu_sort);
		sortMenu.add(2, MENU_SORT_BY_ALBUM_ASC, 0,
				mActivity.getString(R.string.sort_album));
		sortMenu.add(2, MENU_SORT_BY_ALBUM_DESC, 0,
				mActivity.getString(R.string.sort_album_r));

		if (mArtist != null) {
			sortMenu.add(2, MENU_SORT_BY_YEAR_DESC, 0,
					mActivity.getString(R.string.sort_year));
			sortMenu.add(2, MENU_SORT_BY_YEAR_ASC, 0,
					mActivity.getString(R.string.sort_album_r));
		} else {
			sortMenu.add(2, MENU_SORT_BY_ARTIST_ASC, 0,
					mActivity.getString(R.string.sort_artist));
			sortMenu.add(2, MENU_SORT_BY_ARTIST_DESC, 0,
					mActivity.getString(R.string.sort_artist_r));
		}

		if (mInfoManager.getAPIVersion(mActivity.getApplicationContext()) >= Branch.FRODO
				.ordinal()) {
			sortMenu.add(2, MENU_SORT_BY_PLAYCOUNT_DESC, 0,
					mActivity.getString(R.string.sort_playcount));
			sortMenu.add(2, MENU_SORT_BY_PLAYCOUNT_ASC, 0,
					mActivity.getString(R.string.sort_playcount_r));
			sortMenu.add(2, MENU_SORT_BY_DATEADDED_DESC, 0,
					mActivity.getString(R.string.sort_dateadded));
			sortMenu.add(2, MENU_SORT_BY_DATEADDED_ASC, 0,
					mActivity.getString(R.string.sort_dateadded_r));
			sortMenu.add(2, MENU_SORT_BY_LASTPLAYED_DESC, 0,
					mActivity.getString(R.string.sort_lastplayed));
			sortMenu.add(2, MENU_SORT_BY_LASTPLAYED_ASC, 0,
					mActivity.getString(R.string.sort_lastplayed_r));
		}
		// menu.add(0, MENU_SWITCH_VIEW, 0,
		// "Switch view").setIcon(R.drawable.menu_view);

	}

	@Override
	public void onOptionsItemSelected(MenuItem item) {
		final SharedPreferences.Editor ed;
		switch (item.getItemId()) {
		case MENU_PLAY_ALL:
			final Artist artist = mArtist;
			final Genre genre = mGenre;
			if (artist != null && genre == null) {
				mMusicManager.play(new QueryResponse(mActivity,
						"Playing all albums by " + artist.name + "...",
						"Error playing songs!", true), genre, mActivity
						.getApplicationContext());
			} else if (genre != null && artist == null) {
				mMusicManager.play(new QueryResponse(mActivity,
						"Playing all albums of genre " + genre.name + "...",
						"Error playing songs!", true), genre, mActivity
						.getApplicationContext());
			} else if (genre != null && artist != null) {
				mMusicManager
						.play(new QueryResponse(mActivity,
								"Playing all songs of genre " + genre.name
										+ " by " + artist.name + "...",
								"Error playing songs!", true), artist, genre,
								mActivity.getApplicationContext());
			}
			break;
		case MENU_SORT_BY_ALBUM_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ALBUM);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_ALBUM_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ALBUM);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_ARTIST_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ARTIST);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_ARTIST_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ARTIST);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_YEAR_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.YEAR);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_YEAR_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.YEAR);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_PLAYCOUNT_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.PLAYCOUNT);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_PLAYCOUNT_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.PLAYCOUNT);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_DATEADDED_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.DATE_ADDED);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_DATEADDED_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.DATE_ADDED);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_LASTPLAYED_ASC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.LASTPLAYED);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_ASC);
			ed.commit();
			fetch();
			break;
		case MENU_SORT_BY_LASTPLAYED_DESC:
			ed = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			ed.putInt(AbstractManager.PREF_SORT_BY_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.LASTPLAYED);
			ed.putString(AbstractManager.PREF_SORT_ORDER_PREFIX
					+ AbstractManager.PREF_SORT_KEY_ALBUM, SortType.ORDER_DESC);
			ed.commit();
			fetch();
			break;
		}
	}
	protected abstract void fetch();
	
	public void onActivityResume(SherlockActivity activity) {
		super.onActivityResume(activity);
		mMusicManager = ManagerFactory.getMusicManager(this);
		mControlManager = ManagerFactory.getControlManager(this);
		mInfoManager = ManagerFactory.getInfoManager(this);
	}
	
	public void onActivityPause() {
		if (mMusicManager != null) {
			mMusicManager.setController(null);
			mMusicManager.postActivity();
		}
		if (mControlManager != null) {
			mControlManager.setController(null);
		}
		if (mInfoManager != null) {
			mControlManager.setController(null);			
		}
		super.onActivityPause();
	}
	
	
}
