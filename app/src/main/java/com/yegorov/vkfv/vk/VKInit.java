package com.yegorov.vkfv.vk;

import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;
import com.yegorov.vkfv.ui.MainActivity;

public class VKInit extends VKSdkListener {
    private final static String TAG = "VKInit";
    private MainActivity activity;

    public VKInit(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCaptchaError(VKError captchaError) {
        Toast.makeText(activity,
                captchaError.errorMessage,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCaptchaError");
    }

    @Override
    public void onTokenExpired(VKAccessToken expiredToken) {
        Toast.makeText(activity,
                "Token expired",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTokenExpired");
    }

    @Override
    public void onAccessDenied(VKError authorizationError) {
        Toast.makeText(activity,
                authorizationError.errorMessage,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onAccessDenied");
    }

    @Override
    public void onReceiveNewToken(VKAccessToken newToken) {
        newToken.saveTokenToSharedPreferences(activity,
                VKAccessToken.ACCESS_TOKEN);
        activity.getUserProfileFromVk();
        Log.d(TAG, "onReceiveNewToken");
        Log.d(TAG, "accessToken=" + newToken.accessToken);

    }

    @Override
    public void onAcceptUserToken(VKAccessToken token) {
        token.saveTokenToSharedPreferences(activity,
                VKAccessToken.ACCESS_TOKEN);
        activity.getUserProfileFromVk();
        Log.d(TAG, "onAcceptUserToken");
        Log.d(TAG, "accessToken=" + token.accessToken);
    }

    @Override
    public void onRenewAccessToken(VKAccessToken token) {
        token.saveTokenToSharedPreferences(activity,
                VKAccessToken.ACCESS_TOKEN);
        activity.getUserProfileFromVk();
        Log.d(TAG, "onRenewAccessToken");
        Log.d(TAG, "accessToken=" + token.accessToken);
    }

}
