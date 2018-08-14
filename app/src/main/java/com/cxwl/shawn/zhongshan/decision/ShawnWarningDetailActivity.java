package com.cxwl.shawn.zhongshan.decision;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.common.CONST;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;
import com.cxwl.shawn.zhongshan.decision.manager.DBManager;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 预警详情
 */
public class ShawnWarningDetailActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private LinearLayout llBack;
	private TextView tvTitle,tvName,tvTime,tvIntro,tvGuide;
	private ImageView imageView;//预警图标
	private WarningDto data;
	private ScrollView scrollView;
	private SwipeRefreshLayout refreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_warning_detail);
		mContext = this;
		initRefreshLayout();
		initWidget();
	}

	private void initRefreshLayout() {
		refreshLayout = findViewById(R.id.refreshLayout);
		refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
		refreshLayout.setProgressViewEndTarget(true, 300);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
	}
	
	private void refresh() {
		if (getIntent().hasExtra("data")) {
			data = getIntent().getExtras().getParcelable("data");
			if (data != null && !TextUtils.isEmpty(data.html)) {
				OkHttpWarningDetail("http://decision.tianqi.cn/alarm12379/content2/"+data.html);
			}
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("预警详情");
		imageView = findViewById(R.id.imageView);
		tvName = findViewById(R.id.tvName);
		tvTime = findViewById(R.id.tvTime);
		tvIntro = findViewById(R.id.tvIntro);
		tvGuide = findViewById(R.id.tvGuide);
		scrollView = findViewById(R.id.scrollView);

		refresh();
	}
	
	/**
	 * 初始化数据库
	 */
	private void initDBManager() {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		Cursor cursor = database.rawQuery("select * from " + DBManager.TABLE_NAME2 + " where WarningId = " + "\"" + data.type+data.color + "\"",null);
		String content = null;
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			content = cursor.getString(cursor.getColumnIndex("WarningGuide"));
		}
		if (!TextUtils.isEmpty(content)) {
			tvGuide.setText("防御指南：\n"+content);
			tvGuide.setVisibility(View.VISIBLE);
		}else {
			tvGuide.setVisibility(View.GONE);
		}
		dbManager.closeDatabase();
	}
	
	/**
	 * 获取预警详情
	 */
	private void OkHttpWarningDetail(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (object != null) {
											if (!object.isNull("sendTime")) {
												tvTime.setText(object.getString("sendTime"));
											}

											if (!object.isNull("description")) {
												tvIntro.setText(object.getString("description"));
											}

											String name = object.getString("headline");
											if (!TextUtils.isEmpty(name)) {
												tvName.setText(name.replace("发布", "发布\n"));
											}

											String severityCode = object.getString("severityCode");
											String eventType = object.getString("eventType");
											Bitmap bitmap = null;
											if (severityCode.equals(CONST.blue[0])) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+eventType+CONST.blue[1]+CONST.imageSuffix);
												if (bitmap == null) {
													bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.blue[1]+CONST.imageSuffix);
												}
											}else if (severityCode.equals(CONST.yellow[0])) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+eventType+CONST.yellow[1]+CONST.imageSuffix);
												if (bitmap == null) {
													bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.yellow[1]+CONST.imageSuffix);
												}
											}else if (severityCode.equals(CONST.orange[0])) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+eventType+CONST.orange[1]+CONST.imageSuffix);
												if (bitmap == null) {
													bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.orange[1]+CONST.imageSuffix);
												}
											}else if (severityCode.equals(CONST.red[0])) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+eventType+CONST.red[1]+CONST.imageSuffix);
												if (bitmap == null) {
													bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.red[1]+CONST.imageSuffix);
												}
											}else if (severityCode.equals(CONST.white[0])) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+eventType+CONST.white[1]+CONST.imageSuffix);
												if (bitmap == null) {
													bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.white[1]+CONST.imageSuffix);
												}
											}
											if (bitmap == null) {
												bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.imageSuffix);
											}
											imageView.setImageBitmap(bitmap);

											initDBManager();
											scrollView.setVisibility(View.VISIBLE);
											refreshLayout.setRefreshing(false);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;

		default:
			break;
		}
	}

}
