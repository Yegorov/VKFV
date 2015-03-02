package com.yegorov.vkfv.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yegorov.vkfv.R;
import com.yegorov.vkfv.util.LazyGetImage;
import com.yegorov.vkfv.vk.User;
import com.yegorov.vkfv.vk.UserPartDataAccessible;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsListFragment extends Fragment {
    public static final String TAG = "FriendsListFragment";
    private ListView  friendsList;
    private String userId = "0";

    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int cacheSize = maxMemory / 8;
    private LruCache<String, Bitmap> mMemoryCache;


    private List<UserPartDataAccessible> users;


    public FriendsListFragment() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUsers(UserPartDataAccessible[] users) {
        this.users = new ArrayList<>(Arrays.asList(users));
        initUI();
    }

    public void setNextUsers(UserPartDataAccessible[] users) {
        UserAdapter userAdapter = (UserAdapter)friendsList.getAdapter();
        userAdapter.getList().addAll(Arrays.asList(users));
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends_list, container, false);
        setupUI(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(savedInstanceState != null) {
            initUI();
        }

    }

    private void setupUI(View v) {
        friendsList = (ListView) v.findViewById(R.id.friendsListView);
        friendsList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int visibleThreshold = 5;
            private int previousTotal = 0;
            private boolean loading = true;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                    ((MainActivity)getActivity()).getUserNextFriendsFromVk(20, totalItemCount);
                    loading = true;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                //    return;
                //}
            }

        });

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userId = users.get(position).getId();
                ((MainActivity)getActivity()).onClickItemFriendsList(userId);
            }
        });
    }

    private void initUI() {
        UserAdapter adapter = new UserAdapter(getActivity(), this, users);
        friendsList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public static class UserAdapter extends ArrayAdapter<UserPartDataAccessible> {
        private final Activity context;
        private final FriendsListFragment fragment;
        private List<UserPartDataAccessible> users;

        private LazyGetImage[] lgis;

        public List<UserPartDataAccessible> getList() {
            return users;
        }

        Bitmap loadBitmap;

        public UserAdapter(Activity context, Fragment fragment, List<UserPartDataAccessible> objects) {
            super(context, R.layout.friends_list_item, objects);
            this.context = context;
            this.users = objects;
            this.fragment = (FriendsListFragment)fragment;
            lgis = LazyGetImage.getInstances();

            loadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loader_gray);
        }

        static class ViewHolder {
            public TextView  name;
            public TextView  adressVk;
            public ImageView profilePhoto;
            public ImageView onlineTag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.friends_list_item, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                viewHolder.adressVk = (TextView) rowView.findViewById(R.id.urlProfile);
                viewHolder.profilePhoto = (ImageView) rowView.findViewById(R.id.profilePhoto);
                viewHolder.onlineTag = (ImageView) rowView.findViewById(R.id.online);

                rowView.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) rowView.getTag();
            final UserPartDataAccessible user = users.get(position);
            final int pos = position;

            holder.name.setText(user.getName());

            if(user.getOnline() > 0)
                holder.onlineTag.setVisibility(View.VISIBLE);
            else
                holder.onlineTag.setVisibility(View.INVISIBLE);

            holder.adressVk.setText(user.getUserUrl());


            holder.profilePhoto.setImageBitmap(loadBitmap);

            Bitmap bitmap;
            final String bitmapStr = LazyGetImage.urlToName(user.getUrlPhoto200());
            if((bitmap = fragment.getBitmapFromMemCache(bitmapStr)) == null) {

                // perhaps use ThreadPool (Executors java.concurrent)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap img = lgis[pos % LazyGetImage.COUNT].getImage(user.getUrlPhoto200(), (Context) context);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.profilePhoto.setImageBitmap(img);
                                fragment.addBitmapToMemoryCache(bitmapStr, img);
                            }
                        });
                    }
                }).start();
            }
            else {
                holder.profilePhoto.setImageBitmap(bitmap);
            }

            return rowView;
        }



    }

}
