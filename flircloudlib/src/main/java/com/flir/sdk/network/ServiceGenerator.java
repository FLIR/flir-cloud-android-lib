package com.flir.sdk.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Moti Amar on 19/03/2017.
 */
//AuthenticationBase created for NetworkModule.
//This class creates service for each of the LAMBDA services
//Return an implementation of the API endpoints defined by the service interface.

public class ServiceGenerator<S> {

    private final String mBaseUrl;
    private OkHttpClient.Builder okHttpClient;

    public ServiceGenerator(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    private Retrofit.Builder createRetrofit(OkHttpClient okHttpClient) {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(mBaseUrl)
                        .client(okHttpClient)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create());
        return builder;
    }

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public <S> S createService(Class<S> serviceClass, AuthenticationProvider provider) {
        System.out.println("createService");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient = new OkHttpClient.Builder();

        if (provider != null) {
            okHttpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder request = original.newBuilder();

                if (provider.accessToken() != null && !provider.accessToken().isEmpty()) {
                    request.addHeader("Authorization", provider.accessToken());
                    System.out.println("Authorization: " + provider.accessToken());
                }
                return chain.proceed(request.build());
            });
            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(this, provider);
            okHttpClient.authenticator(tokenAuthenticator);
        }
        okHttpClient.addInterceptor(interceptor);
        Retrofit.Builder reBuilder = createRetrofit(okHttpClient.build());
        Retrofit retrofit = reBuilder.build();
        return retrofit.create(serviceClass);
    }

}
