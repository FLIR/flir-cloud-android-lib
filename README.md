# FLIR-cloud-android-lib

FLIR Cloud library is a java library that allows developers to simply use in FLIR Cloud Services

FLIR Cloud Services are cloud-based services designed and built to connect users, systems, and devices within a secure ecosystem that leverages unique FLIR technology.

In a managed environment that is open, highly available and scalable, FLIR Cloud Services offer: 
• Single sign-on (SSO) and authorization 
• Low-latency live streaming of video, audio and metadata 
• Recording and playback services for video, audio, and metadata 
• Cloud storage that is optimized for pictures and photos, and supports generic and FLIR proprietary formats 
• An IoT framework for cloud-connected device management 
• A data processing framework with scalable processing using built-in FLIR image and video processing technology or customized data processors 
• Real-time messaging for dynamic data exchange between devices, users, and services

# Offical Documantaion for FLIR Cloud Services:
	
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


How to use? 

