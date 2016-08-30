package com.example.parkhanee.mytravelapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {
    EditText et_id;
    EditText et_pwd;
    EditText et_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

    }

    public void mOnClick(View view){
        switch (view.getId()) {
            case R.id.button6:  //okay button pressed

                break;
            case R.id.button7:  //cancel button pressed

                break;
            default:
                break;
        }
    }
}
