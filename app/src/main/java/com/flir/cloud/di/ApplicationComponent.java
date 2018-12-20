package com.flir.cloud.di;


import com.flir.cloud.MainApplication;
import com.flir.cloud.Services.paho.MqttPublisher;
import com.flir.cloud.Services.EventMqttIntentService;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraItemActivity;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.TimeLineSelectorLinearLayout;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraVideoView.CameraVideoFrameLayoutCustomView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles.CarouselSettingsDialogView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselShareDialogFiles.CarouselShareDialogView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.UpdateDeviceActivity.UpdateDeviceActivity;
import com.flir.cloud.ui.Access.AccessActivities.AccessAddDevice;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselItemFragment;
import com.flir.cloud.ui.Account.AccountPageActivity;
import com.flir.cloud.ui.Authentication.ChangePasswordPage.ChangePasswordActivity;
import com.flir.cloud.ui.Authentication.ForgotPassword.ForgotPasswordActivity;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Authentication.UpdateUser.UpdateUserActivity;
import com.flir.cloud.ui.Authentication.UserProfile.UserProfileMainPagePage;
import com.flir.cloud.ui.Authentication.SignUp.SignUpActivity;
import com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView.TLView;
import com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderActivity;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.VolumeMainPageActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class,NetworkModule.class, SdkModule.class, Rx.class})
public interface ApplicationComponent {
    void inject(MainApplication mainApplication);
    void inject(UserProfileMainPagePage mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(ForgotPasswordActivity forgotPasswordActivity);
    void inject(SignUpActivity signUpActivity);
    void inject(AccountPageActivity accountPageActivity);
    void inject(UpdateUserActivity updateUserActivity);

    void inject(CarouselSettingsDialogView carouselSettingsDialogView);

    void inject(AccessDevicesCarouselActivity aAccessMainPage);
    void inject(CarouselItemFragment itemFragment);
    void inject(AccessAddDevice accessAddDevice);
    void inject(UpdateDeviceActivity aclUpdateDevice);
    void inject(ChangePasswordActivity aChangePasswordActivity);


    void inject(EventMqttIntentService eventService);
    void inject(MqttPublisher mqttPublisher);
    void inject(CameraItemActivity cameraItemActivity);

    void inject(VolumeMainPageActivity volumeMainPageActivity);
    void inject(FolderActivity FolderActivity);
    void inject(CameraVideoFrameLayoutCustomView aCameraVideoFrameLayoutCustomView);
    void inject(CarouselShareDialogView aCarouselShareDialogView);

    void inject(TLView aTLView);
    void inject(TimeLineSelectorLinearLayout aTimeLineSelectorLinearLayout);

}
