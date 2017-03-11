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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import cse110.grocerysaver.database.DatabaseContract;
import cse110.grocerysaver.database.Favorite;
import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.PersistableManager;
import cse110.grocerysaver.database.ProviderContract;

import cse110.grocerysaver.Emoji.*;

import static android.view.LayoutInflater.*;

public class MyFridgeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    PersistableManager persistableManager;
    ListView listView;
    TextView emptyView;

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
                        FridgeItem fridgeItem = (FridgeItem) persistableManager.findByID(FridgeItem.class, id);

                        persistableManager.delete(fridgeItem);
                    }
                    actionMode.finish();
                    return true;
                case R.id.menu_my_fridge_favorite:
                    for (Long id : adapter.selectedItems) {
                        FridgeItem fridgeItem = (FridgeItem) persistableManager.findByID(FridgeItem.class, id);
                        Favorite favorite = new Favorite();

                        favorite.setName(fridgeItem.getName());
                        favorite.setShelfLife(fridgeItem.getShelfLife());
                        favorite.setNotes(fridgeItem.getNotes());

                        persistableManager.save(favorite);
                    }
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

    static class ViewHolder {
        CheckBox checkBox;
        TextView foodName;
        TextView expirationDate;
    }

    private class RowAdapter extends CursorAdapter implements CompoundButton.OnClickListener {
        HashSet<Long> selectedItems = new HashSet<>();
        ArrayList<ViewHolder> holders = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("MMM d");

        int count = 0;

        RowAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = from(context).inflate(R.layout.adapter_fridge_item_row, parent, false);
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
            int tag = (Integer) view.getTag();
            CheckBox checkBox = holders.get(tag).checkBox;
            TextView foodName = holders.get(tag).foodName;
            TextView expirationDate = holders.get(tag).expirationDate;
            FridgeItem fridgeItem = (FridgeItem) persistableManager.initializedPersistable(FridgeItem.class, cursor);
            String expirationDateString = format.format(fridgeItem.getExpirationDate().getTime());

            foodName.setText(fridgeItem.getName());
            expirationDate.setText(expirationDateString);

            checkBox.setTag(fridgeItem.getID());

            if (fridgeItem.isExpired()) {
                view.setBackgroundResource(R.color.colorExpired);
            } else {
                view.setBackgroundResource(R.color.colorNotExpired);
            }

            if (selectedItems.contains(fridgeItem.getID())) {
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
        persistableManager = new PersistableManager(getActivity());
        listView = (ListView) getActivity().findViewById(R.id.list);
        emptyView = (TextView) getActivity().findViewById(R.id.empty);

        getLoaderManager().initLoader(0, null, this);
        emptyView.setVisibility(View.GONE);
        emptyView.setText("My Fridge is empty. " + Emoji.e(0x1f610) + "\n\n" + "Add food to start tracking them. " + Emoji.e(0x1f34c));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                intent.putExtra("EXTRA_FRIDGE_ITEM_ID", l);
                getActivity().startActivity(intent);
            }
        });
    }

//    @Override
//    public void onListItemClick(ListView listView, View view, int position, long id) {
//        Intent intent = new Intent(getActivity(), AddFoodActivity.class);
//        intent.putExtra("EXTRA_FRIDGE_ITEM_ID", id);
//        getActivity().startActivity(intent);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_fridge, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (actionMode != null) {
            actionMode.finish();
        }
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
            case R.id.menu_my_fridge_clean:
                int num = persistableManager.delete(FridgeItem.class,
                        DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE + " <= ?",
                        new String[] { String.valueOf(Calendar.getInstance().getTimeInMillis()) });
                if (num == 0) {
                    Toast.makeText(getActivity(),
                            "No expired items to remove. " + Emoji.e(0x1F642),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "Removed " + num + " expired fridge item" + (num > 1 ? "s" : "") + ". " + Emoji.e(0x1F61E),
                            Toast.LENGTH_LONG).show();
                }
        }
        return false;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(), ProviderContract.uriForTable(DatabaseContract.FridgeItem.TABLE),
                COLUMNS, null, null, DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE + " ASC"
        );
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
}

