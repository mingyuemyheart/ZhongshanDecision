package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.common.CONST;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;
import com.cxwl.shawn.zhongshan.decision.manager.DBManager;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 预警详情
 */
public class ShawnWarningDetailActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private TextView tvGuide;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日HH时mm分", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_warning_detail);
		mContext = this;
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("预警详情");
		ImageView imageView = findViewById(R.id.imageView);
		TextView tvName = findViewById(R.id.tvName);
		TextView tvTime = findViewById(R.id.tvTime);
		TextView tvIntro = findViewById(R.id.tvIntro);
		tvGuide = findViewById(R.id.tvGuide);

		if (getIntent().hasExtra("data")) {
			WarningDto data = getIntent().getExtras().getParcelable("data");
			if (data != null) {
				if (data.name.contains("发布")) {
					String[] names = data.name.split("发布");
					try {
						tvName.setText(data.name.replace("发布", "发布\n"));
						tvTime.setText(sdf3.format(sdf1.parse(data.time)));
						tvIntro.setText(names[0]+"于"+sdf2.format(sdf1.parse(data.time))+"发布"+names[1]+"信号，请注意防御。");
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				String severityCode = data.color;
				String eventType = data.type;
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

				initDBManager(data);
			}
		}
	}
	
	/**
	 * 初始化数据库
	 */
	private void initDBManager(WarningDto data) {
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
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else {
		}
	}

}
