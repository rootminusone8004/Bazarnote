package com.rootminusone8004.bazarnote;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_table")
public class Session {
    @PrimaryKey(autoGenerate = true)
    private int sessionId;
    private String name;

    public Session(String name){
        this.name = name;
    }

    public void setSessionId(int sessionId){
        this.sessionId = sessionId;
    }

    public int getSessionId(){
        return this.sessionId;
    }

    public String getName(){
        return this.name;
    }
}
