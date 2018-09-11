package com.cxwl.shawn.zhongshan.decision.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.adapter.FactAdapter;
import com.cxwl.shawn.zhongshan.decision.dto.FactDto;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private TextView tvFactTime,tvValue;
    private LinearLayout llContainer,llContainer1;
    private ImageView ivValue;
    private boolean isDesc = true;//是否为降序
    private String RAIN1 = "1h降水",RAIN3 = "3h降水",RAIN6 = "6h降水",RAIN12 = "12h降水",RAIN24 = "24h降水",
            TEMP1 = "1h温度",WINDJD1 = "1h极大风",WINDJD24 = "24h极大风",WINDZD1 = "1h最大风",WINDZD24 = "24h最大风";
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH时", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
    private int width;
    private float density;
    private FactAdapter mAdapter;
    private List<FactDto> dataList = new ArrayList<>();
    private List<FactDto> rain1s = new ArrayList<>();
    private List<FactDto> rain3s = new ArrayList<>();
    private List<FactDto> rain6s = new ArrayList<>();
    private List<FactDto> rain12s = new ArrayList<>();
    private List<FactDto> rain24s = new ArrayList<>();
    private List<FactDto> temp1s = new ArrayList<>();
    private List<FactDto> windjd1s = new ArrayList<>();
    private List<FactDto> windjd24s = new ArrayList<>();
    private List<FactDto> windzd1s = new ArrayList<>();
    private List<FactDto> windzd24s = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_fact);
        mContext = this;
        initWidget();
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

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        density = dm.density;

        addColumns();
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
        list.add(RAIN12);
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
            String name = list.get(i);
            TextView tvName = new TextView(mContext);
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            tvName.setPadding(0, (int)(density*5), 0, (int)(density*5));
            tvName.setText(name);
            tvName.setTag(name);
            if (i == 0) {
                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvName.setTextColor(getResources().getColor(R.color.text_color3));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = width/5;
            tvName.setLayoutParams(params);
            llContainer.addView(tvName, i);

            TextView tvBar = new TextView(mContext);
            tvBar.setGravity(Gravity.CENTER);
            tvBar.setTag(name);
            if (i == 0) {
                tvBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                OkHttpList(name);
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
                            tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                            OkHttpList(name);
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

    private String getDataUrl(String name) {
        String url = "";
        try {
            String endTime = sdf2.format(new Date());
            String startTime1 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60);
            String startTime3 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*3);
            String startTime6 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*6);
            String startTime12 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*12);
            String startTime24 = sdf2.format(sdf2.parse(endTime).getTime()-1000*60*60*24);
            String factTime = "";
            if (TextUtils.equals(name, RAIN1)) {//1小时降水
                factTime = "广东省1小时降水实况["+sdf1.format(sdf2.parse(startTime1))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                tvValue.setText("降水mm");
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s&statime=%s&endtime=%s", "js", startTime1, endTime);
            }else if (TextUtils.equals(name, RAIN3)) {//3小时降水
                tvValue.setText("降水mm");
                factTime = "广东省3小时降水实况["+sdf1.format(sdf2.parse(startTime3))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s&statime=%s&endtime=%s", "js3", startTime3, endTime);
            }else if (TextUtils.equals(name, RAIN6)) {//6小时降水
                tvValue.setText("降水mm");
                factTime = "广东省6小时降水实况["+sdf1.format(sdf2.parse(startTime6))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s&statime=%s&endtime=%s", "js6", startTime6, endTime);
            }else if (TextUtils.equals(name, RAIN12)) {//12小时降水
                tvValue.setText("降水mm");
                factTime = "广东省12小时降水实况["+sdf1.format(sdf2.parse(startTime12))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s&statime=%s&endtime=%s", "js12", startTime12, endTime);
            }else if (TextUtils.equals(name, RAIN24)) {//24小时降水
                tvValue.setText("降水mm");
                factTime = "广东省24小时降水实况["+sdf1.format(sdf2.parse(startTime24))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s&statime=%s&endtime=%s", "js24", startTime24, endTime);
            }else if (TextUtils.equals(name, TEMP1)) {//1小时温度
                tvValue.setText("温度℃");
                factTime = "广东省1小时温度实况["+sdf1.format(sdf2.parse(startTime1))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zswd?statime=%s&endtime=%s", startTime1, endTime);
            }else if (TextUtils.equals(name, WINDJD1)) {//1小时极大风
                tvValue.setText("风速m/s");
                factTime = "广东省1小时极大风速实况["+sdf1.format(sdf2.parse(startTime1))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&statime=%s&endtime=%s&mold=jd", startTime1, endTime);
            }else if (TextUtils.equals(name, WINDJD24)) {//24小时极大风
                tvValue.setText("风速m/s");
                factTime = "广东省24小时极大风速实况["+sdf1.format(sdf2.parse(startTime24))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&statime=%s&endtime=%s&mold=jd", startTime24, endTime);
            }else if (TextUtils.equals(name, WINDZD1)) {//1小时最大风
                tvValue.setText("风速m/s");
                factTime = "广东省1小时最大风速实况["+sdf1.format(sdf2.parse(startTime1))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&statime=%s&endtime=%s&mold=zd", startTime1, endTime);
            }else if (TextUtils.equals(name, WINDZD24)) {//24小时最大风
                tvValue.setText("风速m/s");
                factTime = "广东省24小时最大风速实况["+sdf1.format(sdf2.parse(startTime24))+"-"+sdf1.format(sdf2.parse(endTime))+"]";
                url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&statime=%s&endtime=%s&mold=zd", startTime24, endTime);
            }
            tvFactTime.setText(factTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return url;
    }

    private void OkHttpList(final String name) {
        dataList.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        final String url = getDataUrl(name);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        //判断如果已经加载过就不重新加载了
        if (TextUtils.equals(name, RAIN1)) {//1小时降水
            if (rain1s.size() > 0) {
                dataList.clear();
                dataList.addAll(rain1s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, RAIN3)) {//3小时降水
            if (rain3s.size() > 0) {
                dataList.clear();
                dataList.addAll(rain3s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, RAIN6)) {//6小时降水
            if (rain6s.size() > 0) {
                dataList.clear();
                dataList.addAll(rain6s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, RAIN12)) {//12小时降水
            if (rain12s.size() > 0) {
                dataList.clear();
                dataList.addAll(rain12s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, RAIN24)) {//24小时降水
            if (rain24s.size() > 0) {
                dataList.clear();
                dataList.addAll(rain24s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, TEMP1)) {//1小时温度
            if (temp1s.size() > 0) {
                dataList.clear();
                dataList.addAll(temp1s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, WINDJD1)) {//1小时极大风
            if (windjd1s.size() > 0) {
                dataList.clear();
                dataList.addAll(windjd1s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, WINDJD24)) {//24小时极大风
            if (windjd24s.size() > 0) {
                dataList.clear();
                dataList.addAll(windjd24s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, WINDZD1)) {//1小时最大风
            if (windzd1s.size() > 0) {
                dataList.clear();
                dataList.addAll(windzd1s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }else if (TextUtils.equals(name, WINDZD24)) {//24小时最大风
            if (windzd24s.size() > 0) {
                dataList.clear();
                dataList.addAll(windzd24s);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                loadingView.setVisibility(View.GONE);
                return;
            }
        }
        //判断如果已经加载过就不重新加载了

        loadingView.setVisibility(View.VISIBLE);
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
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            dataList.clear();
                                            rain1s.clear();
                                            rain3s.clear();
                                            rain6s.clear();
                                            rain12s.clear();
                                            rain24s.clear();
                                            temp1s.clear();
                                            windjd1s.clear();
                                            windjd24s.clear();
                                            windzd1s.clear();
                                            windzd24s.clear();
                                            JSONArray array = obj.getJSONArray("data");
                                            if (isDesc) {
                                                for (int i = 0; i < array.length(); i++) {
                                                    FactDto dto = new FactDto();
                                                    dto.columnName = name;
                                                    JSONObject itemObj = array.getJSONObject(i);
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
                                                    dataList.add(dto);

                                                    if (TextUtils.equals(name, RAIN1)) {//1小时降水
                                                        rain1s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN3)) {//3小时降水
                                                        rain3s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN6)) {//6小时降水
                                                        rain6s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN12)) {//12小时降水
                                                        rain12s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN24)) {//24小时降水
                                                        rain24s.add(dto);
                                                    }else if (TextUtils.equals(name, TEMP1)) {//1小时温度
                                                        temp1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDJD1)) {//1小时极大风
                                                        windjd1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDJD24)) {//24小时极大风
                                                        windjd24s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDZD1)) {//1小时最大风
                                                        windzd1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDZD24)) {//24小时最大风
                                                        windzd24s.add(dto);
                                                    }

                                                }
                                            }else {
                                                for (int i = array.length()-1; i >= 0; i--) {
                                                    FactDto dto = new FactDto();
                                                    dto.columnName = name;
                                                    JSONObject itemObj = array.getJSONObject(i);
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
                                                    dataList.add(dto);

                                                    if (TextUtils.equals(name, RAIN1)) {//1小时降水
                                                        rain1s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN3)) {//3小时降水
                                                        rain3s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN6)) {//6小时降水
                                                        rain6s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN12)) {//12小时降水
                                                        rain12s.add(dto);
                                                    }else if (TextUtils.equals(name, RAIN24)) {//24小时降水
                                                        rain24s.add(dto);
                                                    }else if (TextUtils.equals(name, TEMP1)) {//1小时温度
                                                        temp1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDJD1)) {//1小时极大风
                                                        windjd1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDJD24)) {//24小时极大风
                                                        windjd24s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDZD1)) {//1小时最大风
                                                        windzd1s.add(dto);
                                                    }else if (TextUtils.equals(name, WINDZD24)) {//24小时最大风
                                                        windzd24s.add(dto);
                                                    }

                                                }
                                            }

                                            if (mAdapter != null) {
                                                mAdapter.notifyDataSetChanged();
                                            }

                                            loadingView.setVisibility(View.GONE);

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
        int id = v.getId();
        if (id == R.id.llBack) {
            finish();
        }else if (id == R.id.llValue) {
            isDesc = !isDesc;
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
    }

}
