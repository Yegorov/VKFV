package com.yegorov.vkfv.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.yegorov.vkfv.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class LazyGetImage {
    public final static String TAG = "LazyGetImage";
    private static LazyGetImage lazyGetImage;

    private static LazyGetImage[] lazyGetImages;
    public final static int COUNT = 10;

    private boolean hasSDCard;
    private boolean hasCacheDir;
    private final String cacheDir = ".VKCacheImg";
    private String absPath;

    private LazyGetImage() {
        checkSDCard();
        absPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        createCacheDir();
        Log.d(TAG, "SDCardPath: " + absPath);
    }

    private void checkSDCard() {
        hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        Log.d(TAG, "hasSDCard: " + hasSDCard);
    }

    private void createCacheDir() {
        File dir = new File(getCacheDir());
        Log.d(TAG, "CacheDir!!!: " + dir.getPath());
        if(!dir.exists()) {
            hasCacheDir = dir.mkdir();
        }
        else {
            hasCacheDir = true;
        }
        Log.d(TAG, "hasCacheDir: " + hasCacheDir);
    }

    private boolean isContainsFile(String name) {
        File file = new File(getCacheDir() + name);
        if(file.exists()){
            Log.d(TAG, "isContainsFile: " + "true");
            return true;
        }
        else {
            Log.d(TAG, "isContainsFile: " + "false");
            return false;
        }
    }


    private String urlToNameFile(String url) {
        String path;
        path = url.replaceAll("((https?)|:|\\.|\\/)", "");
        Log.d(TAG, "urlToNameFile: " + path);
        return path;
    }

    private Bitmap downloadImage(String urlStr) {
        Bitmap image = null;
        try {
            URL url = new URL(urlStr);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Log.d(TAG, "downloadImage: " + "image download success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void saveImage(Bitmap image, String name) {
        if(!isContainsFile(name)) {
            synchronized (LazyGetImage.class) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(getCacheDir() + name));
                    image.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                    Log.d(TAG, "saveImage: " + "image save success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private Bitmap getDefaultImage(Context ctx) {
        return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.vk_dog);
    }

    private String getCacheDir() {
        Log.d(TAG, absPath + "/" + cacheDir + "/");
        return absPath + "/" + cacheDir + "/";

    }
    public static LazyGetImage getInstance() {
        if(lazyGetImage == null) {
            synchronized (LazyGetImage.class) {
                if(lazyGetImage == null) {
                    lazyGetImage = new LazyGetImage();
                }
            }
        }
        return lazyGetImage;
    }

    public static LazyGetImage[] getInstances() {
        if(lazyGetImages == null) {
            synchronized (LazyGetImage.class) {
                if(lazyGetImages == null) {
                    lazyGetImages = new LazyGetImage[COUNT];
                    for(int i = 0; i < COUNT; ++i)
                        lazyGetImages[i] = new LazyGetImage();
                }
            }
        }
        return lazyGetImages;
    }

    public static String urlToName(String url) {
        String path;
        path = url.replaceAll("((https?)|:|\\.|\\/)", "");
        return path;
    }

    public synchronized Bitmap getImage(String url, Context ctx) {
        String nameFile = urlToNameFile(url);
        Bitmap image = null;
        if(hasSDCard && hasCacheDir) {
            if(isContainsFile(nameFile)) {
                image = BitmapFactory.decodeFile(getCacheDir() + nameFile);
            }
            else {
                //download
                image = downloadImage(url);
                if(image != null) {
                    //save
                    saveImage(image, nameFile);
                }
            }
        }
        else {
            //download
            image = downloadImage(url);
        }

        if(image == null) {
            image = getDefaultImage(ctx);
        }

        return image;
    }

}
