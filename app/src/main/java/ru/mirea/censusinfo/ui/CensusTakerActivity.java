package ru.mirea.censusinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.LoginActivity;

public class CensusTakerActivity extends AppCompatActivity {

    private androidx.drawerlayout.widget.DrawerLayout   drawer;
    private com.google.android.material.navigation.NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_census_taker_drawer);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        nav    = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,new CitizenWelcomeFragment())
                .commit();

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_welcome) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CitizenWelcomeFragment())
                    .commit();

            } else if (id == R.id.nav_all_forms) {
                startActivity(new Intent(this, AllFormsActivity.class));

            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));

            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            else if (id == R.id.action_new_form) {
                Intent intent = new Intent(this, CitizenFormActivity.class);
                intent.putExtra("mode", "new");
                intent.putExtra("isCensusTaker", true);
                startActivity(intent);
            }

            drawer.close();
            return true;
        });

    }
}
