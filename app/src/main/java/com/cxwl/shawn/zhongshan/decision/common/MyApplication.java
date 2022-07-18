package com.cxwl.shawn.zhongshan.decision.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		getUserInfo(this);
	}

	//本地保存用户信息参数
	public static String USERINFO = "userInfo";//userInfo sharedPreferance名称
	public static class UserInfo {
		public static final String uId = "uId";
		public static final String userName = "uName";
		public static final String passWord = "pwd";
		public static final String mobile = "mobile";
	}

	public static String UID = "";//用户id
	public static String USERNAME = "";//用户名
	public static String PASSWORD = "";//用户密码
	public static String MOBILE = "";

	public static void getUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		UID = sharedPreferences.getString(UserInfo.uId, UID);
		USERNAME = sharedPreferences.getString(UserInfo.userName, USERNAME);
		PASSWORD = sharedPreferences.getString(UserInfo.passWord, PASSWORD);
		MOBILE = sharedPreferences.getString(UserInfo.mobile, MOBILE);
	}

	public static void saveUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(UserInfo.uId, UID);
		editor.putString(UserInfo.userName, USERNAME);
		editor.putString(UserInfo.passWord, PASSWORD);
		editor.putString(UserInfo.mobile, MOBILE);
		editor.apply();
	}

	public static void clearUserInfo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		UID = "";
		USERNAME = "";
		PASSWORD = "";
		MOBILE = "";
	}

}
