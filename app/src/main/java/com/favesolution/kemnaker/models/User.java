package com.favesolution.kemnaker.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Daniel on 12/7/2015 for Kemnaker project.
 */
@Table(name = "users")
public class User extends Model implements Parcelable {
    private String mNama;
    private String mGender;
    private String mPeran;
    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setNama(jsonObject.getString("nama"));
            user.setGender(jsonObject.getString("gender"));
            user.setPeran(jsonObject.getString("nama_peran"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
    public String getNamaPanggilan() {
        String prefix = "";
        if (getPeran().equalsIgnoreCase("direktur") || getPeran().toLowerCase().startsWith("kasubdit")) {
            if(getGender().equalsIgnoreCase("pria"))
                prefix="Bapak ";
            else
                prefix="Ibu ";
        }
        return prefix+getNama();
    }
    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getNama() {
        return mNama;
    }

    public void setNama(String nama) {
        mNama = nama;
    }

    public String getPeran() {
        return mPeran;
    }

    public void setPeran(String peran) {
        mPeran = peran;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mNama);
        dest.writeString(this.mGender);
        dest.writeString(this.mPeran);
    }

    protected User(Parcel in) {
        this.mNama = in.readString();
        this.mGender = in.readString();
        this.mPeran = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
