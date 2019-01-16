package com.bumthing.shi.feedback.utilities;

import java.io.IOException;
import java.util.Scanner;
import data.Feedback;

import java.util.ArrayList;
import android.util.Log;
import org.apache.commons.csv.*;

public class ParseUtil{
    private static final String TAG ="TAG/"+ "Parse";

    public static ArrayList parseToList(String str){
        Log.v(TAG,"String recieved by parseToList is ["+str+"]");
        Scanner s  = new Scanner(str.trim());

        ArrayList<Feedback> list = new ArrayList();
        long id;
        String name, mssg;
        try {
            CSVParser parser = CSVParser.parse(str,CSVFormat.DEFAULT.withHeader());
            for(CSVRecord record : parser){
                id = Long.parseLong(record.get("id"));
                name = record.get("username");
                mssg = record.get("feedback_mssg");
                list.add(new Feedback(id,name,mssg));
            }
        } catch (IOException e) {
            Log.e(TAG,"IO Exception");
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            Log.e(TAG,"column name is not as expected");
            e.printStackTrace();
        } catch(IllegalStateException e){
            Log.e(TAG,"No header mapping was provied");
            e.printStackTrace();
        }

        Log.v(TAG, "Returning list! "+ list);
        return list;
    }

}
