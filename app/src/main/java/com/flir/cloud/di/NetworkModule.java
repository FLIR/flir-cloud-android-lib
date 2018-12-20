package com.flir.cloud.di;

import android.content.SharedPreferences;

import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.sdk.network.AuthenticationBase;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Moti Amar on 21/03/2017.
 */
@Module
public class NetworkModule {

    @Provides ServiceGenerator provideServiceApi(){
        ServiceGenerator serviceGenerator = new ServiceGenerator<AuthenticationServiceApi>(LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.LAMBDA_BASE_URL,"https:/lambda.cloud.flir/"));
        return serviceGenerator;
    }
    @Singleton
    @Provides
    AuthenticationProvider provideAuthenticationToken(SharedPreferences sharedPreferences){
        AuthenticationBase authenticationParams = new AuthenticationBase(sharedPreferences);
        return authenticationParams;
    }
}
