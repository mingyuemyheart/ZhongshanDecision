package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.cxwl.shawn.zhongshan.decision.R;

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
				startActivity(new Intent(mContext, ShawnMainActivity.class));
				finish();
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
	
}
