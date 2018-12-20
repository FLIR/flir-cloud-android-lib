package com.flir.cloud.DialogManagerFiles;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessPresenter;
import com.flir.cloud.ui.Account.AccountPresenter;
import com.flir.cloud.ui.Device.DevicePresenter;

import java.util.Set;

/**
 * Created by Moti on 18-May-17.
 */

public class DialogManager {

    private MaterialDialog materialDialogProgress;
    private MaterialDialog materialDialogShowList;

    private Context context;



    public DialogManager(Context context) {
        this.context = context;
    }

    public void showDialogProgress(String title, String content) {
        materialDialogProgress = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .progress(true, 0)
                .show();
    }

    public void showDialogList(Set<String> aDeviceListNames, Button selectDevice, String title) {
        if(aDeviceListNames != null) {
            materialDialogShowList = new MaterialDialog.Builder(context)
                    .title(title)
                    .items(aDeviceListNames)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            selectDevice.setText(text);
                        }
                    })
                    .show();
        }
    }

    public void showDialogListWithPresenter(Set<String> aDeviceListNames, Button selectDevice, IPresenter presenter, PresenterOptions postGetSingleResource) {
        if(aDeviceListNames != null) {
            materialDialogShowList = new MaterialDialog.Builder(context)
                    .title(R.string.title_dialog_device_chooser_un_share)
                    .items(aDeviceListNames)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            selectDevice.setText(text);
                            switch (postGetSingleResource) {
                                case POST_GET_DEVICE:
                                    ((AccessPresenter) presenter).postGetSingleResource(text.toString());
                                    break;
                                case GET_ALL_SHARED_DEVICES:
                                    ((AccessPresenter) presenter).getAllSharedResources(text.toString());
                                    break;
                                case DEVICE_GET_DEVICE_STATE:
                                    ((DevicePresenter) presenter).getDeviceState(text.toString());
                                    break;
                                case ACCOUNT_GET_ACCOUNT_TYPE:
                                    ((AccountPresenter) presenter).getAccountByType(text.toString());
                                    break;

                                default:
                            }
                        }
                    })
                    .show();
        }
    }


    public MaterialDialog getMaterialDialogProgress() {
        return materialDialogProgress;
    }

    public MaterialDialog getMaterialDialogShowList() {
        return materialDialogShowList;
    }

    public void dismissProgressDialog() {
        if(materialDialogProgress != null && materialDialogProgress.isShowing()){
            materialDialogProgress.dismiss();
        }
    }
}
