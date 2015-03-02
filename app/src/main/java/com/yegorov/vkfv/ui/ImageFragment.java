package com.yegorov.vkfv.ui;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yegorov.vkfv.R;
import com.yegorov.vkfv.util.LazyGetImage;

public class ImageFragment extends Fragment {
    public final static String TAG = "ImageFragment";

    private ImageView imageView;
    private Bitmap imageBitmap;
    private Bitmap loadImg;

    public ImageFragment() {
        loadImg = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.loader_gray);
    }


    public void setImage(final String url) {
        imageView.setImageBitmap(loadImg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap b = LazyGetImage.getInstance().getImage(url, (Context)getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageBitmap = b;
                        imageView.setImageBitmap(imageBitmap);
                    }
                });
            }
        }).start();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_big_imge, container, false);
        setupUI(rootView);
        Log.d(TAG, "onCreateView");
        return rootView;
    }

    private void setupUI(View v) {
        imageView = (ImageView) v.findViewById(R.id.image);
    }

    private void initUI() {
        imageView.setImageBitmap(imageBitmap);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            initUI();
        }
    }
}
