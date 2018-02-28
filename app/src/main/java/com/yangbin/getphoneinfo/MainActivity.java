package com.yangbin.getphoneinfo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView mTvPhoneSn;
    TextView mTvPhoneMeid;
    TextView mTvPhoneImei;
    TextView mTvPhoneOtherImei;
    TextView mTvVersionNumber;
    TextView mTvPhoneModels;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvPhoneSn = (TextView) findViewById(R.id.tv_phone_sn);
        mTvPhoneMeid = (TextView) findViewById(R.id.tv_phone_meid);
        mTvPhoneImei = (TextView) findViewById(R.id.tv_phone_imei);
        mTvPhoneOtherImei = (TextView) findViewById(R.id.tv_phone_otherimei);
        mTvVersionNumber = (TextView) findViewById(R.id.tv_version_number);
        mTvPhoneModels = (TextView) findViewById(R.id.tv_phone_models);


        if (Build.VERSION.SDK_INT < 21) {
            //如果获取系统的IMEI/MEID，14位代表meid 15位是imei
            if (GetSystemInfoUtil.getNumber(getApplication()) == 14) {
                mTvPhoneMeid.setText(GetSystemInfoUtil.getImeiOrMeid(getApplication()));//meid
                mTvPhoneImei.setText("");
                mTvPhoneOtherImei.setText("");

            } else if (GetSystemInfoUtil.getNumber(getApplication()) == 15) {
                mTvPhoneImei.setText(GetSystemInfoUtil.getImeiOrMeid(getApplication()));//imei1
                mTvPhoneMeid.setText("");
                mTvPhoneOtherImei.setText("");
            }
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT >= 21) {
            Map<String, String> map = GetSystemInfoUtil.getIMEII(getApplication());
            Map<String, String> mapMeid = GetSystemInfoUtil.getImeiAndMeid(getApplication());
            mTvPhoneImei.setText(map.get("IMEI1"));//imei1
            if (map.get("IMEI2") == null || ("null").equals(map.get("IMEI2"))) {
                mTvPhoneOtherImei.setText("");//imei2
            } else {
                mTvPhoneOtherImei.setText(map.get("IMEI2"));//imei2
            }

            mTvPhoneMeid.setText(mapMeid.get("meid"));//meid
        }
        mTvPhoneSn.setText(GetSystemInfoUtil.getSn(getApplication()));//SN
        mTvPhoneModels.setText(GetSystemInfoUtil.getSystemModel());//手机型号 PRO6
        mTvVersionNumber.setText(GetSystemInfoUtil.getSystemVersion());//软件版本号  FLYME 6.02
    }


}

