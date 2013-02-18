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
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.android.remote.presentation.activity.DialogFactory;
import org.xbmc.android.remote.presentation.activity.MusicArtistActivity;
import org.xbmc.android.util.ImportUtilities;
import org.xbmc.api.business.DataResponse;
import org.xbmc.api.object.Artist;
import org.xbmc.api.object.Genre;
import org.xbmc.api.type.ThumbSize;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class ArtistListController extends FragmentListController implements
		IController {

	private static final int mThumbSize = ThumbSize.SMALL;
	public static final int ITEM_CONTEXT_QUEUE = 1;
	public static final int ITEM_CONTEXT_PLAY = 2;
	public static final int ITEM_CONTEXT_QUEUE_GENRE = 3;
	public static final int ITEM_CONTEXT_PLAY_GENRE = 4;
	public static final int ITEM_CONTEXT_INFO = 5;

	private boolean mLoadCovers = false;

	public void onCreate(SherlockFragment fragment, Handler handler,
			AbsListView list) {

		mActivity = fragment.getActivity();
		mMusicManager = ManagerFactory.getMusicManager(this);
		mControlManager = ManagerFactory.getControlManager(this);
		mInfoManager = ManagerFactory.getInfoManager(this);
		

		final String sdError = ImportUtilities.assertSdCard();
		mLoadCovers = sdError == null;

		if (!isCreated()) {
			super.onCreate(mActivity, handler, list);

			if (!mLoadCovers) {
				Toast toast = Toast.makeText(mActivity, sdError
						+ " Displaying place holders only.", Toast.LENGTH_LONG);
				toast.show();
			}

			mGenre = (Genre) mActivity.getIntent().getSerializableExtra(
					ListController.EXTRA_GENRE);

			mFallbackBitmap = BitmapFactory.decodeResource(
					mActivity.getResources(), R.drawable.icon_artist);
			setupIdleListener();

			mActivity.registerForContextMenu(mList);
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (isLoading())
						return;
					Intent nextActivity;
					Artist artist = (Artist) mList.getAdapter().getItem(
							position);
					nextActivity = new Intent(view.getContext(),
							MusicArtistActivity.class);
					nextActivity.putExtra(ListController.EXTRA_LIST_CONTROLLER,
							new AlbumListController());
					nextActivity.putExtra(ListController.EXTRA_ARTIST, artist);
					mActivity.startActivity(nextActivity);
				}
			});
			fetch();
		}
	}

	@Override
	protected void fetch() {

		final String title = mGenre != null ? mGenre.name + " - " : ""
				+ "Artists";
		DataResponse<ArrayList<Artist>> response = new DataResponse<ArrayList<Artist>>() {
			@SuppressLint("")
			public void run() {
				if (value.size() > 0) {
					setTitle(title + " (" + value.size() + ")");
					setAdapter(new ArtistAdapter(mActivity, value));
				} else {
					setTitle(title);
					setNoDataMessage("No artists found.",
							R.drawable.icon_artist_dark);
				}
			}
		};

		mList.setOnKeyListener(new ListControllerOnKeyListener<Artist>());

		showOnLoading();
		setTitle(title + "...");
		if (mGenre != null) {
			mMusicManager.getArtists(response, mGenre,
					mActivity.getApplicationContext());
		} else {
			mMusicManager.getArtists(response,
					mActivity.getApplicationContext());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = info.position;
		final Artist artist = (Artist) mList.getAdapter().getItem(position);
		menu.setHeaderTitle(artist.name);
		menu.add(0, ITEM_CONTEXT_QUEUE, 1, "Queue all songs from Artist");
		menu.add(0, ITEM_CONTEXT_PLAY, 2, "Play all songs from Artist");
		if (mGenre != null) {
			menu.add(0, ITEM_CONTEXT_QUEUE_GENRE, 3, "Queue only "
					+ mGenre.name + " from Artist");
			menu.add(0, ITEM_CONTEXT_PLAY_GENRE, 4, "Play only " + mGenre.name
					+ " from Artist");
		}
		menu.add(0, ITEM_CONTEXT_INFO, 5, "View Details");
	}

	public void onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final Artist artist = (Artist) mList.getAdapter()
				.getItem(info.position);
		switch (item.getItemId()) {
		case ITEM_CONTEXT_QUEUE:
			mMusicManager.addToPlaylist(new QueryResponse(mActivity,
					"Adding all songs by " + artist.name + " to playlist...",
					"Error adding songs!"), artist, mActivity
					.getApplicationContext());
			break;
		case ITEM_CONTEXT_PLAY:
			mMusicManager.play(new QueryResponse(mActivity,
					"Playing all songs by " + artist.name + "...",
					"Error playing songs!", true), artist, mActivity
					.getApplicationContext());
			break;
		case ITEM_CONTEXT_QUEUE_GENRE:
			mMusicManager.addToPlaylist(new QueryResponse(mActivity,
					"Adding all songs of genre " + mGenre.name + " by "
							+ artist.name + " to playlist...",
					"Error adding songs!"), artist, mGenre, mActivity
					.getApplicationContext());
			break;
		case ITEM_CONTEXT_PLAY_GENRE:
			mMusicManager.play(
					new QueryResponse(mActivity, "Playing all songs of genre "
							+ mGenre.name + " by " + artist.name + "...",
							"Error playing songs!", true), artist, mGenre,
					mActivity.getApplicationContext());
			break;
		case ITEM_CONTEXT_INFO:
			DialogFactory.getArtistDetail(mMusicManager, mActivity, artist,
					mActivity.getApplicationContext()).show();
			break;
		default:
			return;
		}
	}

	private class ArtistAdapter extends ArrayAdapter<Artist> {
		ArtistAdapter(Activity activity, ArrayList<Artist> items) {
			super(activity, 0, items);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				view = inflater.inflate(R.layout.pic_item, null);
			} else {
				view = convertView;
			}
			final Artist artist = this.getItem(position);
			TextView title = (TextView) view.findViewById(R.id.TitleTextView);
			TextView subtitle = (TextView) view
					.findViewById(R.id.SubtitleTextView);
			final ImageView icon = (ImageView) view
					.findViewById(R.id.IconImageView);
			title.setText(artist.name);
			subtitle.setText("");
			if (mLoadCovers) {
				icon.setImageResource(R.drawable.icon_album_dark_big);
				icon.setTag(artist);

				if (mMusicManager.coverLoaded(artist, mThumbSize)) {
					icon.setImageBitmap(mMusicManager.getCoverSync(artist,
							mThumbSize));
				} else {
					mMusicManager.getCover(new DataResponse<Bitmap>() {
						@Override
						public void run() {
							if (value == null) {
								return;
							}

							ImageView view = (ImageView) mList
									.findViewWithTag(artist);
							if (view == null) {
								return;
							}

							int width = icon.getWidth();
							int height = icon.getHeight();
							Bitmap transformed = Bitmap.createScaledBitmap(
									value, width, height, true);
							view.setImageBitmap(transformed);
						}

					}, artist, ThumbSize.MEDIUM, BitmapFactory.decodeResource(
							mActivity.getResources(),
							R.drawable.icon_album_dark_big), mActivity, false);
				}
			}
			return view;
		}
	}

	private static final long serialVersionUID = 4360738733222799619L;
}
