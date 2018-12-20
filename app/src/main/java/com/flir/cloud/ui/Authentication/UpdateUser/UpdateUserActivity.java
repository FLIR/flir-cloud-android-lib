package com.flir.cloud.ui.Authentication.UpdateUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.userModel.UpdateUserResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class UpdateUserActivity extends Activity implements UpdateUserView {

    private static final int RESULT_UPDATE_PROFILE_DETAILS = 111;
    private static final int RESULT_UPDATE_IMAGE_UPDATED = 211;
    private static final int RESULT_LOAD_IMG = 516;

    @Inject
    UserInterceptor userInterceptor;

    UpdateUserPresenter updateUserPresenter;

    @BindView(R.id.lambda_custom_progress_bar_update_user)
    LambdaCustomProgressBar progressBar;

    @BindView(R.id.input_first_name)
    EditText firstNameEditText;

    @BindView(R.id.input_last_name)
    EditText lastNameEditText;

    @BindView(R.id.input_phone)
    EditText phoneEditText;

    @BindView(R.id.button_update_user_details)
    Button buttonUpdateUser;

    @OnClick(R.id.upload_image)
    public void uploadUserImage() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

    }

    @OnClick(R.id.button_update_user_details)
    public void SendUpdateUserRequest() {
        SignUp signUp = new SignUp();
        String first = firstNameEditText.getText().toString();
        String last = lastNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        signUp.firstName = first;
        signUp.lastName = last;
        signUp.phone = phone;

        updateUserPresenter.postUpdateUserRequest(signUp);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        ButterKnife.bind(this);
        ((MainApplication)getApplication()).getApplicationComponent().inject(this);
        updateUserPresenter = new UpdateUserPresenter(userInterceptor, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateUserPresenter.onStop();
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
        //Toast.makeText(this, "Failure: " + appErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getUpdateUserResponseState(UpdateUserResponse response) {
        if(response != null) {
            Intent userResponseDetails = getIntent();

            userResponseDetails.putExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_FIRST_NAME, response.userInfo.firstName);
            userResponseDetails.putExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_LAST_NAME, response.userInfo.lastName);
            userResponseDetails.putExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_EMAIL, response.userInfo.email);

            setResult(RESULT_UPDATE_PROFILE_DETAILS, userResponseDetails);
            finish();
        }
    }

    @Override
    public void updateUserProfile(UpdateUserResponse updateUserResponse) {
        Intent imageUpdated = getIntent();
        imageUpdated.putExtra(LambdaConstant.UPDATE_DETAILS_IMAGE_UPDATED, updateUserResponse.userInfo.pictureUrl);
        setResult(RESULT_UPDATE_IMAGE_UPDATED, imageUpdated);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMG) {
            try {
                if (data != null) {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    updateUserPresenter.uploadImage(selectedImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
