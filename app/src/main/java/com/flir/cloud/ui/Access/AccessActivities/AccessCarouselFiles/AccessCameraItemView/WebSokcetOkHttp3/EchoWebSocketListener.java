package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.WebSokcetOkHttp3;

import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.Fmp4WebSocketClient;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.ffmpegConverterToFmp4;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private Fmp4WebSocketClient mFmp4WebSocketClient;

    public EchoWebSocketListener(String url) {
        mFmp4WebSocketClient = new Fmp4WebSocketClient(url, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        if(bytes.utf8().contains("READY")){
            do{
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (!ffmpegConverterToFmp4.isFinished);

            mFmp4WebSocketClient.writeAudioDataToFile();

        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    }
}
