package fr.media85.myradioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import fr.media85.myradioapp.fragments.Fragment1;
import fr.media85.myradioapp.fragments.Fragment2;
import fr.media85.myradioapp.fragments.PlayFragment;
import fr.media85.myradioapp.fragments.Fragment3;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayFragment()).commit();

        configureBottomMenu();

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.phone:
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "0610943567", null));
                startActivity(callIntent);
                break;
            case R.id.mail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"youremail@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Message depuis l'application My Radio App");
                i.putExtra(Intent.EXTRA_TEXT   , "Bonjour");
                try {
                    startActivity(Intent.createChooser(i, "Envoyer un email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.infos:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.dialog_infos) //set the view
                        .create() //create the dialog_infos
                        .show(); //show the dialog_infos

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()) {

                        case R.id.nav_play:
                            Log.i("MainActivity", "onNavigationItemSelected: PlayFragment ");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayFragment()).commit();
                            break;
                        case R.id.nav_emissions:
                            Log.i("MainActivity", "onNavigationItemSelected: Fragment1 ");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment1()).commit();
                            break;
                        case R.id.nav_titres:
                            Log.i("MainActivity", "onNavigationItemSelected: Fragment3 ");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment3()).commit();
                            break;
                        case R.id.nav_network:
                            Log.i("MainActivity", "onNavigationItemSelected: Fragment2 ");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment2()).commit();
                            break;
                    }

                    return true;
                }
            };


    private void configureBottomMenu(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}