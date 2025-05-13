package ru.mirea.censusinfo.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.Query;
import java.util.List;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.FirestoreRepository;
import ru.mirea.censusinfo.data.SupportRequest;

public class TicketsActivity extends AppCompatActivity {

    private FirestoreRepository   repo;
    private SupportRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupportRequestAdapter();
        rv.setAdapter(adapter);

        repo = new FirestoreRepository(getApplicationContext());

        repo.supportRequests()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    List<SupportRequest> list = snap.toObjects(SupportRequest.class);
                    adapter.setData(list);
                });
    }
}