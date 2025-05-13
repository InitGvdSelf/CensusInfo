package ru.mirea.censusinfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ru.mirea.censusinfo.databinding.ActivityLoginBinding;

import ru.mirea.censusinfo.data.FirestoreRepository;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private FirestoreRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        repo = new FirestoreRepository(getApplicationContext());

        binding.signInButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or password empty", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            routeByRole(auth.getCurrentUser().getUid());
                        } else {
                            Toast.makeText(this,
                                    "Auth failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        binding.registerButton.setOnClickListener(
                v -> startActivity(new Intent(this, RegistrationActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null)
            routeByRole(auth.getCurrentUser().getUid());
    }

    private void routeByRole(String uid){
        repo.getUser(uid).addOnSuccessListener(d -> {
            String role = d.exists() ? d.getString("role") : null;
            if (openRoleActivity(role)) finish();   // завершаем ТОЛЬКО если всё ок
        }).addOnFailureListener(e ->
            Toast.makeText(this,"Не удалось получить роль",
                           Toast.LENGTH_SHORT).show());
    }

    private boolean openRoleActivity(String role){
        if (role == null) role = "";
        role = role.replace("_","").toLowerCase();
        Intent i;
        switch (role){
            case "citizen":    case "user":
                i = new Intent(this, ru.mirea.censusinfo.ui.CitizenActivity.class); break;
            case "censustaker":
                i = new Intent(this, ru.mirea.censusinfo.ui.CensusTakerActivity.class); break;
            case "statistician":
                i = new Intent(this, ru.mirea.censusinfo.ui.AdminActivity.class); break;
            default:
                Toast.makeText(this,"Неизвестная роль",Toast.LENGTH_SHORT).show();
                return false;
        }
        startActivity(i);
        if (this instanceof LoginActivity) return true;
        finish();
        return true;
    }
}
