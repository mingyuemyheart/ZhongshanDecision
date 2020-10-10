package com.cxwl.shawn.zhongshan.decision.util;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态获取权限
 */
public class AuthorityUtil {

    public static final int AUTHOR_MULTI = 1000;//多个权限一起申请
    public static final int AUTHOR_LOCATION = 1001;//定位权限
    public static final int AUTHOR_STORAGE = 1002;//存储权限
    public static final int AUTHOR_PHONE = 1003;//电话权限
    public static final int AUTHOR_CAMERA = 1004;//相机权限
//    public static final int AUTHOR_CONTACTS = 1005;//通讯录权限
//    public static final int AUTHOR_MICROPHONE = 1006;//麦克风权限
//    public static final int AUTHOR_SMS = 1007;//短信权限
//    public static final int AUTHOR_CALENDAR = 1008;//日历权限
//    public static final int AUTHOR_SENSORS = 1009;//传感器权限

    //需要申请的所有权限
    public static String[] allPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //拒绝的权限集合
    public static List<String> deniedList = new ArrayList<>();

    /**
     * 前往权限设置界面
     * @param message
     */
    public static void intentAuthorSetting(final Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText(message);
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
    }

}
