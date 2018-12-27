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
	
# Using FLIR-cloud-lib

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.FLIR:flir-cloud-android-lib:1.0.24'
	}


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

````


