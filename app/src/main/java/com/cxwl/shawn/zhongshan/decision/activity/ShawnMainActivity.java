package com.cxwl.shawn.zhongshan.decision.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.amap.api.maps.AMapUtils;
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
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonNameAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonPublishAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.TyphoonStartAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WarningAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WarningMapTypeAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WarningStatisticAdapter;
import com.cxwl.shawn.zhongshan.decision.adapter.WindMapTypeAdapter;
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
import com.cxwl.shawn.zhongshan.decision.util.WeatherUtil;
import com.cxwl.shawn.zhongshan.decision.util.sofia.Sofia;
import com.cxwl.shawn.zhongshan.decision.view.MinuteFallView;
import com.cxwl.shawn.zhongshan.decision.view.ScrollviewGridview;
import com.cxwl.shawn.zhongshan.decision.view.WaitWindView2;
import com.suke.widget.SwitchButton;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShawnMainActivity extends ShawnBaseActivity implements View.OnClickListener, AMapLocationListener, AMap.OnMarkerClickListener,
        AMap.OnMapClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {

    private Context mContext;
    private AVLoadingIndicatorView loadingView;
    private ScrollView scrollView;
    private boolean scaleAnimation = true;
    private TextureMapView mapView;
    private AMap aMap;//高德地图
    private int AMapType = AMap.MAP_TYPE_SATELLITE;
    private float zoom = 4.0f;
    private Bundle savedInstanceState;
    private LinearLayout llBack,llMenu,llTyphoon,llFact,llSatelite,llRadar,llWarning,llFore,llMinute,llWind,llValue;
    private ImageView ivBack,ivRefresh,ivMenu,ivTyphoon,ivFact,ivSatelite,ivRadar,ivWarning,ivFore,ivMinute,ivWind,ivValue;
    private TextView tvBack,tvTyphoonName,tvTyphoon,tvFact,tvSatelite,tvRadar,tvWarning,tvFore,tvMinute,tvWind,tvValue;
    private AMapLocationClientOption mLocationOption;//声明mLocationOption对象
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private String locationAdcode = "442000", clickAdcode = "442000";//定位点对应行政区划
    private LatLng locationLatLng = new LatLng(22.519470, 113.356614),clickLatLng = new LatLng(22.519470, 113.356614);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf5 = new SimpleDateFormat("MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf7 = new SimpleDateFormat("HH", Locale.CHINA);
    private SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
    private String TYPE_TYPHOON = "type_typhoon", TYPE_WARNING= "type_warning", TYPE_FORE = "type_fore";
    private int width, height;

    //台风
    private boolean isShowTyphoon = true;//是否显示台风layout
    private String selectPublishName, selectPublishCode;//选中的台风数据源
    private ListView startListView;
    private TyphoonStartAdapter startAdapter;
    private List<TyphoonDto> startList = new ArrayList<>();//所有活跃台风
    private List<TyphoonDto> selectList = new ArrayList<>();//被选中的台风，支持多选功能
    private ListView publishListView;
    private TyphoonPublishAdapter publishAdapter;
    private List<TyphoonDto> publishList = new ArrayList<>();
    private ListView nameListView;
    private TyphoonNameAdapter nameAdapter;
    private List<TyphoonDto> nameList = new ArrayList<>();//某一年所有台风
    private RoadThread mRoadThread;//绘制台风点的线程
    private Circle circle7, circle10;//风圈

    private Map<String, List<Polyline>> factLinesMap = new HashMap<>();//实线数据
    private Map<String, List<Polyline>> foreLinesMap = new HashMap<>();//虚线数据
    private Map<String, List<Marker>> markerPointsMap = new HashMap<>();//台风点数据
    private Map<String, Marker> rotateMarkersMap = new HashMap<>();//台风旋转markers
    private Map<String, Marker> factTimeMarkersMap = new HashMap<>();//最后一个实况点时间markers
    private Map<String, Marker> foreTimeMarkersMap = new HashMap<>();//预报点时间markers
    private Map<String, List<Polyline>> rangeLinesMap = new HashMap<>();//测距虚线数据
    private Map<String, Marker> rangeMarkersMap = new HashMap<>();//测距中点距离marker
    private Map<String, TyphoonDto> lastFactPointMap = new HashMap<>();//最后一个实况点数据集合

    private List<Polygon> windCirclePolygons = new ArrayList<>();//fengquan
    private boolean isShowInfoWindow = true;//是否显示气泡
    private boolean isShowTime = false;//是否显示台风实况、预报时间
    private boolean isRanging = false;//是否允许测距
    private Marker locationMarker,clickMarker;
    private final int DRAW_TYPHOON_COMPLETE = 1002;//一个台风绘制结束
    private Circle circle100, circle300, circle500;//定位点对一个的区域圈
    private Text text100, text300, text500;//定位点对一个的区域圈文字
    private ImageView ivList,ivRange,ivTyphoonClose,ivLegend,ivLegendClose,ivLocation;
    private TextView tvCurrent,tvHistory;
    private RelativeLayout lyoutTyphoon,reTyphoonList,reLegend;
    private String typhoonPointAddr = "";

    //更多
    private ImageView ivMore,ivMapType1,ivMapType2;
    private ScrollView scrollViewMore;
    private boolean isShowMore = false;//是否显示更多layout
    private ScrollviewGridview gridviewWarning;
    private WarningMapTypeAdapter warningMapTypeAdapter;
    private List<WarningDto> warningTypeList = new ArrayList<>();
    private String selectWarningTypes = "";//选中预警类型
    private ScrollviewGridview gridviewWind;
    private WindMapTypeAdapter windMapTypeAdapter;
    private List<WindDto> windTypeList = new ArrayList<>();
    private TextView tvWarningTypeStr,tvWarningListStr,tvWindTypeStr,tvMinuteStr;//预警类型字符串，风场数据字符串
    private SwitchButton sbWarning,sbMinute;

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
    private ImageView ivWarningClose;
    private List<WarningDto> warningList = new ArrayList<>();
    private Map<String, Marker> warningMarkers = new LinkedHashMap<>();//预警html为key
    private TextView tvWarningPrompt,tvWaringList;
    private LinearLayout llWarningContainer;
    private ListView warningListView;
    private WarningStatisticAdapter warningAdapter;
    private List<WarningDto> warningStatistics = new ArrayList<>();//统计
    private RelativeLayout reWarningPrompt,layoutWarning;
    private Map<String, List<List<Polygon>>> warningPolaygonsMap = new LinkedHashMap<>();//预警html为key

    //天气预报
    private boolean isShowFore = false;//是否显示天气预报layout
    private Map<String, WeatherDto> allCitysMap = new HashMap<>();//所有城市信息
    private Map<String, Marker> allCitysMarkerMap = new HashMap<>();//所有城市天气信息marker
    private LatLng leftlatlng,rightLatlng;

    //分钟降水
    private boolean isShowMinute = false;//是否显示分钟降水layout
    private ImageView ivMinuteClose;
    private RelativeLayout layoutMinute,reMinutePrompt;
    private LinearLayout llContainerMinute;//分钟降水曲线图
    private TextView tvAddr,tvRain;//地址
    private GeocodeSearch geocoderSearch;

    //风场
    private boolean isShowWind = false;//是否显示风场layout
    private RelativeLayout layoutWind;
    private WaitWindView2 waitWindView;
    private boolean isGfs = true;//默认为GFS
    private WindData windDataGFS,windDataT639;
    private RelativeLayout windContainer1;
    public RelativeLayout windContainer2;
    private TextView tvWindTime;//风场数据时间
    private String windHeight = "1000";//200、500、1000pha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.shawn_activity_main);
        mContext = this;
        Sofia.with(this)
                .invasionStatusBar()//设置顶部状态栏缩进
                .statusBarBackground(Color.TRANSPARENT);//设置状态栏颜色
        checkMultiAuthority();
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
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.926628, 105.178100), zoom));
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setMapType(AMapType);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                drawWarningLines();

                if (CommonUtil.isLocationOpen(mContext)) {
                    startLocation();
                }else {
                    getSharedPreferences();
                }
            }
        });
    }

    /**
     * 读取设置
     */
    private void getSharedPreferences() {
        SharedPreferences sp = getSharedPreferences("zhongshan", Context.MODE_PRIVATE);

        if (sp.contains("adCode")) {
            String adCode = sp.getString("adCode", "");
            if (!TextUtils.isEmpty(adCode)) {
                clickAdcode = adCode;
            }
        }

        if (sp.contains("lat") && sp.contains("lng")) {
            double lat = sp.getLong("lat", 0);
            double lng = sp.getLong("lng", 0);
            if (lat != 0 && lng != 0) {
                clickLatLng = new LatLng(lat, lng);
            }
        }
    }

    /**
     * 保存设置
     */
    private void saveSharedPreferences() {
        SharedPreferences sp = getSharedPreferences("zhongshan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!TextUtils.isEmpty(clickAdcode)) {
            editor.putString("adCode", clickAdcode);
        }

        if (clickLatLng != null) {
            editor.putLong("lat", (long)clickLatLng.latitude);
            editor.putLong("lng", (long)clickLatLng.longitude);
        }

        editor.apply();
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
            locationAdcode = amapLocation.getAdCode();
            clickAdcode = locationAdcode;
            locationLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            clickLatLng = locationLatLng;
            addLocationMarker();
            saveSharedPreferences();
        }
    }

    private void removeLocationMarker() {
        if (locationMarker != null) {
            locationMarker.remove();
            locationMarker = null;
        }
    }

    private void addLocationMarker() {
        if (clickLatLng == null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(clickLatLng);
        options.anchor(0.5f, 1.0f);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), R.drawable.shawn_iv_map_click_map),
                (int)(CommonUtil.dip2px(mContext, 21)), (int)(CommonUtil.dip2px(mContext, 32)));
        if (bitmap != null) {
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }else {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.shawn_iv_map_click_map));
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
        ivBack = findViewById(R.id.ivBack);
        tvBack = findViewById(R.id.tvBack);
        ivRefresh = findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(this);
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
        ivLocation = findViewById(R.id.ivLocation);
        ivLocation.setOnClickListener(this);
        lyoutTyphoon = findViewById(R.id.lyoutTyphoon);

        //更多
        ivMore = findViewById(R.id.ivMore);
        ivMore.setOnClickListener(this);
        scrollViewMore = findViewById(R.id.scrollViewMore);
        ivMapType1 = findViewById(R.id.ivMapType1);
        ivMapType1.setOnClickListener(this);
        ivMapType2 = findViewById(R.id.ivMapType2);
        ivMapType2.setOnClickListener(this);
        tvWarningTypeStr = findViewById(R.id.tvWarningTypeStr);
        tvWarningListStr = findViewById(R.id.tvWarningListStr);
        tvWindTypeStr = findViewById(R.id.tvWindTypeStr);
        tvMinuteStr = findViewById(R.id.tvMinuteStr);
        sbWarning = findViewById(R.id.sbWarning);
        sbMinute = findViewById(R.id.sbMinute);
        sbWarning.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    animationDownToUp(reWarningPrompt);
                }else {
                    animationUpToDown(reWarningPrompt);
                }
            }
        });
        sbMinute.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    animationDownToUp(reMinutePrompt);
                }else {
                    animationUpToDown(reMinutePrompt);
                }
            }
        });

        //雷达拼图
        caiyunManager = new CaiyunManager(mContext);

        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);

        //预警
        reWarningPrompt = findViewById(R.id.reWarningPrompt);
        tvWarningPrompt = findViewById(R.id.tvWarningPrompt);
        tvWaringList = findViewById(R.id.tvWaringList);
        tvWaringList.setOnClickListener(this);
        llWarningContainer = findViewById(R.id.llWarningContainer);
        layoutWarning = findViewById(R.id.layoutWarning);
        ivWarningClose = findViewById(R.id.ivWarningClose);
        ivWarningClose.setOnClickListener(this);

        //分钟降水
        layoutMinute = findViewById(R.id.layoutMinute);
        reMinutePrompt = findViewById(R.id.reMinutePrompt);
        llContainerMinute = findViewById(R.id.llContainerMinute);
        tvAddr = findViewById(R.id.tvAddr);
        tvRain = findViewById(R.id.tvRain);
        ivMinuteClose = findViewById(R.id.ivMinuteClose);
        ivMinuteClose.setOnClickListener(this);

        //风场
        layoutWind = findViewById(R.id.layoutWind);
        windContainer1 = findViewById(R.id.windContainer1);
        windContainer2 = findViewById(R.id.windContainer2);
        tvWindTime = findViewById(R.id.tvWindTime);
    }

    /**
     * 左移动画
     */
    private void toLeftAnimation() {
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
     * 右移动画
     */
    private void toRightAnimation() {
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
     * 缩小动画
     */
    private void narrowAnimation(final View view) {
        android.view.animation.ScaleAnimation animation = new android.view.animation.ScaleAnimation(
                1.0f, 0.0f, 1.0f, 0.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 1.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 1.0f
        );
        animation.setDuration(200);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 放大动画
     */
    private void enlargeAnimation(final View view) {
        android.view.animation.ScaleAnimation animation = new android.view.animation.ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 1.0f, android.view.animation.Animation.RELATIVE_TO_SELF, 1.0f
        );
        animation.setDuration(200);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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
        if (clickLatLng == null) {
            return;
        }
        if (isRanging) {
            circle100 = aMap.addCircle(new CircleOptions().center(clickLatLng)
                    .radius(100000).strokeColor(0x90ff6c00).strokeWidth(4));

            circle300 = aMap.addCircle(new CircleOptions().center(clickLatLng)
                    .radius(300000).strokeColor(0x90ffd800).strokeWidth(4));

            circle500 = aMap.addCircle(new CircleOptions().center(clickLatLng)
                    .radius(500000).strokeColor(0x9000b4ff).strokeWidth(4));

            text100 = addCircleText(clickLatLng, 100000, 0xffff6c00, "100km");

            text300 = addCircleText(clickLatLng, 300000, 0xffffd800, "300km");

            text500 = addCircleText(clickLatLng, 500000, 0xff00b4ff, "500km");
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
     * 初始化台风发布单位列表
     */
    private void initYearListView() {
        publishList.clear();
        TyphoonDto dto = new TyphoonDto();
        dto.publishName = "广州";
        dto.publishCode = "BCGZ";
        dto.isSelected = true;
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "北京";
        dto.publishCode = "BABJ";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "香港";
        dto.publishCode = "VHHH";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "日本";
        dto.publishCode = "RJTD";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "关岛";
        dto.publishCode = "PGTW";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "欧洲";
        dto.publishCode = "ECMF";
        publishList.add(dto);
//        dto = new TyphoonDto();
//        dto.publishName = "广州热带所KM";
//        dto.publishCode = "GZRD";
//        publishList.add(dto);
//        dto = new TyphoonDto();
//        dto.publishName = "广州热带所9KM";
//        dto.publishCode = "GZRD9KM";
//        publishList.add(dto);


        publishListView = findViewById(R.id.publishListView);
        publishAdapter = new TyphoonPublishAdapter(mContext, publishList);
        publishListView.setAdapter(publishAdapter);
        publishListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TyphoonDto data = publishList.get(position);
                if (!TextUtils.equals(selectPublishCode, data.publishCode)) {//选择数据源如果是同一个就不重新绘制了
                    selectPublishName = data.publishName;
                    selectPublishCode = data.publishCode;

                    for (TyphoonDto dto : publishList) {
                        if (TextUtils.equals(dto.publishCode, data.publishCode)) {
                            dto.isSelected = true;
                        }else {
                            dto.isSelected = false;
                        }
                    }
                    if (publishAdapter != null) {
                        publishAdapter.notifyDataSetChanged();
                    }

                    //绘制所有选中的台风
                    clearAllPoints(null);
                    for (TyphoonDto dto : selectList) {
                        isShowInfoWindow = true;
                        String name = dto.code+" "+dto.name+" "+dto.enName;
                        OkHttpTyphoonDetail(dto.status, dto.id, name);
                    }

                }
            }
        });

        selectPublishName = publishList.get(0).publishName;
        selectPublishCode = publishList.get(0).publishCode;
        OkHttpTyphoonList();
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
                TyphoonDto dto = nameList.get(arg2);
                dto.isSelected = !dto.isSelected;
                if (nameAdapter != null) {
                    nameAdapter.notifyDataSetChanged();
                }

                String name = dto.code+" "+dto.name+" "+dto.enName;


                //所有选中的台风
                if (dto.isSelected) {
                    selectList.add(0, dto);

                    isShowInfoWindow = true;
                    OkHttpTyphoonDetail(dto.status, dto.id, name);
                }else {
                    clearAllPoints(dto.id);
                    selectList.remove(dto);
                }

                String typhoonName = "";
                for (TyphoonDto select : selectList) {
                    if (!TextUtils.isEmpty(typhoonName)) {
                        typhoonName = select.code+" "+select.name+" "+select.enName+"\n"+typhoonName;
                    }else {
                        typhoonName = select.code+" "+select.name+" "+select.enName;
                    }
                }
                tvTyphoonName.setText(typhoonName);

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
                tvTyphoonName.setText(dto.code+" "+dto.name+" "+dto.enName);

                clearAllPoints(null);
                isShowInfoWindow = true;
                OkHttpTyphoonDetail(dto.status, dto.id, tvTyphoonName.getText().toString());

            }
        });
    }

    /**
     * 获取某一年的台风列表信息
     */
    private void OkHttpTyphoonList() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://61.142.114.104:8080/zstyphoon/lhdata/zstf?type=0";
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
                        if (TextUtils.isEmpty(result)) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("DATA")) {
                                        nameList.clear();
                                        selectList.clear();
                                        JSONArray array = obj.getJSONArray("DATA");
                                        for (int i = 0; i < array.length(); i++) {
                                            TyphoonDto dto = new TyphoonDto();
                                            JSONObject itemObj = array.getJSONObject(i);
                                            if (!itemObj.isNull("TSID")) {
                                                dto.id = itemObj.getString("TSID");
                                            }
                                            if (!itemObj.isNull("TSENAME")) {
                                                dto.enName = itemObj.getString("TSENAME");
                                            }
                                            if (!itemObj.isNull("TSCNAME")) {
                                                dto.name = itemObj.getString("TSCNAME");
                                            }
                                            if (!itemObj.isNull("INTLID")) {
                                                dto.code = itemObj.getString("INTLID");
                                            }
                                            if (!itemObj.isNull("status")) {
                                                dto.status = itemObj.getString("status");
                                                dto.isSelected = true;//生效台风默认选中状态
                                            }else {
                                                dto.status = "0";
                                            }
                                            nameList.add(dto);

                                            //把活跃台风过滤出来存放
                                            if (TextUtils.equals(dto.status, "1")) {
                                                dto.isSelected = true;//生效台风默认选中状态
                                                startList.add(0, dto);
                                                selectList.add(0, dto);
                                            }
                                        }

                                        String typhoonName = "";
                                        for (TyphoonDto dto : startList) {
                                            if (!TextUtils.isEmpty(typhoonName)) {
                                                typhoonName = dto.code+" "+dto.name+" "+dto.enName+"\n"+typhoonName;
                                            }else {
                                                typhoonName = dto.code+" "+dto.name+" "+dto.enName;
                                            }
                                            String name = dto.code+" "+dto.name+" "+dto.enName;
                                            OkHttpTyphoonDetail(dto.status, dto.id, name);
                                        }

                                        if (!TextUtils.isEmpty(typhoonName)) {
                                            tvTyphoonName.setText(typhoonName);
                                        }
                                        if (startList.size() <= 0) {// 没有生效台风
                                            tvTyphoonName.setText(getString(R.string.no_typhoon));
                                        }
                                        tvTyphoonName.setVisibility(View.VISIBLE);

                                        if (startAdapter != null) {
                                            startAdapter.notifyDataSetChanged();
                                        }

                                        if (nameAdapter != null) {
                                            nameAdapter.notifyDataSetChanged();
                                        }

                                        loadingView.setVisibility(View.GONE);
                                        lyoutTyphoon.setVisibility(View.VISIBLE);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取台风详情，增加缓存机制
     */
    private void OkHttpTyphoonDetail(String status, final String typhoonId, final String typhoonName) {
        loadingView.setVisibility(View.VISIBLE);
        final String url = String.format("http://61.142.114.104:8080/zstyphoon/lhdata/zstf?type=1&tsid=%s&fcid=%s", typhoonId, selectPublishCode);
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
                        if (TextUtils.isEmpty(result) || TextUtils.equals(result, "{}")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingView.setVisibility(View.GONE);
                                    lyoutTyphoon.setVisibility(View.VISIBLE);
                                    if (!TextUtils.isEmpty(typhoonName)) {
                                        Toast.makeText(mContext, "暂无"+selectPublishName+typhoonName+"数据", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<TyphoonDto> allPoints = new ArrayList<>();
                                    JSONArray array = new JSONArray(result);
                                    if (array.length() <= 0) {
                                        loadingView.setVisibility(View.GONE);
                                        lyoutTyphoon.setVisibility(View.VISIBLE);
                                        if (!TextUtils.isEmpty(typhoonName)) {
                                            Toast.makeText(mContext, "暂无"+selectPublishName+typhoonName+"数据", Toast.LENGTH_SHORT).show();
                                        }
                                        return;
                                    }

                                    for (int i = 0; i < array.length(); i++) {
                                        TyphoonDto dto = new TyphoonDto();
                                        JSONObject itemObj = array.getJSONObject(i);
                                        if (!TextUtils.isEmpty(typhoonName)) {
                                            dto.name = typhoonName;
                                        }
                                        if (!itemObj.isNull("DDATETIME") && !TextUtils.isEmpty(itemObj.getString("DDATETIME"))) {
                                            String time = itemObj.getString("DDATETIME");
                                            if (!TextUtils.isEmpty(time)) {
                                                try {
                                                    dto.time = sdf3.format(sdf8.parse(time));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        if (!itemObj.isNull("LONGITUDE") && !TextUtils.isEmpty(itemObj.getString("LONGITUDE"))) {
                                            dto.lng = itemObj.getDouble("LONGITUDE");
                                        }
                                        if (!itemObj.isNull("LATITUDE") && !TextUtils.isEmpty(itemObj.getString("LATITUDE"))) {
                                            dto.lat = itemObj.getDouble("LATITUDE");
                                        }
                                        if (!itemObj.isNull("PRESSURE") && !TextUtils.isEmpty(itemObj.getString("PRESSURE"))) {
                                            dto.pressure = itemObj.getString("PRESSURE");
                                        }
                                        if (!itemObj.isNull("WINDSPEED") && !TextUtils.isEmpty(itemObj.getString("WINDSPEED"))) {
                                            dto.max_wind_speed = itemObj.getString("WINDSPEED");
                                        }
                                        if (!itemObj.isNull("SPEED") && !TextUtils.isEmpty(itemObj.getString("SPEED"))) {
                                            dto.move_speed = itemObj.getString("SPEED");
                                        }
                                        if (!itemObj.isNull("DIRECTION") && !TextUtils.isEmpty(itemObj.getString("DIRECTION"))) {
                                            float fx = (float) itemObj.getDouble("DIRECTION");
                                            String wind_dir;
                                            if(fx >= 22.5 && fx < 67.5){
                                                wind_dir = "东北";
                                            }else if(fx >= 67.5 && fx < 112.5){
                                                wind_dir = "东";
                                            }else if(fx >= 112.5 && fx < 157.5){
                                                wind_dir = "东南";
                                            }else if(fx >= 157.5 && fx < 202.5){
                                                wind_dir = "南";
                                            }else if(fx >= 202.5 && fx < 247.5){
                                                wind_dir = "西南";
                                            }else if(fx >= 247.5 && fx < 292.5){
                                                wind_dir = "西";
                                            }else if(fx >= 292.5 && fx < 337.5){
                                                wind_dir = "西北";
                                            }else {
                                                wind_dir = "北";
                                            }
                                            dto.wind_dir = wind_dir;
                                        }
                                        if (!itemObj.isNull("TCRANK") && !TextUtils.isEmpty(itemObj.getString("TCRANK"))) {
                                            String type = itemObj.getString("TCRANK");
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
                                            }else if (TextUtils.equals(type, "SUPER TY")) {//超强台风
                                                type = "6";
                                            }
                                            dto.type = type;
                                        }
                                        if (!itemObj.isNull("TYPE") && !TextUtils.isEmpty(itemObj.getString("TYPE"))) {
                                            String isFactFore = itemObj.getString("TYPE");
                                            if (TextUtils.equals(isFactFore, "0")) {
                                                dto.isFactPoint = true;
                                            }else {
                                                dto.isFactPoint = false;
                                            }
                                        }
                                        if (!itemObj.isNull("RR07") && !TextUtils.isEmpty(itemObj.getString("RR07"))) {
                                            dto.radius_7 = itemObj.getString("RR07");
                                        }
                                        if (!itemObj.isNull("RR10") && !TextUtils.isEmpty(itemObj.getString("RR10"))) {
                                            dto.radius_10 = itemObj.getString("RR10");
                                        }
                                        allPoints.add(dto);
                                    }

                                    loadingView.setVisibility(View.GONE);
                                    lyoutTyphoon.setVisibility(View.VISIBLE);
                                    //防止多个台风绘制不全
                                    try {
                                        drawTyphoon(typhoonId,false, allPoints);
                                        Thread.sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });
            }
        }).start();
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
    private void removeRange(String tid) {
        if (!TextUtils.isEmpty(tid)) {
            //清除测距虚线
            if (rangeLinesMap.containsKey(tid)) {
                List<Polyline> polylines = rangeLinesMap.get(tid);
                for (Polyline polyline : polylines) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
                polylines.clear();
                rangeLinesMap.remove(tid);
            }

            //清除测距marker
            if (rangeMarkersMap.containsKey(tid)) {
                Marker marker = rangeMarkersMap.get(tid);
                if (marker != null) {
                    marker.remove();
                }
                rangeMarkersMap.remove(tid);
            }
        }else {
            //清除测距虚线
            for (String typhoonId : rangeLinesMap.keySet()) {
                if (rangeLinesMap.containsKey(typhoonId)) {
                    List<Polyline> polylines = rangeLinesMap.get(typhoonId);
                    for (Polyline polyline : polylines) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                    }
                    polylines.clear();
                }
            }
            rangeLinesMap.clear();

            //清除测距marker
            for (String typhoonId : rangeMarkersMap.keySet()) {
                if (rangeMarkersMap.containsKey(typhoonId)) {
                    Marker marker = rangeMarkersMap.get(typhoonId);
                    if (marker != null) {
                        marker.remove();
                    }
                }
            }
            rangeMarkersMap.clear();
        }

    }

    /**
     * 清除七级、十级风圈
     */
    private void removeWindCircle() {
//        for (Polygon polygon : windCirclePolygons) {
//            polygon.remove();
//        }
//        windCirclePolygons.clear();

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
    private void removeTyphoons(String tid) {
        if (!TextUtils.isEmpty(tid)) {
            //清除实况线段
            if (factLinesMap.containsKey(tid)) {
                List<Polyline> factLines = factLinesMap.get(tid);
                for (Polyline polyline : factLines) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
                factLines.clear();
                factLinesMap.remove(tid);
            }

            //清除虚线线段
            if (foreLinesMap.containsKey(tid)) {
                List<Polyline> dashLines = foreLinesMap.get(tid);
                for (Polyline polyline : dashLines) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
                dashLines.clear();
                foreLinesMap.remove(tid);
            }

            //清除所有台风点数据
            if (markerPointsMap.containsKey(tid)) {
                List<Marker> markers = markerPointsMap.get(tid);
                for (Marker marker : markers) {
                    if (marker != null) {
                        marker.remove();
                    }
                }
                markers.clear();
                markerPointsMap.remove(tid);
            }

            //清除所有台风旋转图标
            if (rotateMarkersMap.containsKey(tid)) {
                Marker rotateMarker = rotateMarkersMap.get(tid);
                if (rotateMarker != null) {
                    rotateMarker.remove();
                }
                rotateMarkersMap.remove(tid);
            }

            //清除所有台风最后一个实况点对应的时间marker
            if (factTimeMarkersMap.containsKey(tid)) {
                Marker factTimeMarker = factTimeMarkersMap.get(tid);
                if (factTimeMarker != null) {
                    factTimeMarker.remove();
                }
                factTimeMarkersMap.remove(tid);
            }

            //清除所有台风预报点赌赢的marker
            if (foreTimeMarkersMap.containsKey(tid)) {
                Marker foreTimeMrker = foreTimeMarkersMap.get(tid);
                if (foreTimeMrker != null) {
                    foreTimeMrker.remove();
                }
                foreTimeMarkersMap.remove(tid);
            }

            //清除实况最后一个点
            if (lastFactPointMap.containsKey(tid)) {
                lastFactPointMap.remove(tid);
            }
        }else {
            //清除实况线段
            for (String typhoonId : factLinesMap.keySet()) {
                if (factLinesMap.containsKey(typhoonId)) {
                    List<Polyline> polylines = factLinesMap.get(typhoonId);
                    for (Polyline polyline : polylines) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                    }
                    polylines.clear();
                }
            }
            factLinesMap.clear();

            //清除虚线线段
            for (String typhoonId : foreLinesMap.keySet()) {
                if (foreLinesMap.containsKey(typhoonId)) {
                    List<Polyline> polylines = foreLinesMap.get(typhoonId);
                    for (Polyline polyline : polylines) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                    }
                    polylines.clear();
                }
            }
            foreLinesMap.clear();

            //清除所有台风点数据
            for (String typhoonId : markerPointsMap.keySet()) {
                if (markerPointsMap.containsKey(typhoonId)) {
                    List<Marker> markers = markerPointsMap.get(typhoonId);
                    for (Marker marker : markers) {
                        if (marker != null) {
                            marker.remove();
                        }
                    }
                    markers.clear();
                }
            }
            markerPointsMap.clear();

            //清除所有台风旋转图标
            for (String typhoonId : rotateMarkersMap.keySet()) {
                if (rotateMarkersMap.containsKey(typhoonId)) {
                    Marker marker = rotateMarkersMap.get(typhoonId);
                    if (marker != null) {
                        marker.remove();
                    }
                }
            }
            rotateMarkersMap.clear();

            //清除所有台风最后一个实况点对应的时间marker
            for (String typhoonId : factTimeMarkersMap.keySet()) {
                if (factTimeMarkersMap.containsKey(typhoonId)) {
                    Marker marker = factTimeMarkersMap.get(typhoonId);
                    if (marker != null) {
                        marker.remove();
                    }
                }
            }
            factTimeMarkersMap.clear();

            //清除所有台风预报点赌赢的marker
            for (String typhoonId : foreTimeMarkersMap.keySet()) {
                if (foreTimeMarkersMap.containsKey(typhoonId)) {
                    Marker marker = foreTimeMarkersMap.get(typhoonId);
                    if (marker != null) {
                        marker.remove();
                    }
                }
            }
            foreTimeMarkersMap.clear();

            //清除实况最后一个点
            lastFactPointMap.clear();
        }

    }

    /**
     * 清除一个台风
     */
    private void clearAllPoints(String typhoonId) {
        removeLocationCirces();
        removeWindCircle();
        removeRange(typhoonId);
        removeTyphoons(typhoonId);
    }

    /**
     * 绘制台风
     * @param isAnimate
     */
    private void drawTyphoon(String typhoonId, boolean isAnimate, List<TyphoonDto> list) {
        if (list.isEmpty()) {
            return;
        }

        if (mRoadThread != null) {
            mRoadThread.cancel();
            mRoadThread = null;
        }
        mRoadThread = new RoadThread(typhoonId, list, isAnimate);
        mRoadThread.start();
    }

    /**
     * 绘制台风点
     */
    private class RoadThread extends Thread {

        private boolean cancelled;
        private boolean isAnimate;
        private List<TyphoonDto> allPoints;//整个台风路径信息
        private String typhoonId;

        private RoadThread(String typhoonId, List<TyphoonDto> allPoints, boolean isAnimate) {
            this.typhoonId = typhoonId;
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

            List<Polyline> factLines = new ArrayList<>();//实况线段
            List<Polyline> foreLines = new ArrayList<>();//预报线段
            List<Marker> markerPoints = new ArrayList<>();//台风点数据
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
                TyphoonDto lastFactPoint = null;//最后一个实况点
                if (factPoints.size() > 0) {
                    lastFactPoint = factPoints.get(factPoints.size()-1);//最后一个实况点
                }
                drawRoute(typhoonId, factLines, foreLines, markerPoints, firstPoint, lastPoint, lastFactPoint);
            }
            factLinesMap.put(typhoonId, factLines);
            foreLinesMap.put(typhoonId, foreLines);
            markerPointsMap.put(typhoonId, markerPoints);

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
    private void drawRoute(String typhoonId, List<Polyline> factLines, List<Polyline> foreLines, List<Marker> markerPoints, TyphoonDto firstPoint, TyphoonDto lastPoint, TyphoonDto lastFactPoint) {
        if (lastPoint == null) {//最后一个点
            lastPoint = firstPoint;
        }
        if (lastFactPoint == null) {
            lastFactPoint = firstPoint;
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
            Polyline factLine = aMap.addPolyline(polylineOptions);
            factLines.add(factLine);
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
                    foreLines.add(dashLine);
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
        String title1 = firstPoint.name+"|"+firstPoint.content(mContext)+";";
        String title2 = firstPoint.move_speed+"|"+lastPoint.move_speed+"|"+lastPoint.time+"|"+firstPoint.wind_dir+";";//前后两个点速度，为计算强度字符串所用
        String title3 = firstPoint.radius_7+"|"+firstPoint.radius_10;
        options.title(title1+title2+title3);
        options.snippet(TYPE_TYPHOON);
        options.anchor(0.5f, 0.5f);
        options.position(firstLatLng);
        options.icon(BitmapDescriptorFactory.fromView(typhoonPoint));
        Marker marker = aMap.addMarker(options);
        markerPoints.add(marker);


        if (firstPoint.isFactPoint && lastFactPoint == firstPoint) {//最后一个实况点
            if(isShowInfoWindow) {
                clickMarker = marker;
                clickMarker.showInfoWindow();

                //绘制最后一个实况点对应的七级、十级风圈
                drawWindCircle(firstPoint.radius_7, firstPoint.radius_10, firstLatLng);
            }

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
            factTimeMarkersMap.put(typhoonId, factTimeMarker);
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
            rotateMarkersMap.put(typhoonId, rotateMarker);

            //多个台风最后实况点合在一起
            lastFactPointMap.put(typhoonId, lastFactPoint);

            addLocationMarker();
            addLocationCircles();
            ranging(typhoonId);
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
            foreTimeMarkersMap.put(typhoonId, m);
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
    private void drawWindCircle(String radius_7, String radius_10, LatLng center) {
        removeWindCircle();

//        //七级风圈
//        List<LatLng> wind7Points = new ArrayList<>();
//        getWindCirclePoints(center, en_radius7, 0, wind7Points);
//        getWindCirclePoints(center, wn_radius7, 90, wind7Points);
//        getWindCirclePoints(center, ws_radius7, 180, wind7Points);
//        getWindCirclePoints(center, es_radius7, 270, wind7Points);
//        if (wind7Points.size() > 0) {
//            PolygonOptions polygonOptions = new PolygonOptions();
//            polygonOptions.strokeWidth(5).strokeColor(Color.YELLOW).fillColor(0x30ffffff);
//            for (LatLng latLng : wind7Points) {
//                polygonOptions.add(latLng);
//            }
//            Polygon polygon = aMap.addPolygon(polygonOptions);
//            windCirclePolygons.add(polygon);
//        }
//
//
//        //十级风圈
//        List<LatLng> wind10Points = new ArrayList<>();
//        getWindCirclePoints(center, en_radius10, 0, wind10Points);
//        getWindCirclePoints(center, wn_radius10, 90, wind10Points);
//        getWindCirclePoints(center, ws_radius10, 180, wind10Points);
//        getWindCirclePoints(center, es_radius10, 270, wind10Points);
//        if (wind10Points.size() > 0) {
//            PolygonOptions polygonOptions = new PolygonOptions();
//            polygonOptions.strokeWidth(5).strokeColor(Color.RED).fillColor(0x30ffffff);
//            for (LatLng latLng : wind10Points) {
//                polygonOptions.add(latLng);
//            }
//            Polygon polygon = aMap.addPolygon(polygonOptions);
//            windCirclePolygons.add(polygon);
//        }

        if (!TextUtils.isEmpty(radius_7) && !TextUtils.equals(radius_7, "null")) {
            circle7 = aMap.addCircle(new CircleOptions().center(center)
                    .radius(Double.valueOf(radius_7)*1000).strokeColor(Color.YELLOW)
                    .fillColor(0x30ffffff).strokeWidth(5));
        }

        if (!TextUtils.isEmpty(radius_10) && !TextUtils.equals(radius_10, "null")) {
            circle10 = aMap.addCircle(new CircleOptions().center(center)
                    .radius(Double.valueOf(radius_10)*1000).strokeColor(Color.RED)
                    .fillColor(0x30ffffff).strokeWidth(5));
        }

    }

    /**
     * 获取风圈经纬度点集合
     * @param center
     * @param radius
     * @param startAngle
     * @return
     */
    private void getWindCirclePoints(LatLng center, String radius, double startAngle, List<LatLng> points) {
        if (!TextUtils.isEmpty(radius) && !TextUtils.equals(radius, "null")) {
            double r = Double.valueOf(radius)/100;
            int pointCount = 90;
            double endAngle = startAngle+90;

            for (int i = 0; i <= pointCount; i++) {
                double angle = startAngle+(endAngle-startAngle)*i/pointCount;
                double lat = center.latitude+r*Math.sin(angle*Math.PI/180);
                double lng = center.longitude+r*Math.cos(angle*Math.PI/180);
                points.add(new LatLng(lat, lng));
            }
        }
    }

    /**
     * 测距
     */
    private void ranging(String tid) {
        if (clickLatLng == null || !isRanging) {
            return;
        }

        if (!TextUtils.isEmpty(tid)) {
            rangingSingle(tid);
        }else {
            for (String typhoonId : lastFactPointMap.keySet()) {
                rangingSingle(typhoonId);
            }
        }
    }

    /**
     * 单个点测距
     * @param typhoonId
     */
    private void rangingSingle(String typhoonId) {
        double locationLat = clickLatLng.latitude;
        double locationLng = clickLatLng.longitude;
        if (lastFactPointMap.containsKey(typhoonId)) {
            TyphoonDto dto = lastFactPointMap.get(typhoonId);
            double lat = dto.lat;
            double lng = dto.lng;
            double dis = Math.sqrt(Math.pow(locationLat-lat, 2)+ Math.pow(locationLng-lng, 2));
            int numPoint = (int) Math.floor(dis/0.2);
            double lng_per = (lng-locationLng)/numPoint;
            double lat_per = (lat-locationLat)/numPoint;
            List<Polyline> polylines = new ArrayList<>();
            List<LatLng> ranges = new ArrayList<>();
            for (int i = 0; i < numPoint; i++) {
                PolylineOptions line = new PolylineOptions();
                line.color(0xff6291E1);
                line.width(CommonUtil.dip2px(mContext, 2));
                ranges.add(new LatLng(locationLat+i*lat_per, locationLng+i*lng_per));
                if (i % 2 == 1) {
                    line.addAll(ranges);
                    Polyline polyline = aMap.addPolyline(line);
                    polylines.add(polyline);
                    ranges.clear();
                }
            }
            rangeLinesMap.put(typhoonId, polylines);

            LatLng centerLatLng = new LatLng((locationLat+lat)/2, (locationLng+lng)/2);
            addRangeMarker(typhoonId, centerLatLng, locationLng, locationLat, lng, lat);
        }
    }

    /**
     * 添加每个台风的测距距离
     */
    private void addRangeMarker(String typhoonId, LatLng latLng, double longitude1, double latitude1, double longitude2, double latitude2) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.shawn_typhoon_marker_range, null);
        TextView tvName = mView.findViewById(R.id.tvName);
        float distance = AMapUtils.calculateLineDistance(new LatLng(latitude1, longitude1), new LatLng(latitude2, longitude2));
        float d = new BigDecimal(distance/1000).setScale(1, BigDecimal.ROUND_FLOOR).floatValue();
        tvName.setText("距离台风"+d+"公里");
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromView(mView));
        Marker marker = aMap.addMarker(options);
        marker.setClickable(false);
        rangeMarkersMap.put(typhoonId, marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker != locationMarker) {
            String markerType = marker.getSnippet();
            if (!TextUtils.isEmpty(markerType)) {
                if (TextUtils.equals(markerType, TYPE_TYPHOON)) {//点击台风点
                    String[] titles = marker.getTitle().split(";");
                    if (!TextUtils.isEmpty(titles[2])) {
                        String[] circles = titles[2].split("\\|");
                        drawWindCircle(circles[0], circles[1], marker.getPosition());
                        searchAddrByLatLng(marker.getPosition().latitude, marker.getPosition().longitude);//参考位置
                    }
                }else if (TextUtils.equals(markerType, TYPE_WARNING)) {//点击预警marker
                    //点击预警marker
                }else if (TextUtils.equals(markerType, TYPE_FORE)) {//点击预报marker
                    //点击预报marker
                }
            }

            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            }else {
                marker.showInfoWindow();
            }
            clickMarker = marker;

        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isShowTyphoon && isRanging) {//测距状态下
            removeRange(null);
            clickLatLng = latLng;
            addLocationMarker();
            addLocationCircles();
            ranging(null);
        }

        if (isShowMinute) {//分钟降水
            clickLatLng = latLng;
            addLocationMarker();
            searchAddrByLatLng(latLng.latitude, latLng.longitude);
            OkHttpMinute(latLng.longitude, latLng.latitude);
        }

        if (isShowWarning) {//预警
            clickLatLng = latLng;
            addLocationMarker();
            searchAddrByLatLng(latLng.latitude, latLng.longitude);
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
                String[] titles = marker.getTitle().split(";");

                //台风内容
                String[] title1 = titles[0].split("\\|");
                if (!TextUtils.isEmpty(title1[0])) {
                    tvName.setText(title1[0]);
                }
                if (!TextUtils.isEmpty(title1[1])) {
                    tvInfo.setText(title1[1]);
                }

                //预报结论
                String[] title2 = titles[1].split("\\|");
                float currentSpeed = Float.parseFloat(title2[0]);
                float nextSpeed = Float.parseFloat(title2[1]);

                String strength = "";
                if (currentSpeed > nextSpeed) {
                    strength = "强度逐渐减弱。";
                }else {
                    strength = "强度逐渐增强。";
                }
                String time = "";
                try {
                    time = "(下次更新时间为"+sdf5.format(sdf3.parse(title2[2]))+")";
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String content = tvInfo.getText().toString();
                if (TextUtils.isEmpty(typhoonPointAddr)) {
                    typhoonPointAddr = marker.getPosition().latitude+","+marker.getPosition().longitude;
                }
                String position = "参考位置："+typhoonPointAddr+"\n";

                String result = "";
                if (!TextUtils.isEmpty(title2[3]) && !TextUtils.equals(title2[3], "null")) {
                    result = "预报结论：将以每小时"+currentSpeed+"公里左右\n的速度偏"+title2[3]+"方向移动，\n"+strength+"\n";
                }else {
                    result = "预报结论：将以每小时"+currentSpeed+"公里左右\n的速度移动，"+strength+"\n";
                }
                tvInfo.setText(content+position+result+time);

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
        }else if (TextUtils.equals(marker.getSnippet(), TYPE_FORE)) {//天气预报
            view = inflater.inflate(R.layout.shawn_fore_marker_info, null);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvInfo = view.findViewById(R.id.tvInfo);

            if (allCitysMap.containsKey(marker.getTitle())) {
                WeatherDto dto = allCitysMap.get(marker.getTitle());
                if (dto != null) {
                    tvName.setText(dto.cityName);
                    String phe = getString(WeatherUtil.getWeatherId(dto.highPheCode)) + "~" + getString(WeatherUtil.getWeatherId(dto.lowPheCode));
                    if (dto.highPheCode == dto.lowPheCode) {
                        phe = getString(WeatherUtil.getWeatherId(dto.highPheCode));
                    }
                    String temp = dto.highTemp+"~"+dto.lowTemp+"℃";
                    String windDir = getString(WeatherUtil.getWindDirection(dto.highWindDir));
                    String windForce = WeatherUtil.getFactWindForce(dto.highWindForce);
                    tvInfo.setText(phe+"\n"+temp+"\n"+windDir+windForce);
                }
            }

        }

        return view;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (allCitysMap.containsKey(marker.getTitle())) {
            WeatherDto dto = allCitysMap.get(marker.getTitle());
            if (dto != null) {
                Intent intent = new Intent(mContext, ShawnForecastActivity.class);
                intent.putExtra("cityName", dto.cityName);
                intent.putExtra("cityId", dto.cityId);
                intent.putExtra("lng", dto.lat);
                intent.putExtra("lat", dto.lng);
                startActivity(intent);
            }
        }

    }

    /**
     * 向下动画
     * @param view
     */
    private void animationUpToDown(final View view) {
        if (view.getVisibility() == View.GONE) {
            return;
        }
        TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1);

        animation.setDuration(200);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 向上动画
     * @param view
     */
    private void animationDownToUp(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);

        animation.setDuration(200);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
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
                    .zIndex(10000)
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
                    Thread.sleep(1000);
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
        final String url = "https://api.bluepi.tianqi.cn/outdata/zhongshan/zhongShanWarnning";
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
                                                dto.cId = item0.substring(0, 4);
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

                                            addLocationWarnings();

                                            switchWarningMarkersPolygons();
                                            addWarningMarkers();

                                            try {
                                                String count = warningList.size()+"";
                                                String time = "";
                                                if (!object.isNull("time")) {
                                                    long t = object.getLong("time");
                                                    time = sdf6.format(new Date(t*1000));
                                                }
                                                if (TextUtils.equals(count, "0")) {
                                                    tvWarningPrompt.setText("广东省当前生效预警"+count+"条");
                                                    tvWaringList.setVisibility(View.GONE);
                                                    layoutWarning.setVisibility(View.VISIBLE);
                                                    return;
                                                }

//                                                String str1 = time+", "+"广东省当前生效预警";
                                                String str1 = "广东省当前生效预警";
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
                                                layoutWarning.setVisibility(View.VISIBLE);

                                                //计算统计列表信息
                                                int rnation = 0, rpro = 0, rcity = 0, rdis = 0;
                                                int onation = 0, opro = 0, ocity = 0, odis = 0;
                                                int ynation = 0, ypro = 0, ycity = 0, ydis = 0;
                                                int bnation = 0, bpro = 0, bcity = 0, bdis = 0;
                                                int wnation = 0, wpro = 0, wcity = 0, wdis = 0;
                                                for (int i = 0; i < warningList.size(); i++) {
                                                    WarningDto dto = warningList.get(i);
                                                    if (TextUtils.equals(dto.color, "01")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            wnation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            wpro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            wcity += 1;
                                                        }else {
                                                            wdis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "05")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            rnation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            rpro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            rcity += 1;
                                                        }else {
                                                            rdis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "04")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            onation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            opro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            ocity += 1;
                                                        }else {
                                                            odis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "03")) {
                                                        if (TextUtils.equals(dto.item0, "000000")) {
                                                            ynation += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-4, dto.item0.length()), "0000")) {
                                                            ypro += 1;
                                                        }else if (TextUtils.equals(dto.item0.substring(dto.item0.length()-2, dto.item0.length()), "00")) {
                                                            ycity += 1;
                                                        }else {
                                                            ydis += 1;
                                                        }
                                                    }else if (TextUtils.equals(dto.color, "02")) {
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
                                                wDto.colorName = "白"+(wnation+wpro+wcity+wdis);
                                                wDto.nationCount = wnation+"";
                                                wDto.proCount = wpro+"";
                                                wDto.cityCount = wcity+"";
                                                wDto.disCount = wdis+"";
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

                                initWarningGridView();
                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 切换预警图标、图层显示或隐藏
     */
    private void switchWarningMarkersPolygons() {
        for (String html : warningMarkers.keySet()) {
            if (warningMarkers.containsKey(html)) {
                Marker marker = warningMarkers.get(html);
                String warningType = html.split("-")[2].substring(0,5);
                if (isShowWarning && selectWarningTypes.contains(warningType)) {
                    marker.setVisible(true);
                }else {
                    marker.setVisible(false);
                }
            }
        }

        for (String html : warningPolaygonsMap.keySet()) {
            String warningType = html.split("-")[2].substring(0, 5);
            List<List<Polygon>> warningPolygons = warningPolaygonsMap.get(html);
            for (List<Polygon> polygons : warningPolygons) {
                for (Polygon polygon : polygons) {
                    if (polygon != null) {
                        if (isShowWarning && selectWarningTypes.contains(warningType)) {
                            polygon.setVisible(true);
                        }else {
                            polygon.setVisible(false);
                        }
                    }
                }
            }
        }

    }

    /**
     * 绘制预警markers
     */
    private void addWarningMarkers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (warningMarkers.size() <= 0 || warningPolaygonsMap.size() <= 0) {//预警marker、图层没有添加绘制国
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    for (WarningDto dto : warningList) {
                        if (!warningMarkers.containsKey(dto.html)) {
                            double lat = Double.valueOf(dto.lat);
                            double lng = Double.valueOf(dto.lng);
                            MarkerOptions optionsTemp = new MarkerOptions();
                            optionsTemp.title(dto.lat+","+dto.lng+","+dto.html);
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
                            }else if (dto.color.equals(CONST.white[0])) {
                                bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.white[1]+CONST.imageSuffix);
                                if (bitmap == null) {
                                    bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.white[1]+CONST.imageSuffix);
                                }
                            }
                            ivMarker.setImageBitmap(bitmap);
                            optionsTemp.icon(BitmapDescriptorFactory.fromView(mView));

                            Marker marker = aMap.addMarker(optionsTemp);
                            warningMarkers.put(dto.html, marker);
                        }

                    }

                    List<WarningDto> list = new ArrayList<>(warningList);
                    Collections.sort(list, new Comparator<WarningDto>() {//按照预警等级排序，保证同一个区域绘制在最上层的是最高等级预警
                        @Override
                        public int compare(WarningDto arg0, WarningDto arg1) {
                            return Integer.valueOf(arg0.color).compareTo(Integer.valueOf(arg1.color));
                        }
                    });
                    for (WarningDto dto : list) {
                        if (!warningPolaygonsMap.containsKey(dto.html)) {
                            int color = 0;
                            if (TextUtils.equals(dto.color, "01")) {
                                color = 0x99ffffff;
                            }else if (TextUtils.equals(dto.color, "02")) {
                                color = 0x990456F3;
                            }else if (TextUtils.equals(dto.color, "03")) {
                                color = 0x99FFFF0C;
                            }else if (TextUtils.equals(dto.color, "04")) {
                                color = 0x99FC9807;
                            }else if (TextUtils.equals(dto.color, "05")) {
                                color = 0x99D5202A;
                            }

                            drawGuangdongDistrict(dto.html, dto.item0, color);
                        }
                    }
                }else {
                    switchWarningMarkersPolygons();
                }
            }
        }).start();
    }

    /**
     * 绘制广东提供对应的行政区划数据
     * @param html
     * @param wId
     * @param color
     */
    private void drawGuangdongDistrict(final String html, final String wId, final int color) {
        if (color == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = CommonUtil.getFromAssets(mContext, "json/guangdong/" +wId+".json");
                if (!TextUtils.isEmpty(result)) {
                    try {
                        double latitude = 0,longitude = 0;
                        List<List<Polygon>> warningPolygons = new ArrayList<>();
                        JSONArray array1 = new JSONArray(result);
                        for (int i = 0; i < array1.length(); i++) {
                            JSONArray array2 = array1.getJSONArray(i);
                            for (int j = 0; j < array2.length(); j++) {
                                List<Polygon> polygons = new ArrayList<>();
                                PolygonOptions polygonOptions = new PolygonOptions();
                                polygonOptions.fillColor(color);
                                polygonOptions.strokeColor(Color.WHITE).strokeWidth(5);
                                JSONArray array3 = array2.getJSONArray(j);
                                for (int k = 0; k < array3.length(); k++) {
                                    JSONArray itemArray = array3.getJSONArray(k);
                                    double lat = itemArray.getDouble(0);
                                    double lng = itemArray.getDouble(1);
                                    polygonOptions.add(new LatLng(lat, lng));

                                    if (i == 0 && k == 0) {
                                        latitude = lat;
                                        longitude = lng;
                                    }
                                }
                                Polygon polygon = aMap.addPolygon(polygonOptions);
                                if (wId.endsWith("00")) {//市级行政区划绘制在区县级下面
                                    polygon.setZIndex(10);
                                }else {
                                    polygon.setZIndex(20);
                                }
                                polygons.add(polygon);
                                warningPolygons.add(polygons);
                            }
                        }
                        warningPolaygonsMap.put(html, warningPolygons);

//                        TextOptions options = new TextOptions();
//                        options.position(new LatLng(latitude, longitude));
//                        options.fontSize(25);
//                        options.fontColor(Color.GREEN);
//                        options.text(key+adCode);
//                        aMap.addText(options);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    /**
     * 增加当前定位点对应预警信息
     */
    private void addLocationWarnings() {
        llWarningContainer.removeAllViews();
        llWarningContainer.setVisibility(View.GONE);
        //判断点击点是否在预警区域内并显示气泡
        for (String html : warningPolaygonsMap.keySet()) {
            List<List<Polygon>> warningPolygons = warningPolaygonsMap.get(html);
            for (List<Polygon> polygons : warningPolygons) {
                for (Polygon polygon : polygons) {
                    if (polygon != null && polygon.contains(clickLatLng)) {
                        //判断选中区域后，是否显示对应区域marker hideInfo或者showInfo
                        for (String html2 : warningMarkers.keySet()) {
                            if (warningMarkers.containsKey(html2)) {
                                Marker marker = warningMarkers.get(html2);
                                if (TextUtils.equals(html, html2)) {
                                    clickMarker = marker;
                                    if (!marker.isInfoWindowShown() && TextUtils.equals(marker.getSnippet(), TYPE_WARNING)) {
                                        marker.showInfoWindow();
                                    }
                                }else {
                                    marker.hideInfoWindow();
                                }
                            }
                        }

                        //增加底部选中区域生效预警显示
                        for (WarningDto dto : warningList) {
                            if (TextUtils.equals(html, dto.html)) {
                                TextView tvWarning = new TextView(mContext);
                                tvWarning.setText(dto.name);
                                tvWarning.setTag(dto);
                                tvWarning.setTextColor(getResources().getColor(R.color.text_color3));
                                tvWarning.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                                Paint paint = tvWarning.getPaint();
                                paint.setUnderlineText(true);
                                paint.setAntiAlias(true);
                                tvWarning.setLayerPaint(paint);
                                llWarningContainer.addView(tvWarning);
                                llWarningContainer.setVisibility(View.VISIBLE);

                                tvWarning.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (int i = 0; i < llWarningContainer.getChildCount(); i++) {
                                            TextView tvWarning = (TextView) llWarningContainer.getChildAt(i);
                                            if (tvWarning.getTag() == v.getTag()) {
                                                WarningDto dto = (WarningDto) v.getTag();
                                                Intent intentDetail = new Intent(mContext, ShawnWarningDetailActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelable("data", dto);
                                                intentDetail.putExtras(bundle);
                                                startActivity(intentDetail);
                                                break;
                                            }
                                        }
                                    }
                                });
                            }

                        }

                    }
                }
            }
        }

    }

    /**
     * 初始化预警统计列表
     */
    private void initWarningListView() {
        warningListView = findViewById(R.id.warningListView);
        warningAdapter = new WarningStatisticAdapter(mContext, warningStatistics);
        warningListView.setAdapter(warningAdapter);
    }

    /**
     * 初始化listview
     */
    private void initWarningGridView() {
        if (warningMapTypeAdapter != null) {
            warningMapTypeAdapter.notifyDataSetChanged();
            return;
        }

        warningTypeList.clear();
        String[] array1 = getResources().getStringArray(R.array.warningType);
        for (int i = 1; i < array1.length; i++) {
            HashMap<String, Integer> map = new LinkedHashMap<>();
            String[] value = array1[i].split(",");
            int count = 0;
            for (WarningDto w : warningList) {
                String[] array = w.html.split("-");
                String type = array[2].substring(0, 5);
                if (TextUtils.equals(type, value[0])) {
                    map.put(type, count++);
                }
            }

            WarningDto dto = new WarningDto();
            dto.name = value[1];
            dto.type = value[0];
            dto.count = count;
            if (count > 0) {
                warningTypeList.add(dto);
            }
        }

        //获取选中的预警类型
        selectWarningTypes = "";
        for (WarningDto w : warningTypeList) {
            if (w.isSelected) {
                selectWarningTypes += w.type+",";
            }
        }

        gridviewWarning = findViewById(R.id.gridviewWarning);
        warningMapTypeAdapter = new WarningMapTypeAdapter(mContext, warningTypeList);
        gridviewWarning.setAdapter(warningMapTypeAdapter);
        gridviewWarning.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                WarningDto dto = warningTypeList.get(arg2);
                dto.isSelected = !dto.isSelected;
                warningMapTypeAdapter.notifyDataSetChanged();

                //获取选中的预警类型
                selectWarningTypes = "";
                for (WarningDto w : warningTypeList) {
                    if (w.isSelected) {
                        selectWarningTypes += w.type+",";
                    }
                }

                //判断显示地图上预警类型对应marker
                for (String html : warningMarkers.keySet()) {
                    if (warningMarkers.containsKey(html)) {
                        Marker marker = warningMarkers.get(html);
                        String warningType = html.split("-")[2].substring(0,5);
                        if (TextUtils.equals(dto.type, warningType)) {
                            if (dto.isSelected) {
                                marker.setVisible(true);
                            }else {
                                marker.setVisible(false);
                            }
                        }
                    }
                }

                //判断显示地图上预警类型对应marker绘制区域
                for (String html : warningPolaygonsMap.keySet()) {
                    if (warningPolaygonsMap.containsKey(html)) {
                        String warningType = html.split("-")[2].substring(0,5);
                        if (TextUtils.equals(warningType, dto.type)) {
                            List<List<Polygon>> warningPolygons = warningPolaygonsMap.get(html);
                            for (List<Polygon> polygons : warningPolygons) {
                                if (dto.isSelected) {
                                    for (Polygon polygon : polygons) {
                                        if (polygon != null) {
                                            polygon.setVisible(true);
                                        }
                                    }
                                }else {
                                    for (Polygon polygon : polygons) {
                                        if (polygon != null) {
                                            polygon.setVisible(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        });
    }


    //天气预报
    /**
     * 获取所有城市信息
     */
    private void OkHttpAllCitys() {
        if (allCitysMap.size() > 0) {
            moveMapLoadFore(zoom);
            return;
        }
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.statistic()).build(), new Callback() {
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

                                JSONArray array1 = obj.getJSONArray("level1");
                                for (int i = 0; i < array1.length(); i++) {
                                    WeatherDto dto = new WeatherDto();
                                    JSONObject itemObj = array1.getJSONObject(i);
                                    dto.cityId = itemObj.getString("areaid");
                                    dto.cityName = itemObj.getString("name");
                                    dto.lat = itemObj.getDouble("lat");
                                    dto.lng = itemObj.getDouble("lon");
                                    dto.level = itemObj.getString("level");
                                    allCitysMap.put(dto.cityId, dto);
                                }

                                JSONArray array2 = obj.getJSONArray("level2");
                                for (int i = 0; i < array2.length(); i++) {
                                    WeatherDto dto = new WeatherDto();
                                    JSONObject itemObj = array2.getJSONObject(i);
                                    dto.cityId = itemObj.getString("areaid");
                                    dto.cityName = itemObj.getString("name");
                                    dto.lat = itemObj.getDouble("lat");
                                    dto.lng = itemObj.getDouble("lon");
                                    dto.level = itemObj.getString("level");
                                    allCitysMap.put(dto.cityId, dto);
                                }

                                JSONArray array3 = obj.getJSONArray("level3");
                                for (int i = 0; i < array3.length(); i++) {
                                    WeatherDto dto = new WeatherDto();
                                    JSONObject itemObj = array3.getJSONObject(i);
                                    dto.cityId = itemObj.getString("areaid");
                                    dto.cityName = itemObj.getString("name");
                                    dto.lat = itemObj.getDouble("lat");
                                    dto.lng = itemObj.getDouble("lon");
                                    dto.level = itemObj.getString("level");
                                    if (dto.cityId.startsWith("10101") || dto.cityId.startsWith("10102")
                                            || dto.cityId.startsWith("10103") || dto.cityId.startsWith("10104")) {
                                        dto.level = "2";
                                    }
                                    allCitysMap.put(dto.cityId, dto);
                                }

                                //加载所在区域对应的预报点数据
                                moveMapLoadFore(zoom);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingView.setVisibility(View.GONE);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler foreHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    float zoom = (float)msg.obj;
                    moveMapLoadFore(zoom);
                    break;
            }
        }
    };

    /**
     * 缩放地图加载预报数据
     * @param zoom
     */
    private void moveMapLoadFore(float zoom) {
        removeForeMarkers();

        for (String cityId : allCitysMap.keySet()) {
            if (allCitysMap.containsKey(cityId)) {
                WeatherDto dto = allCitysMap.get(cityId);
                if (leftlatlng != null && dto.lat > leftlatlng.latitude && dto.lat < rightLatlng.latitude
                        && rightLatlng != null && dto.lng > leftlatlng.longitude && dto.lng < rightLatlng.longitude) {
                    if (zoom <= 6.5f) {
                        if (TextUtils.equals(dto.level, "1")) {
                            getWeatherInfo(dto);
                        }
                    }else if (zoom > 6.5f && zoom <= 8.5f) {
                        if (TextUtils.equals(dto.level, "1") || TextUtils.equals(dto.level, "2")) {
                            getWeatherInfo(dto);
                        }
                    }else {
                        getWeatherInfo(dto);
                    }
                }
            }
        }
    }

    /**
     * 获取某个城市天气数据
     */
    private void getWeatherInfo(final WeatherDto dto) {
        if (dto == null || TextUtils.isEmpty(dto.cityId)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherAPI.getWeather2(mContext, dto.cityId, Constants.Language.ZH_CN, new AsyncResponseHandler() {
                    @Override
                    public void onComplete(final Weather content) {
                        super.onComplete(content);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (content != null) {
                                    try {
                                        JSONObject obj = new JSONObject(content.toString());

                                        //15天预报
                                        if (!obj.isNull("f")) {
                                            JSONObject f = obj.getJSONObject("f");
                                            JSONArray f1 = f.getJSONArray("f1");
                                            if (f1.length() > 0) {
                                                //预报内容
                                                JSONObject weeklyObj = f1.getJSONObject(0);

                                                //晚上
                                                dto.lowPheCode = Integer.valueOf(weeklyObj.getString("fb"));
                                                dto.lowTemp = Integer.valueOf(weeklyObj.getString("fd"));
                                                dto.lowWindDir = Integer.valueOf(weeklyObj.getString("ff"));
                                                dto.lowWindForce = Integer.valueOf(weeklyObj.getString("fh"));

                                                //白天
                                                dto.highPheCode = Integer.valueOf(weeklyObj.getString("fa"));
                                                dto.highTemp = Integer.valueOf(weeklyObj.getString("fc"));
                                                dto.highWindDir = Integer.valueOf(weeklyObj.getString("fe"));
                                                dto.highWindForce = Integer.valueOf(weeklyObj.getString("fg"));

                                                addCityForecastMarker(dto);
                                            }
                                        }

                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
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
     * 添加城市天气预报marker
     * @param dto
     */
    private void addCityForecastMarker(final WeatherDto dto) {
        int currentHour = Integer.parseInt(sdf7.format(new Date()));
        MarkerOptions options = new MarkerOptions();
        options.title(dto.cityId);
        options.snippet(TYPE_FORE);
        options.anchor(0.5f, 1.0f);
        options.position(new LatLng(dto.lat, dto.lng));
        if (currentHour >= 6 && currentHour <= 18) {
            options.icon(BitmapDescriptorFactory.fromView(weatherBitmapView(dto.highPheCode)));
        }else {
            options.icon(BitmapDescriptorFactory.fromView(weatherBitmapView(dto.lowPheCode)));
        }
        Marker marker = aMap.addMarker(options);
        marker.setZIndex(100);
        allCitysMarkerMap.put(dto.cityId, marker);
    }

    /**
     * 清除所有预报markers
     * 注意，这里不清空预报marker的集合，为判断是否有加载过的marker使用
     */
    private void removeForeMarkers() {
        for (String cityId : allCitysMarkerMap.keySet()) {
            if (allCitysMarkerMap.containsKey(cityId)) {
                Marker marker = allCitysMarkerMap.get(cityId);
                if (marker != null) {
                    marker.remove();
                }
            }
        }
        allCitysMarkerMap.clear();
    }

    /**
     * 添加天气marker
     * @return
     */
    private View weatherBitmapView(int weatherCode) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_fore_marker_icon, null);
        if (view == null) {
            return null;
        }
        ImageView ivMarker = view.findViewById(R.id.ivMarker);
        ivMarker.setImageBitmap(WeatherUtil.getDayBitmap(mContext, weatherCode));
        return view;
    }


    //分钟降水
    /**
     * 通过经纬度获取地理位置信息
     * @param lat
     * @param lng
     */
    private void searchAddrByLatLng(double lat, double lng) {
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        loadingView.setVisibility(View.VISIBLE);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
    }
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
            loadingView.setVisibility(View.GONE);

            clickAdcode = result.getRegeocodeAddress().getAdCode();
            clickLatLng = new LatLng(result.getRegeocodeQuery().getPoint().getLatitude(), result.getRegeocodeQuery().getPoint().getLongitude());
            saveSharedPreferences();

            if (isShowTyphoon) {
                typhoonPointAddr = result.getRegeocodeAddress().getProvince()+result.getRegeocodeAddress().getCity();
                if (clickMarker != null && TextUtils.equals(clickMarker.getSnippet(), TYPE_TYPHOON)) {//为了显示"参考位置"
                    clickMarker.showInfoWindow();
                }
            }

            if (isShowMinute) {
                String addr = result.getRegeocodeAddress().getFormatAddress();
                if (!TextUtils.isEmpty(addr)) {
                    tvAddr.setText(addr);
                }
            }

            if (isShowWarning) {
                addLocationWarnings();
//                Toast.makeText(mContext, clickAdcode+"-"+result.getRegeocodeAddress().getCity()+result.getRegeocodeAddress().getDistrict(), Toast.LENGTH_LONG).show();
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
                                                    llContainerMinute.addView(minuteFallView, width-(int)(CommonUtil.dip2px(mContext, 65)), (int)(CommonUtil.dip2px(mContext, 120)));
                                                }

                                                if (reMinutePrompt.getVisibility() != View.VISIBLE) {
                                                    animationDownToUp(reMinutePrompt);
                                                    sbMinute.setChecked(true);
                                                }

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


    //全球风场
    private void initWindTypeGridView() {
        if (windMapTypeAdapter != null) {
            windMapTypeAdapter.notifyDataSetChanged();
            return;
        }

        windTypeList.clear();
        WindDto dto = new WindDto();
        dto.windHeight = "1000";
        dto.isGFS = true;
        windTypeList.add(dto);
        dto = new WindDto();
        dto.windHeight = "1000";
        dto.isGFS = false;
        windTypeList.add(dto);
        dto = new WindDto();
        dto.windHeight = "500";
        dto.isGFS = true;
        windTypeList.add(dto);
        dto = new WindDto();
        dto.windHeight = "500";
        dto.isGFS = false;
        windTypeList.add(dto);
        dto = new WindDto();
        dto.windHeight = "200";
        dto.isGFS = true;
        windTypeList.add(dto);
        dto = new WindDto();
        dto.windHeight = "200";
        dto.isGFS = false;
        windTypeList.add(dto);

        gridviewWind = findViewById(R.id.gridviewWind);
        windMapTypeAdapter = new WindMapTypeAdapter(mContext, windTypeList);
        windMapTypeAdapter.windHeight = windHeight;
        windMapTypeAdapter.isGFS = isGfs;
        gridviewWind.setAdapter(windMapTypeAdapter);
        gridviewWind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WindDto dto = windTypeList.get(position);
                windHeight = dto.windHeight;
                isGfs = dto.isGFS;
                windMapTypeAdapter.windHeight = windHeight;
                windMapTypeAdapter.isGFS = isGfs;
                windMapTypeAdapter.notifyDataSetChanged();

                if (isGfs) {
                    windDataGFS = null;
                    OkHttpGFS();
                }else {
                    windDataT639 = null;
                    OkHttpT639();
                }

            }
        });
    }


    private void OkHttpGFS() {
        loadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.windGFS(windHeight)).build(), new Callback() {
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
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.windT639(windHeight, "0")).build(), new Callback() {
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
        //通用
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Point leftPoint = new Point(0, dm.heightPixels);
        Point rightPoint = new Point(dm.widthPixels, 0);
        leftlatlng = aMap.getProjection().fromScreenLocation(leftPoint);
        rightLatlng = aMap.getProjection().fromScreenLocation(rightPoint);

        if (isShowFore) {//选中天气预报状态下
            //加载预报点
            zoom = arg0.zoom;
            foreHandler.removeMessages(1001);
            Message msg = foreHandler.obtainMessage();
            msg.what = 1001;
            msg.obj = zoom;
            foreHandler.sendMessageDelayed(msg, 1000);
        }

        if (isShowWind) {//选中风场状态下
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

        int strokeColor = Color.WHITE;
        if (AMapType == AMap.MAP_TYPE_SATELLITE) {
            strokeColor = Color.WHITE;
        }else if (AMapType == AMap.MAP_TYPE_NORMAL) {
            strokeColor = 0x9955A7FF;
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
            waitWindView.setStokeColor(strokeColor);
            waitWindView.start();
            waitWindView.invalidate();
        }else {
            if (isGfs) {
                waitWindView.setData(windDataGFS);
            }else {
                waitWindView.setData(windDataT639);
            }
            waitWindView.setStokeColor(strokeColor);
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
                    tvWindTime.setText("GFS("+windHeight+"hPa) "+sdf3.format(sdf2.parse(time)) + "风场预报");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }else {
            time = windDataT639.filetime;
            if (!TextUtils.isEmpty(time)) {
                try {
                    tvWindTime.setText("T639("+windHeight+"hPa) "+sdf3.format(sdf2.parse(time)) + "风场预报");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.llBack) {
            finish();

        } else if (i == R.id.ivRefresh) {
            android.view.animation.Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.shawn_round_animation);
            ivRefresh.startAnimation(animation);

        } else if (i == R.id.llMenu) {
            scaleAnimation = !scaleAnimation;
            if (scaleAnimation) {
                toRightAnimation();
            } else {
                toLeftAnimation();
            }


            //台风
        } else if (i == R.id.llTyphoon) {
            isShowTyphoon = !isShowTyphoon;
            if (isShowTyphoon) {
                lyoutTyphoon.setVisibility(View.VISIBLE);
                llTyphoon.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivTyphoon.setImageResource(R.drawable.shawn_icon_typhoon_press);
                tvTyphoon.setTextColor(Color.WHITE);

                String typhoonName = "";
                for (TyphoonDto dto : startList) {
                    String name;
                    if (TextUtils.equals(dto.enName, "nameless")) {
                        if (!TextUtils.isEmpty(typhoonName)) {
                            typhoonName = dto.enName + "\n" + typhoonName;
                        } else {
                            typhoonName = dto.enName;
                        }
                        name = dto.code + " " + dto.enName;
                    } else {
                        if (!TextUtils.isEmpty(typhoonName)) {
                            typhoonName = dto.code + " " + dto.name + " " + dto.enName + "\n" + typhoonName;
                        } else {
                            typhoonName = dto.code + " " + dto.name + " " + dto.enName;
                        }
                        name = dto.code + " " + dto.name + " " + dto.enName;
                    }
                    OkHttpTyphoonDetail(dto.status, dto.id, name);
                }
                if (!TextUtils.isEmpty(typhoonName)) {
                    tvTyphoonName.setText(typhoonName);
                }
                if (startList.size() <= 0) {// 没有生效台风
                    tvTyphoonName.setText(getString(R.string.no_typhoon));
                }
                tvTyphoonName.setVisibility(View.VISIBLE);
            } else {
                lyoutTyphoon.setVisibility(View.GONE);
                llTyphoon.setBackgroundColor(Color.TRANSPARENT);
                ivTyphoon.setImageResource(R.drawable.shawn_icon_typhoon);
                tvTyphoon.setTextColor(getResources().getColor(R.color.text_color3));
                clearAllPoints(null);
            }

        } else if (i == R.id.ivRange) {
            isRanging = !isRanging;
            if (isRanging) {
                ivRange.setImageResource(R.drawable.shawn_icon_range_press);
                addLocationMarker();
                addLocationCircles();
                ranging(null);
            } else {
                ivRange.setImageResource(R.drawable.shawn_icon_range);
                removeLocationMarker();
                removeLocationCirces();
                removeRange(null);
            }

        } else if (i == R.id.tvCurrent) {
            if (startListView.getVisibility() == View.VISIBLE) {
                return;
            }

            tvCurrent.setBackgroundResource(R.drawable.shawn_bg_current_press);
            tvHistory.setBackgroundResource(R.drawable.shawn_bg_history);
            tvCurrent.setTextColor(Color.WHITE);
            tvHistory.setTextColor(getResources().getColor(R.color.text_color3));
            startListView.setVisibility(View.VISIBLE);
            publishListView.setVisibility(View.GONE);
            nameListView.setVisibility(View.GONE);


            tvTyphoonName.setText("");
            clearAllPoints(null);

            //清空选中的所有台风，并还原历史台风列表状态
            selectList.clear();
            for (TyphoonDto dto : nameList) {
                dto.isSelected = false;
            }
            if (nameAdapter != null) {
                nameAdapter.notifyDataSetChanged();
            }

            String typhoonName = "";
            for (TyphoonDto dto : startList) {
                if (!TextUtils.isEmpty(typhoonName)) {
                    typhoonName = dto.code + " " + dto.name + " " + dto.enName + "\n" + typhoonName;
                } else {
                    typhoonName = dto.code + " " + dto.name + " " + dto.enName;
                }
                String name = dto.code + " " + dto.name + " " + dto.enName;
                OkHttpTyphoonDetail(dto.status, dto.id, name);
            }
            if (!TextUtils.isEmpty(typhoonName)) {
                tvTyphoonName.setText(typhoonName);
            }
            if (startList.size() <= 0) {// 没有生效台风
                tvTyphoonName.setText(getString(R.string.no_typhoon));
            }
            tvTyphoonName.setVisibility(View.VISIBLE);

        } else if (i == R.id.tvHistory) {
            if (startListView.getVisibility() != View.VISIBLE) {
                return;
            }
            tvCurrent.setBackgroundResource(R.drawable.shawn_bg_current);
            tvHistory.setBackgroundResource(R.drawable.shawn_bg_history_press);
            tvCurrent.setTextColor(getResources().getColor(R.color.text_color3));
            tvHistory.setTextColor(Color.WHITE);
            startListView.setVisibility(View.GONE);
            publishListView.setVisibility(View.VISIBLE);
            nameListView.setVisibility(View.VISIBLE);

            //清空选中的所有台风，并还原历史台风列表状态
            selectList.clear();
            for (TyphoonDto dto : nameList) {
                if (TextUtils.equals(dto.status, "1")) {
                    dto.isSelected = true;
                    selectList.add(dto);
                } else {
                    dto.isSelected = false;
                }
            }
            if (nameAdapter != null) {
                nameAdapter.notifyDataSetChanged();
            }

        } else if (i == R.id.ivList || i == R.id.ivTyphoonClose) {
            if (reTyphoonList.getVisibility() == View.GONE) {
                animationDownToUp(reTyphoonList);
                ivList.setImageResource(R.drawable.shawn_icon_list_press);
            } else {
                animationUpToDown(reTyphoonList);
                ivList.setImageResource(R.drawable.shawn_icon_list);
            }

        } else if (i == R.id.ivLegend || i == R.id.ivLegendClose) {
            if (reLegend.getVisibility() == View.GONE) {
                animationDownToUp(reLegend);
                ivLegend.setImageResource(R.drawable.shawn_icon_legend_press);
            } else {
                animationUpToDown(reLegend);
                ivLegend.setImageResource(R.drawable.shawn_icon_legend);
            }

        } else if (i == R.id.ivLocation) {
            clickAdcode = locationAdcode;
            clickLatLng = locationLatLng;

            if (isRanging) {//测距状态下
                addLocationMarker();
                addLocationCircles();
                ranging(null);
            }

            if (isShowMinute) {//选中分钟降水状态下
                if (clickLatLng != null) {
                    addLocationMarker();
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude);
                    OkHttpMinute(clickLatLng.longitude, clickLatLng.latitude);
                }
            }

            if (isShowWarning) {//选中预警状态下
                if (clickLatLng != null) {
                    addLocationMarker();
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude);
                }
            }

            if (clickLatLng != null) {
                addLocationMarker();
                aMap.animateCamera(CameraUpdateFactory.newLatLng(clickLatLng));
            }


            //更多
        } else if (i == R.id.ivMore) {
            isShowMore = !isShowMore;
            if (isShowMore) {
                ivMore.setImageResource(R.drawable.shawn_icon_more_press);
                enlargeAnimation(scrollViewMore);
                scrollViewMore.setVisibility(View.VISIBLE);
            } else {
                ivMore.setImageResource(R.drawable.shawn_icon_more);
                narrowAnimation(scrollViewMore);
                scrollViewMore.setVisibility(View.GONE);
            }

        } else if (i == R.id.ivMapType1) {
            if (AMapType == AMap.MAP_TYPE_SATELLITE) {
                return;
            } else {
                ivBack.setImageResource(R.drawable.shawn_icon_back);
                tvBack.setTextColor(Color.WHITE);
                ivRefresh.setImageResource(R.drawable.shawn_icon_refresh_white);
                tvTyphoonName.setTextColor(Color.WHITE);
                ivMapType1.setBackgroundResource(R.drawable.shawn_bg_corner_map_press);
                ivMapType2.setBackgroundColor(getResources().getColor(R.color.transparent));
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                AMapType = AMap.MAP_TYPE_SATELLITE;
                if (isShowWind) {
                    reloadWind();
                }
            }

        } else if (i == R.id.ivMapType2) {
            if (AMapType == AMap.MAP_TYPE_NORMAL) {
                return;
            } else {
                ivBack.setImageResource(R.drawable.shawn_icon_arrow_left);
                tvBack.setTextColor(Color.BLACK);
                ivRefresh.setImageResource(R.drawable.shawn_icon_refresh_black);
                tvTyphoonName.setTextColor(getResources().getColor(R.color.text_color3));
                ivMapType1.setBackgroundColor(getResources().getColor(R.color.transparent));
                ivMapType2.setBackgroundResource(R.drawable.shawn_bg_corner_map_press);
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                AMapType = AMap.MAP_TYPE_NORMAL;
                if (isShowWind) {
                    reloadWind();
                }
            }


            //天气实况
        } else if (i == R.id.llFact) {
            Toast.makeText(mContext, "开发中，敬请期待~~~", Toast.LENGTH_SHORT).show();


            //卫星拼图
        } else if (i == R.id.llSatelite) {
            isShowCloud = !isShowCloud;
            if (isShowCloud) {
                llSatelite.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivSatelite.setImageResource(R.drawable.shawn_icon_satelite_press);
                tvSatelite.setTextColor(Color.WHITE);
                if (cloudBitmap == null) {
                    OkHttpCloudChart();
                } else {
                    drawCloud(cloudBitmap);
                }
            } else {
                llSatelite.setBackgroundColor(Color.TRANSPARENT);
                ivSatelite.setImageResource(R.drawable.shawn_icon_satelite);
                tvSatelite.setTextColor(getResources().getColor(R.color.text_color3));
                removeCloud();
            }


            //雷达拼图
        } else if (i == R.id.llRadar) {
            isShowRadar = !isShowRadar;
            if (isShowRadar) {
                llRadar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivRadar.setImageResource(R.drawable.shawn_icon_radar_press);
                tvRadar.setTextColor(Color.WHITE);
                if (caiyunList.size() <= 0) {
                    OkHttpCaiyun();
                } else {
                    removeRadar();
                    caiyunThread = new CaiyunThread(caiyunList);
                    caiyunThread.start();
                }
            } else {
                llRadar.setBackgroundColor(Color.TRANSPARENT);
                ivRadar.setImageResource(R.drawable.shawn_icon_radar);
                tvRadar.setTextColor(getResources().getColor(R.color.text_color3));
                removeRadar();
            }


            //预警
        } else if (i == R.id.llWarning) {
            isShowWarning = !isShowWarning;
            if (isShowWarning) {
                layoutWarning.setVisibility(View.VISIBLE);
                tvWarningTypeStr.setVisibility(View.VISIBLE);
                tvWarningListStr.setVisibility(View.VISIBLE);
                sbWarning.setVisibility(View.VISIBLE);
                if (gridviewWarning != null) {
                    gridviewWarning.setVisibility(View.VISIBLE);
                }
                llWarning.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivWarning.setImageResource(R.drawable.shawn_icon_warning_press);
                tvWarning.setTextColor(Color.WHITE);
                if (warningList.size() <= 0) {
                    OkHttpWarning();
                } else {
                    addWarningMarkers();
                }
            } else {
                layoutWarning.setVisibility(View.GONE);
                tvWarningTypeStr.setVisibility(View.GONE);
                tvWarningListStr.setVisibility(View.GONE);
                sbWarning.setVisibility(View.GONE);
                if (gridviewWarning != null) {
                    gridviewWarning.setVisibility(View.GONE);
                }
                llWarning.setBackgroundColor(Color.TRANSPARENT);
                ivWarning.setImageResource(R.drawable.shawn_icon_warning);
                tvWarning.setTextColor(getResources().getColor(R.color.text_color3));
                switchWarningMarkersPolygons();
            }

        } else if (i == R.id.tvWaringList) {
            Intent intent = new Intent(mContext, ShawnWarningListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("warningList", (ArrayList<? extends Parcelable>) warningList);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (i == R.id.ivWarningClose) {
            if (reWarningPrompt.getVisibility() == View.VISIBLE) {
                animationUpToDown(reWarningPrompt);
                sbWarning.setChecked(false);
            } else {
                animationDownToUp(reWarningPrompt);
                sbWarning.setChecked(true);
            }


            //天气预报
        } else if (i == R.id.llFore) {
            isShowFore = !isShowFore;
            if (isShowFore) {
                llFore.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivFore.setImageResource(R.drawable.shawn_icon_fore_press);
                tvFore.setTextColor(Color.WHITE);
                if (allCitysMap.size() <= 0) {
                    OkHttpAllCitys();
                } else {
                    moveMapLoadFore(zoom);
                }
            } else {
                llFore.setBackgroundColor(Color.TRANSPARENT);
                ivFore.setImageResource(R.drawable.shawn_icon_fore);
                tvFore.setTextColor(getResources().getColor(R.color.text_color3));
                removeForeMarkers();
            }


            //分钟降水
        } else if (i == R.id.llMinute) {
            isShowMinute = !isShowMinute;
            if (isShowMinute) {
                layoutMinute.setVisibility(View.VISIBLE);
//                    tvMinuteStr.setVisibility(View.VISIBLE);
//                    sbMinute.setVisibility(View.VISIBLE);
                llMinute.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivMinute.setImageResource(R.drawable.shawn_icon_minute_press);
                tvMinute.setTextColor(Color.WHITE);
                if (clickLatLng != null) {
                    addLocationMarker();
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude);
                    OkHttpMinute(clickLatLng.longitude, clickLatLng.latitude);
                }
            } else {
                layoutMinute.setVisibility(View.GONE);
                tvMinuteStr.setVisibility(View.GONE);
                sbMinute.setVisibility(View.GONE);
                llMinute.setBackgroundColor(Color.TRANSPARENT);
                ivMinute.setImageResource(R.drawable.shawn_icon_minute);
                tvMinute.setTextColor(getResources().getColor(R.color.text_color3));
            }

        } else if (i == R.id.ivMinuteClose) {
            if (reMinutePrompt.getVisibility() == View.VISIBLE) {
                animationUpToDown(reMinutePrompt);
                sbMinute.setChecked(false);
            } else {
                animationDownToUp(reMinutePrompt);
                sbMinute.setChecked(true);
            }


            //风场
        } else if (i == R.id.llWind) {
            isShowWind = !isShowWind;
            if (isShowWind) {
                layoutWind.setVisibility(View.VISIBLE);
                tvWindTypeStr.setVisibility(View.VISIBLE);
                if (gridviewWind != null) {
                    gridviewWind.setVisibility(View.VISIBLE);
                }
                llWind.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivWind.setImageResource(R.drawable.shawn_icon_wind_press);
                tvWind.setTextColor(Color.WHITE);
                initWindTypeGridView();
                if (windDataGFS == null) {
                    OkHttpGFS();
                } else {
                    reloadWind();
                }
            } else {
                layoutWind.setVisibility(View.GONE);
                tvWindTypeStr.setVisibility(View.GONE);
                if (gridviewWind != null) {
                    gridviewWind.setVisibility(View.GONE);
                }
                llWind.setBackgroundColor(Color.TRANSPARENT);
                ivWind.setImageResource(R.drawable.shawn_icon_wind);
                tvWind.setTextColor(getResources().getColor(R.color.text_color3));
                windContainer2.removeAllViews();
                windContainer1.removeAllViews();
            }


            //数值预报
        } else if (i == R.id.llValue) {
            startActivity(new Intent(mContext, ShawnValueForecastActivity.class));
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

//    /**
//     * 申请权限
//     */
//    private void checkAuthority() {
//        if (Build.VERSION.SDK_INT < 23) {
//            initAll();
//        }else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, AuthorityUtil.AUTHOR_LOCATION);
//            }else {
//                initAll();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case AuthorityUtil.AUTHOR_LOCATION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initAll();
//                }else {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                        AuthorityUtil.intentAuthorSetting(this, "\""+getString(R.string.app_name)+"\""+"需要使用定位权限，是否前往设置？");
//                    }
//                }
//                break;
//        }
//    }

    /**
     * 申请多个权限
     */
    private void checkMultiAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            initAll();
        }else {
            AuthorityUtil.deniedList.clear();
            for (int i = 0; i < AuthorityUtil.allPermissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, AuthorityUtil.allPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    AuthorityUtil.deniedList.add(AuthorityUtil.allPermissions[i]);
                }
            }
            if (AuthorityUtil.deniedList.isEmpty()) {//所有权限都授予
                initAll();
            }else {
                String[] permissions = AuthorityUtil.deniedList.toArray(new String[AuthorityUtil.deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(this, permissions, AuthorityUtil.AUTHOR_MULTI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_MULTI:
                initAll();
                break;
        }
    }

}
