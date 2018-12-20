package com.flir.sdk;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import retrofit2.HttpException;

/**
 * Created by mamar on 11/29/2017.
 */

public class UnitTestUtils {

    public static String createRandomString(){
        return UUID.randomUUID().toString();
    }

    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public static String resolveBasePath() {
        final String path = "./sdk/src/test/resources/";
        if (Arrays.asList(new File(path).list()).contains("profile.jpg")) {
            return path+"/profile.jpg"; // version for call unit tests from Android Studio
        }
        return "../" + path; // version for call unit tests from terminal './gradlew test'
    }

    public static void printOnErrorTestLog(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            System.out.println(httpException.getMessage());
        }
    }



}
