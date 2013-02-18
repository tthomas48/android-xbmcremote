package org.xbmc.android.remote.presentation.activity;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.api.business.IEventClientManager;
import org.xbmc.eventclient.ButtonCodes;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class MusicLibraryActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_library);

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
		actionBar
				.addTab(getSupportActionBar()
						.newTab()
						.setText("Artists")
						.setIcon(R.drawable.st_artist_off)
						.setTabListener(
								new ListTabListener<MusicLibraryArtistFragment>(
										this, "tab_artists",
										MusicLibraryArtistFragment.class)),
						false);
		actionBar.addTab(
				getSupportActionBar()
						.newTab()
						.setText("Genres")
						.setIcon(R.drawable.st_genre_off)
						.setTabListener(
								new ListTabListener<MusicLibraryGenreFragment>(
										this, "tab_genres",
										MusicLibraryGenreFragment.class)),
				false);
		actionBar
				.addTab(getSupportActionBar()
						.newTab()
						.setText("Compilations")
						.setIcon(R.drawable.st_va_off)
						.setTabListener(
								new ListTabListener<MusicLibraryCompilationsFragment>(
										this, "tab_Compilations",
										MusicLibraryCompilationsFragment.class)),
						false);
		actionBar
				.addTab(getSupportActionBar()
						.newTab()
						.setText("Files")
						.setIcon(R.drawable.st_filemode_off)
						.setTabListener(
								new ListTabListener<MusicLibraryFileFragment>(
										this, "tab_files",
										MusicLibraryFileFragment.class)), false);

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
		// TODO: this technically should have a controller attached so that it can update
		// the screen. Somehow we should be able to get the controller from the current tab
		IEventClientManager client = ManagerFactory
				.getEventClientManager(null);
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