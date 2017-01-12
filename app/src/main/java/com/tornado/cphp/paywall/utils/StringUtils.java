package com.tornado.cphp.paywall.utils;


import android.content.Context;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Toast;

public class StringUtils {

    public static String strBaseURL = "http://paywall.mlm4india.com/paywall/api.php";
    public static final String UPLOAD_URL="http://paywall.mlm4india.com/paywall/image_upload.php";


    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isNotBlank(String str) {
        if (str == null || str.trim().equalsIgnoreCase(""))
            return false;
        return true;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().equalsIgnoreCase(""))
            return true;
        return false;
    }

    public static String getReverseString(String msg) {
        if (msg.equalsIgnoreCase("0")) {
            return "1";
        } else if (msg.equalsIgnoreCase("1")) {
            return "0";
        } else if (msg.equalsIgnoreCase("")) {
            return "1";
        } else {
            return "";
        }
    }

    public static String getColoredString(String text, int color) {
        String str = "";
        str = "<font color='" + color + "'>" + text + "</font>";
        return str;
    }

    public static void showToast(String strMessage, Context context) {

        Toast toast=Toast.makeText(context,strMessage,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }


}
