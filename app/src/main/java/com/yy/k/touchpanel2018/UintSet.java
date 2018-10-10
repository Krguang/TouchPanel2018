package com.yy.k.touchpanel2018;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class UintSet extends Activity {

    CheckBox yaCha0_50_CB;
    CheckBox yaCha0_100_CB;
    CheckBox yaCha50_50_CB;

    CheckBox neiBuXieYi_CB;
    CheckBox waiBuXieYi_CB;

    CheckBox maiChong_CB;
    CheckBox dianPing_CB;

    EditText slaveAdd_ET;

    SharedPreferences sharedUintSet;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uint_set);

        sharedUintSet = this.getSharedPreferences("uint_set",this.MODE_WORLD_READABLE);
        editor = sharedUintSet.edit();


        yaCha0_50_CB = findViewById(R.id.yaCha0_50_cb);
        yaCha0_100_CB = findViewById(R.id.yaCha0_100_cb);
        yaCha50_50_CB = findViewById(R.id.yaCha50_50_cb);

        neiBuXieYi_CB = findViewById(R.id.neiBuXieYi_cb);
        waiBuXieYi_CB = findViewById(R.id.waiBuXieYi_cb);

        maiChong_CB = findViewById(R.id.maiChong_cb);
        dianPing_CB = findViewById(R.id.dianPing_cb);


        slaveAdd_ET = findViewById(R.id.slave_add_et);


        //压差量程 0，1，2 分别对应 0-50Pa,0-100Pa,-50-+50Pa;

        if (sharedUintSet.getInt("压差量程",0) == 0){

            yaCha0_50_CB.setChecked(true);
            yaCha0_100_CB.setChecked(false);
            yaCha50_50_CB.setChecked(false);
        }else if (sharedUintSet.getInt("压差量程",0) == 1){

            yaCha0_50_CB.setChecked(false);
            yaCha0_100_CB.setChecked(true);
            yaCha50_50_CB.setChecked(false);

        }else if (sharedUintSet.getInt("压差量程",0) == 2){

            yaCha0_50_CB.setChecked(false);
            yaCha0_100_CB.setChecked(false);
            yaCha50_50_CB.setChecked(true);

        }

        //协议类型：0，1分别对用内部协议，外部协议
        if (sharedUintSet.getInt("协议类型",0) == 0){

            neiBuXieYi_CB.setChecked(true);
            waiBuXieYi_CB.setChecked(false);
        }else if (sharedUintSet.getInt("协议类型",0) == 1){

            neiBuXieYi_CB.setChecked(false);
            waiBuXieYi_CB.setChecked(true);

        }

        //按键模式：0，1 分别对应脉冲模式，电平模式
        if (sharedUintSet.getInt("按键模式",0) == 0){

            maiChong_CB.setChecked(true);
            dianPing_CB.setChecked(false);
        }else if (sharedUintSet.getInt("按键模式",0) == 1){

            maiChong_CB.setChecked(false);
            dianPing_CB.setChecked(true);
        }


        slaveAdd_ET.setText(sharedUintSet.getInt("从机地址", 1)+"");


        yaCha0_50_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_100_CB.setChecked(false);
                    yaCha50_50_CB.setChecked(false);

                }
                editor.putInt("压差量程",0);
                editor.apply();
            }
        });

        yaCha0_100_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_50_CB.setChecked(false);
                    yaCha50_50_CB.setChecked(false);
                }
                editor.putInt("压差量程",1);
                editor.apply();
            }
        });

        yaCha50_50_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    yaCha0_50_CB.setChecked(false);
                    yaCha0_100_CB.setChecked(false);

                }
                editor.putInt("压差量程",2);
                editor.apply();
            }
        });

        neiBuXieYi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    waiBuXieYi_CB.setChecked(false);
                }
                editor.putInt("协议类型",0);
                editor.apply();
            }
        });


        waiBuXieYi_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    neiBuXieYi_CB.setChecked(false);
                }
                editor.putInt("协议类型",1);
                editor.apply();
            }
        });

        maiChong_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dianPing_CB.setChecked(false);
                }
                editor.putInt("按键模式",0);
                editor.apply();
            }
        });

        dianPing_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    maiChong_CB.setChecked(false);
                }
                editor.putInt("按键模式",1);
                editor.apply();
            }
        });

        slaveAdd_ET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                editor.putInt("从机地址", Integer.parseInt(slaveAdd_ET.getText().toString()));
                editor.apply();
                return false;
            }
        });
    }
}
