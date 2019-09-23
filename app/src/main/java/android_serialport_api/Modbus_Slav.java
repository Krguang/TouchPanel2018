package android_serialport_api;

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

    private ArrayList<Byte> rxTemp = new ArrayList<Byte>();
    private Timer timer10ms=new Timer();

    private int[] regHodingBuf = new int[1024];

    public boolean allowWriteShiDuSet = true;
    public boolean allowWriteWenDuSet = true;

    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

    public int SLAV_addr = 1;
    public int yaChaLiangCheng;     //压差量程 0，1，2 分别对应 0-50Pa,0-100Pa,-50-+50Pa;
    public int xieYiLeiXing;        //协议类型：0，1分别对用内部协议，外部协议

    private int jiZuStartStop = 0;
    private int zhiBanStartStop = 0;
    private int fuYaStartStop = 0;
    public int wenDuSet = 250;
    public int shiDuSet = 500;
    public int yaChaSet = 500;
    public int wenDu = 250;
    public int shiDu = 500;
    public int yaCha = 500;
    private int fengJiZhuangTai = 0;
    private int zhiBanZhuangTai = 0;
    private int fuYaZhuangtai = 0;
    private int fengJiGuZhang = 0;
    private int GaoXiao;

    public int ColdWaterValveOpening = 0;//冷水阀
    public int HotWaterValveOpening = 0;//热水阀
    public int HumidifieOpening = 0;   //加湿器
    public int TheAirTemperature = 0;//新风温度

    private int upperComputerHeartBeatMonitoringPoint = 0;       //上位机心跳监控点
    private int upperComputerHandAutomaticallyMonitoringPoint = 0;//上位机手自动监控点
    private int upperComputerFengjiZHuangTaiMonitoringPoint;//上位机风机状态监控点
    private int upperComputerZhongXiaoMonitoringPoint;//上位机盘管低温监控点
    private int upperComputerGaoXiaoMonitoringPoint;//上位机高效报警监控点
    private int upperComputerChuXiaoMonitoringPoint;//上位机中效报警监控点
    private int upperComputerElectricWarmOneMonitoringPoint;//上位机电加热1监控点
    private int upperComputerElectricWarmTwoMonitoringPoint;//上位机电加热2监控点
    private int upperComputerElectricWarmThreeMonitoringPoint;//上位机电加热3监控点
    private int upperComputerElectricWarmHighTemperatureMonitoringPoint;//上位机电加热高温监控点
    private int upperComputerFengJiQueFengMonitoringPoint;//上位机风机缺风监控点
    private int upperComputerSterilizationMonitoringPoint;//上位机灭菌监控点
    private int upperComputerFengJiStartMonitoringPoint;//上位机风机已启动监控点
    private int upperComputerPaiFengJiStartMonitoringPoint;//上位机排风机已启动监控点
    private int upperComputerZhiBanStartMonitoringPoint;//上位机值班已启动监控点
    private int upperComputerFuYaStartMonitoringPoint;//上位机负压启动监控点
    private int upperComputerElectricPreheatOneMonitoringPoint;//上位机电预热1监控点
    private int upperComputerElectricPreheatTwoMonitoringPoint;//上位机电预热2监控点
    private int upperComputerElectricPreheatThreeMonitoringPoint;//上位机电预热3监控点
    private int upperComputerElectricPreheatHighTemperatureMonitoringPoint;//上位机电预热高温监控点
    private int upperComputerCompressorOneStartMonitoringPoint;//上位机压缩机1运行监控点
    private int upperComputerCompressorTwoStartMonitoringPoint;//上位机压缩机2运行监控点
    private int upperComputerCompressorThreeStartMonitoringPoint;//上位机压缩机3运行监控点
    private int upperComputerCompressorFourStartMonitoringPoint;//上位机压缩机4运行监控点
    private int upperComputerCompressorOneBreakdownMonitoringPoint;//上位机压缩机1故障监控点
    private int upperComputerCompressorTwoBreakdownMonitoringPoint;//上位机压缩机2故障监控点
    private int upperComputerCompressorThreeBreakdownMonitoringPoint;//上位机压缩机3故障监控点
    private int upperComputerCompressorFourBreakdownMonitoringPoint;//上位机压缩机4故障监控点
    private int WinterInSummer = 0;//冬夏季

   // Timer timer10ms=new Timer();

    private SerialPort mserialPort = null;

    private final static Modbus_Slav instance = new Modbus_Slav();

    private Modbus_Slav() {
        try {
            try {
                mserialPort = getSerialPort();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (InvalidParameterException | SecurityException e) {
            e.printStackTrace();
        }
        mInputStream = mserialPort.getInputStream();
        mOutputStream = mserialPort.getOutputStream();
    }


    public void closePort() throws IOException {
        mInputStream.close();
        mOutputStream.close();
    }

    public static Modbus_Slav getInstance(){
        return instance;
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
                            for (Byte aRxTemp : rxTemp) {

                                if (i < rxTemp.size() + 255) {
                                    rxTempByteArray[i] = aRxTemp;
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
    private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mserialPort == null) {

            String path = "/dev/ttyS2";
            int baudrate = 19200;
            if (path.length() == 0) {
                throw new InvalidParameterException();
            }

            mserialPort = new SerialPort(new File(path), baudrate, 0);

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

        if (!(SLAV_addr == reBuf[0])) {
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
    private void onDataSend(byte[] seBuf, int size) {
        try {
            mOutputStream.write(seBuf, 0, size);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /***
     * slave   功能码06
     * @param reBuf
     * @param size
     */
        /*private void mod_Fun_06_Slav(byte[] reBuf, int size) {


		}
		*/

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

        System.arraycopy(reBuf, 0, seBuf, 0, 6);

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
        TheAirTemperature = regHodingBuf[16]; //新风温度

        upperComputerHeartBeatMonitoringPoint =  (regHodingBuf[17] & 0x0100) >> 8;                    //上位机心跳监控点
        upperComputerHandAutomaticallyMonitoringPoint =  (regHodingBuf[17] & 0x0200) >> 9;            //上位机手自动监控点
        upperComputerFengjiZHuangTaiMonitoringPoint =  (regHodingBuf[17] & 0x0400) >> 10;             //上位机风机状态监控点
        upperComputerZhongXiaoMonitoringPoint =  (regHodingBuf[17] & 0x0800) >> 11;                     //上位机中效报警监控点
        upperComputerGaoXiaoMonitoringPoint =  (regHodingBuf[17] & 0x1000) >> 12;                     //上位机高效报警监控点
        upperComputerChuXiaoMonitoringPoint =  (regHodingBuf[17] & 0x2000) >> 13;                     //上位机初效报警监控点
        upperComputerElectricWarmOneMonitoringPoint =  (regHodingBuf[17] & 0x4000) >> 14;             //上位机电加热1监控点
        upperComputerElectricWarmTwoMonitoringPoint =  (regHodingBuf[17] & 0x8000) >> 15;             //上位机电加热2监控点

        upperComputerElectricWarmThreeMonitoringPoint =  (regHodingBuf[17] & 0x01);                     //上位机电加热3监控点
        upperComputerElectricWarmHighTemperatureMonitoringPoint =  (regHodingBuf[17] & 0x02) >> 1;    //上位机电加热高温监控点
        upperComputerFengJiQueFengMonitoringPoint =  (regHodingBuf[17] & 0x04) >> 2;                  //上位机风机缺风监控点
        upperComputerSterilizationMonitoringPoint =  (regHodingBuf[17] & 0x08) >> 3;                  //上位机灭菌监控点
        upperComputerFengJiStartMonitoringPoint =  (regHodingBuf[17] & 0x10) >> 4;                    //上位机风机已启动监控点
        upperComputerPaiFengJiStartMonitoringPoint = (regHodingBuf[17] & 0x20) >> 5;                 //上位机排风机已启动监控点
        upperComputerZhiBanStartMonitoringPoint = (regHodingBuf[17] & 0x40) >> 6;                    //上位机值班已启动监控点
      //  upperComputerFuYaStartMonitoringPoint = (regHodingBuf[17] & 0x80) >> 7;                      //上位机负压启动监控点  与 fuYaZhuangtai重复
/*
        upperComputerElectricPreheatOneMonitoringPoint = (int) ((regHodingBuf[18] & 0x10) >> 4);             //上位机电预热1监控点
        upperComputerElectricPreheatTwoMonitoringPoint = (int) ((regHodingBuf[18] & 0x20) >> 5);             //上位机电预热2监控点
        upperComputerElectricPreheatThreeMonitoringPoint = (int) ((regHodingBuf[18] & 0x40) >> 6);           //上位机电预热3监控点
        upperComputerElectricPreheatHighTemperatureMonitoringPoint = (int) ((regHodingBuf[18] & 0x80) >> 7); //上位机电预热高温监控点
        upperComputerCompressorOneStartMonitoringPoint = (int) ((regHodingBuf[18] & 0x0100) >> 8);           //上位机压缩机1运行监控点
        upperComputerCompressorTwoStartMonitoringPoint = (int) ((regHodingBuf[18] & 0x0200) >> 9);           //上位机压缩机2运行监控点
        upperComputerCompressorThreeStartMonitoringPoint = (int) ((regHodingBuf[18] & 0x0400) >> 10);        //上位机压缩机3运行监控点
        upperComputerCompressorFourStartMonitoringPoint = (int) ((regHodingBuf[18] & 0x0800) >> 11);         //上位机压缩机4运行监控点
        upperComputerCompressorOneBreakdownMonitoringPoint = (int) (regHodingBuf[18] & 0x01);                //上位机压缩机1故障监控点
        upperComputerCompressorTwoBreakdownMonitoringPoint = (int) ((regHodingBuf[18] & 0x02) >> 1);         //上位机压缩机2故障监控点
        upperComputerCompressorThreeBreakdownMonitoringPoint = (int) ((regHodingBuf[18] & 0x04) >> 2);       //上位机压缩机3故障监控点
        upperComputerCompressorFourBreakdownMonitoringPoint = (int) ((regHodingBuf[18] & 0x08) >> 3);        //上位机压缩机4故障监控点
        WinterInSummer = (int) ((regHodingBuf[20] & 0x04) >> 2);                                             //冬夏季监控控制点偏移2
    */
        WinterInSummer = (regHodingBuf[20] & 0x04) >> 2;
        yaCha = regHodingBuf[21];
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
            regHodingBuf[2] = 0;//预留
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


    public int getJiZuStartStop() {
        return jiZuStartStop;
    }


    public void setJiZuStartStop(int jiZuStartStop) {
        this.jiZuStartStop = jiZuStartStop;
    }


    public int getZhiBanStartStop() {
        return zhiBanStartStop;
    }


    public void setZhiBanStartStop(int zhiBanStartStop) {
        this.zhiBanStartStop = zhiBanStartStop;
    }


    public int getFuYaStartStop() {
        return fuYaStartStop;
    }


    public void setFuYaStartStop(int fuYaStartStop) {
        this.fuYaStartStop = fuYaStartStop;
    }


    public int getWenDuSet() {
        return wenDuSet;
    }


    public void setWenDuSet(int wenDuSet) {
        this.wenDuSet = wenDuSet;
    }


    public int getShiDuSet() {
        return shiDuSet;
    }

    public void setShiDuSet(int shiDuSet) {
        this.shiDuSet = shiDuSet;
    }


    public int getYaChaSet() {
        return yaChaSet;
    }


    public void setYaChaSet(int yaChaSet) {
        this.yaChaSet = yaChaSet;
    }


    public int getWenDu() {
        return wenDu;
    }


    public void setWenDu(int wenDu) {
        this.wenDu = wenDu;
    }


    public int getShiDu() {
        return shiDu;
    }


    public void setShiDu(int shiDu) {
        this.shiDu = shiDu;
    }


    public int getYaCha() {
        return yaCha;
    }


    public void setYaCha(int yaCha) {
        this.yaCha = yaCha;
    }


    public int getFengJiZhuangTai() {
        return fengJiZhuangTai;
    }


    public void setFengJiZhuangTai(int fengJiZhuangTai) {
        this.fengJiZhuangTai = fengJiZhuangTai;
    }


    public int getZhiBanZhuangTai() {
        return zhiBanZhuangTai;
    }


    public void setZhiBanZhuangTai(int zhiBanZhuangTai) {
        this.zhiBanZhuangTai = zhiBanZhuangTai;
    }


    public int getFengJiGuZhang() {
        return fengJiGuZhang;
    }


    public void setFengJiGuZhang(int fengJiGuZhang) {
        this.fengJiGuZhang = fengJiGuZhang;
    }


    public int getGaoXiao() {
        return GaoXiao;
    }


    public void setGaoXiao(int gaoXiao) {
        GaoXiao = gaoXiao;
    }


    public int getFuYaZhuangtai() {
        return fuYaZhuangtai;
    }


    public void setFuYaZhuangtai(int fuYaZhuangtai) {
        this.fuYaZhuangtai = fuYaZhuangtai;
    }


    public int getColdWaterValveOpening() {
        return ColdWaterValveOpening;
    }


    public void setColdWaterValveOpening(int coldWaterValveOpening) {
        ColdWaterValveOpening = coldWaterValveOpening;
    }


    public int getHotWaterValveOpening() {
        return HotWaterValveOpening;
    }


    public void setHotWaterValveOpening(int hotWaterValveOpening) {
        HotWaterValveOpening = hotWaterValveOpening;
    }


    public int getHumidifieOpening() {
        return HumidifieOpening;
    }


    public void setHumidifieOpening(int humidifieOpening) {
        HumidifieOpening = humidifieOpening;
    }


    public int getTheAirTemperature() {
        return TheAirTemperature;
    }


    public void setTheAirTemperature(int theAirTemperature) {
        TheAirTemperature = theAirTemperature;
    }


    public int getUpperComputerHeartBeatMonitoringPoint() {
        return upperComputerHeartBeatMonitoringPoint;
    }


    public void setUpperComputerHeartBeatMonitoringPoint(
            int upperComputerHeartBeatMonitoringPoint) {
        this.upperComputerHeartBeatMonitoringPoint = upperComputerHeartBeatMonitoringPoint;
    }


    public int getUpperComputerHandAutomaticallyMonitoringPoint() {
        return upperComputerHandAutomaticallyMonitoringPoint;
    }


    public void setUpperComputerHandAutomaticallyMonitoringPoint(
            int upperComputerHandAutomaticallyMonitoringPoint) {
        this.upperComputerHandAutomaticallyMonitoringPoint = upperComputerHandAutomaticallyMonitoringPoint;
    }


    public int getUpperComputerFengjiZHuangTaiMonitoringPoint() {
        return upperComputerFengjiZHuangTaiMonitoringPoint;
    }


    public void setUpperComputerFengjiZHuangTaiMonitoringPoint(
            int upperComputerFengjiZHuangTaiMonitoringPoint) {
        this.upperComputerFengjiZHuangTaiMonitoringPoint = upperComputerFengjiZHuangTaiMonitoringPoint;
    }


    public int getUpperComputerZhongXiaoMonitoringPoint() {
        return upperComputerZhongXiaoMonitoringPoint;
    }


    public void setUpperComputerZhongXiaoMonitoringPoint(
            int upperComputerZhongXiaoMonitoringPoint) {
        this.upperComputerZhongXiaoMonitoringPoint = upperComputerZhongXiaoMonitoringPoint;
    }


    public int getUpperComputerGaoXiaoMonitoringPoint() {
        return upperComputerGaoXiaoMonitoringPoint;
    }


    public void setUpperComputerGaoXiaoMonitoringPoint(
            int upperComputerGaoXiaoMonitoringPoint) {
        this.upperComputerGaoXiaoMonitoringPoint = upperComputerGaoXiaoMonitoringPoint;
    }


    public int getUpperComputerChuXiaoMonitoringPoint() {
        return upperComputerChuXiaoMonitoringPoint;
    }


    public void setUpperComputerChuXiaoMonitoringPoint(
            int upperComputerChuXiaoMonitoringPoint) {
        this.upperComputerChuXiaoMonitoringPoint = upperComputerChuXiaoMonitoringPoint;
    }


    public int getUpperComputerElectricWarmOneMonitoringPoint() {
        return upperComputerElectricWarmOneMonitoringPoint;
    }


    public void setUpperComputerElectricWarmOneMonitoringPoint(
            int upperComputerElectricWarmOneMonitoringPoint) {
        this.upperComputerElectricWarmOneMonitoringPoint = upperComputerElectricWarmOneMonitoringPoint;
    }


    public int getUpperComputerElectricWarmTwoMonitoringPoint() {
        return upperComputerElectricWarmTwoMonitoringPoint;
    }


    public void setUpperComputerElectricWarmTwoMonitoringPoint(
            int upperComputerElectricWarmTwoMonitoringPoint) {
        this.upperComputerElectricWarmTwoMonitoringPoint = upperComputerElectricWarmTwoMonitoringPoint;
    }


    public int getUpperComputerElectricWarmThreeMonitoringPoint() {
        return upperComputerElectricWarmThreeMonitoringPoint;
    }


    public void setUpperComputerElectricWarmThreeMonitoringPoint(
            int upperComputerElectricWarmThreeMonitoringPoint) {
        this.upperComputerElectricWarmThreeMonitoringPoint = upperComputerElectricWarmThreeMonitoringPoint;
    }


    public int getUpperComputerElectricWarmHighTemperatureMonitoringPoint() {
        return upperComputerElectricWarmHighTemperatureMonitoringPoint;
    }


    public void setUpperComputerElectricWarmHighTemperatureMonitoringPoint(
            int upperComputerElectricWarmHighTemperatureMonitoringPoint) {
        this.upperComputerElectricWarmHighTemperatureMonitoringPoint = upperComputerElectricWarmHighTemperatureMonitoringPoint;
    }


    public int getUpperComputerFengJiQueFengMonitoringPoint() {
        return upperComputerFengJiQueFengMonitoringPoint;
    }


    public void setUpperComputerFengJiQueFengMonitoringPoint(
            int upperComputerFengJiQueFengMonitoringPoint) {
        this.upperComputerFengJiQueFengMonitoringPoint = upperComputerFengJiQueFengMonitoringPoint;
    }


    public int getUpperComputerSterilizationMonitoringPoint() {
        return upperComputerSterilizationMonitoringPoint;
    }


    public void setUpperComputerSterilizationMonitoringPoint(
            int upperComputerSterilizationMonitoringPoint) {
        this.upperComputerSterilizationMonitoringPoint = upperComputerSterilizationMonitoringPoint;
    }


    public int getUpperComputerFengJiStartMonitoringPoint() {
        return upperComputerFengJiStartMonitoringPoint;
    }


    public void setUpperComputerFengJiStartMonitoringPoint(
            int upperComputerFengJiStartMonitoringPoint) {
        this.upperComputerFengJiStartMonitoringPoint = upperComputerFengJiStartMonitoringPoint;
    }


    public int getUpperComputerPaiFengJiStartMonitoringPoint() {
        return upperComputerPaiFengJiStartMonitoringPoint;
    }


    public void setUpperComputerPaiFengJiStartMonitoringPoint(
            int upperComputerPaiFengJiStartMonitoringPoint) {
        this.upperComputerPaiFengJiStartMonitoringPoint = upperComputerPaiFengJiStartMonitoringPoint;
    }


    public int getUpperComputerZhiBanStartMonitoringPoint() {
        return upperComputerZhiBanStartMonitoringPoint;
    }


    public void setUpperComputerZhiBanStartMonitoringPoint(
            int upperComputerZhiBanStartMonitoringPoint) {
        this.upperComputerZhiBanStartMonitoringPoint = upperComputerZhiBanStartMonitoringPoint;
    }


    public int getUpperComputerFuYaStartMonitoringPoint() {
        return upperComputerFuYaStartMonitoringPoint;
    }


    public void setUpperComputerFuYaStartMonitoringPoint(
            int upperComputerFuYaStartMonitoringPoint) {
        this.upperComputerFuYaStartMonitoringPoint = upperComputerFuYaStartMonitoringPoint;
    }


    public int getUpperComputerElectricPreheatOneMonitoringPoint() {
        return upperComputerElectricPreheatOneMonitoringPoint;
    }


    public void setUpperComputerElectricPreheatOneMonitoringPoint(
            int upperComputerElectricPreheatOneMonitoringPoint) {
        this.upperComputerElectricPreheatOneMonitoringPoint = upperComputerElectricPreheatOneMonitoringPoint;
    }


    public int getUpperComputerElectricPreheatTwoMonitoringPoint() {
        return upperComputerElectricPreheatTwoMonitoringPoint;
    }


    public void setUpperComputerElectricPreheatTwoMonitoringPoint(
            int upperComputerElectricPreheatTwoMonitoringPoint) {
        this.upperComputerElectricPreheatTwoMonitoringPoint = upperComputerElectricPreheatTwoMonitoringPoint;
    }


    public int getUpperComputerElectricPreheatThreeMonitoringPoint() {
        return upperComputerElectricPreheatThreeMonitoringPoint;
    }


    public void setUpperComputerElectricPreheatThreeMonitoringPoint(
            int upperComputerElectricPreheatThreeMonitoringPoint) {
        this.upperComputerElectricPreheatThreeMonitoringPoint = upperComputerElectricPreheatThreeMonitoringPoint;
    }


    public int getUpperComputerElectricPreheatHighTemperatureMonitoringPoint() {
        return upperComputerElectricPreheatHighTemperatureMonitoringPoint;
    }


    public void setUpperComputerElectricPreheatHighTemperatureMonitoringPoint(
            int upperComputerElectricPreheatHighTemperatureMonitoringPoint) {
        this.upperComputerElectricPreheatHighTemperatureMonitoringPoint = upperComputerElectricPreheatHighTemperatureMonitoringPoint;
    }


    public int getUpperComputerCompressorOneStartMonitoringPoint() {
        return upperComputerCompressorOneStartMonitoringPoint;
    }


    public void setUpperComputerCompressorOneStartMonitoringPoint(
            int upperComputerCompressorOneStartMonitoringPoint) {
        this.upperComputerCompressorOneStartMonitoringPoint = upperComputerCompressorOneStartMonitoringPoint;
    }


    public int getUpperComputerCompressorTwoStartMonitoringPoint() {
        return upperComputerCompressorTwoStartMonitoringPoint;
    }


    public void setUpperComputerCompressorTwoStartMonitoringPoint(
            int upperComputerCompressorTwoStartMonitoringPoint) {
        this.upperComputerCompressorTwoStartMonitoringPoint = upperComputerCompressorTwoStartMonitoringPoint;
    }


    public int getUpperComputerCompressorThreeStartMonitoringPoint() {
        return upperComputerCompressorThreeStartMonitoringPoint;
    }


    public void setUpperComputerCompressorThreeStartMonitoringPoint(
            int upperComputerCompressorThreeStartMonitoringPoint) {
        this.upperComputerCompressorThreeStartMonitoringPoint = upperComputerCompressorThreeStartMonitoringPoint;
    }


    public int getUpperComputerCompressorFourStartMonitoringPoint() {
        return upperComputerCompressorFourStartMonitoringPoint;
    }


    public void setUpperComputerCompressorFourStartMonitoringPoint(
            int upperComputerCompressorFourStartMonitoringPoint) {
        this.upperComputerCompressorFourStartMonitoringPoint = upperComputerCompressorFourStartMonitoringPoint;
    }


    public int getUpperComputerCompressorOneBreakdownMonitoringPoint() {
        return upperComputerCompressorOneBreakdownMonitoringPoint;
    }


    public void setUpperComputerCompressorOneBreakdownMonitoringPoint(
            int upperComputerCompressorOneBreakdownMonitoringPoint) {
        this.upperComputerCompressorOneBreakdownMonitoringPoint = upperComputerCompressorOneBreakdownMonitoringPoint;
    }


    public int getUpperComputerCompressorTwoBreakdownMonitoringPoint() {
        return upperComputerCompressorTwoBreakdownMonitoringPoint;
    }


    public void setUpperComputerCompressorTwoBreakdownMonitoringPoint(
            int upperComputerCompressorTwoBreakdownMonitoringPoint) {
        this.upperComputerCompressorTwoBreakdownMonitoringPoint = upperComputerCompressorTwoBreakdownMonitoringPoint;
    }


    public int getUpperComputerCompressorThreeBreakdownMonitoringPoint() {
        return upperComputerCompressorThreeBreakdownMonitoringPoint;
    }


    public void setUpperComputerCompressorThreeBreakdownMonitoringPoint(
            int upperComputerCompressorThreeBreakdownMonitoringPoint) {
        this.upperComputerCompressorThreeBreakdownMonitoringPoint = upperComputerCompressorThreeBreakdownMonitoringPoint;
    }


    public int getUpperComputerCompressorFourBreakdownMonitoringPoint() {
        return upperComputerCompressorFourBreakdownMonitoringPoint;
    }


    public void setUpperComputerCompressorFourBreakdownMonitoringPoint(
            int upperComputerCompressorFourBreakdownMonitoringPoint) {
        this.upperComputerCompressorFourBreakdownMonitoringPoint = upperComputerCompressorFourBreakdownMonitoringPoint;
    }


    public int getWinterInSummer() {
        return WinterInSummer;
    }


    public void setWinterInSummer(int winterInSummer) {
        WinterInSummer = winterInSummer;
    }


}