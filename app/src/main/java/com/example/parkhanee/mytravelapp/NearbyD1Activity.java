package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbyD1Activity extends AppCompatActivity {

    Button okay;
    int radius=500;
    String strRadius;
    int cat=-1; // 전체 == -1
    String strCat ;
    Spinner spinnerRadius;
    Spinner spinnerCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d1);

        Intent a = getIntent();
        final Double lat = a.getDoubleExtra("lat",0.0);
        final Double lgt = a.getDoubleExtra("lgt",0.0);

        okay = (Button) findViewById(R.id.button3);
        spinnerRadius = (Spinner) findViewById(R.id.spinner);
        spinnerCat = (Spinner) findViewById(R.id.spinner2);

        //스피너 처리
        spinnerRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strRadius = (String) adapterView.getItemAtPosition(i);
                switch (i){
                    case 0 : radius = 500;
                        break;
                    case 1: radius = 750;
                        break;
                    case 2 : radius = 1000;
                        break;
                    case 3 : radius = 2000;
                        break;
                    case 4 : radius = 3000;
                        break;
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCat = (String) adapterView.getItemAtPosition(i);
                switch (i){
                    case 0 : cat=-1;
                        break;
                    case 1 : cat=12;
                        break;
                    case 2 : cat=39;
                        break;
                    case 3 : cat=32;
                        break;
                    case 4 : cat=15;
                        break;
                    case 5 : cat = 25;
                        break;
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 확인 버튼
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(NearbyD1Activity.this, NearbyD2Activity.class);
                i.putExtra("radius",radius);
                i.putExtra("cat",cat);
                i.putExtra("strRadius",strRadius);
                i.putExtra("strCat",strCat);
                i.putExtra("lgt",lgt);
                i.putExtra("lat",lat);
                startActivity(i);
            }
        });
    }
}
