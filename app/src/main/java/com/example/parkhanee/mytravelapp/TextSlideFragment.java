package com.example.parkhanee.mytravelapp;

//import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 8. 19..
 */
public class TextSlideFragment extends Fragment {
    //private TextView textView;
    private int mPageNum;
    //private static final String TEXT_DATA_EXTRA ="resId";
    private ArrayList<String> textArrayList; // why does it need to be static? static..?ㅠㅠ

    static TextSlideFragment newInstance(int textNum, ArrayList<String> arrayList){
        final TextSlideFragment fragment = new TextSlideFragment();
        final Bundle args = new Bundle();
        args.putInt("page",textNum);
        args.putStringArrayList("contents",arrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNum = getArguments().getInt("page");
        textArrayList = getArguments().getStringArrayList("contents");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_text_slide, container, false);
        ((TextView) rootView.findViewById(R.id.tv)).setText(textArrayList.get(mPageNum));
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState); //??
    }
}
