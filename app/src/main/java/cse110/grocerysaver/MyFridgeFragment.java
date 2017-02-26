package cse110.grocerysaver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import cse110.grocerysaver.database.DatabaseContract;
import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.ProviderContract;

public class MyFridgeFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActionMode actionMode = null;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.my_fridge_select_context, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_my_fridge_remove:
                    for (Long id : adapter.selectedItems) {
                        FridgeItem.findByID(getContext(), id.longValue()).delete();
                    }
                    actionMode.finish();
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

    static  class ViewHolder {
        CheckBox checkBox;
        TextView foodName;
        TextView expirationDate;
    }

    private class RowAdapter extends CursorAdapter implements CompoundButton.OnClickListener {
        HashSet<Long> selectedItems = new HashSet<>();
        ArrayList<ViewHolder> holders = new ArrayList<>();

        int count = 0;

        public RowAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_fridge_item_row, parent, false);
            ViewHolder holder = new ViewHolder();

            holder.checkBox = (CheckBox) view.findViewById(R.id.adapter_fridge_item_row_check_box);
            holder.foodName = (TextView) view.findViewById(R.id.adapter_fridge_item_row_name);
            holder.expirationDate = (TextView) view.findViewById(R.id.adapter_fridge_item_row_expiration_date);

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
            TextView expirationDate = holders.get(tag).expirationDate;

            DateFormat format = new SimpleDateFormat("MMM dd");
            FridgeItem fridgeItem = new FridgeItem(context, cursor);
            String expirationDateString = format.format(fridgeItem.getExpirationDate().getTime());

            foodName.setText(fridgeItem.getName());
            expirationDate.setText(expirationDateString);

            checkBox.setTag(Long.valueOf(id));

            if (selectedItems.contains(id)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            if (checkBox.isChecked()) {
                selectedItems.add((Long) checkBox.getTag());
            } else {
                selectedItems.remove((Long) checkBox.getTag());
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

    private static final String[] COLUMNS = {
            DatabaseContract.FridgeItem._ID,
            DatabaseContract.FridgeItem.COLUMN_NAME,
            DatabaseContract.FridgeItem.COLUMN_DATE_ADDED,
            DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE
    };

    private RowAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RowAdapter(getActivity(), null, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_fridge, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.my_fridge);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_fridge, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_fridge_add:
                Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                getActivity().startActivity(intent);
                return true;
        }
        return false;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(), ProviderContract.uriForTable(DatabaseContract.FridgeItem.TABLE),
                COLUMNS, null, null, DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

