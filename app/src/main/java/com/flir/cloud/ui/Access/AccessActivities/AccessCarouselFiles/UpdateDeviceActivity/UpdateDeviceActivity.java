package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.UpdateDeviceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.DialogManagerFiles.DialogManager;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles.CarouselSettingsDialogView;
import com.flir.cloud.ui.Views.AddTagsCustomView.AddTagsCustomView;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.AddDevice;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.Device.UpdateDevice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Moti on 16-May-17.
 */

public class UpdateDeviceActivity extends AppCompatActivity implements IUpdateDeviceView {

    UpdateDevicePresenter presenter;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    DialogManager dialogManager;
    private JSONObject mTagsJsonObjectData;
    private String deviceSerial;
    private String deviceName;
    private boolean isUpdateReq;

    @Inject
    DeviceInterceptor DeviceInterceptor;

    @BindView(R.id.tv_update_device_device_name)
    TextView deviceNameTestView;

    @BindView(R.id.add_device_json_details)
    TextView deviceJsonDetails;
    @BindView(R.id.acl_update_input_name)
    EditText name;
    @BindView(R.id.access_button_update_device)
    Button addDevice;
    @BindView(R.id.lambda_custom_progress_bar_acl)
    LambdaCustomProgressBar lambdaProgress;


    @BindView(R.id.add_data_row_button)
    ImageButton addDataRowButton;

    @OnClick(R.id.add_data_row_button)
    void addDataRowClicked(){
        doAddDataRowClicked();
    }


    @OnClick(R.id.access_button_update_device)
    public void updateResource() {
        JsonObject tags = getDataJson();
        UpdateDevice updateDevice  = new UpdateDevice(name.getText().toString(),tags);
        isUpdateReq = true;
        presenter.updateDevice(deviceSerial, updateDevice);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTING_UPDATE_DEVICE_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, deviceSerial);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_update_device);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        presenter = new UpdateDevicePresenter(DeviceInterceptor, this);
        Intent intent = getIntent();
        dialogManager = new DialogManager(this);
        deviceSerial = intent.getStringExtra(CarouselSettingsDialogView.DEVICE_DETAILS_SERIAL);
        deviceName = intent.getStringExtra(CarouselSettingsDialogView.DEVICE_DETAILS_NAME);
        //presenter.postGetSingleResource(deviceSerial);
        presenter.getDevices();

    }

    private void doAddDataRowClicked() {
        AddTagsCustomView csv = new AddTagsCustomView(this, mTagsJsonObjectData);

        new MaterialDialog.Builder(this)
                .customView(csv, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getDialogTagsData(dialog);
                    }
                })
                .positiveText(R.string.dialog_done)
                .show();
    }

    private void getDialogTagsData(MaterialDialog dialog) {
        LinearLayout tagDetailsLinearLayout = (LinearLayout) dialog.findViewById(R.id.ll_tag_details_view);
        JSONObject tagsJsonObject = new JSONObject();
        String tagName;
        String[] tagValues;

        for (int i = 0; i < tagDetailsLinearLayout.getChildCount(); i++) {
            tagName = ((TextView) tagDetailsLinearLayout.getChildAt(i).findViewById(R.id.add_tag_custom_view_tag_name)).getText().toString();
            tagValues = ((TextView) tagDetailsLinearLayout.getChildAt(i).findViewById(R.id.add_tag_custom_view_tag_values)).getText().toString().split(", ");
            try {
                tagsJsonObject.put(tagName, new JSONArray(Arrays.asList(tagValues)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mTagsJsonObjectData = tagsJsonObject;
        showJsonDataOnTextView();

    }

    private void showJsonDataOnTextView() {

        deviceJsonDetails.setTextColor(ContextCompat.getColor(this, R.color.flir_account_page_text_color));
        deviceJsonDetails.setText(getResources().getString(R.string.tag_custom_tags_view_title));

        Iterator<String> iter = mTagsJsonObjectData.keys();
        String data = "";
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = mTagsJsonObjectData.get(key);
                data += "\t\t" + key + ":" + "\n\t\t" + value + "\n\n";
            } catch (JSONException e) {
            }
        }

        String jsonData = data.replaceAll("[{\"}]", "");
        jsonData = jsonData.replace("[", "");
        jsonData = jsonData.replace("]", "");
        deviceJsonDetails.setText(deviceJsonDetails.getText() + "\n\n" + jsonData);
    }

    @Override
    public void showWait() {
        lambdaProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        lambdaProgress.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        addDevice.setBackgroundColor(ContextCompat.getColor(this,R.color.acl_device_failure_request));
    }

    @Override
    public void responseFromServer(AddDevice device) {
      //  finish();

    }

    @Override
    public void responseFromServer(List<DeviceDetails> deviceList) {
        ///API need to update - get one device each request.
        DeviceDetails currentDevice = getCurrentDevice(deviceList,deviceSerial);
        if(!isUpdateReq) {
            try {
                mTagsJsonObjectData = new JSONObject(currentDevice.tags.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            deviceNameTestView.setText(currentDevice.name);
            name.setText(currentDevice.name);

            showJsonDataOnTextView();
        }else{
            finish();
        }
    }

    @Override
    public void responseFromServer(DeviceDetails deviceDetails) {
        finish();
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

    private DeviceDetails getCurrentDevice(List<DeviceDetails> deviceList, String deviceSerial) {
        for (DeviceDetails deviceItemDetails : deviceList) {
            if(deviceItemDetails.id.equals(deviceSerial)){
                return deviceItemDetails;
            }
        }
        return null;
    }

    private JsonObject getDataJson() {
        if (mTagsJsonObjectData != null) {
            JsonParser jsonParser = new JsonParser();
            return (JsonObject) jsonParser.parse(mTagsJsonObjectData.toString());
        } else {
            return null;
        }
    }

}
