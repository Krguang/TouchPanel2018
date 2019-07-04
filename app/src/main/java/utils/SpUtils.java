package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private static SharedPreferences sp;

    public static void putInt(Context ctx, String key, int value){

        if (sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).apply();
    }

    public static int getInt(Context ctx, String key, int defValue){
        if (sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key,defValue);
    }

    public static void putFloat(Context ctx, String key, float value){

        if (sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putFloat(key,value).apply();
    }

    public static float getFloat(Context ctx, String key, float defValue){
        if (sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getFloat(key,defValue);
    }
}
