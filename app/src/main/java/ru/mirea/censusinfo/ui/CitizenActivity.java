package ru.mirea.censusinfo.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.LoginActivity;
import android.widget.Toast;
import android.net.Uri;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.ui.SupportActivity;

public class CitizenActivity extends AppCompatActivity {

    private androidx.drawerlayout.widget.DrawerLayout   drawer;
    private com.google.android.material.navigation.NavigationView nav;

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_citizen_drawer);

        // Toolbar + гамбургер
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        nav    = findViewById(R.id.nav_view);

        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // стартовый экран
        if (s == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CitizenWelcomeFragment())
                    .commit();

        // обработка пунктов меню
        nav.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawers();
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                openFragment(new CitizenWelcomeFragment());
                return true;

            } else if (id == R.id.nav_my_form) {
                openFragment(new MyFormsFragment());
                return true;

            } else if (id == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));
                return true;

            } else if (id == R.id.nav_exit) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, ru.mirea.censusinfo.LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                return true;
            }
            return false;
        });
    }

    private void openFragment(androidx.fragment.app.Fragment f){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
    }
}
