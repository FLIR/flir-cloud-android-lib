package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.flir.cloud.MainApplication;

import java.io.File;
import java.io.IOException;


public class AudioRecorder {

   // public static final String RECORD_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoStream/";
    public static final String RECORD_DIR_PATH = (MainApplication.getAppContext()).getCacheDir() + "/videoStream/";
    public static final String RECORD_FILE_NAME_IN = "inRecordLong.mp4";
    public static final String FULL_RECORD_FILE_PATH_NAME_IN = RECORD_DIR_PATH + RECORD_FILE_NAME_IN;

    public static final String RECORD_FILE_NAME_OUT = "outRecordLong.mp4";
    public static final String FULL_RECORD_FILE_PATH_NAME_OUT = RECORD_DIR_PATH + RECORD_FILE_NAME_OUT;
    MediaRecorder recorder = new MediaRecorder();

    private Context mContext;

    public AudioRecorder(Context aContext) {
        mContext = aContext;
    }



    public void startRecording() {
        File dir = new File(RECORD_DIR_PATH);

        if(!dir.exists()){
            dir.mkdir();
        }


        String status = Environment.getExternalStorageState();
        if(status.equals("mounted")) {

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(441000);
            recorder.setAudioEncodingBitRate(192000);

            recorder.setOutputFile(FULL_RECORD_FILE_PATH_NAME_IN);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}
