package com.cxwl.shawn.zhongshan.decision;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonNameAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonStartAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonYearAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WarningAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WarningStatisticAdapter;
import com.cxwl.shawn.zhongshan.decision.common.CONST;
import com.cxwl.shawn.zhongshan.decision.dto.MinuteFallDto;
import com.cxwl.shawn.zhongshan.decision.dto.TyphoonDto;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;
import com.cxwl.shawn.zhongshan.decision.dto.WeatherDto;
import com.cxwl.shawn.zhongshan.decision.dto.WindData;
import com.cxwl.shawn.zhongshan.decision.dto.WindDto;
import com.cxwl.shawn.zhongshan.decision.manager.CaiyunManager;
import com.cxwl.shawn.zhongshan.decision.util.AuthorityUtil;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.cxwl.shawn.zhongshan.decision.util.SecretUrlUtil;
import com.cxwl.shawn.zhongshan.decision.view.MinuteFallView;
import com.cxwl.shawn.zhongshan.decision.view.WaitWindView2;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
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

public class ShawnMainActivity extends ShawnBaseActivity implements View.OnClickListener, AMapLocationListener, AMap.OnMarkerClickListener,
        AMap.OnMapClickListener, AMap.InfoWindowAdapter, GeocodeSearch.OnGeocodeSearchListener, AMap.OnCameraChangeListener {

    private Context mContext;
    private AVLoadingIndicatorView loadingView;
    private ScrollView scrollView;
    private boolean scaleAnimation = true;
    private TextureMapView mapView;
    private AMap aMap;//高德地图
    private Bundle savedInstanceState;
    private LinearLayout llBack,llMenu,llTyphoon,llFact,llSatelite,llRadar,llWarning,llFore,llMinute,llWind,llValue;
    private ImageView ivMenu,ivTyphoon,ivFact,ivSatelite,ivRadar,ivWarning,ivFore,ivMinute,ivWind,ivValue;
    private TextView tvTyphoon,tvFact,tvSatelite,tvRadar,tvWarning,tvFore,tvMinute,tvWind,tvValue;
    private TextView tvTitle,tvTyphoonName;
    private AMapLocationClientOption mLocationOption;//声明mLocationOption对象
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private LatLng locationLatLng;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf5 = new SimpleDateFormat("MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private String TYPE_TYPHOON = "type_typhoon", TYPE_WARNING= "type_warning";
    private int width, height;

    //台风
    private boolean isShowTyphoon = true;//是否显示台风layout
    private ListView startListView;
    private TyphoonStartAdapter startAdapter;
    private List<TyphoonDto> startList = new ArrayList<>();//某一个活跃台风
    private ListView yearListView;
    private TyphoonYearAdapter yearAdapter;
    private List<TyphoonDto> yearList = new ArrayList<>();
    private ListView nameListView;
    private TyphoonNameAdapter nameAdapter;
    private List<TyphoonDto> nameList = new ArrayList<>();//某一年所有台风
    private RoadThread mRoadThread;//绘制台风点的线程

    private List<Polyline> fullLines = new ArrayList<>();//实线数据
    private List<Polyline> dashLines = new ArrayList<>();//虚线数据
    private List<Marker> markerPoints = new ArrayList<>();//台风点数据
    private List<Marker> factTimeMarkers = new ArrayList<>();//最后一个实况点时间markers
    private List<Marker> foreTimeMarkers = new ArrayList<>();//预报点时间markers
    private List<Marker> rotateMarkers = new ArrayList<>();//台风旋转markers
    private List<Polyline> rangeLines = new ArrayList<>();//测距虚线数据
    private List<Marker> rangeMarkers = new ArrayList<>();//测距中点距离marker
    private List<TyphoonDto> lastFactLatLngList = new ArrayList<>();//最后一个实况点数据集合

    private Circle circle7, circle10;//七级风圈和十级风圈
    private boolean isShowInfoWindow = false;//是否显示气泡
    private boolean isShowTime = false;//是否显示台风实况、预报时间
    private boolean isRanging = false;//是否允许测距
    private Marker locationMarker,clickMarker;
    private final int DRAW_TYPHOON_COMPLETE = 1002;//一个台风绘制结束
    private Circle circle100, circle300, circle500;//定位点对一个的区域圈
    private Text text100, text300, text500;//定位点对一个的区域圈文字

    private ImageView ivList,ivRange,ivTyphoonClose,ivLegend,ivLegendClose;
    private TextView tvCurrent,tvHistory;
    private RelativeLayout lyoutTyphoon,reTyphoonList,reLegend;

    //卫星拼图
    private boolean isShowCloud = false;//是否显示卫星拼图
    private Bitmap cloudBitmap;
    private GroundOverlay cloudOverlay;

    //雷达拼图
    private boolean isShowRadar = false;//是否显示雷达拼图
    private List<MinuteFallDto> caiyunList = new ArrayList<>();
    private GroundOverlay radarOverlay;
    private CaiyunManager caiyunManager;
    private CaiyunThread caiyunThread;
    private static final int HANDLER_SHOW_RADAR = 1;
    private static final int HANDLER_PROGRESS = 2;
    private static final int HANDLER_LOAD_FINISHED = 3;
    private static final int HANDLER_PAUSE = 4;

    //预警
    private boolean isShowWarning = false;//是否显示预警layout
    private boolean isShowWarningList = false;//是否显示预警统计列表
    private List<WarningDto> warningList = new ArrayList<>();
    private List<Marker> warningMarkers = new ArrayList<>();
    private LinearLayout llWarningPrompt;
    private TextView tvWarningPrompt,tvWaringList;
    private ImageView ivWarningArrow;
    private ListView warningListView;
    private WarningStatisticAdapter warningAdapter;
    private List<WarningDto> warningStatistics = new ArrayList<>();//统计
    private RelativeLayout layoutWarning;

    //分钟降水
    private boolean isShowMinute = false;//是否显示分钟降水layout
    private boolean isShowMinutePrompt = false;//是否显示分钟降水曲线图动画
    private RelativeLayout layoutMinute;
    private LinearLayout llMinutePrompt,llContainerMinute;//分钟降水曲线图
    private TextView tvAddr,tvRain;//地址
    private GeocodeSearch geocoderSearch;
    private ImageView ivMinuteArrow;

    //风场
    private boolean isShowWind = false;//是否显示风场layout
    private RelativeLayout layoutWind;
    private WaitWindView2 waitWindView;
    private boolean isGfs = true;//默认为GFS
    private WindData windDataGFS,windDataT639;
    private RelativeLayout windContainer1;
    public RelativeLayout windContainer2;
    private TextView tvWindTime;//风场数据时间
    private String dataHeight = "1000";//200、500、1000pha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.shawn_activity_main);
        mContext = this;
        checkAuthority();
    }

    private void initAll() {
        initAmap(savedInstanceState);
        initWidget();

        //台风
        initYearListView();
        initNameListView();
        initCurrentListView();

        //预警
        initWarningListView();
    }

    /**
     * 初始化高德地图
     */
    private void initAmap(Bundle bundle) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(bundle);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.926628, 105.178100), 4.0f));
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                drawWarningLines();
                startLocation();
            }
        });
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();//初始化定位参数
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
            mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        }
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(mContext);//初始化定位
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        }
        mLocationClient.startLocation();//启动定位
        mLocationClient.setLocationListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            locationLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            addLocationMarker();
        }
    }

    private void addLocationMarker() {
        if (locationLatLng == null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(locationLatLng);
        options.anchor(0.5f, 1.0f);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.iv_map_click_map),
                (int)(CommonUtil.dip2px(mContext, 21)), (int)(CommonUtil.dip2px(mContext, 32)));
        if (bitmap != null) {
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }else {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_map_click_map));
        }
        if (locationMarker != null) {
            locationMarker.remove();
        }
        locationMarker = aMap.addMarker(options);
        locationMarker.setClickable(false);
    }

    private void initWidget() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        loadingView = findViewById(R.id.loadingView);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);
        llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.app_name));
        llMenu = findViewById(R.id.llMenu);
        llMenu.setOnClickListener(this);
        ivMenu = findViewById(R.id.ivMenu);
        llTyphoon = findViewById(R.id.llTyphoon);
        llTyphoon.setOnClickListener(this);
        llFact = findViewById(R.id.llFact);
        llFact.setOnClickListener(this);
        llSatelite = findViewById(R.id.llSatelite);
        llSatelite.setOnClickListener(this);
        llRadar = findViewById(R.id.llRadar);
        llRadar.setOnClickListener(this);
        llWarning = findViewById(R.id.llWarning);
        llWarning.setOnClickListener(this);
        llFore = findViewById(R.id.llFore);
        llFore.setOnClickListener(this);
        llMinute = findViewById(R.id.llMinute);
        llMinute.setOnClickListener(this);
        llWind = findViewById(R.id.llWind);
        llWind.setOnClickListener(this);
        llValue = findViewById(R.id.llValue);
        llValue.setOnClickListener(this);
        ivTyphoon = findViewById(R.id.ivTyphoon);
        ivFact = findViewById(R.id.ivFact);
        ivSatelite = findViewById(R.id.ivSatelite);
        ivRadar = findViewById(R.id.ivRadar);
        ivWarning = findViewById(R.id.ivWarning);
        ivFore = findViewById(R.id.ivFore);
        ivMinute = findViewById(R.id.ivMinute);
        ivWind = findViewById(R.id.ivWind);
        ivValue = findViewById(R.id.ivValue);
        tvTyphoon = findViewById(R.id.tvTyphoon);
        tvFact = findViewById(R.id.tvFact);
        tvSatelite = findViewById(R.id.tvSatelite);
        tvRadar = findViewById(R.id.tvRadar);
        tvWarning = findViewById(R.id.tvWarning);
        tvFore = findViewById(R.id.tvFore);
        tvMinute = findViewById(R.id.tvMinute);
        tvWind = findViewById(R.id.tvWind);
        tvValue = findViewById(R.id.tvValue);

        //台风
        tvTyphoonName = findViewById(R.id.tvTyphoonName);
        ivRange = findViewById(R.id.ivRange);
        ivRange.setOnClickListener(this);
        tvCurrent = findViewById(R.id.tvCurrent);
        tvCurrent.setOnClickListener(this);
        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setOnClickListener(this);
        ivTyphoonClose = findViewById(R.id.ivTyphoonClose);
        ivTyphoonClose.setOnClickListener(this);
        reTyphoonList = findViewById(R.id.reTyphoonList);
        ivList = findViewById(R.id.ivList);
        ivList.setOnClickListener(this);
        ivLegendClose = findViewById(R.id.ivLegendClose);
        ivLegendClose.setOnClickListener(this);
        reLegend = findViewById(R.id.reLegend);
        ivLegend = findViewById(R.id.ivLegend);
        ivLegend.setOnClickListener(this);
        lyoutTyphoon = findViewById(R.id.lyoutTyphoon);

        //雷达拼图
        caiyunManager = new CaiyunManager(mContext);

        //预警
        llWarningPrompt = findViewById(R.id.llWarningPrompt);
        llWarningPrompt.setOnClickListener(this);
        tvWarningPrompt = findViewById(R.id.tvWarningPrompt);
        tvWaringList = findViewById(R.id.tvWaringList);
        tvWaringList.setOnClickListener(this);
        ivWarningArrow = findViewById(R.id.ivWarningArrow);
        ivWarningArrow.setOnClickListener(this);
        layoutWarning = findViewById(R.id.layoutWarning);

        //分钟降水
        layoutMinute = findViewById(R.id.layoutMinute);
        llMinutePrompt = findViewById(R.id.llMinutePrompt);
        llMinutePrompt.setOnClickListener(this);
        llContainerMinute = findViewById(R.id.llContainerMinute);
        tvAddr = findViewById(R.id.tvAddr);
        tvRain = findViewById(R.id.tvRain);
        ivMinuteArrow = findViewById(R.id.ivMinuteArrow);
        ivMinuteArrow.setOnClickListener(this);

        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);

        //风场
        layoutWind = findViewById(R.id.layoutWind);
        windContainer1 = findViewById(R.id.windContainer1);
        windContainer2 = findViewById(R.id.windContainer2);
        tvWindTime = findViewById(R.id.tvWindTime);
    }

    /**
     * 缩小动画
     */
    private void narrowAnimation() {
        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        trans.setDuration(200);
        trans.setFillAfter(true);
        scrollView.startAnimation(trans);

        RotateAnimation rotate = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        ivMenu.startAnimation(rotate);
    }

    /**
     * 放大动画
     */
    private void enlargeAnimation() {
        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        trans.setDuration(200);
        trans.setFillAfter(true);
        scrollView.startAnimation(trans);

        RotateAnimation rotate = new RotateAnimation(180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        ivMenu.startAnimation(rotate);
    }

    /**
     * 绘制24h、48h警戒线
     */
    private void drawWarningLines() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //24小时
                PolylineOptions line1 = new PolylineOptions();
                line1.width(CommonUtil.dip2px(mContext, 2));
                line1.color(getResources().getColor(R.color.red));
                line1.add(new LatLng(34.005024, 126.993568), new LatLng(21.971252, 126.993568));
                line1.add(new LatLng(17.965860, 118.995521), new LatLng(10.971050, 118.995521));
                line1.add(new LatLng(4.486270, 113.018959) ,new LatLng(-0.035506, 104.998939));
                aMap.addPolyline(line1);
                drawWarningText(getString(R.string.line_24h), getResources().getColor(R.color.red), new LatLng(30.959474, 126.993568));

                //48小时
                PolylineOptions line2 = new PolylineOptions();
                line2.width(CommonUtil.dip2px(mContext, 2));
                line2.color(getResources().getColor(R.color.yellow));
                line2.add(new LatLng(-0.035506, 104.998939), new LatLng(-0.035506, 119.962318));
                line2.add(new LatLng(14.968860, 131.981361) ,new LatLng(33.959474, 131.981361));
                aMap.addPolyline(line2);
                drawWarningText(getString(R.string.line_48h), getResources().getColor(R.color.yellow), new LatLng(30.959474, 131.981361));
            }
        }).start();
    }

    /**
     * 绘制警戒线提示文字
     */
    private void drawWarningText(String text, int textColor, LatLng latLng) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_typhoon_marker_warning_line, null);
        TextView tvLine = view.findViewById(R.id.tvLine);
        tvLine.setText(text);
        tvLine.setTextColor(textColor);
        MarkerOptions options = new MarkerOptions();
        options.anchor(-0.3f, 0.2f);
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromView(view));
        aMap.addMarker(options);
    }

    /**
     * 绘制定位点对应的圈
     */
    private void addLocationCircles() {
        removeLocationCirces();
        if (locationLatLng == null) {
            return;
        }
        if (isRanging) {
            circle100 = aMap.addCircle(new CircleOptions().center(locationLatLng)
                    .radius(100000).strokeColor(0x90ff6c00).strokeWidth(5));

            circle300 = aMap.addCircle(new CircleOptions().center(locationLatLng)
                    .radius(300000).strokeColor(0x90ffd800).strokeWidth(5));

            circle500 = aMap.addCircle(new CircleOptions().center(locationLatLng)
                    .radius(500000).strokeColor(0x9000b4ff).strokeWidth(5));

            text100 = addCircleText(locationLatLng, 100000, 0xffff6c00, "100km");

            text300 = addCircleText(locationLatLng, 300000, 0xffffd800, "300km");

            text500 = addCircleText(locationLatLng, 500000, 0xff00b4ff, "500km");
        }
    }

    /**
     * 添加影响范围
     * @param center
     * @param radius
     * @param color
     * @param distance
     * @return
     */
    private Text addCircleText(LatLng center, int radius, int color, String distance) {
        double r = 6371000.79;
        int numpoints = 360;
        double phase = 2 * Math.PI / numpoints;

        //画图
//		for (int i = 0; i < numpoints; i++) {
        double dx = (radius * Math.cos(numpoints*3/4 * phase));
        double dy = (radius * Math.sin(numpoints*3/4 * phase));//乘以1.6 椭圆比例

        double dlng = dx / (r * Math.cos(center.latitude * Math.PI / 180) * Math.PI / 180);
        double dlat = dy / (r * Math.PI / 180);

        TextOptions textOptions = new TextOptions();
        textOptions.backgroundColor(Color.TRANSPARENT);
        textOptions.fontSize(40);
        textOptions.fontColor(color);
        textOptions.text(distance);
        textOptions.position(new LatLng(center.latitude + dlat, center.longitude + dlng));
        Text text = aMap.addText(textOptions);
//		}

        return text;
    }

    /**
     * 初始化台风年份列表
     */
    private void initYearListView() {
        yearList.clear();
        final int currentYear = Integer.valueOf(sdf1.format(new Date()));
        int years = 5;//要获取台风的年数
        for (int i = 0; i < years; i++) {
            TyphoonDto dto = new TyphoonDto();
            dto.yearly = currentYear-i;
            yearList.add(dto);
        }
        yearListView = findViewById(R.id.yearListView);
        yearAdapter = new TyphoonYearAdapter(mContext, yearList);
        yearListView.setAdapter(yearAdapter);
        yearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                for (int i = 0; i < yearList.size(); i++) {
                    if (i == arg2) {
                        yearAdapter.isSelected.put(i, true);
                    }else {
                        yearAdapter.isSelected.put(i, false);
                    }
                }
                if (yearAdapter != null) {
                    yearAdapter.notifyDataSetChanged();
                }

                for (int i = 0; i < nameList.size(); i++) {
                    nameAdapter.isSelected.put(i, false);
                }
                if (nameAdapter != null) {
                    nameAdapter.notifyDataSetChanged();
                }

                TyphoonDto dto = yearList.get(arg2);
                OkHttpTyphoonList(currentYear, dto.yearly);
            }
        });

        OkHttpTyphoonList(currentYear, currentYear);
    }

    /**
     * 初始化某一年台风名称列表
     */
    private void initNameListView() {
        nameListView = findViewById(R.id.nameListView);
        nameAdapter = new TyphoonNameAdapter(mContext, nameList);
        nameListView.setAdapter(nameAdapter);
        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                for (int i = 0; i < nameList.size(); i++) {
                    if (i == arg2) {
                        nameAdapter.isSelected.put(i, true);
                    }else {
                        nameAdapter.isSelected.put(i, false);
                    }
                }
                if (nameAdapter != null) {
                    nameAdapter.notifyDataSetChanged();
                }

                TyphoonDto dto = nameList.get(arg2);
                if (TextUtils.equals(dto.enName, "nameless")) {
                    tvTyphoonName.setText(dto.enName);
                }else {
                    tvTyphoonName.setText(dto.code+" "+dto.name+" "+dto.enName);
                }

                clearAllPoints();
                isShowInfoWindow = false;
                String detailUrl = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/"+dto.id;
                OkHttpTyphoonDetail(dto.status, dto.id, detailUrl, tvTyphoonName.getText().toString());

            }
        });
    }

    /**
     * 初始化生效台风列表
     */
    private void initCurrentListView() {
        startListView = findViewById(R.id.startListView);
        startAdapter = new TyphoonStartAdapter(mContext, startList);
        startListView.setAdapter(startAdapter);
        startListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TyphoonDto dto = startList.get(arg2);
                if (TextUtils.equals(dto.enName, "nameless")) {
                    tvTyphoonName.setText(dto.enName);
                }else {
                    tvTyphoonName.setText(dto.code+" "+dto.name+" "+dto.enName);
                }

                clearAllPoints();
                isShowInfoWindow = false;
                String detailUrl = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/"+dto.id;
                OkHttpTyphoonDetail(dto.status, dto.id, detailUrl, tvTyphoonName.getText().toString());

            }
        });
    }

    /**
     * 获取某一年的台风列表信息
     */
    private void OkHttpTyphoonList(int currentYear, final int selectYear) {
        if (currentYear != selectYear) {//当年的就不用缓存了，防止数据更新
            SharedPreferences sp = getSharedPreferences(selectYear+"", Context.MODE_PRIVATE);
            String requestResult = sp.getString(selectYear+"", "");
            if (!TextUtils.isEmpty(requestResult)) {
                parseTyphoonList(requestResult);
                return;
            }
        }

        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/list/"+selectYear;
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
                        final String requestResult = response.body().string();

                        if (!TextUtils.isEmpty(requestResult) && !TextUtils.isEmpty(selectYear+"")) {
                            SharedPreferences sp = getSharedPreferences(selectYear+"", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(selectYear+"");
                            editor.putString(selectYear+"", requestResult);
                            editor.apply();
                        }

                        parseTyphoonList(requestResult);

                    }
                });
            }
        }).start();
    }

    /**
     * 解析台风列表
     * @param requestResult
     */
    private void parseTyphoonList(final String requestResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(requestResult)) {
                    String c = "(";
                    String c2 = "})";
                    String result = requestResult.substring(requestResult.indexOf(c)+c.length(), requestResult.indexOf(c2)+1);
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (!obj.isNull("typhoonList")) {
                                nameList.clear();
                                JSONArray array = obj.getJSONArray("typhoonList");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONArray itemArray = array.getJSONArray(i);
                                    TyphoonDto dto = new TyphoonDto();
                                    dto.id = itemArray.getString(0);
                                    dto.enName = itemArray.getString(1);
                                    dto.name = itemArray.getString(2);
                                    dto.code = itemArray.getString(4);
                                    dto.status = itemArray.getString(7);
                                    nameList.add(dto);

                                    //把活跃台风过滤出来存放
                                    if (TextUtils.equals(dto.status, "start")) {
                                        startList.add(dto);
                                    }
                                }

                                String typhoonName = "";
                                for (int i = startList.size()-1; i >= 0; i--) {
                                    TyphoonDto dto = startList.get(i);
                                    String name;
                                    if (TextUtils.equals(dto.enName, "nameless")) {
                                        if (!TextUtils.isEmpty(typhoonName)) {
                                            typhoonName = dto.enName+"\n"+typhoonName;
                                        }else {
                                            typhoonName = dto.enName;
                                        }
                                        name = dto.code+" "+dto.enName;
                                    }else {
                                        if (!TextUtils.isEmpty(typhoonName)) {
                                            typhoonName = dto.code+" "+dto.name+" "+dto.enName+"\n"+typhoonName;
                                        }else {
                                            typhoonName = dto.code+" "+dto.name+" "+dto.enName;
                                        }
                                        name = dto.code+" "+dto.name+" "+dto.enName;
                                    }
                                    String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/"+dto.id;
                                    OkHttpTyphoonDetail(dto.status, dto.id, url, name);
                                }
                                if (!TextUtils.isEmpty(typhoonName)) {
                                    tvTyphoonName.setText(typhoonName);
                                }
                                if (startList.size() <= 0) {// 没有生效台风
                                    tvTyphoonName.setText(getString(R.string.no_typhoon));
                                }
                                tvTyphoonName.setVisibility(View.VISIBLE);

                                if (nameAdapter != null) {
                                    nameAdapter.notifyDataSetChanged();
                                }

                                if (startAdapter != null) {
                                    startAdapter.notifyDataSetChanged();
                                }
                                loadingView.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }

    /**
     * 获取台风详情，增加缓存机制
     */
    private void OkHttpTyphoonDetail(String status, final String typhoonId, final String url, final String typhoonName) {
        if (!TextUtils.equals(status, "start")) {//防止数据更新
            SharedPreferences sp = getSharedPreferences(typhoonId, Context.MODE_PRIVATE);
            String requestResult = sp.getString(typhoonId, "");
            if (!TextUtils.isEmpty(requestResult)) {
                parseTyphoonDetail(requestResult, typhoonName);
                return;
            }
        }

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
                        final String requestResult = response.body().string();

                        if (!TextUtils.isEmpty(requestResult) && !TextUtils.isEmpty(typhoonId)) {
                            SharedPreferences sp = getSharedPreferences(typhoonId, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(typhoonId);
                            editor.putString(typhoonId, requestResult);
                            editor.apply();
                        }

                        parseTyphoonDetail(requestResult, typhoonName);

                    }
                });
            }
        }).start();
    }

    /**
     * 解析台风信息
     * @param requestResult
     * @param typhoonName
     */
    private void parseTyphoonDetail(final String requestResult, final String typhoonName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(requestResult)) {
                    String c = "(";
                    String result = requestResult.substring(requestResult.indexOf(c)+c.length(), requestResult.indexOf(")"));
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (!obj.isNull("typhoon")) {
                                final List<TyphoonDto> factPoints = new ArrayList<>();//台风实况点
                                List<TyphoonDto> forePoints = new ArrayList<>();//台风预报点
                                JSONArray array = obj.getJSONArray("typhoon");
                                JSONArray itemArray = array.getJSONArray(8);
                                for (int j = 0; j < itemArray.length(); j++) {
                                    JSONArray itemArray2 = itemArray.getJSONArray(j);
                                    TyphoonDto dto = new TyphoonDto();
                                    if (!TextUtils.isEmpty(typhoonName)) {
                                        dto.name = typhoonName;
                                    }
                                    long longTime = itemArray2.getLong(2);
                                    String time = sdf2.format(new Date(longTime));
                                    dto.time = time;
                                    String str_year = time.substring(0, 4);
                                    if(!TextUtils.isEmpty(str_year)){
                                        dto.year = Integer.parseInt(str_year);
                                    }
                                    String str_month = time.substring(4, 6);
                                    if(!TextUtils.isEmpty(str_month)){
                                        dto.month = Integer.parseInt(str_month);
                                    }
                                    String str_day = time.substring(6, 8);
                                    if(!TextUtils.isEmpty(str_day)){
                                        dto.day = Integer.parseInt(str_day);
                                    }
                                    String str_hour = time.substring(8, 10);
                                    if(!TextUtils.isEmpty(str_hour)){
                                        dto.hour = Integer.parseInt(str_hour);
                                    }

                                    dto.lng = itemArray2.getDouble(4);
                                    dto.lat = itemArray2.getDouble(5);
                                    dto.pressure = itemArray2.getString(6);
                                    dto.max_wind_speed = itemArray2.getString(7);
                                    dto.move_speed = itemArray2.getString(9);
                                    String fx_string = itemArray2.getString(8);
                                    if( !TextUtils.isEmpty(fx_string)){
                                        String windDir = "";
                                        for (int i = 0; i < fx_string.length(); i++) {
                                            String fx = fx_string.substring(i, i+1);
                                            if (TextUtils.equals(fx, "N")) {
                                                fx = "北";
                                            }else if (TextUtils.equals(fx, "S")) {
                                                fx = "南";
                                            }else if (TextUtils.equals(fx, "W")) {
                                                fx = "西";
                                            }else if (TextUtils.equals(fx, "E")) {
                                                fx = "东";
                                            }
                                            windDir = windDir+fx;
                                        }
                                        dto.wind_dir = windDir;
                                    }

                                    String type = itemArray2.getString(3);
                                    if (TextUtils.equals(type, "TD")) {//热带低压
                                        type = "1";
                                    }else if (TextUtils.equals(type, "TS")) {//热带风暴
                                        type = "2";
                                    }else if (TextUtils.equals(type, "STS")) {//强热带风暴
                                        type = "3";
                                    }else if (TextUtils.equals(type, "TY")) {//台风
                                        type = "4";
                                    }else if (TextUtils.equals(type, "STY")) {//强台风
                                        type = "5";
                                    }else if (TextUtils.equals(type, "SuperTY")) {//超强台风
                                        type = "6";
                                    }
                                    dto.type = type;
                                    dto.isFactPoint = true;

                                    JSONArray array10 = itemArray2.getJSONArray(10);
                                    for (int m = 0; m < array10.length(); m++) {
                                        JSONArray itemArray10 = array10.getJSONArray(m);
                                        if (m == 0) {
                                            dto.radius_7 = itemArray10.getString(1);
                                            dto.en_radius_7 = itemArray10.getString(1);
                                            dto.es_radius_7 = itemArray10.getString(2);
                                            dto.wn_radius_7 = itemArray10.getString(3);
                                            dto.ws_radius_7 = itemArray10.getString(4);
                                        }else if (m == 1) {
                                            dto.radius_10 = itemArray10.getString(1);
                                            dto.en_radius_10 = itemArray10.getString(1);
                                            dto.es_radius_10 = itemArray10.getString(2);
                                            dto.wn_radius_10 = itemArray10.getString(3);
                                            dto.ws_radius_10 = itemArray10.getString(4);
                                        }
                                    }
                                    factPoints.add(dto);

                                    if (!itemArray2.get(11).equals(null)) {
                                        JSONObject obj11 = itemArray2.getJSONObject(11);
                                        JSONArray array11 = obj11.getJSONArray("BABJ");
                                        if (array11.length() > 0) {
                                            forePoints.clear();
                                        }
                                        for (int n = 0; n < array11.length(); n++) {
                                            JSONArray itemArray11 = array11.getJSONArray(n);
                                            for (int i = 0; i < itemArray11.length(); i++) {
                                                TyphoonDto data = new TyphoonDto();
                                                if (!TextUtils.isEmpty(typhoonName)) {
                                                    data.name = typhoonName;
                                                }
                                                data.lng = itemArray11.getDouble(2);
                                                data.lat = itemArray11.getDouble(3);
                                                data.pressure = itemArray11.getString(4);
                                                data.move_speed = itemArray11.getString(5);

                                                long t1 = longTime;
                                                long t2 = itemArray11.getLong(0)*3600*1000;
                                                long ttt = t1+t2;
                                                String ttime = sdf2.format(new Date(ttt));
                                                data.time = ttime;
                                                String year = ttime.substring(0, 4);
                                                if(!TextUtils.isEmpty(year)){
                                                    data.year = Integer.parseInt(year);
                                                }
                                                String month = ttime.substring(4, 6);
                                                if(!TextUtils.isEmpty(month)){
                                                    data.month = Integer.parseInt(month);
                                                }
                                                String day = ttime.substring(6, 8);
                                                if(!TextUtils.isEmpty(day)){
                                                    data.day = Integer.parseInt(day);
                                                }
                                                String hour = ttime.substring(8, 10);
                                                if(!TextUtils.isEmpty(hour)){
                                                    data.hour = Integer.parseInt(hour);
                                                }

                                                String babjType = itemArray11.getString(7);
                                                if (TextUtils.equals(babjType, "TD")) {//热带低压
                                                    babjType = "1";
                                                }else if (TextUtils.equals(babjType, "TS")) {//热带风暴
                                                    babjType = "2";
                                                }else if (TextUtils.equals(babjType, "STS")) {//强热带风暴
                                                    babjType = "3";
                                                }else if (TextUtils.equals(babjType, "TY")) {//台风
                                                    babjType = "4";
                                                }else if (TextUtils.equals(babjType, "STY")) {//强台风
                                                    babjType = "5";
                                                }else if (TextUtils.equals(babjType, "SuperTY")) {//超强台风
                                                    babjType = "6";
                                                }
                                                data.type = babjType;
                                                data.isFactPoint = false;

                                                forePoints.add(data);
                                            }
                                        }
                                    }
                                }

                                factPoints.addAll(forePoints);
                                drawTyphoon(false, factPoints);
                                loadingView.setVisibility(View.GONE);
                                lyoutTyphoon.setVisibility(View.VISIBLE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 清除定位点对应的100、300、500km影响圈
     */
    private void removeLocationCirces() {
        if (circle100 != null) {
            circle100.remove();
            circle100 = null;
        }
        if (circle300 != null) {
            circle300.remove();
            circle300 = null;
        }
        if (circle500 != null) {
            circle500.remove();
            circle500 = null;
        }
        if (text100 != null) {
            text100.remove();
            text100 = null;
        }
        if (text300 != null) {
            text300.remove();
            text300 = null;
        }
        if (text500 != null) {
            text500.remove();
            text500 = null;
        }
    }

    /**
     * 清除测距markers
     */
    private void removeRange() {
        //清除测距虚线
        for (int i = 0; i < rangeLines.size(); i++) {
            rangeLines.get(i).remove();
        }
        rangeLines.clear();

        //清除测距marker
        for (int i = 0; i < rangeMarkers.size(); i++) {
            rangeMarkers.get(i).remove();
        }
        rangeMarkers.clear();
    }

    /**
     * 清除七级、十级风圈
     */
    private void removeWindCircle() {
        if (circle7 != null) {
            circle7.remove();
            circle7 = null;
        }
        if (circle10 != null) {
            circle10.remove();
            circle10 = null;
        }
    }

    /**
     * 清除台风实况、预报、旋转图标、时间等markers
     */
    private void removeTyphoons() {
        for (int i = 0; i < fullLines.size(); i++) {//清除实线
            fullLines.get(i).remove();
        }
        fullLines.clear();

        for (int i = 0; i < dashLines.size(); i++) {//清除虚线
            dashLines.get(i).remove();
        }
        dashLines.clear();

        for (int i = 0; i < rotateMarkers.size(); i++) {
            rotateMarkers.get(i).remove();
        }
        rotateMarkers.clear();

        for (int i = 0; i < markerPoints.size(); i++) {//清除台风点
            markerPoints.get(i).remove();
        }
        markerPoints.clear();

        for (int i = 0; i < factTimeMarkers.size(); i++) {
            factTimeMarkers.get(i).remove();
        }
        factTimeMarkers.clear();

        for (int i = 0; i < foreTimeMarkers.size(); i++) {
            foreTimeMarkers.get(i).remove();
        }
        foreTimeMarkers.clear();
    }

    /**
     * 清除一个台风
     */
    private void clearAllPoints() {
        removeLocationCirces();
        removeWindCircle();
        removeRange();
        removeTyphoons();
    }

    /**
     * 绘制台风
     * @param isAnimate
     */
    private void drawTyphoon(boolean isAnimate, List<TyphoonDto> list) {
        if (list.isEmpty()) {
            return;
        }

        if (mRoadThread != null) {
            mRoadThread.cancel();
            mRoadThread = null;
        }
        mRoadThread = new RoadThread(list, isAnimate);
        mRoadThread.start();
    }

    /**
     * 绘制台风点
     */
    private class RoadThread extends Thread {

        private boolean cancelled;
        private boolean isAnimate;
        private List<TyphoonDto> allPoints;//整个台风路径信息

        private RoadThread(List<TyphoonDto> allPoints, boolean isAnimate) {
            this.allPoints = allPoints;
            this.isAnimate = isAnimate;
        }

        @Override
        public void run() {
            final int len = allPoints.size();

            //台风实况点
            final List<TyphoonDto> factPoints = new ArrayList<>();
            for (int j = 0; j < len; j++) {
                if (allPoints.get(j).isFactPoint) {
                    factPoints.add(allPoints.get(j));
                }
            }

            for (int i = 0; i < len; i++) {
                if (cancelled) {
                    break;
                }

                if (i == len-1) {
                    Message msg = typhoonHandler.obtainMessage(DRAW_TYPHOON_COMPLETE);
                    msg.obj = allPoints;
                    typhoonHandler.sendMessage(msg);
                }

                if (isAnimate) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final TyphoonDto firstPoint = allPoints.get(i);//第一个点
                final TyphoonDto lastPoint = i >= (len-1) ? null : allPoints.get(i+1);//最后一个点
                final TyphoonDto lastFactPoint = factPoints.get(factPoints.size()-1);//最后一个实况点
                drawRoute(firstPoint, lastPoint, lastFactPoint);
            }

        }

        private void cancel() {
            cancelled = true;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler typhoonHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DRAW_TYPHOON_COMPLETE://台风绘制结束
                    List<TyphoonDto> mPoints = (ArrayList<TyphoonDto>)msg.obj;
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (int i = 0; i < mPoints.size(); i++) {
                        TyphoonDto dto = mPoints.get(i);
                        LatLng latLng = new LatLng(dto.lat, dto.lng);
                        builder.include(latLng);
                    }
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
                    break;
            }
        }
    };

    /**
     * 绘制台风路径
     * @param firstPoint
     * @param lastPoint
     * @param lastFactPoint
     */
    private void drawRoute(TyphoonDto firstPoint, TyphoonDto lastPoint, TyphoonDto lastFactPoint) {
        if (lastPoint == null) {//最后一个点
            return;
        }

        double firstLat = firstPoint.lat;
        double firstLng = firstPoint.lng;
        double lastLat = lastPoint.lat;
        double lastLng = lastPoint.lng;
        LatLng firstLatLng = new LatLng(firstLat, firstLng);
        LatLng lastLatLng = new LatLng(lastLat, lastLng);

        //绘制线
        ArrayList<LatLng> latLngs = new ArrayList<>();
        if (lastPoint.isFactPoint) {//实况线
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(CommonUtil.dip2px(mContext, 2));
            polylineOptions.color(Color.RED);
            latLngs.add(firstLatLng);
            latLngs.add(lastLatLng);
            polylineOptions.addAll(latLngs);
            Polyline fullLine = aMap.addPolyline(polylineOptions);
            fullLines.add(fullLine);
        } else {//预报虚线
            double dis = Math.sqrt(Math.pow(firstLat-lastLat, 2)+ Math.pow(firstLng-lastLng, 2));
            int numPoint = (int) Math.floor(dis/0.2);
            double lng_per = (lastLng-firstLng)/numPoint;
            double lat_per = (lastLat-firstLat)/numPoint;
            for (int i = 0; i < numPoint; i++) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(CommonUtil.dip2px(mContext, 2));
                latLngs.add(new LatLng(firstLat+i*lat_per, firstLng+i*lng_per));
                if (i % 2 == 1) {
                    polylineOptions.addAll(latLngs);
                    Polyline dashLine = aMap.addPolyline(polylineOptions);
                    dashLines.add(dashLine);
                    latLngs.clear();
                }
            }
        }

        //绘制台风点
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View typhoonPoint = inflater.inflate(R.layout.shawn_typhoon_marker_point, null);
        ImageView ivPoint = typhoonPoint.findViewById(R.id.ivPoint);
        if (TextUtils.equals(firstPoint.type, "1")) {
            ivPoint.setImageResource(R.drawable.typhoon_level1);
        }else if (TextUtils.equals(firstPoint.type, "2")) {
            ivPoint.setImageResource(R.drawable.typhoon_level2);
        }else if (TextUtils.equals(firstPoint.type, "3")) {
            ivPoint.setImageResource(R.drawable.typhoon_level3);
        }else if (TextUtils.equals(firstPoint.type, "4")) {
            ivPoint.setImageResource(R.drawable.typhoon_level4);
        }else if (TextUtils.equals(firstPoint.type, "5")) {
            ivPoint.setImageResource(R.drawable.typhoon_level5);
        }else if (TextUtils.equals(firstPoint.type, "6")) {
            ivPoint.setImageResource(R.drawable.typhoon_level6);
        }else {//预报点
            ivPoint.setImageResource(R.drawable.typhoon_yb);
        }
        MarkerOptions options = new MarkerOptions();
        options.title(firstPoint.name+"|"+firstPoint.content(mContext)+"|"+firstPoint.radius_7+"|"+firstPoint.radius_10);
        options.snippet(TYPE_TYPHOON);
        options.anchor(0.5f, 0.5f);
        options.position(firstLatLng);
        options.icon(BitmapDescriptorFactory.fromView(typhoonPoint));
        Marker marker = aMap.addMarker(options);
        markerPoints.add(marker);


        if (firstPoint.isFactPoint && lastFactPoint == firstPoint) {//最后一个实况点
            if(isShowInfoWindow) {
                marker.showInfoWindow();
                clickMarker = marker;
            }

            //绘制最后一个实况点对应的七级、十级风圈
            drawWindCircle(firstPoint.radius_7, firstPoint.radius_10, firstLatLng);

            //绘制最后一个实况点对应的时间
            View timeView = inflater.inflate(R.layout.shawn_typhoon_marker_time, null);
            TextView tvTime = timeView.findViewById(R.id.tvTime);
            if (!TextUtils.isEmpty(firstPoint.time)) {
                try {
                    tvTime.setText(sdf5.format(sdf2.parse(firstPoint.time)));
                    tvTime.setTextColor(Color.BLACK);
                    tvTime.setBackgroundResource(R.drawable.shawn_bg_corner_typhoon_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            MarkerOptions mo = new MarkerOptions();
            mo.anchor(1.2f, 0.5f);
            mo.position(firstLatLng);
            mo.icon(BitmapDescriptorFactory.fromView(timeView));
            Marker factTimeMarker = aMap.addMarker(mo);
            factTimeMarker.setClickable(false);
            factTimeMarkers.add(factTimeMarker);
            if (isShowTime) {
                factTimeMarker.setVisible(true);
            }else {
                factTimeMarker.setVisible(false);
            }

            //绘制台风旋转图标
            MarkerOptions tOption = new MarkerOptions();
            tOption.position(firstLatLng);
            tOption.anchor(0.5f, 0.5f);
            tOption.icon(BitmapDescriptorFactory.fromAsset("typhoon/typhoon_icon1.png"));
//            ArrayList<BitmapDescriptor> iconList = new ArrayList<>();
//            for (int i = 1; i <= 9; i++) {
//                iconList.add(BitmapDescriptorFactory.fromAsset("typhoon/typhoon_icon"+i+".png"));
//            }
//            tOption.icons(iconList);
//            tOption.period(6);
            tOption.zIndex(-10);
            Marker rotateMarker = aMap.addMarker(tOption);
            rotateMarker.setClickable(false);
            rotateMarkers.add(rotateMarker);

            //多个台风最后实况点合在一起
            boolean isContain = false;
            for (int i = 0; i < lastFactLatLngList.size(); i++) {
                TyphoonDto data = lastFactLatLngList.get(i);
                if (data.lat == lastFactPoint.lat && data.lng == lastFactPoint.lng) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                if (startList.size() <= 1) {
                    lastFactLatLngList.clear();
                }
                lastFactLatLngList.add(lastFactPoint);
            }

            addLocationMarker();
            addLocationCircles();
            ranging();
        }else {//绘制预报点的预报时间
            View timeView = inflater.inflate(R.layout.shawn_typhoon_marker_time, null);
            TextView tvTime = timeView.findViewById(R.id.tvTime);
            if (!TextUtils.isEmpty(firstPoint.time)) {
                try {
                    tvTime.setText(sdf4.format(sdf2.parse(firstPoint.time)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            MarkerOptions mo = new MarkerOptions();
            mo.anchor(-0.05f, 0.5f);
            mo.position(firstLatLng);
            mo.icon(BitmapDescriptorFactory.fromView(timeView));
            Marker m = aMap.addMarker(mo);
            foreTimeMarkers.add(m);
            if (isShowTime) {
                m.setVisible(true);
            }else {
                m.setVisible(false);
            }
        }
    }

    /**
     * 绘制七级、十级风圈
     */
    private void drawWindCircle(String radius_7, String radius_10, LatLng latLng) {
        removeWindCircle();

        if (!TextUtils.isEmpty(radius_7) && !TextUtils.equals(radius_7, "null")) {
            circle7 = aMap.addCircle(new CircleOptions().center(latLng)
                    .radius(Double.valueOf(radius_7)*1000).strokeColor(Color.YELLOW)
                    .fillColor(0x30ffffff).strokeWidth(5));
        }

        if (!TextUtils.isEmpty(radius_10) && !TextUtils.equals(radius_10, "null")) {
            circle10 = aMap.addCircle(new CircleOptions().center(latLng)
                    .radius(Double.valueOf(radius_10)*1000).strokeColor(Color.RED)
                    .fillColor(0x30ffffff).strokeWidth(5));
        }
    }

    /**
     * 测距
     */
    private void ranging() {
        if (locationLatLng == null || !isRanging) {
            return;
        }

        double locationLat = locationLatLng.latitude;
        double locationLng = locationLatLng.longitude;
        for (int j = 0; j < lastFactLatLngList.size(); j++) {
            TyphoonDto dto = lastFactLatLngList.get(j);
            double lat = dto.lat;
            double lng = dto.lng;
            double dis = Math.sqrt(Math.pow(locationLat-lat, 2)+ Math.pow(locationLng-lng, 2));
            int numPoint = (int) Math.floor(dis/0.2);
            double lng_per = (lng-locationLng)/numPoint;
            double lat_per = (lat-locationLat)/numPoint;
            List<LatLng> ranges = new ArrayList<>();
            for (int i = 0; i < numPoint; i++) {
                PolylineOptions line = new PolylineOptions();
                line.color(0xff6291E1);
                line.width(CommonUtil.dip2px(mContext, 2));
                ranges.add(new LatLng(locationLat+i*lat_per, locationLng+i*lng_per));
                if (i % 2 == 1) {
                    line.addAll(ranges);
                    Polyline dashLine = aMap.addPolyline(line);
                    rangeLines.add(dashLine);
                    ranges.clear();
                }
            }

            LatLng centerLatLng = new LatLng((locationLat+lat)/2, (locationLng+lng)/2);
            addRangeMarker(centerLatLng, locationLng, locationLat, lng, lat);
        }

    }

    /**
     * 添加每个台风的测距距离
     */
    private void addRangeMarker(LatLng latLng, double longitude1, double latitude1, double longitude2, double latitude2) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.shawn_typhoon_marker_range, null);
        TextView tvName = mView.findViewById(R.id.tvName);
        tvName.setText("距离台风"+getDistance(longitude1, latitude1, longitude2, latitude2)+"公里");
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromView(mView));
        Marker marker = aMap.addMarker(options);
        marker.setClickable(false);
        rangeMarkers.add(marker);
    }

    /**
     * 计算两点之间距离
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    private static String getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double EARTH_RADIUS = 6378137;//单位米
        double Lat1 = latitude1 * Math.PI / 180.0;
        double Lat2 = latitude2 * Math.PI / 180.0;
        double Lng1 = longitude1 * Math.PI / 180.0;
        double Lng2 = longitude2 * Math.PI / 180.0;
        double a = Lat1-Lat2;
        double b = Lng1-Lng2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        BigDecimal bd = new BigDecimal(s / 1000);
        double d = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d+"";
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker != locationMarker) {
            String markerType = marker.getSnippet();
            if (!TextUtils.isEmpty(markerType)) {
                if (TextUtils.equals(markerType, TYPE_TYPHOON)) {
                    String[] title = marker.getTitle().split("\\|");
                    drawWindCircle(title[2], title[3], marker.getPosition());
                }else if (TextUtils.equals(markerType, TYPE_WARNING)) {
                    //点击预警marker
                }
            }

            marker.showInfoWindow();
            clickMarker = marker;

        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isShowTyphoon && isRanging) {//测距状态下
            removeRange();
            locationLatLng = latLng;
            addLocationMarker();
            addLocationCircles();
            ranging();
        }

        if (isShowMinute) {
            locationLatLng = latLng;
            addLocationMarker();
            searchAddrByLatLng(latLng.latitude, latLng.longitude);
            OkHttpMinute(latLng.longitude, latLng.latitude);
        }

        mapClick();
    }

    private void mapClick() {
        if (clickMarker != null && clickMarker.isInfoWindowShown()) {
            clickMarker.hideInfoWindow();
        }
        removeWindCircle();
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (TextUtils.equals(marker.getSnippet(), TYPE_TYPHOON)) {//台风
            view = inflater.inflate(R.layout.shawn_typhoon_marker_icon, null);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvInfo = view.findViewById(R.id.tvInfo);
            ImageView ivDelete = view.findViewById(R.id.ivDelete);
            if (!TextUtils.isEmpty(marker.getTitle())) {
                String[] str = marker.getTitle().split("\\|");
                if (!TextUtils.isEmpty(str[0])) {
                    tvName.setText(str[0]);
                }
                if (!TextUtils.isEmpty(str[1])) {
                    tvInfo.setText(str[1]);
                }
            }
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick();
                }
            });
        }else if (TextUtils.equals(marker.getSnippet(), TYPE_WARNING)) {//预警
            view = inflater.inflate(R.layout.shawn_warning_marker_info, null);
            final List<WarningDto> infoList = new ArrayList<>();
            infoList.clear();

            for (int i = 0; i < warningList.size(); i++) {
                WarningDto dto = warningList.get(i);
                String[] latLng = marker.getTitle().split(",");
                if (TextUtils.equals(latLng[0], dto.lat) && TextUtils.equals(latLng[1], dto.lng)) {
                    infoList.add(dto);
                }
            }

            //按照预警级别排序
            Collections.sort(infoList, new Comparator<WarningDto>() {
                @Override
                public int compare(WarningDto a, WarningDto b) {
                    return b.color.compareTo(a.color);
                }
            });

            ListView mListView = view.findViewById(R.id.listView);
            WarningAdapter mAdapter = new WarningAdapter(mContext, infoList, true);
            mListView.setAdapter(mAdapter);
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            if (infoList.size() == 1) {
                params.height = (int) CommonUtil.dip2px(mContext, 50);
            }else if (infoList.size() == 2) {
                params.height = (int) CommonUtil.dip2px(mContext, 100);
            }else if (infoList.size() > 2){
                params.height = (int) CommonUtil.dip2px(mContext, 150);
            }
            mListView.setLayoutParams(params);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    WarningDto dto = infoList.get(arg2);
                    Intent intentDetail = new Intent(mContext, ShawnWarningDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", dto);
                    intentDetail.putExtras(bundle);
                    startActivity(intentDetail);
                }
            });
        }

        return view;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    /**
     * 列表动画
     * @param flag
     * @param view
     */
    private void upDownAnimation(boolean flag, final View view) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation animation;
        if (!flag) {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0);
        }else {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,0f,
                    Animation.RELATIVE_TO_SELF,0f,
                    Animation.RELATIVE_TO_SELF,0f,
                    Animation.RELATIVE_TO_SELF,1.0f);
        }
        animation.setDuration(400);
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


    //卫星拼图
    /**
     * 获取云图数据
     */
    private void OkHttpCloudChart() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://decision-admin.tianqi.cn/Home/other/getDecisionCloudImages";
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
                                        if (!obj.isNull("l")) {
                                            JSONArray array = obj.getJSONArray("l");
                                            if (array.length() > 0) {
                                                JSONObject itemObj = array.getJSONObject(0);
                                                String imgUrl = itemObj.getString("l2");
                                                OkHttpCloudImg(imgUrl);
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

    /**
     * 下载云图
     * @param imgUrl
     */
    private void OkHttpCloudImg(final String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(imgUrl).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final byte[] bytes = response.body().bytes();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                cloudBitmap = bitmap;
                                drawCloud(bitmap);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 绘制卫星拼图
     */
    private void drawCloud(Bitmap bitmap) {
        loadingView.setVisibility(View.GONE);
        if (bitmap == null) {
            return;
        }
        BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(-10.787277369124666, 62.8820698883665))
                .include(new LatLng(56.385845314127209, 161.69675114151386))
                .build();

        if (cloudOverlay == null) {
            cloudOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                    .anchor(0.5f, 0.5f)
                    .positionFromBounds(bounds)
                    .image(fromView)
                    .transparency(0.2f));
        } else {
            cloudOverlay.setImage(null);
            cloudOverlay.setPositionFromBounds(bounds);
            cloudOverlay.setImage(fromView);
        }
    }

    /**
     * 清除云图
     */
    private void removeCloud() {
        if (cloudOverlay != null) {
            cloudOverlay.remove();
            cloudOverlay = null;
        }
    }


    //雷达拼图
    /**
     * 获取彩云数据
     */
    private void OkHttpCaiyun() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://api.tianqi.cn:8070/v1/img.py";
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
                        String result = response.body().string();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (!obj.isNull("status")) {
                                    if (obj.getString("status").equals("ok")) {
                                        if (!obj.isNull("radar_img")) {
                                            JSONArray array = new JSONArray(obj.getString("radar_img"));
                                            caiyunList.clear();
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONArray array0 = array.getJSONArray(i);
                                                MinuteFallDto dto = new MinuteFallDto();
                                                dto.imgUrl = array0.optString(0);
                                                dto.time = array0.optLong(1);
                                                JSONArray itemArray = array0.getJSONArray(2);
                                                dto.p1 = itemArray.optDouble(0);
                                                dto.p2 = itemArray.optDouble(1);
                                                dto.p3 = itemArray.optDouble(2);
                                                dto.p4 = itemArray.optDouble(3);
                                                caiyunList.add(dto);
                                            }
                                            if (caiyunList.size() > 0) {
                                                startDownloadCaiyunImgs(caiyunList);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 下载图片
     * @param list
     */
    private void startDownloadCaiyunImgs(List<MinuteFallDto> list) {
        caiyunManager.loadImagesAsyn(list, new CaiyunManager.RadarListener() {
            @Override
            public void onResult(int result, List<MinuteFallDto> images) {
                radarHandler.sendEmptyMessage(HANDLER_LOAD_FINISHED);
                if (result == CaiyunManager.RadarListener.RESULT_SUCCESSED) {
                    removeRadar();
                    caiyunThread = new CaiyunThread(images);
                    caiyunThread.start();

                    //把最新的一张降雨图片覆盖在地图上
//			MinuteFallDto radar = images.get(images.size()-1);
//			Message message = mHandler.obtainMessage();
//			message.what = HANDLER_SHOW_RADAR;
//			message.obj = radar;
//			message.arg1 = 100;
//			message.arg2 = 100;
//			mHandler.sendMessage(message);
                }
            }

            @Override
            public void onProgress(String url, int progress) {
                Message msg = new Message();
                msg.obj = progress;
                msg.what = HANDLER_PROGRESS;
                radarHandler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler radarHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SHOW_RADAR:
                    if (msg.obj != null) {
                        MinuteFallDto dto = (MinuteFallDto) msg.obj;
                        if (!TextUtils.isEmpty(dto.path)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                            if (bitmap != null) {
                                drawRadar(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                            }
                        }
                    }
                    break;
                case HANDLER_PROGRESS:
//				if (mDialog != null) {
//					if (msg.obj != null) {
//						int progress = (Integer) msg.obj;
//						mDialog.setPercent(progress);
//					}
//				}
                    break;
                case HANDLER_LOAD_FINISHED:
                    loadingView.setVisibility(View.GONE);
                    break;
                case HANDLER_PAUSE:

                    break;

                default:
                    break;
            }

        };
    };

    /**
     * 绘制雷达图层
     * @param bitmap
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    private void drawRadar(Bitmap bitmap, double p1, double p2, double p3, double p4) {
        BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(p3, p2))
                .include(new LatLng(p1, p4))
                .build();

        if (radarOverlay == null) {
            radarOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                    .anchor(0.5f, 0.5f)
                    .positionFromBounds(bounds)
                    .image(fromView)
                    .transparency(0.0f));
        } else {
            radarOverlay.setImage(null);
            radarOverlay.setPositionFromBounds(bounds);
            radarOverlay.setImage(fromView);
        }
        aMap.runOnDrawFrame();
    }

    private class CaiyunThread extends Thread {
        static final int STATE_NONE = 0;
        static final int STATE_PLAYING = 1;
        static final int STATE_PAUSE = 2;
        static final int STATE_CANCEL = 3;
        private List<MinuteFallDto> images;
        private int state;
        private int index;
        private int count;
        private boolean isTracking;

        public CaiyunThread(List<MinuteFallDto> images) {
            this.images = images;
            this.count = images.size();
            this.index = 0;
            this.state = STATE_NONE;
            this.isTracking = false;
        }

        public int getCurrentState() {
            return state;
        }

        @Override
        public void run() {
            super.run();
            this.state = STATE_PLAYING;
            while (true) {
                if (state == STATE_CANCEL) {
                    break;
                }
                if (state == STATE_PAUSE) {
                    continue;
                }
                if (isTracking) {
                    continue;
                }
                sendRadar();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendRadar() {
            if (index >= count || index < 0) {
                index = 0;

//				if (mRadarThread != null) {
//					mRadarThread.pause();
//
//					Message message = mHandler.obtainMessage();
//					message.what = HANDLER_PAUSE;
//					mHandler.sendMessage(message);
//					if (seekBar != null) {
//						seekBar.setProgress(100);
//					}
//				}
            }else {
                MinuteFallDto radar = images.get(index);
                Message message = radarHandler.obtainMessage();
                message.what = HANDLER_SHOW_RADAR;
                message.obj = radar;
                message.arg1 = count - 1;
                message.arg2 = index ++;
                radarHandler.sendMessage(message);
            }
        }

        public void cancel() {
            this.state = STATE_CANCEL;
        }
        public void pause() {
            this.state = STATE_PAUSE;
        }
        public void play() {
            this.state = STATE_PLAYING;
        }

        public void setCurrent(int index) {
            this.index = index;
        }

        public void startTracking() {
            isTracking = true;
        }

        public void stopTracking() {
            isTracking = false;
            if (this.state == STATE_PAUSE) {
                sendRadar();
            }
        }
    }

    /**
     * 清除雷达拼图，取消线程
     */
    private void removeRadar() {
        if (caiyunThread != null) {
            caiyunThread.cancel();
            caiyunThread = null;
        }
        if (radarOverlay != null) {
            radarOverlay.remove();
            radarOverlay = null;
        }
    }


    //预警
    /**
     * 获取预警信息
     */
    private void OkHttpWarning() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://decision-admin.tianqi.cn/Home/extra/getwarns?order=1&areaid=44";
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
                                        if (!object.isNull("data")) {
                                            warningList.clear();
                                            JSONArray jsonArray = object.getJSONArray("data");
                                            for (int i = jsonArray.length()-1; i >= 0; i--) {
                                                JSONArray tempArray = jsonArray.getJSONArray(i);
                                                WarningDto dto = new WarningDto();
                                                dto.html = tempArray.optString(1);
                                                String[] array = dto.html.split("-");
                                                String item0 = array[0];
                                                String item1 = array[1];
                                                String item2 = array[2];

                                                dto.item0 = item0;
                                                dto.provinceId = item0.substring(0, 2);
                                                dto.type = item2.substring(0, 5);
                                                dto.color = item2.substring(5, 7);
                                                dto.time = item1;
                                                dto.lng = tempArray.optString(2);
                                                dto.lat = tempArray.optString(3);
                                                dto.name = tempArray.optString(0);

                                                if (!dto.name.contains("解除")) {
                                                    warningList.add(dto);
                                                }
                                            }

                                            removeWarningMarkers();
                                            addWarningMarkers();

                                            try {
                                                String count = warningList.size()+"";
                                                String time = "";
                                                if (!object.isNull("time")) {
                                                    long t = object.getLong("time");
                                                    time = sdf6.format(new Date(t*1000));
                                                }
                                                if (TextUtils.equals(count, "0")) {
                                                    tvWarningPrompt.setText(time+", "+"当前生效预警"+count+"条");
                                                    tvWaringList.setVisibility(View.GONE);
                                                    llWarningPrompt.setVisibility(View.VISIBLE);
                                                    layoutWarning.setVisibility(View.VISIBLE);
                                                    return;
                                                }

                                                String str1 = time+", "+"当前生效预警";
                                                String str2 = "条";
                                                String warningInfo = str1+count+str2;
                                                SpannableStringBuilder builder = new SpannableStringBuilder(warningInfo);
                                                ForegroundColorSpan builderSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.text_color3));
                                                ForegroundColorSpan builderSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.red));
                                                ForegroundColorSpan builderSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.text_color3));
                                                builder.setSpan(builderSpan1, 0, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                builder.setSpan(builderSpan2, str1.length(), str1.length()+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                builder.setSpan(builderSpan3, str1.length()+count.length(), str1.length()+count.length()+str2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                tvWarningPrompt.setText(builder);
                                                tvWaringList.setVisibility(View.VISIBLE);
                                                llWarningPrompt.setVisibility(View.VISIBLE);
                                                layoutWarning.setVisibility(View.VISIBLE);

                                                //计算统计列表信息
                                                int rnation = 0, rpro = 0, rcity = 0, rdis = 0;
                                                int onation = 0, opro = 0, ocity = 0, odis = 0;
                                                int ynation = 0, ypro = 0, ycity = 0, ydis = 0;
                                                int bnation = 0, bpro = 0, bcity = 0, bdis = 0;
                                                for (int i = 0; i < warningList.size(); i++) {
                                                    WarningDto dto = warningList.get(i);
                                                    if (TextUtils.equals(dto.color, "04")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            rnation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            rpro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            rcity += 1;
                                                        }else {
                                                            rdis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "03")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            onation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            opro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            ocity += 1;
                                                        }else {
                                                            odis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "02")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            ynation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            ypro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            ycity += 1;
                                                        }else {
                                                            ydis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "01")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            bnation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            bpro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            bcity += 1;
                                                        }else {
                                                            bdis += 1;
                                                        }
                                                    }
                                                }

                                                warningStatistics.clear();
                                                WarningDto wDto = new WarningDto();
                                                wDto.colorName = "预警"+warningList.size();
                                                wDto.nationCount = "国家级"+(rnation+onation+ynation+bnation);
                                                wDto.proCount = "省级"+(rpro+opro+ypro+bpro);
                                                wDto.cityCount = "市级"+(rcity+ocity+ycity+bcity);
                                                wDto.disCount = "县级"+(rdis+odis+ydis+bdis);
                                                warningStatistics.add(wDto);

                                                wDto = new WarningDto();
                                                wDto.colorName = "红"+(rnation+rpro+rcity+rdis);
                                                wDto.nationCount = rnation+"";
                                                wDto.proCount = rpro+"";
                                                wDto.cityCount = rcity+"";
                                                wDto.disCount = rdis+"";
                                                warningStatistics.add(wDto);

                                                wDto = new WarningDto();
                                                wDto.colorName = "橙"+(onation+opro+ocity+odis);
                                                wDto.nationCount = onation+"";
                                                wDto.proCount = opro+"";
                                                wDto.cityCount = ocity+"";
                                                wDto.disCount = odis+"";
                                                warningStatistics.add(wDto);

                                                wDto = new WarningDto();
                                                wDto.colorName = "黄"+(ynation+ypro+ycity+ydis);
                                                wDto.nationCount = ynation+"";
                                                wDto.proCount = ypro+"";
                                                wDto.cityCount = ycity+"";
                                                wDto.disCount = ydis+"";
                                                warningStatistics.add(wDto);

                                                wDto = new WarningDto();
                                                wDto.colorName = "蓝"+(bnation+bpro+bcity+bdis);
                                                wDto.nationCount = bnation+"";
                                                wDto.proCount = bpro+"";
                                                wDto.cityCount = bcity+"";
                                                wDto.disCount = bdis+"";
                                                warningStatistics.add(wDto);

                                                if (warningAdapter != null) {
                                                    warningAdapter.notifyDataSetChanged();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
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
     * 清除预警markers
     */
    private void removeWarningMarkers() {
        for (Marker marker : warningMarkers) {
            marker.remove();
        }
        warningMarkers.clear();
    }

    /**
     * 绘制预警markers
     */
    private void addWarningMarkers() {
        if (warningList.size() <= 0) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (WarningDto dto : warningList) {
            double lat = Double.valueOf(dto.lat);
            double lng = Double.valueOf(dto.lng);
            MarkerOptions optionsTemp = new MarkerOptions();
            optionsTemp.title(dto.lat+","+dto.lng);
            optionsTemp.snippet(TYPE_WARNING);
            optionsTemp.anchor(0.5f, 0.5f);
            optionsTemp.position(new LatLng(lat, lng));
            View mView = inflater.inflate(R.layout.shawn_warning_marker_icon, null);
            ImageView ivMarker = mView.findViewById(R.id.ivMarker);

            Bitmap bitmap = null;
            if (dto.color.equals(CONST.blue[0])) {
                bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.blue[1]+CONST.imageSuffix);
                if (bitmap == null) {
                    bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.blue[1]+CONST.imageSuffix);
                }
            }else if (dto.color.equals(CONST.yellow[0])) {
                bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.yellow[1]+CONST.imageSuffix);
                if (bitmap == null) {
                    bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.yellow[1]+CONST.imageSuffix);
                }
            }else if (dto.color.equals(CONST.orange[0])) {
                bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.orange[1]+CONST.imageSuffix);
                if (bitmap == null) {
                    bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.orange[1]+CONST.imageSuffix);
                }
            }else if (dto.color.equals(CONST.red[0])) {
                bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.red[1]+CONST.imageSuffix);
                if (bitmap == null) {
                    bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.red[1]+CONST.imageSuffix);
                }
            }
            ivMarker.setImageBitmap(bitmap);
            optionsTemp.icon(BitmapDescriptorFactory.fromView(mView));

            Marker marker = aMap.addMarker(optionsTemp);
            warningMarkers.add(marker);
            ScaleAnimation animation = new ScaleAnimation(0,1,0,1);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(300);
            marker.setAnimation(animation);
            marker.startAnimation();
        }
    }

    /**
     * 初始化预警统计列表
     */
    private void initWarningListView() {
        warningListView = findViewById(R.id.warningListView);
        warningAdapter = new WarningStatisticAdapter(mContext, warningStatistics);
        warningListView.setAdapter(warningAdapter);
        warningListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                warningListViewAnimation();
            }
        });
    }

    /**
     * 预警列表动画
     */
    private void warningListViewAnimation() {
        int height = CommonUtil.getListViewHeightBasedOnChildren(warningListView);
        if (!isShowWarningList) {
            ivWarningArrow.setImageResource(R.drawable.shawn_icon_arrow_top);
            hideOrShowListViewAnimator(warningListView, 0, height);
        }else {
            ivWarningArrow.setImageResource(R.drawable.shawn_icon_arrow_bottom);
            hideOrShowListViewAnimator(warningListView, height, 0);
        }
        isShowWarningList = !isShowWarningList;
    }

    /**
     * 隐藏或显示ListView的动画
     */
    public void hideOrShowListViewAnimator(final View view, final int startValue,final int endValue){
        //1.设置属性的初始值和结束值
        ValueAnimator mAnimator = ValueAnimator.ofInt(0,100);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatorValue = (Integer) animation.getAnimatedValue();
                float fraction = animatorValue/100f;
                IntEvaluator mEvaluator = new IntEvaluator();
                //3.使用IntEvaluator计算属性值并赋值给ListView的高
                view.getLayoutParams().height = mEvaluator.evaluate(fraction, startValue, endValue);
                view.requestLayout();
            }
        });
        //4.为ValueAnimator设置LinearInterpolator
        mAnimator.setInterpolator(new LinearInterpolator());
        //5.设置动画的持续时间
        mAnimator.setDuration(200);
        //6.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(view);
        mAnimator.start();
    }


    //分钟降水
    /**
     * 通过经纬度获取地理位置信息
     * @param lat
     * @param lng
     */
    private void searchAddrByLatLng(double lat, double lng) {
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
            String addr = result.getRegeocodeAddress().getFormatAddress();
            if (!TextUtils.isEmpty(addr)) {
                tvAddr.setText(addr);
            }
        }
    }

    /**
     * 异步加载一小时内降雨、或降雪信息
     * @param lng
     * @param lat
     */
    private void OkHttpMinute(double lng, double lat) {
        loadingView.setVisibility(View.VISIBLE);
        final String url = String.format("http://api.caiyunapp.com/v2/HyTVV5YAkoxlQ3Zd/%s,%s/forecast", lng, lat);
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
                                        if (!object.isNull("result")) {
                                            JSONObject obj = object.getJSONObject("result");
                                            if (!obj.isNull("minutely")) {
                                                JSONObject objMin = obj.getJSONObject("minutely");
                                                if (!objMin.isNull("description")) {
                                                    String rain = objMin.getString("description");
                                                    if (!TextUtils.isEmpty(rain)) {
                                                        tvRain.setText(rain.replace("小彩云", ""));
                                                        tvRain.setVisibility(View.VISIBLE);
                                                    }else {
                                                        tvRain.setVisibility(View.GONE);
                                                    }
                                                }
                                                if (!objMin.isNull("precipitation_2h")) {
                                                    JSONArray array = objMin.getJSONArray("precipitation_2h");
                                                    List<WeatherDto> minuteList = new ArrayList<>();
                                                    for (int i = 0; i < array.length(); i++) {
                                                        WeatherDto dto = new WeatherDto();
                                                        dto.minuteFall = (float) array.getDouble(i);
                                                        minuteList.add(dto);
                                                    }

                                                    llContainerMinute.removeAllViews();
                                                    MinuteFallView minuteFallView = new MinuteFallView(mContext);
                                                    minuteFallView.setData(minuteList, tvRain.getText().toString());
                                                    llContainerMinute.addView(minuteFallView, width, (int)(CommonUtil.dip2px(mContext, 120)));
                                                }

                                                llMinutePrompt.setVisibility(View.VISIBLE);

                                            }
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
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
     * 预警列表动画
     */
    private void minuteChartAnimation() {
        int height = (int)CommonUtil.dip2px(mContext, 120);//分钟降水曲线图高度
        if (!isShowMinutePrompt) {
            ivMinuteArrow.setImageResource(R.drawable.shawn_icon_arrow_top_white);
            hideOrShowListViewAnimator(llContainerMinute, 0, height);
        }else {
            ivMinuteArrow.setImageResource(R.drawable.shawn_icon_arrow_bottom_white);
            hideOrShowListViewAnimator(llContainerMinute, height, 0);
        }
        isShowMinutePrompt = !isShowMinutePrompt;
    }


    //全球风场
    private void OkHttpGFS() {
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.windGFS(dataHeight)).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        String result = response.body().string();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (windDataGFS == null) {
                                    windDataGFS = new WindData();
                                }
                                if (!obj.isNull("gridHeight")) {
                                    windDataGFS.height = obj.getInt("gridHeight");
                                }
                                if (!obj.isNull("gridWidth")) {
                                    windDataGFS.width = obj.getInt("gridWidth");
                                }
                                if (!obj.isNull("x0")) {
                                    windDataGFS.x0 = obj.getDouble("x0");
                                }
                                if (!obj.isNull("y0")) {
                                    windDataGFS.y0 = obj.getDouble("y0");
                                }
                                if (!obj.isNull("x1")) {
                                    windDataGFS.x1 = obj.getDouble("x1");
                                }
                                if (!obj.isNull("y1")) {
                                    windDataGFS.y1 = obj.getDouble("y1");
                                }
                                if (!obj.isNull("filetime")) {
                                    windDataGFS.filetime = obj.getString("filetime");
                                }

                                if (!obj.isNull("field")) {
                                    windDataGFS.dataList.clear();
                                    JSONArray array = new JSONArray(obj.getString("field"));
                                    for (int i = 0; i < array.length(); i += 2) {
                                        WindDto dto2 = new WindDto();
                                        dto2.initX = (float) (array.optDouble(i));
                                        dto2.initY = (float) (array.optDouble(i + 1));
                                        windDataGFS.dataList.add(dto2);
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingView.setVisibility(View.GONE);
                                        reloadWind();
                                    }
                                });

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void OkHttpT639() {
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.windT639(dataHeight, "0")).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        String result = response.body().string();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (windDataT639 == null) {
                                    windDataT639 = new WindData();
                                }
                                if (!obj.isNull("gridHeight")) {
                                    windDataT639.height = obj.getInt("gridHeight");
                                }
                                if (!obj.isNull("gridWidth")) {
                                    windDataT639.width = obj.getInt("gridWidth");
                                }
                                if (!obj.isNull("x0")) {
                                    windDataT639.x0 = obj.getDouble("x0");
                                }
                                if (!obj.isNull("y0")) {
                                    windDataT639.y0 = obj.getDouble("y0");
                                }
                                if (!obj.isNull("x1")) {
                                    windDataT639.x1 = obj.getDouble("x1");
                                }
                                if (!obj.isNull("y1")) {
                                    windDataT639.y1 = obj.getDouble("y1");
                                }
                                if (!obj.isNull("filetime")) {
                                    windDataT639.filetime = obj.getString("filetime");
                                }

                                if (!obj.isNull("field")) {
                                    windDataT639.dataList.clear();
                                    JSONArray array = new JSONArray(obj.getString("field"));
                                    for (int i = 0; i < array.length(); i += 2) {
                                        WindDto dto2 = new WindDto();
                                        dto2.initX = (float) (array.optDouble(i));
                                        dto2.initY = (float) (array.optDouble(i + 1));
                                        windDataT639.dataList.add(dto2);
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingView.setVisibility(View.GONE);
                                        reloadWind();
                                    }
                                });

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
        if (isShowWind) {
            windContainer1.removeAllViews();
            windContainer2.removeAllViews();
            tvWindTime.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        if (isShowWind) {
            reloadWind();
        }
    }

    long t = new Date().getTime();
    /**
     * 重新加载风场
     */
    private void reloadWind() {
        t = new Date().getTime() - t;
        if (t < 1000) {
            return;
        }

        LatLng latLngStart = aMap.getProjection().fromScreenLocation(new Point(0, 0));
        LatLng latLngEnd = aMap.getProjection().fromScreenLocation(new Point(width, height));
        Log.e("latLng", latLngStart.latitude+","+latLngStart.longitude+"\n"+latLngEnd.latitude+","+latLngEnd.longitude);
        if (isGfs) {
            windDataGFS.latLngStart = latLngStart;
            windDataGFS.latLngEnd = latLngEnd;
        }else {
            windDataT639.latLngStart = latLngStart;
            windDataT639.latLngEnd = latLngEnd;
        }
        if (waitWindView == null) {
            waitWindView = new WaitWindView2(mContext);
            waitWindView.init(ShawnMainActivity.this);
            if (isGfs) {
                waitWindView.setData(windDataGFS);
            }else {
                waitWindView.setData(windDataT639);
            }
            waitWindView.start();
            waitWindView.invalidate();
        }else {
            if (isGfs) {
                waitWindView.setData(windDataGFS);
            }else {
                waitWindView.setData(windDataT639);
            }
        }

        windContainer2.removeAllViews();
        windContainer1.removeAllViews();
        windContainer1.addView(waitWindView);
        tvWindTime.setVisibility(View.VISIBLE);
        String time;
        if (isGfs) {
            time = windDataGFS.filetime;
            if (!TextUtils.isEmpty(time)) {
                try {
                    tvWindTime.setText("GFS "+sdf3.format(sdf2.parse(time)) + "风场预报");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else {
            time = windDataT639.filetime;
            if (!TextUtils.isEmpty(time)) {
                try {
                    tvWindTime.setText("T639 "+sdf3.format(sdf2.parse(time)) + "风场预报");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llMenu:
                scaleAnimation = !scaleAnimation;
                if (scaleAnimation) {
                    enlargeAnimation();
                }else {
                    narrowAnimation();
                }
                break;

                //台风
            case R.id.llTyphoon:
                isShowTyphoon = !isShowTyphoon;
                if (isShowTyphoon) {
                    lyoutTyphoon.setVisibility(View.VISIBLE);
                    llTyphoon.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivTyphoon.setImageResource(R.drawable.shawn_icon_typhoon_press);
                    tvTyphoon.setTextColor(Color.WHITE);

                    String typhoonName = "";
                    for (int i = startList.size()-1; i >= 0; i--) {
                        TyphoonDto dto = startList.get(i);
                        String name;
                        if (TextUtils.equals(dto.enName, "nameless")) {
                            if (!TextUtils.isEmpty(typhoonName)) {
                                typhoonName = dto.enName+"\n"+typhoonName;
                            }else {
                                typhoonName = dto.enName;
                            }
                            name = dto.code+" "+dto.enName;
                        }else {
                            if (!TextUtils.isEmpty(typhoonName)) {
                                typhoonName = dto.code+" "+dto.name+" "+dto.enName+"\n"+typhoonName;
                            }else {
                                typhoonName = dto.code+" "+dto.name+" "+dto.enName;
                            }
                            name = dto.code+" "+dto.name+" "+dto.enName;
                        }
                        String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/"+dto.id;
                        OkHttpTyphoonDetail(dto.status, dto.id, url, name);
                    }
                    if (!TextUtils.isEmpty(typhoonName)) {
                        tvTyphoonName.setText(typhoonName);
                    }
                    if (startList.size() <= 0) {// 没有生效台风
                        tvTyphoonName.setText(getString(R.string.no_typhoon));
                    }
                    tvTyphoonName.setVisibility(View.VISIBLE);
                }else {
                    lyoutTyphoon.setVisibility(View.GONE);
                    llTyphoon.setBackgroundColor(0xc0ffffff);
                    ivTyphoon.setImageResource(R.drawable.shawn_icon_typhoon);
                    tvTyphoon.setTextColor(getResources().getColor(R.color.text_color3));
                    clearAllPoints();
                }
                break;
            case R.id.ivRange:
                isRanging = !isRanging;
                if (isRanging) {
                    ivRange.setImageResource(R.drawable.shawn_icon_range_press);
                    addLocationMarker();
                    addLocationCircles();
                    ranging();
                }else {
                    ivRange.setImageResource(R.drawable.shawn_icon_range);
                    removeRange();
                    removeLocationCirces();
                }
                break;
            case R.id.tvCurrent:
            case R.id.tvHistory:
                if (startListView.getVisibility() == View.VISIBLE) {
                    tvCurrent.setBackgroundResource(R.drawable.shawn_bg_current);
                    tvHistory.setBackgroundResource(R.drawable.shawn_bg_history_press);
                    tvCurrent.setTextColor(getResources().getColor(R.color.text_color3));
                    tvHistory.setTextColor(Color.WHITE);
                    startListView.setVisibility(View.GONE);
                    yearListView.setVisibility(View.VISIBLE);
                    nameListView.setVisibility(View.VISIBLE);
                }else {
                    tvCurrent.setBackgroundResource(R.drawable.shawn_bg_current_press);
                    tvHistory.setBackgroundResource(R.drawable.shawn_bg_history);
                    tvCurrent.setTextColor(Color.WHITE);
                    tvHistory.setTextColor(getResources().getColor(R.color.text_color3));
                    startListView.setVisibility(View.VISIBLE);
                    yearListView.setVisibility(View.GONE);
                    nameListView.setVisibility(View.GONE);
                }
                break;
            case R.id.ivList:
            case R.id.ivTyphoonClose:
                if (reTyphoonList.getVisibility() == View.GONE) {
                    upDownAnimation(false, reTyphoonList);
                    reTyphoonList.setVisibility(View.VISIBLE);
                    ivList.setImageResource(R.drawable.shawn_icon_list_press);
                }else {
                    upDownAnimation(true, reTyphoonList);
                    reTyphoonList.setVisibility(View.GONE);
                    ivList.setImageResource(R.drawable.shawn_icon_list);
                }
                break;
            case R.id.ivLegend:
            case R.id.ivLegendClose:
                if (reLegend.getVisibility() == View.GONE) {
                    upDownAnimation(false, reLegend);
                    reLegend.setVisibility(View.VISIBLE);
                    ivLegend.setImageResource(R.drawable.shawn_icon_legend_press);
                }else {
                    upDownAnimation(true, reLegend);
                    reLegend.setVisibility(View.GONE);
                    ivLegend.setImageResource(R.drawable.shawn_icon_legend);
                }
                break;

                //天气实况
            case R.id.llFact:
                Toast.makeText(mContext, "开发中，敬请期待~~~", Toast.LENGTH_SHORT).show();
                break;

                //卫星拼图
            case R.id.llSatelite:
                isShowCloud = !isShowCloud;
                if (isShowCloud) {
                    llSatelite.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivSatelite.setImageResource(R.drawable.shawn_icon_satelite_press);
                    tvSatelite.setTextColor(Color.WHITE);
                    if (cloudBitmap == null) {
                        OkHttpCloudChart();
                    }else {
                        drawCloud(cloudBitmap);
                    }
                }else {
                    llSatelite.setBackgroundColor(0xc0ffffff);
                    ivSatelite.setImageResource(R.drawable.shawn_icon_satelite);
                    tvSatelite.setTextColor(getResources().getColor(R.color.text_color3));
                    removeCloud();
                }
                break;

                //雷达拼图
            case R.id.llRadar:
                isShowRadar = !isShowRadar;
                if (isShowRadar) {
                    llRadar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivRadar.setImageResource(R.drawable.shawn_icon_radar_press);
                    tvRadar.setTextColor(Color.WHITE);
                    if (caiyunList.size() <= 0) {
                        OkHttpCaiyun();
                    }else {
                        removeRadar();
                        caiyunThread = new CaiyunThread(caiyunList);
                        caiyunThread.start();
                    }
                }else {
                    llRadar.setBackgroundColor(0xc0ffffff);
                    ivRadar.setImageResource(R.drawable.shawn_icon_radar);
                    tvRadar.setTextColor(getResources().getColor(R.color.text_color3));
                    removeRadar();
                }
                break;

                //预警
            case R.id.llWarning:
                isShowWarning = !isShowWarning;
                if (isShowWarning) {
                    layoutWarning.setVisibility(View.VISIBLE);
                    llWarning.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivWarning.setImageResource(R.drawable.shawn_icon_warning_press);
                    tvWarning.setTextColor(Color.WHITE);
                    if (warningList.size() <= 0) {
                        OkHttpWarning();
                    }else {
                        addWarningMarkers();
                    }
                }else {
                    layoutWarning.setVisibility(View.GONE);
                    llWarning.setBackgroundColor(0xc0ffffff);
                    ivWarning.setImageResource(R.drawable.shawn_icon_warning);
                    tvWarning.setTextColor(getResources().getColor(R.color.text_color3));
                    removeWarningMarkers();
                }
                break;
            case R.id.llWarningPrompt:
            case R.id.ivWarningArrow:
                warningListViewAnimation();
                break;
            case R.id.tvWaringList:
                Intent intent = new Intent(mContext, ShawnWarningListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("warningList", (ArrayList<? extends Parcelable>) warningList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

                //天气预报
            case R.id.llFore:
                Toast.makeText(mContext, "开发中，敬请期待~~~", Toast.LENGTH_SHORT).show();
                break;

                //分钟降水
            case R.id.llMinute:
                isShowMinute = !isShowMinute;
                if (isShowMinute) {
                    layoutMinute.setVisibility(View.VISIBLE);
                    llMinute.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivMinute.setImageResource(R.drawable.shawn_icon_minute_press);
                    tvMinute.setTextColor(Color.WHITE);
                    if (locationLatLng != null) {
                        addLocationMarker();
                        searchAddrByLatLng(locationLatLng.latitude, locationLatLng.longitude);
                        OkHttpMinute(locationLatLng.longitude, locationLatLng.latitude);
                    }
                }else {
                    layoutMinute.setVisibility(View.GONE);
                    llMinute.setBackgroundColor(0xc0ffffff);
                    ivMinute.setImageResource(R.drawable.shawn_icon_minute);
                    tvMinute.setTextColor(getResources().getColor(R.color.text_color3));
                }
                break;
            case R.id.llMinutePrompt:
            case R.id.ivMinuteArrow:
                minuteChartAnimation();
                break;

                //风场
            case R.id.llWind:
                isShowWind = !isShowWind;
                if (isShowWind) {
                    layoutWind.setVisibility(View.VISIBLE);
                    llWind.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    ivWind.setImageResource(R.drawable.shawn_icon_wind_press);
                    tvWind.setTextColor(Color.WHITE);
                    if (windDataGFS == null) {
                        OkHttpGFS();
                    }else {
                        reloadWind();
                    }
                }else {
                    layoutWind.setVisibility(View.GONE);
                    llWind.setBackgroundColor(0xc0ffffff);
                    ivWind.setImageResource(R.drawable.shawn_icon_wind);
                    tvWind.setTextColor(getResources().getColor(R.color.text_color3));
                    windContainer2.removeAllViews();
                    windContainer1.removeAllViews();
                }
                break;

                //数值预报
            case R.id.llValue:
                Toast.makeText(mContext, "开发中，敬请期待~~~", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        removeRadar();
    }

    /**
     * 申请权限
     */
    private void checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            initAll();
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, AuthorityUtil.AUTHOR_LOCATION);
            }else {
                initAll();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAll();
                }else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        AuthorityUtil.intentAuthorSetting(this, "\""+getString(R.string.app_name)+"\""+"需要使用定位权限，是否前往设置？");
                    }
                }
                break;
        }
    }

//    /**
//     * 申请多个权限
//     */
//    private void checkMultiAuthority() {
//        if (Build.VERSION.SDK_INT < 23) {
//            initAll();
//        }else {
//            AuthorityUtil.deniedList.clear();
//            for (int i = 0; i < AuthorityUtil.allPermissions.length; i++) {
//                if (ContextCompat.checkSelfPermission(mContext, AuthorityUtil.allPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
//                    AuthorityUtil.deniedList.add(AuthorityUtil.allPermissions[i]);
//                }
//            }
//            if (AuthorityUtil.deniedList.isEmpty()) {//所有权限都授予
//                initAll();
//            }else {
//                String[] permissions = AuthorityUtil.deniedList.toArray(new String[AuthorityUtil.deniedList.size()]);//将list转成数组
//                ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_MULTI);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case AuthorityUtil.AUTHOR_MULTI:
//                initAll();
//                break;
//        }
//    }

}
