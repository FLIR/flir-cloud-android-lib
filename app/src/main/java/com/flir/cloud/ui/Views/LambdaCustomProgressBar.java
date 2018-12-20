package com.flir.cloud.ui.Views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.f2prateek.progressbutton.ProgressButton;
import com.flir.cloud.R;

/**
 * Created by Moti on 01-Jun-17.
 */

public class LambdaCustomProgressBar extends LinearLayout {

    private Context mContext;
    private ProgressButton pb;

    public LambdaCustomProgressBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LambdaCustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public LambdaCustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        this.setBackgroundColor(ContextCompat.getColor(mContext, R.color.flir_color_primary));
        setGravity(Gravity.CENTER);
        pb = new ProgressButton(mContext, null, android.R.attr.progressBarStyleLarge);
        pb.setMax(100);
        LayoutParams layoutparams = new LayoutParams(300, 300);
        pb.setCircleColor(ContextCompat.getColor(mContext, R.color.lambda_custom_progress_bar_circle_un_progress_color));
        pb.setProgressColor(ContextCompat.getColor(mContext, R.color.lambda_custom_progress_bar_circle_progress_color));

        pb.setShadowDrawable(ContextCompat.getDrawable(mContext, R.drawable.lambda_icon));
        pb.setPinnedDrawable(ContextCompat.getDrawable(mContext, R.drawable.lambda_icon));
        pb.setUnpinnedDrawable(ContextCompat.getDrawable(mContext, R.drawable.lambda_icon));

        addView(pb, layoutparams);
        startCustomProgressBarAnimation();
    }

    private void startCustomProgressBarAnimation() {

        ObjectAnimator anim = ObjectAnimator.ofInt(pb, "progress", 0, 100);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        anim.setRepeatCount(900);
        anim.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}
