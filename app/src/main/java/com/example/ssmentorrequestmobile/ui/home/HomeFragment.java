package com.example.ssmentorrequestmobile.ui.home;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ssmentorrequestmobile.LandingActivity;
import com.example.ssmentorrequestmobile.MainActivity;
import com.example.ssmentorrequestmobile.R;
import com.example.ssmentorrequestmobile.databinding.FragmentHomeBinding;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String requestUri = "https://ss-mentor-request-mobile.vercel.app";
    private int roleId = 0;
    private int regionId = 0;
    EditText riotId;

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

        riotId = root.findViewById(R.id.riot_id);

        Button sumbitButton = root.findViewById(R.id.submit_button);
        sumbitButton.setOnClickListener(view -> {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token.sub", getArguments().getString("sub"));
            params.put("token.username", getArguments().getString("username"));
            params.put("rank", "");
            params.put("summonerName", riotId.getText().toString());
            params.put("region", regions[regionId]);
            params.put("role", roles[roleId]);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    requestUri,
                    new JSONObject(params),
                    response -> {
                        Toast.makeText(getContext(), "Request successfully sent!", Toast.LENGTH_SHORT).show();

                    }, error -> {
                Log.d("error_auth", error.toString());
            }) {

            };


        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}