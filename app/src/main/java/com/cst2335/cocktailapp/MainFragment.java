package com.cst2335.cocktailapp;

import static android.content.Context.MODE_PRIVATE;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Main fragment contains the recent view items and the framelayout to inflate the search and favourites fragment
 */
public class MainFragment extends Fragment {
    public static final String TAG ="Cocktails";
    ArrayList<Cocktail> cocktailDetailsList = new ArrayList<>();
    MyListAdapter myAdapter;
    ListView listView;
    /** to create progress bar*/
    ProgressBar progressBar;

    /**
     * onCreate Method
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Method inflates fragment_main.xml and shows the list of cocktails
     * @param inflater
     * @param parent
     * @param savedInstanceState
     * @return frag
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View frag=inflater.inflate(R.layout.fragment_main, parent, false);
        listView = frag.findViewById(R.id.list);
        listView.setAdapter(myAdapter=new MyListAdapter());
        ImageButton searchButton=frag.findViewById(R.id.imageButton);
        EditText input=frag.findViewById(R.id.cocktailInput);
        progressBar = frag.findViewById(R.id.pvMainFragment);
        searchButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Method to create new intent to launch SearchDetails activity
             * on a new page when an item is clicked
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent newPage=new Intent(getActivity(),SearchDetails.class);
                newPage.putExtra("Cocktail",input.getText().toString());
                startActivity(newPage);
            }
        });

        //SharedPreferences to store the details of the Cocktail selected
        SharedPreferences shPref=getActivity().getSharedPreferences(TAG,MODE_PRIVATE);
        Long cid=shPref.getLong("CocktailId",0);
        String name=shPref.getString("CocktailName","");
        String picture=shPref.getString("CocktailPicture","");
        String category=shPref.getString("CocktailCategory","");
        String instructions=shPref.getString("CocktailInstructions","");
        String url=shPref.getString("CocktailUrl","");
        byte[] decodedString = Base64.decode(picture.getBytes(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //object of Cocktail class
        Cocktail cocktailInfo = new Cocktail(cid,name, bmp, category,instructions,url);
        //cocktailDetailsList for the details of the selected cocktail
        cocktailDetailsList.add(cocktailInfo);
        myAdapter.notifyDataSetChanged();
        //listview for the details of the selected Cocktail
        listView.setOnItemClickListener((cocktailDetailsList, view, position, id)->{
            progressBar.setVisibility(View.VISIBLE);
            Long cocktailId = myAdapter.getItem(position).getId();
            String cocktailName = myAdapter.getItem(position).getName(); //cocktail name
            String cocktailImageUrl = myAdapter.getItem(position).getImageUrl(); //image from web
            Bitmap cocktailImage = myAdapter.getItem(position).getImage(); //Bitmap form
            String categoryCocktailCategory = myAdapter.getItem(position).getCategory();
            String instructionsCocktail = myAdapter.getItem(position).getInstructions(); //instructions
            String URLCocktail = myAdapter.getItem(position).getURL(); //get url

            //converts the Byte source from web to Bitmap to be displayed
            //Retreived from: https://jayrambhia.com/blog/pass-activity-bitmap
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            cocktailImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();
            Intent newPage=new Intent(getActivity(),CocktailDetails.class);
            newPage.putExtra("id", cocktailId);
            newPage.putExtra("name", cocktailName);
            newPage.putExtra("category", categoryCocktailCategory);
            newPage.putExtra("instructions", instructionsCocktail);
            newPage.putExtra("image", byteArray);
            newPage.putExtra("url", URLCocktail);
            newPage.putExtra("image_url", cocktailImageUrl);

//            progressBar.setVisibility(View.GONE);
            //starts the activity on anew page
            startActivity(newPage);
        });
        return frag; //returns the built fragment
    }

    /**
     * onViewCreated method
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    /**
     * @extends BaseAdapter class
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * Method to return the size of the cocktailDetailsList
         * @return int
         */
        @Override
        public int getCount() {
            return cocktailDetailsList.size();
        }

        /**
         * Method to get the object of Cocktail class
         * @param position
         * @return Cocktail
         */
        @Override
        public Cocktail getItem(int position) {
            return cocktailDetailsList.get(position);
        }

        /**
         * Method to get the id of cocktailDetailsList item
         * @param position
         * @return long
         */
        @Override
        public long getItemId(int position) {
            return cocktailDetailsList.get(position).getId();
        }

        /**
         * Method to get the View object
         * @param position
         * @param convertView
         * @param parent
         * @return View
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            //make a new row:
            View newView = inflater.inflate(R.layout.layout_row, parent, false);

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.tvSearchedItems);
            tView.setText(cocktailDetailsList.get(position).getName());

            ImageView imageView = newView.findViewById(R.id.imageView);
            imageView.setImageBitmap(cocktailDetailsList.get(position).getImage());

            //return it to be put in the table
            return newView;
        }
    }

}