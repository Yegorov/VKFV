package com.yegorov.vkfv.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.yegorov.vkfv.R;
import com.yegorov.vkfv.util.LazyGetImage;
import com.yegorov.vkfv.util.ProjectVars;
import com.yegorov.vkfv.vk.User;
import com.yegorov.vkfv.vk.VKFriendUser;
import com.yegorov.vkfv.vk.VKFriends;
import com.yegorov.vkfv.vk.VKFriendsAdd;
import com.yegorov.vkfv.vk.VKInit;
import com.yegorov.vkfv.vk.VKUser;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    public final static String TAG = "MainActivity";

    private static final String TAG_PROFILE_FRAGMENT = "Profile Fragment";
    private static final String TAG_MAIN_FRAGMENT = "Main Fragment";
    private static final String TAG_LISTFRIEND_FRAGMENT = "FriendsList Fragment";
    private static final String TAG_IMAGE_FRAGMENT = "Image Fragment";
    private static final String TAG_FRIENDS_PROFILE_FRAGMENT = "Friends Profile Fragment";

    private ProfileFragment profileFragment;
    private MainFragment mainFragment;
    private FriendsListFragment friendsListFragment;
    private ImageFragment imageFragment;
    private ProfileFragment friendsProfileFragment;

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            VKSdk.initialize(new VKInit(this), ProjectVars.VK_APP_ID,
                    VKAccessToken.tokenFromSharedPreferences(this, VKAccessToken.ACCESS_TOKEN));
            Log.d(TAG, "VKSdk.initialize");
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mainFragment, TAG_MAIN_FRAGMENT)
                    .commit();
            Log.d(TAG, "Main Fragment to Activity");
        }
        else {
            //user = savedInstanceState.
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        VKUIHelper.onResume(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.action_update) {
            update();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void onClickSignVk() {
        VKSdk.authorize(VKScope.FRIENDS,VKScope.STATUS);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSignVk) {
            onClickSignVk();
        }
        else if(v.getId() == R.id.btnFriends) {
            onClickFriends();
        }
        else if(v.getId() == R.id.profilePhoto) {
            if(((ProfileFragment)getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT)).isVisible())
                onClickProfilePhoto();
            else
                onClickFriendProfilePhoto();
        }
    }

    private void onClickFriends() {
        getUserFriendsFromVk(20, 0);
    }

    private void changeFragmentToList() {
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT);
        friendsListFragment = (FriendsListFragment) getSupportFragmentManager().findFragmentByTag(TAG_LISTFRIEND_FRAGMENT);
        if(friendsListFragment == null) {
            friendsListFragment = new FriendsListFragment();
            getSupportFragmentManager().beginTransaction()
                    .remove(profileFragment)
                    .add(R.id.container, friendsListFragment, TAG_LISTFRIEND_FRAGMENT)
                    .addToBackStack(TAG_PROFILE_FRAGMENT + TAG_LISTFRIEND_FRAGMENT)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }

    }

    private void getUserFriendsFromVk(int count, int offset) {
        VKRequest request = VKApi.friends().get(VKParameters
                .from(User.ConstFields.ORDER, User.ConstFields.HINTS,
                        VKApiConst.COUNT, count,
                        VKApiConst.OFFSET, offset,
                        VKApiConst.FIELDS, User.ConstFields.getSomeFields()));
        request.setPreferredLang("ru");
        request.executeWithListener(new VKFriends(this));
    }

    public void setFriendsList(User[] users) {
        changeFragmentToList();
        friendsListFragment.setUsers(users);
    }

    public void getUserNextFriendsFromVk(int count, int offset) {
        VKRequest request = VKApi.friends().get(VKParameters
                .from(User.ConstFields.ORDER, User.ConstFields.HINTS,
                        VKApiConst.COUNT, count,
                        VKApiConst.OFFSET, offset,
                        VKApiConst.FIELDS, User.ConstFields.getSomeFields()));
        request.setPreferredLang("ru");
        request.executeWithListener(new VKFriendsAdd(this));
    }

    public void addFriendsList(User[] users) {
        if(friendsListFragment == null)
            friendsListFragment = (FriendsListFragment) getSupportFragmentManager().findFragmentByTag(TAG_LISTFRIEND_FRAGMENT);

        friendsListFragment.setNextUsers(users);
    }


    private void changeFragment() {
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT);
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAIN_FRAGMENT);
        if(profileFragment == null) {
            profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .remove(mainFragment)
                    .add(R.id.container, profileFragment, TAG_PROFILE_FRAGMENT)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    public void getUserProfileFromVk() {
        VKRequest request = VKApi.users().get(VKParameters
                .from(VKApiConst.FIELDS,
                        User.ConstFields.getAllFields()));
        request.setPreferredLang("ru");
        request.executeWithListener(new VKUser(this));

    }

    public void setUserProfile(User user) {
        changeFragment();
        profileFragment.setUser(user);
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        fm.popBackStack();
        fm.executePendingTransactions();

        update();
    }

    private void update() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        ProfileFragment pf= (ProfileFragment)fm.findFragmentByTag(TAG_PROFILE_FRAGMENT);
        FriendsListFragment flf = (FriendsListFragment)fm.findFragmentByTag(TAG_LISTFRIEND_FRAGMENT);
        ProfileFragment fpf= (ProfileFragment)fm.findFragmentByTag(TAG_FRIENDS_PROFILE_FRAGMENT);

        if(pf != null && pf.isAdded()) {
            getUserProfileFromVk();
        }
        else if(flf != null && flf.isAdded()) {
            getUserFriendsFromVk(20, 0);
        }
        else if(fpf != null && fpf.isAdded()) {
            getUserFriendProfileFromVk(flf.getUserId());
        }
    }



    private void onClickProfilePhoto() {
        if(profileFragment == null)
            profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE_FRAGMENT);

        String url = profileFragment.getPhotoUrl();
        changeFragmentToBigImage(TAG_PROFILE_FRAGMENT);
        imageFragment.setImage(url);
    }

    private void changeFragmentToBigImage(String tag) {
        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(tag);
        imageFragment = (ImageFragment) getSupportFragmentManager().findFragmentByTag(TAG_IMAGE_FRAGMENT);
        if(imageFragment == null) {
            imageFragment = new ImageFragment();
            getSupportFragmentManager().beginTransaction()
                    .remove(profileFragment)
                    .add(R.id.container, imageFragment, TAG_IMAGE_FRAGMENT)
                    .addToBackStack(TAG_PROFILE_FRAGMENT + TAG_IMAGE_FRAGMENT)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void onClickFriendProfilePhoto() {
        if(friendsProfileFragment == null)
            friendsProfileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRIENDS_PROFILE_FRAGMENT);

        String url = friendsProfileFragment.getPhotoUrl();
        changeFragmentToBigImage(TAG_FRIENDS_PROFILE_FRAGMENT);
        imageFragment.setImage(url);
    }

    public void onClickItemFriendsList(String id) {
        getUserFriendProfileFromVk(id);
    }

    private void changeFragmentToFriendsProfile() {
        friendsProfileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRIENDS_PROFILE_FRAGMENT);
        friendsListFragment = (FriendsListFragment) getSupportFragmentManager().findFragmentByTag(TAG_LISTFRIEND_FRAGMENT);
        if(friendsProfileFragment == null) {
            friendsProfileFragment = new ProfileFragment();
            friendsProfileFragment.setDisableButton(true);
            getSupportFragmentManager().beginTransaction()
                    .remove(friendsListFragment)
                    .add(R.id.container, friendsProfileFragment, TAG_FRIENDS_PROFILE_FRAGMENT)
                    .addToBackStack(TAG_LISTFRIEND_FRAGMENT + TAG_FRIENDS_PROFILE_FRAGMENT)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    public void getUserFriendProfileFromVk(String id) {
        VKRequest request = VKApi.users().get(VKParameters
                .from(VKApiConst.USER_IDS, id,
                        VKApiConst.FIELDS,
                        User.ConstFields.getAllFields()));
        request.setPreferredLang("ru");
        request.executeWithListener(new VKFriendUser(this));

    }

    public void setUserFriendProfile(User user) {
        changeFragmentToFriendsProfile();
        friendsProfileFragment.setUser(user);
    }





    public static class MainFragment extends Fragment {

        private Button signVkBtn;
        private View.OnClickListener clickListener;

        public MainFragment() {
        }

        @Override
        public void onDetach() {
            super.onDetach();
            clickListener = null;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            clickListener = (View.OnClickListener)activity;
        }

        private void setupUI(View v) {
            signVkBtn = (Button)v.findViewById(R.id.buttonSignVk);
            signVkBtn.setOnClickListener(clickListener);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setupUI(rootView);
            return rootView;
        }

    }
}
