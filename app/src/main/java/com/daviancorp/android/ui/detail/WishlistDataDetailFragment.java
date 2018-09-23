package com.daviancorp.android.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daviancorp.android.data.classes.WishlistData;
import com.daviancorp.android.data.database.DataManager;
import com.daviancorp.android.data.database.WishlistDataCursor;
import com.daviancorp.android.loader.WishlistDataListCursorLoader;
import com.daviancorp.android.mh4udatabase.R;
import com.daviancorp.android.ui.ClickListeners.ItemClickListener;
import com.daviancorp.android.ui.dialog.WishlistDataDeleteDialogFragment;
import com.daviancorp.android.ui.dialog.WishlistDeleteDialogFragment;
import com.daviancorp.android.ui.dialog.WishlistRenameDialogFragment;
import com.daviancorp.android.ui.list.WishlistListActivity;
import com.daviancorp.android.ui.list.WishlistListFragment;

import java.io.IOException;

public class WishlistDataDetailFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	public static final String EXTRA_DETAIL_REFRESH =
			"com.daviancorp.android.ui.general.wishlist_detail_refresh";

	private static final String ARG_ID = "ID";

	private static final String DIALOG_WISHLIST_DATA_EDIT = "wishlist_data_edit";
	private static final String DIALOG_WISHLIST_DATA_DELETE = "wishlist_data_delete";
	private static final int REQUEST_REFRESH = 0;
	private static final int REQUEST_EDIT = 1;
	private static final int REQUEST_DELETE = 2;
	private static final int REQUEST_WISHLIST_DATA_DELETE = 10;

	private static final String TAG = "WishlistDataFragment";

	private boolean started, fromOtherTab;

	private ListView mListView;

	public static WishlistDataDetailFragment newInstance(long id) {
		Bundle args = new Bundle();
		args.putLong(ARG_ID, id);
		WishlistDataDetailFragment f = new WishlistDataDetailFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.wishlist_data_detail_fragment, getArguments(), this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wishlist_item_list, container, false);

		mListView = v.findViewById(android.R.id.list);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.context_wishlist_data, menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Return nothing if result is failed
		if(resultCode != Activity.RESULT_OK) return;

		switch (requestCode){
			case REQUEST_WISHLIST_DATA_DELETE:
				updateUI();
				break;
		}
	}

	public void updateUI() {
		if (started) {
			// Refresh wishlist data fragment
			getLoaderManager().getLoader( R.id.wishlist_data_detail_fragment ).forceLoad();
			WishlistDataListCursorAdapter adapter = (WishlistDataListCursorAdapter) getListAdapter();
			adapter.notifyDataSetChanged();

			if (!fromOtherTab) {
				sendResult(Activity.RESULT_OK, true);
			}
			else {
				fromOtherTab = false;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// If we are becoming visible, then...
		if (isVisibleToUser) {
			updateUI();
		}
	}


	private void sendResult(int resultCode, boolean refresh) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		i.putExtra(EXTRA_DETAIL_REFRESH, refresh);

		getTargetFragment()
				.onActivityResult(getTargetRequestCode(), resultCode, i);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long mId = -1;
		if (args != null) {
			mId = args.getLong(ARG_ID);
		}
		return new WishlistDataListCursorLoader(getActivity(), mId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		WishlistDataListCursorAdapter adapter = new WishlistDataListCursorAdapter(
				getActivity(), (WishlistDataCursor) cursor);
		setListAdapter(adapter);

		started = true;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}



	private class WishlistDataListCursorAdapter extends CursorAdapter {

		private WishlistDataCursor mWishlistDataCursor;

		public WishlistDataListCursorAdapter(Context context, WishlistDataCursor cursor) {
			super(context, cursor, 0);
			mWishlistDataCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_wishlist_data_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
			final WishlistData data = mWishlistDataCursor.getWishlistData();

			// Set up the text view
			LinearLayout root = view.findViewById(R.id.listitem);
			ImageView itemImageView = view.findViewById(R.id.item_image);
			TextView itemTextView = view.findViewById(R.id.item);
			TextView amtTextView = view.findViewById(R.id.amt);
			TextView extraTextView = view.findViewById(R.id.extra);

			final long id = data.getItem().getId();
			final String nameText = data.getItem().getName();
			String amtText = "" + data.getQuantity();

			String extraText = data.getPath();
			int satisfied = data.getSatisfied();

			// Indicate a piece's requirements are met
			if (satisfied == 1) {
				itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color));
			} else {
				itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
			}

			// Assign textviews
			itemTextView.setText(nameText);
			amtTextView.setText(amtText);
			extraTextView.setText(extraText);


			String cellImage = "";
			int cellRare = data.getItem().getRarity();
			String sub_type = data.getItem().getSubType();

			switch(sub_type){
				case "Head":
					cellImage = "icons_armor/icons_head/head" + cellRare + ".png";
					break;
				case "Body":
					cellImage = "icons_armor/icons_body/body" + cellRare + ".png";
					break;
				case "Arms":
					cellImage = "icons_armor/icons_arms/arms" + cellRare + ".png";
					break;
				case "Waist":
					cellImage = "icons_armor/icons_waist/waist" + cellRare + ".png";
					break;
				case "Legs":
					cellImage = "icons_armor/icons_legs/legs" + cellRare + ".png";
					break;
				case "Great Sword":
					cellImage = "icons_weapons/icons_great_sword/great_sword" + cellRare + ".png";
					break;
				case "Long Sword":
					cellImage = "icons_weapons/icons_long_sword/long_sword" + cellRare + ".png";
					break;
				case "Sword and Shield":
					cellImage = "icons_weapons/icons_sword_and_shield/sword_and_shield" + cellRare + ".png";
					break;
				case "Dual Blades":
					cellImage = "icons_weapons/icons_dual_blades/dual_blades" + cellRare + ".png";
					break;
				case "Hammer":
					cellImage = "icons_weapons/icons_hammer/hammer" + cellRare + ".png";
					break;
				case "Hunting Horn":
					cellImage = "icons_weapons/icons_hunting_horn/hunting_horn" + cellRare + ".png";
					break;
				case "Lance":
					cellImage = "icons_weapons/icons_lance/lance" + cellRare + ".png";
					break;
				case "Gunlance":
					cellImage = "icons_weapons/icons_gunlance/gunlance" + cellRare + ".png";
					break;
				case "Switch Axe":
					cellImage = "icons_weapons/icons_switch_axe/switch_axe" + cellRare + ".png";
					break;
				case "Charge Blade":
					cellImage = "icons_weapons/icons_charge_blade/charge_blade" + cellRare + ".png";
					break;
				case "Insect Glaive":
					cellImage = "icons_weapons/icons_insect_glaive/insect_glaive" + cellRare + ".png";
					break;
				case "Light Bowgun":
					cellImage = "icons_weapons/icons_light_bowgun/light_bowgun" + cellRare + ".png";
					break;
				case "Heavy Bowgun":
					cellImage = "icons_weapons/icons_heavy_bowgun/heavy_bowgun" + cellRare + ".png";
					break;
				case "Bow":
					cellImage = "icons_weapons/icons_bow/bow" + cellRare + ".png";
					break;
				default:
					cellImage = "icons_items/" + data.getItem().getFileLocation();
			}

			Drawable i = null;
			try {
				i = Drawable.createFromStream(
						context.getAssets().open(cellImage), null);
			} catch (IOException e) {
				e.printStackTrace();
			}

			itemImageView.setImageDrawable(i);


			root.setOnClickListener(new ItemClickListener(context, data.getItem()));

			root.setOnLongClickListener(new View.OnLongClickListener() {
											@Override
											public boolean onLongClick(View v) {
												WishlistDataDeleteDialogFragment dialogDelete = WishlistDataDeleteDialogFragment.newInstance(data.getId(), nameText);
												dialogDelete.setTargetFragment(WishlistDataDetailFragment.this, REQUEST_WISHLIST_DATA_DELETE);
												dialogDelete.show(getFragmentManager(), DIALOG_WISHLIST_DATA_DELETE);
												return true;
											}
										});
		}
	}

	// Define interface WishlistDetailPagerActivity must implement to refresh it's title
	public interface RefreshActivityTitle{
		void refreshTitle();
	}
}