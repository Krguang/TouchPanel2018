package com.yy.k.touchpanel2018;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android_serialport_api.Modbus_Slav;
import utils.SpUtils;


public class UnitMonitoringDataActivity extends Activity {
    Button bt_bendiControl;
    Button bt_chuXiaoWaring;
    Button bt_fengjiquefengwaring;
    Button bt_zhongxiaowaring;
    Button bt_gaowenwaring;
    Button bt_bendicontrol;
    Button bt_yuanchengcontrol;
    Button bt_coldwater;
    Button bt_hotwater;
    Button bt_xiaodu;
    Button bt_paifeng;
    Button bt_fengji;
    Button bt_zhiban;
    Button bt_fengjilun;
    Button bt_dianjiareone;
    Button bt_dianjiaretwo;
    Button bt_dianjiarethree;
    Button bt_jinfengflow1;
    Button bt_jinfengflow2;
    Button bt_jinfengflow3;
    Button bt_jinfengflow4;
    Button bt_jinfengflow5;
    Button bt_jinfengflow6;
    Button bt_jinfengflow7;
    Button bt_jinfengflow8;
    Button bt_jinfengflow9;
    Button bt_jinfengflow10;
    Button bt_jinfengflow11;
    Button bt_jinfengflow12;
    Button bt_paiFengJiLun;

    int flow_temp;

    TextView tv_coldWateropening;
    TextView tv_hotWateropening;
    TextView tv_humidifieOpening;
    TextView tv_huiFengWenDu;
    TextView tv_huiFengShiDu;
    TextView tv_sheDingWenDu;
    TextView tv_sheDingShiDu;

    private int fengjilun_temp = 0;
    private int paifengJi_temp = 0;

    Intent intent = new Intent();
    Timer unit_time = new Timer();
    SharedPreferences sharedPreferences;

    private double huiFengWenDuDouble;
    private double huiFengShiDuDoubLe;
    private double sheDingWenDuDouble;
    private double sheDingShiDuDouble;


    private double humiDouble;
    private double coldWaterDouble;
    private double hotWaterDouble;

    private TextView tv_panGuan;
    private TextView tv_dianJiaRe;
    private TextView tv_zaiReDuan;
    private TextView tv_lengShuiFaKaiDu;
    private TextView tv_reShuiFaKaiDu;
    private TextView tv_dianJiaReGaowen;

    private Button bt_reShuiFa;
    private Button bt_dianJiaRe;

    Modbus_Slav modbusSlave;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_monitoringdata);

        bt_bendiControl = (Button) findViewById(R.id.bt_bendicontrol_id);
        bt_chuXiaoWaring = (Button) findViewById(R.id.bt_chuxaiowaring_id);
        bt_fengjiquefengwaring = (Button) findViewById(R.id.bt_fengjiquefengwaring_id);
        bt_zhongxiaowaring = (Button) findViewById(R.id.bt_zhongxiaowaring_id);
        bt_gaowenwaring = (Button) findViewById(R.id.bt_gaowenwaring_id);
        bt_bendicontrol = (Button) findViewById(R.id.bt_bendicontrol_id);
        bt_yuanchengcontrol = (Button) findViewById(R.id.bt_yuanchengcontrol_id);
        bt_coldwater = (Button) findViewById(R.id.bt_coldwater_id);
        bt_hotwater = (Button) findViewById(R.id.bt_hotwater_id);
        bt_xiaodu = (Button) findViewById(R.id.bt_xiaodu_id);
        bt_paifeng = (Button) findViewById(R.id.bt_paifeng_id);
        bt_fengji = (Button) findViewById(R.id.bt_fengji_id);
        bt_zhiban = (Button) findViewById(R.id.bt_zhiban_id);
        tv_coldWateropening = (TextView) findViewById(R.id.tv_wateropening_id);
        tv_hotWateropening = (TextView) findViewById(R.id.tv_hotWaterOpening_id);
        tv_humidifieOpening = (TextView) findViewById(R.id.tv_HumidifieOpening_id);

        tv_huiFengWenDu = findViewById(R.id.tv_huifengwendu);
        tv_huiFengShiDu = findViewById(R.id.tv_huifengshidu);
        tv_sheDingWenDu = findViewById(R.id.tv_shedingwendu);
        tv_sheDingShiDu = findViewById(R.id.tv_shedingshidu);

        bt_fengjilun = (Button) findViewById(R.id.bt_fengjilun_id);

        bt_dianjiareone = (Button) findViewById(R.id.bt_dianjiareone_id);
        bt_dianjiaretwo = (Button) findViewById(R.id.bt_dianjiaretwo_id);
        bt_dianjiarethree = (Button) findViewById(R.id.bt_dianjiarethree_id);
        bt_jinfengflow1 = (Button) findViewById(R.id.bt_jinfengflow1_id);
        bt_jinfengflow2 = (Button) findViewById(R.id.bt_jinfengflow2_id);
        bt_jinfengflow3 = (Button) findViewById(R.id.bt_jinfengflow3_id);
        bt_jinfengflow4 = (Button) findViewById(R.id.bt_jinfengflow4_id);
        bt_jinfengflow5 = (Button) findViewById(R.id.bt_jinfengflow5_id);
        bt_jinfengflow6 = (Button) findViewById(R.id.bt_jinfengflow6_id);
        bt_jinfengflow7 = (Button) findViewById(R.id.bt_jinfengflow7_id);
        bt_jinfengflow8 = (Button) findViewById(R.id.bt_jinfengflow8_id);
        bt_jinfengflow9 = (Button) findViewById(R.id.bt_jinfengflow9_id);
        bt_jinfengflow10 = (Button) findViewById(R.id.bt_jinfengflow10_id);
        bt_jinfengflow11 = (Button) findViewById(R.id.bt_jinfengflow11_id);
        bt_jinfengflow12 = (Button) findViewById(R.id.bt_jinfengflow12_id);
        bt_paiFengJiLun = findViewById(R.id.paiFengJi);


        tv_panGuan = findViewById(R.id.tv_lengPanGuan_id);
        tv_dianJiaRe = findViewById(R.id.tv_dianJiaRe_id);
        tv_zaiReDuan = findViewById(R.id.tv_zaiReDuan_id);
        tv_lengShuiFaKaiDu = findViewById(R.id.tv_lengSHuiFaKaiDu_id);
        tv_reShuiFaKaiDu = findViewById(R.id.tv_reShuiFaKaiDu_id);
        tv_dianJiaReGaowen = findViewById(R.id.tv_dianJiaReGaoWen_id);

        bt_reShuiFa = findViewById(R.id.pic_reShuiFa_id);
        bt_dianJiaRe = findViewById(R.id.pic_DianJiaRe_id);
        modbusSlave = Modbus_Slav.getInstance();

        unit_time.schedule(unitTime, 100, 100);
    }


    TimerTask unitTime = new TimerTask() {

        public void run() {
            runOnUiThread(new Runnable() {      // UI thread

                public void run() {

                    int gongSHuiFangShi = SpUtils.getInt(getApplicationContext(),"供水方式",0);

                    huiFengWenDuDouble = modbusSlave.wenDu / 10.0;
                    huiFengShiDuDoubLe = modbusSlave.shiDu /10.0;
                    sheDingWenDuDouble = modbusSlave.wenDuSet / 10.0;
                    sheDingShiDuDouble = modbusSlave.shiDuSet /10.0;

                    humiDouble = modbusSlave.HumidifieOpening/10.0;
                    coldWaterDouble = modbusSlave.ColdWaterValveOpening/10.0;
                    hotWaterDouble = modbusSlave.HotWaterValveOpening/10.0;

                    String wenDuString = String.format(Locale.US,"%.1f",huiFengWenDuDouble);
                    String shiDuString = String.format(Locale.US,"%.1f",huiFengShiDuDoubLe);
                    String wenDuSetString = String.format(Locale.US,"%.1f",sheDingWenDuDouble);
                    String shiDuSetString = String.format(Locale.US,"%.1f",sheDingShiDuDouble);

                    String humiString = String.format(Locale.US,"%.1f",humiDouble);
                    String coldWaterString = String.format(Locale.US,"%.1f",coldWaterDouble);
                    String hotWaterString = String.format(Locale.US,"%.1f",hotWaterDouble);

                    tv_huiFengWenDu.setText("回风温度："+wenDuString+"℃");
                    tv_huiFengShiDu.setText("回风湿度："+shiDuString+"RH");
                    tv_sheDingWenDu.setText("设定温度："+wenDuSetString+"℃");
                    tv_sheDingShiDu.setText("设定湿度："+shiDuSetString+"RH");

                    tv_humidifieOpening.setText(humiString+"%");

                    if (gongSHuiFangShi ==0){   //两管制

                        tv_panGuan.setText("盘管");
                        tv_dianJiaRe.setText("电加热");
                        tv_zaiReDuan.setText("再热段");
                        tv_lengShuiFaKaiDu.setText("水阀开度");
                        tv_reShuiFaKaiDu.setText("");
                        tv_dianJiaReGaowen.setText("电加热高温报警");

                        if (modbusSlave.WinterInSummer == 1) {      //夏季 通冷水

                            tv_coldWateropening.setText(coldWaterString +"%");

                        }else {                         //冬季 通热水

                            tv_coldWateropening.setText(hotWaterString +"%");
                        }

                        tv_hotWateropening.setText("");

                        if (modbusSlave.ElectricWarmOneMonitoringPoint == 1) {
                            bt_dianjiareone.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiareone.setBackgroundResource(R.drawable.init_ing);
                        }

                        if (modbusSlave.ElectricWarmTwoMonitoringPoint == 1) {
                            bt_dianjiaretwo.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiaretwo.setBackgroundResource(R.drawable.init_ing);
                        }
                        if (modbusSlave.ElectricWarmThreeMonitoringPoint == 1) {
                            bt_dianjiarethree.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiarethree.setBackgroundResource(R.drawable.init_ing);
                        }

                        if (modbusSlave.ElectricWarmHighTemperatureMonitoringPoint == 0) {
                            bt_gaowenwaring.setBackgroundResource(R.drawable.waring);
                        } else {
                            bt_gaowenwaring.setBackgroundResource(R.drawable.init_ing);
                        }

                        bt_dianJiaRe.setBackgroundResource(R.drawable.dianjiare);
                        bt_reShuiFa.setBackgroundResource(R.drawable.touming);

                    }else {                     //四管制

                        tv_panGuan.setText("冷盘管");
                        tv_dianJiaRe.setText("");
                        tv_zaiReDuan.setText("热盘管");
                        tv_lengShuiFaKaiDu.setText("冷水阀开度");
                        tv_reShuiFaKaiDu.setText("热水阀开度");
                        tv_dianJiaReGaowen.setText("");

                        tv_coldWateropening.setText(coldWaterString+"%");
                        tv_hotWateropening.setText(hotWaterString+"%");

                        bt_dianJiaRe.setBackgroundResource(R.drawable.touming);
                        bt_reShuiFa.setBackgroundResource(R.drawable.reshuifa);

                        bt_dianjiareone.setBackgroundResource(R.drawable.touming);
                        bt_dianjiaretwo.setBackgroundResource(R.drawable.touming);
                        bt_dianjiarethree.setBackgroundResource(R.drawable.touming);
                        bt_gaowenwaring.setBackgroundResource(R.drawable.touming);
                    }


                    if (modbusSlave.PaiFengJiStartMonitoringPoint == 1){

                        if (paifengJi_temp == 0){
                            paifengJi_temp = 1;
                            bt_paiFengJiLun.setBackgroundResource(R.drawable.exhaust_air_1);
                        }else {
                            paifengJi_temp = 0;
                            bt_paiFengJiLun.setBackgroundResource(R.drawable.exhaust_air_2);
                        }

                    }

                    if (modbusSlave.FengjiZHuangTaiMonitoringPoint==1) {

                        flow_temp++;
                        if (flow_temp > 36)
                            flow_temp = 0;
                        switch (flow_temp) {
                            case 3: {

                                bt_jinfengflow1.setBackgroundResource(R.drawable.flow_2);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;
                            case 6: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(R.drawable.flow_3);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 9: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(R.drawable.flow_2);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 12: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(R.drawable.flow_3);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 15: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(R.drawable.flow_3);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 18: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(R.drawable.flow_0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;


                            case 21: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(R.drawable.flow_0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 24: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(R.drawable.flow_0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;


                            case 27: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(R.drawable.flow_0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 30: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(R.drawable.flow_1);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;

                            case 33: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(R.drawable.flow_1);
                                bt_jinfengflow12.setBackgroundResource(0);

                            }
                            break;
                            case 36: {

                                bt_jinfengflow1.setBackgroundResource(0);
                                bt_jinfengflow2.setBackgroundResource(0);
                                bt_jinfengflow3.setBackgroundResource(0);
                                bt_jinfengflow4.setBackgroundResource(0);
                                bt_jinfengflow5.setBackgroundResource(0);
                                bt_jinfengflow6.setBackgroundResource(0);
                                bt_jinfengflow7.setBackgroundResource(0);
                                bt_jinfengflow8.setBackgroundResource(0);
                                bt_jinfengflow9.setBackgroundResource(0);
                                bt_jinfengflow10.setBackgroundResource(0);
                                bt_jinfengflow11.setBackgroundResource(0);
                                bt_jinfengflow12.setBackgroundResource(R.drawable.flow_2);

                            }
                            break;


                            default:
                                break;
                        }


                        if (fengjilun_temp == 0) {
                            fengjilun_temp = 1;
                            bt_fengjilun.setBackgroundResource(R.drawable.fengshanlu2);
                        } else {
                            fengjilun_temp = 0;
                            bt_fengjilun.setBackgroundResource(R.drawable.fengshanlu1);
                        }

                        bt_fengji.setBackgroundResource(R.drawable.running);
                    }else {
                        bt_fengji.setBackgroundResource(R.drawable.init_ing);
                        bt_jinfengflow1.setBackgroundResource(0);
                        bt_jinfengflow2.setBackgroundResource(0);
                        bt_jinfengflow3.setBackgroundResource(0);
                        bt_jinfengflow4.setBackgroundResource(0);
                        bt_jinfengflow5.setBackgroundResource(0);
                        bt_jinfengflow6.setBackgroundResource(0);
                        bt_jinfengflow7.setBackgroundResource(0);
                        bt_jinfengflow8.setBackgroundResource(0);
                        bt_jinfengflow9.setBackgroundResource(0);
                        bt_jinfengflow10.setBackgroundResource(0);
                        bt_jinfengflow11.setBackgroundResource(0);
                        bt_jinfengflow12.setBackgroundResource(0);
                    }

                    if (modbusSlave.ChuXiaoMonitoringPoint == 1) {
                        bt_chuXiaoWaring.setBackgroundResource(R.drawable.waring);
                    } else {
                        bt_chuXiaoWaring.setBackgroundResource(R.drawable.init_ing);
                    }


                    if (modbusSlave.FengJiQueFengMonitoringPoint == 1) {
                        bt_fengjiquefengwaring.setBackgroundResource(R.drawable.waring);
                    } else {
                        bt_fengjiquefengwaring.setBackgroundResource(R.drawable.init_ing);
                    }

                    if (modbusSlave.ZhongXiaoMonitoringPoint == 1) {
                        bt_zhongxiaowaring.setBackgroundResource(R.drawable.waring);
                    } else {
                        bt_zhongxiaowaring.setBackgroundResource(R.drawable.init_ing);
                    }

                    if (modbusSlave.HandAutomaticallyMonitoringPoint == 1) {
                        bt_bendicontrol.setBackgroundResource(R.drawable.init_ing);
                        bt_yuanchengcontrol.setBackgroundResource(R.drawable.running);
                    } else {
                        bt_bendicontrol.setBackgroundResource(R.drawable.running);
                        bt_yuanchengcontrol.setBackgroundResource(R.drawable.init_ing);
                    }



                    if (modbusSlave.WinterInSummer == 1) {

                        bt_coldwater.setBackgroundResource(R.drawable.running);
                        bt_hotwater.setBackgroundResource(R.drawable.init_ing);


                    } else {
                        bt_coldwater.setBackgroundResource(R.drawable.init_ing);
                        bt_hotwater.setBackgroundResource(R.drawable.running);

                    }
                    if (modbusSlave.SterilizationMonitoringPoint == 1) {
                        bt_xiaodu.setBackgroundResource(R.drawable.running);
                    } else {
                        bt_xiaodu.setBackgroundResource(R.drawable.init_ing);
                    }
                    if (modbusSlave.PaiFengJiStartMonitoringPoint == 1) {
                        bt_paifeng.setBackgroundResource(R.drawable.running);
                    } else {
                        bt_paifeng.setBackgroundResource(R.drawable.init_ing);
                    }
                    if (modbusSlave.FengJiStartMonitoringPoint == 1) {
                        bt_fengji.setBackgroundResource(R.drawable.running);

                    } else {
                        bt_fengji.setBackgroundResource(R.drawable.init_ing);
                    }

                    if (modbusSlave.ZhiBanStartMonitoringPoint == 1) {
                        bt_zhiban.setBackgroundResource(R.drawable.running);
                    } else {
                        bt_zhiban.setBackgroundResource(R.drawable.init_ing);
                    }
                }
            });

        }
    };


    public void loginback(View v) {
        finish();
    }

    public void ButUintSet(View view) {

        intent.setClass(this,UintSet.class);
        startActivity(intent);

    }
}