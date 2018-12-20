package com.flir.cloud.ui.fileExplorer.VolumePageClasses.UpdateVolumeDialogView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flir.cloud.R;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.AddVolumeDialogView.IAddVolumeDialog;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.VolumeMainPagePresenter;
import com.flir.sdk.models.Storage.UpdateVolume;

/**
 * Created by Moti on 20-Jul-17.
 */

public class UpdateVolumeDialogView extends LinearLayout {

    private Context mContext;
    private Button updateVolumeButton;
    private VolumeMainPagePresenter volumeMainPagePresenterPresenter;
    private IAddVolumeDialog iAddVolumeDialog;
    private View dialogView;
    private String serial;

    public UpdateVolumeDialogView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public UpdateVolumeDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public UpdateVolumeDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public UpdateVolumeDialogView(Context context, String serial ,VolumeMainPagePresenter volumeMainPagePresenter, IAddVolumeDialog aIAddVolumeDialog) {
        super(context);
        mContext = context;
        iAddVolumeDialog = aIAddVolumeDialog;
        this.serial = serial;
        this.volumeMainPagePresenterPresenter = volumeMainPagePresenter;
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.file_explorer_add_volume_dialog, null);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(layoutParams);

        updateVolumeButton = (Button) dialogView.findViewById(R.id.file_explorer_button_add_volume);
        updateVolumeButton.setText(R.string.file_explorer_update_volume_item);
        updateVolumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdateVolumeButtonClickListener();
                iAddVolumeDialog.removeDialog();
            }
        });


        addView(dialogView);
    }

    private void setUpdateVolumeButtonClickListener() {
        EditText name = (EditText)dialogView.findViewById(R.id.file_explorer_input_name);
        UpdateVolume updateVolume = new UpdateVolume();
        updateVolume.name = name.getText().toString();
        volumeMainPagePresenterPresenter.putUpdateVolume(serial, updateVolume);

        this.removeAllViews();
    }
}
