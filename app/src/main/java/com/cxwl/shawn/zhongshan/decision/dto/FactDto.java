package com.cxwl.shawn.zhongshan.decision.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class FactDto implements Parcelable {

    public double lat, lng, rain, temp, windS;
    public float windD;
    public String pro, city, dis, town, vill;
    public String stationId, stationName,time,columnName,unit;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.rain);
        dest.writeDouble(this.temp);
        dest.writeDouble(this.windS);
        dest.writeFloat(this.windD);
        dest.writeString(this.pro);
        dest.writeString(this.city);
        dest.writeString(this.dis);
        dest.writeString(this.town);
        dest.writeString(this.vill);
        dest.writeString(this.stationId);
        dest.writeString(this.stationName);
        dest.writeString(this.time);
        dest.writeString(this.columnName);
        dest.writeString(this.unit);
    }

    public FactDto() {
    }

    protected FactDto(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.rain = in.readDouble();
        this.temp = in.readDouble();
        this.windS = in.readDouble();
        this.windD = in.readFloat();
        this.pro = in.readString();
        this.city = in.readString();
        this.dis = in.readString();
        this.town = in.readString();
        this.vill = in.readString();
        this.stationId = in.readString();
        this.stationName = in.readString();
        this.time = in.readString();
        this.columnName = in.readString();
        this.unit = in.readString();
    }

    public static final Creator<FactDto> CREATOR = new Creator<FactDto>() {
        @Override
        public FactDto createFromParcel(Parcel source) {
            return new FactDto(source);
        }

        @Override
        public FactDto[] newArray(int size) {
            return new FactDto[size];
        }
    };
}
