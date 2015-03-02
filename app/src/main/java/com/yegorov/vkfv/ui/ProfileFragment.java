package com.yegorov.vkfv.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yegorov.vkfv.R;
import com.yegorov.vkfv.util.LazyGetImage;
import com.yegorov.vkfv.util.Utils;
import com.yegorov.vkfv.vk.User;

import org.w3c.dom.Text;


public class ProfileFragment extends Fragment {
    public final static String TAG = "ProfileFragment";

    private ImageView profilePhoto;
    private TextView online;
    private TextView name;
    private TextView status;
    private TextView adressVk;
    private TextView bdate;
    private TextView city;
    private TextView country;
    private TextView univ;
    private ListView listViewSocNet;
    private Button   btnFriends;

    private boolean isDisableButton = false;

    private User user;

    private View.OnClickListener clickListener;

    public void setUser(User user) {
        this.user = user;
        initUI();
    }

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        setupUI(rootView);
        Log.d(TAG, "onCreateView");
        return rootView;
    }


    private void setupUI(View rootView) {
        profilePhoto   = (ImageView)rootView.findViewById(R.id.profilePhoto);
        online         = (TextView)rootView.findViewById(R.id.online);
        name           = (TextView)rootView.findViewById(R.id.name);
        status         = (TextView)rootView.findViewById(R.id.status);
        adressVk       = (TextView)rootView.findViewById(R.id.adressVk);
        bdate          = (TextView)rootView.findViewById(R.id.bdate);
        city           = (TextView)rootView.findViewById(R.id.city);
        univ           = (TextView)rootView.findViewById(R.id.univ);
        country        = (TextView)rootView.findViewById(R.id.country);
        listViewSocNet = (ListView)rootView.findViewById(R.id.listViewSocNet);
        btnFriends     = (Button)rootView.findViewById(R.id.btnFriends);

        btnFriends.setOnClickListener(clickListener);
        profilePhoto.setOnClickListener(clickListener);

        if(isDisableButton)
            btnFriends.setVisibility(View.GONE);
    }
    private void initUI() {
        setUserPhoto();
        online.setText(user.getOnline() > 0 ? getText(R.string.online) : getText(R.string.last_seen) + " " + Utils.unixTimeToStr(user.getLastSeen()));
        name.setText(user.getName());
        status.setText(user.getStatus());
        adressVk.setText(user.getUserUrl());
        bdate.setText(user.getBirthday());
        city.setText(user.getCity());
        country.setText(user.getCountry());
        univ.setText(user.getUniversity());
        btnFriends.setText(getText(R.string.friends) + " (" + user.getCountFriends() + ")");
        ArrayAdapter<String> socNetAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, user.getOtherConnections());
        listViewSocNet.setAdapter(socNetAdapter);

        checkUserData();
    }

    public void setDisableButton(boolean isDisableButton) {
        this.isDisableButton = isDisableButton;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        if(savedInstanceState != null) {
            initUI();
        }
        //Log.d(TAG, "onActivityCreated    " + savedInstanceState);

    }


    private void checkUserData() {
        View v  = getView();
        LinearLayout llCity    = (LinearLayout) v.findViewById(R.id.llCity);
        LinearLayout llCountry = (LinearLayout) v.findViewById(R.id.llCountry);
        LinearLayout llBdate   = (LinearLayout) v.findViewById(R.id.llBdate);
        LinearLayout llUniv    = (LinearLayout) v.findViewById(R.id.llUniv);

        LinearLayout llSocNet  = (LinearLayout) v.findViewById(R.id.llSocNet);

        if(user.getCity().equals(User.UNKNOWN)) {
            llCity.setVisibility(View.GONE);
        }
        else {
            llCity.setVisibility(View.VISIBLE);
        }

        if(user.getCountry().equals(User.UNKNOWN)) {
            llCountry.setVisibility(View.GONE);
        }
        else {
            llCountry.setVisibility(View.VISIBLE);
        }

        if(user.getBirthday().equals(User.UNKNOWN)) {
            llBdate.setVisibility(View.GONE);
        }
        else {
            llBdate.setVisibility(View.VISIBLE);
        }

        if(user.getUniversity().equals(User.UNKNOWN)) {
            llUniv.setVisibility(View.GONE);
        }
        else {
            llUniv.setVisibility(View.VISIBLE);
        }

        if(user.getOtherConnections().length == 0) {
            llSocNet.setVisibility(View.GONE);
        }
        else {
            llSocNet.setVisibility(View.VISIBLE);
        }
    }

    private void setUserPhoto() {
        profilePhoto.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.loader_gray));
        new Thread(new Runnable() {
            @Override
            public void run() {
                LazyGetImage lgi = LazyGetImage.getInstance();
                final Bitmap img = lgi.getImage(user.getUrlPhoto200(), (Context) getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profilePhoto.setImageBitmap(img);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        clickListener = (View.OnClickListener) activity;
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
        Log.d(TAG, "onDetach");
    }

    public String getPhotoUrl() {
        return user.getUrlPhotoMaxOrig();
    }
}
