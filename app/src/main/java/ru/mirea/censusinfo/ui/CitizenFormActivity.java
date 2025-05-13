package ru.mirea.censusinfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.Citizen;
import ru.mirea.censusinfo.data.FirestoreRepository;
import ru.mirea.censusinfo.data.User;
import ru.mirea.censusinfo.databinding.ActivityCitizenFormBinding;

public class CitizenFormActivity extends AppCompatActivity {

    private ActivityCitizenFormBinding binding;
    private boolean isCensusTaker, isNewForm;
    private String  editId;
    private Citizen citizen;
    private FirestoreRepository repo;

    @Override protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        binding = ActivityCitizenFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isCensusTaker = getIntent().getBooleanExtra("isCensusTaker", false);
        isNewForm     = "new".equals(getIntent().getStringExtra("mode"));
        editId        = getIntent().getStringExtra("cid");
        repo          = new FirestoreRepository(getApplicationContext());

        setupUi();

        if (!isNewForm && editId != null) {
            repo.getCitizen(editId).addOnSuccessListener(d -> {
                citizen = d.toObject(Citizen.class);
                if (citizen != null) fillForm();
                refreshUi();
            });
        } else {
            citizen = new Citizen();
            citizen.setStatus(isCensusTaker ? "approved" : "pending");
            refreshUi();
        }
    }

    /* ────────── UI initialisation ────────── */
    private void setupUi() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> marital =
                ArrayAdapter.createFromResource(this,
                        R.array.marital_statuses,
                        android.R.layout.simple_spinner_item);
        marital.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spMarital.setAdapter(marital);

        binding.spMarital.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> p, View v,int pos,long id){
                        binding.tilSpouse.setVisibility(pos==1 ? View.VISIBLE : View.GONE);
                    }
                    public void onNothingSelected(AdapterView<?> p){}
                });

        binding.swChildren.setOnCheckedChangeListener(
                (b, c)-> binding.containerChildren
                        .setVisibility(c ? View.VISIBLE : View.GONE));

        binding.btnAddChild.setOnClickListener(v -> addChildField(null));
        binding.btnSave    .setOnClickListener(v -> saveForm());
        binding.btnApprove .setOnClickListener(v -> {
            citizen.setStatus("approved");
            saveForm();
        });

        if (!isCensusTaker) binding.tilEmail.setVisibility(View.GONE);
    }

    private void refreshUi() {
        /* поле Email */
        if (isCensusTaker) {
            binding.tilEmail.setVisibility(View.VISIBLE);
            binding.emailInput.setEnabled(isNewForm);
            if (citizen != null) binding.emailInput.setText(citizen.getEmail());
        } else {
            binding.tilEmail.setVisibility(View.GONE);
        }

        /* кнопки и read-only логика */
        boolean needApprove = isCensusTaker && citizen!=null
                && "pending".equals(citizen.getStatus());

        boolean readOnlyUser = !isCensusTaker && citizen!=null
                && "approved".equals(citizen.getStatus());

        binding.btnApprove.setVisibility(needApprove ? View.VISIBLE : View.GONE);
        binding.btnSave   .setVisibility(
                (needApprove || readOnlyUser) ? View.GONE : View.VISIBLE);

        /* включаем / выключаем поля формы */
        setFormEnabled(!readOnlyUser);
    }

    /* ────────── заполнение данными ────────── */
    private void fillForm() {
        binding.etFullName.setText(citizen.getFullName());
        binding.etAddress .setText(citizen.getAddress());

        int years = java.time.Period.between(
                LocalDate.parse(citizen.getBirthDate()),
                LocalDate.now()).getYears();
        binding.etAge.setText(String.valueOf(years));

        if ("male".equals(citizen.getGender()))  binding.rbMale.setChecked(true);
        else                                     binding.rbFemale.setChecked(true);

        @SuppressWarnings("unchecked")
        int pos = ((ArrayAdapter<String>)binding.spMarital.getAdapter())
                .getPosition(citizen.getMaritalStatus());
        binding.spMarital.setSelection(pos);
        binding.tilSpouse.getEditText().setText(citizen.getSpouse());

        if (citizen.getChildren() != null && !citizen.getChildren().isEmpty()) {
            binding.swChildren.setChecked(true);
            for (String kid : citizen.getChildren()) addChildField(kid);
        } else {
            binding.swChildren.setChecked(false);          // контейнер спрячется сам
        }
    }

    /* ────────── динамичные поля «Ребёнок» ────────── */
    private void addChildField(String name){
        TextInputLayout til = new TextInputLayout(this);
        til.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        til.setEndIconOnClickListener(v -> binding.containerChildren.removeView(til));

        com.google.android.material.textfield.TextInputEditText et =
                new com.google.android.material.textfield.TextInputEditText(this);
        et.setHint("ФИО ребёнка");
        if (name != null) et.setText(name);
        til.addView(et);

        binding.containerChildren.addView(til);
    }

    /* ────────── сохранение ────────── */
    private void saveForm() {

        binding.pbSave.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        String email = isCensusTaker
                ? binding.emailInput.getText().toString().trim()
                : FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (isCensusTaker && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Введите корректный e-mail");
            showReady();
            return;
        }

        String fio  = binding.etFullName.getText().toString().trim(),
                adr  = binding.etAddress   .getText().toString().trim(),
                ageS = binding.etAge       .getText().toString().trim();
        if (fv(fio,"ФИО")|fv(adr,"адрес")|fv(ageS,"возраст")) return;

        int age = Integer.parseInt(ageS);
        String gender = binding.rgGender.getCheckedRadioButtonId()==R.id.rbMale
                ? "male" : "female";

        repo.getOrCreateHouseholdId(adr).addOnSuccessListener(hid -> {

            Citizen c = citizen == null ? new Citizen() : citizen;
            c.setId(editId!=null ? editId : UUID.randomUUID().toString());
            c.setHouseholdId(hid);
            c.setFullName(fio);
            c.setBirthDate(LocalDate.now().minusYears(age).toString());
            c.setGender(gender);
            c.setAddress(adr);
            c.setMaritalStatus(binding.spMarital.getSelectedItem().toString());
            c.setSpouse(binding.tilSpouse.getEditText().getText().toString().trim());
            c.setEmail(email);
            c.setStatus(citizen!=null ? citizen.getStatus()
                    : (isCensusTaker ? "approved" : "pending"));
            c.setUpdatedAt(com.google.firebase.Timestamp.now());

            /* дети */
            List<String> kids = new ArrayList<>();
            if (binding.swChildren.isChecked()) {
                for (int i = 0; i < binding.containerChildren.getChildCount(); i++) {
                    View v = binding.containerChildren.getChildAt(i);
                    if (v instanceof TextInputLayout) {
                        String kid = ((com.google.android.material.textfield.TextInputEditText)
                                ((TextInputLayout) v).getEditText())
                                .getText().toString().trim();
                        if (!kid.isEmpty()) kids.add(kid);
                    }
                }
            }
            c.setChildren(kids.isEmpty() ? null : kids);

            /* ────────── Валидация e-mail и установка ownerUid ────────── */
            Task<User> ownerTask = isCensusTaker
                    ? repo.findUserByEmail(email)
                    : Tasks.forResult(null);

            ownerTask.addOnSuccessListener(u -> {

                if (isCensusTaker && u == null) {       // юзер не найден
                    showError("Пользователь с таким e-mail не найден");
                    return;
                }

                if (c.getOwnerUid() == null) {
                    c.setOwnerUid(isCensusTaker ? u.getUid()
                            : FirebaseAuth.getInstance().getUid());
                }

                /* ────────── Сохраняем ────────── */
                repo.addOrQueueCitizen(c)
                        .addOnSuccessListener(v -> finishOk())
                        .addOnFailureListener(e -> showError(e.getMessage()));

            }).addOnFailureListener(e -> showError(e.getMessage()));

        }).addOnFailureListener(e -> showError(e.getMessage()));
    }

    /* ────────── helpers ────────── */

    /** Включает / выключает все интерактивные элементы формы */
    private void setFormEnabled(boolean enabled){
        binding.etFullName.setEnabled(enabled);
        binding.etAddress .setEnabled(enabled);
        binding.etAge     .setEnabled(enabled);
        binding.rbMale    .setEnabled(enabled);
        binding.rbFemale  .setEnabled(enabled);
        binding.spMarital .setEnabled(enabled);
        binding.tilSpouse.getEditText().setEnabled(enabled);
        binding.swChildren.setEnabled(enabled);
        binding.btnAddChild.setEnabled(enabled);

        for(int i=0;i<binding.containerChildren.getChildCount();i++){
            View v = binding.containerChildren.getChildAt(i);
            v.setEnabled(enabled);
        }
        if (isCensusTaker) binding.emailInput.setEnabled(enabled && isNewForm);
    }

    private void finishOk(){
        toast(editId==null ? "Анкета сохранена" : "Анкета обновлена");
        setResult(RESULT_OK,new Intent().putExtra("saved",true));
        finish();
    }
    private void showError(String m){ toast("Ошибка: "+m); showReady(); }

    private boolean fv(String v,String n){
        if (TextUtils.isEmpty(v)){ toast("Введите "+n); showReady(); return true; }
        return false;
    }
    private void showReady(){ binding.pbSave.setVisibility(View.GONE);
        binding.btnSave.setEnabled(true); }
    private void toast(String m){ Toast.makeText(this,m,Toast.LENGTH_SHORT).show(); }
}