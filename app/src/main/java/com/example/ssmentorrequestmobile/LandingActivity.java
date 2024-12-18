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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class LandingActivity extends AppCompatActivity {

    String clientSecret = BuildConfig.CLIENT_SECRET;
    String clientID = BuildConfig.CLIENT_ID;
    String redirectUri = "ssmentor://callback";
    String authUri = "https://discord.com/oauth2/authorize";
    String tokenUri = "https://discord.com/api/oauth2/token";
    String userUri = "https://discord.com/api/users/@me";
    String verifier = "2xQQtMyzvi6jmeCFkcX6JMC9z0s8Bt-Aznj8X52_m8I";
    String challenge = "qUmxBmBQucNUMrJ1iDI_aLP39uBUDjkYwu4wepJj3W4";
    private String access_token;

    static RequestQueue requestQueue;
    Uri.Builder builder = new Uri.Builder();


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

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        Button button = findViewById(R.id.button);

        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        verifier = Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        byte[] bytes = verifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        challenge = Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);

        button.setOnClickListener(view -> {

            builder.appendQueryParameter("response_type", "code");
            builder.appendQueryParameter("client_id", clientID);
            builder.appendQueryParameter("redirect_uri", redirectUri);
            builder.appendQueryParameter("scope", "identify");
            builder.appendQueryParameter("code_challenge", challenge);
            builder.appendQueryParameter("code_challenge_method", "S256");

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUri + builder));
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String code = uri.getQueryParameter("code");

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    tokenUri,
                    response -> {
                        processResponse(response);

                        StringRequest request2 = new StringRequest(Request.Method.GET, userUri, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                User resp = new Gson().fromJson(response, User.class);
                                Intent transIntent = new Intent(getApplicationContext(), MainActivity.class);
                                transIntent.putExtra("sub", resp.id);
                                transIntent.putExtra("username", resp.username);
                                startActivity(transIntent);
                            }

                        }, error -> {
                            Log.d("error_auth", error.toString());
                        }) {

                            //This is for Headers If You Needed
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json; charset=UTF-8");
                                params.put("Authorization", "Bearer " + access_token);
                                Log.d("token header", access_token);
                                return params;
                            }

                        };

                        requestQueue.add(request2);
                    },
                    error -> {
                        Log.d("error", error.toString());
                    }

            ) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("grant_type", "authorization_code");
                    params.put("code", code);
                    params.put("redirect_uri", redirectUri);
                    params.put("code_challenge", challenge);
                    params.put("code_verifier", verifier);
                    params.put("client_id", clientID);
                    params.put("client_secret", clientSecret);
                    params.put("scope", "identify");
                    params.put("code_challenge_method", "S256");

                    return params;
                }
            };
            request.setShouldCache(false);
            requestQueue.add(request);

            User user;

        }
    }

    public void processResponse(String response) {
        Tokens resp = new Gson().fromJson(response, Tokens.class);
        access_token = resp.access_token;
    }

    public class Tokens {
        public String access_token;
        public String refresh_token;
    }

    public class User {
        public String username;
        public String id;
    }
}