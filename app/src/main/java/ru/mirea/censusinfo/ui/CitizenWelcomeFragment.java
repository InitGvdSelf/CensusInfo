package ru.mirea.censusinfo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mirea.censusinfo.R;                      // <-- add this

public class CitizenWelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup   container,
                             @Nullable Bundle      savedInstanceState) {

        // inflate the fragment’s layout
        return inflater.inflate(R.layout.fragment_citizen_greeting,
                container,
                false);
    }
}