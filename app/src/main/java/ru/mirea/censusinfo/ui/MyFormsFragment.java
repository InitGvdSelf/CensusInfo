package ru.mirea.censusinfo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.Collections;

import ru.mirea.censusinfo.R;
import ru.mirea.censusinfo.data.Citizen;
import ru.mirea.censusinfo.data.Household;
import ru.mirea.censusinfo.data.FirestoreRepository;

/** Экран «Мои анкеты» */
public class MyFormsFragment extends Fragment {

    private RecyclerView        recycler;
    private TextView            tvEmpty;
    private MyFormsAdapter      adapter;
    private boolean openedAutomatically = false;
    private FirestoreRepository repo;
    private String              uid;
    private boolean justCreated = false;

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    r -> {
                        if (r.getResultCode() == Activity.RESULT_OK)
                            loadData();               // перечитать после любого сохранения
                    });
    @Override
    public void onResume() {
        super.onResume();
        loadData();          // перечитать данные всегда
    }

    public static class Item {
        final Household hh;
        final Citizen   ct;
        Item(Household h, Citizen c) { hh = h; ct = c; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inf.inflate(R.layout.fragment_my_forms, container, false);


        recycler = v.findViewById(R.id.recyclerMyForms);
        tvEmpty  = v.findViewById(R.id.tvEmpty);

        adapter = new MyFormsAdapter(this::openForm);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        uid  = FirebaseAuth.getInstance().getUid();
        repo = new FirestoreRepository(requireContext());

        loadData();
        return v;
    }

    private void loadData() {
        Task<List<Household>> hhTask =
                repo.getHouseholdsByOwner(uid);
        Task<List<Citizen>>   ctTask =
                repo.getCitizensByOwner(uid);

        Tasks.whenAllComplete(hhTask, ctTask)
             .addOnCompleteListener(t -> {

                 List<Household> remoteHhs =
                         hhTask.isSuccessful() ? hhTask.getResult()
                                               : java.util.Collections.emptyList();
                 List<Citizen>  remoteCts =
                         ctTask.isSuccessful() ? ctTask.getResult()
                                               : java.util.Collections.emptyList();

                 List<Household> allHhs = new ArrayList<>(remoteHhs);
                 allHhs.addAll(repo.pendingHouseholds());

                 List<Citizen>  allCts = new ArrayList<>(remoteCts);
                 allCts.addAll(repo.pendingCitizens());

                 Map<String,Household> hhMap = new java.util.HashMap<>();
                 for (Household h : allHhs) hhMap.put(h.getId(), h);


                 List<Item> items = new ArrayList<>();
                 for (Citizen c : allCts)
                     items.add(new Item(hhMap.get(c.getHouseholdId()), c));

                 adapter.setData(items);
                 tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);

                 if (items.isEmpty()) {
                     if (!openedAutomatically) {
                         openedAutomatically = true;
                         formLauncher.launch(new Intent(requireContext(), CitizenFormActivity.class));
                     }
                 } else {
                     openedAutomatically = false;
                 }
             });
    }


    private void openForm(Item it){
        if(it.ct==null) return;
        Intent i = new Intent(requireContext(), CitizenFormActivity.class);
        i.putExtra("cid", it.ct.getId());
        formLauncher.launch(i);
    }
}
