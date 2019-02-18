package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;

import android.util.Log;

import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.WebSokcetOkHttp3.EchoWebSocketListener;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

public class Fmp4WebSocketClient {

    private WebSocket ws;
    public static int counter;
    private OkHttpClient client;

    public Fmp4WebSocketClient(String url, EchoWebSocketListener aEchoWebSocketListener) {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        ws = client.newWebSocket(request, aEchoWebSocketListener);
        client.dispatcher().executorService().shutdown();
    }

    public void writeAudioDataToFile() {

        String filePath = AudioRecorder.FULL_RECORD_FILE_PATH_NAME_OUT;

        File file = new File(filePath);


        int nextStartPosition = 0;
        try {

            byte[] fileBytes = convertFileToByteArray(file);
            counter = 0;
            int nextBoxSize = -1;
            String nextBoxType = "";
            byte[] fragmentedMp4 = new byte[0];
            while (nextStartPosition + (nextBoxSize) < fileBytes.length) {
                nextBoxSize = getNextBoxSize(fileBytes, nextStartPosition);
                nextBoxType = getNextBoxType(fileBytes, nextStartPosition);
                fragmentedMp4 = getCopyOfRange(fileBytes, nextStartPosition, nextStartPosition + nextBoxSize);
                nextStartPosition = nextStartPosition + nextBoxSize;
                if(!nextBoxType.equals("mfra")) {
                    ws.send(ByteString.of(fragmentedMp4));
                }
            }

            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MNMNKLG" , "mWebSocket.send IOException = " + e.getMessage());
        }catch (Exception e){
            Log.d("MNMNKLG" , "mWebSocket.send Exception = " + e.getMessage());
        }
    }

    public static int getNextBoxSize(byte[] bytes, int offset) {

        byte [] bb = new byte[4];
        bb[0] = bytes[offset];
        bb[1] = bytes[offset +1];
        bb[2] = bytes[offset +2];
        bb[3] = bytes[offset +3];
        return ByteBuffer.wrap(bb).getInt();

    }


    public static String getNextBoxType(byte[] bytes, int offset) {

        byte [] bb = new byte[4];

        bb[0] = bytes[offset + 4];
        bb[1] = bytes[offset + 5];
        bb[2] = bytes[offset + 6];
        bb[3] = bytes[offset + 7];

        return new String(bb, StandardCharsets.UTF_8);

    }

    public byte[] convertFileToByteArray(File file) throws IOException {

        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }

    public byte[] getCopyOfRange(byte[] byteArr, int from, int to){
        return Arrays.copyOfRange(byteArr, from, to);
    }
}
