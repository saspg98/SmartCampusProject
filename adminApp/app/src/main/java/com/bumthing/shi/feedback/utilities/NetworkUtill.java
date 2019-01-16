package com.bumthing.shi.feedback.utilities;

import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.util.Scanner;
import android.util.Log;

import data.Constants;
public class NetworkUtill{
    private static final String TAG = "TAG/"+"NetworkUtil";

    public static URL getRecentURL(){//because of how I'am stupid, this returns the url of getting only the last feedback
        StringBuilder url = new StringBuilder(Constants.BASE_URL);
        url.append("/"+ Constants.READ_URL)
        .append("/"+ Constants.ACCESS_KEY);
        URL u = null;
        try{
            u = new URL(url.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG, "URL: "+ u.toString() + ": was formed!");
        return u;
    }

    public static URL getRecentURL(long index){//set this to 0 to get all the feedback. Higher numbers will incrementally fetch lesser and lesser feedback
        StringBuilder url = new StringBuilder(Constants.BASE_URL);
        url.append("/"+ Constants.READ_URL)
        .append("/"+ Constants.ACCESS_KEY)
        .append("/"+ Constants.MULTIPLE_ENTRIES)
        .append("/"+ index);
        URL u = null;
        try{
            u = new URL(url.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG, "URL: "+ u.toString() + ": was formed!");
        return u;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }




}
