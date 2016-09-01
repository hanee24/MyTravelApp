package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

/**
 * Created by parkhanee on 2016. 8. 31..
 */
public class FbLoginFragment extends Fragment{
    public FbLoginFragment(){   }

    CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    LoginButton loginButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //get arguments and set them as private variables
        super.onCreate(savedInstanceState);

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
                if (newProfile != null) {
                    //TODO the user has logged out
                } else {
                    //TODO the user may have logged in or changed some of his profile settings
                }
            }
        };
        profileTracker.startTracking();
        tokenTracker.startTracking();
    }

    public static AccessToken getCurrentAccessToken(){
        AccessToken token = AccessToken.getCurrentAccessToken();
        return token;
    }

    public static Profile getCurrentProfile(){
        Profile profile = Profile.getCurrentProfile();
        return profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fb_login, container, false);
        return view;
    }

   /* private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            //TODO, implement onSuccess
            String userId = loginResult.getAccessToken().getUserId();
            Toast.makeText(getContext(), userId, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            //TODO, implement onCancel
        }

        @Override
        public void onError(FacebookException e) {
            //TODO, implement onError inorder to handle the errors
        }
    };*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loginButton = (LoginButton) view.findViewById(R.id.login_button); //TODO : put it in onCREATE or OnCreateView?>??

        //TODO: get some more permissions from the user
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("email");
        //You can customize the properties of Login button
        //includes LoginBehavior, DefaultAudience, ToolTipPopup.Style and permissions on the LoginButton

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { //when the user newly logged in
                // App code
                //TODO : send boolean to Main activity , 메인에서 회우너정보 SP에 저장하게
                Intent a = new Intent(getContext(),MainActivity.class);
                a.putExtra("ifNewlyLogged",true);
                a.putExtra("ifbLogged",true);
                startActivity(a);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //얘가 없으면 로그인 하고나서 로그인 버튼이 "로그아웃"으로 바뀌지 않으ㅁ //it pass the result to the CallbackManager
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}