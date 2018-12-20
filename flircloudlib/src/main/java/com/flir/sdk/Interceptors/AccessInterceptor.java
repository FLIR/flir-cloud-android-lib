package com.flir.sdk.Interceptors;


import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.ShareResource;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.AccessServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.ResponseBody;

/**
 * Created by moti on 16/03/2017.
 */

public class AccessInterceptor {

    private AccessServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;

    public AccessInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (AccessServiceApi) serviceGenerator.createService(AccessServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<ResourceResponse> postAddResource(AddResource addResource) {
       return serviceApi.addResource(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), addResource)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResourceResponse> getGetSingleResource(String serial) {
       return serviceApi.getSingleResource(authenticationProvider.getAccountToken(),serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<List<ResourceResponse>> getAllResources() {
       return serviceApi.getAllResources(authenticationProvider.getAccountToken())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResourceResponse> putUpdateResource(AddResource updateResource) {
       return serviceApi.updateResource(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken() ,updateResource.id, updateResource)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> deleteResource(String serial) {
       return serviceApi.deleteResource(authenticationProvider.getAccountToken() ,serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> postShareResource(ShareResource shareResource) {
        return serviceApi.shareResource(authenticationProvider.getAccountToken(), shareResource.id,  shareResource.email)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> postUnShareResource(ShareResource unShareResource) {
        return serviceApi.unShareResource(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), unShareResource.id, unShareResource.email)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<List<SharedUsersResponse>> getSharedResources(String serial) {
        return serviceApi.getSharedResources(authenticationProvider.getAccountToken(), serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}
