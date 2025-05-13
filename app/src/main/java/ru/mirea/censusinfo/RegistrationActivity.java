package ru.mirea.censusinfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import ru.mirea.censusinfo.data.FirestoreRepository;
import ru.mirea.censusinfo.data.User;
import ru.mirea.censusinfo.databinding.ActivityRegisterBinding;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirestoreRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                this, R.array.roles, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRole.setAdapter(roleAdapter);

        auth  = FirebaseAuth.getInstance();
        repo  = new FirestoreRepository(getApplicationContext());

        binding.signUpButton.setOnClickListener(v -> signUp());
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void signUp() {
        String email = binding.emailEditText.getText().toString().trim();
        String pass  = binding.passwordEditText.getText().toString().trim();
        String conf  = binding.confirmEditText.getText().toString().trim();
        String role = binding.spRole.getSelectedItem()==null
                      ? "" : binding.spRole.getSelectedItem().toString();
        if(role.isEmpty()){
            Toast.makeText(this,"Выберите роль",Toast.LENGTH_SHORT).show(); return;
        }

        if (email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            Toast.makeText(this,"Заполните все поля",Toast.LENGTH_SHORT).show(); return;
        }
        if (!pass.equals(conf)) {
            Toast.makeText(this,"Пароли не совпадают",Toast.LENGTH_SHORT).show(); return;
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                repo.addUser(new User(uid, email, role))
                    .addOnSuccessListener(v -> {
                        if (openRoleActivity(role)) finish();
                    })
                    .addOnFailureListener(e ->
                        Toast.makeText(this,"Не удалось сохранить роль: "
                                + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this,
                        "Ошибка регистрации: " + t.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
private boolean openRoleActivity(String role){
    if (role == null || role.isEmpty()) {
        Toast.makeText(this,"Роль не задана",Toast.LENGTH_SHORT).show();
        return false;
    }
    Intent i;
    switch (role){
        case "citizen":
            i = new Intent(this, ru.mirea.censusinfo.ui.CitizenActivity.class); break;
        case "censusTaker":
            i = new Intent(this, ru.mirea.censusinfo.ui.CensusTakerActivity.class); break;
        case "statistician":
            i = new Intent(this, ru.mirea.censusinfo.ui.AdminActivity.class); break;
        default:
            // если роль не распознана – просто к главному экрану
            i = new Intent(this, MainActivity.class);
    }
    startActivity(i);
    return true;
}
}
