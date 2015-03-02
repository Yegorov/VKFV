package com.yegorov.vkfv.vk;

import org.json.JSONObject;

public interface UserPartDataAccessible {
    public String getId();
    public void setId(String id);

    public String getDomain();
    public void setDomain(String domain);

    public String getFirstName();
    public void setFirstName(String firstName);

    public String getLastName();
    public void setLastName(String lastName);

    public String getName();
    public String getUserUrl();

    public String getUrlPhoto50();
    public void setUrlPhoto50(String urlPhoto50);

    public int getOnline();
    public void setOnline(int online);

    public String getUrlPhoto200();
    public void setUrlPhoto200(String urlPhoto50);
}
