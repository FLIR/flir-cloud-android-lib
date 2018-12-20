package com.flir.cloud.ui.Views.TimeLineCustomView;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView.TLView;

/**
 * Created by Moti on 12-Sep-17.
 */

public class GestureTap extends GestureDetector.SimpleOnGestureListener {

    private TLView mTLView;

    public GestureTap(TLView aTLView) {
        mTLView = aTLView;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mTLView.doOnSingleTapClicked(e.getX());
        return true;
    }
}