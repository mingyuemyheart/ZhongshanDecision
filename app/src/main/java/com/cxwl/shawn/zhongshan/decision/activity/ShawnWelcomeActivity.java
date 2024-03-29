package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.common.MyApplication;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 欢迎界面
 */
public class ShawnWelcomeActivity extends ShawnBaseActivity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_welcome);

		//点击Home键后再点击APP图标，APP重启而不是回到原来界面
		if (!isTaskRoot()) {
			finish();
			return;
		}
		//点击Home键后再点击APP图标，APP重启而不是回到原来界面

		mContext = this;

//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int width = dm.widthPixels;
//		ImageView imageView = findViewById(R.id.imageView);
//		ViewGroup.LayoutParams params = imageView.getLayoutParams();
//		params.width = width/3;
//		imageView.setLayoutParams(params);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!TextUtils.isEmpty(MyApplication.USERNAME) && !TextUtils.isEmpty(MyApplication.PASSWORD)) {
					okHttpLogin();
				} else {
					startActivity(new Intent(mContext, LoginActivity.class));
					finish();
				}
			}
		}, 2000);

	}

	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if (KeyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	private void okHttpLogin() {
		FormBody.Builder builder = new FormBody.Builder();
		final String url = "http://zsadmin.cxwldata.cn/api/auth/login";
		builder.add("name", MyApplication.USERNAME);
		builder.add("password", MyApplication.PASSWORD);
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(@NonNull Call call, @NonNull IOException e) {
					}
					@Override
					public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("code")) {
											int code = obj.getInt("code");
											if (code == 1) {
												startActivity(new Intent(ShawnWelcomeActivity.this, ShawnMainActivity.class));
											} else {
												startActivity(new Intent(mContext, LoginActivity.class));
											}
											finish();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}
	
}
