package ru.mirea.censusinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.LoginActivity;
import ru.mirea.censusinfo.R;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout   drawer;
    private NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_census_taker_drawer);

        /* ───────── toolbar + бургер ───────── */
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        nav    = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* ───────── подменяем меню ───────── */
        nav.getMenu().clear();                         // убираем меню переписчика
        nav.inflateMenu(R.menu.menu_admin_drawer);     // грузим меню админа

        /* стартовый фрагмент */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CitizenWelcomeFragment())
                    .commit();
        }

        nav.setNavigationItemSelectedListener(this::onNavItem);
    }

    private boolean onNavItem(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_welcome) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CitizenWelcomeFragment())
                    .commit();

        } else if (id == R.id.nav_all_forms) {               // все анкеты
            startActivity(new Intent(this, AllFormsActivity.class)
                    .putExtra("isCensusTaker", true));

        } else if (id == R.id.nav_tickets) {                 // обращения
            startActivity(new Intent(this, TicketsActivity.class));

        } else if (id == R.id.nav_support) {                 // «написать в техподдержку»
            startActivity(new Intent(this, SupportActivity.class));

        } else if (id == R.id.nav_logout) {                  // выход
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return true;
        }

        drawer.close();
        return true;
    }
}