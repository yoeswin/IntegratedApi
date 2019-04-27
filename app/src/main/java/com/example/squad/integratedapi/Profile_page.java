package com.example.squad.integratedapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;

public class Profile_page extends AppCompatActivity {
    String first_name, last_name;
    TextView Name, DOB, emaill, gender, place;
    ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        im = findViewById(R.id.user_image);
        Name = findViewById(R.id.name);
        DOB = findViewById(R.id.DOB);
        emaill = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        place = findViewById(R.id.place);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken == null;
        if (!isLoggedIn) {
//            Toast.makeText(Profile_page.this, Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString(), Toast.LENGTH_SHORT).show();
            getUserProfile(AccessToken.getCurrentAccessToken());
        }

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (session != null) {
            twitter();
        }


    }

    void twitter() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
        call.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                Name.setText(user.name);
                emaill.setText(user.email);
                place.setText(user.location);
                Toast.makeText(Profile_page.this, user.name, Toast.LENGTH_SHORT).show();

                String imageProfileUrl = user.profileImageUrl;
                imageProfileUrl = imageProfileUrl.replace("_normal", "");

                ///load image using Picasso
                Picasso.with(Profile_page.this)
                        .load(imageProfileUrl)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(im);

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(Profile_page.this, "Failed to authenticate. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            emaill.setText(R.string.emailtest);
                            if (object.has("first_name")) {
                                first_name = object.getString("first_name");
                                Name.setText(first_name);
                                if (object.has("last_name")) {
                                    last_name = object.getString("last_name");
                                    Name.setText(first_name + " " + last_name);
                                }
                            }
                            if (object.has("email")) {
                                emaill.setText(object.getString("email"));
                            }
                            if (object.has("birthday")) {
                                DOB.setText(object.getString("birthday"));
                            }
                            if (object.has("gender")) {
                                gender.setText(object.getString("gender"));
                            }
                            if (object.has("hometown"))
                                place.setText(object.getString("hometown"));
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?height=500&width=500";

                            ///load image using Picasso
                            Picasso.with(Profile_page.this)
                                    .load(image_url)
                                    .placeholder(R.mipmap.ic_launcher_round)
                                    .into(im);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,birthday ,gender , hometown");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void logout(View view) {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        }
        finish();
        Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
    }
}
