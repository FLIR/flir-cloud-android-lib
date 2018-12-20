package com.flir.cloud.ui.fileExplorer.VolumePageClasses;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.AddVolumeDialogView.AddVolumeDialogView;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.AddVolumeDialogView.IAddVolumeDialog;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.FileExplorerRecyclerViewfiles.FileExplorerRecyclerViewAdapter;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.VolumeResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolumeMainPageActivity extends Activity implements VolumeMainPageView,IAddVolumeDialog {


    public static final int MAX_DISPLAY_SPAN_COUNT = 8;
    public static final int MIN_DISPLAY_SPAN_COUNT = 2;


    VolumeMainPagePresenter volumeMainPagePresenter;
    List<VolumeItemObject> allItems;
    FileExplorerRecyclerViewAdapter rcAdapter;
    private MaterialDialog.Builder addVolumeDialog;
    private MaterialDialog dismissObject;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @Inject
    StorageInterceptor accessInterceptor;

    @BindView(R.id.lambda_custom_progress_bar_volume_main_page)
    LambdaCustomProgressBar progressBar;

    private GridLayoutManager lLayout;

    @BindView(R.id.ll_tool_bar_container)
    LinearLayout mLlToolBarContainer;

    @BindView(R.id.file_explorer_main_grid_view)
    RecyclerView fileExplorerMainGridRecyclerView;

    @BindView(R.id.ib_cloud_storage_display_option)
    ImageButton mDisplayOptionButton;

    @OnClick(R.id.ib_cloud_storage_display_option)
    void DisplayOptionButtonClicked(){
        doDisplayOptionClicked();
    }

    @OnClick(R.id.btn_tool_bar_add_volume)
    void addVolumeClicked(){
        showAddFolderDialog();
    }

    public VolumeMainPagePresenter getVolumeMainPagePresenter() {
        return volumeMainPagePresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer_main_page);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        ButterKnife.bind(this);
        if(LambdaUtils.checkExternalStoragePermission(this)) {
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        if (LambdaUtils.checkExternalStoragePermission(this)) {
            volumeMainPagePresenter = new VolumeMainPagePresenter(accessInterceptor, this);
            getVolumeGridList();
            initDialog();
        }
    }

    private void initGridItems(List<VolumeResponse> response) {

        LambdaSharedPreferenceManager.getInstance().saveVolumesList(response);

        initItemList(response);
        lLayout = new GridLayoutManager(this, LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, 4));
        fileExplorerMainGridRecyclerView.setLayoutManager(lLayout);
   }

    private void getVolumeGridList() {
        volumeMainPagePresenter.postGetAllResources();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void responseFromServer(List<VolumeResponse> response) {
        initGridItems(response);
    }

    @Override
    public void addVolumePosition(VolumeResponse response) {

        LambdaSharedPreferenceManager.getInstance().addVolumeToVolumeList(response);

        VolumeItemObject volumeItemObject = new VolumeItemObject(response);
        volumeItemObject.setPhoto(R.drawable.ic_empty_folder_icon);
        allItems.add(allItems.size() - 1, volumeItemObject);

        rcAdapter.notifyDataSetChanged();
    }

    private int getItemPositionVolume(String serial) {
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getId().equals(serial)) {
                return i;
            }
        }
        return -1;
    }


    private void initItemList(List<VolumeResponse> response ) {

        allItems = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            allItems.add(new VolumeItemObject(response.get(i)));

        }

        addNewFolderItemToList();

        if(rcAdapter == null) {
            rcAdapter = new FileExplorerRecyclerViewAdapter(this, allItems, mLlToolBarContainer);
            fileExplorerMainGridRecyclerView.setAdapter(rcAdapter);
            rcAdapter.notifyDataSetChanged();
        }

        for (VolumeResponse volumeResponse : response) {
            volumeMainPagePresenter.getVolumeFiles(volumeResponse.VolumeId, 100);
        }
    }

    @Override
    public void responseFromServer(VolumeFilesResponse response) {
        int position = getItemPositionVolume(response.volume);
        if(position != -1) {
            allItems.get(position).updateFilesDetails(response);

            if (response.files.length > 0) {
                allItems.get(position).setPhoto(R.drawable.ic_not_empty_folder_icon);
            } else {
                allItems.get(position).setPhoto(R.drawable.ic_empty_folder_icon);
            }
            rcAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void updateVolumePosition(VolumeResponse response, String serial) {
        int position = getItemPositionVolume(serial);
        allItems.get(position).setName(response.VolumeName);
        rcAdapter.notifyItemChanged(position);
    }

    private void addNewFolderItemToList() {
        VolumeResponse volumeResponse = new VolumeResponse("", getResources().getString(R.string.cloud_storage_lambda_add_new_folder));
        VolumeItemObject addVolume = new VolumeItemObject(volumeResponse);
        addVolume.setPhoto(R.drawable.ic_add_folder_icon);
        allItems.add(addVolume);
    }

    private void initDialog(){
        AddVolumeDialogView addVolumeDialogView = new AddVolumeDialogView(this, volumeMainPagePresenter, this);

        addVolumeDialog = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.file_explorer_add_folder_item))
                .customView(addVolumeDialogView, false)
                .positiveText(R.string.dialog_done);
    }

    @Override
    public void showAddFolderDialog() {
       dismissObject = addVolumeDialog.show();
    }


    private void doDisplayOptionClicked() {
        if(lLayout != null) {
            int SpanCount = lLayout.getSpanCount();
            int filesCount = allItems.size();
            if (filesCount > SpanCount++ && SpanCount <= MAX_DISPLAY_SPAN_COUNT) {
                LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, SpanCount);
                lLayout.setSpanCount(SpanCount);
            } else {
                LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, MIN_DISPLAY_SPAN_COUNT);
                lLayout.setSpanCount(MIN_DISPLAY_SPAN_COUNT);
            }
        }
    }

    @Override
    public void removeDialog() {
        if (dismissObject != null) {
            dismissObject.dismiss();
        }
    }

    public class VolumeItemObject implements Serializable{

        VolumeResponse mItemVolumeDetails;
        VolumeFilesResponse mVolumeFiles;
        int folderIcon;

        public VolumeItemObject(VolumeResponse aItemVolumeDetails) {
            mItemVolumeDetails = aItemVolumeDetails;
        }

        public void updateFilesDetails(VolumeFilesResponse aVolumeFiles){
            mVolumeFiles = aVolumeFiles;
        }

        public String getName() {
            return mItemVolumeDetails.VolumeName;
        }

        public void setName(String folderName) {
            this.mItemVolumeDetails.VolumeName = folderName;
        }

        public int getPhoto() {
            return folderIcon;
        }

        public String getId() {
            return mItemVolumeDetails.VolumeId;
        }

        public void setPhoto(int folderIcon) {
            this.folderIcon = folderIcon;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == LambdaUtils.REQUEST_EXTERNAL_PERMISSION_CODE) {
                // Continue with your action after permission request succeed
                initRecyclerView();
            }
        } else {
            finish();
        }
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

}
