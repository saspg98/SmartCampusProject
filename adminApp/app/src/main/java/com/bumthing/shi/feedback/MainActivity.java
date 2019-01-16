package com.bumthing.shi.feedback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;//recyclerview and LayoutManager
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import android.util.Log;
import android.widget.*;//Textview and shit

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.net.URL;
import android.os.AsyncTask;


import data.AppDatabase;
import data.Feedback;

import com.bumthing.shi.feedback.utilities.AppExecutor;
import com.bumthing.shi.feedback.utilities.BackgroundUtil;
import com.bumthing.shi.feedback.utilities.NetworkUtill;
import com.bumthing.shi.feedback.utilities.ParseUtil;


//TODO: decide for either "pausing" bg tasks before and after change or in the observer
public class MainActivity extends AppCompatActivity implements RAdapter.ItemClickListener {
    private static final String TAG = "TAG/"+"MainActivity";
    public static final String UPDATE_AVAILABLE_KEY = "sick";
    private boolean mUpdatedForIntent = false;


    private RecyclerView mRecyclerView;
    private RAdapter mAdapter;
    private long mLastIndex;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView statusTextView;
    private LiveData<List<Feedback>> mFeedbackList;
    private AppDatabase mDatabase;
    private AppExecutor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppDatabase mDatabase = AppDatabase.getInstance(this);

        mRecyclerView = findViewById(R.id.rv_feedback_list);
        statusTextView = findViewById(R.id.tv_status);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //TODO: replace fake data with an empty constructor!!
        mAdapter = new RAdapter(getFakeData(), this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                BackgroundUtil.clearBackgroundTasks(MainActivity.this);
                Log.v(TAG, "Expect an error if there is no data");
                long l = mDatabase.fDao().getLastFeedbackId();
                Log.v(TAG, "Value of l is "+l);
                BackgroundUtil.scheduleBackgroundTask(MainActivity.this,l);
            }
        });
        initData();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mUpdatedForIntent = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent.hasExtra(UPDATE_AVAILABLE_KEY) && !mUpdatedForIntent){
            updateLocalData();
            mUpdatedForIntent = true;//TODO: confirm necessity
        }

    }

    private void initData() {
        Log.v(TAG,"Initialising data from database");
        mDatabase = AppDatabase.getInstance(this);
        mFeedbackList = mDatabase.fDao().loadFeedback();

        mFeedbackList.observe(this, new Observer<List<Feedback>>() {
            @Override
            public void onChanged(@Nullable List<Feedback> feedbacks) {
                BackgroundUtil.clearBackgroundTasks(MainActivity.this);
                Log.v(TAG,"Data in database changed! Reading LiveData");
                mAdapter.setList(feedbacks);
                mLastIndex = getMaxIndex(feedbacks);
                BackgroundUtil.scheduleBackgroundTask(MainActivity.this,mLastIndex);

            }

            private long getMaxIndex(List<Feedback> feedbacks){
                long max=0;
                for(Feedback feedback : feedbacks){
                    if(feedback.getFId()>max)
                        max=feedback.getFId();
                }
                Log.v(TAG,"New Max ID is " + max);
                return max;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id== R.id.action_settings){
            //TODO: open settings activity!
        }
        else if(id==R.id.action_refresh){
            //TODO: call method to refresh all that shite!
            updateLocalData();

        }
        else if(id == R.id.action_delete){
            //TODO: Empty the "ROOM"
            deleteAllFeedback();
        }
        return true;
    }

    private void deleteAllFeedback() {
//        final Context context = this;
        Log.v(TAG,"Preparing to empty local data");
//        BackgroundUtil.clearBackgroundTasks(context);
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                Log.v(TAG,"Deleting local data");
                mDatabase.fDao().deleteAll();
                Log.v(TAG,"Rescheduling bgTasks");
//                BackgroundUtil.scheduleBackgroundTask(context,mLastIndex);
            }
        });

    }




    private void updateLocalData() {

        Log.v(TAG,"checking and updating local data");
        URL recentURL = NetworkUtill.getRecentURL(mLastIndex);
        new NetworkQueryTask().execute(recentURL);
    }

    @Override
    public void onItemClickListener(long feedbackId) {
        Log.v(TAG,"Item clicked! Feedback id is " + feedbackId);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.FEEDBACK_ID, feedbackId);
        startActivity(intent);
    }


    public  class NetworkQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            statusTextView.setText(getResources().getString(R.string.loading_data_message));
        }





        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String result = null;

            try {
                result = NetworkUtill.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                Log.v(TAG, "Recieved String is \n"+result);
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            statusTextView.setText(getResources().getString(R.string.loaded_message));

            if (result != null && !result.equals("")) {
                List<Feedback> list = ParseUtil.parseToList(result);
                addToDatabase(list);
            }
        }
    }

    private void addToDatabase(final List<Feedback> list) {
//        final Context context = this;
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

//                BackgroundUtil.clearBackgroundTasks(context);
                for(Feedback feedback: list){
                    Log.v(TAG,"Adding feedback " + feedback.getMssg() + " by " + feedback.getName()+
                            ", no. " + feedback.getFId());
                    mDatabase.fDao().insertFeedback(feedback);
                }

//                BackgroundUtil.scheduleBackgroundTask(context,mLastIndex);
            }
        });


    }



    private ArrayList getFakeData(){
        String ulist[] = new String[100];
        String feedlist[] = new String[100];

        ArrayList<Feedback> fblist = new ArrayList();
        char randomChar;
        for(int i=0;i<ulist.length;i++){//generate 100 random feedbacks
            ulist[i] = "";
            for(int j=0;j<5;j++){
            //generate 5 random letters for usernames
                randomChar = (char)ThreadLocalRandom.current().nextInt(97, 122 + 1);
                ulist[i] += randomChar;
            }
            feedlist[i] = "";
            int numChar = ThreadLocalRandom.current().nextInt(20,200+1);
            for(int j=0;j<numChar;j++){
            //generate 20 random letters for feedback
                randomChar = (char)ThreadLocalRandom.current().nextInt(97, 122 + 1);

                feedlist[i] += randomChar;
                if(j%5==0)//adding space after 5 characters
                    feedlist[i] += " ";
            }

            fblist.add(new Feedback(100,ulist[i],feedlist[i]));
        }

        return fblist;
    }


}
