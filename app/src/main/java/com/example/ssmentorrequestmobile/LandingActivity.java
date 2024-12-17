package com.example.ssmentorrequestmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import io.mokulu.discord.oauth.DiscordOAuth;

public class LandingActivity extends AppCompatActivity {


    String clientSecret = BuildConfig.CLIENT_SECRET;
    String clientID = BuildConfig.CLIENT_ID;
    String redirectUri = "ssmentor://callback";
    String[] scope = {"identify"};

    DiscordOAuth oauthHandler = new DiscordOAuth(clientID, clientSecret, redirectUri, scope);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button = findViewById(R.id.button);

        button.setOnClickListener(view -> {
            String url = oauthHandler.getAuthorizationURL("randomshit");
            SecureRandom sr = new SecureRandom();
            byte[] code = new byte[32];
            sr.nextBytes(code);
            String verifier = Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            byte[] bytes = verifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest md;
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