package com.yy.k.touchpanel2018;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yy.k.touchpanel2018.R;

import android_serialport_api.Modbus_Slav;
import utils.SpUtils;

public class UintSet extends Activity {

    CheckBox yaCha0_50_CB;
    CheckBox yaCha0_100_CB;
    CheckBox yaCha50_50_CB;

    CheckBox neiBuXieYi_CB;
    CheckBox waiBuXieYi_CB;

    CheckBox maiChong_CB;
    CheckBox dianPing_CB;

    CheckBox liangGuanZhi_CB;
    CheckBox siGuanZhi_CB;

    EditText slaveAdd_ET;

    Modbus_Slav modbusSlave = Modbus_Slav.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uint_set);

        yaCha0_50_CB = findViewById(R.id.yaCha0_50_cb);
        yaCha0_100_CB = findViewById(R.id.yaCha0_100_cb);
        yaCha50_50_CB = findViewById(R.id.yaCha50_50_cb);

        neiBuXieYi_CB = findViewById(R.id.neiBuXieYi_cb);
        waiBuXieYi_CB = findViewById(R.id.waiBuXieYi_cb);

        maiChong_CB = findViewById(R.id.maiChong_cb);
        dianPing_CB = findViewById(R.id.dianPing_cb);

        liangGuanZhi_CB = findViewById(R.id.liangGuanZhi_cb);
        siGuanZhi_CB = findViewById(R.id.siGuanZhi_cb);

        slaveAdd_ET = findViewById(R.id.slave_add_et);


        //压差量程 0，1，2 分别对应 0-50Pa,0-100Pa,-50-+50Pa;

        if (modbusSlave.yaChaLiangCheng == 0){

            yaCha0_50_CB.setChecked(true);
            yaCha0_100_CB.setChecked(false);
            yaCha50_50_CB.setChecked(false);
        }else if (modbusSlave.yaChaLiangCheng == 1){

            yaCha0_50_CB.setChecked(false);
            yaCha0_100_CB.setChecked(true);
            yaCha50_50_CB.setChecked(false);

        }else if (modbusSlave.yaChaLiangCheng == 2){

            yaCha0_50_CB.setChecked(false);
            yaCha0_100_CB.setChecked(false);
            yaCha50_50_CB.setChecked(true);

        }

        //协议类型：0，1分别对用内部协议，外部协议
        if (modbusSlave.xieYiLeiXing == 0){

            neiBuXieYi_CB.setChecked(true);
            waiBuXieYi_CB.setChecked(false);
        }else if (modbusSlave.xieYiLeiXing == 1){

            neiBuXieYi_CB.setChecked(false);
            waiBuXieYi_CB.setChecked(true);

        }

        //按键模式：0，1 分别对应脉冲模式，电平模式
        if (SpUtils.getInt(this,"按键模式",0) == 0){

            maiChong_CB.setChecked(true);
            dianPing_CB.setChecked(false);
        }else if (SpUtils.getInt(this,"按键模式",0) == 1){

            maiChong_CB.setChecked(false);
            dianPing_CB.setChecked(true);
        }

        if (SpUtils.getInt(this,"供水方式",0) == 0){
            liangGuanZhi_CB.setChecked(true);
            siGuanZhi_CB.setChecked(false);
        }else {
            liangGuanZhi_CB.setChecked(false);
            siGuanZhi_CB.setChecked(true);
        }

        slaveAdd_ET.setText(modbusSlave.slaveAdd+"");

        yaCha0_50_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_100_CB.setChecked(false);
                    yaCha50_50_CB.setChecked(false);

                }
                modbusSlave.yaChaLiangCheng = 0;
                SpUtils.putInt(getApplicationContext(),"压差量程",0);
            }
        });

        yaCha0_100_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_50_CB.setChecked(false);
                    yaCha50_50_CB.setChecked(false);
                }
                modbusSlave.yaChaLiangCheng = 1;
                SpUtils.putInt(getApplicationContext(),"压差量程",1);
            }
        });

        yaCha50_50_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_50_CB.setChecked(false);
                    yaCha0_100_CB.setChecked(false);

                }
                modbusSlave.yaChaLiangCheng = 2;
                SpUtils.putInt(getApplicationContext(),"压差量程",2);
            }
        });

        neiBuXieYi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    waiBuXieYi_CB.setChecked(false);
                }
                modbusSlave.xieYiLeiXing = 0;
                SpUtils.putInt(getApplicationContext(),"协议类型",0);
            }
        });


        waiBuXieYi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    neiBuXieYi_CB.setChecked(false);
                }
                modbusSlave.xieYiLeiXing = 1;
                SpUtils.putInt(getApplicationContext(),"协议类型",1);
            }
        });

        maiChong_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dianPing_CB.setChecked(false);
                }
                SpUtils.putInt(getApplicationContext(),"按键模式",0);
            }
        });

        dianPing_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    maiChong_CB.setChecked(false);
                }
                SpUtils.putInt(getApplicationContext(),"按键模式",1);
            }
        });

        slaveAdd_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                modbusSlave.slaveAdd = Integer.parseInt(slaveAdd_ET.getText().toString());
                SpUtils.putInt(getApplicationContext(),"从机地址",modbusSlave.slaveAdd);
                return false;
            }
        });


        liangGuanZhi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    siGuanZhi_CB.setChecked(false);
                    SpUtils.putInt(getApplicationContext(),"供水方式",0);
                }
            }
        });

        siGuanZhi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    liangGuanZhi_CB.setChecked(false);
                    SpUtils.putInt(getApplicationContext(),"供水方式",1);
                }
            }
        });
    }
}
