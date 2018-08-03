package com.cxwl.shawn.zhongshan.decision.util;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密访问接口地址工具类
 */

public class SecretUrlUtil {

    private static final String APPID = "6f688d62594549a2";//机密需要用到的AppId
    private static final String CHINAWEATHER_DATA = "chinaweather_data";//加密秘钥名称
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);

    /**
     * 获取9位城市id
     * @param lng 经度
     * @param lat 维度
     * @return
     */
    public static final String geo(double lng, double lat) {
        String URL = "http://geoload.tianqi.cn/ag9/";
        String sysdate = sdf1.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("lon=").append(lng);
        buffer.append("&");
        buffer.append("lat=").append(lat);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 空气污染
     * @return
     */
    public static final String airpollution() {
        String URL = "http://scapi.weather.com.cn/weather/getaqiobserve";
        String sysdate = sdf1.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 天气统计
     */
    public static String statistic() {
        String URL = "http://scapi.weather.com.cn/weather/stationinfo";
        String sysdate = sdf1.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 天气统计详情
     * @return
     */
    public static String statisticDetail(String stationid) {
        String URL = "http://scapi.weather.com.cn/weather/historycount";
        String sysdate = sdf1.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("stationid=").append(stationid);
        buffer.append("&");
        buffer.append("type=").append("all");
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 获取监测站信息
     */
    public static String stationsInfo(String stationIds) {
        String URL = "http://decision-171.tianqi.cn/weather/rgwst/NewestDataNew";
        String sysdate = sdf2.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("stationids=").append(stationIds);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }


    /**
     * 等风T639
     * @param type
     * @param index
     * @return
     */
    public static String windT639(String type, String index) {
        String URL = "http://scapi.weather.com.cn/weather/micaps/windfile";
        String sysdate = sdf3.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("type=").append(type);
        buffer.append("&");
        buffer.append("index=").append(index);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 等风GFS
     * @param type
     * @return
     */
    public static String windGFS(String type) {
        String URL = "http://scapi.weather.com.cn/weather/getwindmincas";
        String sysdate = sdf3.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("type=").append(type);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 风场某点详情
     */
    public static final String windDetail(double lng, double lat) {
        String URL = "http://scapi.weather.com.cn/weather/getBase_WindD";
        String sysdate = sdf3.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("lon=").append(lng);
        buffer.append("&");
        buffer.append("lat=").append(lat);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 实况站点详情
     * @return
     */
    public static String stationDetail(String stationids, String interfaceType) {
        String URL = "http://decision-171.tianqi.cn/weather/rgwst/OneDayStatistics";//
        if (TextUtils.equals(interfaceType, "newOneDay")) {
            URL = "http://decision-171.tianqi.cn/weather/rgwst/newOneDayStatistics";
        }
        String sysdate = sdf2.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("stationids=").append(stationids);
        buffer.append("&");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 预警界面用到的14类灾害预警图层
     * @param type
     * @return
     */
    public static String warningLayer(String type) {
        String URL = "https://scapi.tianqi.cn/weather/yjtc";
        String sysdate = sdf1.format(new Date());
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append("?");
        buffer.append("date=").append(sysdate);
        buffer.append("&");
        buffer.append("mold=").append("zaihaiyj");
        buffer.append("&");
        buffer.append("type=").append(type);
        buffer.append("&");
        buffer.append("map=").append("china");
        buffer.append("&");
        buffer.append("appid=").append(APPID);

        String key = getKey(CHINAWEATHER_DATA, buffer.toString());
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("appid=").append(APPID.substring(0, 6));
        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 获取空气质量24小时曲线预报
     * @return
     */
    public static final String airForecast(double lng, double lat) {
        final String appid = "182e7b37a63445558b05fbcce2b3d6e7";//机密需要用到的AppId
        final String keyName = "9d0232248739420fa4ff19593c731c11";//加密秘钥名称
        String URL = "http://openapi.mlogcn.com:8000/api/aqi/fc/coor/";
        long timestamp = new Date().getTime();
        String start = sdf3.format(timestamp);
        String end = sdf3.format(timestamp+1000*60*60*24);
        StringBuffer buffer = new StringBuffer();
        buffer.append(URL);
        buffer.append(lng).append("/").append(lat);
        buffer.append("/h/");
        buffer.append(start).append("/").append(end);
        buffer.append(".json?");
        buffer.append("appid=").append(appid);
        buffer.append("&");
        buffer.append("timestamp=").append(timestamp);
        buffer.append("&");

        String key = getKey(keyName, timestamp+appid);
        buffer.delete(buffer.lastIndexOf("&"), buffer.length());

        buffer.append("&");
        buffer.append("key=").append(key.substring(0, key.length() - 3));
        String result = buffer.toString();
        return result;
    }

    /**
     * 获取秘钥
     * @param key
     * @param src
     * @return
     */
    public static final String getKey(String key, String src) {
        try{
            byte[] rawHmac;
            byte[] keyBytes = key.getBytes("UTF-8");
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            rawHmac = mac.doFinal(src.getBytes("UTF-8"));
            String encodeStr = Base64.encodeToString(rawHmac, Base64.DEFAULT);
            String keySrc = URLEncoder.encode(encodeStr, "UTF-8");
            return keySrc;
        }catch(Exception e){
            Log.e("SceneException", e.getMessage(), e);
        }
        return null;
    }

}
