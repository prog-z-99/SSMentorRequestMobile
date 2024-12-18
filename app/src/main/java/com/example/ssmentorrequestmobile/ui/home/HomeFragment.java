package com.example.ssmentorrequestmobile.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ssmentorrequestmobile.R;
import com.example.ssmentorrequestmobile.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.regionText;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //get the spinner from the xml.

        Spinner regionsDropdown = root.findViewById(R.id.region);
        Spinner roleDropdown = root.findViewById(R.id.role);
        String[] regions = new String[]{"EUW", "NA", "KR"};
        String[] roles = new String[]{"Top", "Jungle", "Mid", "Bot", "Support"};
        ArrayAdapter<String> adapterRegions = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, regions);
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, roles);

        regionsDropdown.setAdapter(adapterRegions);
        roleDropdown.setAdapter(adapterRoles);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}