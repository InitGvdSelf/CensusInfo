package ru.mirea.censusinfo.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.FirestoreRepository;
import ru.mirea.censusinfo.data.SupportRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.appbar.MaterialToolbar;

public class SupportActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_support);

        // toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        FirestoreRepository repo = new FirestoreRepository(getApplicationContext());
        String uid = FirebaseAuth.getInstance().getUid();

        findViewById(R.id.btnSend).setOnClickListener(v -> {
            String msg = ((TextInputEditText) findViewById(R.id.etMessage))
                         .getText().toString().trim();
            if (msg.isEmpty()){
                Toast.makeText(this,"Введите сообщение",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,"Отправляем...",Toast.LENGTH_SHORT).show();
            repo.addSupportRequest(new SupportRequest(uid, msg))
                .addOnSuccessListener(t -> finish())
                .addOnFailureListener(e ->
                    Toast.makeText(this,"Ошибка: "+e.getMessage(),Toast.LENGTH_SHORT).show());
        });
    }
}
