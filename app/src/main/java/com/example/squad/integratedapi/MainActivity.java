package com.example.squad.integratedapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    TwitterLoginButton loginbbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_main);


        //facebook login
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken == null;
        if (!isLoggedIn) {
            startActivity(new Intent(this, Profile_page.class));
        }
        String EMAIL = "email";
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);
        defaultLoginTwitter();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                if (!loggedIn) {
                    startActivity(new Intent(MainActivity.this, Profile_page.class));
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

//
//        //twitter login
        loginbbutton = findViewById(R.id.twitter);

        loginbbutton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                startActivity(new Intent(MainActivity.this, Profile_page.class));
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });


    }

    public void defaultLoginTwitter() {
        //check if user is already authenticated or not
        if (getTwitterSession() != null) {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Profile_page.class));
        }
    }

    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return session;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        loginbbutton.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }
//
//    private void getUserProfile(AccessToken currentAccessToken) {
//        GraphRequest request = GraphRequest.newMeRequest(
//                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.d("TAG", object.toString());
//                        try {
//                            String first_name = object.getString("first_name");
//                            String last_name = object.getString("last_name");
//                            String email = object.getString("email");
//                            String id = object.getString("id");
//                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
//
//                            Toast.makeText(MainActivity.this, first_name, Toast.LENGTH_SHORT).show();
//
//                            SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor e=sharedpreferences.edit();
//                            e.putString("fname", first_name);
//                            e.putString("name", last_name);
//                            e.putString("email", email);
//                            e.commit();
////                            txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
////                            txtEmail.setText(email);
////                            Picasso.with(MainActivity.this).load(image_url).into(imageView);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "first_name,last_name,email,id");
//        request.setParameters(parameters);
//        request.executeAsync();
//
//    }

}
