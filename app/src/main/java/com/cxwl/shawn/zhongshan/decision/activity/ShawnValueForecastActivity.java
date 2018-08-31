package com.cxwl.shawn.zhongshan.decision.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.adapter.ValueForecastAdapter;
import com.cxwl.shawn.zhongshan.decision.dto.ValueDto;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.cxwl.shawn.zhongshan.decision.view.PhotoView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 数值预报
 */
public class ShawnValueForecastActivity extends ShawnBaseActivity implements View.OnClickListener {

    private ImageView imageView,ivSwitch;
    private LinearLayout llGroup,llContainer;
    private TextView tvSwitch,tvEc,tvNc,tvTime,tvCount;
    private int width;
    private float density;
    private ValueForecastAdapter adapter;
    private List<ValueDto> dataList = new ArrayList<>();
    private List<String> ecElementList = new ArrayList<>();//存放标签
    private List<List<ValueDto>> ecDataList = new ArrayList<>();
    private List<String> ncElementList = new ArrayList<>();//存放标签
    private List<List<ValueDto>> ncDataList = new ArrayList<>();
    private boolean isEc = true;
    private int columnPosition = 0, gridviewPosition = 0;//对应标签下标、gridview下标
    private ViewPager mViewPager;
    private RelativeLayout reViewPager;
    private AVLoadingIndicatorView loadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_value_forecast);
        initWidget();
        initGridView();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("数值预报");
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        LinearLayout llSwitch = findViewById(R.id.llSwitch);
        llSwitch.setOnClickListener(this);
        llGroup = findViewById(R.id.llGroup);
        llContainer = findViewById(R.id.llContainer);
        tvTime = findViewById(R.id.tvTime);
        tvSwitch = findViewById(R.id.tvSwitch);
        ivSwitch = findViewById(R.id.ivSwitch);
        tvEc = findViewById(R.id.tvEc);
        tvEc.setOnClickListener(this);
        tvNc = findViewById(R.id.tvNc);
        tvNc.setOnClickListener(this);
        tvCount = findViewById(R.id.tvCount);
        reViewPager = findViewById(R.id.reViewPager);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        density = dm.density;

        OkHttpList();

    }

    private void initGridView() {
        GridView gridView = findViewById(R.id.gridView);
        adapter = new ValueForecastAdapter(this, dataList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gridviewPosition == position) {
                    return;
                }
                gridviewPosition = position;
                ValueDto dto = dataList.get(position);
                if (!TextUtils.isEmpty(dto.imgUrl)) {
                    Picasso.get().load(dto.imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(imageView);
                }else {
                    imageView.setImageResource(R.drawable.shawn_icon_seat_bitmap);
                }
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = width;
                params.height = width*3/4;
                imageView.setLayoutParams(params);

                for (int i = 0; i < dataList.size(); i++) {
                    ValueDto data = dataList.get(i);
                    if (i == position) {
                        data.isSelected = true;
                    }else {
                        data.isSelected = false;
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void addColumn() {
        llContainer.removeAllViews();
        List<String> elementList;
        final List<List<ValueDto>> dataList;
        if (isEc) {
            elementList = ecElementList;
            dataList = ecDataList;
        }else {
            elementList = ncElementList;
            dataList = ncDataList;
        }
        for (int i = 0; i < elementList.size(); i++) {
            String columnName = elementList.get(i);
            TextView tvName = new TextView(this);
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            tvName.setPadding((int)(density*10), (int)(density*8), (int)(density*10), (int)(density*8));
            tvName.setText(columnName);
            tvName.setTag(columnName);

            if (i == columnPosition) {
                tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                tvName.setTextColor(getResources().getColor(R.color.text_color3));
            }

            llContainer.addView(tvName);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < llContainer.getChildCount(); j++) {
                        TextView tvName = (TextView) llContainer.getChildAt(j);
                        if (TextUtils.equals(tvName.getTag()+"", v.getTag()+"")) {
                            tvName.setTextColor(getResources().getColor(R.color.colorPrimary));
                            //加载图片数据
                            columnPosition = j;
                            loadImages(dataList.get(j));
                        }else {
                            tvName.setTextColor(getResources().getColor(R.color.text_color3));
                        }
                    }
                }
            });

            //加载图片数据
            loadImages(dataList.get(columnPosition));
        }

    }

    /**
     * 加载图片数据
     * @param list
     */
    private void loadImages(List<ValueDto> list) {
        if (list != null && list.size() > 0) {
            dataList.clear();
            dataList.addAll(list);
            for (int i = 0; i < dataList.size(); i++) {
                ValueDto data = dataList.get(i);
                if (i == gridviewPosition) {
                    data.isSelected = true;
                }else {
                    data.isSelected = false;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (!TextUtils.isEmpty(dataList.get(gridviewPosition).imgUrl)) {
                Picasso.get().load(dataList.get(gridviewPosition).imgUrl).error(R.drawable.shawn_icon_seat_bitmap).into(imageView);
            }else {
                imageView.setImageResource(R.drawable.shawn_icon_seat_bitmap);
            }
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = width;
            params.height = width*3/4;
            imageView.setLayoutParams(params);

            initViewPager();

        }
    }

    /**
     * 获取数值预报
     */
    private void OkHttpList() {
        final String url = "https://api.bluepi.tianqi.cn/outdata/zhongshan/zhongShanElementPic";
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

                                        String startTime = "",endTime = "";
                                        if (!obj.isNull("startTime")) {
                                            startTime = obj.getString("startTime");
                                        }
                                        if (!obj.isNull("endTime")) {
                                            endTime = obj.getString("endTime");
                                        }
                                        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                                            tvTime.setText("起报："+startTime+"    "+"预报："+endTime);
                                        }

                                        if (!obj.isNull("data")) {
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {

                                                JSONObject dataObj1 = array.getJSONObject(i);
                                                if (!dataObj1.isNull("element")) {
                                                    String element = dataObj1.getString("element");
                                                    if (!dataObj1.isNull("data")) {
                                                        JSONObject dataObj2 = dataObj1.getJSONObject("data");

                                                        if (!dataObj2.isNull("ecmwffine")) {
                                                            JSONObject ecmwffine = dataObj2.getJSONObject("ecmwffine");
                                                            if (!ecmwffine.isNull("hn")) {
                                                                List<ValueDto> list = new ArrayList<>();
                                                                JSONArray hn = ecmwffine.getJSONArray("hn");
                                                                for (int j = 0; j < hn.length(); j++) {
                                                                    JSONObject itemObj = hn.getJSONObject(j);
                                                                    ValueDto dto = new ValueDto();
                                                                    if (!itemObj.isNull("url")) {
                                                                        dto.imgUrl = itemObj.getString("url");
                                                                    }
                                                                    if (!itemObj.isNull("time")) {
                                                                        dto.time = itemObj.getString("time");
                                                                    }
                                                                    list.add(dto);
                                                                }
                                                                ecDataList.add(list);
                                                                ecElementList.add(element+"(华南)");
                                                            }
                                                            if (!ecmwffine.isNull("cn")) {
                                                                List<ValueDto> list = new ArrayList<>();
                                                                JSONArray cn = ecmwffine.getJSONArray("cn");
                                                                for (int j = 0; j < cn.length(); j++) {
                                                                    JSONObject itemObj = cn.getJSONObject(j);
                                                                    ValueDto dto = new ValueDto();
                                                                    if (!itemObj.isNull("url")) {
                                                                        dto.imgUrl = itemObj.getString("url");
                                                                    }
                                                                    if (!itemObj.isNull("time")) {
                                                                        dto.time = itemObj.getString("time");
                                                                    }
                                                                    list.add(dto);
                                                                }
                                                                ecDataList.add(list);
                                                                ecElementList.add(element+"(全国)");
                                                            }
                                                        }

                                                        if (!dataObj2.isNull("ncepgfs0p5")) {
                                                            JSONObject ncepgfs0p5 = dataObj2.getJSONObject("ncepgfs0p5");
                                                            if (!ncepgfs0p5.isNull("hn")) {
                                                                List<ValueDto> list = new ArrayList<>();
                                                                JSONArray hn = ncepgfs0p5.getJSONArray("hn");
                                                                for (int j = 0; j < hn.length(); j++) {
                                                                    JSONObject itemObj = hn.getJSONObject(j);
                                                                    ValueDto dto = new ValueDto();
                                                                    if (!itemObj.isNull("url")) {
                                                                        dto.imgUrl = itemObj.getString("url");
                                                                    }
                                                                    if (!itemObj.isNull("time")) {
                                                                        dto.time = itemObj.getString("time");
                                                                    }
                                                                    list.add(dto);
                                                                }
                                                                ncDataList.add(list);
                                                                ncElementList.add(element+"(华南)");
                                                            }
                                                            if (!ncepgfs0p5.isNull("cn")) {
                                                                List<ValueDto> list = new ArrayList<>();
                                                                JSONArray cn = ncepgfs0p5.getJSONArray("cn");
                                                                for (int j = 0; j < cn.length(); j++) {
                                                                    JSONObject itemObj = cn.getJSONObject(j);
                                                                    ValueDto dto = new ValueDto();
                                                                    if (!itemObj.isNull("url")) {
                                                                        dto.imgUrl = itemObj.getString("url");
                                                                    }
                                                                    if (!itemObj.isNull("time")) {
                                                                        dto.time = itemObj.getString("time");
                                                                    }
                                                                    list.add(dto);
                                                                }
                                                                ncDataList.add(list);
                                                                ncElementList.add(element+"(全国)");
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                        addColumn();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        ImageView[] imageArray = new ImageView[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            ValueDto dto = dataList.get(i);
            if (!TextUtils.isEmpty(dto.imgUrl)) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(dto.imgUrl).into(imageView);
                imageArray[i] = imageView;
            }
        }

        mViewPager = findViewById(R.id.viewPager);
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(imageArray);
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                tvCount.setText((arg0+1)+"/"+dataList.size());
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class MyViewPagerAdapter extends PagerAdapter {

        private ImageView[] mImageViews;

        private MyViewPagerAdapter(ImageView[] imageViews) {
            this.mImageViews = imageViews;
        }

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Drawable drawable = mImageViews[position].getDrawable();
            photoView.setImageDrawable(drawable);
            container.addView(photoView, 0);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    scaleColloseAnimation(reViewPager);
                    reViewPager.setVisibility(View.GONE);
                }
            });
            return photoView;
        }

    }

    /**
     * 放大动画
     * @param view
     */
    private void scaleExpandAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1.0f,0,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setDuration(300);
        animationSet.addAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);

        view.startAnimation(animationSet);
    }

    /**
     * 缩小动画
     * @param view
     */
    private void scaleColloseAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setDuration(300);
        animationSet.addAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);

        view.startAnimation(animationSet);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (reViewPager.getVisibility() == View.VISIBLE) {
            scaleColloseAnimation(reViewPager);
            reViewPager.setVisibility(View.GONE);
            return false;
        }else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.llBack) {
            finish();
        }else if (id == R.id.llSwitch) {
            if (llGroup.getVisibility() == View.VISIBLE) {
                llGroup.setVisibility(View.GONE);
                ivSwitch.setImageResource(R.drawable.shawn_icon_arrow_bottom);
            }else {
                llGroup.setVisibility(View.VISIBLE);
                ivSwitch.setImageResource(R.drawable.shawn_icon_arrow_top);
            }
        }else if (id == R.id.tvEc) {
            if (isEc) {
                return;
            }
            isEc = true;
            tvEc.setTextColor(getResources().getColor(R.color.white));
            tvNc.setTextColor(getResources().getColor(R.color.text_color3));
            tvEc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvNc.setBackgroundColor(getResources().getColor(R.color.white));
            tvSwitch.setText(tvEc.getText().toString());
            llGroup.setVisibility(View.GONE);
            addColumn();
        }else if (id == R.id.tvNc) {
            if (!isEc) {
                return;
            }
            isEc = false;
            tvEc.setTextColor(getResources().getColor(R.color.text_color3));
            tvNc.setTextColor(getResources().getColor(R.color.white));
            tvEc.setBackgroundColor(getResources().getColor(R.color.white));
            tvNc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvSwitch.setText(tvNc.getText().toString());
            llGroup.setVisibility(View.GONE);
            addColumn();
        }else if (id == R.id.imageView) {
            if (reViewPager.getVisibility() == View.GONE) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(gridviewPosition);
                }
                scaleExpandAnimation(reViewPager);
                reViewPager.setVisibility(View.VISIBLE);
                tvCount.setText((gridviewPosition+1)+"/"+dataList.size());
            }
        }
    }

}
