package com.example.androidfinal;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name;
    public String email;
    public String phone;
    public int wallet;

    public User()
    {
    }

    public User(String name, String email, String phone)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.wallet = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }
}
