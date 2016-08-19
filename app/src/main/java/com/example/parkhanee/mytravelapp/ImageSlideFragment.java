package com.example.parkhanee.mytravelapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by parkhanee on 2016. 8. 19..
 */
public class ImageSlideFragment extends Fragment {
    private ImageView imageView;
    private int mImageNum;
    private static final String IMAGE_DATA_EXTRA ="resId";

    static ImageSlideFragment newInstance(int imageNum){
        final ImageSlideFragment f = new ImageSlideFragment();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA,imageNum);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image_slide, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imageView2);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState); //??
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NearbyD3Activity.class.isInstance(getActivity())){
           // final int resId = NearbyD3Activity.i
        }
    }
}
