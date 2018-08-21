package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.adapter.ForecastWeeklyAdapter;
import com.cxwl.shawn.zhongshan.decision.dto.WeatherDto;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;
import com.cxwl.shawn.zhongshan.decision.util.WeatherUtil;
import com.cxwl.shawn.zhongshan.decision.view.ScrollviewListview;
import com.cxwl.shawn.zhongshan.decision.view.WeeklyView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants.Language;
import cn.com.weather.listener.AsyncResponseHandler;

/**
 * 天气预报详情
 */
public class ShawnForecastActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private TextView tvTitle,tvCity,tvTime,tvTemp,tvForeTemp,tvPhe,tvHumidity,tvWind,tvQuality;
	private ImageView ivPhenomenon,ivSwitcher;//天气显现对应的图标
	private LinearLayout llContainer1,llContainer2;//加载逐小时预报曲线容器
	private ScrollviewListview mListView;//一周预报列表listview
	private ForecastWeeklyAdapter mAdapter;
	private ScrollView scrollView;//全屏
	private int width = 0;
	private List<WeatherDto> weeklyList = new ArrayList<>();
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.CHINA);
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_forecast);
		mContext = this;
		initWidget();
		initListView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = findViewById(R.id.tvTitle);
		//解决scrollView嵌套listview，动态计算listview高度后，自动滚动到屏幕底部
		tvCity = findViewById(R.id.tvCity);
		tvTime = findViewById(R.id.tvTime);
		tvTime.setFocusable(true);
		tvTime.setFocusableInTouchMode(true);
		tvTime.requestFocus();
		ivSwitcher = findViewById(R.id.ivSwitcher);
		ivSwitcher.setOnClickListener(this);
		tvTemp = findViewById(R.id.tvTemp);
		tvForeTemp = findViewById(R.id.tvForeTemp);
		ivPhenomenon = findViewById(R.id.ivPhenomenon);
		tvPhe = findViewById(R.id.tvPhe);
		tvQuality = findViewById(R.id.tvQuality);
		tvHumidity = findViewById(R.id.tvHumidity);
		tvWind = findViewById(R.id.tvWind);
		llContainer1 = findViewById(R.id.llContainer1);
		llContainer2 = findViewById(R.id.llContainer2);
		scrollView = findViewById(R.id.scrollView);
		loadingView = findViewById(R.id.loadingView);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		
		refresh();
	}
	
	private void refresh() {
		String areaName = getIntent().getStringExtra("cityName");
		if (!TextUtils.isEmpty(areaName)) {
			tvTitle.setText(areaName);
			tvCity.setText(areaName);
		}
		
		String cityId = getIntent().getStringExtra("cityId");
		if (!TextUtils.isEmpty(cityId)) {
			getWeatherInfo(cityId);
		}
	}

	/**
	 * 获取天气数据
	 */
	private void getWeatherInfo(final String cityId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WeatherAPI.getWeather2(mContext, cityId, Language.ZH_CN, new AsyncResponseHandler() {
					@Override
					public void onComplete(final Weather content) {
						super.onComplete(content);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String result = content.toString();
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);

										//实况信息
										if (!obj.isNull("l")) {
											JSONObject l = obj.getJSONObject("l");
											if (!l.isNull("l7")) {
												String time = l.getString("l7");
												if (time != null) {
													tvTime.setText(time+"发布");
												}
											}
											if (!l.isNull("l5")) {
												String weatherCode = WeatherUtil.lastValue(l.getString("l5"));
												int current = Integer.parseInt(sdf2.format(new Date()));
												if (current >= 5 && current < 18) {
													ivPhenomenon.setImageBitmap(WeatherUtil.getDayBitmap(mContext, Integer.valueOf(weatherCode)));
												}else {
													ivPhenomenon.setImageBitmap(WeatherUtil.getNightBitmap(mContext, Integer.valueOf(weatherCode)));
												}
												tvPhe.setText(getString(WeatherUtil.getWeatherId(Integer.valueOf(weatherCode))));
											}
											if (!l.isNull("l1")) {
												String factTemp = WeatherUtil.lastValue(l.getString("l1"));
												tvTemp.setText(factTemp);
											}
											if (!l.isNull("l2")) {
												String humidity = WeatherUtil.lastValue(l.getString("l2"));
												tvHumidity.setText("湿度"+" "+humidity + getString(R.string.unit_percent));
											}
											if (!l.isNull("l4")) {
												String windDir = WeatherUtil.lastValue(l.getString("l4"));
												if (!l.isNull("l3")) {
													String windForce = WeatherUtil.lastValue(l.getString("l3"));
													if (!TextUtils.isEmpty(windDir) && !TextUtils.isEmpty(windForce)) {
														tvWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir)))+
																" " + WeatherUtil.getFactWindForce(Integer.valueOf(windForce)));
													}
												}
											}
										}

										//空气质量
										if (!obj.isNull("k")) {
											JSONObject k = obj.getJSONObject("k");
											if (!k.isNull("k3")) {
												String num = WeatherUtil.lastValue(k.getString("k3"));
												if (!TextUtils.isEmpty(num)) {
													tvQuality.setText("空气质量" + " " +
															WeatherUtil.getAqi(mContext, Integer.valueOf(num)) + " " + num);
												}
											}
										}

										//逐小时预报信息
										if (!obj.isNull("jh")) {
											JSONArray jh = obj.getJSONArray("jh");
											llContainer1.removeAllViews();
											for (int i = 0; i < jh.length(); i++) {
												JSONObject itemObj = jh.getJSONObject(i);
												int hourlyCode = Integer.valueOf(itemObj.getString("ja"));
												int hourlyTemp = Integer.valueOf(itemObj.getString("jb"));
												String hourlyTime = itemObj.getString("jf");

												LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
												View view = inflater.inflate(R.layout.shawn_fore_hourly, null);
												TextView tvHour = view.findViewById(R.id.tvHour);
												ImageView ivPhe = view.findViewById(R.id.ivPhe);
												TextView tvPhe = view.findViewById(R.id.tvPhe);
												TextView tvTemp = view.findViewById(R.id.tvTemp);
												try {
													int current = Integer.parseInt(sdf2.format(sdf1.parse(hourlyTime)));
													tvHour.setText(current+"时");
													if (current >= 5 && current < 18) {
														ivPhe.setImageBitmap(WeatherUtil.getDayBitmap(mContext, hourlyCode));
													}else {
														ivPhe.setImageBitmap(WeatherUtil.getNightBitmap(mContext, hourlyCode));
													}
												} catch (ParseException e) {
													e.printStackTrace();
												}
												tvPhe.setText(getString(WeatherUtil.getWeatherId(hourlyCode)));
												tvTemp.setText(hourlyTemp+getString(R.string.unit_degree));
												llContainer1.addView(view);
											}
										}

										//15天预报
										if (!obj.isNull("f")) {
											weeklyList.clear();
											JSONObject f = obj.getJSONObject("f");
											String f0 = f.getString("f0");

											long foreDate = 0,currentDate = 0;
											try {
												String fTime = sdf3.format(sdf1.parse(f0));
												foreDate = sdf3.parse(fTime).getTime();
												currentDate = sdf3.parse(sdf3.format(new Date())).getTime();
											} catch (ParseException e) {
												e.printStackTrace();
											}

											if (!f.isNull("f1")) {
												JSONArray f1 = f.getJSONArray("f1");
												for (int i = 0; i < f1.length(); i++) {
													WeatherDto dto = new WeatherDto();

													dto.week = CommonUtil.getWeek(i);//星期几
													dto.date = CommonUtil.getDate(f0, i);//日期

													JSONObject weeklyObj = f1.getJSONObject(i);
													//晚上
													dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
													dto.lowPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fb"))));
													dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
													dto.lowWindDir = Integer.valueOf(weeklyObj.getString("ff"));
													dto.lowWindForce = Integer.valueOf(weeklyObj.getString("fh"));

													//白天
													dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
													dto.highPhe = getString(WeatherUtil.getWeatherId(Integer.valueOf(weeklyObj.getString("fa"))));
													dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
													dto.highWindDir = Integer.valueOf(weeklyObj.getString("fe"));
													dto.highWindForce = Integer.valueOf(weeklyObj.getString("fg"));

													weeklyList.add(dto);

													if (currentDate > foreDate) {
														if (i == 1) {
															tvForeTemp.setText(dto.highTemp+"℃"+"/"+dto.lowTemp+"℃");
														}
													}else {
														if (i == 0) {
															tvForeTemp.setText(dto.highTemp+"℃"+"/"+dto.lowTemp+"℃");
														}
													}
												}
											}

											//一周预报列表
											if (mAdapter != null) {
												mAdapter.foreDate = foreDate;
												mAdapter.currentDate = currentDate;
												mAdapter.notifyDataSetChanged();
											}

											//一周预报曲线
											WeeklyView weeklyView = new WeeklyView(mContext);
											weeklyView.setData(weeklyList, foreDate, currentDate);
											llContainer2.removeAllViews();
											llContainer2.addView(weeklyView, width*2, (int)(CommonUtil.dip2px(mContext, 400)));

										}

									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								scrollView.setVisibility(View.VISIBLE);
								loadingView.setVisibility(View.GONE);

							}
						});
					}

					@Override
					public void onError(Throwable error, String content) {
						super.onError(error, content);
					}
				});
			}
		}).start();
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		mListView = findViewById(R.id.listView);
		mAdapter = new ForecastWeeklyAdapter(mContext, weeklyList);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.llBack) {
			finish();

		} else if (i == R.id.ivSwitcher) {
			if (mListView.getVisibility() == View.VISIBLE) {
				ivSwitcher.setImageResource(R.drawable.shawn_iv_trend);
				mListView.setVisibility(View.GONE);
				llContainer2.setVisibility(View.VISIBLE);
			} else {
				ivSwitcher.setImageResource(R.drawable.shawn_iv_list);
				mListView.setVisibility(View.VISIBLE);
				llContainer2.setVisibility(View.GONE);
			}

		} else {
		}
	}

}