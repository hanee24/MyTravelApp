package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by parkhanee on 2016. 9. 5..
 */
public class NearbyFragment extends Fragment {


    Button okay;
    int radius=500;
    String strRadius;
    int cat=-1; // 전체 == -1
    String strCat ;
    Spinner spinnerRadius;
    Spinner spinnerCat;

    Double lat;
    Double lng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearby,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            lat = bundle.getDouble("lat",0.0);
            lng = bundle.getDouble("lng",0.0);
        }

        okay = (Button) view.findViewById(R.id.button3);
        spinnerRadius = (Spinner) view.findViewById(R.id.spinner);
        spinnerCat = (Spinner) view.findViewById(R.id.spinner2);

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
                Intent i  = new Intent(getActivity(), NearbyD2Activity.class);
                i.putExtra("radius",radius);
                i.putExtra("cat",cat);
                i.putExtra("strRadius",strRadius);
                i.putExtra("strCat",strCat);
                i.putExtra("lng",lng);
                i.putExtra("lat",lat);
                startActivity(i);
            }
        });

    }
}
