package com.flir.cloud.ui.Authentication.UserProfile;

import com.flir.sdk.models.userModel.UpdateUserResponse;

/**
 * Created by Moti Amar on 29/03/2017.
 */

public interface UserProfileView {

    void updateUserProfile(UpdateUserResponse updateUserResponse);

    void updateUserProfileImage(String imageUrl);
}
