package com.yy.k.touchpanel2018;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.CRC_16;
import android_serialport_api.SerialPort;

public  class TelephoneSend {

    int[] regHodingBuf = new int[256];
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    private final String uartPath = "/dev/ttyS1";
    private SerialPort mserialPort = null;
    private byte M2ASlaveAdd = 1;


    public short phone_dial_0;
    public short phone_dial_1;
    public short phone_dial_2;
    public short phone_dial_3;
    public short phone_dial_4;
    public short phone_dial_5;
    public short phone_dial_6;
    public short phone_dial_7;
    public short phone_dial_8;
    public short phone_dial_9;
    public short phone_dial_miHao;
    public short phone_dial_jingHao;
    public short phone_dial_miantiJian;
    public short duiJiangJian;

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
        } catch (InvalidParameterException e) {

            e.printStackTrace();
        } catch (SecurityException e) {

        }
        mInputStream = mserialPort.getInputStream();
        mOutputStream = mserialPort.getOutputStream();

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

    private void slav_hand_10() {

        regHodingBuf[0] = (phone_dial_0<<0)|(phone_dial_1<<1)|(phone_dial_2<<2)|(phone_dial_3<<3)|(phone_dial_4<<4)|(phone_dial_5<<5)|(phone_dial_6<<6)|
                (phone_dial_7<<7)|(phone_dial_8<<8)|(phone_dial_9<<9)|(phone_dial_miHao<<10)|(phone_dial_jingHao<<11)|(phone_dial_miantiJian<<12)|(duiJiangJian<<13);
/*
        if (1 == phone_dial_0){

            regHodingBuf[0] |= 1;
        }else {

            regHodingBuf[0] &= ~1;
        }

        if (1 == phone_dial_1){

            regHodingBuf[0] |= (1<<1);
        }else {

            regHodingBuf[0] &= ~(1<<1);
        }

        if (1 == phone_dial_2){

            regHodingBuf[0] |= (1<<2);
        }else {

            regHodingBuf[0] &= ~(1<<2);
        }

        if (1 == phone_dial_3){

            regHodingBuf[0] |= (1<<3);
        }else {

            regHodingBuf[0] &= ~(1<<3);
        }

        if (1 == phone_dial_4){

            regHodingBuf[0] |= (1<<4);
        }else {

            regHodingBuf[0] &= ~(1<<4);
        }

        if (1 == phone_dial_5){

            regHodingBuf[0] |= (1<<5);
        }else {

            regHodingBuf[0] &= ~(1<<5);
        }

        if (1 == phone_dial_6){

            regHodingBuf[0] |= (1<<6);
        }else {

            regHodingBuf[0] &= ~(1<<6);
        }

        if (1 == phone_dial_7){

            regHodingBuf[0] |= (1<<7);
        }else {

            regHodingBuf[0] &= ~(1<<7);
        }

        if (1 == phone_dial_8){

            regHodingBuf[0] |= (1<<8);
        }else {

            regHodingBuf[0] &= ~(1<<8);
        }

        if (1 == phone_dial_9){

            regHodingBuf[0] |= (1<<9);
        }else {

            regHodingBuf[0] &= ~(1<<9);
        }

        if (1 == phone_dial_miHao){

            regHodingBuf[0] |= (1<<10);
        }else {

            regHodingBuf[0] &= ~(1<<10);
        }

        if (1 == phone_dial_jingHao){

            regHodingBuf[0] |= (1<<11);
        }else {

            regHodingBuf[0] &= ~(1<<11);
        }

        if (1 == phone_dial_miantiJian){

            regHodingBuf[0] |= (1<<12);
        }else {

            regHodingBuf[0] &= ~(1<<12);
        }

        if (1 == duiJiangJian){

            regHodingBuf[0] |= (1<<13);
        }else {

            regHodingBuf[0] &= ~(1<<13);
        }
        */
    }

    public void sendDataMaster16() {
        int i,txCount;
        byte[] txBuf = new byte[256];
        CRC_16 crc = new CRC_16();

        slav_hand_10();

        txBuf[0] = M2ASlaveAdd;
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

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mserialPort == null) {

            String path = uartPath;
            int baudrate = 9600;
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            mserialPort = new SerialPort(new File(path), baudrate, 0);

        }
        return mserialPort;
    }
}
