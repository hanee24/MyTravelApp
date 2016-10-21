package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * Created by parkhanee on 2016. 10. 21..
 * http://fimtrus.tistory.com/entry/Android-%EC%82%AC%EC%A7%84-%EC%A0%80%EC%9E%A5-%ED%9B%84-%EA%B0%A4%EB%9F%AC%EB%A6%AC%EC%97%90-%EB%B3%B4%EC%9D%B4%EC%A7%80-%EC%95%8A%EB%8A%94-%EA%B2%BD%EC%9A%B0MediaScanner
 *
 */

public class MediaScanner {
    private Context mContext;

    private String mPath;

    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;

    public static MediaScanner newInstance(Context context) {
        return new MediaScanner(context);
    }

    private MediaScanner(Context context) {
        mContext = context;
    }

    public void mediaScanning(final String path) {

        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {

                @Override
                public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(mPath, null); // 디렉토리
                    // 가져옴
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            };
            mMediaScanner = new MediaScannerConnection(mContext, mMediaScannerClient);
        }

        mPath = path;
        mMediaScanner.connect();
        mMediaScanner.disconnect();
    }
}
