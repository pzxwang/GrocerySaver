package cse110.grocerysaver;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites,container,false);

        //FoodItem [] foodItemList = {(new FoodItem("apple")),(new FoodItem("peach"))};

        final List<FoodItem> foodItemList = new ArrayList<>();
        foodItemList.add(new FoodItem("apple"));
        foodItemList.add(new FoodItem("pear"));
        foodItemList.add(new FoodItem("peach"));
        foodItemList.add(new FoodItem("banana"));

        ListAdapter customAdapter = new CustomListViewRowAdapter(getActivity(),foodItemList);
        ListView favoriteList = (ListView) view.findViewById(R.id.favoriteList);
        favoriteList.setAdapter(customAdapter);

        return view;
    }

}
