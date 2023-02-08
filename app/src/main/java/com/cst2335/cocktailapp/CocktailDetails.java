package com.cst2335.cocktailapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Class is an activity that shows the details of a searched cocktail and gives the option
 * to save it or remove it from the database if cocktail already exists on database.
 * It also contains the link of the cocktail' website.
 *
 * @author Van Nguyen
 */
public class CocktailDetails extends AppCompatActivity {
    /** stores object of Cocktail class     */
    ArrayList<Cocktail> cocktails = new ArrayList<Cocktail>();
    /** stores the id from the database */
    String idGlobal = null;
    /** if cocktail is on the database*/
    Boolean found = false;
    /** importing the progress bar */
    ProgressBar progressBar;


    /**
     * Called when activity starts and inflates the layout for cocktail details.
     * @param savedInstanceState null when the activity first starts,
     *                          and contain the data from the previous session if activity already started.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_details);

        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Cocktail App: Van Nguyen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CocktailDatabase dbHelper = new CocktailDatabase(getApplicationContext());
        SQLiteDatabase dbWriter = dbHelper.getWritableDatabase();

        Button saveButton = (Button) findViewById(R.id.btnSaveToFavourites);
        Button removeButton = (Button) findViewById(R.id.btnRemoveFromFavourites);

        progressBar = findViewById(R.id.pvCocktailDetails);

        TextView tvCocktailName = findViewById(R.id.tvDrinkName);
        ImageView ivCocktailImage = findViewById(R.id.ivDrink);
        TextView tvCocktailCategory = findViewById(R.id.tvCategory);
        TextView tvCocktailInstructions = findViewById(R.id.tvPrepInstructions);
        TextView tvCocktailURL = findViewById(R.id.tvURL);

        Intent intent = getIntent();

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = byteArrayToBitmap(byteArray); //method

        long id = intent.getLongExtra("id", 0);
        long idCocktail = intent.getLongExtra("cocktailId", 0);
        String idCocktailString = intent.getStringExtra("cocktailId");
        String nameCocktail = intent.getStringExtra("name");
        String categoryCocktail = intent.getStringExtra("category");
        String instructionsCocktail = intent.getStringExtra("instructions");
        String urlCocktail = intent.getStringExtra("url");


        //to verify if cocktail is already on database
        try{
            Cursor cursorIds = dbWriter.rawQuery( "Select * from "
                    + dbHelper.TABLE_NAME + ";", null );

            if(cursorIds != null){
                int idIndex = cursorIds.getColumnIndex(dbHelper.COL_ID);
                int idCocktailIndex = cursorIds.getColumnIndex(dbHelper.COL_COCKTAIL_ID);

                while (cursorIds.moveToNext()) {
                    if(cursorIds.getInt(idCocktailIndex) == idCocktail ){
                        idGlobal = Long.toString(cursorIds.getInt(idIndex) );
                        found = true;
                    }
                }
            }
            //changes button visibility, if exists on database shows remove
            if(idGlobal != null){
                saveButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.VISIBLE);
            } else {
                saveButton.setVisibility(View.VISIBLE);
                removeButton.setVisibility(View.GONE);
            }
        } catch(Exception e){
            System.out.println(e);
        }

        tvCocktailName.setText(nameCocktail);
        ivCocktailImage.setImageBitmap(bmp);
        tvCocktailCategory.setText(categoryCocktail);
        tvCocktailInstructions.setText(instructionsCocktail);
        tvCocktailURL.setText("Link to drink website");

        /**
         * To go to the Web Page
         */
        tvCocktailURL.setOnClickListener(click -> {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW);
            urlIntent.setData(Uri.parse(urlCocktail));
            startActivity(urlIntent);
        });


        /**
         * Saves into database when pressing save button
         * and makes remove button visible
         */
        saveButton.setOnClickListener(click ->{
            progressBar.setVisibility(View.VISIBLE);
            setProgressCounter();
            //for inserting rows
            ContentValues newRow = new ContentValues();
            newRow.put( dbHelper.COL_COCKTAIL_ID, idCocktail);
            newRow.put( dbHelper.COL_NAME, nameCocktail);
            newRow.put( dbHelper.COL_IMAGE_DATA, byteArray);
            newRow.put( dbHelper.COL_CATEGORY, categoryCocktail);
            newRow.put( dbHelper.COL_PREP_INSTRUCTIONS, instructionsCocktail);
            newRow.put( dbHelper.COL_URL,urlCocktail );

            //returns row id
            long databaseId = dbWriter.insert(dbHelper.TABLE_NAME, null, newRow);

            Cocktail myCocktail = new Cocktail(databaseId, idCocktail,nameCocktail, bmp, categoryCocktail, instructionsCocktail, urlCocktail  );
            cocktails.add(myCocktail);
            saveButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "saved to favourites", Toast.LENGTH_LONG).show();
        });


        /**
         * Removes cocktail from database when pressing removed button
         * and makes save button visible again.
         */
        removeButton.setOnClickListener(click ->{
            progressBar.setVisibility(View.VISIBLE);
            setProgressCounter();
            dbWriter.delete(dbHelper.TABLE_NAME,
                    dbHelper.COL_ID +" = ?", new String[] { idGlobal});
            saveButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
            Toast.makeText(this, "Removed from favourites", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(this,MainActivity.class);
            startActivity(intent1);
        });
    }


    /**
     * Method for progress bar
     */
    private void setProgressCounter () {
        for (int counter = 0; counter != 100; counter++) {
            progressBar.setProgress(counter);
            progressBar.setMax(100);
            if(counter == 100){
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method converts bitmap into an array of bytes
     * @param bitmapImage image to be converted to bytes
     * @return array of bytes
     */
    private byte[] bitmapToByteArray(Bitmap bitmapImage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();
        return imageByteArray;
    }

    /**
     * Method converts array of bytes into bitmap
     * @param byteArray array of bytes
     * @return bitmap image
     */
    private Bitmap byteArrayToBitmap(byte[] byteArray){
        Bitmap imageData = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return imageData;
    }

}