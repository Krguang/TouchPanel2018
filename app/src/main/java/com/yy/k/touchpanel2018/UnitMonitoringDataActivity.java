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
    Button bt_dianjiare;
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
    SharedPreferences sharedPreferences;

    private double huiFengWenDuDouble;
    private double huiFengShiDuDoubLe;
    private double sheDingWenDuDouble;
    private double sheDingShiDuDouble;

    private double humiDouble;
    private double coldWaterDouble;
    private double hotWaterDouble;


    Timer timer1 = new Timer();
    TimerTask task1;
    Modbus_Slav modbusSlave;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_monitoringdata);


        modbusSlave = Modbus_Slav.getInstance();


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
        bt_dianjiare = (Button) findViewById(R.id.bt_dianjiare_id);
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
        sharedPreferences = getSharedPreferences("ljq", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    //    Modbus_Slav.stop_UnitActivity = true;
        if (timer1!=null){
            if (task1!=null){
                task1.cancel();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
  //      Modbus_Slav.stop_UnitActivity = false;

        if (timer1!=null){
            if (task1!=null){
                task1.cancel();
            }
        }
        task1=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      // UI thread

                    public void run() {

//                        short huiFengWenDu = (short) sharedPreferences.getInt("回风温度",0);
//                        short huiFengShiDu = (short) sharedPreferences.getInt("回风湿度",0);
//                        short sheDingWenDu = (short) sharedPreferences.getInt("设定温度",0);
//                        short sheDingShiDu = (short) sharedPreferences.getInt("设定湿度",0);
//
//                        short ColdWaterValveOpening = (short) sharedPreferences.getInt("冷水阀", 0);
//                        short HotWaterValveOpening = (short) sharedPreferences.getInt("热水阀", 0);
//                        short HumidifieOpening = (short) sharedPreferences.getInt("加湿器", 0);


                        huiFengWenDuDouble = modbusSlave.wenDu / 10.0;
                        huiFengShiDuDoubLe = modbusSlave.shiDu /10.0;
                        sheDingWenDuDouble = modbusSlave.wenDuSet / 10.0;
                        sheDingShiDuDouble = modbusSlave.shiDuSet /10.0;

                        humiDouble = modbusSlave.HumidifieOpening/10.0;
                        coldWaterDouble = modbusSlave.ColdWaterValveOpening/10.0;
                        hotWaterDouble = modbusSlave.HotWaterValveOpening/10.0;


                        short UpperComputerHandAutomaticallyMonitoringPoint = (short) sharedPreferences.getInt("上位机手自动监控点", 0);
                        short UpperComputerFengjiZHuangTaiMonitoringPoint = (short) sharedPreferences.getInt("上位机风机状态监控点", 0);
                        short upperComputerZhongXiaoMonitoringPoint = (short) sharedPreferences.getInt("上位机盘管低温监控点", 0);

                        short UpperComputerChuXiaoMonitoringPoint = (short) sharedPreferences.getInt("上位机中效报警监控点", 0);
                        short UpperComputerElectricWarmOneMonitoringPoint = (short) sharedPreferences.getInt("上位机电加热1监控点", 0);
                        short UpperComputerElectricWarmTwoMonitoringPoint = (short) sharedPreferences.getInt("上位机电加热2监控点", 0);
                        short UpperComputerElectricWarmThreeMonitoringPoint = (short) sharedPreferences.getInt("上位机电加热3监控点", 0);
                        short UpperComputerElectricWarmHighTemperatureMonitoringPoint = (short) sharedPreferences.getInt("上位机电加热高温监控点", 0);
                        short UpperComputerFengJiQueFengMonitoringPoint = (short) sharedPreferences.getInt("上位机风机缺风监控点", 0);
                        short UpperComputerSterilizationMonitoringPoint = (short) sharedPreferences.getInt("上位机灭菌监控点", 0);
                        short UpperComputerFengJiStartMonitoringPoint = (short) sharedPreferences.getInt("上位机风机已启动监控点", 0);
                        short UpperComputerPaiFengJiStartMonitoringPoint = (short) sharedPreferences.getInt("上位机排风机已启动监控点", 0);
                        short UpperComputerZhiBanStartMonitoringPoint = (short) sharedPreferences.getInt("上位机值班已启动监控点", 0);

                        short WinrerInSummer = (short) sharedPreferences.getInt("冬夏季", 0);


//                        huiFengWenDuDouble = Modbus_Slav.wenDu / 10.0;
//                        huiFengShiDuDoubLe = Modbus_Slav.shiDu /10.0;
//                        sheDingWenDuDouble = Modbus_Slav.wenDuSet / 10.0;
//                        sheDingShiDuDouble = Modbus_Slav.shiDuSet /10.0;
//
//                        humiDouble = Modbus_Slav.HumidifieOpening/10.0;
//                        coldWaterDouble = Modbus_Slav.ColdWaterValveOpening/10.0;
//                        hotWaterDouble = Modbus_Slav.HotWaterValveOpening/10.0;

//                        huiFengWenDuDouble = huiFengWenDu / 10.0;
//                        huiFengShiDuDoubLe = huiFengShiDu /10.0;
//                        sheDingWenDuDouble = sheDingWenDu / 10.0;
//                        sheDingShiDuDouble = sheDingShiDu /10.0;
//
//                        humiDouble = HumidifieOpening/10.0;
//                        coldWaterDouble = ColdWaterValveOpening/10.0;
//                        hotWaterDouble = HotWaterValveOpening/10.0;


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
                        tv_coldWateropening.setText(coldWaterString+"%");
                        tv_hotWateropening.setText(hotWaterString+"%");



                        if (UpperComputerPaiFengJiStartMonitoringPoint == 1){

                            if (paifengJi_temp == 0){
                                paifengJi_temp = 1;
                                bt_paiFengJiLun.setBackgroundResource(R.drawable.exhaust_air_1);
                            }else {
                                paifengJi_temp = 0;
                                bt_paiFengJiLun.setBackgroundResource(R.drawable.exhaust_air_2);
                            }

                        }

                        if (UpperComputerFengjiZHuangTaiMonitoringPoint==1) {

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

                        if (UpperComputerChuXiaoMonitoringPoint == 1) {
                            bt_chuXiaoWaring.setBackgroundResource(R.drawable.waring);
                        } else {
                            bt_chuXiaoWaring.setBackgroundResource(R.drawable.init_ing);
                        }


                        if (UpperComputerFengJiQueFengMonitoringPoint == 1) {
                            bt_fengjiquefengwaring.setBackgroundResource(R.drawable.waring);
                        } else {
                            bt_fengjiquefengwaring.setBackgroundResource(R.drawable.init_ing);
                        }

                        if (upperComputerZhongXiaoMonitoringPoint == 1) {
                            bt_zhongxiaowaring.setBackgroundResource(R.drawable.waring);
                        } else {
                            bt_zhongxiaowaring.setBackgroundResource(R.drawable.init_ing);
                        }


                        if (UpperComputerElectricWarmOneMonitoringPoint == 1) {
                            bt_dianjiareone.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiareone.setBackgroundResource(R.drawable.init_ing);
                        }

                        if (UpperComputerElectricWarmTwoMonitoringPoint == 1) {
                            bt_dianjiaretwo.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiaretwo.setBackgroundResource(R.drawable.init_ing);
                        }
                        if (UpperComputerElectricWarmThreeMonitoringPoint == 1) {
                            bt_dianjiarethree.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_dianjiarethree.setBackgroundResource(R.drawable.init_ing);
                        }


                        if (UpperComputerElectricWarmHighTemperatureMonitoringPoint == 0) {
                            bt_gaowenwaring.setBackgroundResource(R.drawable.waring);
                        } else {
                            bt_gaowenwaring.setBackgroundResource(R.drawable.init_ing);
                        }


//                        String HumidifieOpeningbai = "0" + HumidifieOpening / 100;
//                        String HumidifieOpeningshi = "0" + HumidifieOpening / 10 % 10;
//                        String HumidifieOpeningge = "0" + HumidifieOpening % 10;
//
//                        tv_humidifieOpening.setText(HumidifieOpeningbai.substring(HumidifieOpeningbai.length() - 1, HumidifieOpeningbai.length()) + HumidifieOpeningshi.substring(HumidifieOpeningshi.length() - 1, HumidifieOpeningshi.length()) + "." + HumidifieOpeningge.substring(HumidifieOpeningge.length() - 1, HumidifieOpeningge.length()) + "%");

                        if (UpperComputerHandAutomaticallyMonitoringPoint == 1) {
                            bt_bendicontrol.setBackgroundResource(R.drawable.init_ing);
                            bt_yuanchengcontrol.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_bendicontrol.setBackgroundResource(R.drawable.running);
                            bt_yuanchengcontrol.setBackgroundResource(R.drawable.init_ing);
                        }

//                        String bai = "0" + ColdWaterValveOpening / 100;
//                        String shi = "0" + ColdWaterValveOpening / 10 % 10;
//                        String ge = "0" + ColdWaterValveOpening % 10;
//
//                        tv_coldWateropening.setText(bai.substring(bai.length() - 1, bai.length()) + shi.substring(shi.length() - 1, shi.length()) + "." + ge.substring(ge.length() - 1, ge.length()) + "%");
//
//                        bai = "0" + HotWaterValveOpening / 100;
//                        shi = "0" + HotWaterValveOpening / 10 % 10;
//                        ge = "0" + HotWaterValveOpening % 10;
//
//                        tv_hotWateropening.setText(bai.substring(bai.length() - 1, bai.length()) + shi.substring(shi.length() - 1, shi.length()) + "." + ge.substring(ge.length() - 1, ge.length()) + "%");


                        if (WinrerInSummer == 1) {

                            bt_coldwater.setBackgroundResource(R.drawable.running);
                            bt_hotwater.setBackgroundResource(R.drawable.init_ing);


                        } else {
                            bt_coldwater.setBackgroundResource(R.drawable.init_ing);
                            bt_hotwater.setBackgroundResource(R.drawable.running);

                        }
                        if (UpperComputerSterilizationMonitoringPoint == 1) {
                            bt_xiaodu.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_xiaodu.setBackgroundResource(R.drawable.init_ing);
                        }
                        if (UpperComputerPaiFengJiStartMonitoringPoint == 1) {
                            bt_paifeng.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_paifeng.setBackgroundResource(R.drawable.init_ing);
                        }
                        if (UpperComputerFengJiStartMonitoringPoint == 1) {
                            bt_fengji.setBackgroundResource(R.drawable.running);

                        } else {
                            bt_fengji.setBackgroundResource(R.drawable.init_ing);
                        }

                        if (UpperComputerZhiBanStartMonitoringPoint == 1) {
                            bt_zhiban.setBackgroundResource(R.drawable.running);
                        } else {
                            bt_zhiban.setBackgroundResource(R.drawable.init_ing);
                        }

                    }
                });
            }
        };
        timer1.schedule(task1, 1000, 1000);
    }

    public void loginback(View v) {
        finish();
    }

    public void ButUintSet(View view) {

        intent.setClass(this, UintSet.class);
        startActivity(intent);

    }
}