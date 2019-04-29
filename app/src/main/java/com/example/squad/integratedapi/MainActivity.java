package com.example.squad.integratedapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    TwitterLoginButton twitterLogin;
    GoogleSignInClient mGoogleSignInClient;

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


        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        boolean googleLoggedIn = account != null;
        if (googleLoggedIn) {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            profilePage();
        }
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1230);
            }
        });


        //facebook login
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken == null;
        if (!isLoggedIn) {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            profilePage();
        }
        String EMAIL = "email";
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Collections.singletonList(EMAIL));
        defaultLoginTwitter();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                if (!loggedIn) {
                    profilePage();
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });


        //      twitter login
        twitterLogin = findViewById(R.id.twitter);
        twitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                profilePage();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //for facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //for twitter
        twitterLogin.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1230) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleLoginHandler(task);
        }

    }

    public void defaultLoginTwitter() {
        //check if user is already authenticated or not
        if (getTwitterSession() != null) {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show();
            profilePage();
        }
    }

    private TwitterSession getTwitterSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }


    private void googleLoginHandler(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            profilePage();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            Log.e("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    public void profilePage() {
        startActivity(new Intent(MainActivity.this, Profile_page.class));
        finish();
    }
}
