package data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FDao {

    @Query("SELECT * FROM Feedback")
    LiveData<List<Feedback>> loadFeedback();

    @Insert
    void insertFeedback(Feedback f);

    @Query("DELETE FROM Feedback")
    void deleteAll();

    @Query("SELECT MAX(fId) FROM Feedback")
    long getLastFeedbackId();//Expect error! what happens when Max is used on an empty table?


    @Query("SELECT * FROM Feedback WHERE FId = :id")
    List<Feedback> getFeedbackById(long id);//Expect error! what happens when Max is used on an empty table?
}