package com.yegorov.vkfv.vk;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.yegorov.vkfv.ui.MainActivity;

import org.json.JSONException;

public class VKFriendsAdd extends VKRequest.VKRequestListener {
    public final static String TAG = "VKFriendsAdd";

    private Activity activity;

    public VKFriendsAdd(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onComplete(VKResponse response) {
        Log.d(TAG, "onComplete");

        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            User[] users = User.parseArrayFromJSON(response.json);
            try {
                Log.d(TAG, response.json.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mainActivity.addFriendsList(users);
        }

    }

    @Override
    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        Log.d(TAG, "attemptFailed");
    }

    @Override
    public void onError(VKError error) {
        Toast.makeText(activity, error.errorMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onError = " + error.errorMessage);
    }
}
