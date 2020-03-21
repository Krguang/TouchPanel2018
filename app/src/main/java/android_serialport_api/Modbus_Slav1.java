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

public class Modbus_Slav1 extends Thread {

    private ArrayList<Byte> rxTemp = new ArrayList<Byte>();
    private Timer timer10ms=new Timer();

    public int backMusic;
    public int BackMusic_upDown;

    public int yangQiChaoYa;
    public int yangQIQianYa;

    public int yaSuoKongQiChaoYa;
    public int yaSuoKongQiQianYa;

    public int xiaoQiChaoYa;
    public int xiaoQiQianYa;

    public int erYangHuaTanChaoYa;
    public int erYangHuaTanQianYa;

    public int fuYaXiYinChaoYa;
    public int fuYaXiYinQianYa;

    public int yaQiChaoYa;
    public int yaQiQianYa;

    public int danQiChaoYa;
    public int danQiQianYa;

    public int yangQiValue;
    public int erYangHuaTanValue;
    public int fuYaXiYinValue;
    public int yaSuoKongQiValue;


    public int Lightling_1 = 1;

    public int Lightling_2 = 1;

    /***
     * 无影灯
     */
    public int Shadowless_Lamp = 1;
    /***
     * 术中灯
     */
    public int Intraoperative_Lamp = 1;
    /***
     * 观片灯
     */
    public int OfLightThe_Lamp = 1;
    /***
     * 备用
     */
    public int Prepare = 1;
    /***
     * 消音
     */
    public int Erasure = 1;

    private int[] regHodingBuf = new int[1024];

    public int gasStatus;

    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private SerialPort mserialPort = null;

    public static int pressFromLocal;
    public volatile boolean stop = false;

    private final static Modbus_Slav1 instance = new Modbus_Slav1();

    private Modbus_Slav1() {


        try {
            mserialPort = getSerialPort();
        } catch (InvalidParameterException | SecurityException | IOException e) {

            e.printStackTrace();
        }
        mInputStream = mserialPort.getInputStream();
        mOutputStream = mserialPort.getOutputStream();

    }

    public static Modbus_Slav1 getInstance(){
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
        int txIdleCount=0;
        int txDataLengthTemp=0;
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
                            // while (iterator.hasNext()) {
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


    /***
     *
     * @return mserialPort_1
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException
     */


    private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mserialPort == null) {

            String path = "/dev/ttyS3";
            int baudrate = 19200;
            if (path.length() == 0) {
                throw new InvalidParameterException();
            }
            mserialPort = new SerialPort(new File(path), baudrate, 0);

        }
        return mserialPort;
    }


    private void onDataReceived(byte[] reBuf, int size) {

        int SLAV_addr = 1;
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
                case 0x06:

                case 0x10:
                    mod_Fun_16_Slav(reBuf, size);
                    break;
            }
        }

    }

    private void onDataSend(byte[] seBuf, int size) {
        try {
            mOutputStream = mserialPort.getOutputStream();
            mOutputStream.write(seBuf, 0, size);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    private void mod_Fun_16_Slav(byte[] reBuf, int size) {

        int addr, len;
        short val;
        byte[] seBuf = new byte[1024];
        CRC_16 crc = new CRC_16();

        addr = (crc.getUnsignedByte(reBuf[2])) << 8;
        addr |= crc.getUnsignedByte(reBuf[3]);
        len = (crc.getUnsignedByte(reBuf[4])) << 8;
        len |= crc.getUnsignedByte(reBuf[5]);
        for (int i = 0; i < len; i++) {
            val = (short) ((crc.getUnsignedByte(reBuf[7 + 2 * i])) << 8);
            val |= crc.getUnsignedByte(reBuf[8 + 2 * i]);
            regHodingBuf[addr + i] = val;
        }
        System.arraycopy(reBuf, 0, seBuf, 0, 6);
        crc.update(seBuf, 6);
        int value_1 = crc.getValue();
        seBuf[6] = (byte) crc.getUnsignedByte((byte) ((value_1 >> 8) & 0xff));
        seBuf[7] = (byte) crc.getUnsignedByte((byte) (value_1 & 0xff));

        slav_hand_10();
        onDataSend(seBuf, 8);

    }

    private void slav_hand_10() {

        yangQiChaoYa = (byte) ((regHodingBuf[3])&1);
        yangQIQianYa = (byte) ((regHodingBuf[3]>>1)&1);

        yaSuoKongQiChaoYa = (byte) ((regHodingBuf[3]>>2)&1);
        yaSuoKongQiQianYa = (byte) ((regHodingBuf[3]>>3)&1);

        xiaoQiChaoYa = (byte) ((regHodingBuf[3]>>4)&1);
        xiaoQiQianYa = (byte) ((regHodingBuf[3]>>5)&1);

        erYangHuaTanChaoYa = (byte) ((regHodingBuf[3]>>6)&1);
        erYangHuaTanQianYa = (byte) ((regHodingBuf[3]>>7)&1);

        fuYaXiYinChaoYa = (byte) ((regHodingBuf[3]>>8)&1);
        fuYaXiYinQianYa = (byte) ((regHodingBuf[3]>>9)&1);

        yaQiChaoYa = (byte) ((regHodingBuf[3]>>12)&1);
        yaQiQianYa = (byte) ((regHodingBuf[3]>>13)&1);

        danQiChaoYa = (byte) ((regHodingBuf[3]>>10)&1);
        danQiQianYa = (byte) ((regHodingBuf[3]>>11)&1);

        gasStatus=regHodingBuf[3];
        pressFromLocal = regHodingBuf[13];

        yangQiValue = (int)(regHodingBuf[6]*1.1-99);
        if (yangQiValue<0) yangQiValue=0;

        erYangHuaTanValue = (int)(regHodingBuf[9]*1.1-99);
        if (erYangHuaTanValue<0) erYangHuaTanValue=0;

        fuYaXiYinValue = (int)(regHodingBuf[10]*1.1-99);
        if (fuYaXiYinValue>0) fuYaXiYinValue=0;

        yaSuoKongQiValue = (int)(regHodingBuf[7]*1.1-99);
        if (yaSuoKongQiValue<0) yaSuoKongQiValue=0;
    }

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


    private void slav_int_03() {

       // regHodingBuf[0] = BackMusic_upDown;

        switch (BackMusic_upDown){
            case 0:
                regHodingBuf[0] = 0;
                break;
            case 1:
                regHodingBuf[0] = 1;
                break;
            case 2:
                regHodingBuf[0] = 3;
                break;
            case 3:
                regHodingBuf[0] = 6;
                break;
        }
        regHodingBuf[1] = (Prepare) | (Intraoperative_Lamp << 1) | (Lightling_2 << 2) | (OfLightThe_Lamp << 3) | (Shadowless_Lamp << 4) | (Lightling_1 << 5) | (Erasure << 6);
    }
}