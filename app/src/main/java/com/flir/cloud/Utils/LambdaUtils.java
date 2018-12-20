package com.flir.cloud.Utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;


import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles.CarouselSettingsDialogView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Moti on 08-Jun-17.
 */

public class LambdaUtils {

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean stopService(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ComponentName getService(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return service.service;
            }
        }
        return null;
    }

    public static void doChangeHeightAnimOnView(final View v, int duration, int from, int to) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = val;
            v.setLayoutParams(layoutParams);
        });

        anim.setDuration(duration);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(to == 0){
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        ;
    }

    public static void doCahngeWidthAnimOnView(final View v, int duration, int from, int to) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.width = val;
            v.setLayoutParams(layoutParams);
        });
        anim.setDuration(duration);
        anim.start();
    }


    public static String convertTextToTimestamp(String timeToJump, String currentTime) {
        if (timeToJump.length() == 5) {
            timeToJump = "0:" + timeToJump;
        }
        // 0:00:00 format
        if (timeToJump.length() == 7) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            Date d = null;
            try {
                d = df.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.HOUR, Integer.valueOf(timeToJump.substring(0, 1)));
            cal.add(Calendar.MINUTE, Integer.valueOf(timeToJump.substring(2, 4)));
            cal.add(Calendar.SECOND, Integer.valueOf(timeToJump.substring(5)));
            return df.format(cal.getTime());
        }

        return "";
    }

    public static long convertDateToTimestamp(String aDate) {
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date lFromDate1 = null;
        try {
            lFromDate1 = datetimeFormatter1.parse(aDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp fromTS1 = new Timestamp(lFromDate1.getTime());
        return fromTS1.getTime();
    }

    public static String fromTimestampToISO(long aTimestamp) {

        Timestamp timestamp = new Timestamp(aTimestamp);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(timestamp);
    }

    public static long fromISOtoTimestamp(String pikerText, String aTimezone) {

        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        //20170924T154021.000Z
        if (!aTimezone.equals(CarouselSettingsDialogView.DEVICE_DEFAULT_TIME_ZONE)) {
            formatter.setTimeZone(TimeZone.getTimeZone(aTimezone));
        }

        String dateFromPiker = pikerText.substring(0, 10);
        String timeFromPiker = pikerText.substring(11);
        String isoString = dateFromPiker + "T" + timeFromPiker;
        Date date = null;
        try {
            date = formatter.parse(isoString.substring(0, 19));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : -1;
    }

    public static Exception downloadFileFromServer(String aUrl, String filePath,  String aFileName) {
        int count;
        try {
            URL url = new URL(aUrl);
            URLConnection connexion = url.openConnection();
            connexion.connect();
            File folder = new File(filePath);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(filePath + File.separator + aFileName);
                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return null;
            }else {
                return new IOException();
            }

        } catch (Exception e) {
            return e;
        }
    }



    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };

    public static boolean checkExternalStoragePermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int readStoragePermissionState = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int writeStoragePermissionState = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        boolean externalStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!externalStoragePermissionGranted) {
            activity.requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_PERMISSION_CODE);
        }

        return externalStoragePermissionGranted;
    }


}
