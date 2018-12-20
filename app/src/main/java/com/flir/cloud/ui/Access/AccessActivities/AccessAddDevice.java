package com.flir.cloud.ui.Access.AccessActivities;

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
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.ui.Access.AccessPresenter;
import com.flir.cloud.ui.Access.AccessView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselItemFragment;
import com.flir.cloud.ui.Views.AddTagsCustomView.AddTagsCustomView;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.models.Device.DeviceDetails;
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

public class AccessAddDevice extends AppCompatActivity implements AccessView {

    AccessPresenter presenter;
    JSONObject mTagsJsonObjectData;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @Inject
    AccessInterceptor accessInterceptor;

    @Inject
    DeviceInterceptor deviceInterceptor;

    @BindView(R.id.access_input_serial)
    EditText serial;
    @BindView(R.id.access_input_name)
    EditText name;
    @BindView(R.id.access_button_add_device)
    Button addDevice;

    @BindView(R.id.lambda_custom_progress_bar_acl)
    LambdaCustomProgressBar mLambdaCustomProgressBar;

    @BindView(R.id.add_device_json_details)
    TextView mDataTagsTextView;

    @BindView(R.id.add_data_row_button)
    ImageButton addDataRowButton;

    @OnClick(R.id.add_data_row_button)
    public void addDataRow() {

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

        mDataTagsTextView.setTextColor(ContextCompat.getColor(this, R.color.flir_account_page_text_color));
        mDataTagsTextView.setText(getResources().getString(R.string.tag_custom_tags_view_title));

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
        mDataTagsTextView.setText(mDataTagsTextView.getText() + "\n\n" + jsonData);
    }

    @OnClick(R.id.access_button_add_device)
    public void addResource() {
        JsonObject tags = getDataJson();
        AddResource addDevice = new AddResource(serial.getText().toString(), LambdaConstant.RESOURCE_TYPE_DEVICE, name.getText().toString(), tags);
        presenter.postAddResource(addDevice);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_BUTTON_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, addDevice.id + "/" + addDevice.name);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_add_device);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        presenter = new AccessPresenter(accessInterceptor, deviceInterceptor, this);
    }

    @Override
    public void showWait() {
        mLambdaCustomProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        mLambdaCustomProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        addDevice.setBackgroundColor(ContextCompat.getColor(this, R.color.acl_device_failure_request));
    }

    @Override
    public void responseFromServer() {

    }

    @Override
    public void responseFromServer(ResourceResponse response) {
        LambdaSharedPreferenceManager.getInstance().addDeviceToListDevices(response);
        setResult(CarouselItemFragment.ADD_DEVICE_RESULT_CODE);
        finish();
    }

    @Override
    public void responseFromServer(List<DeviceDetails> response) {

    }

    @Override
    public void responseListFromServer(List<SharedUsersResponse> response) {

    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
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
