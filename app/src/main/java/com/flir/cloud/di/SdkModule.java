package com.flir.cloud.di;

import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.Interceptors.AuthInterceptor;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.Interceptors.EventsInterceptor;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.Interceptors.PlaybackInterceptor;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.Interceptors.StreamingInterceptor;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceGenerator;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;

/**
 * Created by Moti Amar on 21/03/2017.
 */
@Module
public class SdkModule {

    @Provides
    AuthInterceptor provideAuthInteractor(ServiceGenerator serviceGenerator,
                                          @Named(Rx.IO) Scheduler mainScheduler,
                                          @Named(Rx.MAIN) Scheduler ioScheduler,
                                          AuthenticationProvider authenticationProvider) {
        return new AuthInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static LoginInterceptor provideLoginInterceptor(ServiceGenerator serviceGenerator,
                                                    @Named(Rx.IO) Scheduler mainScheduler,
                                                    @Named(Rx.MAIN) Scheduler ioScheduler) {
        return new LoginInterceptor(serviceGenerator, mainScheduler, ioScheduler);
    }

    @Provides
    static UserInterceptor provideUserInterceptor(ServiceGenerator serviceGenerator,
                                                  @Named(Rx.IO) Scheduler mainScheduler,
                                                  @Named(Rx.MAIN) Scheduler ioScheduler,
                                                  AuthenticationProvider authenticationProvider) {
        return new UserInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static AccountsInterceptor provideAccountInterceptor(ServiceGenerator serviceGenerator,
                                                         @Named(Rx.IO) Scheduler mainScheduler,
                                                         @Named(Rx.MAIN) Scheduler ioScheduler,
                                                         AuthenticationProvider authenticationProvider) {
        return new AccountsInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static AccessInterceptor provideAccessInterceptor(ServiceGenerator serviceGenerator,
                                                      @Named(Rx.IO) Scheduler mainScheduler,
                                                      @Named(Rx.MAIN) Scheduler ioScheduler,
                                                      AuthenticationProvider authenticationProvider) {
        return new AccessInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static DeviceInterceptor provideDeviceInterceptor(ServiceGenerator serviceGenerator,
                                                @Named(Rx.IO) Scheduler mainScheduler,
                                                @Named(Rx.MAIN) Scheduler ioScheduler,
                                                AuthenticationProvider authenticationProvider) {
        return new DeviceInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static EventsInterceptor provideEventInterceptor(ServiceGenerator serviceGenerator,
                                                      @Named(Rx.IO) Scheduler mainScheduler,
                                                      @Named(Rx.MAIN) Scheduler ioScheduler,
                                                      AuthenticationProvider authenticationProvider) {
        return new EventsInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static StorageInterceptor provideStorageInterceptor(ServiceGenerator serviceGenerator,
                                                      @Named(Rx.IO) Scheduler mainScheduler,
                                                      @Named(Rx.MAIN) Scheduler ioScheduler,
                                                      AuthenticationProvider authenticationProvider) {
        return new StorageInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static StreamingInterceptor provideStreamingInterceptor(ServiceGenerator serviceGenerator,
                                                            @Named(Rx.IO) Scheduler mainScheduler,
                                                            @Named(Rx.MAIN) Scheduler ioScheduler,
                                                            AuthenticationProvider authenticationProvider) {
        return new StreamingInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

    @Provides
    static PlaybackInterceptor providePlaybackInterceptor(ServiceGenerator serviceGenerator,
                                                          @Named(Rx.IO) Scheduler mainScheduler,
                                                          @Named(Rx.MAIN) Scheduler ioScheduler,
                                                          AuthenticationProvider authenticationProvider) {
        return new PlaybackInterceptor(serviceGenerator, mainScheduler, ioScheduler, authenticationProvider);
    }

}
