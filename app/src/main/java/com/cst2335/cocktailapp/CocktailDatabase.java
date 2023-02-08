package com.cst2335.cocktailapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class manages the creation and version of the database. It opens the database if exists and creates it if not.
 * it implements onCreate() and onUpgrade() and has two additional methods that allows insert and delete from database.
 *
 * @author Maria Alarcon
 */
public class CocktailDatabase extends SQLiteOpenHelper {
    /**  Name of the database  */
    public static final String DATABASE_NAME = "CocktailDatabase";
    /** Database version */
    public static final int VERSION = 1;
    /** Table name */
    public static final String TABLE_NAME = "Cocktails";
    /** Column to store id */
    public static final String COL_ID = "_id";
    /** Column to store cocktail id present on the website   */
    public static final String COL_COCKTAIL_ID = "cocktail_id";
    /** Column to store cocktail name */
    public static final String COL_NAME = "Name";
    /** Column to store cocktail image bytes (BLOB datatype) */
    public static final String COL_IMAGE_DATA = "Image";
    /** Column to store image url */
    public static final String COL_IMAGE_URL = "Image_url";
    /** Column to store cocktail cocktail category */
    public static final String COL_CATEGORY = "Category";
    /** Column to store cocktail preparation isntructions */
    public static final String COL_PREP_INSTRUCTIONS = "Preparation_Instructions";
    /** Column to store URL of cocktail */
    public static final String COL_URL = "Url";


    /** Stores create query  */
    public static final String CREATE_TABLE = "Create table " + TABLE_NAME
            + " ( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_COCKTAIL_ID + " TEXT, "
            + COL_NAME + " TEXT, "
            + COL_IMAGE_DATA + " BLOB, "
            + COL_IMAGE_URL + " TEXT, "
            + COL_CATEGORY + " TEXT, "
            + COL_PREP_INSTRUCTIONS + " TEXT, "
            + COL_URL + " TEXT );";

    /** Constructor for database */
    public CocktailDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * The first one to be called when database is created.
     * Execute the query stored in the CREATE_TABLE variable
     * @param db SQLiteDatabase data type, allows the use of methods for executing SQL commands.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param db         SQLiteDatabase data type.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }


    /**
     * Allows insert a row into the database. Used to save favourites cocktails.
     * @param cocktailId        The id of the cocktail on the API.
     * @param name              The cocktail name.
     * @param imageData         The bytes of the image.
     * @param category          The cocktail category.
     * @param prepInstructions  The cocktail preparation isntructions.
     * @param url               The url of the cocktail.
     * @return                  True.
     */
    public Boolean insertRow(long cocktailId, String name, byte[] imageData,
                           String category, String prepInstructions, String url) {

        SQLiteDatabase dbWriter = this.getWritableDatabase();
        ContentValues newRow = new ContentValues();

        newRow.put( COL_COCKTAIL_ID, cocktailId);
        newRow.put( COL_NAME, name);
        newRow.put( COL_IMAGE_DATA, imageData);
        newRow.put( COL_CATEGORY, category);
        newRow.put( COL_PREP_INSTRUCTIONS, prepInstructions);
        newRow.put( COL_URL,url );
        //returns row id
        long databaseId = dbWriter.insert(TABLE_NAME, null, newRow);

        if (databaseId == -1)
            return false;
        else
            return true;
    }

    /**
     * Deletes a Cocktail item from database by using id
     * @param id    The id of the item in the database.
     */
    public void deleteItem(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COL_ID+"='"+id+"'");
    }
}
