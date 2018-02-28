package com.yangbin.getphoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取手机系统内部信息工具类
 * Created by yangbin on 3/2/2017.
 */

public class GetSystemInfoUtil {



    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.DISPLAY;
        //return android.os.Build.VERSION.RELEASE;

    }


    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }


    /**
     * 获取SN
     *
     * @return
     */
    public static String getSn(Context ctx) {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception ignored) {

        }

        return serial;
    }

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者Meid
     *
     * @return 手机IMEI
     */
    public static String getImeiOrMeid(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }


        return null;
    }

    /**
     * 拿到imei或者meid后判断是有多少位数
     *
     * @param ctx
     * @return
     */
    public static int getNumber(Context ctx) {
        int count = 0;
        long number = Long.parseLong(getImeiOrMeid(ctx).trim());

        while (number > 0) {
            number = number / 10;
            count++;
        }
        return count;
    }


    /**
     *  5.0 6.0统一使用这个获取IMEI IMEI2 MEID
     *
     * @param ctx
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Map getImeiAndMeid(Context ctx) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        Class<?> clazz = null;
        Method method = null;//(int slotId)

        try {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
            String meid = (String) method.invoke(null, "ril.cdma.meid", "");
            map.put("meid", meid);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray != null && imeiArray.length > 0) {
                    map.put("imei1", imeiArray[0]);

                    if (imeiArray.length > 1) {
                        map.put("imei2", imeiArray[1]);
                    } else {
                        map.put("imei2", mTelephonyManager.getDeviceId(1));
                    }
                } else {
                    map.put("imei1", mTelephonyManager.getDeviceId(0));
                    map.put("imei2", mTelephonyManager.getDeviceId(1));
                }
            } else {
                map.put("imei1", mTelephonyManager.getDeviceId(0));
                map.put("imei2", mTelephonyManager.getDeviceId(1));

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Map getIMEII(Context context) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = new HashMap<String, String>();
        Class<?> c = null;
        Method get = null;
        TelephonyManager tm = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        try {
            c = Class.forName("android.os.SystemProperties");
            get = c.getMethod("get", String.class);
            String imeis = (String) get.invoke(c, "ril.gsm.imei");
            String deviceId = tm.getDeviceId();
            if (getSystemModel().equals("PRO 6 Plus") || getSystemModel().equals("M571C")) {
                map.put("IMEI1", tm.getDeviceId(0));
                map.put("IMEI2", tm.getDeviceId(1));
            }

            // 获取电信手机, 多个IMEI
            else if (!TextUtils.isEmpty(imeis) && imeis.length() > 15) {
                String im[] = imeis.split(",");
                map.put("IMEI1", im[0]);
                map.put("IMEI2", im[1]);
            } else { // 普通手机获取IMEI
                map.put("IMEI1", deviceId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }


    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int vercoe = 0;

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            vercoe = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return vercoe;
    }



}
