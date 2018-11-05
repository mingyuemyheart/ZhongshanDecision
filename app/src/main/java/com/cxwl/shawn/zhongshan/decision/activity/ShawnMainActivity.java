package com.cxwl.shawn.zhongshan.decision.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
import com.amap.api.maps.model.animation.ScaleAnimation;
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
import com.cxwl.shawn.zhongshan.decision.dto.FactDto;
import com.cxwl.shawn.zhongshan.decision.dto.RadarDto;
import com.cxwl.shawn.zhongshan.decision.dto.TyphoonDto;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;
import com.cxwl.shawn.zhongshan.decision.dto.WeatherDto;
import com.cxwl.shawn.zhongshan.decision.dto.WindData;
import com.cxwl.shawn.zhongshan.decision.dto.WindDto;
import com.cxwl.shawn.zhongshan.decision.manager.CloudManager;
import com.cxwl.shawn.zhongshan.decision.manager.RadarManager;
import com.cxwl.shawn.zhongshan.decision.util.AuthorityUtil;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;
import com.cxwl.shawn.zhongshan.decision.util.OkHttpUtil;
import com.cxwl.shawn.zhongshan.decision.util.SecretUrlUtil;
import com.cxwl.shawn.zhongshan.decision.util.WeatherUtil;
import com.cxwl.shawn.zhongshan.decision.util.sofia.Sofia;
import com.cxwl.shawn.zhongshan.decision.view.MinuteFallView;
import com.cxwl.shawn.zhongshan.decision.view.ScrollviewGridview;
import com.cxwl.shawn.zhongshan.decision.view.WaitWindView2;
import com.cxwl.shawn.zhongshan.decision.wheelview.NumericWheelAdapter;
import com.cxwl.shawn.zhongshan.decision.wheelview.OnWheelScrollListener;
import com.cxwl.shawn.zhongshan.decision.wheelview.WheelView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.suke.widget.SwitchButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShawnMainActivity extends ShawnBaseActivity implements View.OnClickListener, AMapLocationListener, AMap.OnMarkerClickListener,
        AMap.OnMapClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnCameraChangeListener {

    private Context mContext;
    private String userAuthority = "-1";//用户权限，3为专业用户，其它为普通用户
    private AVLoadingIndicatorView loadingView;
    private ScrollView scrollView;
    private boolean scaleAnimation = true;
    private TextureMapView mapView;
    private AMap aMap;//高德地图
    private int AMapType = AMap.MAP_TYPE_NORMAL;
    private float zoom = 4.0f,zoom1 = 7.0f,zoom2 = 9.0f;
    private Bundle savedInstanceState;
    private LinearLayout llBack,llMenu,llTyphoon,llFact,llSatelite,llRadar,llWarning,llFore,llMinute,llWind,llValue;
    private ImageView ivBack,ivRefresh,ivMenu,ivTyphoon,ivFact,ivSatelite,ivRadar,ivWarning,ivFore,ivMinute,ivWind,ivValue;
    private TextView tvBack,tvTyphoonName,tvTyphoon,tvFact,tvSatelite,tvRadar,tvWarning,tvFore,tvMinute,tvWind,tvValue;
    private AMapLocationClientOption mLocationOption;//声明mLocationOption对象
    private AMapLocationClient mLocationClient;//声明AMapLocationClient类对象
    private String locationAdcode = "442000", clickAdcode = "442000";//定位点对应行政区划
    private LatLng locationLatLng = new LatLng(22.519470, 113.356614),clickLatLng = new LatLng(22.519470, 113.356614);
    private SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf5 = new SimpleDateFormat("MM月dd日HH时", Locale.CHINA);
    private SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf7 = new SimpleDateFormat("HH", Locale.CHINA);
    private SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.CHINA);
    private SimpleDateFormat sdf9 = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf10 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private SimpleDateFormat sdf11 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    private String TYPE_TYPHOON = "type_typhoon", TYPE_FACT = "type_fact", TYPE_WARNING= "type_warning", TYPE_FORE = "type_fore";
    private int width, height;
    private String MAPCLICK_TYPHOON = "mapclick_typhoon", MAPCLICK_WARNING = "mapclick_warning", MAPCLICK_MINUTE = "mapclick_minute";//区分点击地图时，是哪个要素点击

    //台风
    private boolean isShowTyphoon = true;//是否显示台风layout
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

    private Map<String, String> typhoonNameMap = new LinkedHashMap<>();//台风名称
    private Map<String, List<Polyline>> factLinesMap = new LinkedHashMap<>();//实线数据
    private Map<String, List<Polyline>> foreLinesMap = new LinkedHashMap<>();//虚线数据
    private Map<String, List<Marker>> markerPointsMap = new LinkedHashMap<>();//台风点数据
    private Map<String, Marker> rotateMarkersMap = new LinkedHashMap<>();//台风旋转markers
    private Map<String, Marker> factTimeMarkersMap = new LinkedHashMap<>();//最后一个实况点时间markers
    private Map<String, Marker> foreTimeMarkersMap = new LinkedHashMap<>();//预报点时间markers
    private Map<String, List<Polyline>> rangeLinesMap = new LinkedHashMap<>();//测距虚线数据
    private Map<String, Marker> rangeMarkersMap = new LinkedHashMap<>();//测距中点距离marker
    private Map<String, TyphoonDto> lastFactPointMap = new LinkedHashMap<>();//最后一个实况点数据集合

    private Text text7, text10;
    private List<Polygon> windCirclePolygons = new ArrayList<>();//风圈
    private boolean isShowInfoWindow = true;//是否显示气泡
    private boolean isShowTime = false;//是否显示台风实况、预报时间
    private boolean isRanging = false;//是否允许测距
    private Marker locationMarker,clickMarker, zhongshanMarker;
    private final int DRAW_TYPHOON_COMPLETE = 1002;//一个台风绘制结束
    private Circle circle100, circle300, circle500;//定位点对一个的区域圈
    private Text text100, text300, text500;//定位点对一个的区域圈文字
    private ImageView ivList,ivRange,ivTyphoonClose,ivLegend,ivLegendClose,ivLocation;
    private TextView tvCurrent,tvHistory,tvTyphoonYear,tvLink,tvLinkRadar,tvLinkCloud;
    private RelativeLayout lyoutTyphoon,reTyphoonList,reLegend;
    private String typhoonPointAddr = "";
    private boolean isShowLink = false, isLinkRadar = false, isLinkCloud = false;//联动设置
    private LinearLayout llLink;

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
    private TextView tvFactStr,tvCloudStr,tvRadarStr,tvWarningTypeStr,tvWarningListStr,tvWindTypeStr,tvMinuteStr;//预警类型字符串，风场数据字符串
    private SwitchButton sbFact,sbCloud,sbRadar,sbWarning,sbMinute;

    //实况
    private boolean isShowFact = false;//是否显示实况
    private boolean isShowFactPoint = true;//默认显示是矿站点
    private RelativeLayout layoutFact,reFactPrompt;
    private LinearLayout llFactContainerRain,llFactContainerTemp,llFactContainerWind;
    private TextView tvFactTime,tvFactPoint,tvFactList;
    private ImageView ivFactClose,ivFactLegend;
    private Bitmap factBitmap;
    private GroundOverlay factOverlay;
    private List<FactDto> factRains1 = new ArrayList<>();
    private List<FactDto> factRains3 = new ArrayList<>();
    private List<FactDto> factRains6 = new ArrayList<>();
    private List<FactDto> factRains12 = new ArrayList<>();
    private List<FactDto> factRains24 = new ArrayList<>();
    private List<FactDto> factTemps1 = new ArrayList<>();
    private List<FactDto> factWindsJd1 = new ArrayList<>();
    private List<FactDto> factWindsJd24 = new ArrayList<>();
    private List<FactDto> factWindsZd1 = new ArrayList<>();
    private List<FactDto> factWindsZd24 = new ArrayList<>();
    private List<Marker> factMarkers = new ArrayList<>();
    private String currentFactChartImgUrl;//当前选中的实况图层imgurl

    //卫星拼图
    private boolean isShowCloud = false;//是否显示卫星拼图
    private RelativeLayout layoutCloud,reCloudPrompt;
    private ImageView ivCloudClose,ivCloudPlay;
    private SeekBar cloudSeekbar;
    private TextView tvCloudTime;
    private List<RadarDto> cloudList = new ArrayList<>();
    private GroundOverlay cloudOverlay;
    private CloudManager cloudManager;
    private CloudThread cloudThread;

    //雷达拼图
    private boolean isShowRadar = false;//是否显示雷达拼图
    private RelativeLayout layoutRadar,reRadarPrompt;
    private ImageView ivRadarClose,ivRadarPlay;
    private SeekBar radarSeekbar;
    private TextView tvRadarTime;
    private List<RadarDto> radarList = new ArrayList<>();
    private GroundOverlay radarOverlay;
    private RadarManager radarManager;
    private RadarThread radarThread;

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
    private List<WeatherDto> foreDataList1 = new ArrayList<>();//所有城市天气信息
    private List<WeatherDto> foreDataList2 = new ArrayList<>();//所有城市天气信息
    private List<WeatherDto> foreDataList3 = new ArrayList<>();//所有城市天气信息
    private final String level1 = "level1", level2 = "level2", level3 = "level3";
    private List<Marker> foreMarkers = new ArrayList<>();
    private LatLng leftlatlng = new LatLng(-16.305714763804854,75.13831436634065);
    private LatLng rightLatlng = new LatLng(63.681687310440864,135.21788656711578);

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

                //添加中山市标记
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(22.517645,113.392782));
                options.anchor(0.5f, 0.5f);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.shawn_zhongshan_marker_icon, null);
                TextView tvMarker = mView.findViewById(R.id.tvMarker);
                options.icon(BitmapDescriptorFactory.fromView(mView));
                if (zhongshanMarker != null) {
                    zhongshanMarker.remove();
                }
                zhongshanMarker = aMap.addMarker(options);
                zhongshanMarker.setClickable(false);
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

        //判断是否为专业用户
        if (getIntent().hasExtra("type")) {
            userAuthority = getIntent().getStringExtra("type");
            if (TextUtils.equals(userAuthority, "3")) {
                llValue.setVisibility(View.VISIBLE);
            }else {
                llValue.setVisibility(View.GONE);
            }
        }else {
            llValue.setVisibility(View.GONE);
        }

        //台风
        tvTyphoonName = findViewById(R.id.tvTyphoonName);
        ivRange = findViewById(R.id.ivRange);
        ivRange.setOnClickListener(this);
        tvLink = findViewById(R.id.tvLink);
        tvLink.setOnClickListener(this);
        tvLinkRadar = findViewById(R.id.tvLinkRadar);
        tvLinkRadar.setOnClickListener(this);
        tvLinkCloud = findViewById(R.id.tvLinkCloud);
        tvLinkCloud.setOnClickListener(this);
        llLink = findViewById(R.id.llLink);
        tvCurrent = findViewById(R.id.tvCurrent);
        tvCurrent.setOnClickListener(this);
        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setOnClickListener(this);
        tvTyphoonYear = findViewById(R.id.tvTyphoonYear);
        tvTyphoonYear.setOnClickListener(this);
        tvTyphoonYear.getPaint().setAntiAlias(true);
        tvTyphoonYear.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvTyphoonYear.setText(Calendar.getInstance().get(Calendar.YEAR)+"年");
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
        tvFactStr = findViewById(R.id.tvFactStr);
        tvCloudStr = findViewById(R.id.tvCloudStr);
        tvRadarStr = findViewById(R.id.tvRadarStr);
        tvWarningTypeStr = findViewById(R.id.tvWarningTypeStr);
        tvWarningListStr = findViewById(R.id.tvWarningListStr);
        tvWindTypeStr = findViewById(R.id.tvWindTypeStr);
        tvMinuteStr = findViewById(R.id.tvMinuteStr);
        sbFact = findViewById(R.id.sbFact);
        sbCloud = findViewById(R.id.sbCloud);
        sbRadar = findViewById(R.id.sbRadar);
        sbWarning = findViewById(R.id.sbWarning);
        sbMinute = findViewById(R.id.sbMinute);
        sbFact.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    animationDownToUp(reFactPrompt);
                }else {
                    animationUpToDown(reFactPrompt);
                }
            }
        });
        sbCloud.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    animationDownToUp(reCloudPrompt);
                }else {
                    animationUpToDown(reCloudPrompt);
                }
            }
        });
        sbRadar.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    animationDownToUp(reRadarPrompt);
                }else {
                    animationUpToDown(reRadarPrompt);
                }
            }
        });
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

        //实况
        layoutFact = findViewById(R.id.layoutFact);
        reFactPrompt = findViewById(R.id.reFactPrompt);
        llFactContainerRain = findViewById(R.id.llFactContainerRain);
        llFactContainerTemp = findViewById(R.id.llFactContainerTemp);
        llFactContainerWind = findViewById(R.id.llFactContainerWind);
        ivFactClose = findViewById(R.id.ivFactClose);
        ivFactClose.setOnClickListener(this);
        ivFactLegend = findViewById(R.id.ivFactLegend);
        tvFactTime = findViewById(R.id.tvFactTime);
        tvFactList = findViewById(R.id.tvFactList);
        tvFactList.setOnClickListener(this);
        tvFactPoint = findViewById(R.id.tvFactPoint);
        tvFactPoint.setOnClickListener(this);

        //卫星拼图
        layoutCloud = findViewById(R.id.layoutCloud);
        reCloudPrompt = findViewById(R.id.reCloudPrompt);
        ivCloudClose = findViewById(R.id.ivCloudClose);
        ivCloudClose.setOnClickListener(this);
        ivCloudPlay = findViewById(R.id.ivCloudPlay);
        ivCloudPlay.setOnClickListener(this);
        tvCloudTime = findViewById(R.id.tvCloudTime);
        cloudSeekbar = findViewById(R.id.cloudSeekbar);
        cloudSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (cloudThread != null) {
                    cloudThread.setCurrent(seekBar.getProgress());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (cloudThread != null) {
                    cloudThread.startTracking();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (cloudThread != null) {
                    cloudThread.stopTracking();
                }
            }
        });

        cloudManager = new CloudManager(mContext);

        //雷达拼图
        layoutRadar = findViewById(R.id.layoutRadar);
        reRadarPrompt = findViewById(R.id.reRadarPrompt);
        ivRadarClose = findViewById(R.id.ivRadarClose);
        ivRadarClose.setOnClickListener(this);
        ivRadarPlay = findViewById(R.id.ivRadarPlay);
        ivRadarPlay.setOnClickListener(this);
        tvRadarTime = findViewById(R.id.tvRadarTime);
        radarSeekbar = findViewById(R.id.radarSeekbar);
        radarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (radarThread != null) {
                    radarThread.setCurrent(seekBar.getProgress());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (radarThread != null) {
                    radarThread.startTracking();
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (radarThread != null) {
                    radarThread.stopTracking();
                }
            }
        });

        radarManager = new RadarManager(mContext);
        geocoderSearch = new GeocodeSearch(mContext);

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
     * 台风切换年份
     * @param message
     */
    private void dialogTimeYear(String message) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_time_year, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final WheelView wheelViewYear = view.findViewById(R.id.wheelViewYear);
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(this,1950, curYear);
        numericWheelAdapter1.setLabel("年");
        wheelViewYear.setViewAdapter(numericWheelAdapter1);
        wheelViewYear.setCyclic(false);//是否可循环滑动
        wheelViewYear.setVisibleItems(7);
        wheelViewYear.setCurrentItem(curYear - 1950);
        wheelViewYear.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }
            @Override
            public void onScrollingFinished(WheelView wheel) {
            }
        });

        final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText(message);
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                tvTyphoonYear.setText(wheelViewYear.getCurrentItem()+1950+"年");
                OkHttpTyphoonList();
            }
        });
    }

    /**
     * 初始化台风发布单位列表
     */
    private void initYearListView() {
        publishList.clear();
        TyphoonDto dto = new TyphoonDto();
        dto.publishName = "北京台";
        dto.publishCode = "BABJ";
        dto.isSelected = true;
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "广州台";
        dto.publishCode = "BCGZ";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "香港台";
        dto.publishCode = "VHHH";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "日本台";
        dto.publishCode = "RJTD";
        publishList.add(dto);
        dto = new TyphoonDto();
        dto.publishName = "关岛台";
        dto.publishCode = "PGTW";
        publishList.add(dto);
//        dto = new TyphoonDto();
//        dto.publishName = "欧洲台";
//        dto.publishCode = "ECMF";
//        publishList.add(dto);
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
                data.isSelected = !data.isSelected;
                if (publishAdapter != null) {
                    publishAdapter.notifyDataSetChanged();
                }

                if (data.isSelected) {
                    //绘制所有选中的台风
                    for (TyphoonDto dto : selectList) {
                        isShowInfoWindow = true;
                        String name = dto.code+" "+dto.name+" "+dto.enName;
                        if (TextUtils.equals(data.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                            OkHttpTyphoonDetailBABJ(data.publishName, data.publishCode, dto.tId, dto.code, name);
                        }else {
                            OkHttpTyphoonDetailIdea(data.publishName, data.publishCode, dto.id, dto.code, name);
                        }
                    }
                }else {//清除选择的数据源对应的所有台风
                    for (TyphoonDto dto : selectList) {
                        if (TextUtils.equals(data.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                            clearAllPoints(data.publishCode+dto.tId);
                        }else {
                            clearAllPoints(data.publishCode+dto.id);
                        }
                    }
                }

                boolean atLeastOne = false;
                for (TyphoonDto pub : publishList) {
                    if (pub.isSelected) {
                        atLeastOne = true;
                    }
                }
                if (!atLeastOne) {
                    for (TyphoonDto nam : nameList) {
                        nam.isSelected = false;
                    }
                    if (nameAdapter != null) {
                        nameAdapter.notifyDataSetChanged();
                    }
                    selectList.clear();
                    typhoonNameMap.clear();
                    tvTyphoonName.setText("");
                    clearAllPoints(null);
                }

            }
        });

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
                    for (TyphoonDto pub : publishList) {
                        if (pub.isSelected) {
                            if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                                OkHttpTyphoonDetailBABJ(pub.publishName, pub.publishCode, dto.tId, dto.code, name);
                            }else {
                                OkHttpTyphoonDetailIdea(pub.publishName, pub.publishCode, dto.id, dto.code, name);
                            }
                        }
                    }
                }else {
                    if (typhoonNameMap.containsKey(dto.code)) {
                        typhoonNameMap.remove(dto.code);
                    }
                    String typhoonName = "";
                    for (String tSid : typhoonNameMap.keySet()) {
                        if (typhoonNameMap.containsKey(tSid)) {
                            typhoonName = typhoonName+typhoonNameMap.get(tSid)+"\n";
                        }
                    }
                    tvTyphoonName.setText(typhoonName);

                    for (TyphoonDto pub : publishList) {
                        if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                            clearAllPoints(pub.publishCode+dto.tId);

                        }else {
                            clearAllPoints(pub.publishCode+dto.id);
                        }
                    }
                    selectList.remove(dto);
                }

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
                String name = dto.code+" "+dto.name+" "+dto.enName;

                clearAllPoints(null);
                isShowInfoWindow = true;
                for (TyphoonDto pub : publishList) {
                    if (pub.isSelected) {
                        if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                            OkHttpTyphoonDetailBABJ(pub.publishName, pub.publishCode, dto.tId, dto.code, name);
                        }else {
                            OkHttpTyphoonDetailIdea(pub.publishName, pub.publishCode, dto.id, dto.code, name);
                        }
                    }
                }

            }
        });
    }

    /**
     * 获取某一年的台风列表信息
     */
    private void OkHttpTyphoonList() {
        loadingView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(tvTyphoonYear.getText().toString())) {
            return;
        }
        String selectYear = tvTyphoonYear.getText().toString().substring(0, tvTyphoonYear.getText().length()-1);
        final String url = "http://61.142.114.104:8080/zstyphoon/lhdata/zstf?type=0&year="+selectYear;
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
                                            if (!itemObj.isNull("TFWID")) {
                                                dto.tId = itemObj.getString("TFWID");
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
                                            if (!itemObj.isNull("CRTTIME")) {
                                                dto.createTime = itemObj.getString("CRTTIME");
                                            }
                                            if (!itemObj.isNull("status")) {
                                                dto.status = itemObj.getString("status");
                                                dto.isSelected = true;//生效台风默认选中状态
                                            }else {
                                                dto.status = "0";
                                            }
                                            if (!dto.code.contains("****")) {
                                                nameList.add(dto);
                                            }

                                            //把活跃台风过滤出来存放
                                            if (TextUtils.equals(dto.status, "1") && !dto.code.contains("****")) {
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
                                            for (TyphoonDto pub : publishList) {
                                                if (pub.isSelected) {
                                                    if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                                                        OkHttpTyphoonDetailBABJ(pub.publishName, pub.publishCode, dto.tId, dto.code, name);
                                                    }else {
                                                        OkHttpTyphoonDetailIdea(pub.publishName, pub.publishCode, dto.id, dto.code, name);
                                                    }
                                                }
                                            }
                                        }

                                        if (startList.size() <= 0) {// 没有生效台风
                                            tvTyphoonName.setText(getString(R.string.no_typhoon));
                                        }

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
     * @param publishCode
     * @param typhoonId
     * @param typhoonName
     */
    private void OkHttpTyphoonDetailIdea(final String publishName, final String publishCode, final String typhoonId, final String tSid, final String typhoonName) {
        loadingView.setVisibility(View.VISIBLE);
        final String url = String.format("http://61.142.114.104:8080/zstyphoon/lhdata/zstf?type=1&tsid=%s&fcid=%s", typhoonId, publishCode);
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
                                        Toast.makeText(mContext, "暂无"+publishName+typhoonName+"数据", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(mContext, "暂无"+publishName+typhoonName+"数据", Toast.LENGTH_SHORT).show();
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
                                        if (!itemObj.isNull("LEADTIME")) {
                                            int LEADTIME = itemObj.getInt("LEADTIME");
                                            if (!TextUtils.isEmpty(dto.time)) {
                                                try {
                                                    long time = sdf3.parse(dto.time).getTime()+1000*60*60*LEADTIME;
                                                    dto.time = sdf3.format(new Date(time));
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
                                            dto.wind_dir = CommonUtil.getWindDirection(fx);
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
                                        if (!itemObj.isNull("RD07") && !TextUtils.isEmpty(itemObj.getString("RD07"))) {
                                            dto.radius_7 = itemObj.getString("RD07");
                                        }else if (!itemObj.isNull("RR07") && !TextUtils.isEmpty(itemObj.getString("RR07"))) {
                                            String r = itemObj.getString("RR07");
                                            dto.radius_7 = r+","+r+","+r+","+r;
                                        }

                                        if (!itemObj.isNull("RD10") && !TextUtils.isEmpty(itemObj.getString("RD10"))) {
                                            dto.radius_10 = itemObj.getString("RD10");
                                        }else if (!itemObj.isNull("RR10") && !TextUtils.isEmpty(itemObj.getString("RR10"))) {
                                            String r = itemObj.getString("RR10");
                                            dto.radius_10 = r+","+r+","+r+","+r;
                                        }

                                        allPoints.add(dto);
                                    }

                                    loadingView.setVisibility(View.GONE);
                                    lyoutTyphoon.setVisibility(View.VISIBLE);
                                    //防止多个台风绘制不全
                                    try {
                                        drawTyphoon(publishName, publishCode+typhoonId,tSid, false, allPoints);
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
     * 获取台风网台风详情数据
     * @param publishName
     * @param publishCode
     * @param typhoonId
     * @param typhoonName
     */
    private void OkHttpTyphoonDetailBABJ(final String publishName, final String publishCode, final String typhoonId, final String tSid, final String typhoonName) {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "http://decision-admin.tianqi.cn/Home/extra/gettyphoon/view/"+typhoonId;
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
                                                List<TyphoonDto> allPoints = new ArrayList<>();//台风实点
                                                List<TyphoonDto> forePoints = new ArrayList<>();//台风预报点
                                                JSONArray array = obj.getJSONArray("typhoon");
                                                JSONArray itemArray = array.getJSONArray(8);
                                                for (int j = 0; j < itemArray.length(); j++) {
                                                    JSONArray itemArray2 = itemArray.getJSONArray(j);
                                                    TyphoonDto dto = new TyphoonDto();
                                                    if (!TextUtils.isEmpty(typhoonName)) {
                                                        dto.name = typhoonName;
                                                    }
                                                    if (!TextUtils.isEmpty(publishName)) {
                                                        dto.publishName = publishName;
                                                    }
                                                    long longTime = itemArray2.getLong(2);
                                                    dto.time = sdf3.format(new Date(longTime));

                                                    dto.lng = itemArray2.getDouble(4);
                                                    dto.lat = itemArray2.getDouble(5);
                                                    dto.pressure = itemArray2.getString(6);
                                                    dto.max_wind_speed = itemArray2.getString(7);
                                                    dto.move_speed = itemArray2.getString(9);
                                                    String fx_string = itemArray2.getString(8);
                                                    if( !TextUtils.isEmpty(fx_string)){
                                                        String windDir = "";
                                                        for (int i = 0; i < fx_string.length(); i++) {
                                                            String item = fx_string.substring(i, i+1);
                                                            if (TextUtils.equals(item, "N")) {
                                                                item = "北";
                                                            }else if (TextUtils.equals(item, "S")) {
                                                                item = "南";
                                                            }else if (TextUtils.equals(item, "W")) {
                                                                item = "西";
                                                            }else if (TextUtils.equals(item, "E")) {
                                                                item = "东";
                                                            }
                                                            windDir = windDir+item;
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
                                                            dto.radius_7 = itemArray10.getString(1)+","+itemArray10.getString(2)+","+itemArray10.getString(3)+","+itemArray10.getString(4);
                                                        }else if (m == 1) {
                                                            dto.radius_10 = itemArray10.getString(1)+","+itemArray10.getString(2)+","+itemArray10.getString(3)+","+itemArray10.getString(4);
                                                        }
                                                    }
                                                    allPoints.add(dto);

                                                    if (!itemArray2.get(11).equals("null") && !itemArray2.get(11).equals(null)) {
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
                                                                if (!TextUtils.isEmpty(publishName)) {
                                                                    data.publishName = publishName;
                                                                }
                                                                data.lng = itemArray11.getDouble(2);
                                                                data.lat = itemArray11.getDouble(3);
                                                                data.pressure = itemArray11.getString(4);
                                                                data.max_wind_speed = itemArray11.getString(5);

                                                                long t2 = itemArray11.getLong(0)*3600*1000;
                                                                long ttt = longTime+t2;
                                                                data.time = sdf3.format(new Date(ttt));

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
                                                allPoints.addAll(forePoints);

                                                loadingView.setVisibility(View.GONE);
                                                lyoutTyphoon.setVisibility(View.VISIBLE);
                                                //防止多个台风绘制不全
                                                try {
                                                    drawTyphoon(publishName, publishCode+typhoonId,tSid, false, allPoints);
                                                    Thread.sleep(300);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
        if (text7 != null) {
            text7.remove();
        }
        if (text10 != null) {
            text10.remove();
        }
        for (Polygon polygon : windCirclePolygons) {
            polygon.remove();
        }
        windCirclePolygons.clear();
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
     * @param typhoonId 对应发布单位编号+typhoonId
     */
    private void clearAllPoints(String typhoonId) {
        removeLocationCirces();
        removeWindCircle();
        removeRange(typhoonId);
        removeTyphoons(typhoonId);
    }

    /**
     * 绘制台风
     * @param typhoonId 对应发布单位编号+typhoonId
     * @param isAnimate
     */
    private void drawTyphoon(String publishName, String typhoonId, final String tSid, boolean isAnimate, List<TyphoonDto> list) {
        if (list.isEmpty()) {
            return;
        }

        if (mRoadThread != null) {
            mRoadThread.cancel();
            mRoadThread = null;
        }
        mRoadThread = new RoadThread(publishName, typhoonId, tSid, list, isAnimate);
        mRoadThread.start();
    }

    /**
     * 绘制台风点
     */
    private class RoadThread extends Thread {

        private boolean cancelled;
        private boolean isAnimate;
        private List<TyphoonDto> allPoints;//整个台风路径信息
        private String publishName, typhoonId, tSid;

        private RoadThread(String publishName, String typhoonId, String tSid, List<TyphoonDto> allPoints, boolean isAnimate) {
            this.publishName = publishName;
            this.typhoonId = typhoonId;
            this.tSid = tSid;
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

//                if (i == len-1) {
//                    Message msg = typhoonHandler.obtainMessage(DRAW_TYPHOON_COMPLETE);
//                    msg.obj = allPoints;
//                    typhoonHandler.sendMessage(msg);
//                }

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
                drawRoute(publishName, typhoonId, tSid, factLines, foreLines, markerPoints, firstPoint, lastPoint, lastFactPoint);
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
    private void drawRoute(String publishName, String typhoonId, String tSid, List<Polyline> factLines, List<Polyline> foreLines, List<Marker> markerPoints, TyphoonDto firstPoint, TyphoonDto lastPoint, TyphoonDto lastFactPoint) {
        if (lastPoint == null) {//最后一个点
            lastPoint = firstPoint;
        }
        if (lastFactPoint == null) {
            lastFactPoint = firstPoint;
        }

        int lineColor = 0;
        if (typhoonId.contains("BABJ")) {//北京台
            lineColor = getResources().getColor(R.color.colorPrimary);
        }else if (typhoonId.contains("BCGZ")) {//广州台
            lineColor = Color.RED;
        }else if (typhoonId.contains("VHHH")) {//香港台
            lineColor = Color.YELLOW;
        }else if (typhoonId.contains("RJTD")) {//日本台
            lineColor = Color.GREEN;
        }else if (typhoonId.contains("PGTW")) {//关岛台
            lineColor = Color.BLUE;
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
            polylineOptions.color(lineColor);
            latLngs.add(firstLatLng);
            latLngs.add(lastLatLng);
            polylineOptions.addAll(latLngs);
            Polyline factLine = aMap.addPolyline(polylineOptions);
            factLines.add(factLine);
        } else {//预报虚线
            double dis = Math.sqrt(Math.pow(firstLat-lastLat, 2)+ Math.pow(firstLng-lastLng, 2));
            int numPoint = (int) Math.floor(dis/0.1);
            double lng_per = (lastLng-firstLng)/numPoint;
            double lat_per = (lastLat-firstLat)/numPoint;
            for (int i = 0; i < numPoint; i++) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(lineColor);
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

        if (firstPoint == lastPoint) {//最后一个点，绘制发布源
            if (!TextUtils.isEmpty(publishName)) {
                TextView tvName = typhoonPoint.findViewById(R.id.tvName);
                tvName.setText(publishName);
                tvName.setVisibility(View.VISIBLE);
            }
        }

        ImageView ivPoint = typhoonPoint.findViewById(R.id.ivPoint);
        if (TextUtils.equals(firstPoint.type, "1")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level1);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level1_fore);
            }
        }else if (TextUtils.equals(firstPoint.type, "2")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level2);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level2_fore);
            }
        }else if (TextUtils.equals(firstPoint.type, "3")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level3);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level3_fore);
            }
        }else if (TextUtils.equals(firstPoint.type, "4")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level4);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level4_fore);
            }
        }else if (TextUtils.equals(firstPoint.type, "5")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level5);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level5_fore);
            }
        }else if (TextUtils.equals(firstPoint.type, "6")) {
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level6);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_level6_fore);
            }
        }else {//预报点
            if (firstPoint.isFactPoint) {//实况点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_yb);
            }else {//预报点
                ivPoint.setImageResource(R.drawable.shawn_typhoon_yb_fore);
            }
        }
        MarkerOptions options = new MarkerOptions();
        String content = firstPoint.time+"&"+firstPoint.max_wind_speed+"&"+firstPoint.type+"&"+firstPoint.pressure+"&"+firstPoint.radius_7+"&"+firstPoint.radius_10;
        String title1 = "("+publishName+")"+firstPoint.name+"|"+content+";";
        if (firstPoint.isFactPoint && lastFactPoint == firstPoint) {//最后一个实况点
            title1 = "("+publishName+")"+firstPoint.name+" (当前位置)"+"|"+content+";";
        }

        String isLastFactPoint;
        if (firstPoint.isFactPoint && lastFactPoint == firstPoint) {//最后一个实况点
            isLastFactPoint = "1";
        }else {
            isLastFactPoint = "-1";
        }
        String title2 = firstPoint.move_speed+"|"+lastPoint.move_speed+"|"+lastPoint.time+"|"+firstPoint.wind_dir+"|"+isLastFactPoint+";";//前后两个点速度，为计算强度字符串所用
        String title3 = firstPoint.radius_7+"|"+firstPoint.radius_10;
        options.title(title1+title2+title3);
        options.snippet(TYPE_TYPHOON);
        options.anchor(0.5f, 0.5f);
        options.position(firstLatLng);
        options.icon(BitmapDescriptorFactory.fromView(typhoonPoint));
        Marker marker = aMap.addMarker(options);
        markerPoints.add(marker);


        if (firstPoint.isFactPoint && lastFactPoint == firstPoint) {//最后一个实况点

            //增加台风名称对应等级
            String strength = "";
            if (!TextUtils.isEmpty(firstPoint.type)) {
                if (TextUtils.equals(firstPoint.type, "1")) {
                    strength = getString(R.string.typhoon_level1);
                }else if (TextUtils.equals(firstPoint.type, "2")) {
                    strength = getString(R.string.typhoon_level2);
                }else if (TextUtils.equals(firstPoint.type, "3")) {
                    strength = getString(R.string.typhoon_level3);
                }else if (TextUtils.equals(firstPoint.type, "4")) {
                    strength = getString(R.string.typhoon_level4);
                }else if (TextUtils.equals(firstPoint.type, "5")) {
                    strength = getString(R.string.typhoon_level5);
                }else if (TextUtils.equals(firstPoint.type, "6")) {
                    strength = getString(R.string.typhoon_level6);
                }
            }
            typhoonNameMap.put(tSid, firstPoint.name+"("+strength+")");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String typhoonName = "";
                    for (String tSid : typhoonNameMap.keySet()) {
                        if (typhoonNameMap.containsKey(tSid)) {
                            typhoonName = typhoonName+typhoonNameMap.get(tSid)+"\n";
                        }
                    }
                    tvTyphoonName.setText(typhoonName);
                }
            });

            //最后一个实况点显示info时
            if(isShowInfoWindow) {
                clickMarker = marker;
                clickMarker.showInfoWindow();

                //绘制最后一个实况点对应的七级、十级风圈
                drawWindCircle(firstPoint.radius_7, firstPoint.radius_10, firstLatLng);

                //最后一个实况点处于屏幕中心
                aMap.animateCamera(CameraUpdateFactory.newLatLng(firstLatLng));
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

        //七级风圈
        if (!TextUtils.isEmpty(radius_7) && !TextUtils.equals(radius_7, "null") && radius_7.contains(",")) {
            String[] radiuss = radius_7.split(",");
            List<LatLng> wind7Points = new ArrayList<>();
            getWindCirclePoints(center, radiuss[0], 0, wind7Points);
            getWindCirclePoints(center, radiuss[3], 90, wind7Points);
            getWindCirclePoints(center, radiuss[2], 180, wind7Points);
            getWindCirclePoints(center, radiuss[1], 270, wind7Points);
            if (wind7Points.size() > 0) {
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.strokeWidth(3).strokeColor(Color.YELLOW).fillColor(0x20FFFF00);
                for (int i = 0; i < wind7Points.size(); i++) {
                    LatLng latLng = wind7Points.get(i);
                    polygonOptions.add(latLng);

                    if (i == 0) {
                        TextOptions textOptions = new TextOptions();
                        textOptions.backgroundColor(0x30000000);
                        textOptions.text("七级风圈");
                        textOptions.fontColor(Color.YELLOW);
                        textOptions.fontSize(30);
                        textOptions.position(latLng);
                        text7 = aMap.addText(textOptions);
                    }
                }
                Polygon polygon = aMap.addPolygon(polygonOptions);
                windCirclePolygons.add(polygon);
            }
        }

        //十级风圈
        if (!TextUtils.isEmpty(radius_10) && !TextUtils.equals(radius_10, "null") && radius_10.contains(",")) {
            String[] radiuss = radius_10.split(",");
            List<LatLng> wind10Points = new ArrayList<>();
            getWindCirclePoints(center, radiuss[0], 0, wind10Points);
            getWindCirclePoints(center, radiuss[3], 90, wind10Points);
            getWindCirclePoints(center, radiuss[2], 180, wind10Points);
            getWindCirclePoints(center, radiuss[1], 270, wind10Points);
            if (wind10Points.size() > 0) {
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.strokeWidth(3).strokeColor(Color.RED).fillColor(0x20FF0000);
                for (int i = 0; i < wind10Points.size(); i++) {
                    LatLng latLng = wind10Points.get(i);
                    polygonOptions.add(latLng);

                    if (i == 0) {
                        TextOptions textOptions = new TextOptions();
                        textOptions.backgroundColor(0x30000000);
                        textOptions.text("10级风圈");
                        textOptions.fontColor(Color.RED);
                        textOptions.fontSize(30);
                        textOptions.position(latLng);
                        text10 = aMap.addText(textOptions);
                    }
                }
                Polygon polygon = aMap.addPolygon(polygonOptions);
                windCirclePolygons.add(polygon);
            }
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
            double r = 6371000.79;
            int numpoints = 90;
            double phase = Math.PI/2 / numpoints;

            for (int i = 0; i <= numpoints; i++) {
                double dx = (Integer.valueOf(radius)*1000 * Math.cos((i+startAngle) * phase));
                double dy = (Integer.valueOf(radius)*1000 * Math.sin((i+startAngle) * phase));//乘以1.6 椭圆比例
                double lng = center.longitude + dx / (r * Math.cos(center.latitude * Math.PI / 180) * Math.PI / 180);
                double lat = center.latitude + dy / (r * Math.PI / 180);
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

            LatLng quarter = null;//距离点击测距点1/4距离
            for (int i = 0; i < numPoint; i++) {
                PolylineOptions line = new PolylineOptions();
                line.color(0xff6291E1);
                line.width(CommonUtil.dip2px(mContext, 2));
                ranges.add(new LatLng(locationLat+i*lat_per, locationLng+i*lng_per));
                if (i == numPoint/5) {
                    quarter = new LatLng(locationLat+i*lat_per, locationLng+i*lng_per);
                }
                if (i % 2 == 1) {
                    line.addAll(ranges);
                    Polyline polyline = aMap.addPolyline(line);
                    polylines.add(polyline);
                    ranges.clear();
                }
            }
            rangeLinesMap.put(typhoonId, polylines);

            if (quarter != null) {
                addRangeMarker(typhoonId, quarter, locationLng, locationLat, lng, lat);
            }
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
        options.anchor(0.5f, 1.0f);
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromView(mView));
        Marker marker = aMap.addMarker(options);
        marker.setClickable(false);
        rangeMarkersMap.put(typhoonId, marker);
    }

    /**
     * 根据台风正点时间获取对应台风点云图
     * @param time
     */
    private void OkHttpPointCloudIMg(String time) {
        removeCloudOverlay();
        if (!isLinkCloud || TextUtils.isEmpty(time)) {
            return;
        }
        final String cloudUrl = String.format("http://scapi.weather.com.cn/weather/getyt?statdate=%s&enddate=%s&test=ncg", time, time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(cloudUrl).build(), new Callback() {
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
                                        JSONArray array = new JSONArray(result);
                                        if (array.length() > 0) {
                                            JSONObject obj = array.getJSONObject(0);
                                            if (!obj.isNull("url")) {
                                                String imgUrl = obj.getString("url");
                                                if (!TextUtils.isEmpty(imgUrl)) {
                                                    Picasso.get().load(imgUrl).into(new Target() {
                                                        @Override
                                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                            if (bitmap != null) {
                                                                drawCloud(bitmap, 56.385845314127209,62.8820698883665,-10.787277369124666,161.69675114151386);
                                                            }
                                                        }
                                                        @Override
                                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                                        }
                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                        }
                                                    });
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
                });
            }
        }).start();

        removeRadarOverlay();
        if (!isLinkRadar || TextUtils.isEmpty(time)) {
            return;
        }
        final String radarUrl = String.format("http://scapi.weather.com.cn/weather/ldt?statdate=%s&enddate=%s&test=ncg", time, time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(radarUrl).build(), new Callback() {
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
                                        JSONArray array = new JSONArray(result);
                                        if (array.length() > 0) {
                                            JSONObject obj = array.getJSONObject(0);
                                            if (!obj.isNull("url")) {
                                                String imgUrl = obj.getString("url");
                                                if (!TextUtils.isEmpty(imgUrl)) {
                                                    Picasso.get().load(imgUrl).into(new Target() {
                                                        @Override
                                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                            if (bitmap != null) {
                                                                drawRadar(bitmap, 3.9079,71.9282,57.9079,134.8656);
                                                            }
                                                        }
                                                        @Override
                                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                                        }
                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                        }
                                                    });
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
                });
            }
        }).start();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker != locationMarker && marker != zhongshanMarker) {
            String markerType = marker.getSnippet();
            if (!TextUtils.isEmpty(markerType)) {
                if (TextUtils.equals(markerType, TYPE_TYPHOON)) {//点击台风点
                    String[] titles = marker.getTitle().split(";");
                    if (!TextUtils.isEmpty(titles[1])) {
                        String[] title1 = titles[1].split("\\|");
                        if (!TextUtils.isEmpty(title1[2])) {
                            try {
                                String time = sdf2.format(sdf3.parse(title1[2]));
                                OkHttpPointCloudIMg(time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(titles[2])) {
                        String[] circles = titles[2].split("\\|");
                        drawWindCircle(circles[0], circles[1], marker.getPosition());
                        searchAddrByLatLng(marker.getPosition().latitude, marker.getPosition().longitude, MAPCLICK_TYPHOON);//参考位置
                    }
                }else if (TextUtils.equals(markerType, TYPE_WARNING)) {//点击预警marker
                    //点击预警marker
                }else if (TextUtils.equals(markerType, TYPE_FORE)) {//点击预报marker
                    //点击预报marker
                }else if (TextUtils.equals(markerType, TYPE_FACT)) {//点击实况marker
                    //点击实况marker
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
            clickLatLng = latLng;
            addLocationMarker();
            addLocationCircles();
            removeRange(null);
            ranging(null);
        }

        if (isShowMinute) {//分钟降水
            clickLatLng = latLng;
            addLocationMarker();
            searchAddrByLatLng(latLng.latitude, latLng.longitude, MAPCLICK_MINUTE);
            OkHttpMinute(latLng.longitude, latLng.latitude);
        }

        if (isShowWarning) {//预警
            clickLatLng = latLng;
            addLocationMarker();
            searchAddrByLatLng(latLng.latitude, latLng.longitude, MAPCLICK_WARNING);
        }

        mapClick();
    }

    private void mapClick() {
        if (clickMarker != null && clickMarker.isInfoWindowShown()) {
            clickMarker.hideInfoWindow();
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (TextUtils.equals(marker.getSnippet(), TYPE_TYPHOON)) {//台风
            view = inflater.inflate(R.layout.shawn_typhoon_marker_icon, null);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvTime = view.findViewById(R.id.tvTime);
            TextView tvWindStr = view.findViewById(R.id.tvWindStr);
            TextView tvWind = view.findViewById(R.id.tvWind);
            TextView tvPressureStr = view.findViewById(R.id.tvPressureStr);
            TextView tvPressure = view.findViewById(R.id.tvPressure);
            TextView tvCircle7Str = view.findViewById(R.id.tvCircle7Str);
            TextView tvCircle7 = view.findViewById(R.id.tvCircle7);
            TextView tvCircle10Str = view.findViewById(R.id.tvCircle10Str);
            TextView tvCircle10 = view.findViewById(R.id.tvCircle10);
            TextView tvPosition = view.findViewById(R.id.tvPosition);
            TextView tvResultStr = view.findViewById(R.id.tvResultStr);
            TextView tvResult = view.findViewById(R.id.tvResult);
            TextView divider2 = view.findViewById(R.id.divider2);
            TextView divider3 = view.findViewById(R.id.divider3);
            TextView divider4 = view.findViewById(R.id.divider4);
            TextView divider5 = view.findViewById(R.id.divider5);
            TextView divider6 = view.findViewById(R.id.divider6);
            ImageView ivDelete = view.findViewById(R.id.ivDelete);
            if (!TextUtils.isEmpty(marker.getTitle())) {
                String[] titles = marker.getTitle().split(";");

                //台风内容
                String[] title1 = titles[0].split("\\|");
                if (!TextUtils.isEmpty(title1[0])) {
                    tvName.setText(title1[0]);
                }
                if (!TextUtils.isEmpty(title1[1])) {
                    String[] content = title1[1].split("&");
                    tvTime.setText(content[0]);
                    if(!TextUtils.isEmpty(content[1])){
                        String strength = "";
                        if (!TextUtils.isEmpty(content[2])) {
                            if (TextUtils.equals(content[2], "1")) {
                                strength = getString(R.string.typhoon_level1);
                            }else if (TextUtils.equals(content[2], "2")) {
                                strength = getString(R.string.typhoon_level2);
                            }else if (TextUtils.equals(content[2], "3")) {
                                strength = getString(R.string.typhoon_level3);
                            }else if (TextUtils.equals(content[2], "4")) {
                                strength = getString(R.string.typhoon_level4);
                            }else if (TextUtils.equals(content[2], "5")) {
                                strength = getString(R.string.typhoon_level5);
                            }else if (TextUtils.equals(content[2], "6")) {
                                strength = getString(R.string.typhoon_level6);
                            }
                        }
                        tvWind.setText(WeatherUtil.getHourWindForce(Float.parseFloat(content[1]))+"("+content[1]+"米/秒)，"+strength);
                    }

                    if (TextUtils.isEmpty(content[3]) || TextUtils.equals(content[3], "null")) {
                        tvPressureStr.setVisibility(View.GONE);
                        tvPressure.setVisibility(View.GONE);
                        divider2.setVisibility(View.GONE);
                    }else {
                        tvPressureStr.setVisibility(View.VISIBLE);
                        tvPressure.setVisibility(View.VISIBLE);
                        divider2.setVisibility(View.VISIBLE);
                    }
                    tvPressure.setText(content[3]+"hPa");

                    if (TextUtils.isEmpty(content[4]) || TextUtils.equals(content[4], "null")) {
                        tvCircle7Str.setVisibility(View.GONE);
                        tvCircle7.setVisibility(View.GONE);
                        divider3.setVisibility(View.GONE);
                    }else {
                        tvCircle7Str.setVisibility(View.VISIBLE);
                        tvCircle7.setVisibility(View.VISIBLE);
                        divider3.setVisibility(View.VISIBLE);
                        String[] r = content[4].split(",");
                        int max = 0;
                        for (int i = 0; i < r.length; i++) {
                            if (max <= Integer.valueOf(r[i])) {
                                max = Integer.valueOf(r[i]);
                            }
                        }
                        tvCircle7.setText(max+"公里");
                    }

                    if (TextUtils.isEmpty(content[5]) || TextUtils.equals(content[5], "null")) {
                        tvCircle10Str.setVisibility(View.GONE);
                        tvCircle10.setVisibility(View.GONE);
                        divider4.setVisibility(View.GONE);
                    }else {
                        tvCircle10Str.setVisibility(View.VISIBLE);
                        tvCircle10.setVisibility(View.VISIBLE);
                        divider4.setVisibility(View.VISIBLE);
                        String[] r = content[5].split(",");
                        int max = 0;
                        for (int i = 0; i < r.length; i++) {
                            if (max <= Integer.valueOf(r[i])) {
                                max = Integer.valueOf(r[i]);
                            }
                        }
                        tvCircle10.setText(max+"公里");
                    }
                }

                //预报结论
                String[] title2 = titles[1].split("\\|");

                String strength = "";
                float currentSpeed = 0, nextSpeed = 0;
                if (!TextUtils.isEmpty(title2[0])) {
                    currentSpeed = Float.valueOf(title2[0]);

                }
                if (!TextUtils.isEmpty(title2[1])) {
                    nextSpeed = Float.valueOf(title2[1]);
                }
                if (currentSpeed > nextSpeed) {
                    strength = "强度逐渐减弱";
                }else {
                    strength = "强度逐渐增强";
                }

//                String time = "";
//                if (!TextUtils.isEmpty(title2[2])) {
//                    try {
//                        time = "(下次更新时间为"+sdf5.format(sdf3.parse(title2[2]))+")";
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }

                if (TextUtils.isEmpty(typhoonPointAddr)) {
                    //选重点距离中山市多少千米
                    float distance = AMapUtils.calculateLineDistance(new LatLng(22.517645,113.392782), marker.getPosition())/1000;
                    BigDecimal bd = new BigDecimal(distance).setScale(1, RoundingMode.UP);
                    typhoonPointAddr = marker.getPosition().longitude+"E"+" "+marker.getPosition().latitude+"N，距离中山市"+bd.floatValue()+"公里";
                }
                tvPosition.setText(typhoonPointAddr);

                if (!TextUtils.isEmpty(title2[4]) && !TextUtils.equals(title2[4], "null") && TextUtils.equals(title2[4], "1")) {//最后一个实况点才显示预报结论
                    String result;
                    if (!TextUtils.isEmpty(title2[3]) && !TextUtils.equals(title2[3], "null")) {
                        result = "将以每小时"+currentSpeed+"公里左右的速度向\n"+title2[3]+"方向移动";
                    }else {
                        result = "";
                    }
                    if (currentSpeed <= 0) {
                        result = "";
                    }
                    if (TextUtils.isEmpty(result)) {
                        tvResultStr.setVisibility(View.GONE);
                        tvResult.setVisibility(View.GONE);
                        divider6.setVisibility(View.GONE);
                    }else {
                        tvResultStr.setVisibility(View.VISIBLE);
                        tvResult.setVisibility(View.VISIBLE);
                        divider6.setVisibility(View.VISIBLE);
                    }
                    tvResult.setText(result);
                }else {
                    tvResultStr.setVisibility(View.GONE);
                    tvResult.setVisibility(View.GONE);
                    divider6.setVisibility(View.GONE);
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
        }else if (TextUtils.equals(marker.getSnippet(), TYPE_FORE)) {//天气预报
            view = inflater.inflate(R.layout.shawn_fore_marker_info, null);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvInfo = view.findViewById(R.id.tvInfo);

            String[] titles = marker.getTitle().split(",");
            tvName.setText(titles[1]);
            String phe = getString(WeatherUtil.getWeatherId(Integer.valueOf(titles[2]))) + "~" + getString(WeatherUtil.getWeatherId(Integer.valueOf(titles[5])));
            if (Integer.valueOf(titles[2]) == Integer.valueOf(titles[5])) {
                phe = getString(WeatherUtil.getWeatherId(Integer.valueOf(titles[2])));
            }
            String temp = titles[3]+"~"+titles[7]+"℃";
            String windDir = getString(WeatherUtil.getWindDirection(Integer.valueOf(titles[4])));
            String windForce = WeatherUtil.getFactWindForce(Integer.valueOf(titles[5]));
            try {
                tvInfo.setText(phe+"，"+temp+"，"+windDir+windForce+"\n"+sdf6.format(sdf11.parse(titles[11]))+"发布");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if (TextUtils.equals(marker.getSnippet(), TYPE_FACT)) {//实况
            view = inflater.inflate(R.layout.shawn_fact_marker_info, null);
            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvInfo = view.findViewById(R.id.tvInfo);

            String[] titles = marker.getTitle().split("\\|");
            String[] title0 = titles[0].split(",");
            if (!TextUtils.isEmpty(title0[0])) {
                tvName.setText(title0[0]);
            }
            if (!TextUtils.isEmpty(title0[1])) {
                tvInfo.setText(title0[1]);
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
        if (TextUtils.equals(marker.getSnippet(), TYPE_FORE)) {//天气预报
            String[] titles = marker.getTitle().split(",");
            Intent intent = new Intent(mContext, ShawnForecastActivity.class);
            intent.putExtra("cityName", titles[1]);
            intent.putExtra("cityId", titles[0]);
            startActivity(intent);
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

    /**
     * 获取实况图层数据
     */
    private void OkHttpFactChart() {
        loadingView.setVisibility(View.VISIBLE);
        final String url = "https://app.tianqi.cn/tile_map/getcimisslayer/440000";
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
                                        llFactContainerRain.removeAllViews();
                                        llFactContainerTemp.removeAllViews();
                                        llFactContainerWind.removeAllViews();
                                        JSONArray array = new JSONArray(result);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject itemObj = array.getJSONObject(i);
                                            String imgUrl = itemObj.getString("imgurl");
                                            String columnName = "";

                                            if (!imgUrl.contains("gd_12js.png")) {//暂时过滤掉12小时降水
                                                if (imgUrl.contains("gd_1js.png")) {
                                                    columnName = "1h降水";
                                                }else if (imgUrl.contains("gd_3js.png")) {
                                                    columnName = "3h降水";
                                                }else if (imgUrl.contains("gd_6js.png")) {
                                                    columnName = "6h降水";
                                                }else if (imgUrl.contains("gd_12js.png")) {
                                                    columnName = "12h降水";
                                                }else if (imgUrl.contains("gd_24js.png")) {
                                                    columnName = "24h降水";
                                                }else if (imgUrl.contains("gd_temp.png")) {
                                                    columnName = "1h温度";
                                                }else if (imgUrl.contains("gd_jd_wind_1h.png")) {
                                                    columnName = "1h极大风";
                                                }else if (imgUrl.contains("gd_jd_wind_24h.png")) {
                                                    columnName = "24h极大风";
                                                }else if (imgUrl.contains("gd_zd_wind_1h.png")) {
                                                    columnName = "1h最大风";
                                                }else if (imgUrl.contains("gd_zd_wind_24h.png")) {
                                                    columnName = "24h最大风";
                                                }

                                                TextView tvName = new TextView(mContext);
                                                tvName.setGravity(Gravity.CENTER);
                                                tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                tvName.setPadding((int)CommonUtil.dip2px(mContext, 5), (int)CommonUtil.dip2px(mContext, 5),
                                                        (int)CommonUtil.dip2px(mContext, 5), (int)CommonUtil.dip2px(mContext, 5));
                                                tvName.setText(columnName);
                                                tvName.setTag(imgUrl);

                                                if (imgUrl.contains("gd_1js.png")) {
                                                    tvName.setTextColor(Color.WHITE);
                                                    tvName.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                    currentFactChartImgUrl = imgUrl;
                                                    OkHttpFactImg(imgUrl);
                                                }else {
                                                    tvName.setTextColor(getResources().getColor(R.color.text_color3));
                                                    tvName.setBackgroundColor(Color.TRANSPARENT);
                                                }

                                                if (imgUrl.contains("js.png")) {
                                                    llFactContainerRain.addView(tvName);
                                                }else if (imgUrl.contains("gd_temp.png")) {
                                                    llFactContainerTemp.addView(tvName);
                                                }else {
                                                    llFactContainerWind.addView(tvName);
                                                }

                                                tvName.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        for (int j = 0; j < llFactContainerRain.getChildCount(); j++) {
                                                            TextView tvName = (TextView) llFactContainerRain.getChildAt(j);
                                                            String imgUrl = v.getTag()+"";
                                                            if (TextUtils.equals(tvName.getTag()+"", imgUrl)) {
                                                                if (TextUtils.equals(currentFactChartImgUrl, imgUrl)) {
                                                                    return;
                                                                }
                                                                tvName.setTextColor(Color.WHITE);
                                                                tvName.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                                //加载图片数据
                                                                currentFactChartImgUrl = imgUrl;
                                                                OkHttpFactImg(imgUrl);
                                                            }else {
                                                                tvName.setTextColor(getResources().getColor(R.color.text_color3));
                                                                tvName.setBackgroundColor(Color.TRANSPARENT);
                                                            }
                                                        }

                                                        for (int j = 0; j < llFactContainerTemp.getChildCount(); j++) {
                                                            TextView tvName = (TextView) llFactContainerTemp.getChildAt(j);
                                                            String imgUrl = v.getTag()+"";
                                                            if (TextUtils.equals(tvName.getTag()+"", imgUrl)) {
                                                                if (TextUtils.equals(currentFactChartImgUrl, imgUrl)) {
                                                                    return;
                                                                }
                                                                tvName.setTextColor(Color.WHITE);
                                                                tvName.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                                //加载图片数据
                                                                currentFactChartImgUrl = imgUrl;
                                                                OkHttpFactImg(imgUrl);
                                                            }else {
                                                                tvName.setTextColor(getResources().getColor(R.color.text_color3));
                                                                tvName.setBackgroundColor(Color.TRANSPARENT);
                                                            }
                                                        }

                                                        for (int j = 0; j < llFactContainerWind.getChildCount(); j++) {
                                                            TextView tvName = (TextView) llFactContainerWind.getChildAt(j);
                                                            String imgUrl = v.getTag()+"";
                                                            if (TextUtils.equals(tvName.getTag()+"", imgUrl)) {
                                                                if (TextUtils.equals(currentFactChartImgUrl, imgUrl)) {
                                                                    return;
                                                                }
                                                                tvName.setTextColor(Color.WHITE);
                                                                tvName.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                                //加载图片数据
                                                                currentFactChartImgUrl = imgUrl;
                                                                OkHttpFactImg(imgUrl);
                                                            }else {
                                                                tvName.setTextColor(getResources().getColor(R.color.text_color3));
                                                                tvName.setBackgroundColor(Color.TRANSPARENT);
                                                            }
                                                        }

                                                    }
                                                });
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
     * 绘制实况图
     */
    private void drawFactBitmap(Bitmap bitmap, boolean isAutoBound) {
        if (bitmap == null) {
            return;
        }
        BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(20.211718, 109.654936))
                .include(new LatLng(25.516771, 117.318621))
                .build();

        if (isAutoBound) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }

        if (factOverlay == null) {
            factOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                    .anchor(0.5f, 0.5f)
                    .positionFromBounds(bounds)
                    .image(fromView)
                    .transparency(0.2f));
        } else {
            factOverlay.setImage(null);
            factOverlay.setPositionFromBounds(bounds);
            factOverlay.setImage(fromView);
        }
    }

    /**
     * 清除实况图
     */
    private void removeFact() {
        if (factOverlay != null) {
            factOverlay.remove();
            factOverlay = null;
        }
    }

    /**
     * 绘制实况图层img
     * @param imgUrl
     */
    private void OkHttpFactImg(final String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        loadingView.setVisibility(View.VISIBLE);
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
                                if (bitmap != null) {
                                    factBitmap = bitmap;
                                    drawFactBitmap(factBitmap, true);
                                }
                            }
                        });
                    }
                });
            }
        }).start();

        //判断当前选择的要素是否请求过数据，如果请求过直接通过marker集合绘制
        removeFactMarkers();
        if (imgUrl.contains("gd_1js.png")) {//1小时降水
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_rain1);
            if (factRains1.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factRains1.clear();
            }
        }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_rain3);
            if (factRains3.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factRains3.clear();
            }
        }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_rain6);
            if (factRains6.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factRains6.clear();
            }
        }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_rain12);
            if (factRains12.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factRains12.clear();
            }
        }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_rain24);
            if (factRains24.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factRains24.clear();
            }
        }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_temp);
            if (factTemps1.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factTemps1.clear();
            }
        }else if (imgUrl.contains("gd_jd_wind_1h.png")) {//1小时极大风
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_wind);
            if (factWindsJd1.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factWindsJd1.clear();
            }
        }else if (imgUrl.contains("gd_jd_wind_24h.png")) {//24小时极大风
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_wind);
            if (factWindsJd24.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factWindsJd24.clear();
            }
        }else if (imgUrl.contains("gd_zd_wind_1h.png")) {//1小时最大风
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_wind);
            if (factWindsZd1.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factWindsZd1.clear();
            }
        }else if (imgUrl.contains("gd_zd_wind_24h.png")) {//24小时最大风
            ivFactLegend.setImageResource(R.drawable.shawn_fact_legend_wind);
            if (factWindsZd24.size() > 0) {
                drawFactMarkers(imgUrl);
                loadingView.setVisibility(View.GONE);
                return;
            }else {
                factWindsZd24.clear();
            }
        }

        String url = "";
        if (imgUrl.contains("gd_1js.png")) {//1小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsjs?type=%s", "js");
        }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zswd");
        }else if (imgUrl.contains("gd_jd_wind_1h.png")) {//1小时极大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&mold=jd");
        }else if (imgUrl.contains("gd_jd_wind_24h.png")) {//24小时极大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs24&mold=jd");
        }else if (imgUrl.contains("gd_zd_wind_1h.png")) {//1小时最大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs&mold=zd");
        }else if (imgUrl.contains("gd_zd_wind_24h.png")) {//24小时最大风
            url = String.format("http://national-observe-data.tianqi.cn/zstyphoon/lhdata/zsfs?type=fs24&mold=zd");
        }

        OkHttpFactList(imgUrl, url);

    }

    /**
     * 获取天气实况信息
     */
    private void OkHttpFactList(final String imgUrl, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
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
                                                    JSONArray array = obj.getJSONArray("data");
                                                    for (int i = 0; i < array.length(); i++) {
                                                        FactDto dto = new FactDto();
                                                        JSONObject itemObj = array.getJSONObject(i);

                                                        if (i == 0) {
                                                            String endTime = itemObj.getString("Datetime");
                                                            try {
                                                                String startTime1 = sdf10.format(sdf10.parse(endTime).getTime()-1000*60*60);
                                                                String startTime3 = sdf10.format(sdf10.parse(endTime).getTime()-1000*60*60*3);
                                                                String startTime6 = sdf10.format(sdf10.parse(endTime).getTime()-1000*60*60*6);
                                                                String startTime12 = sdf10.format(sdf10.parse(endTime).getTime()-1000*60*60*12);
                                                                String startTime24 = sdf10.format(sdf10.parse(endTime).getTime()-1000*60*60*24);
                                                                String factTime = "";
                                                                if (imgUrl.contains("gd_1js.png")) {//1小时降水
                                                                    factTime = "广东省1小时降水实况["+sdf1.format(sdf10.parse(startTime1))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
                                                                    factTime = "广东省3小时降水实况["+sdf1.format(sdf10.parse(startTime3))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
                                                                    factTime = "广东省6小时降水实况["+sdf1.format(sdf10.parse(startTime6))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
                                                                    factTime = "广东省12小时降水实况["+sdf1.format(sdf10.parse(startTime12))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
                                                                    factTime = "广东省24小时降水实况["+sdf1.format(sdf10.parse(startTime24))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
                                                                    factTime = "广东省1小时温度实况["+sdf1.format(sdf10.parse(startTime1))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_jd_wind_1h.png")) {//1小时极大风
                                                                    factTime = "广东省1小时极大风速实况["+sdf1.format(sdf10.parse(startTime1))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_jd_wind_24h.png")) {//24小时极大风
                                                                    factTime = "广东省24小时极大风速实况["+sdf1.format(sdf10.parse(startTime24))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_zd_wind_1h.png")) {//1小时最大风
                                                                    factTime = "广东省1小时最大风速实况["+sdf1.format(sdf10.parse(startTime1))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }else if (imgUrl.contains("gd_zd_wind_24h.png")) {//24小时最大风
                                                                    factTime = "广东省24小时最大风速实况["+sdf1.format(sdf10.parse(startTime24))+" - "+sdf1.format(sdf10.parse(endTime))+"]";
                                                                }
                                                                tvFactTime.setText(factTime);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        if (!itemObj.isNull("Lat")) {
                                                            dto.lat = itemObj.getDouble("Lat");
                                                        }
                                                        if (!itemObj.isNull("Lon")) {
                                                            dto.lng = itemObj.getDouble("Lon");
                                                        }

                                                        if (imgUrl.contains("gd_1js.png")) {//1小时降水
                                                            if (!itemObj.isNull("JS")) {
                                                                dto.rain = itemObj.getDouble("JS");
                                                                if (dto.rain >= 9999) {
                                                                    dto.rain = 0;
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
                                                            if (!itemObj.isNull("JS3")) {
                                                                dto.rain = itemObj.getDouble("JS3");
                                                                if (dto.rain >= 9999) {
                                                                    dto.rain = 0;
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
                                                            if (!itemObj.isNull("JS6")) {
                                                                dto.rain = itemObj.getDouble("JS6");
                                                                if (dto.rain >= 9999) {
                                                                    dto.rain = 0;
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
                                                            if (!itemObj.isNull("JS12")) {
                                                                dto.rain = itemObj.getDouble("JS12");
                                                                if (dto.rain >= 9999) {
                                                                    dto.rain = 0;
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
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

                                                        //站点分级
                                                        if (dto.stationId.startsWith("59")) {//国家站
                                                            dto.level = "1";
                                                        }else {
                                                            int[] nums = {2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3};//9:18
                                                            Random random = new Random();
                                                            int level = random.nextInt(nums.length);
                                                            dto.level = nums[level]+"";
                                                        }

                                                        if (imgUrl.contains("gd_1js.png")) {//1小时降水
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factRains1.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factRains1.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factRains3.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factRains3.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factRains6.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factRains6.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factRains12.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factRains12.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factRains24.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factRains24.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factTemps1.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factTemps1.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_jd_wind_1h.png")) {//1小时极大风
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factWindsJd1.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factWindsJd1.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_jd_wind_24h.png")) {//24小时极大风
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factWindsJd24.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factWindsJd24.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_zd_wind_1h.png")) {//1小时最大风
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factWindsZd1.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factWindsZd1.add(dto);
                                                                }
                                                            }
                                                        }else if (imgUrl.contains("gd_zd_wind_24h.png")) {//24小时最大风
                                                            if (TextUtils.equals(userAuthority, "3")) {//专业用户权限
                                                                factWindsZd24.add(dto);
                                                            }else {//普通用户权限，只能查看国家站数据
                                                                if (dto.stationId.startsWith("59")) {
                                                                    factWindsZd24.add(dto);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    drawFactMarkers(imgUrl);
                                                    loadingView.setVisibility(View.GONE);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {
                                            OkHttpFactList(imgUrl, url);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 绘制实况点marker
     */
    private void drawFactMarkers(final String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                removeFactMarkers();
                try {
                    List<FactDto> list = new ArrayList<>();
                    if (imgUrl.contains("gd_1js.png")) {//1小时降水
                        list.addAll(factRains1);
                    }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
                        list.addAll(factRains3);
                    }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
                        list.addAll(factRains6);
                    }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
                        list.addAll(factRains12);
                    }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
                        list.addAll(factRains24);
                    }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
                        list.addAll(factTemps1);
                    }else if (imgUrl.contains("gd_jd_wind_1h.png")) {//1小时极大风
                        list.addAll(factWindsJd1);
                    }else if (imgUrl.contains("gd_jd_wind_24h.png")) {//24小时极大风
                        list.addAll(factWindsJd24);
                    }else if (imgUrl.contains("gd_zd_wind_1h.png")) {//1小时最大风
                        list.addAll(factWindsZd1);
                    }else if (imgUrl.contains("gd_zd_wind_24h.png")) {//24小时最大风
                        list.addAll(factWindsZd24);
                    }

                    if (zoom <= 9.0f) {
                        for (FactDto dto : list) {
                            if (TextUtils.equals(dto.level, "1")) {//添加对应1级别站点
                                addSingleFactMarker(dto, imgUrl);
                            }
                        }
                    }else if (zoom <= 11.0f ) {
                        for (FactDto dto : list) {
                            if (TextUtils.equals(dto.level, "1") || TextUtils.equals(dto.level, "2")) {//添加对应1、2级别站点
                                addSingleFactMarker(dto, imgUrl);
                            }
                        }
                    }else {
                        for (FactDto dto : list) {//添加对应1、2、3级别站点
                            addSingleFactMarker(dto, imgUrl);
                        }
                    }

                }catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addSingleFactMarker(FactDto dto, String imgUrl) {
        if (dto.lat > leftlatlng.latitude && dto.lat < rightLatlng.latitude && dto.lng > leftlatlng.longitude && dto.lng < rightLatlng.longitude) {
//            if (imgUrl.contains("js.png") && dto.rain <= 0) {//过滤掉降水为0、风速为0的点
//                return;
//            }
//            if (imgUrl.contains("gd_jd_wind") && dto.windS <= 0) {//过滤掉降水为0、风速为0的点
//                return;
//            }
            MarkerOptions options = new MarkerOptions();
            String value;
            if (imgUrl.contains("js.png")) {//降水
                value = dto.rain+"mm";
            }else if (imgUrl.contains("gd_temp.png")) {//温度
                value = dto.temp+"℃";
            }else {//风速
                value = CommonUtil.getWindDirection(dto.windD)+"风 "+dto.windS+"m/s";
            }
            options.title(dto.pro+dto.city+dto.dis+"\n"+dto.stationName+","+value+"|"+dto.level);
            options.snippet(TYPE_FACT);
            options.anchor(0.5f, 0.5f);
            options.position(new LatLng(dto.lat, dto.lng));
            options.icon(BitmapDescriptorFactory.fromView(drawFactMarkerBitmap(dto, imgUrl)));
            Marker marker = aMap.addMarker(options);
            marker.setVisible(isShowFactPoint);
            factMarkers.add(marker);
        }
    }

    private View drawFactMarkerBitmap(FactDto dto, String imgUrl) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_fact_marker_icon, null);
        if (view == null) {
            return null;
        }
        ImageView ivMarker = view.findViewById(R.id.ivMarker);
        TextView tvValue = view.findViewById(R.id.tvValue);
        ImageView ivWind = view.findViewById(R.id.ivWind);

        GradientDrawable gradientDrawable = (GradientDrawable) ivMarker.getBackground();
        if (!TextUtils.isEmpty(imgUrl)) {
            if (imgUrl.contains("gd_1js.png")) {//1小时降水
                gradientDrawable.setColor(CommonUtil.factRain1Color(dto.rain));
                tvValue.setText(dto.rain+"");
            }else if (imgUrl.contains("gd_3js.png")) {//3小时降水
                gradientDrawable.setColor(CommonUtil.factRain3Color(dto.rain));
                tvValue.setText(dto.rain+"");
            }else if (imgUrl.contains("gd_6js.png")) {//6小时降水
                gradientDrawable.setColor(CommonUtil.factRain6Color(dto.rain));
                tvValue.setText(dto.rain+"");
            }else if (imgUrl.contains("gd_12js.png")) {//12小时降水
                gradientDrawable.setColor(CommonUtil.factRain12Color(dto.rain));
                tvValue.setText(dto.rain+"");
            }else if (imgUrl.contains("gd_24js.png")) {//24小时降水
                gradientDrawable.setColor(CommonUtil.factRain24Color(dto.rain));
                tvValue.setText(dto.rain+"");
            }else if (imgUrl.contains("gd_temp.png")) {//1小时温度
                gradientDrawable.setColor(CommonUtil.factTemp1Color(dto.temp));
                tvValue.setText(dto.temp+"");
            }else {//风速
                gradientDrawable.setColor(CommonUtil.factWind1Color(dto.windS));
                tvValue.setText(dto.windS+"");

                Bitmap b = CommonUtil.getWindMarker(mContext, dto.windS);
                if (b != null) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(1, 1);
                    matrix.postRotate(dto.windD);
                    Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    if (bitmap != null) {
                        ivWind.setImageBitmap(bitmap);
                        ivWind.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        return view;
    }

    /**
     * 切换实况点显示、隐藏
     */
    private void switchFactMarkers(boolean isShow) {
        for (Marker marker : factMarkers) {
            if (marker != null) {
                marker.setVisible(isShow);
            }
        }
    }

    private void removeFactMarkers() {
        try {
            for (Marker marker : factMarkers) {
                if (marker != null) {
                    marker.remove();
                }
            }
            factMarkers.clear();
        }catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler factHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1002:
                    drawFactBitmap(factBitmap, false);
                    drawFactMarkers(currentFactChartImgUrl);
                    break;
            }
        }
    };

    //卫星拼图
    /**
     * 获取云图数据
     */
    private void OkHttpCloud() {
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
                                        double p1 = 0,p2 = 0,p3 = 0,p4 = 0;
                                        if (!obj.isNull("rect")) {
                                            JSONArray rect = obj.getJSONArray("rect");
                                            p1 = rect.getDouble(2);
                                            p2 = rect.getDouble(1);
                                            p3 = rect.getDouble(0);
                                            p4 = rect.getDouble(3);
                                        }
                                        if (!obj.isNull("l")) {
                                            cloudList.clear();
                                            JSONArray array = obj.getJSONArray("l");
                                            for (int i = array.length()-1; i >= 0; i--) {
                                                RadarDto dto = new RadarDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                dto.imgUrl = itemObj.getString("l2");
                                                try {
                                                    String time = itemObj.getString("l1");
                                                    dto.time = sdf9.format(sdf10.parse(time));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                dto.p1 = p1;
                                                dto.p2 = p2;
                                                dto.p3 = p3;
                                                dto.p4 = p4;
                                                cloudList.add(dto);
                                            }
                                            if (cloudList.size() > 0) {
                                                startDownloadCloudImgs(cloudList);
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
     * 下载图片
     * @param list
     */
    private void startDownloadCloudImgs(List<RadarDto> list) {
        cloudManager.loadImagesAsyn(list, new CloudManager.CloudListener() {
            @Override
            public void onResult(int result, final List<RadarDto> images) {
                if (result == CloudManager.CloudListener.RESULT_SUCCESSED) {
                    if (cloudThread == null) {
                        cloudThread = new CloudThread(images);
                    }

                    //把最新的一张降雨图片覆盖在地图上
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadarDto dto = images.get(images.size()-1);
                            if (!TextUtils.isEmpty(dto.path)) {
                                Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                                if (bitmap != null) {
                                    drawCloud(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                                }
                            }
                            changeCloudSeekbarProgress(dto.time, images.size(), images.size());
                            loadingView.setVisibility(View.GONE);
                        }
                    });

                }
            }

            @Override
            public void onProgress(String url, int progress) {
            }
        });
    }

    private void changeCloudSeekbarProgress(String time, int progress, int max) {
        if (cloudSeekbar != null) {
            cloudSeekbar.setMax(max);
            cloudSeekbar.setProgress(progress);
        }
        if (!TextUtils.isEmpty(time)) {
            tvCloudTime.setText(time);
        }
    }

    /**
     * 绘制雷达图层
     * @param bitmap
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     */
    private void drawCloud(Bitmap bitmap, double p1, double p2, double p3, double p4) {
        BitmapDescriptor fromView = BitmapDescriptorFactory.fromBitmap(bitmap);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(p3, p2))
                .include(new LatLng(p1, p4))
                .build();

        if (cloudOverlay == null) {
            cloudOverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                    .anchor(0.5f, 0.5f)
                    .positionFromBounds(bounds)
                    .image(fromView)
                    .zIndex(1000)
                    .transparency(0.25f));
        } else {
            cloudOverlay.setImage(null);
            cloudOverlay.setPositionFromBounds(bounds);
            cloudOverlay.setImage(fromView);
        }
        aMap.runOnDrawFrame();
    }

    private class CloudThread extends Thread {

        static final int STATE_NONE = 0;
        static final int STATE_PLAYING = 1;
        static final int STATE_PAUSE = 2;
        static final int STATE_CANCEL = 3;
        private List<RadarDto> images;
        private int state;
        private int index;
        private int count;
        private boolean isTracking;

        private CloudThread(List<RadarDto> images) {
            this.images = images;
            this.count = images.size();
            this.index = 0;
            this.state = STATE_NONE;
            this.isTracking = false;
        }

        private int getCurrentState() {
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
                sendCloud();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendCloud() {
            if (index >= count || index < 0) {
                index = 0;
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RadarDto dto = images.get(index);
                        if (!TextUtils.isEmpty(dto.path)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                            if (bitmap != null) {
                                drawCloud(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                            }
                        }
                        changeCloudSeekbarProgress(dto.time, index++, count-1);
                    }
                });
            }
        }

        private void cancel() {
            this.state = STATE_CANCEL;
        }
        private void pause() {
            this.state = STATE_PAUSE;
        }
        private void play() {
            this.state = STATE_PLAYING;
        }

        public void setCurrent(int index) {
            this.index = index;
            sendCloud();
        }

        public void startTracking() {
            isTracking = true;
        }

        public void stopTracking() {
            isTracking = false;
            if (this.state == STATE_PAUSE) {
                sendCloud();
            }
        }
    }

    /**
     * 清除云图
     */
    private void removeCloudOverlay() {
        if (cloudOverlay != null) {
            cloudOverlay.remove();
            cloudOverlay = null;
        }
    }

    private void removeCloudThread() {
        if (cloudThread != null) {
            cloudThread.cancel();
            cloudThread = null;
        }
    }


    //雷达拼图
    /**
     * 获取彩云数据
     */
    private void OkHttpRadar() {
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
                                            radarList.clear();
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONArray array0 = array.getJSONArray(i);
                                                RadarDto dto = new RadarDto();
                                                dto.imgUrl = array0.optString(0);
                                                long time = array0.optLong(1)*1000;
                                                dto.time = sdf9.format(new Date(time));

                                                JSONArray itemArray = array0.getJSONArray(2);
                                                dto.p1 = itemArray.optDouble(0);
                                                dto.p2 = itemArray.optDouble(1);
                                                dto.p3 = itemArray.optDouble(2);
                                                dto.p4 = itemArray.optDouble(3);
                                                radarList.add(dto);
                                            }
                                            if (radarList.size() > 0) {
                                                startDownloadRadarImgs(radarList);
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
    private void startDownloadRadarImgs(List<RadarDto> list) {
        radarManager.loadImagesAsyn(list, new RadarManager.RadarListener() {
            @Override
            public void onResult(int result, final List<RadarDto> images) {
                if (result == RadarManager.RadarListener.RESULT_SUCCESSED) {
                    if (radarThread == null) {
                        radarThread = new RadarThread(images);
                    }

                    //把最新的一张降雨图片覆盖在地图上
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadarDto dto = images.get(images.size()-1);
                            if (!TextUtils.isEmpty(dto.path)) {
                                Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                                if (bitmap != null) {
                                    drawRadar(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                                }
                            }
                            changeRadarSeekbarProgress(dto.time, images.size(), images.size());
                            loadingView.setVisibility(View.GONE);
                        }
                    });

                }
            }

            @Override
            public void onProgress(String url, int progress) {
            }
        });
    }

    private void changeRadarSeekbarProgress(String time, int progress, int max) {
        if (radarSeekbar != null) {
            radarSeekbar.setMax(max);
            radarSeekbar.setProgress(progress);
        }
        if (!TextUtils.isEmpty(time)) {
            tvRadarTime.setText(time);
        }
    }

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
                    .zIndex(1001)
                    .transparency(0.25f));
        } else {
            radarOverlay.setImage(null);
            radarOverlay.setPositionFromBounds(bounds);
            radarOverlay.setImage(fromView);
        }
        aMap.runOnDrawFrame();
    }

    private class RadarThread extends Thread {

        static final int STATE_NONE = 0;
        static final int STATE_PLAYING = 1;
        static final int STATE_PAUSE = 2;
        static final int STATE_CANCEL = 3;
        private List<RadarDto> images;
        private int state;
        private int index;
        private int count;
        private boolean isTracking;

        private RadarThread(List<RadarDto> images) {
            this.images = images;
            this.count = images.size();
            this.index = 0;
            this.state = STATE_NONE;
            this.isTracking = false;
        }

        private int getCurrentState() {
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
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RadarDto dto = images.get(index);
                        if (!TextUtils.isEmpty(dto.path)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                            if (bitmap != null) {
                                drawRadar(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                            }
                        }
                        changeRadarSeekbarProgress(dto.time, index++, count-1);
                    }
                });
            }
        }

        private void cancel() {
            this.state = STATE_CANCEL;
        }
        private void pause() {
            this.state = STATE_PAUSE;
        }
        private void play() {
            this.state = STATE_PLAYING;
        }

        public void setCurrent(int index) {
            this.index = index;
            sendRadar();
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
    private void removeRadarOverlay() {
        if (radarOverlay != null) {
            radarOverlay.remove();
            radarOverlay = null;
        }
    }

    private void removeRadarThread() {
        if (radarThread != null) {
            radarThread.cancel();
            radarThread = null;
        }
    }


    //天气预警
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
                                loadingView.setVisibility(View.GONE);
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject object = new JSONObject(result);
                                        if (!object.isNull("data")) {
                                            warningList.clear();
                                            JSONArray jsonArray = object.getJSONArray("data");
                                            for (int i = 0; i < jsonArray.length(); i++) {
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

                                            Collections.sort(warningList, new Comparator<WarningDto>() {//按照预警等级排序，保证同一个区域绘制在最上层的是最高等级预警
                                                @Override
                                                public int compare(WarningDto arg0, WarningDto arg1) {
                                                    return Double.valueOf(arg0.time).compareTo(Double.valueOf(arg1.time));
                                                }
                                            });

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
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (WarningDto dto : warningList) {
                        if (!warningMarkers.containsKey(dto.html)) {
                            double lat = Double.valueOf(dto.lat);
                            double lng = Double.valueOf(dto.lng);
                            MarkerOptions optionsTemp = new MarkerOptions();
                            optionsTemp.title(dto.lat+","+dto.lng+","+dto.html);
                            optionsTemp.snippet(TYPE_WARNING);
                            optionsTemp.anchor(0.5f, 0.5f);
                            LatLng latLng = new LatLng(lat, lng);
                            optionsTemp.position(latLng);
                            builder.include(latLng);
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
                            if (dto.item0.endsWith("00")) {//默认显示省级、市级预警
                                marker.setVisible(true);
                            }else {
                                marker.setVisible(false);
                            }
                            warningMarkers.put(dto.html, marker);
                        }

                    }
                    if (warningList.size() > 0) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }

//                    List<WarningDto> list = new ArrayList<>(warningList);
//                    Collections.sort(list, new Comparator<WarningDto>() {//按照预警等级排序，保证同一个区域绘制在最上层的是最高等级预警
//                        @Override
//                        public int compare(WarningDto arg0, WarningDto arg1) {
//                            return Integer.valueOf(arg0.color).compareTo(Integer.valueOf(arg1.color));
//                        }
//                    });
                    for (WarningDto dto : warningList) {
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
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    foreDataList1.clear();
                                    foreDataList2.clear();
                                    foreDataList3.clear();
                                    parseStationInfo(result, level1);
                                    parseStationInfo(result, level2);
                                    parseStationInfo(result, level3);
                                    zoom = 7.5f;
                                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.517646,113.392782), zoom));
                                    loadingView.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 解析数据
     */
    private void parseStationInfo(String result, String level) {
        try {
            JSONObject obj = new JSONObject(result);
            if (!obj.isNull(level)) {
                JSONArray array = new JSONArray(obj.getString(level));
                for (int i = 0; i < array.length(); i++) {
                    WeatherDto dto = new WeatherDto();
                    JSONObject itemObj = array.getJSONObject(i);
                    dto.cityId = itemObj.getString("areaid");
                    dto.cityName = itemObj.getString("name");
                    dto.lat = itemObj.getDouble("lat");
                    dto.lng = itemObj.getDouble("lon");
                    dto.level = itemObj.getString("level");

                    if (TextUtils.equals(level, level1)) {
                        foreDataList1.add(dto);
                    }else if (TextUtils.equals(level, level2)) {
                        foreDataList2.add(dto);
                    }else if (TextUtils.equals(level, level3)) {//把四个直辖市的区划入地市级
                        if (dto.cityId.contains("10101") || dto.cityId.contains("10102") || dto.cityId.contains("10103") || dto.cityId.contains("10104")) {
                            dto.level = "2";
                            foreDataList2.add(dto);
                        }else {
                            foreDataList3.add(dto);
                        }
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加预报marker
     */
    private void addForeMarkers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<WeatherDto> list = new ArrayList<>();
                if (zoom <= zoom1) {
                    list.addAll(foreDataList1);
                }else if (zoom > zoom1 && zoom <= zoom2){
                    list.addAll(foreDataList1);
                    list.addAll(foreDataList2);
                }else {
                    list.addAll(foreDataList1);
                    list.addAll(foreDataList2);
                    list.addAll(foreDataList3);
                }

                for (WeatherDto dto : list) {
                    addVisibleAreaMarker(dto);
                }
            }
        }).start();
    }

    private void removeForeMarkers() {
        for (Marker marker : foreMarkers) {
            marker.remove();
        }
        foreMarkers.clear();
    }

    /**
     * 添加可视区域对应的marker
     * @param dto
     */
    private void addVisibleAreaMarker(WeatherDto dto) {
        if (dto.lat > leftlatlng.latitude && dto.lat < rightLatlng.latitude && dto.lng > leftlatlng.longitude && dto.lng < rightLatlng.longitude) {
            getWeatherInfo(dto);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler foreHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    removeForeMarkers();
                    addForeMarkers();
                    break;
            }
        }
    };

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
                        if (content != null) {
                            try {
                                JSONObject obj = new JSONObject(content.toString());

                                //15天预报
                                if (!obj.isNull("f")) {
                                    JSONObject f = obj.getJSONObject("f");
                                    String f0 = f.getString("f0");
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

                                        MarkerOptions options = new MarkerOptions();
                                        options.title(dto.cityId+","+dto.cityName+","+dto.highPheCode+","+dto.highTemp+","+dto.highWindDir+","+dto.highWindForce
                                                +","+dto.lowPheCode+","+dto.lowTemp+","+dto.lowWindDir+","+dto.lowWindForce+","+dto.level+","+f0);
                                        options.snippet(TYPE_FORE);
                                        options.anchor(0.5f, 1.0f);
                                        options.position(new LatLng(dto.lat, dto.lng));
                                        int currentHour = Integer.parseInt(sdf7.format(new Date()));
                                        if (currentHour >= 5 && currentHour <= 18) {
                                            options.icon(BitmapDescriptorFactory.fromView(foreBitmapView(dto.highPheCode, true)));
                                        }else {
                                            options.icon(BitmapDescriptorFactory.fromView(foreBitmapView(dto.lowPheCode, false)));
                                        }
                                        Marker marker = aMap.addMarker(options);
                                        marker.setZIndex(100);
                                        markerExpandAnimation(marker);
                                        foreMarkers.add(marker);
                                    }
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
     * 添加天气预报marker
     * @return
     */
    private View foreBitmapView(int weatherCode, boolean isDay) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_fore_marker_icon, null);
        if (view == null) {
            return null;
        }
        ImageView ivMarker = view.findViewById(R.id.ivMarker);
        if (isDay) {
            ivMarker.setImageBitmap(WeatherUtil.getDayBitmap(mContext, weatherCode));
        }else {
            ivMarker.setImageBitmap(WeatherUtil.getNightBitmap(mContext, weatherCode));
        }
        return view;
    }

    private void markerExpandAnimation(Marker marker) {
        ScaleAnimation animation = new ScaleAnimation(0,1,0,1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(300);
        marker.setAnimation(animation);
        marker.startAnimation();
    }

    private void markerColloseAnimation(Marker marker) {
        ScaleAnimation animation = new ScaleAnimation(1,0,1,0);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(300);
        marker.setAnimation(animation);
        marker.startAnimation();
    }


    //分钟降水
    /**
     * 通过经纬度获取地理位置信息
     * @param lat
     * @param lng
     */
    private void searchAddrByLatLng(double lat, double lng, final String mapClick) {
        //latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
        loadingView.setVisibility(View.VISIBLE);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(lat, lng), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int i) {
                if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                    loadingView.setVisibility(View.GONE);

                    clickAdcode = result.getRegeocodeAddress().getAdCode();
                    clickLatLng = new LatLng(result.getRegeocodeQuery().getPoint().getLatitude(), result.getRegeocodeQuery().getPoint().getLongitude());
                    saveSharedPreferences();

                    if (TextUtils.equals(mapClick, MAPCLICK_TYPHOON) && isShowTyphoon) {
                        typhoonPointAddr = result.getRegeocodeAddress().getProvince()+result.getRegeocodeAddress().getCity();
                        if (clickMarker != null && TextUtils.equals(clickMarker.getSnippet(), TYPE_TYPHOON)) {//为了显示"参考位置"
                            clickMarker.showInfoWindow();
                        }
                    }

                    if (TextUtils.equals(mapClick, MAPCLICK_MINUTE) && isShowMinute) {
                        String addr = result.getRegeocodeAddress().getFormatAddress();
                        if (!TextUtils.isEmpty(addr)) {
                            tvAddr.setText(addr);
                        }
                    }

                    if (TextUtils.equals(mapClick, MAPCLICK_WARNING) && isShowWarning) {
                        addLocationWarnings();
                    }

                }
            }
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });
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
        Point leftPoint = new Point(0, height);
        Point rightPoint = new Point(width, 0);
        leftlatlng = aMap.getProjection().fromScreenLocation(leftPoint);
        rightLatlng = aMap.getProjection().fromScreenLocation(rightPoint);

        zoom = arg0.zoom;

        if (isShowFact) {//选中实况状态下
            factHandler.removeMessages(1002);
            Message msg = foreHandler.obtainMessage();
            msg.what = 1002;
            factHandler.sendMessageDelayed(msg, 1000);
        }

        if (isShowWarning) {//选中预警状态下
            //判断显示地图上预警类型对应marker
            for (String html : warningMarkers.keySet()) {
                if (warningMarkers.containsKey(html)) {
                    Marker marker = warningMarkers.get(html);
                    String item0 = html.split("-")[0];
                    if (zoom <= zoom1) {
                        if (item0.endsWith("00")) {
                            marker.setVisible(true);
                        }else {
                            marker.setVisible(false);
                        }
                    }else {
                        if (!item0.endsWith("00")) {
                            marker.setVisible(true);
                        }
                    }
                }
            }
        }

        if (isShowFore) {//选中预报状态下
            foreHandler.removeMessages(1001);
            Message msg = foreHandler.obtainMessage();
            msg.what = 1001;
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
            strokeColor = 0x9000FF00;
//            strokeColor = getResources().getColor(R.color.refresh_color2);
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
                    for (TyphoonDto pub : publishList) {
                        if (pub.isSelected) {
                            if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                                OkHttpTyphoonDetailBABJ(pub.publishName, pub.publishCode, dto.tId, dto.code, name);
                            }else {
                                OkHttpTyphoonDetailIdea(pub.publishName, pub.publishCode, dto.id, dto.code, name);
                            }
                        }
                    }
                }
                if (startList.size() <= 0) {// 没有生效台风
                    tvTyphoonName.setText(getString(R.string.no_typhoon));
                }
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
            tvTyphoonYear.setVisibility(View.GONE);


            typhoonNameMap.clear();
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
                for (TyphoonDto pub : publishList) {
                    if (pub.isSelected) {
                        if (TextUtils.equals(pub.publishCode, "BABJ") && !TextUtils.isEmpty(dto.tId)) {
                            OkHttpTyphoonDetailBABJ(pub.publishName, pub.publishCode, dto.tId, dto.code, name);
                        }else {
                            OkHttpTyphoonDetailIdea(pub.publishName, pub.publishCode, dto.id, dto.code, name);
                        }
                    }
                }
            }
            if (startList.size() <= 0) {// 没有生效台风
                tvTyphoonName.setText(getString(R.string.no_typhoon));
            }

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
            tvTyphoonYear.setVisibility(View.VISIBLE);

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
        } else if (i == R.id.tvTyphoonYear) {
            dialogTimeYear("选择年份");

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

        } else if (i == R.id.tvLink) {
            isShowLink = !isShowLink;
            if (isShowLink) {
                tvLink.setTextColor(Color.WHITE);
                tvLink.setBackgroundResource(R.drawable.shawn_bg_typhoon_link_press);
                llLink.setVisibility(View.VISIBLE);
            }else {
                tvLink.setTextColor(getResources().getColor(R.color.text_color3));
                tvLink.setBackgroundResource(R.drawable.shawn_bg_typhoon_link);
                llLink.setVisibility(View.GONE);
            }
        } else if (i == R.id.tvLinkRadar) {
            isLinkRadar = !isLinkRadar;
            if (isLinkRadar) {
                tvLinkRadar.setTextColor(Color.WHITE);
                tvLinkRadar.setBackgroundResource(R.drawable.shawn_bg_warning_list);
            }else {
                tvLinkRadar.setTextColor(getResources().getColor(R.color.text_color3));
                tvLinkRadar.setBackgroundColor(Color.TRANSPARENT);
                removeRadarOverlay();
            }
        } else if (i == R.id.tvLinkCloud) {
            isLinkCloud = !isLinkCloud;
            if (isLinkCloud) {
                tvLinkCloud.setTextColor(Color.WHITE);
                tvLinkCloud.setBackgroundResource(R.drawable.shawn_bg_warning_list);
            }else {
                tvLinkCloud.setTextColor(getResources().getColor(R.color.text_color3));
                tvLinkCloud.setBackgroundColor(Color.TRANSPARENT);
                removeCloudOverlay();
            }
        } else if (i == R.id.ivLocation) {
            clickAdcode = locationAdcode;
            clickLatLng = locationLatLng;

            if (isRanging) {//测距状态下
                addLocationMarker();
                addLocationCircles();
                removeRange(null);
                ranging(null);
            }

            if (isShowMinute) {//选中分钟降水状态下
                if (clickLatLng != null) {
                    addLocationMarker();
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude, MAPCLICK_MINUTE);
                    OkHttpMinute(clickLatLng.longitude, clickLatLng.latitude);
                }
            }

            if (isShowWarning) {//选中预警状态下
                if (clickLatLng != null) {
                    addLocationMarker();
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude, MAPCLICK_WARNING);
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
            isShowFact = !isShowFact;
            if (isShowFact) {
                tvFactTime.setVisibility(View.VISIBLE);
                layoutFact.setVisibility(View.VISIBLE);
                llFact.setBackgroundResource(R.drawable.shawn_bg_corner_fact_press);
                ivFact.setImageResource(R.drawable.shawn_icon_fact_press);
                tvFact.setTextColor(Color.WHITE);
                tvFactStr.setVisibility(View.VISIBLE);
                sbFact.setVisibility(View.VISIBLE);
                if (factBitmap == null) {
                    OkHttpFactChart();
                }else {
                    drawFactBitmap(factBitmap, true);
                    if (reFactPrompt.getVisibility() != View.VISIBLE) {
                        animationDownToUp(reFactPrompt);
                        sbFact.setChecked(true);
                    }
                    drawFactMarkers(currentFactChartImgUrl);
                }
            }else {
                tvFactTime.setVisibility(View.GONE);
                layoutFact.setVisibility(View.GONE);
                llFact.setBackgroundColor(Color.TRANSPARENT);
                ivFact.setImageResource(R.drawable.shawn_icon_fact);
                tvFact.setTextColor(getResources().getColor(R.color.text_color3));
                tvFactStr.setVisibility(View.GONE);
                sbFact.setVisibility(View.GONE);
                removeFact();
                removeFactMarkers();
            }
        } else if (i == R.id.tvFactList) {
            Intent intent = new Intent(this, ShawnFactActivity.class);
            intent.putExtra("userAuthority", userAuthority);
            startActivity(intent);
        } else if (i == R.id.tvFactPoint) {
            isShowFactPoint = !isShowFactPoint;
            if (isShowFactPoint) {
                tvFactPoint.setText("隐藏站点");
            }else {
                tvFactPoint.setText("显示站点");
            }
            switchFactMarkers(isShowFactPoint);
        } else if (i == R.id.ivFactClose) {
            if (reFactPrompt.getVisibility() == View.VISIBLE) {
                animationUpToDown(reFactPrompt);
                sbFact.setChecked(false);
            } else {
                animationDownToUp(reFactPrompt);
                sbFact.setChecked(true);
            }


            //卫星拼图
        } else if (i == R.id.llSatelite) {
            isShowCloud = !isShowCloud;
            if (isShowCloud) {
                layoutCloud.setVisibility(View.VISIBLE);
                llSatelite.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivSatelite.setImageResource(R.drawable.shawn_icon_satelite_press);
                tvSatelite.setTextColor(Color.WHITE);
                tvCloudStr.setVisibility(View.VISIBLE);
                sbCloud.setVisibility(View.VISIBLE);
                if (cloudList.size() <= 0) {
                    OkHttpCloud();
                } else {
                    if (reCloudPrompt.getVisibility() != View.VISIBLE) {
                        animationDownToUp(reCloudPrompt);
                        sbCloud.setChecked(true);
                    }
                    if (cloudThread != null) {
                        int index = cloudThread.index;
                        if (index >= cloudList.size()-1) {
                            index = cloudList.size()-1;
                        }
                        RadarDto dto = cloudList.get(index);
                        if (!TextUtils.isEmpty(dto.path)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                            if (bitmap != null) {
                                drawCloud(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                            }
                        }
                        changeCloudSeekbarProgress(dto.time, index, cloudList.size()-1);
                    }
                }
            } else {
                layoutCloud.setVisibility(View.GONE);
                llSatelite.setBackgroundColor(Color.TRANSPARENT);
                ivSatelite.setImageResource(R.drawable.shawn_icon_satelite);
                tvSatelite.setTextColor(getResources().getColor(R.color.text_color3));
                tvCloudStr.setVisibility(View.GONE);
                sbCloud.setVisibility(View.GONE);
                removeCloudOverlay();
                if (cloudThread != null) {
                    cloudThread.pause();
                    ivCloudPlay.setImageResource(R.drawable.shawn_icon_play);
                }
            }
        }else if (i == R.id.ivCloudPlay) {
            if (cloudThread != null) {
                if (cloudThread.getCurrentState() == RadarThread.STATE_NONE) {
                    cloudThread.start();
                    ivCloudPlay.setImageResource(R.drawable.shawn_icon_pause);
                }else if (cloudThread.getCurrentState() == RadarThread.STATE_PLAYING) {
                    cloudThread.pause();
                    ivCloudPlay.setImageResource(R.drawable.shawn_icon_play);
                }else if (cloudThread.getCurrentState() == RadarThread.STATE_PAUSE) {
                    cloudThread.play();
                    ivCloudPlay.setImageResource(R.drawable.shawn_icon_pause);
                }
            }
        }else if (i == R.id.ivCloudClose) {
            if (reCloudPrompt.getVisibility() == View.VISIBLE) {
                animationUpToDown(reCloudPrompt);
                sbCloud.setChecked(false);
            } else {
                animationDownToUp(reCloudPrompt);
                sbCloud.setChecked(true);
            }


            //雷达拼图
        } else if (i == R.id.llRadar) {
            isShowRadar = !isShowRadar;
            if (isShowRadar) {
                layoutRadar.setVisibility(View.VISIBLE);
                llRadar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ivRadar.setImageResource(R.drawable.shawn_icon_radar_press);
                tvRadar.setTextColor(Color.WHITE);
                tvRadarStr.setVisibility(View.VISIBLE);
                sbRadar.setVisibility(View.VISIBLE);
                if (radarList.size() <= 0) {
                    OkHttpRadar();
                } else {
                    if (reRadarPrompt.getVisibility() != View.VISIBLE) {
                        animationDownToUp(reRadarPrompt);
                        sbRadar.setChecked(true);
                    }
                    if (radarThread != null) {
                        int index = radarThread.index;
                        if (index >= radarList.size()-1) {
                            index = radarList.size()-1;
                        }
                        RadarDto dto = radarList.get(index);
                        if (!TextUtils.isEmpty(dto.path)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(dto.path);
                            if (bitmap != null) {
                                drawRadar(bitmap, dto.p1, dto.p2, dto.p3, dto.p4);
                            }
                        }
                        changeRadarSeekbarProgress(dto.time, index, radarList.size()-1);
                    }
                }
            } else {
                layoutRadar.setVisibility(View.GONE);
                llRadar.setBackgroundColor(Color.TRANSPARENT);
                ivRadar.setImageResource(R.drawable.shawn_icon_radar);
                tvRadar.setTextColor(getResources().getColor(R.color.text_color3));
                tvRadarStr.setVisibility(View.GONE);
                sbRadar.setVisibility(View.GONE);
                removeRadarOverlay();
                if (radarThread != null) {
                    radarThread.pause();
                    ivRadarPlay.setImageResource(R.drawable.shawn_icon_play);
                }
            }
        }else if (i == R.id.ivRadarPlay) {
            if (radarThread != null) {
                if (radarThread.getCurrentState() == RadarThread.STATE_NONE) {
                    radarThread.start();
                    ivRadarPlay.setImageResource(R.drawable.shawn_icon_pause);
                }else if (radarThread.getCurrentState() == RadarThread.STATE_PLAYING) {
                    radarThread.pause();
                    ivRadarPlay.setImageResource(R.drawable.shawn_icon_play);
                }else if (radarThread.getCurrentState() == RadarThread.STATE_PAUSE) {
                    radarThread.play();
                    ivRadarPlay.setImageResource(R.drawable.shawn_icon_pause);
                }
            }
        }else if (i == R.id.ivRadarClose) {
            if (reRadarPrompt.getVisibility() == View.VISIBLE) {
                animationUpToDown(reRadarPrompt);
                sbRadar.setChecked(false);
            } else {
                animationDownToUp(reRadarPrompt);
                sbRadar.setChecked(true);
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
                    if (reWarningPrompt.getVisibility() != View.VISIBLE) {
                        animationDownToUp(reWarningPrompt);
                        sbWarning.setChecked(true);
                    }
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
                if (foreMarkers.size() <= 0) {
                    OkHttpAllCitys();
                } else {
                    addForeMarkers();
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
                    searchAddrByLatLng(clickLatLng.latitude, clickLatLng.longitude, MAPCLICK_MINUTE);
                    OkHttpMinute(clickLatLng.longitude, clickLatLng.latitude);
                }
            } else {
                layoutMinute.setVisibility(View.GONE);
//                tvMinuteStr.setVisibility(View.GONE);
//                sbMinute.setVisibility(View.GONE);
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
        removeCloudOverlay();
        removeCloudThread();
        removeRadarOverlay();
        removeRadarThread();
    }

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
