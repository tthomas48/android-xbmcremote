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

import java.util.ArrayList;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.AbstractManager;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.android.remote.presentation.activity.DialogFactory;
import org.xbmc.android.remote.presentation.activity.ListActivity;
import org.xbmc.android.util.ImportUtilities;
import org.xbmc.api.business.DataResponse;
import org.xbmc.api.business.IControlManager;
import org.xbmc.api.business.IInfoManager;
import org.xbmc.api.business.IMusicManager;
import org.xbmc.api.business.ISortableManager;
import org.xbmc.api.object.Album;
import org.xbmc.api.object.Artist;
import org.xbmc.api.object.Genre;
import org.xbmc.api.type.ThumbSize;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * TODO Once we move to 1.6+, waste the deprecated code.
 */
@SuppressLint("")
public class AlbumListController extends FragmentListController implements IController {

	private static final int mThumbSize = ThumbSize.SMALL;

	private static final String TAG = "AlbumListController";

	public static final int ITEM_CONTEXT_QUEUE = 1;
	public static final int ITEM_CONTEXT_PLAY = 2;
	public static final int ITEM_CONTEXT_INFO = 3;



	public static final int MENU_SWITCH_VIEW = 3;

	private boolean mCompilationsOnly = false;
	private boolean mLoadCovers = false;

	/**
	 * Defines if only compilations should be listed.
	 * 
	 * @param co
	 *            True if compilations only should be listed, false otherwise.
	 */
	public void setCompilationsOnly(boolean co) {
		mCompilationsOnly = co;
	}
	
	public void onCreate(SherlockFragment fragment, Handler handler, AbsListView list) {
		FragmentActivity activity = fragment.getActivity();
		
		mMusicManager = ManagerFactory.getMusicManager(this);
		mControlManager = ManagerFactory.getControlManager(this);
		mInfoManager = ManagerFactory.getInfoManager(this);

		((ISortableManager) mMusicManager)
				.setSortKey(AbstractManager.PREF_SORT_KEY_ALBUM);
		((ISortableManager) mMusicManager).setIgnoreArticle(PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext())
				.getBoolean(ISortableManager.SETTING_IGNORE_ARTICLE, true));
		((ISortableManager) mMusicManager).setPreferences(activity
				.getPreferences(Context.MODE_PRIVATE));

		final String sdError = ImportUtilities.assertSdCard();
		mLoadCovers = sdError == null;

		if (!isCreated()) {
			super.onCreate(activity, handler, list);

			if (!mLoadCovers) {
				Toast toast = Toast.makeText(activity, sdError
						+ " Displaying place holders only.", Toast.LENGTH_LONG);
				toast.show();
			}

			mArtist = (Artist) activity.getIntent().getSerializableExtra(
					ListController.EXTRA_ARTIST);
			mGenre = (Genre) activity.getIntent().getSerializableExtra(
					ListController.EXTRA_GENRE);
			activity.registerForContextMenu(mList);

			mFallbackBitmap = BitmapFactory.decodeResource(
					activity.getResources(), R.drawable.default_album);
			setupIdleListener();

			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (isLoading())
						return;
					Intent nextActivity;
					final Album album = (Album) mList.getAdapter().getItem(position);
					nextActivity = new Intent(view.getContext(),
							ListActivity.class);
					nextActivity.putExtra(ListController.EXTRA_LIST_CONTROLLER,
							new SongListController());
					nextActivity.putExtra(ListController.EXTRA_ALBUM, album);
					mActivity.startActivity(nextActivity);
				}
			});
			mList.setOnKeyListener(new ListControllerOnKeyListener<Album>());
			fetch();
		}
	}

	private void setAdapter(ArrayList<Album> value) {
		setAdapter(new AlbumAdapter(mActivity, value));
	}

	public void updateLibrary() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage(
				"Are you sure you want XBMC to rescan your music library?")
				.setCancelable(false)
				.setPositiveButton("Yes!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mControlManager.updateLibrary(
										new DataResponse<Boolean>() {
											public void run() {
												final String message;
												if (value) {
													message = "Music library updated has been launched.";
												} else {
													message = "Error launching music library update.";
												}
												Toast toast = Toast.makeText(
														mActivity, message,
														Toast.LENGTH_SHORT);
												toast.show();
											}
										}, "music", mActivity
												.getApplicationContext());
							}
						})
				.setNegativeButton("Uh, no.",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
		builder.create().show();

	}

	@Override
	protected void fetch() {
		final String title = mArtist != null ? mArtist.name + " - "
				: mGenre != null ? mGenre.name + " - " : ""
						+ (mCompilationsOnly ? "Compilations" : "Albums");
		DataResponse<ArrayList<Album>> response = new DataResponse<ArrayList<Album>>() {
			public void run() {
				
				if (value.size() > 0) {
					setTitle(title + " (" + value.size() + ")");
					setAdapter(value);
				} else {
					setTitle(title);
					setNoDataMessage("No albums found.",
							R.drawable.default_album);
				}
			}
		};

		showOnLoading();
		setTitle(title + "...");
		if (mArtist != null) { // albums of an artist
			mMusicManager.getAlbums(response, mArtist,
					mActivity.getApplicationContext());
		} else if (mGenre != null) { // albums of a genre
			mMusicManager.getAlbums(response, mGenre,
					mActivity.getApplicationContext());
		} else if (mCompilationsOnly) { // compilations
			mMusicManager.getCompilations(response,
					mActivity.getApplicationContext());
		} else {
			mMusicManager
					.getAlbums(response, mActivity.getApplicationContext());
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int position = info.position;
		menu.setHeaderTitle(((Album) mList.getItemAtPosition(position)).name);
		menu.add(0, ITEM_CONTEXT_QUEUE, 1, "Queue Album");
		menu.add(0, ITEM_CONTEXT_PLAY, 2, "Play Album");
		menu.add(0, ITEM_CONTEXT_INFO, 3, "View Details");
	}


	public void onContextItemSelected(android.view.MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Album album = (Album) mList.getAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case ITEM_CONTEXT_QUEUE:
			mMusicManager.addToPlaylist(new QueryResponse(mActivity,
					"Adding album \"" + album.name + "\" by " + album.artist
							+ " to playlist...", "Error adding album!"), album,
					mActivity.getApplicationContext());
			break;
		case ITEM_CONTEXT_PLAY:
			mMusicManager.play(new QueryResponse(mActivity, "Playing album \""
					+ album.name + "\" by " + album.artist + "...",
					"Error playing album!", true), album, mActivity
					.getApplicationContext());
			break;
		case ITEM_CONTEXT_INFO:
			DialogFactory.getAlbumDetail(mMusicManager, mActivity, album,
					mActivity.getApplicationContext()).show();
			break;
		default:
			return;
		}
	}
	

	private class AlbumAdapter extends ArrayAdapter<Album> {
		AlbumAdapter(Activity activity, ArrayList<Album> items) {
			super(activity, R.layout.pic_item, items);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				view = inflater.inflate(R.layout.pic_item, null);
			} else {
				view = convertView;
			}
			
			final Album album = getItem(position);
			TextView title = (TextView) view.findViewById(R.id.TitleTextView);
			TextView subtitle = (TextView) view
					.findViewById(R.id.SubtitleTextView);
			final ImageView icon = (ImageView) view.findViewById(R.id.IconImageView);
			title.setText(album.name);
			subtitle.setText(album.artist);
			
			Log.i(TAG, "isListIdle: " + mPostScrollLoader.isListIdle());
			if (mLoadCovers) {
				icon.setImageResource(R.drawable.icon_album_dark);
				icon.setTag(album);
				
				if (mMusicManager.coverLoaded(album, mThumbSize)) {
					icon.setImageBitmap(mMusicManager.getCoverSync(album, mThumbSize));
				} else {
					mMusicManager.getCover(new DataResponse<Bitmap>() {
						@Override
						public void run() {
							if(value == null) {
								return;
							}
							
							ImageView view = (ImageView) mList.findViewWithTag(album);
							if(view == null) {
								return;
							}
							
							int width = icon.getWidth();
							int height = icon.getHeight();
							if(width <=0 || height <= 0) {
								return;
							}
							Bitmap transformed = Bitmap.createScaledBitmap(value, width, height, true);
							view.setImageBitmap(transformed);
						}
						
					}, album, ThumbSize.MEDIUM, BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.icon_album_dark_big), mActivity, false);
				}
			}
			return view;
		}
	}



	private static final long serialVersionUID = 1088971882661811256L;
}
