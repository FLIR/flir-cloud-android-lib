package com.flir.cloud.ui.Authentication.UserProfile;

import android.graphics.Bitmap;

import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.userModel.UpdateUserResponse;
import com.flir.sdk.network.NetworkError;

import java.io.ByteArrayOutputStream;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Moti Amar on 29/03/2017.
 */

public class UserProfilePresenter {
    private CompositeDisposable subscriptions;
    private UserInterceptor userInterceptor;
    private UserProfileView view;

    public UserProfilePresenter(UserInterceptor userInterceptor, UserProfileView view) {
        this.view = view;
        this.userInterceptor = userInterceptor;
        subscriptions = new CompositeDisposable();
    }

    public void getSelfUser() {
        Observable observable = userInterceptor.getSelfUser();
        subscriptions.add((Disposable) observable.subscribeWith(new DisposableObserver<UpdateUserResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(UpdateUserResponse updateUserResponse) {
                view.updateUserProfile(updateUserResponse);
            }

            @Override
            public void onError(Throwable e) {
                NetworkError networkError = new NetworkError(e);
            }

        }));
    }

    public void uploadImage(Bitmap bitmap) {
        Bitmap bmp = bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), byteArray);
        Completable completable = userInterceptor.postUploadUserProfilePicture(requestBody);
        completable.subscribe(() -> {
            getSelfUser();
        }, exception -> {
            NetworkError networkError = new NetworkError(exception);
        });
    }

    public void onStop() {
        subscriptions.dispose();
    }
}
