package com.flir.sdk;

import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;
import com.flir.sdk.network.ServiceGenerator;

/**
 * Created by mamar on 11/29/2017.
 */

public class UnitTestServiceGenerator {

    private static final String FLIR_SERVICES_BASE_URL = "https:/lambda.cloud.flir/";
    private static UnitTestServiceGenerator INSTANCE;
    private ServiceGenerator mServiceGenerator;

    private UnitTestServiceGenerator() {
        mServiceGenerator  = new ServiceGenerator<AuthenticationServiceApi>(FLIR_SERVICES_BASE_URL);
    }

    private static void init() {
        if (INSTANCE == null) {
            INSTANCE = new UnitTestServiceGenerator();

        }
    }

    public static UnitTestServiceGenerator getInstance() {
        if(INSTANCE == null){
            init();
        }
        return INSTANCE;
    }

    public  ServiceGenerator getServiceGenerator() {
        return mServiceGenerator;
    }

}
