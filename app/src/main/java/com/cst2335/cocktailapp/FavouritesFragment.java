package com.cst2335.cocktailapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * This class contains the onCreate() method which is called when the activity first starts,
 * it shows a list of previously saved cocktails. Lets the user click on a cocktail from the list
 * to check for its details, where the user will be able to remove cocktail from favourites list.
 *
 * @author Maria Alarcon
 */
public class FavouritesFragment extends Fragment {

    /** stores objects of Cocktail class */
    ArrayList<Cocktail> cocktailArrayList = new ArrayList<>();
    /** MyListAdapter datatype used for the list */
    MyListAdapter myAdapter;
    /** favourites list view */
    ListView listView;
    /** To create an instance of CocktailDatabase */
    CocktailDatabase dbHelper;
    /** To create SQLiteDatabse object use for SQL queries */
    SQLiteDatabase dbWriter;
    /** To store cocktail details retrieved from the database */
    Cursor cursorCocktails;
    /** to create progress bar*/
    ProgressBar progressBar;

    /**
     * Called when activity starts.
     * @param savedInstanceState null when the activity first starts,
     *                          and contain the data from the previous session if activity already started.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Called to initiate the fragment view, favourites list, called after onCreate(),
     * it gets the information from the database and displays it on a list view,
     * the listview lets user access the cocktail details when pressed
     * or delete items with long click (this will delete the item from the database
     * and favourites list), it also contains an undo button.
     *
     * @param inflater              The LayoutInflater datatype that can be used to inflate any views in the fragment.
     * @param parent                The MainActiviy's view that this fragment is attached to.
     * @param savedInstanceState    The saved instances.
     * @return The View for favourites list UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_favourites, parent, false);
        listView = frag.findViewById(R.id.lvFavourites);
        listView.setAdapter(myAdapter = new MyListAdapter());
        progressBar = frag.findViewById(R.id.pvFavourites);

        TextView tvCocktailName = frag.findViewById(R.id.tvSearchedItems);

        dbHelper = new CocktailDatabase(getContext());
        dbWriter = dbHelper.getWritableDatabase();

        cursorCocktails = dbWriter.rawQuery( "Select * from "
                + dbHelper.TABLE_NAME + ";", null );

        //Convert column names to indices:
        int idIndex = cursorCocktails.getColumnIndex(dbHelper.COL_ID);
        int cocktailIdIndex = cursorCocktails.getColumnIndex(dbHelper.COL_COCKTAIL_ID);
        int nameIndex = cursorCocktails.getColumnIndex(dbHelper.COL_NAME);
        int imageDataIndex = cursorCocktails.getColumnIndex(dbHelper.COL_IMAGE_DATA);
        int categoryIndex = cursorCocktails.getColumnIndex(dbHelper.COL_CATEGORY);
        int prepInstructionsIndex = cursorCocktails.getColumnIndex(dbHelper.COL_PREP_INSTRUCTIONS);
        int urlIndex = cursorCocktails.getColumnIndex(dbHelper.COL_URL);

        /**
         * Loads the data from the database
         */
        while (cursorCocktails.moveToNext()) {
            int dbId = cursorCocktails.getInt(idIndex);
            int cocktailId = cursorCocktails.getInt(cocktailIdIndex);
            String name = cursorCocktails.getString(nameIndex);

            byte[] image = cursorCocktails.getBlob(imageDataIndex);
            Bitmap imageData  = byteArrayToBitmap(image);

            String category = cursorCocktails.getString(categoryIndex);
            String prepInstructions = cursorCocktails.getString(prepInstructionsIndex);
            String urlColumn = cursorCocktails.getString(urlIndex);

            Cocktail myCocktail = new Cocktail(dbId, cocktailId, name, imageData, category, prepInstructions, urlColumn);
            cocktailArrayList.add(myCocktail);
            myAdapter.notifyDataSetChanged();
        }
        cursorCocktails.close();

        /**
         * ClickListener that has the intent to go tothe CocktailDetails Activity,
         * it passes the data selected from the lits to the CocktailDetails Activity.
         */
        listView.setOnItemClickListener((cocktailArrayList, view, position, id)->{
            long itemId = id;
            long cocktailId = myAdapter.getItem(position).getCocktailId();
            String cocktailName = myAdapter.getItem(position).getName();
            Bitmap cocktailImage = myAdapter.getItem(position).getImage();
            String categoryCocktailCategory = myAdapter.getItem(position).getCategory();
            String instructionsCocktail = myAdapter.getItem(position).getInstructions();
            String URLCocktail = myAdapter.getItem(position).getURL();
            byte[] byteArray = bitmapToByteArray(cocktailImage);

            Intent toCocktailDetails = new Intent(getContext(), CocktailDetails.class);

            toCocktailDetails.putExtra("id", itemId);
            toCocktailDetails.putExtra("cocktailId", cocktailId);
            toCocktailDetails.putExtra("name", cocktailName);
            toCocktailDetails.putExtra("image", byteArray);
            toCocktailDetails.putExtra("category", categoryCocktailCategory);
            toCocktailDetails.putExtra("instructions", instructionsCocktail);
            toCocktailDetails.putExtra("url", URLCocktail);
            toCocktailDetails.putExtra("isFavourites", true);
            startActivity(toCocktailDetails);
        });

        /**
         * Allows delete items from the list and database as well as re inserting them
         * when pressing undo.
         */
        listView.setOnItemLongClickListener((p,b, position, id)->{
            Cocktail removedCocktail = cocktailArrayList.get(position);
            progressBar.setVisibility(View.VISIBLE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getResources().getString(R.string.delete)+ removedCocktail.getName())
                    .setMessage(getResources().getString(R.string.id) + removedCocktail.getId())
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, click) ->{
                        cocktailArrayList.remove(position);
                        myAdapter.notifyDataSetChanged();
                        //delete from database:
                        dbHelper.deleteItem(Long.toString( removedCocktail.getId()));
                        progressBar.setVisibility(View.GONE);
                        //Snackbar to Undo
                        Snackbar.make(listView, getResources().getString(R.string.Itemdeleted), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(R.string.undo), c->{
                                    cocktailArrayList.add(position,removedCocktail);
                                    myAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    Bitmap bitmapImage = removedCocktail.getImage();
                                    byte[] byteArray = bitmapToByteArray(bitmapImage);//method converts to byteArray
                                    dbHelper.insertRow(removedCocktail.getCocktailId(), removedCocktail.getName(), byteArray,
                                            removedCocktail.getCategory(), removedCocktail.getInstructions(), removedCocktail.getURL());
                                })
                                .show();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, click)->{progressBar.setVisibility(View.GONE); })
                    .create().show();
            return true;
        });
        return frag;
    }

    /**
     * Converts bitmap into an array of bytes.
     *
     * @param bitmapImage The image to be converted to bytes
     * @return image array of bytes
     */
    private byte[] bitmapToByteArray(Bitmap bitmapImage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();
        return imageByteArray;
    }

    /**
     * Converts array of bytes into bitmap.
     *
     * @param byteArray The array of bytes
     * @return a bitmap image
     */
    private Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap imageData = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return imageData;
    }

    /**
     * This class implements BaseAdapter to use it on the list view for display information.
     * This provides access to the items and makes a View for each item in the data set.
     */
    private class MyListAdapter extends BaseAdapter {

        /**
         * Gets the size of the array list.
         * @return returns the size of the array list.
         */
        @Override
        public int getCount() {
            return cocktailArrayList.size();
        }

        /**
         * Gets the item based on the position passed.
         * @param position  The position of the clicked item.
         * @return returns the object stored in the array list.
         */
        @Override
        public Cocktail getItem(int position) {
            return cocktailArrayList.get(position);
        }

        /**
         * Gets the id of the selected item on the list.
         * @param position  The position of the clicked item.
         * @return returns the id of the item stored in the array list.
         */
        @Override
        public long getItemId(int position) {
            return cocktailArrayList.get(position).getId();
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
            tView.setText(cocktailArrayList.get(position).getName());

            ImageView imageView = newView.findViewById(R.id.imageView);
            imageView.setImageBitmap(cocktailArrayList.get(position).getImage());
            return newView;
        }
    }
}