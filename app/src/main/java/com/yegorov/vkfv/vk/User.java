package com.yegorov.vkfv.vk;

import com.yegorov.vkfv.util.ProjectVars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class User implements UserPartDataAccessible {
    //users.get,friends.get
    public final static String TAG = "User";
    public final static String UNKNOWN = "Unknown";

    private String id;
    private String domain;

    private String firstName;
    private String lastName;

    private String urlPhoto50;
    private String urlPhoto200;
    private String urlPhotoMaxOrig;

    private String deactivated;
    private int online;
    private int lastSeen;

    private String status;

    private String birthday;

    private String city;
    private String country;
    private String univ;

    private int countFriends;

    private String[] otherConnections;

    public User() {
        this.id               = null;
        this.domain           = null;
        this.firstName        = null;
        this.lastName         = null;
        this.urlPhoto50       = null;
        this.urlPhoto200      = null;
        this.urlPhotoMaxOrig  = null;
        this.deactivated      = null;
        this.online           = -1;
        this.lastSeen         = -1;
        this.status           = null;
        this.birthday         = null;
        this.city             = null;
        this.country          = null;
        this.countFriends     = -1;
        this.otherConnections = null;
        this.univ             = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getUserUrl() {
        return ProjectVars.HTTP + ProjectVars.VK_COM + "/" + domain;
    }
    @Override
    public String getUrlPhoto50() {
        return urlPhoto50;
    }
    @Override
    public void setUrlPhoto50(String urlPhoto50) {
        this.urlPhoto50 = urlPhoto50;
    }
    @Override
    public String getUrlPhoto200() {
        return urlPhoto200;
    }
    @Override
    public void setUrlPhoto200(String urlPhoto200) {
        this.urlPhoto200 = urlPhoto200;
    }

    public String getUrlPhotoMaxOrig() {
        return urlPhotoMaxOrig;
    }

    public void setUrlPhotoMaxOrig(String urlPhotoMaxOrig) {
        this.urlPhotoMaxOrig = urlPhotoMaxOrig;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(String deactivated) {
        this.deactivated = deactivated;
    }

    @Override
    public int getOnline() {
        return online;
    }
    @Override
    public void setOnline(int online) {
        this.online = online;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCountFriends() {
        return countFriends;
    }

    public void setCountFriends(int countFriends) {
        this.countFriends = countFriends;
    }

    public String[] getOtherConnections() {
        return otherConnections;
    }

    public String getUniversity() {
        return univ;
    }

    public void setUniversity(String univ) {
        this.univ = univ;
    }

    public void setOtherConnections(String[] otherConnections) {
        this.otherConnections = otherConnections;
    }

    public static User parseFromJSON(JSONObject jsonObject) {
        User user = new User();
        JSONObject o;
        JSONObject temp;
        try {
            o = jsonObject.getJSONArray("response").getJSONObject(0);
            user.setId(o.optString(ConstFields.ID, ""));
            user.setDomain(o.optString(ConstFields.DOMAIN, ""));
            user.setFirstName(o.optString(ConstFields.FIRST_NAME, UNKNOWN));
            user.setLastName(o.optString(ConstFields.LAST_NAME, UNKNOWN));
            user.setUrlPhoto50(o.optString(ConstFields.PHOTO_50, UNKNOWN));
            user.setUrlPhoto200(o.optString(ConstFields.PHOTO_200, UNKNOWN));
            user.setUrlPhotoMaxOrig(o.optString(ConstFields.PHOTO_MAX_ORIG, UNKNOWN));
            user.setDeactivated(o.optString(ConstFields.DEACTIVATED, "no"));
            user.setOnline(o.optInt(ConstFields.ONLINE, 0));

            temp = o.optJSONObject(ConstFields.LAST_SEEN);
            user.setLastSeen(temp.optInt(ConstFields.TIME, 0));

            user.setStatus(o.optString(ConstFields.STATUS, UNKNOWN));
            user.setBirthday(o.optString(ConstFields.BDATE, UNKNOWN));

            temp = o.optJSONObject(ConstFields.CITY);
            user.setCity(temp == null ? UNKNOWN : temp.optString(ConstFields.TITLE, UNKNOWN));

            temp = o.optJSONObject(ConstFields.COUNTRY);
            user.setCountry(temp == null ? UNKNOWN : temp.optString(ConstFields.TITLE, UNKNOWN));


            temp = o.optJSONArray(ConstFields.UNIVERSITIES) == null ? null : o.optJSONArray(ConstFields.UNIVERSITIES).optJSONObject(0);
            user.setUniversity(temp == null ? UNKNOWN : temp.optString(ConstFields.NAME, UNKNOWN));

            temp = o.optJSONObject(ConstFields.COUNTERS);
            user.setCountFriends(temp == null ? -1 : temp.optInt(ConstFields.FRIENDS, -1));

            String[] con = {ConstFields.TWITTER,
                            ConstFields.INSTAGRAM,
                            ConstFields.LIVEJOURNAL,
                            ConstFields.FACEBOOK,
                            ConstFields.SKYPE };

            ArrayList<String> arr = new ArrayList<>(5);
            for(String key : con) {
                String s = o.optString(key, null);
                if(s != null)
                    arr.add(key + ": " + s);
            }
            String[] str = new String[arr.size()];
            user.setOtherConnections(arr.toArray(str));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User[] parseArrayFromJSON(JSONObject jsonObject) {
        ArrayList<User> us = new ArrayList<>(20);
        JSONObject o;
        JSONArray array;
        JSONObject item;
        User user;
        int count;
        try {
            o = jsonObject.getJSONObject("response");
            count = o.optInt("count");
            array = o.getJSONArray("items");
            for(int i = 0; i < array.length(); ++i) {
                item = array.getJSONObject(i);
                user = new User();
                user.setFirstName(item.optString(ConstFields.FIRST_NAME, UNKNOWN));
                user.setLastName(item.optString(ConstFields.LAST_NAME, UNKNOWN));
                user.setDomain(item.optString(ConstFields.DOMAIN, UNKNOWN));
                user.setId(item.optString(ConstFields.ID, "1"));
                user.setOnline(item.optInt(ConstFields.ONLINE, 0));
                user.setUrlPhoto200(item.optString(ConstFields.PHOTO_200, UNKNOWN));
                us.add(user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        User[] users = new User[us.size()];
        us.toArray(users);
        return users;
    }
    public static class ConstFields {
        private final static String split         = ",";
        private static String allFields           = null;
        private static String someFields          = null;

        public final static String ID             = "id";
        public final static String DOMAIN         = "domain";
        public final static String FIRST_NAME     = "first_name";
        public final static String LAST_NAME      = "last_name";
        public final static String PHOTO_50       = "photo_50";
        public final static String PHOTO_200      = "photo_200";
        public final static String PHOTO_MAX_ORIG = "photo_max_orig";
        public final static String DEACTIVATED    = "deactivated";
        public final static String ONLINE         = "online";

        public final static String LAST_SEEN      = "last_seen";
        public final static String TIME           = "time";

        public final static String STATUS         = "status";
        public final static String BDATE          = "bdate";

        public final static String UNIVERSITIES   = "universities";
        public final static String NAME           = "name";

        public final static String CITY           = "city";
        public final static String COUNTRY        = "country";
        public final static String TITLE          = "title";

        public final static String COUNTERS       = "counters";
        public final static String ALBUMS         = "albums";
        public final static String VIDEOS         = "videos";
        public final static String AUDIOS         = "audios";
        public final static String PHOTOS         = "photos";
        public final static String FRIENDS        = "friends";
        public final static String GROUPS         = "groups";
        public final static String ONLINE_FRIENDS = "online_friends";
        public final static String MUTUAL_FRIENDS = "mutual_friends";
        public final static String USER_VIDEOS    = "user_videos";
        public final static String FOLLOWERS      = "followers";

        public final static String CONNECTIONS    = "connections";
        public final static String TWITTER        = "twitter";
        public final static String INSTAGRAM      = "instagram";
        public final static String LIVEJOURNAL    = "livejournal";
        public final static String FACEBOOK       = "facebook";
        public final static String SKYPE          = "skype";


        public final static String ORDER          = "order";
        public final static String HINTS          = "hints";

        /**
         * id,domain,first_name,last_name,photo_50,photo_200,photo_max_orig,
         * deactivated,online,last_seen,status,bdate,city,country,counters,universities,connections
         */
        public static String getAllFields() {
            if(allFields == null) {
                StringBuilder f = new StringBuilder();
                f.append(ID             + split);
                f.append(DOMAIN         + split);
                f.append(FIRST_NAME     + split);
                f.append(LAST_NAME      + split);
                f.append(PHOTO_50       + split);
                f.append(PHOTO_200      + split);
                f.append(PHOTO_MAX_ORIG + split);
                f.append(DEACTIVATED    + split);
                f.append(ONLINE         + split);
                f.append(LAST_SEEN      + split);
                f.append(STATUS         + split);
                f.append(BDATE          + split);
                f.append(CITY           + split);
                f.append(COUNTRY        + split);
                f.append(COUNTERS       + split);
                f.append(UNIVERSITIES   + split);
                f.append(CONNECTIONS);

                allFields = f.toString();
            }
            return allFields;
        }

        /**
         * id,domain,first_name,last_name,photo_50,online
         */
        public static String getSomeFields() {
            if(someFields == null) {
                StringBuilder f = new StringBuilder();
                f.append(ID         + split);
                f.append(DOMAIN     + split);
                f.append(FIRST_NAME + split);
                f.append(LAST_NAME  + split);
                f.append(ONLINE     + split);
                f.append(PHOTO_200);

                someFields = f.toString();
            }
            return someFields;
        }
    }
}
