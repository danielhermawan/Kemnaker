package com.favesolution.kemnaker.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.favesolution.kemnaker.utils.ListConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Daniel on 12/7/2015 for Kemnaker project.
 */
@Table(name = "Dokumens")
public class Dokumen extends Model implements Parcelable {
    @Column(name = "remoteId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String mRemoteId;
    @Column(name = "noAgenda")
    private String mNoAgenda;
    @Column(name = "tanggalTerima")
    private Date mTanggalTerima;
    @Column(name = "asalSurat")
    private String mAsalSurat;
    @Column(name = "noTanggal")
    private String mNoTanggal;
    @Column(name = "perihal")
    private String mPerihal;
    @Column(name = "lajurDisposisi")
    private String mLajurDisposisi;
    @Column(name = "catatan")
    private String mCatatan;
    @Column(name = "namaTipe")
    private String mNamaTipe;
    public static Dokumen fromJson(JSONObject jsonObject) throws JSONException, ParseException {
        Dokumen dokumen = new Dokumen();
        dokumen.setRemoteId(jsonObject.getString("id"));
        dokumen.setNoAgenda(jsonObject.getString("no_agenda"));
        String tanggalTerima = jsonObject.getString("tanggal_terima");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
        dokumen.setTanggalTerima(format.parse(tanggalTerima));
        dokumen.setAsalSurat(jsonObject.getString("asal_surat"));
        dokumen.setNoTanggal(jsonObject.getString("no_tanggal"));
        dokumen.setPerihal(jsonObject.getString("perihal"));
        dokumen.setLajurDisposisi(jsonObject.getString("lajur_disposisi"));
        dokumen.setNamaTipe(jsonObject.getString("nama_tipe"));
        return dokumen;
    }
    public static List<Dokumen> fromJson(JSONArray jsonArray) throws JSONException, ParseException {
        List<Dokumen> dokumens = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject dokumenJson = jsonArray.getJSONObject(i);
            Dokumen dokumen = Dokumen.fromJson(dokumenJson);
            dokumens.add(dokumen);
        }
        return dokumens;
    }
    public static void saveDeleteListDokumen(List<Dokumen> dokumens) {
        new Delete().from(Dokumen.class).execute();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < dokumens.size(); i++) {
                Dokumen dokumen = dokumens.get(i);
                dokumen.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
    public static void addListDokumen(List<Dokumen> dokumens) {
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < dokumens.size(); i++) {
                Dokumen dokumen = dokumens.get(i);
                dokumen.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
    public static Dokumen getDokumenByRemoteId(String id) {
        return new Select()
                .from(Dokumen.class)
                .where("remoteId = ?",id)
                .executeSingle();
    }
    public static List<String> getTipeDokumenByUser(User user,String menu) {
        List<String> tipe = new ArrayList<>();
        if (user.getPeran().equalsIgnoreCase("direktur") && menu.equalsIgnoreCase(ListConstant.MENU_ARSIP)) {
            tipe.add("Semua");
            tipe.add("Rahasia");
            tipe.add("Penting Segera");
            tipe.add("Biasa");
        } else {
            tipe.add("Semua");
            tipe.add("Penting Segera");
            tipe.add("Biasa");
        }
        return tipe;
    }
    public static List<Dokumen> filterDokumenByTipe(List<Dokumen> dokumens,String tipe) {
        List<Dokumen> filteredDokumens = new ArrayList<>();
        for (int i = 0; i < dokumens.size(); i++) {
            if (dokumens.get(i).getNamaTipe().equalsIgnoreCase(tipe)) {
                filteredDokumens.add(dokumens.get(i));
            }
        }
        return filteredDokumens;
    }
    public String getAsalSurat() {
        return mAsalSurat;
    }

    public void setAsalSurat(String asalSurat) {
        mAsalSurat = asalSurat;
    }

    public String getCatatan() {
        return mCatatan;
    }

    public void setCatatan(String catatan) {
        mCatatan = catatan;
    }

    public String getLajurDisposisi() {
        return mLajurDisposisi;
    }

    public void setLajurDisposisi(String lajurDisposisi) {
        mLajurDisposisi = lajurDisposisi;
    }

    public String getNamaTipe() {
        return mNamaTipe;
    }

    public void setNamaTipe(String namaTipe) {
        mNamaTipe = namaTipe;
    }

    public String getNoAgenda() {
        return mNoAgenda;
    }

    public void setNoAgenda(String noAgenda) {
        mNoAgenda = noAgenda;
    }

    public String getNoTanggal() {
        return mNoTanggal;
    }

    public void setNoTanggal(String noTanggal) {
        mNoTanggal = noTanggal;
    }

    public String getPerihal() {
        return mPerihal;
    }

    public void setPerihal(String perihal) {
        mPerihal = perihal;
    }

    public Date getTanggalTerima() {
        return mTanggalTerima;
    }

    public void setTanggalTerima(Date tanggalTerima) {
        mTanggalTerima = tanggalTerima;
    }

    public Dokumen() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mNoAgenda);
        dest.writeLong(mTanggalTerima != null ? mTanggalTerima.getTime() : -1);
        dest.writeString(this.mAsalSurat);
        dest.writeString(this.mNoTanggal);
        dest.writeString(this.mPerihal);
        dest.writeString(this.mLajurDisposisi);
        dest.writeString(this.mCatatan);
        dest.writeString(this.mNamaTipe);
    }

    protected Dokumen(Parcel in) {
        this.mNoAgenda = in.readString();
        long tmpMTanggalTerima = in.readLong();
        this.mTanggalTerima = tmpMTanggalTerima == -1 ? null : new Date(tmpMTanggalTerima);
        this.mAsalSurat = in.readString();
        this.mNoTanggal = in.readString();
        this.mPerihal = in.readString();
        this.mLajurDisposisi = in.readString();
        this.mCatatan = in.readString();
        this.mNamaTipe = in.readString();
    }

    public static final Creator<Dokumen> CREATOR = new Creator<Dokumen>() {
        public Dokumen createFromParcel(Parcel source) {
            return new Dokumen(source);
        }

        public Dokumen[] newArray(int size) {
            return new Dokumen[size];
        }
    };

    public String getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(String remoteId) {
        mRemoteId = remoteId;
    }
}

