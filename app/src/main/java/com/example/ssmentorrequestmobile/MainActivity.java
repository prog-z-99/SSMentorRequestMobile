package com.example.ssmentorrequestmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ssmentorrequestmobile.databinding.ActivityMainBinding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import io.mokulu.discord.oauth.DiscordOAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    String clientID = "1317734356936163358";
    String clientSecret = "882LBt3i0Fb3LBibNQiEAMSmrs_-oVAp";
    String redirectUri = "ssmentor://callback";
    String[] scope = {"identify"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        DiscordOAuth oauthHandler = new DiscordOAuth(clientID, clientSecret, redirectUri, scope);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = oauthHandler.getAuthorizationURL("randomshit");
                SecureRandom sr = new SecureRandom();
                byte[] code = new byte[32];
                sr.nextBytes(code);
                String verifier = Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
                byte[] bytes = verifier.getBytes(StandardCharsets.US_ASCII);
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                md.update(bytes, 0, bytes.length);
                byte[] digest = md.digest();
                String challenge = Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);

                Log.d("challenge", challenge);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + "&code_challenge=" + challenge +
                        "&code_challenge_method=S256"));

                startActivity(intent);
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            Log.d("uri", uri.toString());
        }
    }


}