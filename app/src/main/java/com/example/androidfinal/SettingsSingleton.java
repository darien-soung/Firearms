package com.example.androidfinal;


public class SettingsSingleton {
    static SettingsSingleton singletonObj = new SettingsSingleton();

    private boolean notificationStatus;
    private int currency;
    private String curUsername;


    private SettingsSingleton()
    {

    }

    public static SettingsSingleton getInstance()
    {
        return singletonObj;
    }

    public boolean isNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(boolean notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getCurUsername() {
        return curUsername;
    }

    public void setCurUsername(String curUsername) {
        this.curUsername = curUsername;
    }

}

