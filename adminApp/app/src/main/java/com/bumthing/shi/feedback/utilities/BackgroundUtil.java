package com.bumthing.shi.feedback.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class BackgroundUtil {
    private final static String TAG ="TAG/"+"BackgroundUtil";



//    private static final int UPDATE_INTERVAL_MINUTES = 60;
    private static final int UPDATE_INTERVAL_SECONDS = 5;//(int) (TimeUnit.MINUTES.toSeconds(UPDATE_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = UPDATE_INTERVAL_SECONDS;

    private static final String JOB_TAG = "background_check_tag";

    private static boolean sInitialized;
    private static long index = 0;


    synchronized public static void clearBackgroundTasks(@NonNull final Context context){

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancelAll();
        sInitialized=false;
        Log.v(TAG,"Cancelling all jobs!, s is now uninitialised");
    }


    synchronized public static void scheduleBackgroundTask(@NonNull final Context context,long lastIndex) {

        Log.v(TAG,"inside schedule bkg tasks!");

        if (sInitialized){
            Log.v(TAG,"s is already initialized!");

            return;
        }
        Log.v(TAG,"initializing s");
        index = lastIndex;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);



        Bundle bundle = new Bundle();
        bundle.putLong(FeedbackCheckJobService.LAST_INDEX_KEY,index);

        Job updateJob = dispatcher.newJobBuilder()

                .setService(FeedbackCheckJobService.class)
                .setTag(JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setExtras(bundle)
                .setRecurring(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setTrigger(Trigger.executionWindow(
                        UPDATE_INTERVAL_SECONDS,
                        UPDATE_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(updateJob);
        Log.v(TAG,"Job scheduled! with last int " + index);
        sInitialized = true;
    }
}
