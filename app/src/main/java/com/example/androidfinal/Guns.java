package com.example.androidfinal;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Guns implements Parcelable {
    private String Name, WeaponClass, FiringMode;
    private int Price, Weight, Damage;
    private String ImageUrl;
    private String mKey;


    public Guns()
    {

    }

    public Guns(String imageUrl, String name, String weaponClass,
                int price, String firingMode, int weight, int damage)
    {
        if(name.trim().equals(""))
            name = "No name";

        if(weaponClass.trim().equals(""))
            weaponClass = "null";

        if(firingMode.trim().equals(""))
            firingMode = "null";

        Name = name;
        WeaponClass = weaponClass;
        FiringMode = firingMode;
        Price = price;
        Weight = weight;
        Damage = damage;
        ImageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getWeaponClass() {
        return WeaponClass;
    }

    public void setWeaponClass(String weaponClass) {
        this.WeaponClass = weaponClass;
    }

    public String getFiringMode() {
        return FiringMode;
    }

    public void setFiringMode(String firingMode) {
        this.FiringMode = firingMode;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        this.Price = price;
    }

    public int getWeight() {
        return Weight;
    }

    public void setWeight(int weight) {
        this.Weight = weight;
    }

    public int getDamage() {
        return Damage;
    }

    public void setDamage(int damage) {
        this.Damage = damage;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }


    //PARCELABLE DETAILS
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(WeaponClass);
        parcel.writeString(FiringMode);
        parcel.writeInt(Weight);
        parcel.writeInt(Damage);
        parcel.writeInt(Price);
        parcel.writeString(ImageUrl);
    }

    public static final Parcelable.Creator<Guns> CREATOR = new Parcelable.Creator<Guns>() {
        public Guns createFromParcel(Parcel in) {
            return new Guns(in);
        }

        public Guns[] newArray(int size) {
            return new Guns[size];
        }
    };

    private Guns(Parcel in) {
        Name = in.readString();
        WeaponClass = in.readString();
        FiringMode = in.readString();
        Weight = in.readInt();
        Damage  = in.readInt();
        Price  = in.readInt();
        ImageUrl = in.readString();
    }
}
