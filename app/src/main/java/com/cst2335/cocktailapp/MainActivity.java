package com.cst2335.cocktailapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

/**
 * This class contains the toolbar and drawer menu used in the application.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    MainFragment mainFragment;
    FavouritesFragment favouritesFragment;

    /**
     * onCreate method for the main activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //this
        mainFragment = new MainFragment();
        if (mainFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, mainFragment);
            ft.commit();
        }
    }

    /**
     * Method to inflate the Toolbar menu on the main activity
     * @param menu
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.toolbar_menu, menu );
        return true;
    }

    /**
     * Method for toolbar menu and AlertDialog Notification
     * @param item selected item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //putting case for the menu:
        switch (item.getItemId()) {
            case R.id.help:
                //AlertDialog Notification when clicked on Navigation items
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.help))
                        .setMessage(getResources().getString(R.string.overflow))
                        .setPositiveButton(getResources().getString(R.string.closed),(click, arg) -> {}).create().show();
                break;
            case R.id.home:
                //launch main activity (home page)
                FragmentTransaction transactionToMain = getSupportFragmentManager().beginTransaction();
                transactionToMain.replace(R.id.flMain,mainFragment);
                transactionToMain.commit();
                break;
            case R.id.favorite:
                //launch FavoritesFragment
                favouritesFragment = new FavouritesFragment();
                FragmentTransaction transactionToFavourites = getSupportFragmentManager().beginTransaction();
                transactionToFavourites.replace(R.id.flMain,favouritesFragment);
                transactionToFavourites.commit();
                break;
            case R.id.website:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.thecocktaildb.com/"));
                startActivity(i);

                break;
        }
        if (message != null) {
            Toast.makeText( this, message, Toast.LENGTH_LONG ).show(); //message shown as Toast
        }
        return true;
    }

    /**
     * Method to navigate to the Fragment of the respective Navigation option chosen
     * @param item
     * @return False
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;
        switch (item.getItemId()) {
            //launch main fragment of main activity for home Navigation option
            case R.id.home:
                mainFragment = new MainFragment();
                FragmentTransaction transactionToMain = getSupportFragmentManager().beginTransaction();
                transactionToMain.replace(R.id.flMain,mainFragment);
                transactionToMain.commit();
                break;
            case R.id.favorite:
                //launch FavouritesFragment for Favourites Navigation option

                favouritesFragment = new FavouritesFragment();
                FragmentTransaction transactionToFavourites = getSupportFragmentManager().beginTransaction();
                transactionToFavourites.replace(R.id.flMain,favouritesFragment);
                transactionToFavourites.commit();
                break;
            case R.id.website:
                //launches the internet
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.thecocktaildb.com/"));
                startActivity(i);
                break;
        }
        //DrawerLayout object
        DrawerLayout drawerLayout = findViewById( R.id.drawer_layout );
        drawerLayout.closeDrawer( GravityCompat.START );

        return false;
    }
}