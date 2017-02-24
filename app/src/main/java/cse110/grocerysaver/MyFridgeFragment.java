package cse110.grocerysaver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cse110.grocerysaver.database.MyFridgeDataSource;
import cse110.grocerysaver.database.MyFridgeHelper;

import static android.app.Activity.RESULT_OK;

public class MyFridgeFragment extends Fragment {

    /* Request code */
    static final int NEW_FOOD_ITEM = 1;

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
                addFood();
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

    private void addFood() {
        Intent intent = new Intent(getActivity(), AddFoodActivity.class);
        startActivityForResult(intent, NEW_FOOD_ITEM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_FOOD_ITEM) {
            if (resultCode == RESULT_OK) {
                dataBase.open();
                String foodName = data.getStringExtra("foodName");
                String foodExpDate = data.getStringExtra("foodExpDate");
                String foodNotes = data.getStringExtra("foodNotes");

                FoodItem foodItem = new FoodItem(foodName);
                foodList.add(foodItem);

                dataBase.insertRow(foodName, foodNotes, 5, 666, foodItem.getID());
                dataBase.close();
            }
        }
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

    public void removeItemFromDb(String itemId) {
        dataBase.open();
        dataBase.deleteItem(itemId);
        dataBase.close();
    }

    //unset any checked items after any action is performed
    private void unSelectItems() {
        for (int i = 0; i < myFridgeList.getCount(); i++) {
            myFridgeList.setItemChecked(i, false);
        }
    }
}

