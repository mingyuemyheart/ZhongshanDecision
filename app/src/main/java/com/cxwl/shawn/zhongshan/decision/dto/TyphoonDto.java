package com.cxwl.shawn.zhongshan.decision.dto;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.util.WeatherUtil;

import java.util.GregorianCalendar;

public class TyphoonDto implements Parcelable {

	public String publishName,publishCode;//发布单位名称、编号
	public String createTime;//台风创建时间
	public String name;//台风名称
	public String id;//台风id，idea平台
	public String tId;//台风id，台风网，只有北京台才使用
	public String code;//台风code
	public String enName;//台风应为名称
	public String status;//台风状态,stop、start
	public double lat;
	public double lng;
	public String pressure;//气压
	public String max_wind_speed = "";//最大风速
	public String move_speed = "";//移动速度
	public String wind_dir;//移动方向
	public String type;//台风类型
	public String radius_7,radius_10;
	public String time;
	public boolean isFactPoint = true;//true为实况点，false为预报点
	public String strength;//台风强度
	public boolean isSelected;
	
	public String content(Context context) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("时间：").append(time);
		if(!TextUtils.isEmpty(max_wind_speed)){
			if (!TextUtils.isEmpty(type)) {
				if (TextUtils.equals(type, "1")) {
					strength = context.getString(R.string.typhoon_level1);
				}else if (TextUtils.equals(type, "2")) {
					strength = context.getString(R.string.typhoon_level2);
				}else if (TextUtils.equals(type, "3")) {
					strength = context.getString(R.string.typhoon_level3);
				}else if (TextUtils.equals(type, "4")) {
					strength = context.getString(R.string.typhoon_level4);
				}else if (TextUtils.equals(type, "5")) {
					strength = context.getString(R.string.typhoon_level5);
				}else if (TextUtils.equals(type, "6")) {
					strength = context.getString(R.string.typhoon_level6);
				}
				buffer.append("\n").append("中心风力："+ WeatherUtil.getHourWindForce(Float.parseFloat(max_wind_speed))+"("+strength+")，");
			}
			buffer.append(max_wind_speed).append("米/秒");
		}
		if(!TextUtils.isEmpty(pressure)){
			buffer.append("\n").append("中心气压：").append(pressure).append("hPa");
		}
		if(!TextUtils.isEmpty(radius_7)){
			buffer.append("\n").append("7级风圈半径：").append(radius_7).append("公里");
		}
		if(!TextUtils.isEmpty(radius_10)){
			buffer.append("\n").append("10级风圈半径：").append(radius_10).append("公里");
		}
				
		return buffer.toString();
	}
	
	public int icon() {
		int power = TextUtils.isEmpty(type) ? -1: Integer.parseInt(type);
		if(power == 1){
			return R.drawable.typhoon_level1;
		}else if(power == 2){
			return R.drawable.typhoon_level2;
		}else if(power == 3){
			return R.drawable.typhoon_level3;
		}else if(power == 4){
			return R.drawable.typhoon_level4;
		}else if(power == 5){
			return R.drawable.typhoon_level5;
		}else if(power == 6){
			return R.drawable.typhoon_level6;
		}
		return R.drawable.typhoon_yb;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.publishName);
		dest.writeString(this.publishCode);
		dest.writeString(this.createTime);
		dest.writeString(this.name);
		dest.writeString(this.id);
		dest.writeString(this.tId);
		dest.writeString(this.code);
		dest.writeString(this.enName);
		dest.writeString(this.status);
		dest.writeDouble(this.lat);
		dest.writeDouble(this.lng);
		dest.writeString(this.pressure);
		dest.writeString(this.max_wind_speed);
		dest.writeString(this.move_speed);
		dest.writeString(this.wind_dir);
		dest.writeString(this.type);
		dest.writeString(this.radius_7);
		dest.writeString(this.radius_10);
		dest.writeString(this.time);
		dest.writeByte(this.isFactPoint ? (byte) 1 : (byte) 0);
		dest.writeString(this.strength);
		dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
	}

	public TyphoonDto() {
	}

	protected TyphoonDto(Parcel in) {
		this.publishName = in.readString();
		this.publishCode = in.readString();
		this.createTime = in.readString();
		this.name = in.readString();
		this.id = in.readString();
		this.tId = in.readString();
		this.code = in.readString();
		this.enName = in.readString();
		this.status = in.readString();
		this.lat = in.readDouble();
		this.lng = in.readDouble();
		this.pressure = in.readString();
		this.max_wind_speed = in.readString();
		this.move_speed = in.readString();
		this.wind_dir = in.readString();
		this.type = in.readString();
		this.radius_7 = in.readString();
		this.radius_10 = in.readString();
		this.time = in.readString();
		this.isFactPoint = in.readByte() != 0;
		this.strength = in.readString();
		this.isSelected = in.readByte() != 0;
	}

	public static final Parcelable.Creator<TyphoonDto> CREATOR = new Parcelable.Creator<TyphoonDto>() {
		@Override
		public TyphoonDto createFromParcel(Parcel source) {
			return new TyphoonDto(source);
		}

		@Override
		public TyphoonDto[] newArray(int size) {
			return new TyphoonDto[size];
		}
	};
}
