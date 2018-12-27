# FLIR-cloud-android-lib

FLIR Cloud library is a dependency injection library that allows developers to simply use in FLIR Cloud Services

FLIR Cloud Services are cloud-based services designed and built to connect users, systems, and devices within a secure ecosystem that leverages unique FLIR technology.

In a managed environment that is open, highly available and scalable, FLIR Cloud Services offer: 
• Single sign-on (SSO) and authorization 
• Low-latency live streaming of video, audio and metadata 
• Recording and playback services for video, audio, and metadata 
• Cloud storage that is optimized for pictures and photos, and supports generic and FLIR proprietary formats 
• An IoT framework for cloud-connected device management 
• A data processing framework with scalable processing using built-in FLIR image and video processing technology or customized data processors 
• Real-time messaging for dynamic data exchange between devices, users, and services

# Privacy Policy
Before using the FLIR Cloud API, read our Privacy Policy:
https://www.flir.com/corporate/privacy-policy/

# Official Documentation for FLIR Cloud Services:
	
https://int-api.cloud.flir/
	
# Installation & Setup

FLIR cloud library is published via [JitPack](https://jitpack.io/#rewe-digital-incubator/katana). First add the JitPack repository
as described in JitPack's documentation, then add the following dependencies:

```gradle
dependencies {
     implementation 'com.github.FLIR:flir-cloud-android-lib:1.0.24'
}
```


# Getting started
FLIR Cloud library consists of two core concepts: modules and components.

### Module
A module describes how dependencies are provided. 
Each module should represent a logical unit. For instance there might be a module for every feature of your application. 

Example of Network Module:
```java
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

```

### Component

A component is composed of one or more modules. It performs the actual injection and is also responsible for holding instances of dependencies which have been declared as singletons. This concept is important to understand! As long as the same component reference is used, the same singleton instances will be provided by this component. The developer is responsible for holding component references and releasing them when necessary. Only when the component is eligible for garbage collection will it's singletons be GC'd, too. This applies for module instances which were passed to a component, too. Module instances should only be held by a component and not stored anywhere else. Especially when the module provides object instances outside of it's own scope which were passed to the module during creation.

Example of app component:

```java

@Singleton
@Component(modules = {ApplicationModule.class,NetworkModule.class, SdkModule.class, Rx.class})
public interface ApplicationComponent {
    void inject(MainApplication mainApplication);
    void inject(LoginActivity loginActivity);
}

```

The component pattern has been introduced so that – especially in an Android environment – it is possible to inject objects that should be released when the view has been destroyed, like for example the current Context.

Example of activity injection :

```java 

    @Inject
    AuthenticationProvider authenticationProvider;
    
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
    }
    
    ```
