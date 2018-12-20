package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselShareDialogFiles;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.models.Access.ShareResource;
import com.flir.sdk.models.Access.SharedUsersResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Moti on 05-Jun-17.
 */

public class CarouselShareDialogView extends LinearLayout implements CarouselShareView, AdapterView.OnItemSelectedListener{

    @Inject
    AccessInterceptor mAccessInterceptor;

    private Context mContext;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    private LambdaCustomProgressBar lambdaCustomProgressBar;
    private Spinner spinnerSharedDevice;
    private String deviceSerial;
    private ArrayList<String> spinnerSharedEmailsList;
    private CarouselSharePresenter mCarouselSharePresenter;
    private ArrayAdapter<String> mSpinnerSharedArrayAdapter;

    private TextView mUnShareTextView;
    private ImageButton mUnShareButton;
    private ImageButton mShareButton;
    private EditText mShareDeviceEditText;
    private String mUnShareDeviceSelectedEmail;

    public CarouselShareDialogView(Context context, String serial) {
        super(context);
        mContext = context;
        ((MainApplication)mContext.getApplicationContext()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
        deviceSerial = serial;
        mCarouselSharePresenter = new CarouselSharePresenter(mAccessInterceptor,this);
        initView();
    }

    public CarouselShareDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CarouselShareDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void initView() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.devices_carousel_share_view, null);
        lambdaCustomProgressBar = (LambdaCustomProgressBar) dialogView.findViewById(R.id.lambda_custom_progress_bar_settings_dialog);
        spinnerSharedDevice = (Spinner) dialogView.findViewById(R.id.spinner_un_share_chooser);
        mShareButton = (ImageButton)dialogView.findViewById(R.id.btn_share_device_dialog);
        mShareDeviceEditText = (EditText)dialogView.findViewById(R.id.et_share_device_dialog);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareDevice();
            }
        });

        mUnShareTextView = (TextView) dialogView.findViewById(R.id.tv_un_share_device);
        mUnShareButton = (ImageButton)dialogView.findViewById(R.id.btn_un_share_device_dialog);
        mUnShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doUnShareDevice();
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(layoutParams);
        addView(dialogView);

        mCarouselSharePresenter.postGetAllSharedDevices(deviceSerial);


    }

    private void doShareDevice() {
        if(!mShareDeviceEditText.getText().toString().isEmpty()){
            ShareResource sr = new ShareResource(deviceSerial, mShareDeviceEditText.getText().toString());
            mCarouselSharePresenter.postSharedDevice(sr);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SHARE_DEVICE_CLICKED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, sr.id + "/" + sr.email);
        }
    }


    private void initSpinnerValues(List<SharedUsersResponse> response) {
        spinnerSharedEmailsList = new ArrayList<>();
        int currentPosition = 0;
        for (SharedUsersResponse res : response) {
            if(res.acl.length == 1){
                spinnerSharedEmailsList.add(currentPosition++,res.email);
            }
        }

        initSpinnerAdapter();

    }

    private void initSpinnerAdapter(){
        mSpinnerSharedArrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item, spinnerSharedEmailsList);
        mSpinnerSharedArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSharedDevice.setAdapter(mSpinnerSharedArrayAdapter);
        spinnerSharedDevice.setOnItemSelectedListener(this);
    }


    public void setUnShareViewVisibility(int aVisibility){
        spinnerSharedDevice.setVisibility(aVisibility);
        mUnShareTextView.setVisibility(aVisibility);
        mUnShareButton.setVisibility(aVisibility);
    }


    @Override
    public void showWait() {
        lambdaCustomProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        lambdaCustomProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {

    }

    @Override
    public void responseFromServer() {
        invalidate();
    }

    @Override
    public void responseFromServer(List<SharedUsersResponse> res) {
        if(res.size() == 1){
            setUnShareViewVisibility(View.GONE);
        }
        initSpinnerValues(res);
    }

    @Override
    public void responseFromServer(String email, int isShareOrUnShare) {
        if(isShareOrUnShare == CarouselSharePresenter.SHARE_DEVICE_SUCCESS_CODE){
            addElement(email);
        }else{
            removeElement(email);
        }
        mSpinnerSharedArrayAdapter.notifyDataSetChanged();

    }

    public void removeElement(String aMail) {
        int mailPosition = getMailPosition(aMail);
        if(mailPosition != -1) {
            spinnerSharedEmailsList.remove(getMailPosition(aMail));
        }
        if(spinnerSharedEmailsList.size() == 0){
            setUnShareViewVisibility(View.GONE);
        }
    }

    private int getMailPosition(String aMail) {
        for (int i = 0; i <spinnerSharedEmailsList.size(); i++) {
            if(spinnerSharedEmailsList.get(i).equals(aMail)){
                return i;
            }
        }
        return -1;
    }

    public void addElement(String aMail) {
        if (spinnerSharedEmailsList != null) {
            spinnerSharedEmailsList.add(aMail);
        }
        if(mUnShareButton.getVisibility() == View.GONE){
            setUnShareViewVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinnerSharedEmailsList != null){
            mUnShareDeviceSelectedEmail = spinnerSharedEmailsList.get(position);
        }
    }

    private void doUnShareDevice() {
        mUnShareDeviceSelectedEmail = spinnerSharedDevice.getSelectedItem().toString();
        if(mUnShareDeviceSelectedEmail != null) {
            ShareResource sr = new ShareResource(deviceSerial, mUnShareDeviceSelectedEmail);
            mCarouselSharePresenter.postUnSharedDevices(sr);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_UN_SHARE_DEVICE_CLICKED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, sr.id + "/" + sr.email);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

}
