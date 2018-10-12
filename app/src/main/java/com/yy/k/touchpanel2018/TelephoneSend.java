package com.yy.k.touchpanel2018;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.CRC_16;
import android_serialport_api.SerialPort;

class TelephoneSend {

    private int[] regHodingBuf = new int[256];
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private SerialPort mserialPort = null;


    short phone_dial_0;
    short phone_dial_1;
    short phone_dial_2;
    short phone_dial_3;
    short phone_dial_4;
    short phone_dial_5;
    short phone_dial_6;
    short phone_dial_7;
    short phone_dial_8;
    short phone_dial_9;
    short phone_dial_miHao;
    short phone_dial_jingHao;
    short phone_dial_miantiJian;
    short duiJiangJian;

    TelephoneSend(){

        uartInit();

    }

    private void uartInit() {

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

    private void slav_hand_10() {

        regHodingBuf[0] = (phone_dial_0)|(phone_dial_1<<1)|(phone_dial_2<<2)|(phone_dial_3<<3)|(phone_dial_4<<4)|(phone_dial_5<<5)|(phone_dial_6<<6)|
                (phone_dial_7<<7)|(phone_dial_8<<8)|(phone_dial_9<<9)|(phone_dial_miHao<<10)|(phone_dial_jingHao<<11)|(phone_dial_miantiJian<<12)|(duiJiangJian<<13);
    }

    void sendDataMaster16() {
        int i,txCount;
        byte[] txBuf = new byte[256];
        CRC_16 crc = new CRC_16();

        slav_hand_10();

        byte m2ASlaveAdd = 1;
        txBuf[0] = m2ASlaveAdd;
        txBuf[1] = 0x10;
        txBuf[2] = 0x00;         //数据的起始地址；
        txBuf[3] = 0x00;
        txBuf[4] = 0x00;         //数据的个数；
        txBuf[5] = 0x01;
        txBuf[6] = 0x02;         //数据的字节数；
        for (i = 0; i<txBuf[6]/2; i++) {
            txBuf[7 + 2 * i] = (byte) (regHodingBuf[i+ txBuf[3]] >> 8);
            txBuf[8 + 2 * i] = (byte)(regHodingBuf[i+ txBuf[3]] & 0xff);
        }
        crc.update(txBuf, txBuf[6] + 7);
        int temp = crc.getValue();
        txBuf[7 + txBuf[6]] = (byte)((temp >> 8) & 0xff);
        txBuf[8 + txBuf[6]] = (byte)(temp & 0xff);
        txCount = 9 + txBuf[6];
        onDataSend(txBuf, txCount);
    }

    private SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mserialPort == null) {

            String path = "/dev/ttyS1";
            int baudrate = 9600;
            if (path.length() == 0) {
                throw new InvalidParameterException();
            }

            mserialPort = new SerialPort(new File(path), baudrate, 0);

        }
        return mserialPort;
    }
}
