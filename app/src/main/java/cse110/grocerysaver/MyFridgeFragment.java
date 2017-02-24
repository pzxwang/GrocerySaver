package cse110.grocerysaver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cse110.grocerysaver.database.MyFridgeDataSource;
import cse110.grocerysaver.database.MyFridgeHelper;

public class MyFridgeFragment extends Fragment {

    protected MyFridgeDataSource dataBase;
    private CustomListViewRowAdapter customAdapter;
    private ListView myFridgeList;
    private List<FoodItem> foodList;
    private EditText newItemName;
    private EditText newItemNotes;
    private EditText newItemExpDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_fridge,container,false);
        dataBase = new MyFridgeDataSource(getActivity());
        foodList = new ArrayList<>();

        //initialize fridge
        myFridgeList = (ListView) view.findViewById(R.id.myFridgeList);
        fetchMyFridge(view);
        myFridgeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
                View view = (LayoutInflater.from(getActivity())).inflate(R.layout.user_input, null);
                addNewItemScreen(view);
                return true;
            case R.id.menu_my_fridge_remove:
                SparseBooleanArray checkedItems = myFridgeList.getCheckedItemPositions();

                for (int i = myFridgeList.getCount()-1; i >= 0; i--) {
                    if (checkedItems.valueAt(i) == true) {
                        removeItemFromDb(foodList.get(i).getID());
                        foodList.remove(foodList.get(i));
                    }
                }
                unSelectItems();
                customAdapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }

    public void fetchMyFridge(View view) {
        dataBase.open();
        Cursor cursor = dataBase.selectAllAmount();
        cursor.moveToFirst();
        while( !cursor.isAfterLast() ) {
            int namePos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_NAME);
            int idPos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_ID);
            int notesPos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_NOTES);

            // TODO: add in dates for add/exp
            FoodItem toFetch = new FoodItem(cursor.getString(namePos), cursor.getString(idPos),
                    cursor.getString(notesPos), 0, 0);
            foodList.add(toFetch);

            cursor.moveToNext();
        }
        dataBase.close();
        customAdapter = new CustomListViewRowAdapter(getActivity(), foodList);
        myFridgeList.setAdapter(customAdapter);
    }

    public void storeNewItemInDb() {
        dataBase.open();
        String food_name = newItemName.getText().toString();
        FoodItem addItem = new FoodItem(food_name);
        foodList.add(addItem);
        String itemId = addItem.getID();
        String food_eDate = newItemExpDate.getText().toString();
        Integer v1 = Integer.parseInt(food_eDate);
        String food_notes = newItemNotes.getText().toString();
        dataBase.insertRow(food_name, food_notes, 5, v1, itemId);
        dataBase.close();
    }

    public void removeItemFromDb(String itemId) {
        dataBase.open();
        dataBase.deleteItem(itemId);
        dataBase.close();
    }

    public void addNewItemScreen(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setView(view);
        newItemName = (EditText) view.findViewById(R.id.nameInput);
        newItemNotes = (EditText) view.findViewById(R.id.notesInput);
        newItemExpDate = (EditText) view.findViewById(R.id.expInput);

        alertBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storeNewItemInDb();
                        customAdapter.notifyDataSetChanged();
                    }
                });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    //unset any checked items after any action is performed
    private void unSelectItems() {
        for (int i = 0; i < myFridgeList.getCount(); i++) {
            myFridgeList.setItemChecked(i, false);
        }
    }
}

