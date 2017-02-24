package cse110.grocerysaver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
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
    private Button delBtn;
    private CustomListViewRowAdapter customAdapter;
    private ListView myFridgeList;
    private List<FoodItem> foodList;
    private Button addBtn;
    private EditText newItemName;
    private EditText newItemNotes;
    private EditText newItemExpDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_fridge,container,false);
        delBtn = (Button) view.findViewById(R.id.deleteButton);
        dataBase = new MyFridgeDataSource(getActivity());
        foodList = new ArrayList<>();

        //initialize fridge
        myFridgeList = (ListView) view.findViewById(R.id.myFridgeList);
        fetchMyFridge(view);
        myFridgeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Creates an input menu on clicking add button that stores in MyFridge database
        addBtn = (Button) view.findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = (LayoutInflater.from(getActivity())).inflate(R.layout.user_input, null);
                addNewItemScreen(view);
            }
        });

        // listener to delete items
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = myFridgeList.getCheckedItemPositions();
                // get list of all items to delete so we don't mess with
                //List<String> deleteIds = new ArrayList<String>();
                System.out.println("num items: " + myFridgeList.getCount());
                for (int i = myFridgeList.getCount()-1; i >= 0; i--) {
                    if (checkedItems.valueAt(i) == true) {
                        System.out.println("food name is: " + foodList.get(i).getFoodName());
                        removeItemFromDb(foodList.get(i).getID());
                        foodList.remove(foodList.get(i));
                    }
                }
                unSelectItems();
                customAdapter.notifyDataSetChanged();
            }


        });
        return view;
    }

    public void fetchMyFridge(View view) {
        dataBase.open();
        Cursor cursor = dataBase.selectAllAmount();
        cursor.moveToFirst();
        while( !cursor.isAfterLast() ) {
            int namePos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_NAME);
            //int idPos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_ID);
            //int notesPos = cursor.getColumnIndex(MyFridgeHelper.COLUMN_NOTES);

            // TODO: add in dates for add/exp
            //FoodItem toFetch = new FoodItem(getString(namePos), getString(idPos), getString(notesPos), 0, 0);
            //foodList.add(toFetch);

            foodList.add(new FoodItem(cursor.getString(namePos)));

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

