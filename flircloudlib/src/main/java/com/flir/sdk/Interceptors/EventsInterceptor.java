package com.flir.sdk.Interceptors;



import com.flir.sdk.models.Events.GetUrlResponse;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.EventsServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by moti on 16/03/2017.
 */

public class EventsInterceptor {

    private EventsServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;

    public EventsInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (EventsServiceApi) serviceGenerator.createService(EventsServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<GetUrlResponse> getMqttUrl() {
       return serviceApi.getMqttUrl(authenticationProvider.getAccountToken())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}
