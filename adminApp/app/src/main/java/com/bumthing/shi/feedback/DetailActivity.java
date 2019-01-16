package com.bumthing.shi.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumthing.shi.feedback.utilities.AppExecutor;

import java.util.List;

import data.AppDatabase;
import data.Feedback;

public class DetailActivity extends AppCompatActivity {

    public static final String FEEDBACK_ID=  "this a feedback yo";
    private static final long DEFAULT_FID = 0;
    private AppDatabase mDatabase;
    private TextView mUserTextView;
    private TextView mMssgTextView;
    private static final String TAG= "TAG/"+"DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mUserTextView = findViewById(R.id.tv_user);
        mMssgTextView = findViewById(R.id.tv_mssg);

        mDatabase = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        if(intent.hasExtra(FEEDBACK_ID)){
            final long fid = intent.getLongExtra(FEEDBACK_ID,DEFAULT_FID);
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG,"Fetching Feedback by Id");
                    final List<Feedback> list = mDatabase.fDao().getFeedbackById(fid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI(list.get(0));
                        }
                    });
                }
            });
        }
    }


    void updateUI(Feedback feedback){
        //TODO: Extract string resource
        mUserTextView.setText("reviewed by ".concat(feedback.getName()));
        mMssgTextView.setText(feedback.getMssg());
    }
}
