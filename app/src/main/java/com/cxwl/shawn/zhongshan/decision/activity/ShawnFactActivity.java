package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.adapter.FactAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.FactCityAdapter;
import com.cxwl.shawn.zhongshan.decision.dto.FactDto;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 天气实况
 */
public class ShawnFactActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private TextView tvSwitch,tvFactTime,tvValue;
    private LinearLayout llTitle,llGridView,llContainer,llContainer1;
    private ImageView ivSwitch,ivValue,ivCity;
    private boolean isDesc = true;//是否为降序
    private String RAIN1 = "1h降水",RAIN3 = "3h降水",RAIN6 = "6h降水",RAIN12 = "12h降水",RAIN24 = "24h降水",
            TEMP1 = "1h温度",WINDJD1 = "1h极大风",WINDJD24 = "24h极大风",WINDZD1 = "1h最大风",WINDZD24 = "24h最大风";
    private String selectedColumnName = RAIN1;
    private String selectedCityName = "全省";
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private int width;
    private float density;
    private FactAdapter mAdapter;
    private List<FactDto> dataList = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;
    private String userAuthority = "-1";//用户权限，3为专业用户，其它为普通用户

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_fact);
        mContext = this;
        initWidget();
        initGridView();
        initListView();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        llContainer = findViewById(R.id.llContainer);
        llContainer1 = findViewById(R.id.llContainer1);
        tvFactTime = findViewById(R.id.tvFactTime);
        tvValue = findViewById(R.id.tvValue);
        LinearLayout llValue = findViewById(R.id.llValue);
        llValue.setOnClickListener(this);
        ivValue = findViewById(R.id.ivValue);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("天气实况");
        LinearLayout llSwitch = findViewById(R.id.llSwitch);
        llSwitch.setOnClickListener(this);
        tvSwitch = findViewById(R.id.tvSwitch);
        ivSwitch = findViewById(R.id.ivSwitch);
        llGridView = findViewById(R.id.llGridView);
        LinearLayout llCity = findViewById(R.id.llCity);
        llCity.setOnClickListener(this);
        ivCity = findViewById(R.id.ivCity);
        llTitle = findViewById(R.id.llTitle);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        density = dm.density;

        //判断是否为专业用户
        if (getIntent().hasExtra("userAuthority")) {
            userAuthority = getIntent().getStringExtra("userAuthority");
        }

        addColumns();
    }

    private void initGridView() {
        final List<FactDto> cityList = new ArrayList<>();
        String[] citys = getResources().getStringArray(R.array.fact_citys);
        for (int i = 0; i < citys.length; i++) {
            FactDto dto = new FactDto();
            if (i == 0) {
                dto.isSelected = true;
                selectedCityName = "全省";
            }else {
                dto.isSelected = false;
            }
            dto.city = citys[i];
            cityList.add(dto);
        }
        GridView gridView = findViewById(R.id.gridView);
        final FactCityAdapter cityAdapter = new FactCityAdapter(mContext, cityList);
        gridView.setAdapter(cityAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FactDto dto = cityList.get(position);
                dto.isSelected = true;
                selectedCityName = dto.city;
                for (int i = 0; i < cityList.size(); i++) {
                    if (i == position) {
                        cityList.get(i).isSelected = true;
                    }else {
                        cityList.get(i).isSelected = false;
                    }
                }
                cityAdapter.notifyDataSetChanged();
                tvSwitch.setText(dto.city);
                closeList();

                OkHttpList(selectedColumnName, selectedCityName);

            }
        });
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        mAdapter = new FactAdapter(mContext, dataList);
        listView.setAdapter(mAdapter);
    }

    /**
     * 添加各要素栏目
     */
    private void addColumns() {
        List<String> list = new ArrayList<>();
        list.add(RAIN1);
        list.add(RAIN3);
        list.add(RAIN6);
//        list.add(RAIN12);//暂时过滤掉12小时降水
        list.add(RAIN24);
        list.add(TEMP1);
        list.add(WINDJD1);
        list.add(WINDJD24);
        list.add(WINDZD1);
        list.add(WINDZD24);

        llContainer.removeAllViews();
        llContainer1.removeAllViews();
        int columnSize = list.size();
        for (int i = 0; i < columnSize; i++) {
            String columnName = list.get(i);
            TextView tvName = new TextView(mContext);
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            tvName.setPadding(0, (int)(density*5), 0, (int)(density*5));
            tvName.setText(columnName);
            tvName.setTag(columnName);
            if (i == 0) {
                selectedColumnName = columnName;
                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                OkHttpList(columnName, selectedCityName);
            }else {
                tvName.setTextColor(getResources().getColor(R.color.text_color3));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = width/5;
            tvName.setLayoutParams(params);
            llContainer.addView(tvName, i);

            TextView tvBar = new TextView(mContext);
            tvBar.setGravity(Gravity.CENTER);
            tvBar.setTag(columnName);
            if (i == 0) {
                tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.width = width/5-(int)(density*20);
            params1.height = (int) (density*2);
            params1.setMargins((int)(density*10), 0, (int)(density*10), 0);
            tvBar.setLayoutParams(params1);
            llContainer1.addView(tvBar, i);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < llContainer.getChildCount(); j++) {
                        TextView tvName = (TextView) llContainer.getChildAt(j);
                        String name = v.getTag()+"";
                        if (TextUtils.equals(tvName.getTag()+"", name)) {
                            selectedColumnName = name;
                            tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                            OkHttpList(name, selectedCityName);
                        }else {
                            tvName.setTextColor(getResources().getColor(R.color.text_color3));
                        }
                    }

                    for (int j = 0; j < llContainer1.getChildCount(); j++) {
                        TextView tvBar = (TextView) llContainer1.getChildAt(j);
                        String name = v.getTag()+"";
                        if (TextUtils.equals(tvBar.getTag()+"", name)) {
                            tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        }else {
                            tvBar.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
            });

        }
    }

    private String getDataUrl(String columnName) {
        String url = "";
        if (TextUtils.equals(columnName, RAIN1)) {//1小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (TextUtils.equals(columnName, RAIN3)) {//3小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (TextUtils.equals(columnName, RAIN6)) {//6小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (TextUtils.equals(columnName, RAIN12)) {//12小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (TextUtils.equals(columnName, RAIN24)) {//24小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (TextUtils.equals(columnName, TEMP1)) {//1小时温度
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zswd");
        }else if (TextUtils.equals(columnName, WINDJD1)) {//1小时极大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&mold=jd");
        }else if (TextUtils.equals(columnName, WINDJD24)) {//24小时极大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs24&mold=jd");
        }else if (TextUtils.equals(columnName, WINDZD1)) {//1小时最大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&mold=zd");
        }else if (TextUtils.equals(columnName, WINDZD24)) {//24小时最大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs24&mold=zd");
        }
        return url;
    }

    private void OkHttpList(final String columnName, final String cityName) {
        final String url = getDataUrl(columnName);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        OkHttpList(columnName, selectedCityName);
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
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            dataList.clear();
                                            JSONArray array = obj.getJSONArray("data");
                                            if (isDesc) {
                                                for (int i = 0; i < array.length(); i++) {
                                                    FactDto dto = new FactDto();
                                                    dto.columnName = columnName;
                                                    JSONObject itemObj = array.getJSONObject(i);

                                                    if (i == 0) {
                                                        String endTime = itemObj.getString("Datetime");
                                                        if (!TextUtils.isEmpty(endTime)) {
                                                            try {
                                                                String startTime1 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60);
                                                                String startTime3 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*3);
                                                                String startTime6 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*6);
                                                                String startTime12 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*12);
                                                                String startTime24 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*24);
                                                                String factTime = "";
                                                                if (TextUtils.equals(columnName, RAIN1)) {//1小时降水
                                                                    factTime = "广东省1小时降水实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                    tvValue.setText("降水mm");
                                                                }else if (TextUtils.equals(columnName, RAIN3)) {//3小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省3小时降水实况["+sdf1.format(sdf2.parse(startTime3))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN6)) {//6小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省6小时降水实况["+sdf1.format(sdf2.parse(startTime6))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN12)) {//12小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省12小时降水实况["+sdf1.format(sdf2.parse(startTime12))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN24)) {//24小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省24小时降水实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, TEMP1)) {//1小时温度
                                                                    tvValue.setText("温度℃");
                                                                    factTime = "广东省1小时温度实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDJD1)) {//1小时极大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省1小时极大风速实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDJD24)) {//24小时极大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省24小时极大风速实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDZD1)) {//1小时最大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省1小时最大风速实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDZD24)) {//24小时最大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省24小时最大风速实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }
                                                                tvFactTime.setText(factTime);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    if (!itemObj.isNull("Lat")) {
                                                        dto.lat = itemObj.getDouble("Lat");
                                                    }
                                                    if (!itemObj.isNull("Lon")) {
                                                        dto.lng = itemObj.getDouble("Lon");
                                                    }

                                                    if (TextUtils.equals(columnName, RAIN1)) {//1小时降水
                                                        if (!itemObj.isNull("JS")) {
                                                            dto.rain = itemObj.getDouble("JS");
                                                            if (dto.rain >= 9999) {
                                                                dto.rain = 0;
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(columnName, RAIN3)) {//3小时降水
                                                        if (!itemObj.isNull("JS3")) {
                                                            dto.rain = itemObj.getDouble("JS3");
                                                            if (dto.rain >= 9999) {
                                                                dto.rain = 0;
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(columnName, RAIN6)) {//6小时降水
                                                        if (!itemObj.isNull("JS6")) {
                                                            dto.rain = itemObj.getDouble("JS6");
                                                            if (dto.rain >= 9999) {
                                                                dto.rain = 0;
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(columnName, RAIN12)) {//12小时降水
                                                        if (!itemObj.isNull("JS12")) {
                                                            dto.rain = itemObj.getDouble("JS12");
                                                            if (dto.rain >= 9999) {
                                                                dto.rain = 0;
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(columnName, RAIN24)) {//24小时降水
                                                        if (!itemObj.isNull("JS24")) {
                                                            dto.rain = itemObj.getDouble("JS24");
                                                            if (dto.rain >= 9999) {
                                                                dto.rain = 0;
                                                            }
                                                        }
                                                    }

                                                    if (!itemObj.isNull("DPT")) {
                                                        dto.temp = itemObj.getDouble("DPT");
                                                        if (dto.temp >= 9999) {
                                                            dto.temp = 0;
                                                        }
                                                    }
                                                    if (!itemObj.isNull("FS")) {
                                                        dto.windS = itemObj.getDouble("FS");
                                                        if (dto.windS >= 9999) {
                                                            dto.windS = 0;
                                                        }
                                                    }
                                                    if (!itemObj.isNull("FX")) {
                                                        dto.windD = (float)itemObj.getDouble("FX");
                                                        if (dto.windD >= 9999) {
                                                            dto.windD = 0;
                                                        }
                                                    }
                                                    if (!itemObj.isNull("Prcode")) {
                                                        dto.pro = itemObj.getString("Prcode");
                                                    }
                                                    if (!itemObj.isNull("City")) {
                                                        dto.city = itemObj.getString("City");
                                                    }
                                                    if (!itemObj.isNull("Cnty")) {
                                                        dto.dis = itemObj.getString("Cnty");
                                                    }
                                                    if (!itemObj.isNull("Town")) {
                                                        dto.town = itemObj.getString("Town");
                                                    }
                                                    if (!itemObj.isNull("Village")) {
                                                        dto.vill = itemObj.getString("Village");
                                                    }
                                                    if (!itemObj.isNull("Station_Name")) {
                                                        dto.stationName = itemObj.getString("Station_Name");
                                                    }
                                                    if (!itemObj.isNull("Station_Id_C")) {
                                                        dto.stationId = itemObj.getString("Station_Id_C");
                                                    }
                                                    if (!itemObj.isNull("Prcode")) {
                                                        dto.pro = itemObj.getString("Prcode");
                                                    }

                                                    if (TextUtils.equals(cityName, "全省")) {
                                                        if (TextUtils.equals(userAuthority, "3")) {//专业用户
                                                            dataList.add(dto);
                                                        }else {
                                                            if (dto.stationId.startsWith("59")) {//普通用户只能看国家站
                                                                dataList.add(dto);
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(cityName, dto.city)) {
                                                        if (TextUtils.equals(userAuthority, "3")) {//专业用户
                                                            dataList.add(dto);
                                                        }else {
                                                            if (dto.stationId.startsWith("59")) {//普通用户只能看国家站
                                                                dataList.add(dto);
                                                            }
                                                        }
                                                    }

                                                }
                                            }else {
                                                for (int i = array.length()-1; i >= 0; i--) {
                                                    FactDto dto = new FactDto();
                                                    dto.columnName = columnName;
                                                    JSONObject itemObj = array.getJSONObject(i);

                                                    if (i == 0) {
                                                        String endTime = itemObj.getString("Datetime");
                                                        if (!TextUtils.isEmpty(endTime)) {
                                                            try {
                                                                String startTime1 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60);
                                                                String startTime3 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*3);
                                                                String startTime6 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*6);
                                                                String startTime12 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*12);
                                                                String startTime24 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*24);
                                                                String factTime = "";
                                                                if (TextUtils.equals(columnName, RAIN1)) {//1小时降水
                                                                    factTime = "广东省1小时降水实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                    tvValue.setText("降水mm");
                                                                }else if (TextUtils.equals(columnName, RAIN3)) {//3小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省3小时降水实况["+sdf1.format(sdf2.parse(startTime3))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN6)) {//6小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省6小时降水实况["+sdf1.format(sdf2.parse(startTime6))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN12)) {//12小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省12小时降水实况["+sdf1.format(sdf2.parse(startTime12))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, RAIN24)) {//24小时降水
                                                                    tvValue.setText("降水mm");
                                                                    factTime = "广东省24小时降水实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, TEMP1)) {//1小时温度
                                                                    tvValue.setText("温度℃");
                                                                    factTime = "广东省1小时温度实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDJD1)) {//1小时极大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省1小时极大风速实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDJD24)) {//24小时极大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省24小时极大风速实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDZD1)) {//1小时最大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省1小时最大风速实况["+sdf1.format(sdf2.parse(startTime1))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }else if (TextUtils.equals(columnName, WINDZD24)) {//24小时最大风
                                                                    tvValue.setText("风速m/s");
                                                                    factTime = "广东省24小时最大风速实况["+sdf1.format(sdf2.parse(startTime24))+" - "+sdf1.format(sdf2.parse(endTime))+"]";
                                                                }
                                                                tvFactTime.setText(factTime);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    if (!itemObj.isNull("Lat")) {
                                                        dto.lat = itemObj.getDouble("Lat");
                                                    }
                                                    if (!itemObj.isNull("Lon")) {
                                                        dto.lng = itemObj.getDouble("Lon");
                                                    }
                                                    if (!itemObj.isNull("JS")) {
                                                        dto.rain = itemObj.getDouble("JS");
                                                    }
                                                    if (!itemObj.isNull("DPT")) {
                                                        dto.temp = itemObj.getDouble("DPT");
                                                    }
                                                    if (!itemObj.isNull("FS")) {
                                                        dto.windS = itemObj.getDouble("FS");
                                                    }
                                                    if (!itemObj.isNull("FX")) {
                                                        dto.windD = (float)itemObj.getDouble("FX");
                                                    }
                                                    if (!itemObj.isNull("Prcode")) {
                                                        dto.pro = itemObj.getString("Prcode");
                                                    }
                                                    if (!itemObj.isNull("City")) {
                                                        dto.city = itemObj.getString("City");
                                                    }
                                                    if (!itemObj.isNull("Cnty")) {
                                                        dto.dis = itemObj.getString("Cnty");
                                                    }
                                                    if (!itemObj.isNull("Town")) {
                                                        dto.town = itemObj.getString("Town");
                                                    }
                                                    if (!itemObj.isNull("Village")) {
                                                        dto.vill = itemObj.getString("Village");
                                                    }
                                                    if (!itemObj.isNull("Station_Name")) {
                                                        dto.stationName = itemObj.getString("Station_Name");
                                                    }
                                                    if (!itemObj.isNull("Station_Id_C")) {
                                                        dto.stationId = itemObj.getString("Station_Id_C");
                                                    }
                                                    if (!itemObj.isNull("Prcode")) {
                                                        dto.pro = itemObj.getString("Prcode");
                                                    }

                                                    if (TextUtils.equals(cityName, "全省")) {
                                                        if (TextUtils.equals(userAuthority, "3")) {//专业用户
                                                            dataList.add(dto);
                                                        }else {
                                                            if (dto.stationId.startsWith("59")) {//普通用户只能看国家站
                                                                dataList.add(dto);
                                                            }
                                                        }
                                                    }else if (TextUtils.equals(cityName, dto.city)) {
                                                        if (TextUtils.equals(userAuthority, "3")) {//专业用户
                                                            dataList.add(dto);
                                                        }else {
                                                            if (dto.stationId.startsWith("59")) {//普通用户只能看国家站
                                                                dataList.add(dto);
                                                            }
                                                        }
                                                    }

                                                }
                                            }

                                            Log.e("size", dataList.size()+"");
                                            rankValueData();
                                            loadingView.setVisibility(View.GONE);
                                            llTitle.setVisibility(View.VISIBLE);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    OkHttpList(columnName, selectedCityName);
                                }

                            }
                        });

                    }
                });
            }
        }).start();
    }

    /**
     * @param flag false为显示map，true为显示list
     */
    private void startAnimation(boolean flag, final View view) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation animation;
        if (!flag) {
            animation = new AlphaAnimation(0.0f, 1.0f);
        }else {
            animation = new AlphaAnimation(1.0f, 0.0f);
        }
        animation.setDuration(300);
        animationSet.addAnimation(animation);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                view.clearAnimation();
            }
        });
    }

    private void openList() {
        startAnimation(false, llGridView);
        llGridView.setVisibility(View.VISIBLE);
        ivSwitch.setImageResource(R.drawable.shawn_icon_arrow_top);
    }

    private void closeList() {
        startAnimation(true, llGridView);
        llGridView.setVisibility(View.GONE);
        ivSwitch.setImageResource(R.drawable.shawn_icon_arrow_bottom);
    }

    /**
     * 数值排序
     */
    private void rankValueData() {
        if (isDesc) {
            ivValue.setImageResource(R.drawable.shawn_icon_sequnce_down);
            Collections.sort(dataList, new Comparator<FactDto>() {
                @Override
                public int compare(FactDto arg0, FactDto arg1) {
                    double value1 = 0, value2 = 0;
                    if (TextUtils.equals(arg0.columnName, RAIN1)) {//1小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN3)) {//3小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN6)) {//6小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN12)) {//12小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN24)) {//24小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, TEMP1)) {//1小时温度
                        value1 = arg0.temp;
                        value2 = arg1.temp;
                    }else if (TextUtils.equals(arg0.columnName, WINDJD1)) {//1小时极大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDJD24)) {//24小时极大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDZD1)) {//1小时最大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDZD24)) {//24小时最大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }
                    return Double.compare(value2, value1);
                }
            });
        }else {
            ivValue.setImageResource(R.drawable.shawn_icon_sequnce_up);
            Collections.sort(dataList, new Comparator<FactDto>() {
                @Override
                public int compare(FactDto arg0, FactDto arg1) {
                    double value1 = 0, value2 = 0;
                    if (TextUtils.equals(arg0.columnName, RAIN1)) {//1小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN3)) {//3小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN6)) {//6小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN12)) {//12小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, RAIN24)) {//24小时降水
                        value1 = arg0.rain;
                        value2 = arg1.rain;
                    }else if (TextUtils.equals(arg0.columnName, TEMP1)) {//1小时温度
                        value1 = arg0.temp;
                        value2 = arg1.temp;
                    }else if (TextUtils.equals(arg0.columnName, WINDJD1)) {//1小时极大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDJD24)) {//24小时极大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDZD1)) {//1小时最大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }else if (TextUtils.equals(arg0.columnName, WINDZD24)) {//24小时最大风
                        value1 = arg0.windS;
                        value2 = arg1.windS;
                    }
                    return Double.compare(value1, value2);
                }
            });
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 名称排序
     */
    private void rankNameData() {
        if (isDesc) {
            ivCity.setImageResource(R.drawable.shawn_icon_sequnce_down);
            Collections.sort(dataList, new Comparator<FactDto>() {
                @Override
                public int compare(FactDto arg0, FactDto arg1) {
                    if (TextUtils.isEmpty(arg0.city) || TextUtils.isEmpty(arg1.city)) {
                        return 0;
                    }else {
                        return getPinYinHeadChar(arg0.city).compareTo(getPinYinHeadChar(arg1.city));
                    }
                }
            });
        }else {
            ivCity.setImageResource(R.drawable.shawn_icon_sequnce_up);
            Collections.sort(dataList, new Comparator<FactDto>() {
                @Override
                public int compare(FactDto arg0, FactDto arg1) {
                    if (TextUtils.isEmpty(arg0.city) || TextUtils.isEmpty(arg1.city)) {
                        return -1;
                    }else {
                        return getPinYinHeadChar(arg1.city).compareTo(getPinYinHeadChar(arg0.city));
                    }
                }
            });
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 返回中文的首字母
      * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        int size = str.length();
        if (size >= 2) {
            size = 2;
        }
        for (int j = 0; j < size; j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.llBack) {
            finish();
        }else if (id == R.id.llSwitch) {
            if (llGridView.getVisibility() == View.GONE) {
                openList();
            }else {
                closeList();
            }
        }else if (id == R.id.llValue) {
            ivValue.setVisibility(View.VISIBLE);
            ivCity.setVisibility(View.GONE);
            isDesc = !isDesc;
            rankValueData();
        }else if (id == R.id.llCity) {
            ivValue.setVisibility(View.GONE);
            ivCity.setVisibility(View.VISIBLE);
            isDesc = !isDesc;
            rankNameData();
        }
    }

}
