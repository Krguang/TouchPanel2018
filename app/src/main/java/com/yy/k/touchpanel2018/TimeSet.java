package com.yy.k.touchpanel2018;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by K on 2017/11/6.
 */

public class TimeSet extends Activity {

    private DatePicker dpToday;
    private TimePicker tpNow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_set);

        dpToday=(DatePicker)super.findViewById(R.id.today);
        tpNow=(TimePicker)super.findViewById(R.id.now);
        tpNow.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        tpNow.setCurrentHour(calendar.get(calendar.HOUR_OF_DAY));
        dpToday.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        tpNow.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

    }

    public void ButTimeSave(View view) throws IOException, InterruptedException {
        int iYear=0;
        int iMonth=0;
        int iDay=0;
        String sDate="";
        iYear=dpToday.getYear();
        iMonth=dpToday.getMonth()+1;
        iDay=dpToday.getDayOfMonth();

        int iHour=0;
        int iMin=0;
        String sTime="";
        iHour=tpNow.getCurrentHour();
        iMin=tpNow.getCurrentMinute();
        SystemDateTime.setDateTime(iYear,iMonth,iDay,iHour,iMin);
        Toast.makeText(getApplicationContext(), "修改成功",Toast.LENGTH_LONG).show();
         finish();
    }

    public void ButTimeExit(View view) {
        finish();
    }
}
