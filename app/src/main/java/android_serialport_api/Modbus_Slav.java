package android_serialport_api;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 串口UTRA5
 *
 * @author Administrator
 */
public class Modbus_Slav extends Thread {

    private OutputStream mOutputStream;
    private InputStream mInputStream;


    private ArrayList<Byte> rxTemp = new ArrayList<Byte>();
    private Timer timer10ms=new Timer();

    private final String uartPath = "/dev/ttyS2";

    private int[] regHodingBuf = new int[1024];

    public boolean allowWriteShiDuSet = true;
    public boolean allowWriteWenDuSet = true;

    public int slaveAdd = 1;
    public int yaChaLiangCheng; //压差量程 0，1，2 分别对应 0-50Pa,0-100Pa,-50-+50Pa;
    public int xieYiLeiXing;    //协议类型：0，1分别对用内部协议，外部协议

    public int wenDuSet = 230;
    public int shiDuSet = 500;
    public int wenDu = 230;
    public int shiDu = 500;

    public int ColdWaterValveOpening;
    public int HotWaterValveOpening;
    public int HumidifieOpening;

    public int jiZuStartStop;
    public int zhiBanStartStop;
    public int fuYaStartStop;

    public int fengJiZhuangTai;
    public int zhiBanZhuangTai;
    public int fuYaZhuangtai;
    public int fengJiGuZhang;
    public int GaoXiao;

    public int HeartBeatMonitoringPoint;       //上位机心跳监控点
    public int HandAutomaticallyMonitoringPoint;//上位机手自动监控点
    public int FengjiZHuangTaiMonitoringPoint;//上位机风机状态监控点
    public int ZhongXiaoMonitoringPoint;//上位机中效监控点
    public int GaoXiaoMonitoringPoint;//上位机高效报警监控点
    public int ChuXiaoMonitoringPoint;//上位机初效报警监控点
    public int ElectricWarmOneMonitoringPoint;//上位机电加热1监控点
    public int ElectricWarmTwoMonitoringPoint;//上位机电加热2监控点
    public int ElectricWarmThreeMonitoringPoint;//上位机电加热3监控点
    public int ElectricWarmHighTemperatureMonitoringPoint;//上位机电加热高温监控点
    public int FengJiQueFengMonitoringPoint;//上位机风机缺风监控点
    public int SterilizationMonitoringPoint;//上位机灭菌监控点
    public int FengJiStartMonitoringPoint;//上位机风机已启动监控点
    public int PaiFengJiStartMonitoringPoint;//上位机排风机已启动监控点
    public int ZhiBanStartMonitoringPoint;//上位机值班已启动监控点

    public int WinterInSummer;//冬夏季

    // Timer timer10ms=new Timer();

    private SerialPort mserialPort = null;

    private final static Modbus_Slav instance = new Modbus_Slav();

    private Modbus_Slav(){
        try {
            try {
                mserialPort = getSerialPort();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (InvalidParameterException e) {

            e.printStackTrace();
        } catch (SecurityException ignored) {

        }
        mInputStream = mserialPort.getInputStream();
        mOutputStream = mserialPort.getOutputStream();
    }

    public static Modbus_Slav getInstance(){
        return instance;
    }


    public void closePort() throws IOException {
        mInputStream.close();
        mOutputStream.close();
    }

    /**
     * 数据等待接收
     */


    public void run() {
        super.run();
        timer10ms.schedule(taskPoll,10,10);//5ms后开始，每5ms轮询一次
        while (!isInterrupted()) {

            int size;
            try {
                byte[] reBuf = new byte[128];
                if (mInputStream == null) return;
                size = mInputStream.read(reBuf);

                if (size > 0) {
                    for (int i =0;i<size;i++){
                        rxTemp.add((reBuf[i]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mInputStream.close();
                    mInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     *判断接收空闲，总线空闲时置位rxFlag
     */
    private TimerTask taskPoll=new TimerTask() {
        int txDataLengthTemp=0;
        int txIdleCount=0;
        public void run() {

            if(rxTemp.size()>0){
                if(txDataLengthTemp!=rxTemp.size()){
                    txDataLengthTemp=rxTemp.size();
                    txIdleCount=0;
                }
                if(txIdleCount<4){
                    txIdleCount++;
                    if (txIdleCount>=4){
                        txIdleCount=0;
                        try{

                            byte[] rxTempByteArray = new byte[rxTemp.size()+255];
                            int i=0;
                            Iterator<Byte> iterator = rxTemp.iterator();
                            while (iterator.hasNext()) {

                                if (i < rxTemp.size()+255){
                                    rxTempByteArray[i] = iterator.next();
                                    i++;
                                }
                            }

                            onDataReceived(rxTempByteArray,rxTemp.size());
                            //  Log.d(TAG, "run: "+Arrays.toString(rxTempByteArray));
                            rxTemp.clear();

                        }catch (Exception e){
                            rxTemp.clear();
                            e.printStackTrace();
                        }
                    }
                }
            }
            else {
                txDataLengthTemp=0;
            }
        }
    };

    /**
     * @return mesrialPort  串口
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mserialPort == null) {

            int baudrate = 19200;
            mserialPort = new SerialPort(new File(uartPath), baudrate, 0);
        }
        return mserialPort;
    }


    /***
     *
     * @param reBuf
     * @param size
     * 数据接收处理
     */

    private void onDataReceived(byte[] reBuf, int size) {

        if (!(slaveAdd == reBuf[0])) {
            return;
        }

        if (size <= 3)
            return;
        if (CRC_16.checkBuf(reBuf)) {
            switch (reBuf[1]) {
                case 0x03:
                    mod_Fun_03_Slav(reBuf);
                    break;
                //case 0x06:	    mod_Fun_06_Slav(reBuf,size);	break;
                case 0x10:
                    mod_Fun_16_Slav(reBuf, size);
                    break;
                default:
                    break;
            }
        }
    }

    /***
     * 发送数据
     * @param seBuf
     */
    public void onDataSend(byte[] seBuf, int size) {
        try {
            mOutputStream.write(seBuf, 0, size);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /***
     * slave   功能码16
     * @param reBuf
     * @param size
     */

    private void mod_Fun_16_Slav(byte[] reBuf, int size) {

        int addr, len;
        int val;
        byte[] seBuf = new byte[1024];
        CRC_16 crc = new CRC_16();
        addr = (crc.getUnsignedByte(reBuf[2])) << 8;
        addr |= crc.getUnsignedByte(reBuf[3]);
        len = (crc.getUnsignedByte(reBuf[4])) << 8;
        len |= crc.getUnsignedByte(reBuf[5]);

        for (int i = 0; i < len; i++) {
            val = (int) ((crc.getUnsignedByte(reBuf[7 + 2 * i])) << 8);
            val |= crc.getUnsignedByte(reBuf[8 + 2 * i]);

            /***
             * 取起始地址开始的数据
             */
            regHodingBuf[addr + i] = val;
        }

        for (int i = 0; i < 6; i++) {
            seBuf[i] = reBuf[i];
        }

        crc.update(seBuf, 6);
        int value = crc.getValue();
        seBuf[6] = (byte) crc.getUnsignedByte((byte) ((value >> 8) & 0xff));
        seBuf[7] = (byte) crc.getUnsignedByte((byte) (value & 0xff));

        slav_hand_10();
        onDataSend(seBuf, 8);
    }

    /***
     * slave   功能码16处理函数
     */
    private void slav_hand_10() {



        if (xieYiLeiXing == 0){         //内部协议

            wenDu = regHodingBuf[7];
            shiDu =  regHodingBuf[8];

            fengJiZhuangTai =  regHodingBuf[9];
            zhiBanZhuangTai= regHodingBuf[10]&0x01;
            fuYaZhuangtai = (regHodingBuf[10] & 0x02) >> 1;                      //上位机负压启动监控点
            fengJiGuZhang = regHodingBuf[11];
            GaoXiao =  regHodingBuf[12];

        }else if (xieYiLeiXing == 1){   //外部协议

            wenDu = regHodingBuf[8];
            shiDu =  regHodingBuf[9];

            fengJiZhuangTai =   regHodingBuf[7]&0x01;
            zhiBanZhuangTai=    (regHodingBuf[7]>>1)&0x01;
            fuYaZhuangtai =     (regHodingBuf[7]>>2)&0x01;                      //上位机负压启动监控点
            fengJiGuZhang =     (regHodingBuf[7]>>3)&0x01;
            GaoXiao =           (regHodingBuf[7]>>4)&0x01;
        }

        if (allowWriteWenDuSet) {
            wenDuSet = regHodingBuf[5];
        }
        if (allowWriteShiDuSet) {
            shiDuSet =  regHodingBuf[6];
        }

        ColdWaterValveOpening =  regHodingBuf[13];//冷水阀开度
        HotWaterValveOpening = regHodingBuf[14];//热水阀开度
        HumidifieOpening =  regHodingBuf[15];  //加湿器开度1

        HeartBeatMonitoringPoint =  (regHodingBuf[17] & 0x0100) >> 8;                    //上位机心跳监控点
        HandAutomaticallyMonitoringPoint =  (regHodingBuf[17] & 0x0200) >> 9;            //上位机手自动监控点
        FengjiZHuangTaiMonitoringPoint =  (regHodingBuf[17] & 0x0400) >> 10;             //上位机风机状态监控点
        ZhongXiaoMonitoringPoint =  (regHodingBuf[17] & 0x0800) >> 11;                     //上位机中效报警监控点
        GaoXiaoMonitoringPoint =  (regHodingBuf[17] & 0x1000) >> 12;                     //上位机高效报警监控点
        ChuXiaoMonitoringPoint =  (regHodingBuf[17] & 0x2000) >> 13;                     //上位机初效报警监控点
        ElectricWarmOneMonitoringPoint =  (regHodingBuf[17] & 0x4000) >> 14;             //上位机电加热1监控点
        ElectricWarmTwoMonitoringPoint =  (regHodingBuf[17] & 0x8000) >> 15;             //上位机电加热2监控点

        ElectricWarmThreeMonitoringPoint =  (regHodingBuf[17] & 0x01);                     //上位机电加热3监控点
        ElectricWarmHighTemperatureMonitoringPoint =  (regHodingBuf[17] & 0x02) >> 1;    //上位机电加热高温监控点
        FengJiQueFengMonitoringPoint =  (regHodingBuf[17] & 0x04) >> 2;                  //上位机风机缺风监控点
        SterilizationMonitoringPoint =  (regHodingBuf[17] & 0x08) >> 3;                  //上位机灭菌监控点
        FengJiStartMonitoringPoint =  (regHodingBuf[17] & 0x10) >> 4;                    //上位机风机已启动监控点
        PaiFengJiStartMonitoringPoint = (regHodingBuf[17] & 0x20) >> 5;                 //上位机排风机已启动监控点
        ZhiBanStartMonitoringPoint = (regHodingBuf[17] & 0x40) >> 6;                    //上位机值班已启动监控点

        WinterInSummer = (regHodingBuf[20] & 0x04) >> 2;

    }


    /***
     * slave  功能码03
     * @param reBuf
     */
    private void mod_Fun_03_Slav(byte[] reBuf) {
        slav_int_03();
        int addr;
        int len;
        CRC_16 crc = new CRC_16();
        byte[] seBuf = new byte[1024];
        addr = (crc.getUnsignedByte(reBuf[2])) << 8;
        addr |= crc.getUnsignedByte(reBuf[3]);
        len = (crc.getUnsignedByte(reBuf[4])) << 8;
        len |= crc.getUnsignedByte(reBuf[5]);

        if (len + addr > 64)
            return;
        else {
            seBuf[0] = (byte) reBuf[0];
            seBuf[1] = (byte) reBuf[1];
            seBuf[2] = (byte) (2 * len);

            for (int i = 0; i < len; i++) {
                seBuf[3 + 2 * i] = (byte) (crc.getUnsignedIntt(regHodingBuf[i + addr]) >> 8);
                seBuf[4 + 2 * i] = (byte) (crc.getUnsignedIntt(regHodingBuf[i + addr]));

            }

            crc.update(seBuf, 2 * len + 3);
            int value = crc.getValue();

            seBuf[3 + 2 * len] = (byte) crc.getUnsignedByte((byte) ((value >> 8) & 0xff));
            seBuf[4 + 2 * len] = (byte) crc.getUnsignedByte((byte) (value & 0xff));
        }

        onDataSend(seBuf, 4 + 2 * len + 1);
    }


    /***
     * slave  功能码03初始化
     */

    private void slav_int_03() {


        if (xieYiLeiXing == 0){

            regHodingBuf[0] = jiZuStartStop;
            regHodingBuf[1] = zhiBanStartStop;
            regHodingBuf[2] = Modbus_Slav1.pressFromLocal;//预留
            regHodingBuf[3] = fuYaStartStop;
            regHodingBuf[4] = 0;//预留
        }else if (xieYiLeiXing == 1){

            if (1 == jiZuStartStop){
                regHodingBuf[4] |= 0x01;
            }else {
                regHodingBuf[4] &= ~0x01;
            }

            if (1 == zhiBanStartStop){
                regHodingBuf[4] |= (0x01<<1);
            }else {
                regHodingBuf[4] &= ~(0x01<<1);
            }

            if (1 == fuYaStartStop){
                regHodingBuf[4] |= (0x01<<2);
            }else {
                regHodingBuf[4] &= ~(0x01<<2);
            }
        }

        regHodingBuf[5] = wenDuSet;
        regHodingBuf[6] = shiDuSet;
    }
}