package ru.mirea.censusinfo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.censusinfo.databinding.ActivityMainBinding;

import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.data.FirestoreRepository;
import ru.mirea.censusinfo.data.User;

import android.content.Intent;
import android.widget.Toast;

import ru.mirea.censusinfo.ui.CitizenActivity;
import ru.mirea.censusinfo.ui.CensusTakerActivity;

public class MainActivity extends AppCompatActivity {

    private static final String ROLE_CITIZEN      = "citizen";
    private static final String ROLE_CENSUS_TAKER = "censusTaker";
    private static final String ROLE_STATISTICIAN = "statistician";

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirestoreRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        repo = new FirestoreRepository(getApplicationContext());
    }

    @Override protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {          // ещё не авторизован
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }
        routeByRole(auth.getCurrentUser().getUid());  // авторизован → роутинг
    }

    private void routeByRole(String uid){
        repo.getUser(uid).addOnSuccessListener(d -> {
            String role = d.getString("role");
            openRoleActivity(role);
        }).addOnFailureListener(e ->
                Toast.makeText(this,"Ошибка загрузки роли",Toast.LENGTH_SHORT).show());
    }

    private void openRoleActivity(String role){
        if (role == null) role = "";
        role = role.replace("_","").toLowerCase();
        Intent i;
        switch (role){
            case "citizen":    case "user":
                i = new Intent(this, ru.mirea.censusinfo.ui.CitizenActivity.class); break;
            case "censustaker":
                i = new Intent(this, ru.mirea.censusinfo.ui.CensusTakerActivity.class); break;
            case "statistician":
                i = new Intent(this, ru.mirea.censusinfo.ui.AdminActivity.class);
            default:
                Toast.makeText(this,"Неизвестная роль",Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(i);
        finish();
    }
}
