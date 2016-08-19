package com.example.parkhanee.mytravelapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by parkhanee on 2016. 8. 19..
 */
public class TextSlideFragment extends Fragment{
    private ImageView textView;
    private int mTextNum;
    private static final String TEXT_DATA_EXTRA ="resId";

    static TextSlideFragment newInstance(int textNum){
        final TextSlideFragment f = new TextSlideFragment();
        final Bundle args = new Bundle();
        args.putInt(TEXT_DATA_EXTRA,textNum);
        f.setArguments(args);
        return f;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_slide, container, false);
        //textView = (TextView) rootView.findViewById(R.id.tv);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState); //??
    }
}
