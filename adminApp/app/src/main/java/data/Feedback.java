package data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Feedback{
    @PrimaryKey
    private long fId;
    private String name;
    private String mssg;
    //TODO:Add the date!
    //private Date pubDate;

    public Feedback(long fId, String name, String mssg){
        this.fId =fId;
        this.name= name;
        this.mssg = mssg;
    }

    public long getFId(){
        return fId;
    }

    public String getName(){
        return name;
    }

    public String getMssg(){
        return mssg;
    }

    public void setfId(long fId) {
        this.fId = fId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMssg(String mssg) {
        this.mssg = mssg;
    }
}
