package com.cst2335.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

/**
 * This class is to collect information enterred in the main and uses asynckTask
 * to retrieve data from cocktailDB and printing it out into a listView
 * @author Ingabire Drice Michaella
 */
public class SearchDetails extends AppCompatActivity {
    public static final String SEARCH ="List";
    public static final String TAG ="Cocktails";
    public static final String TAG1 ="Save";
    ArrayList<Cocktail> cocktailDetailsList = new ArrayList<>();
    MyListAdapter myAdapter;
    ListView listView;
    String inputStr;
    String input;

    /**
     * Called when activity starts
     * @param savedInstanceState null when the activity first starts,
     *                           and contain the data from the previous session if activity already started.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(getResources().getString(R.string.SearchName));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProgressBar pg=findViewById(R.id.progressBar);

        Intent fromMain = getIntent();
        inputStr = fromMain.getStringExtra("Cocktail");

        SharedPreferences sp=getSharedPreferences(TAG1,MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("nameInput",inputStr);
        ed.apply();
        SharedPreferences shPref=getSharedPreferences(TAG1,MODE_PRIVATE);
        input=shPref.getString("nameInput","");

        listView = findViewById(R.id.listView);
        listView.setAdapter(myAdapter=new MyListAdapter());
        //intializing MyHTTPRequest class
        MyHTTPRequest req = new MyHTTPRequest();
        //execute the MyHTTPRequest
        req.execute("https://www.thecocktaildb.com/api/json/v1/1/search.php?s="+ inputStr.toLowerCase());


        listView.setOnItemClickListener((cocktailDetailsList, view, position, id)->{
            pg.setVisibility(View.VISIBLE);
            long itemId = id;
            long cocktailId = myAdapter.getItem(position).getCocktailId();
            String cocktailName = myAdapter.getItem(position).getName();
            String cocktailImageUrl = myAdapter.getItem(position).getImageUrl();
            Bitmap cocktailImage = myAdapter.getItem(position).getImage();
            String categoryCocktailCategory = myAdapter.getItem(position).getCategory();
            String instructionsCocktail = myAdapter.getItem(position).getInstructions();
            String URLCocktail = myAdapter.getItem(position).getURL();

            //Retreived from: https://jayrambhia.com/blog/pass-activity-bitmap
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            cocktailImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();

            SharedPreferences shPreferences=getSharedPreferences(TAG,MODE_PRIVATE);
            String name=shPreferences.getString("CocktailName","");

            SharedPreferences sharedPreferences=getSharedPreferences(TAG,MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong("CocktailId",cocktailId);
            editor.putString("CocktailName",cocktailName);
            editor.putString("CocktailCategory",categoryCocktailCategory);
            editor.putString("CocktailInstructions",instructionsCocktail);
            editor.putString("CocktailUrl",URLCocktail);
            String EncodeImage= Base64.getEncoder().encodeToString(byteArray);
            editor.putString("CocktailPicture",EncodeImage);
            editor.apply();


            Intent toCocktailDetails = new Intent(this, CocktailDetails.class);

            toCocktailDetails.putExtra("id", itemId);
            toCocktailDetails.putExtra("cocktailId", cocktailId);
            toCocktailDetails.putExtra("name", cocktailName);
            toCocktailDetails.putExtra("image", byteArray);
            toCocktailDetails.putExtra("category", categoryCocktailCategory);
            toCocktailDetails.putExtra("instructions", instructionsCocktail);
            toCocktailDetails.putExtra("url", URLCocktail);
            toCocktailDetails.putExtra("isFavourites", false);

            startActivity(toCocktailDetails);
        });
    }

    /**
     * This class contains AsyncTask that will be used to retrieve data from the cocktailDB website
     */
    private class MyHTTPRequest extends AsyncTask<String,Integer,String> {

        static private final String TAG = "MyHTTPRequest";

        /**
         * This function will be used to access the CocktailDB
         * @param args it is an array of strings
         * @return string enterred int the search bar
         */
        @Override
        protected String doInBackground(String... args) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string
                JSONObject uvReport = new JSONObject(result);
                JSONArray drinksArray = uvReport.getJSONArray("drinks");
                // convert string to JSON: Look at slide 27:
                for(int i=0;i<drinksArray.length();i++){
                    try{
                        JSONObject jasonObject = drinksArray.getJSONObject(i);
                        String icon = jasonObject.getString("strDrinkThumb");
                        Bitmap imge = null;
                        try {
                            URL urlImg = new URL(icon);
                            imge = BitmapFactory.decodeStream(new BufferedInputStream(urlImg.openConnection().getInputStream()));
                        } catch(IOException e) {
                            System.out.println(e);
                        }

                        String cocktailName = jasonObject.getString("strDrink");
                        Long cocktailId = jasonObject.getLong("idDrink");
                        String category = jasonObject.getString("strCategory");
                        String instructions = jasonObject.getString("strInstructions");
                        String urlCocktail = ("https://www.thecocktaildb.com/drink/" + cocktailId);

                        Cocktail cocktailInfo = new Cocktail(cocktailId, cocktailName, imge, category, instructions, urlCocktail);
                        cocktailDetailsList.add(cocktailInfo);
                    }
                    catch(JSONException exception){}

                }
                myAdapter.notifyDataSetChanged();

            }catch (Exception e){

            }

            return inputStr;
        }

        /**
         *This function used to update the GUI
         * @param args it is an array of integer
         */
        public void onProgressUpdate(Integer ... args)
        {
            Log.i(TAG, "onProgressUpdate");
        }

        /**
         * This function will invoke doInBackground()
         * @param fromDoInBackground  calling doInBackground
         */
        public void onPostExecute(String fromDoInBackground)
        {
            Log.i(TAG, fromDoInBackground);
            myAdapter.notifyDataSetChanged();
        }

    }

    /**
     * This class implements BaseAdapter to use it on the list view for display information.
     * This provides access to the items and makes a View for each item in the data set.
     */
    private class MyListAdapter extends BaseAdapter{
        /**
         * Gets the size of the array list.
         * @return returns the size of the array list.
         */
        @Override
        public int getCount() {
            return cocktailDetailsList.size();
        }

        /**
         * Gets the item based on the position passed.
         * @param position  The position of the clicked item.
         * @return returns the object stored in the array list.
         */
        @Override
        public Cocktail getItem(int position) {
            return cocktailDetailsList.get(position);
        }

        /**
         * Gets the id of the selected item on the list.
         * @param position  The position of the clicked item.
         * @return returns the id of the item stored in the array list.
         */
        @Override
        public long getItemId(int position) {
            return cocktailDetailsList.get(position).getId();
        }

        /**
         * Inflates the layout_row view to make a View for each item in the data set.
         * It sets the value for the cocktail image and cocktail name.
         * @param position      The position of the item in the database
         * @param convertView   The view to be converted
         * @param parent        The view for parent
         * @return returns the new inflated view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            //make a new row:
            View newView = inflater.inflate(R.layout.layout_row, parent, false);

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.tvSearchedItems);

            tView.setText( cocktailDetailsList.get(position).getName());

            ImageView imageView = newView.findViewById(R.id.imageView);

            imageView.setImageBitmap( cocktailDetailsList.get(position).getImage());

            //return it to be put in the table
            return newView;
        }
    }

    /**
     * onPause will be used to store the inputted value from the main page
     */
    @Override
    protected void onPause() {
        super.onPause();
        Intent fromMain = getIntent();
        inputStr = fromMain.getStringExtra("Cocktail");
        SharedPreferences shp=getSharedPreferences(TAG1,MODE_PRIVATE);
        SharedPreferences.Editor ed=shp.edit();
        ed.putString("nameInput",inputStr);
        ed.apply();

    }

}