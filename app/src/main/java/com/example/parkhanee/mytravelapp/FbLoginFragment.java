package com.example.parkhanee.mytravelapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.HashMap;



/**
 * Created by parkhanee on 2016. 8. 31..
 */
public class FbLoginFragment extends Fragment{
    public FbLoginFragment(){   }

    CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    LoginButton loginButton;

    HashMap<String, String> postDataParams;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //get arguments and set them as private variables
        super.onCreate(savedInstanceState);

        postDataParams = new HashMap<>();

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create(); //create a callback manager to handle login responses
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
            }
        };
        profileTracker.startTracking();
        tokenTracker.startTracking();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fb_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loginButton = (LoginButton) view.findViewById(R.id.login_button);

        //TODO: could get some more permissions from the user
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("email");
        //You can customize the properties of Login button
        //includes LoginBehavior, DefaultAudience, ToolTipPopup.Style and permissions on the LoginButton

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { //when the user newly logged in

                // set a dialog which informs that the user has just logged in.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("로그인 되었습니다")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent a = new Intent(getActivity(),MainActivity.class);
                                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //TODO : not sure if it's correct to use this flag
                                startActivity(a);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //얘가 없으면 로그인 하고나서 로그인 버튼이 "로그아웃"으로 바뀌지 않으ㅁ //it passes the result to the CallbackManager
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }






}
