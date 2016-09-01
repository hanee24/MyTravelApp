package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by parkhanee on 2016. 8. 31..
 */
public class FbLoginFragment extends Fragment{
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //get arguments and set them as private variables
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create(); //create a callback manager to handle login responses
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fb_login, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.login_button); //TODO : put it in onCREATE or OnCreateView?>??

        //loginButton.setReadPermissions("email");
        // You can customize the properties of Login button
        //includes LoginBehavior, DefaultAudience, ToolTipPopup.Style and permissions on the LoginButton

        loginButton.setFragment(this);


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                String userId = token.getUserId();
                Toast.makeText(getContext(), "logged in : "+userId, Toast.LENGTH_SHORT).show();
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
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
