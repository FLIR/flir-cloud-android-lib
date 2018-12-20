package com.flir.cloud.ui.fileExplorer.VolumePageClasses.AddVolumeDialogView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.R;
import com.flir.cloud.ui.Views.AddTagsCustomView.AddTagsCustomView;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.VolumeMainPagePresenter;
import com.flir.sdk.models.Storage.VolumeDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Moti on 20-Jul-17.
 */

public class AddVolumeDialogView extends LinearLayout {

    private Context mContext;
    private Button addVolumeButton;
    private EditText mVolumeName;
    private JSONObject mTagsJsonObjectData;
    private TextView mDataTagsTextView;
    private ImageButton addVolumeDataButton;
    private VolumeMainPagePresenter volumeMainPagePresenterPresenter;
    private IAddVolumeDialog iAddVolumeDialog;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    private View dialogView;

    public AddVolumeDialogView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public AddVolumeDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public AddVolumeDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public AddVolumeDialogView(Context context, VolumeMainPagePresenter volumeMainPagePresenter, IAddVolumeDialog aIAddVolumeDialog) {
        super(context);
        mContext = context;
        iAddVolumeDialog = aIAddVolumeDialog;
        this.volumeMainPagePresenterPresenter = volumeMainPagePresenter;
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.file_explorer_add_volume_dialog, null);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(layoutParams);

        addVolumeButton = (Button) dialogView.findViewById(R.id.file_explorer_button_add_volume);
        addVolumeButton.setText(R.string.file_explorer_button_add_volume);
        addVolumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddVolumeButtonClickListener();
                iAddVolumeDialog.removeDialog();
            }
        });

        mDataTagsTextView = (TextView)dialogView.findViewById(R.id.add_device_json_details);
        mVolumeName = (EditText)dialogView.findViewById(R.id.file_explorer_input_name);
        addVolumeDataButton = (ImageButton) dialogView.findViewById(R.id.add_data_row_button);
        addVolumeDataButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataClicked();
            }
        });


        addView(dialogView);
    }

    private void addDataClicked() {

        AddTagsCustomView csv = new AddTagsCustomView(mContext, mTagsJsonObjectData);

        new MaterialDialog.Builder(mContext)
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

        mDataTagsTextView.setTextColor(ContextCompat.getColor(mContext, R.color.flir_account_page_text_color));
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

    private void setAddVolumeButtonClickListener() {
        VolumeDetails AddVolume = new VolumeDetails(mVolumeName.getText().toString());
        volumeMainPagePresenterPresenter.postAddVolumeResource(AddVolume);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_CREATED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, AddVolume.VolumeName);
    }

}
