package cse110.grocerysaver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import cse110.grocerysaver.database.DatabaseContract;
import cse110.grocerysaver.database.Favorite;
import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.PersistableManager;
import cse110.grocerysaver.database.ProviderContract;

import cse110.grocerysaver.Emoji.*;

import static android.view.LayoutInflater.from;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    PersistableManager persistableManager;

    ListView listView;
    TextView emptyView;

    private class RowAdapter extends CursorAdapter implements View.OnClickListener {

        HashSet<Long> selectedItems = new HashSet<>();
        ArrayList<ViewHolder> holders = new ArrayList<>();
        int count = 0;

        public RowAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = from(context).inflate(R.layout.adapter_favorites_row, parent, false);
            FavoritesFragment.ViewHolder holder = new FavoritesFragment.ViewHolder();

            holder.checkBox = (CheckBox) view.findViewById(R.id.adapter_favorites_row_check_box);
            holder.foodName = (TextView) view.findViewById(R.id.adapter_favorites_row_name);
            holders.add(holder);
            view.setTag(count++);

            holder.checkBox.setOnClickListener(this);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.FridgeItem._ID));
            int tag = (Integer) view.getTag();
            CheckBox checkBox = holders.get(tag).checkBox;
            TextView foodName = holders.get(tag).foodName;

            Favorite favorite = (Favorite) persistableManager.initializedPersistable(Favorite.class, cursor);
            foodName.setText(favorite.getName());

            checkBox.setTag(id);

            if (selectedItems.contains(id)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view;
            if (checkBox.isChecked()) {
                selectedItems.add((Long) checkBox.getTag());
            } else {
                selectedItems.remove(checkBox.getTag());
            }

            if (actionMode == null) {
                actionMode = getActivity().startActionMode(actionModeCallback);
            }

            if (selectedItems.size() == 0) {
                actionMode.finish();
            } else {
                actionMode.setTitle(String.valueOf(selectedItems.size()));
            }
        }
    }

    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.favorites_select_context, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_favorites_remove:
                    for (Long id : adapter.selectedItems) {
                        Favorite favorite = (Favorite) persistableManager.findByID(Favorite.class, id);
                        persistableManager.delete(favorite);
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "Items have been deleted",
                            Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                case R.id.menu_favorites_refrigerate:
                    for (Long id : adapter.selectedItems) {

                        Favorite favorite = (Favorite) persistableManager.findByID(Favorite.class, id);
                        FridgeItem fridgeItem = new FridgeItem();

                        Calendar newExpDate = Calendar.getInstance();
                        newExpDate.setTimeInMillis(
                                System.currentTimeMillis() + favorite.getShelfLife());

                        fridgeItem.setName(favorite.getName());
                        fridgeItem.setNotes(favorite.getNotes());
                        fridgeItem.setDateAdded(Calendar.getInstance());
                        fridgeItem.setExpirationDate(newExpDate);

                        persistableManager.save(fridgeItem);
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "Items have been added to My Fridge",
                            Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.selectedItems.clear();
            adapter.notifyDataSetChanged();
        }
    };

    RowAdapter adapter;
    ActionMode actionMode;

    public FavoritesFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new FavoritesFragment.RowAdapter(getActivity(), null, 0);
        persistableManager = new PersistableManager(getActivity());

        listView = (ListView) getActivity().findViewById(R.id.list);
        emptyView = (TextView) getActivity().findViewById(R.id.empty);

        listView.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
        emptyView.setVisibility(View.GONE);
        emptyView.setText("Favorites is empty. " + Emoji.e(0x1f610) + "\n\n" + "Add food from here or My Fridge. " + Emoji.e(0x1f34c));
        listView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.favorites);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorites, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorites_add:
                Intent intent = new Intent(getActivity(), AddFavoriteActivity.class);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ProviderContract.uriForTable(DatabaseContract.Favorite.TABLE),
                new String[] {
                        DatabaseContract.Favorite._ID,
                        DatabaseContract.Favorite.COLUMN_NAME
                }, null, null, DatabaseContract.FridgeItem.COLUMN_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

        if (data.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView foodName;
    }

}
