package ru.mirea.censusinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.Citizen;
import ru.mirea.censusinfo.data.FirestoreRepository;

public class AllFormsActivity extends AppCompatActivity {

    private CitizenAdapter adapter;
    private FirestoreRepository repo;

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_all_forms);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> finish());

        adapter = new CitizenAdapter(this::openForm);
        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo = new FirestoreRepository(getApplicationContext());
        repo.getAllCitizens().addOnSuccessListener(adapter::setData);
    }

    private void openForm(Citizen c){
        Intent i = new Intent(this, CitizenFormActivity.class);
        i.putExtra("cid", c.getId());
        i.putExtra("isCensusTaker", true);
        editLauncher.launch(i);
    }

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    r -> {
                        if (r.getResultCode() == RESULT_OK) {
                            repo.getAllCitizens()
                                    .addOnSuccessListener(adapter::setData); // перечитываем
                        }
                    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem == null) return true;
        menu.add(0, 1001, 0, "Только одобренные");
        menu.add(0, 1002, 1, "Только не одобренные");
        menu.add(0, 1003, 2, "Все анкеты");
        View actionView = searchItem.getActionView();
        if (!(actionView instanceof SearchView)) return true;

        SearchView searchView = (SearchView) actionView;
        searchView.setQueryHint("Поиск по анкетам...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1001:
                adapter.filterByApproval(true);  // только одобренные
                return true;
            case 1002:
                adapter.filterByApproval(false); // только не одобренные
                return true;
            case 1003:
                adapter.resetFilter();           // все
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
