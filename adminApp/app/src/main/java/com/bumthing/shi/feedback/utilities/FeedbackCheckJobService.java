package com.bumthing.shi.feedback.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.IOException;
import java.util.ArrayList;

import data.Feedback;

public class FeedbackCheckJobService extends JobService {
    private static final String TAG = "TAG/"+"FeedbackService";

    AsyncTask mTask;
    public static final String LAST_INDEX_KEY = "Tenali Raman";

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.v(TAG,"on Start Job!");
        mTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                Context con = FeedbackCheckJobService.this;
                long index = job.getExtras().getLong(LAST_INDEX_KEY);
                String result="";
                try {
                    Log.v(TAG,"Attempting connection!");
                    result= NetworkUtill.getResponseFromHttpUrl(NetworkUtill.getRecentURL(index));
                }catch (IOException e){
                    e.printStackTrace();
                }
                ArrayList<Feedback> list = ParseUtil.parseToList(result);
                if (list.size()>0){
                    Log.v(TAG,"New Feedback!");
                    NotificationUtil.makeNotification(con,list.size(),list.get(0).getMssg());
                } else {
                    Log.v(TAG,"No new feedback!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job,false);
            }
        };

        mTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mTask != null) {
            Log.v(TAG,"Job has to be cancelled!");
            mTask.cancel(true);}
        return true;
    }
}
