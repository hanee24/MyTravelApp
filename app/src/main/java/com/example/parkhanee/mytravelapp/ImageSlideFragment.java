package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 8. 19..
 */
public class ImageSlideFragment extends Fragment {
    private ImageView imageView;
    private int mImageNum;
    private static final String IMAGE_DATA_EXTRA ="resId";
    private ArrayList<String> imgArrayList;

    static ImageSlideFragment newInstance(int imageNum, ArrayList<String> arrayList){
        final ImageSlideFragment f = new ImageSlideFragment();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA,imageNum);
        args.putStringArrayList("contents",arrayList);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments().getInt(IMAGE_DATA_EXTRA);
        imgArrayList = getArguments().getStringArrayList("contents");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image_slide, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView2);
        setImageView(rootView.getContext());
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState); //??
    }

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       //TODO : set image on imageView with picasso

    }*/

    private void setImageView(Context context){
        Picasso.with(context).load(imgArrayList.get(mImageNum)).into(imageView); //is this getting url image with UI Thread?
    }
}
