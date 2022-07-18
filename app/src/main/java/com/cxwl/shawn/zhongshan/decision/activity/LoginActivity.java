package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.common.MyApplication;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends ShawnBaseActivity implements OnClickListener {

	private EditText etUserName, etPwd;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		TextView tvLogin = findViewById(R.id.tvLogin);
		tvLogin.setOnClickListener(this);
		etUserName = findViewById(R.id.etUserName);
		etPwd = findViewById(R.id.etPwd);
		loadingView = findViewById(R.id.loadingView);
	}

	private void okHttpLogin() {
		FormBody.Builder builder = new FormBody.Builder();
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(etPwd.getText().toString())) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}

		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://zsadmin.cxwldata.cn/api/auth/login";
		builder.add("name", etUserName.getText().toString());
		builder.add("password", etPwd.getText().toString());
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
								loadingView.setVisibility(View.GONE);
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("code")) {
											int code = obj.getInt("code");
											if (code == 1) {
												//把用户信息保存在sharedPreferance里
												MyApplication.USERNAME = etUserName.getText().toString();
												MyApplication.PASSWORD = etPwd.getText().toString();
												MyApplication.saveUserInfo(LoginActivity.this);

												startActivity(new Intent(LoginActivity.this, ShawnMainActivity.class));
												finish();
											} else {
												Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
											}
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

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.tvLogin) {
			okHttpLogin();
		}
	}
}
